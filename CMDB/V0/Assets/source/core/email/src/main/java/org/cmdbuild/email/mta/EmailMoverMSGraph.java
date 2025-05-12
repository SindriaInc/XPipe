/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.email.mta;

import static com.google.common.base.Preconditions.checkNotNull;
import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.models.MailFolder;
import com.microsoft.graph.models.MessageMoveParameterSet;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.MailFolderCollectionPage;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import java.util.Map;
import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.email.EmailException;
import static org.cmdbuild.email.utils.EmailMtaUtils.getMessageInfoSafe;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.toInverseMap;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;

/**
 * Moves mail to another folder in a MS email account, using MSGraph library
 *
 * @author afelice
 */
public class EmailMoverMSGraph implements EmailMoverProvider {

    public static final CmCollectionUtils.FluentList<String> MS_WELL_KNOWN_FOLDER_NAMES = list("inbox", "drafts", "outbox");

    private final Logger logger;

    private final EmailAccount emailAccount;
    private final GraphServiceClient msGraphClient;
    private final com.microsoft.graph.models.Message messageMSGraph;

    public EmailMoverMSGraph(EmailAccount emailAccount, GraphServiceClient msGraphClient, com.microsoft.graph.models.Message messageMSGraph, Logger logger) {
        this.logger = logger;

        this.emailAccount = emailAccount;
        this.msGraphClient = msGraphClient;
        this.messageMSGraph = messageMSGraph;
    }

    @Override
    public void moveToFolder(Message message, String targetFolderName) throws MessagingException {
        checkNotBlank(targetFolderName);
        try {
            // @todo può essere unificato parzialmente con EmailReceiverMSGraph.receiveMails()
            Map<String, String> foldersById = map();
            MS_WELL_KNOWN_FOLDER_NAMES.forEach(f -> {
                MailFolder mailFolder = msGraphClient.users(emailAccount.getAddress()).mailFolders(f).buildRequest().get();
                foldersById.put(mailFolder.id, mailFolder.displayName);
            });
            MailFolderCollectionPage folderPage = msGraphClient.users(emailAccount.getAddress()).mailFolders().buildRequest().get();
            if (folderPage != null) {
                folderPage.getCurrentPage().forEach(f -> {
                    foldersById.put(f.id, f.displayName);
                });
            }
            String source = checkNotNull(foldersById.get(messageMSGraph.parentFolderId));
            logger.debug("moving message = {} from folder = {} to folder = {}", getMessageInfoSafe(message), source, targetFolderName);
            String targetFolderNameId = toInverseMap(foldersById).get(checkNotBlank(targetFolderName));
            if (targetFolderNameId == null) {
                MailFolder mailFolder = new MailFolder();
                mailFolder.displayName = targetFolderName;
                targetFolderNameId = msGraphClient.users(emailAccount.getAddress()).mailFolders().buildRequest().post(mailFolder).id;
            }

            // #7176 -- MS Graph - Read mail has to be marked as "read"
            setMessageAsRead(emailAccount, messageMSGraph.id);

            msGraphClient.users(emailAccount.getAddress()).messages(messageMSGraph.id).move(buildDestinationParameters(targetFolderNameId)).buildRequest().post();
        } catch (ClientException ex) {
            throw new EmailException(ex, "error moving message = %s to folder = %s", getMessageInfoSafe(message), targetFolderName);
        }
    }

    protected static MessageMoveParameterSet buildDestinationParameters(String targetFolderNameId) {
        return MessageMoveParameterSet.newBuilder().withDestinationId(targetFolderNameId).build();
    }

    private void setMessageAsRead(EmailAccount emailAccount, String messageId) {
        com.microsoft.graph.models.Message messageIsRead = new com.microsoft.graph.models.Message();
        messageIsRead.isRead = true;
        msGraphClient.users(emailAccount.getAddress()).messages(messageId).buildRequest().patch(messageIsRead);
    }
}
