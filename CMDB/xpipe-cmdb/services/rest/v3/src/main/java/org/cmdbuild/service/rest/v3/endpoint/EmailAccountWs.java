package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_EMAIL_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_EMAIL_VIEW_AUTHORITY;
import static org.cmdbuild.common.utils.PagedElements.paged;
import org.cmdbuild.config.EmailConfigurationImpl;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.debuginfo.InstanceInfoService;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailAccount;
import static org.cmdbuild.email.EmailAccount.AUTHENTICATION_TYPE_DEFAULT;
import static org.cmdbuild.email.EmailAccount.AUTHENTICATION_TYPE_GOOGLE_OAUTH2;
import static org.cmdbuild.email.EmailAccount.AUTHENTICATION_TYPE_MS_OAUTH2;
import org.cmdbuild.email.EmailAccountService;
import org.cmdbuild.email.EmailSignature;
import org.cmdbuild.email.EmailSignatureService;
import org.cmdbuild.email.beans.EmailAccountImpl;
import org.cmdbuild.email.beans.EmailAccountImpl.EmailAccountImplBuilder;
import org.cmdbuild.email.data.EmailRepository;
import org.cmdbuild.email.mta.EmailProcessedAction;
import org.cmdbuild.email.mta.EmailProvider;
import org.cmdbuild.email.mta.EmailProviderStrategy;
import org.cmdbuild.email.mta.EmailReceiveConfig;
import org.cmdbuild.email.mta.EmailReceivedAction;
import org.cmdbuild.lock.ItemLock;
import org.cmdbuild.lock.LockResponse;
import org.cmdbuild.lock.LockScope;
import org.cmdbuild.lock.LockService;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isSystemViewMode;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DETAILED;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;
import static org.cmdbuild.utils.crypto.PasswordBulletsUtils.handleBullets;
import static org.cmdbuild.utils.crypto.PasswordBulletsUtils.stringToBullets;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@Path("email/accounts/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class EmailAccountWs {

    private final EmailAccountService service;

    public EmailAccountWs(EmailAccountService service) {
        this.service = checkNotNull(service);
    }

    @GET
    @Path(EMPTY)
    @RolesAllowed(ADMIN_EMAIL_VIEW_AUTHORITY)
    public Object readAll(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @QueryParam(LIMIT) Long limit, @QueryParam(START) Long offset, @QueryParam(DETAILED) @DefaultValue(FALSE) Boolean detailed) {
        List<EmailAccount> list = isAdminViewMode(viewMode) ? service.getAll() : service.getAllActive();
        return response(paged(list, detailed ? this::serializeDetailedAccount : this::serializeBasicAccount, offset, limit));
    }

    @GET
    @Path("{accountId}/")
    @RolesAllowed(ADMIN_EMAIL_VIEW_AUTHORITY)
    public Object read(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @PathParam("accountId") String idOrCode) {
        EmailAccount account = service.getAccountByIdOrCode(idOrCode);
        return response(serializeDetailedAccount(account).accept(m -> {
            if (isSystemViewMode(viewMode)) {
                m.put("password", account.getPassword());
            }
        }));
    }

    @GET
    @Path("{accountId}/public")
    public Object readPublic(@PathParam("accountId") String idOrCode) {
        EmailAccount account = service.getAccountByIdOrCode(idOrCode);
        return response(serializePublicAccount(account));
    }

    @POST
    @Path(EMPTY)
    @RolesAllowed(ADMIN_EMAIL_MODIFY_AUTHORITY)
    public Object create(WsEmailAccountData data) {
        EmailAccount account = service.create(data.toEmailAccount().build());
        return response(serializeDetailedAccount(account));
    }

    @PUT
    @Path("{accountId}/")
    @RolesAllowed(ADMIN_EMAIL_MODIFY_AUTHORITY)
    public Object update(@PathParam("accountId") Long id, WsEmailAccountData data) {
        EmailAccount account = service.update(data.toEmailAccount().withId(id).build());
        return response(serializeDetailedAccount(account));
    }

    @DELETE
    @Path("{accountId}/")
    @RolesAllowed(ADMIN_EMAIL_MODIFY_AUTHORITY)
    public Object delete(@PathParam("accountId") Long id) {
        service.delete(id);
        return success();
    }

    @POST
    @Path("_NEW/test")
    @RolesAllowed(ADMIN_EMAIL_VIEW_AUTHORITY)
    public Object testAccountConfig(WsEmailAccountData data) {
        return testAccount(data.toEmailAccount().build());
    }

    @POST
    @Path("{accountId}/test")
    @RolesAllowed(ADMIN_EMAIL_VIEW_AUTHORITY)
    public Object testExistingAccount(@PathParam("accountId") String idOrCode, @Nullable WsEmailAccountData data) {
        EmailAccount account = service.getAccountByIdOrCode(idOrCode);
        if (data != null) {
            account = data.toEmailAccount().withPassword(handleBullets(data.password, account::getPassword)).build();
        }
        return testAccount(account);
    }

    private Object testAccount(EmailAccount emailAccount) {
        EmailProviderStrategy emailProviderStrategy = new EmailProviderStrategy();

        EmailProvider emailProvider;
        if (emailAccount.isSmtpConfigured()) {
            emailProvider = emailProviderStrategy.buildSender(emailAccount, new EmailConfigurationImpl(), new MockInstanceInfoService(), new MockEmailSignatureService());
        } else {
            emailProvider = emailProviderStrategy.buildReceiver(emailAccount, new MockEmailReceiveConfigImpl(), new MockLockServiceImpl(), new MockEmailRepositoryImpl());
        }

        return response(map().accept(m -> {
            switch (emailAccount.getAuthenticationType()) {
                case AUTHENTICATION_TYPE_MS_OAUTH2 -> {
                    emailProvider.testConnection(emailAccount);
                    m.put("msgraph", true);
                }
                case AUTHENTICATION_TYPE_DEFAULT, AUTHENTICATION_TYPE_GOOGLE_OAUTH2 -> {
                    if (emailAccount.isImapConfigured()) {
                        emailProvider.testConnection(emailAccount);
                        m.put("imap", true);
                    }
                    if (emailAccount.isSmtpConfigured()) {
                        emailProvider.testConnection(emailAccount);
                        m.put("smtp", true);
                    }
                }
            }
        }));
    }

    private FluentMap<String, Object> serializeBasicAccount(EmailAccount a) {
        return map(
                "_id", a.getId(),
                "name", a.getName()
        );
    }

    private FluentMap<String, Object> serializePublicAccount(EmailAccount a) {
        return serializeBasicAccount(a).with(
                "maxAttachmentSizeForEmail", a.getMaxEmailAttachmentsSizeMegs()
        );
    }

    private FluentMap<String, Object> serializeDetailedAccount(EmailAccount a) {
        return serializePublicAccount(a).with(
                "default", equal(a.getName(), service.getDefaultCodeOrNull()),
                "username", a.getUsername(),
                "password", stringToBullets(a.getPassword()),
                "address", a.getAddress(),
                "smtp_server", a.getSmtpServer(),
                "smtp_port", a.getSmtpPort(),
                "smtp_ssl", a.getSmtpSsl(),
                "smtp_starttls", a.getSmtpStartTls(),
                "imap_output_folder", a.getSentEmailFolder(),
                "imap_server", a.getImapServer(),
                "imap_port", a.getImapPort(),
                "imap_ssl", a.getImapSsl(),
                "imap_starttls", a.getImapStartTls(),
                "auth_type", a.getAuthenticationType(),
                "active", a.isActive()
        );
    }

    public static class WsEmailAccountData {

        private final String name, username, password, address, smtpServer, imapOutputFolder, imapServer, authType;
        private final Integer smtpPort, imapPort, maxAttachmentSizeForEmail;
        private final Boolean smtpSsl, smtpStarttls, imapSsl, imapStarttls, active;

        public WsEmailAccountData(
                @JsonProperty("name") String name,
                @JsonProperty("username") String username,
                @JsonProperty("password") String password,
                @JsonProperty("address") String address,
                @JsonProperty("smtp_server") String smtpServer,
                @JsonProperty("imap_output_folder") String imapOutputFolder,
                @JsonProperty("imap_server") String imapServer,
                @JsonProperty("smtp_port") Integer smtpPort,
                @JsonProperty("imap_port") Integer imapPort,
                @JsonProperty("maxAttachmentSizeForEmail") Integer maxAttachmentSizeForEmail,
                @JsonProperty("smtp_ssl") Boolean smtpSsl,
                @JsonProperty("smtp_starttls") Boolean smtpStarttls,
                @JsonProperty("imap_ssl") Boolean imapSsl,
                @JsonProperty("imap_starttls") Boolean imapStarttls,
                @JsonProperty("auth_type") String authType,
                @JsonProperty("active") Boolean active) {
            this.name = checkNotBlank(name);
            this.username = username;
            this.password = password;
            this.address = address;
            this.smtpServer = smtpServer;
            this.imapOutputFolder = imapOutputFolder;
            this.imapServer = imapServer;
            this.smtpPort = smtpPort;
            this.imapPort = imapPort;
            this.smtpSsl = smtpSsl;
            this.smtpStarttls = smtpStarttls;
            this.imapSsl = imapSsl;
            this.imapStarttls = imapStarttls;
            this.authType = authType;
            this.active = active;
            this.maxAttachmentSizeForEmail = maxAttachmentSizeForEmail;
        }

        public EmailAccountImplBuilder toEmailAccount() {
            return EmailAccountImpl.builder()
                    .withName(name)
                    .withAddress(address)
                    .withImapPort(imapPort)
                    .withImapServer(imapServer)
                    .withImapSsl(imapSsl)
                    .withImapStartTls(imapStarttls)
                    .withSentEmailFolder(imapOutputFolder)
                    .withSmtpPort(smtpPort)
                    .withSmtpServer(smtpServer)
                    .withSmtpSsl(smtpSsl)
                    .withSmtpStartTls(smtpStarttls)
                    .withUsername(username)
                    .withPassword(password)
                    .withMaxEmailAttachmentsSizeMegs(maxAttachmentSizeForEmail)
                    .withAuthenticationType(authType)
                    .withActive(active);
        }

    } // end WsEmailAccountData class

    private static class MockEmailRepositoryImpl implements EmailRepository {

        @Override
        public Email create(Email email) {
            return email;
        }

        @Override
        public List<Email> getAllForCard(long reference, DaoQueryOptions queryOptions) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public Email getOneOrNull(long emailId) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public Email update(Email email) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void delete(Email email) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public List<Email> getAllForOutgoingProcessing() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public List<Email> getAllForErrorProcessing() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public List<Email> getByMessageId(String messageId) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public Email getLastWithReferenceBySenderAndSubjectFuzzyMatchingOrNull(String from, String subject) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public List<Email> getAllForTemplate(long templateId) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public Email getLastReceivedEmail() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
    } // end MockEmailRepositoryImpl class

    private static class MockInstanceInfoService implements InstanceInfoService {

        public MockInstanceInfoService() {
        }

        @Override
        public String getVersion() {
            return "aVersion";
        }

        @Override
        public String getRevision() {
            return "aRevision";
        }

        @Override
        public String getNodeId() {
            return "aNodeId";
        }

        @Override
        public String getInstanceName() {
            return "aInstanceName";
        }
    } // end MockInstanceInfoService class

    private static class MockEmailReceiveConfigImpl implements EmailReceiveConfig {

        public MockEmailReceiveConfigImpl() {
            super();
        }

        @Override
        public String getIncomingFolder() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public String getReceivedFolder() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public String getRejectedFolder() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public Function<Email, EmailProcessedAction> getCallback() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public String getAccount() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public EmailReceivedAction getReceivedEmailAction() {
            return EmailReceivedAction.ERA_DO_NOTHING;
        }
    } // end MockEmailReceiveConfigImpl class

    private static class MockEmailSignatureService implements EmailSignatureService {

        public MockEmailSignatureService() {
        }

        @Override
        public List<EmailSignature> getAll() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public EmailSignature getOneByCode(String code) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public EmailSignature getOne(long id) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public EmailSignature create(EmailSignature emailSignature) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public EmailSignature update(EmailSignature emailSignature) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void delete(long id) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public String getSignatureHtmlForCurrentUser(long id) {
            return "<i>aSignatureHtmlForCurrentUser<i>";
        }
    } // end MockEmailSignatureService class

    private static class MockLockServiceImpl implements LockService {

        public MockLockServiceImpl() {
            super();
        }

        @Override
        public LockResponse aquireLock(String itemId, LockScope lockScope) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public LockResponse aquireLockOrWait(String itemId, LockScope lockScope, long waitForMillis) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public ItemLock getLockOrNull(String itemId) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void releaseLock(ItemLock itemLock) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void deleteLock(String lockId) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void releaseAllLocks() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public List<ItemLock> getAllLocks() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void requireNotLockedByOthers(String itemId) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void requireLockedByCurrent(String itemId) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public LockResponse aquireLockOrWait(String itemId, LockScope lockScope) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
    } // end MockLockServiceImpl class

}
