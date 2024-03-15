package org.cmdbuild.email.mta;

import static com.google.common.base.Preconditions.checkNotNull;
import javax.mail.MessagingException;
import org.cmdbuild.config.EmailQueueConfiguration;
import org.cmdbuild.debuginfo.InstanceInfoService;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.email.EmailAccountService;
import org.cmdbuild.email.EmailException;
import org.cmdbuild.email.EmailSignatureService;
import org.cmdbuild.email.data.EmailRepository;
import org.cmdbuild.lock.LockService;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.springframework.stereotype.Component;

@Component
public class EmailMtaServiceImpl implements EmailMtaService {

    public static final String EMAIL_HEADER_MESSAGE_ID = "Message-ID",
            EMAIL_HEADER_REFERENCES = "References",
            EMAIL_HEADER_X_MAILER = "X-Mailer",
            EMAIL_HEADER_IN_REPLY_TO = "In-Reply-To";

    private final EmailAccountService emailAccountService;

    // Sender stuff
    private final EmailQueueConfiguration queueConfig;
    private final InstanceInfoService infoService;
    private final EmailSignatureService signatureService;

    // Receiver stuff
    private final LockService lockService;

    private final EmailRepository repository;

    // Strategy to build proper sender/receiver
    private final EmailProviderStrategy emailProviderStrategy;

    public EmailMtaServiceImpl(EmailAccountService emailAccountService, LockService lockService, EmailQueueConfiguration queueConfig, EmailRepository repository, EmailSignatureService signatureService, InstanceInfoService infoService) {
        this.emailAccountService = checkNotNull(emailAccountService);

        // Sender stuff
        this.queueConfig = checkNotNull(queueConfig);
        this.infoService = checkNotNull(infoService);
        this.signatureService = checkNotNull(signatureService);

        // Receiver stuff        
        this.lockService = checkNotNull(lockService);
        this.repository = checkNotNull(repository);

        this.emailProviderStrategy = new EmailProviderStrategy();
    }

    @Override
    public Email send(Email email) {
        try {
            EmailAccount emailAccount;
            if (email.getAccount() == null) {
                emailAccount = checkNotNull(emailAccountService.getDefaultOrNull(), "no account supplied for email, and no default account found");
            } else {
                emailAccount = emailAccountService.getAccount(email.getAccount());
            }
            
            EmailSenderProvider senderProvider = emailProviderStrategy.buildSender(emailAccount, queueConfig, infoService, signatureService);
            return senderProvider.sendEmail(email);

            // AFE: before strategy was:
//            return switch (account.getAuthenticationType()) {
//                case AUTHENTICATION_TYPE_MS_OAUTH2 ->
//                    new EmailSenderMSGraph(emailAccount).sendEmail(email);
//                case AUTHENTICATION_TYPE_DEFAULT, AUTHENTICATION_TYPE_GOOGLE_OAUTH2 ->
//                    new EmailSenderJavaMail(emailAccount).sendEmail(email);
//                default ->
//                    throw new IllegalArgumentException(format("unsupported authentication type =< %s >", emailAccount.getAuthenticationType()));
//            };
        } catch (MessagingException ex) {
            throw new EmailException(ex);
        }
    }

    @Override
    public void receive(EmailReceiveConfig config) {
        EmailAccount emailAccount = emailAccountService.getAccount(checkNotBlank(config.getAccount(), "missing account param"));
        receive(emailAccount, config);
    }

    @Override
    public void receive(EmailAccount emailAccount, EmailReceiveConfig receiveConfig) {
        try {
            EmailReceiverProvider receiverProvider = emailProviderStrategy.buildReceiver(emailAccount, receiveConfig, lockService, repository);
            receiverProvider.receiveMails();

            // AFE: before strategy was:
//            switch (account.getAuthenticationType()) {
//                case AUTHENTICATION_TYPE_MS_OAUTH2 ->
//                    new EmailReaderMSGraph(emailAccount).receiveMails(receiveConfig);
//                case AUTHENTICATION_TYPE_DEFAULT, AUTHENTICATION_TYPE_GOOGLE_OAUTH2 ->
//                    new EmailReaderJavaMail(emailAccount).receiveMails(receiveConfig);
//            };
        } catch (MessagingException ex) {
            throw new EmailException(ex, "error receiving email for account = %s with folder = %s", emailAccount, receiveConfig.getIncomingFolder());
        }
    }

}
