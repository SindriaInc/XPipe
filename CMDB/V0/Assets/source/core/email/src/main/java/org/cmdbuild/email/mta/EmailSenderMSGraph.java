/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.email.mta;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Iterables.getLast;
import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.models.Attachment;
import com.microsoft.graph.models.AttachmentCreateUploadSessionParameterSet;
import com.microsoft.graph.models.AttachmentItem;
import com.microsoft.graph.models.AttachmentType;
import com.microsoft.graph.models.BodyType;
import com.microsoft.graph.models.EmailAddress;
import com.microsoft.graph.models.FileAttachment;
import com.microsoft.graph.models.InternetMessageHeader;
import com.microsoft.graph.models.ItemBody;
import com.microsoft.graph.models.Message;
import com.microsoft.graph.models.Recipient;
import com.microsoft.graph.models.SingleValueLegacyExtendedProperty;
import com.microsoft.graph.models.UploadSession;
import com.microsoft.graph.options.HeaderOption;
import com.microsoft.graph.requests.AttachmentCollectionPage;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.SingleValueLegacyExtendedPropertyCollectionPage;
import com.microsoft.graph.tasks.IProgressCallback;
import com.microsoft.graph.tasks.LargeFileUploadTask;
import java.io.IOException;
import static java.lang.String.format;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import jakarta.mail.MessagingException;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.debuginfo.InstanceInfoService;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.email.EmailAttachment;
import org.cmdbuild.email.EmailException;
import org.cmdbuild.email.EmailSignatureService;
import static org.cmdbuild.email.template.EmailUtils.parseEmailHeaderToken;
import static org.cmdbuild.utils.date.CmDateUtils.toDateTime;
import static org.cmdbuild.utils.io.CmIoUtils.newDataHandler;
import static org.cmdbuild.utils.io.CmStreamProgressUtils.progressDescription;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import org.slf4j.LoggerFactory;

/**
 *
 * @author afelice
 */
public class EmailSenderMSGraph extends BaseEmailSenderProvider {

    protected static final int SEND_MSG_TIMEOUT_SECONDS = 30;
    protected static final HeaderOption HEADER_OPTION_IMMUTABLE = new HeaderOption("Prefer", "IdType=\"ImmutableId\"");

    protected final EmailMSGraphClientProvider emailMsGraphClientProvider;

    public EmailSenderMSGraph(EmailAccount emailAccount, EmailMSGraphClientProvider emailMsGraphClientProvider,
            InstanceInfoService infoService, EmailSignatureService signatureService) {
        super(emailAccount, infoService, signatureService, LoggerFactory.getLogger(EmailSenderMSGraph.class));
        this.emailMsGraphClientProvider = emailMsGraphClientProvider;

        logger = LoggerFactory.getLogger(getClass());
    }

    @Override
    public Email sendEmail(Email email) throws MessagingException {
        checkArgument(email.hasDestinationAddress(), "invalid email: no destination address found (TO, CC or BCC)");
        Email emailToSend = prepareEmail(email);

        GraphServiceClient msGraphClient = this.emailMsGraphClientProvider.create();
        try (this.emailMsGraphClientProvider) {
            logger.debug("send email = {}", emailToSend);
            com.microsoft.graph.models.Message messageMSGraph = convertMessageToMessageGraph(emailToSend, infoService);
            logger.debug("send message = {}", getMessageInfoSafe(messageMSGraph));

            logger.info("handling email attachments");
            final String sendingAddress = emailAccount.getAddress();

            messageMSGraph = handleAttachments(emailToSend, messageMSGraph, msGraphClient, sendingAddress);

            logger.info("sending message");

            CompletableFuture<Void> postAsync = msGraphClient.users(sendingAddress).messages().byId(messageMSGraph.id).send().buildRequest(HEADER_OPTION_IMMUTABLE).postAsync();
            postAsync.get(SEND_MSG_TIMEOUT_SECONDS, TimeUnit.SECONDS);

            logger.debug("sent message");

            com.microsoft.graph.models.Message messageSent = msGraphClient.users(sendingAddress).messages().byId(messageMSGraph.id).buildRequest().select("id,parentFolderId,internetMessageId,internetMessageHeaders").get();

            String messageId = checkNotBlank(parseEmailHeaderToken(getMessageId(messageSent)), "error: message sent, but message id header is null");
            String rawHeaders = serializeMessageHeaders(emailToSend, messageSent);
            final ZonedDateTime sentDateTime = toDateTime(messageSent.sentDateTime);

            return buildMessageSent(emailToSend, sentDateTime, messageId, rawHeaders);
        } catch (InterruptedException | ExecutionException | TimeoutException ex) {
            throw new EmailException(ex, "error creating imap session for account = %s", emailAccount);
        }
    } // end sendMail method

    @Override
    public void testConnection(EmailAccount emailAccount) {
        BaseEmailMSGraphClientProvider.testConnection(emailAccount, logger);
    }

    private Message handleAttachments(Email email, Message messageToSend, GraphServiceClient msGraphClient, final String sendingAddress) throws ClientException {
        Message messageDraftMessageSent;
        if (email.hasAttachments() && email.getAttachments().stream().mapToLong(a -> a.getData().length).sum() < 2000000) {
            messageToSend.attachments = buildSmallAttachments(email.getAttachments());
            messageDraftMessageSent = sendMessage(msGraphClient, sendingAddress, messageToSend);
        } else if (email.hasAttachments()) {
            messageDraftMessageSent = sendMessage(msGraphClient, sendingAddress, messageToSend); // Mandatory before uploading large attachments
            uploadLargeAttachments(msGraphClient, sendingAddress, email.getAttachments(), messageDraftMessageSent.id);
        } else {
            messageDraftMessageSent = sendMessage(msGraphClient, sendingAddress, messageToSend);
        }
        return messageDraftMessageSent;
    }

    private Message sendMessage(GraphServiceClient msGraphClient, final String sendingAddress, Message messageToSend) throws ClientException {
        return msGraphClient.users(sendingAddress).messages().buildRequest(HEADER_OPTION_IMMUTABLE).post(messageToSend);
    }

    private com.microsoft.graph.models.Message convertMessageToMessageGraph(Email email, InstanceInfoService infoService) throws MessagingException {
        try {
            com.microsoft.graph.models.Message messageMSGraph = new com.microsoft.graph.models.Message();

            // setting references and in-reply-to
            List<SingleValueLegacyExtendedProperty> extendedProperties = calculateReferencesReply(email);
            if (!extendedProperties.isEmpty()) {
                messageMSGraph.singleValueExtendedProperties = new SingleValueLegacyExtendedPropertyCollectionPage(extendedProperties, null);
            }

            // setting headers
            messageMSGraph.internetMessageHeaders = buildHeaders(email, infoService);

            // setting body
            ItemBody body = new ItemBody();
            body.contentType = fetchContentType(email.getContentType());
            body.content = email.getContent();

            if (email.getFirstFromAddressOrNull() != null) {
                Recipient recipient = buildRecipient(email.getFirstFromAddressOrNull());
                messageMSGraph.from = recipient;
            }
            addMailDestinations(messageMSGraph, email);
            messageMSGraph.subject = email.getSubject();
            messageMSGraph.body = body;

            return messageMSGraph;
        } catch (EmailException ex) {
            throw new EmailException(ex, "error converting message to ms graph");
        }
    }

    private List<InternetMessageHeader> buildHeaders(Email email, InstanceInfoService infoService) {
        List<InternetMessageHeader> headersList = list();
        InternetMessageHeader cmdbuildHeader1 = new InternetMessageHeader();
        cmdbuildHeader1.name = "X-Mailer";
        cmdbuildHeader1.value = format("CMDBuild v%s", infoService.getVersion());
        headersList.add(cmdbuildHeader1);
        InternetMessageHeader cmdbuildHeader2 = new InternetMessageHeader();
        cmdbuildHeader2.name = "X-CMDBuild-Version";
        cmdbuildHeader2.value = infoService.getVersion();
        headersList.add(cmdbuildHeader2);
        InternetMessageHeader cmdbuildHeader3 = new InternetMessageHeader();
        cmdbuildHeader3.name = "X-CMDBuild-Revision";
        cmdbuildHeader3.value = infoService.getRevision();
        headersList.add(cmdbuildHeader3);
        InternetMessageHeader cmdbuildHeader4 = new InternetMessageHeader();
        cmdbuildHeader4.name = "X-CMDBuild-InstanceInfo";
        cmdbuildHeader4.value = infoService.getInstanceInfo();
        headersList.add(cmdbuildHeader4);
        if (email.hasId()) {
            InternetMessageHeader cmdbuildHeader5 = new InternetMessageHeader();
            cmdbuildHeader5.name = "X-CMDBuild-EmailId";
            cmdbuildHeader5.value = toStringNotBlank(email.getId());
            headersList.add(cmdbuildHeader5);
        }
        return headersList;
    }

    private void addMailDestinations(Message messageMSGraph, Email email) {
        messageMSGraph.toRecipients = email.getToEmailAddressList().stream().map(this::buildRecipient).collect(toList());
        messageMSGraph.ccRecipients = email.getCcEmailAddressList().stream().map(this::buildRecipient).collect(toList());
        messageMSGraph.bccRecipients = email.getBccEmailAddressList().stream().map(this::buildRecipient).collect(toList());
    }

    private Recipient buildRecipient(String e) {
        Recipient recipient = new Recipient();
        EmailAddress emailAddress = new EmailAddress();
        emailAddress.address = checkNotBlank(e);
        recipient.emailAddress = emailAddress;
        return recipient;
    }

    private com.microsoft.graph.models.BodyType fetchContentType(String emailContentType) {
        return switch (emailContentType) {
            case "text/plain" ->
                BodyType.TEXT;
            case "text/html" ->
                BodyType.HTML;
            default ->
                BodyType.TEXT;
        };
    }

    private String serializeMessageHeaders(Email email, com.microsoft.graph.models.Message messageMSGraph) {
        if (messageMSGraph.internetMessageHeaders != null) {
            Map<String, String> mapiProperty = map("String 0x1039", "References", "String 0x1042", "In-Reply-To");
            return messageMSGraph.internetMessageHeaders.stream().map(h -> format("%s: %s", h.name, nullToEmpty(h.value))).collect(joining("\n"))
                    .concat("\n")
                    .concat(calculateReferencesReply(email).stream().map(h -> format("%s: %s", mapiProperty.get(h.id), nullToEmpty(h.value))).collect(joining("\n")));
        } else {
            return "";
        }
    }

    private List<SingleValueLegacyExtendedProperty> calculateReferencesReply(Email email) {
        List<SingleValueLegacyExtendedProperty> extendedProperties = list();
        String inReplyTo = email.getInReplyTo();
        List<String> references = email.getReferences();
        if (isNotBlank(inReplyTo) && (references.isEmpty() || !equal(getLast(references), inReplyTo))) {
            references = list(references).with(inReplyTo);
        }
        if (!references.isEmpty()) {
            SingleValueLegacyExtendedProperty header = new SingleValueLegacyExtendedProperty();
            header.id = "String 0x1039";
            header.value = references.stream().collect(joining(" "));
            extendedProperties.add(header);
        }
        if (isNotBlank(inReplyTo)) {
            SingleValueLegacyExtendedProperty header = new SingleValueLegacyExtendedProperty();
            header.id = "String 0x1042";
            header.value = inReplyTo;
            extendedProperties.add(header);
        }
        return extendedProperties;
    }

    private String getMessageId(com.microsoft.graph.models.Message messageMSGraph) {
        return messageMSGraph.internetMessageId;
    }

    private String getMessageInfoSafe(com.microsoft.graph.models.Message messageMSGraph) {
        try {
            return toStringHelper(messageMSGraph)
                    .add("id", messageMSGraph.internetMessageId)
                    .add("subject", nullToEmpty(messageMSGraph.subject))
                    .add("from", messageMSGraph.from == null ? "" : messageMSGraph.from.emailAddress)
                    .add("to", messageMSGraph.toRecipients == null ? "" : messageMSGraph.toRecipients.stream().map(to -> to.emailAddress.address).collect(joining(","))).toString();
        } catch (Exception ex) {
            logger.warn("unable to get message infos for log message", ex);
            return messageMSGraph.toString();
        }
    }

    /**
     * Add directly as file attachments to mail
     *
     * @param attachments
     * @return
     */
    private AttachmentCollectionPage buildSmallAttachments(List<EmailAttachment> attachments) {
        logger.debug("uploading small attachments");

        List<Attachment> fileAttachments = attachments.stream().map(this::buildFileAttachment).collect(Collectors.toList());
        AttachmentCollectionPage attachmentCollectionPage = new AttachmentCollectionPage(fileAttachments, null);

        return attachmentCollectionPage;
    }

    private FileAttachment buildFileAttachment(EmailAttachment a) {
        FileAttachment fileAttachment = new FileAttachment();

        fileAttachment.name = a.getFileName();
        fileAttachment.contentId = a.getContentId();
        fileAttachment.contentType = a.getContentType();
        fileAttachment.contentBytes = a.getData();
        fileAttachment.oDataType = "#microsoft.graph.fileAttachment";
        fileAttachment.isInline = equal(a.getContentDisposition(), "inline");
        return fileAttachment;
    }

    /**
     * Upload attachments, in chunk, to previously sent message
     *
     * @param msGraphClient
     * @param emailAddress
     * @param attachments
     * @param messageMSGraphId
     */
    private void uploadLargeAttachments(GraphServiceClient msGraphClient, String emailAddress, List<EmailAttachment> attachments, String messageMSGraphId) {
        logger.debug("uploading large attachments");
        attachments.forEach(a -> {
            try {
                AttachmentItem attachmentItem = new AttachmentItem();
                attachmentItem.attachmentType = AttachmentType.FILE;
                attachmentItem.name = a.getFileName();
                attachmentItem.size = (long) a.getData().length;
                attachmentItem.contentType = "application/octet-stream";

                UploadSession uploadSession = msGraphClient.users(emailAddress)
                        .messages(messageMSGraphId)
                        .attachments()
                        .createUploadSession(AttachmentCreateUploadSessionParameterSet.newBuilder().withAttachmentItem(attachmentItem).build())
                        .buildRequest()
                        .post();

                LargeFileUploadTask<AttachmentItem> chunkedUploadProvider = new LargeFileUploadTask<>(uploadSession, msGraphClient, newDataHandler(a.getData()).getInputStream(), attachmentItem.size, AttachmentItem.class);

                IProgressCallback callback = (current, max) -> logger.debug("uploading file {}, {} of {}MB", attachmentItem.name, progressDescription(current, max), attachmentItem.size / 1024 / 1024);

                chunkedUploadProvider.upload(0, null, callback);
            } catch (ClientException | IOException ex) {
                throw new EmailException(ex, "error uploading attachment %s", a.getFileName());
            }
        });
    }

}
