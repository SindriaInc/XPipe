package org.cmdbuild.etl.jobs;

import com.fasterxml.jackson.databind.JsonNode;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import com.google.common.eventbus.Subscribe;
import java.io.IOException;
import static java.lang.String.format;
import java.net.IDN;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import javax.annotation.PreDestroy;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.cmdbuild.config.WaterwayConfig;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.LOOKUP;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.LOOKUPARRAY;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.REFERENCE;
import org.cmdbuild.dao.utils.AttributeFilterProcessor;
import org.cmdbuild.dao.utils.FulltextFilterProcessor;
import org.cmdbuild.email.template.EmailTemplateProcessorService;
import org.cmdbuild.etl.EtlException;
import org.cmdbuild.etl.config.WaterwayConfigReloadEvent;
import org.cmdbuild.etl.config.WaterwayDescriptorService;
import org.cmdbuild.etl.config.WaterwayItem;
import static org.cmdbuild.etl.config.WaterwayItemType.WYCIT_WEBHOOK;
import org.cmdbuild.etl.waterway.WaterwayService;
import static org.cmdbuild.etl.waterway.WaterwayService.WATERWAY_SERVICE_MINION;
import org.cmdbuild.etl.webhook.WebhookConfig;
import static org.cmdbuild.etl.webhook.WebhookMethod.WHM_DELETE;
import static org.cmdbuild.etl.webhook.WebhookMethod.WHM_GET;
import static org.cmdbuild.etl.webhook.WebhookMethod.WHM_POST;
import static org.cmdbuild.etl.webhook.WebhookMethod.WHM_PUT;
import org.cmdbuild.etl.webhook.WebhookService;
import org.cmdbuild.event.CardEvent;
import static org.cmdbuild.event.DaoEventType.DE_CARD_DELETE_AFTER;
import org.cmdbuild.eventbus.EventBusService;
import org.cmdbuild.minions.MinionComponent;
import org.cmdbuild.minions.MinionHandler;
import org.cmdbuild.minions.MinionHandlerExt;
import org.cmdbuild.minions.MinionHandlerImpl;
import static org.cmdbuild.minions.MinionRuntimeStatus.MRS_ERROR;
import static org.cmdbuild.minions.MinionRuntimeStatus.MRS_READY;
import org.cmdbuild.requestcontext.RequestContextService;
import static org.cmdbuild.utils.io.HttpClientUtils.checkStatus;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toPrettyJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmExecutorUtils.executorService;
import static org.cmdbuild.utils.lang.CmExecutorUtils.shutdownQuietly;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullNorBlank;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import org.cmdbuild.workflow.inner.RiverFlowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class WaterwayWebhookTriggerService implements MinionComponent {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final WaterwayDescriptorService configService;
    private final WebhookService webhookService;
    private final EmailTemplateProcessorService emailTemplateProcessorService;

    private final MinionHandlerExt minionHandler;

    private final ExecutorService executor;
    private List<WebhookHandler> handlers = emptyList();

    public WaterwayWebhookTriggerService(RequestContextService contextService, WaterwayService waterwayService, DaoService dao, WaterwayDescriptorService configService, WebhookService webhookService, EmailTemplateProcessorService emailTemplateProcessorService, EventBusService eventBusService) {
        this.dao = checkNotNull(dao);
        this.configService = checkNotNull(configService);
        this.webhookService = checkNotNull(webhookService);
        this.emailTemplateProcessorService = checkNotNull(emailTemplateProcessorService);

        this.minionHandler = MinionHandlerImpl.builder()
                .withName("Waterway_ Webhook")
                .withDescription("Waterway Webhook Trigger")
                .withRequires(WATERWAY_SERVICE_MINION)
                .reloadOnConfigs(WaterwayConfig.class)
                .build();

        executor = executorService(getClass().getName(), () -> {
            MDC.put("cm_type", "sys");
            MDC.put("cm_id", format("sys:whevent:%s", randomId(6)));
            contextService.initCurrentRequestContext("webhook processing job");
        });

        eventBusService.getCardEventBus().register(new Object() {
            @Subscribe
            public void handleCardWebhook(CardEvent event) {
                if (waterwayService.isReady() && !handlers.isEmpty() && event.getCurrentCard().getType().hasServiceWritePermission()) {
                    logger.trace("processing card webhook = {}", event);
                    handlers.forEach(h -> executor.submit(() -> h.handleCardEvent(event)));//TODO improve this (?)
                }
            }
        });
        eventBusService.getWorkflowEventBus().register(new Object() {
            @Subscribe
            public void handleWorkflowEvent(RiverFlowEvent event) {
                if (waterwayService.isReady() && !handlers.isEmpty()) {
                    logger.trace("processing workflow event = {}", event);
                    handlers.forEach(h -> executor.submit(() -> h.handleWorkflowEventBus(event)));
                }
            }
        });
        eventBusService.getDaoEventBus().register(new Object() {
            @Subscribe
            public void handleWaterwayConfigReloadEvent(WaterwayConfigReloadEvent event) {
                loadHandlers();
            }
        });
    }

    @Override
    public MinionHandler getMinionHandler() {
        return minionHandler;
    }

    @Override
    public void start() {
        loadHandlers();
    }

    @PreDestroy
    public void cleanup() {
        shutdownQuietly(executor);
    }

    public void loadHandlers() {
        try {
            logger.debug("load wy webhook handlers");
            handlers = configService.getAllItems().stream().filter(i -> i.isOfType(WYCIT_WEBHOOK) && i.hasConfigNotBlank("url") && i.hasConfigNotBlank("event") && i.isEnabled()).map(WebhookHandler::new).collect(toImmutableList());
            logger.info("{} wy webhook handlers ready", handlers.size());
            minionHandler.setStatus(MRS_READY);
        } catch (Exception ex) {
            logger.error(marker(), "error loading card webhook triggers", ex);
            handlers = emptyList();
            minionHandler.setStatus(MRS_ERROR);
        }
    }

    private class WebhookHandler {

        private final WaterwayItem item;
        private final WebhookConfig webhook;

        public WebhookHandler(WaterwayItem item) {
            this.item = checkNotNull(item);
            try {
                this.webhook = webhookService.getByName(item.getCode());
            } catch (Exception ex) {
                throw new EtlException(ex, "error loading webhook handler from trigger item = %s", item);
            }
        }

        private void handleCardEvent(CardEvent event) {
            try {
                if (webhook.getEvents().contains(serializeEnum(event.getType())) && matchesFilter(event.getCurrentCard())) {
                    logger.debug("trigger handler = {} for card webhook = {}", item, event);
                    if (event.getType().equals(DE_CARD_DELETE_AFTER)) {
                        doWebhookRequest(new WebhookRequest(webhook, event.getCurrentCard()));
                    } else {
                        doWebhookRequest(new WebhookRequest(webhook, event.getCurrentCard().getIdOrNull()));
                    }
                }
            } catch (Exception ex) {
                logger.error(marker(), "error processing webhook = {} with handler = {}", event, item, ex);
            }
        }

        private void handleWorkflowEventBus(RiverFlowEvent event) {
            try {
                if (webhook.getEvents().contains(serializeEnum(event.getType())) && matchesFilter(event.getFlow())) {
                    logger.debug("trigger handler = {} for workflow webhook = {}", item, event);
                    doWebhookRequest(new WebhookRequest(webhook, event.getFlow().getIdOrNull()));
                }
            } catch (Exception ex) {
                logger.error(marker(), "error processing event = {} with handler = {}", event, item, ex);
            }
        }

        private void doWebhookRequest(WebhookRequest webhookRequest) throws IOException, MalformedURLException, URISyntaxException {
            switch (webhook.getMethod()) {
                case WHM_GET ->
                    webhookRequest.doGetResource();
                case WHM_POST ->
                    webhookRequest.doPostResource();
                case WHM_PUT ->
                    webhookRequest.doPutResource();
                case WHM_DELETE ->
                    webhookRequest.doDeleteResource();
                default ->
                    throw new EtlException("unsupported method mode = <%s>", webhook.getMethod());
            }
        }

        private boolean matchesFilter(Card card) {
            if (webhook.getFilter().isNoop()) {
                return true;
            } else if (webhook.getFilter().hasAttributeFilter() && !AttributeFilterProcessor.<Card>builder().withKeyToValueFunction((k, c) -> c.get(k)).withFilter(webhook.getFilter().getAttributeFilter()).build().match(card)) {
                return false;
            } else if (webhook.getFilter().hasFulltextFilter() && !FulltextFilterProcessor.<Card>build(webhook.getFilter().getFulltextFilter()).withKeyToValueFunction((k, c) -> c.get(k)).match(card)) {
                return false;
            } else {
                return true;
            }
        }

    }

    private class WebhookRequest {

        private final WebhookConfig webhook;
        private final Card card;

        private final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();

        public WebhookRequest(WebhookConfig webhook, Long cardId) {
            this.webhook = checkNotNull(webhook);
            this.card = checkNotNull(dao.getCard(cardId));
        }

        public WebhookRequest(WebhookConfig webhook, Card card) {
            this.webhook = checkNotNull(webhook);
            this.card = checkNotNull(card);
        }

        public void doGetResource() throws IOException, MalformedURLException, URISyntaxException {
            HttpGet request = new HttpGet(serializeUrl());
            request = (HttpGet) serializeHeader(request);
            logger.debug("get resource =< {} >", request);
            list(request.getAllHeaders()).forEach(h -> logger.debug("get header =< {} >", format("%s: %s", h.getName(), h.getValue())));

            //executing the get request
            checkStatus(getHttpClient().execute(request));
        }

        public void doPostResource() throws IOException, MalformedURLException, URISyntaxException {
            HttpPost request = new HttpPost(serializeUrl());
            request.setHeader("Content-Type", "application/json");
            request = (HttpPost) serializeHeader(request);
            String serializedBody = serializeBody();
            request.setEntity(new StringEntity(serializedBody, ContentType.APPLICATION_JSON));
            logger.debug("post resource =< {} >", request);
            list(request.getAllHeaders()).forEach(h -> logger.debug("post header =< {} >", format("%s: %s", h.getName(), h.getValue())));
            logger.debug("post body =< {} >", serializedBody);

            //executing the post request
            checkStatus(getHttpClient().execute(request));
        }

        public void doPutResource() throws IOException, MalformedURLException, URISyntaxException {
            HttpPut request = new HttpPut(serializeUrl());
            request.setHeader("Content-Type", "application/json");
            request = (HttpPut) serializeHeader(request);
            String serializedBody = serializeBody();
            request.setEntity(new StringEntity(serializedBody, ContentType.APPLICATION_JSON));
            logger.debug("put resource =< {} >", request);
            list(request.getAllHeaders()).forEach(h -> logger.debug("put header =< {} >", format("%s: %s", h.getName(), h.getValue())));
            logger.debug("put body =< {} >", serializedBody);

            //executing the post request
            checkStatus(getHttpClient().execute(request));
        }

        public void doDeleteResource() throws IOException, MalformedURLException, URISyntaxException {
            HttpDelete request = new HttpDelete(serializeUrl());
            request = (HttpDelete) serializeHeader(request);
            logger.debug("delete resource =< {} >", request);
            list(request.getAllHeaders()).forEach(h -> logger.debug("delete header =< {} >", format("%s: %s", h.getName(), h.getValue())));

            //executing the post request
            checkStatus(getHttpClient().execute(request));
        }

        private String serializeUrl() throws MalformedURLException, URISyntaxException {
            URL url = new URL(handleTemplateExpressions(webhook.getUrl()));
            logger.debug("trying to encode url =< {} >", url);
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), IDN.toASCII(url.getHost()), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
            logger.debug("url encoded into =< {} >", uri.toASCIIString());
            return uri.toASCIIString();
        }

        private Object serializeHeader(Object request) {
            if (isNotNullNorBlank(webhook.getHeaders())) {
                JsonNode jsonHeader = fromJson(webhook.getHeaders(), JsonNode.class);
                jsonHeader.fieldNames().forEachRemaining(k -> {
                    if (request instanceof HttpRequestBase httpRequestBase) {
                        httpRequestBase.addHeader(k, jsonHeader.get(k).asText());
                    } else if (request instanceof HttpEntityEnclosingRequestBase httpEntityEnclosingRequestBase) {
                        httpEntityEnclosingRequestBase.addHeader(k, jsonHeader.get(k).asText());
                    }
                });
            }
            return request;
        }

        private String serializeBody() {
            if (isNotNullNorBlank(webhook.getBody())) {
                return addBrakets(handleTemplateExpressions(removeBrakets(webhook.getBody())));
            } else {
                return addBrakets(handleTemplateExpressions(removeBrakets(getFullBody())));
            }
        }

        private String getFullBody() {
            return toPrettyJson(map().accept(m -> {
                card.getType().getActiveServiceAttributes().stream().forEach(a -> {
                    m.put(a.getName(), format("{card:%s}", a.getName()));
                    if (a.isOfType(LOOKUP, LOOKUPARRAY, REFERENCE)) {
                        m.put(format("_%s_code", a.getName()), format("{card:%s.Code}", a.getName()));
                        m.put(format("_%s_description", a.getName()), format("{card:%s.Description}", a.getName()));
                    }
                });
            }));
        }

        private String handleTemplateExpressions(String value) {
            return emailTemplateProcessorService.applyEmailTemplateExpr(value, card, webhook.getLanguage());
        }

        private String removeBrakets(String value) {
            return StringUtils.trim(value).replaceFirst("^\\{([\\d\\D]+)\\}$", "$1");
        }

        private String addBrakets(String value) {
            return format("{%s}", value);
        }

        private CloseableHttpClient getHttpClient() {
            return HttpClients.custom().setConnectionManager(connectionManager).build();
        }

    }
}
