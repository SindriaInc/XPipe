/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli.commands;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableMap;
import java.net.URI;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import static java.util.stream.Collectors.toList;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import static org.cmdbuild.common.Constants.DESCRIPTION_ATTRIBUTE;
import org.cmdbuild.services.soap.client.beans.ActivitySchema;
import org.cmdbuild.services.soap.client.beans.Attribute;
import org.cmdbuild.services.soap.client.beans.Card;
import org.cmdbuild.services.soap.client.beans.CardList;
import org.cmdbuild.services.soap.client.beans.Private;
import org.cmdbuild.services.soap.client.beans.UserGroup;
import org.cmdbuild.services.soap.client.beans.UserInfo;
import org.cmdbuild.services.soap.client.beans.Workflow;
import org.cmdbuild.services.soap.client.CmdbuildSoapClient;
import static org.cmdbuild.services.soap.client.CmdbuildSoapClient.PasswordType.TEXT;
import static org.cmdbuild.services.soap.client.CmdbuildSoapClient.usernameAndPassword;
import org.cmdbuild.utils.cli.utils.CliAction;
import org.cmdbuild.utils.cli.utils.CliCommand;
import org.cmdbuild.utils.cli.utils.CliCommandParser;
import static org.cmdbuild.utils.cli.utils.CliCommandParser.printActionHelp;
import org.cmdbuild.utils.cli.utils.CliCommandUtils;
import static org.cmdbuild.utils.cli.utils.CliCommandUtils.prepareAction;

public class SoapCommandRunner extends AbstractCommandRunner {

    private final Map<String, CliAction> actions;
    private String username, password, baseUrl, wsUrl;

    public SoapCommandRunner() {
        super("soapws", "test cmdbuild soap ws");
        actions = new CliCommandParser().parseActions(this);
    }

    @Override
    protected Options buildOptions() {
        Options options = super.buildOptions();
        options.addOption("url", true, "set cmdbuild root url for soap ws (default is http://localhost:8080/cmdbuild/)");
        options.addOption("username", true, "set ws username (default is 'admin')");
        options.addOption("password", true, "set ws password (default is 'admin')");
        return options;
    }

    @Override
    protected void printAdditionalHelp() {
        System.out.println("\navailable soap methods:");
        printActionHelp(actions);
    }

    @Override
    protected void exec(CommandLine cmd) throws Exception {
        username = firstNonNull(cmd.getOptionValue("username"), "admin");
        password = firstNonNull(cmd.getOptionValue("password"), "admin");
        baseUrl = firstNonNull(cmd.getOptionValue("url"), "http://localhost:8080/cmdbuild/");
        wsUrl = URI.create(baseUrl + "/services/soap/Private").normalize().toString();

        Iterator<String> iterator = cmd.getArgList().iterator();
        if (!iterator.hasNext()) {
            System.out.println("no soap call requested, doing nothing...");
        } else {
            CliCommandUtils.ExecutableAction action = prepareAction(actions, iterator);
            action.execute();
        }
    }

    @CliCommand
    protected void test() {
        Private proxy = CmdbuildSoapClient.<Private>aSoapClient()
                .withUrl(wsUrl)
                .withAuthentication(usernameAndPassword(TEXT, username, password))
                .build().getProxy();
        boolean ok = true;
        logger.info("created proxy for url = {}", wsUrl);
        try {
            logger.debug("try to log in and create session");
            String sessionId = proxy.createSession();
            logger.info("successfully created session id = {}", sessionId);
        } catch (Exception ex) {
            logger.error("error, unable to create session", ex);
            ok = false;
        }
        try {
            logger.debug("try to log in and retrieve user id");
            UserInfo userInfo = proxy.getUserInfo();
            logger.info("user info: username = {}", userInfo.getUsername());
            logger.info("user info: user type = {}", userInfo.getUserType());
            logger.info("user info: groups = {}", firstNonNull(userInfo.getGroups(), Collections.<UserGroup>emptyList()).stream().map((group) -> group.getName()).collect(toList()));
        } catch (Exception ex) {
            logger.error("error, unable to get user info", ex);
            ok = false;
        }
        System.out.println("test " + (ok ? "OK" : "ERROR"));
    }

    @CliCommand
    protected void getCards(String className) {
        Private proxy = CmdbuildSoapClient.<Private>aSoapClient()
                .withUrl(wsUrl)
                .withAuthentication(usernameAndPassword(TEXT, username, password))
                .build().getProxy();
        logger.info("created proxy for url = {}", wsUrl);
        logger.debug("try to log in and create session");
        String sessionId = proxy.createSession();
        logger.debug("successfully created session id = {}", sessionId);
        CardList cards = checkNotNull(proxy.getCardList(className, null, null, null, 10l, null, null, null));
        System.out.println("received card values for classe: " + className);
        cards.getCards().forEach((card) -> {
            System.out.format("\t%-10s\t%s\n", card.getId(), card.getAttributeList().stream().filter((attr) -> attr.getName().equalsIgnoreCase(DESCRIPTION_ATTRIBUTE)).map(Attribute::getValue).findAny().orElse(""));
        });
    }

    @CliCommand
    protected void startDemoProcess(String workflowClassName, String email) {
        Private proxy = CmdbuildSoapClient.<Private>aSoapClient()
                .withUrl(wsUrl)
                .withAuthentication(usernameAndPassword(TEXT, username, password))
                .build().getProxy();
//			String workflowClassName = "AccountDemoCMDBuild";
        logger.info("get activity objects for class = {}", workflowClassName);
        ActivitySchema activityObjects = proxy.getActivityObjects(workflowClassName, null);
        logger.info("got activity objects = {}", activityObjects.getAttributes().stream().map((attr) -> attr.getName()).collect(toList()));
        //[Email, Cognome, Nome, Ente, Ruolo, Note, Lingua, TipoAccount]
        //TODO

        /*
			
        self.request.set(WSConst.CLASSNAME, "AccountDemoCMDBuild")
        self.request.set(WSConst.ISPROCESS, True)
        self.request.set("Email", email)
        self.request.set("Nome", nome)
        self.request.set("Cognome", cognome)
        self.request.set("Ente", azienda)
        self.request.set("Ruolo", ruolo)
        self.request.set("Note", note)
        self.request.set("Lingua", self._getCurrentLanguageLookupId())
        if tipo:
            self.request.set("TipoAccount", self._getTypeLookupId(tipo))  ="CMDBuild"
        
			
			
         card = ns0.card_Def('', '')
         card._attributeList = attributes
         card._className = className
         card._id = 0
         request = updateWorkflow()
         request._card = card
         request._completeTask = True
			
			
         */
        String requestId = UUID.randomUUID().toString().substring(0, 4).toLowerCase();
        logger.info("using random request id = '{}' as seed for all field values", requestId);
//			String email = firstNonNull(trimToNull(cmd.getOptionValue("email")), "test." + requestId + "@fakemail.com");
        logger.info("using email address = {}", email);
        Card card = new Card();
        card.setClassName(workflowClassName);
        for (Map.Entry<String, String> entry : ImmutableMap.<String, String>builder()
                .put("Email", email)
                .put("Nome", "test_nome_" + requestId)
                .put("Cognome", "test_cognome_" + requestId)
                .put("Ente", "test_ente_" + requestId)
                .put("Ruolo", "Altro")
                .put("Note", "test_note_" + requestId)
                .put("Lingua", "2394")
                .build().entrySet()) {
            Attribute attribute = new Attribute();
            attribute.setName(entry.getKey());
            attribute.setValue(entry.getValue());
            card.getAttributeList().add(attribute);
        }
        logger.info("sending updateWorkflow request");
        Workflow workflow = proxy.updateWorkflow(card, true, null);
        logger.info("updateWorkflow response = {}", workflow);
        checkNotNull(workflow);
        logger.info("workflow running with processId = {} instanceId = {}", workflow.getProcessid(), workflow.getProcessinstanceid());
    }

}
