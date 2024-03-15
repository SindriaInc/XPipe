/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.waterway.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import com.google.common.collect.ImmutableList;
import static com.google.common.collect.Maps.uniqueIndex;
import java.time.ZonedDateTime;
import java.util.Collection;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_BEGINDATE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.cmdbuild.etl.waterway.message.WaterwayMessage;
import org.cmdbuild.etl.waterway.message.WaterwayMessageAttachment;
import org.cmdbuild.etl.waterway.message.WaterwayMessageStatus;
import org.cmdbuild.fault.FaultEvent;
import org.cmdbuild.fault.FaultEventImpl;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import org.cmdbuild.utils.json.JsonBean;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;

@CardMapping("_EtlMessage")
public class EtlMessageImpl implements EtlMessage {

    private final static String QUEUE_KEY_META = "wy_queue_key", STORAGE_KEY_META = "wy_storage_key";

    private final Long id;
    private final String messageId, queue, nodeId, logs, storage;
    private final int transactionId;
    private final ZonedDateTime timestamp;
    private final WaterwayMessageStatus status;
    private final Map<String, String> meta;
    private final Map<String, WaterwayMessageAttachment> attachments;
    private final List<FaultEvent> errors;
    private final List<String> history;

    private EtlMessageImpl(EtlMessageImplBuilder builder) {
        Map<String, String> map = firstNotNull(builder.meta, emptyMap());
        this.id = builder.id;
        this.transactionId = builder.transactionId;
        this.messageId = checkNotBlank(builder.messageId);
        this.storage = firstNotBlank(builder.storage, map.get(STORAGE_KEY_META));
        this.queue = firstNotBlank(builder.queue, map.get(QUEUE_KEY_META));
        this.nodeId = checkNotBlank(builder.nodeId);
        this.logs = nullToEmpty(builder.logs);
        this.timestamp = checkNotNull(builder.timestamp);
        this.status = checkNotNull(builder.status);
        this.meta = map(map).withoutKeys(QUEUE_KEY_META, STORAGE_KEY_META).immutable();
        this.attachments = uniqueIndex(firstNotNull(builder.attachments, emptyList()), WaterwayMessageAttachment::getName);
        this.errors = ImmutableList.copyOf(firstNotNull(builder.errors, emptyList()));
        this.history = ImmutableList.copyOf(firstNotNull(builder.history, emptyList()));
    }

    @Override
    @CardAttr(ATTR_ID)
    @Nullable
    public Long getId() {
        return id;
    }

    @Override
    public String getStorage() {
        return storage;
    }

    @CardAttr(ETL_MESSAGE_ATTR_TRANSACTION_ID)
    @Override
    public int getTransactionId() {
        return transactionId;
    }

    @Override
    @CardAttr(ATTR_CODE)
    public String getMessageId() {
        return messageId;
    }

    @Override
    public String getQueue() {
        return queue;
    }

    @Override
    @CardAttr(value = ETL_MESSAGE_ATTR_STORAGE_CODE, readFromDb = false)
    public String getStorageCode() {
        return EtlMessage.super.getStorageCode();
    }

    @Override
    @CardAttr(value = ETL_MESSAGE_ATTR_QUEUE_CODE, readFromDb = false)
    public String getQueueCode() {
        return EtlMessage.super.getQueueCode();
    }

    @Override
    @CardAttr
    public String getNodeId() {
        return nodeId;
    }

    @Nullable
    @Override
    @CardAttr
    public String getLogs() {
        return logs;
    }

    @Override
    @CardAttr(value = ATTR_BEGINDATE, writeToDb = false)
    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    @CardAttr(ETL_MESSAGE_ATTR_MESSAGE_STATUS)
    public WaterwayMessageStatus getStatus() {
        return status;
    }

    @Override
    public Map<String, String> getMeta() {
        return meta;
    }

    @CardAttr("Meta")
    @JsonBean
    public Map<String, String> getMetaForDb() {
        return map(meta).with(QUEUE_KEY_META, queue, STORAGE_KEY_META, storage);
    }

    @Override
    public Map<String, WaterwayMessageAttachment> getAttachmentMap() {
        return attachments;
    }

    @Override
    public List<FaultEvent> getErrors() {
        return errors;
    }

    @CardAttr("Attachments")
    @JsonBean
    public Object getAttachmentsAsJson() {
        return map(attachments).mapValues(EtlMessageAttachmentsData::fromAttachment);
    }

    @CardAttr("Errors")
    @JsonBean
    public Object getErrorsAsJson() {
        return errors;//ErrorMessagesData.fromErrorsAndWarningEvents(errors).getData();
    }

    @CardAttr
    @Override
    public List<String> getHistory() {
        return history;
    }

    @Override
    public String toString() {
        return "EtlMessage{" + "id=" + id + ", messageId=" + messageId + ", timestamp=" + timestamp + ", status=" + status + '}';
    }

    public static EtlMessageImplBuilder builder() {
        return new EtlMessageImplBuilder();
    }

    public static EtlMessageImplBuilder copyOf(WaterwayMessage source) {
        return new EtlMessageImplBuilder()
                .withMessageId(source.getMessageId())
                .withQueue(source.getQueue())
                .withNodeId(source.getNodeId())
                .withTimestamp(source.getTimestamp())
                .withStatus(source.getStatus())
                .withMeta(source.getMeta())
                .withAttachments(source.getAttachmentMap())
                .withTransactionId(source.getTransactionId())
                .withStorage(source.getStorage())
                .withLogs(source.getLogs())
                .withErrors(source.getErrors())
                .withHistory(source.getHistory());
    }

    public static EtlMessageImplBuilder copyOf(EtlMessage source) {
        return copyOf((WaterwayMessage) source)
                .withId(source.getId());
    }

    public static class EtlMessageImplBuilder implements Builder<EtlMessageImpl, EtlMessageImplBuilder> {

        private Long id;
        private Integer transactionId;
        private String messageId, storage;
        private String queue;
        private String nodeId;
        private String logs;
        private ZonedDateTime timestamp;
        private WaterwayMessageStatus status;
        private Map<String, String> meta;
        private Collection<WaterwayMessageAttachment> attachments;
        private List<FaultEvent> errors;
        private List<String> history;

        public EtlMessageImplBuilder withHistory(List<String> history) {
            this.history = history;
            return this;
        }

        public EtlMessageImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public EtlMessageImplBuilder withTransactionId(Integer transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public EtlMessageImplBuilder withStorage(String storage) {
            this.storage = storage;
            return this;
        }

        public EtlMessageImplBuilder withMessageId(String messageId) {
            this.messageId = messageId;
            return this;
        }

        public EtlMessageImplBuilder withQueue(String queue) {
            this.queue = queue;
            return this;
        }

        public EtlMessageImplBuilder withNodeId(String nodeId) {
            this.nodeId = nodeId;
            return this;
        }

        public EtlMessageImplBuilder withLogs(String logs) {
            this.logs = logs;
            return this;
        }

        public EtlMessageImplBuilder withTimestamp(ZonedDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public EtlMessageImplBuilder withStatus(WaterwayMessageStatus status) {
            this.status = status;
            return this;
        }

        @CardAttr("Meta")
        public EtlMessageImplBuilder withMeta(Map<String, String> meta) {
            this.meta = meta;
            return this;
        }

        public EtlMessageImplBuilder withAttachments(Map<String, WaterwayMessageAttachment> attachments) {
            this.attachments = attachments.values();
            return this;
        }

        public EtlMessageImplBuilder withErrors(List<FaultEvent> errors) {
            this.errors = errors;
            return this;
        }

        public EtlMessageImplBuilder withErrorsAsJson(ArrayNode json) {
            return this.withErrors((List) fromJson(json, new TypeReference<List<FaultEventImpl>>() {
            }));
        }

        public EtlMessageImplBuilder withAttachmentsAsJson(ObjectNode attachments) {
            return this.withAttachments(attachments != null ? map(fromJson(attachments, new TypeReference<Map<String, EtlMessageAttachmentsData>>() {
            })).mapValues(EtlMessageAttachmentsData::toAttachment) : map());
        }

        @Override
        public EtlMessageImpl build() {
            return new EtlMessageImpl(this);
        }

    }
}
