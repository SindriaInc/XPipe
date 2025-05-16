/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.email.mta;

import static com.google.common.base.Preconditions.checkArgument;
import com.microsoft.graph.models.MailFolder;
import com.microsoft.graph.options.HeaderOption;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.MailFolderCollectionPage;
import com.microsoft.graph.requests.MailFolderCollectionRequest;
import com.microsoft.graph.requests.MailFolderRequest;
import com.microsoft.graph.requests.MessageCollectionPage;
import com.microsoft.graph.requests.MessageCollectionRequestBuilder;
import java.io.InputStream;
import static java.lang.String.format;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.joining;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.email.EmailException;
import org.cmdbuild.email.data.EmailRepository;
import static org.cmdbuild.email.mta.EmailMoverMSGraph.MS_WELL_KNOWN_FOLDER_NAMES;
import org.cmdbuild.lock.LockService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.lazyString;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowSupplier;
import org.slf4j.LoggerFactory;

/**
 *
 * @author afelice
 */
public class EmailReceiverMSGraph extends BaseEmailReceiverProvider {

    protected final EmailMSGraphClientProvider emailMsGraphClientProvider;

    public EmailReceiverMSGraph(EmailAccount emailAccount, EmailMSGraphClientProvider emailMsGraphClientProvider,
            EmailReceiveConfig receiveConfig,
            LockService lockService,
            EmailRepository repository) {
        super(emailAccount, receiveConfig, lockService, repository, LoggerFactory.getLogger(EmailReceiverMSGraph.class));
        this.emailMsGraphClientProvider = emailMsGraphClientProvider;

        this.logger = LoggerFactory.getLogger(getClass());
    }

    @Override
    public void receiveMails() throws MessagingException {
        final String sendingAddress = emailAccount.getAddress();

        GraphServiceClient msGraphClient = this.emailMsGraphClientProvider.create();
        try (this.emailMsGraphClientProvider;  EmailReaderLocker readerLocker = new EmailReaderLocker(emailAccount, lockService);) {
            List<String> wellKnownFolders = MS_WELL_KNOWN_FOLDER_NAMES;
            Map<String, String> foldersByName = map();

            wellKnownFolders.forEach(f -> {
                MailFolder mailFolder = buildFolderRequest(msGraphClient, sendingAddress, f).get();
                foldersByName.put(mailFolder.displayName, mailFolder.id);
            });
            MailFolderCollectionPage folderPage = buildAllFoldersRequest(msGraphClient, sendingAddress).get();
            if (folderPage != null) {
                folderPage.getCurrentPage().forEach(f -> foldersByName.put(f.displayName, f.id));
            }
            String incomingFolder = receiveConfig.getIncomingFolder();
            logger.debug("open incoming folder = {}", incomingFolder);
            checkArgument(foldersByName.containsKey(incomingFolder), "incoming folder not found for name = %s; available folders = %s", incomingFolder, lazyString(rethrowSupplier(() -> list(foldersByName.keySet()).collect(joining(", ")))));

            MessageCollectionPage messagesPage = msGraphClient.users(sendingAddress).messages()
                    .buildRequest()
                    .filter(format("parentFolderId eq '%s'", foldersByName.get(incomingFolder)))
                    .select("parentFolderId,id")
                    .get();

            while (messagesPage != null) {
                List<com.microsoft.graph.models.Message> messages = messagesPage.getCurrentPage();

                if (messages.isEmpty()) {
                    logger.debug("no message received for account = {} folder = {}", emailAccount, incomingFolder);
                    break;
                } else {
                    logger.debug("processing {} incoming messages for account = {} folder = {}", messages.size(), emailAccount, incomingFolder);
//                    Collections.shuffle(messages);
                    messages.forEach(m -> processMessageSafe(m, msGraphClient));

                    MessageCollectionRequestBuilder nextPage = messagesPage.getNextPage();
                    if (nextPage == null) {
                        break;
                    } else {
                        messagesPage = nextPage.buildRequest(new HeaderOption("Prefer", "outlook.body-content-type=\"text\"")).get(); // Re-add the header to subsequent requests
                    }
                    logger.info("processed {} incoming messages for account = {} folder = {}", messages.size(), emailAccount, incomingFolder);
                }
            } // end while
        }
    } // end receiveMails() method

    @Override
    public void testConnection(EmailAccount emailAccount) {
        BaseEmailMSGraphClientProvider.testConnection(emailAccount, logger);
    }

    private MailFolderCollectionRequest buildAllFoldersRequest(GraphServiceClient msGraphClient, final String sendingAddress) {
        return msGraphClient.users(sendingAddress).mailFolders().buildRequest();
    }

    private MailFolderRequest buildFolderRequest(GraphServiceClient msGraphClient, final String sendingAddress, String aFolderString) {
        return msGraphClient.users(sendingAddress).mailFolders(aFolderString).buildRequest();
    }

    private void processMessageSafe(com.microsoft.graph.models.Message messageMSGraph, GraphServiceClient msGraphClient) {
        Message message = convertMimeToMessage(msGraphClient.users(emailAccount.getAddress()).messages(messageMSGraph.id).content().buildRequest().get());

        EmailMoverMSGraph emailMover = new EmailMoverMSGraph(emailAccount, msGraphClient, messageMSGraph, logger);
        processMessageSafe(message, emailMover);
    }

    private static Message convertMimeToMessage(InputStream mimeMessage) {
        try {
            return new MimeMessage(null, mimeMessage);
        } catch (MessagingException ex) {
            throw new EmailException(ex, "error loading mime message from ms graph");
        }
    }

}
