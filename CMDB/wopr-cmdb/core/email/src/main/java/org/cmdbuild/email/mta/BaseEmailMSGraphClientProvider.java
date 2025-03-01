/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.email.mta;

import static com.google.common.base.Preconditions.checkArgument;
import com.microsoft.graph.requests.GraphServiceClient;
import org.cmdbuild.email.EmailAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class to create a session for a <code>GraphServiceClient</code>
 *
 * @author afelice
 */
abstract class BaseEmailMSGraphClientProvider implements EmailMSGraphClientProvider {

    protected final EmailAccount emailAccount;
    protected GraphServiceClient msGraphClient;
    protected static EmailMSGraphClientStrategy emailMSGraphClientStrategy = new EmailMSGraphClientStrategy();

    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected BaseEmailMSGraphClientProvider(EmailAccount account) {
        this.emailAccount = account;
    }

    /**
     * MS Graph has not an idempotent test for connection of sender. So only a
     * test on receiver is performed, reading the number of new email
     *
     * @param emailAccount
     * @param logger
     */
    public static void testConnection(EmailAccount emailAccount, Logger logger) {
        logger.info("test ms graph connection for account = {}", emailAccount);
        EmailMSGraphClientProvider msGraphClientProvider = emailMSGraphClientStrategy.buildMSGraphClientProvider(emailAccount);

        GraphServiceClient currentMsGraphClient = msGraphClientProvider.create();
        Long count = currentMsGraphClient.users(emailAccount.getAddress()).messages().count().buildRequest().get();
        checkArgument(count != null, "ms graph cannot connect");
        logger.info("ms graph connection ok");
    }

    @Override
    public void close() {
        if (msGraphClient != null) {
            logger.debug("destroying ms graph context");
            msGraphClient = null;
        }
    }
}
