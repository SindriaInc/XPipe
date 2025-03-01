/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.email.mta;

import com.azure.identity.UsernamePasswordCredential;
import com.azure.identity.UsernamePasswordCredentialBuilder;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.requests.GraphServiceClient;
import java.util.Map;
import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.email.EmailException;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_STRINGS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

/**
 *
 * @author afelice
 */
public class EmailMSGraphClient_Password extends BaseEmailMSGraphClientProvider {

    public final static String CLIENT_ID_KEY = "clientId";
    public final static String PASSWORD_KEY = "password";

    public EmailMSGraphClient_Password(EmailAccount emailAccount) {
        super(emailAccount);
    }

    @Override
    public final GraphServiceClient create() {
        try {
            Map<String, String> msConfig = fromJson(emailAccount.getPassword(), MAP_OF_STRINGS);

            UsernamePasswordCredential usernamePasswordCredential = new UsernamePasswordCredentialBuilder()
                    .clientId(msConfig.get(CLIENT_ID_KEY))
                    .username(emailAccount.getUsername())
                    .password(msConfig.get(PASSWORD_KEY))
                    .build();

            TokenCredentialAuthProvider tokenCredAuthProvider = new TokenCredentialAuthProvider(list(msConfig.getOrDefault("scope", "https://graph.microsoft.com/.default")), usernamePasswordCredential);

            this.msGraphClient = GraphServiceClient
                    .builder()
                    .authenticationProvider(tokenCredAuthProvider)
                    .buildClient();

            return this.msGraphClient;
        } catch (ClientException ex) {
            throw new EmailException(ex, "error creating imap session for account = %s", emailAccount);
        }
    }
}
