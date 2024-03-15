/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.email.mta;

import com.azure.identity.ClientCertificateCredential;
import com.azure.identity.ClientCertificateCredentialBuilder;
import com.google.common.annotations.VisibleForTesting;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.requests.GraphServiceClient;
import java.io.ByteArrayInputStream;
import java.io.File;
import static java.lang.String.format;
import java.nio.charset.StandardCharsets;
import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.email.EmailException;
import static org.cmdbuild.utils.encode.CmPackUtils.unpack;
import org.cmdbuild.utils.io.CmIoUtils;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author afelice
 */
public class EmailMSGraphClient_ClientCertificate extends BaseEmailMSGraphClientProvider {

    public final static String CLIENT_ID_KEY = "clientId";
    public final static String TENANT_ID_KEY = "tenantId";
    public final static String CLIENT_PRIVATE_KEY = "clientPrivateKey";
    public final static String CLIENT_CERTIFICATE_KEY = "clientCertificate";

    @VisibleForTesting
    protected File _tempCertificateFile;
    private final ClientCertificateCredentialBuilder wrappedBuilder = new ClientCertificateCredentialBuilder();


    public EmailMSGraphClient_ClientCertificate(EmailAccount emailAccount) {
        super(emailAccount);
    }

    /**
     * <b>CMDBuild customization</b>: stores to file in file system the PEM
     * certificate for authenticating to AAD, because
     * <code>ClientCertificateCredentialBuilder.pemCertificate(InputString)</code>
     * is private and so not directly invocable.
     *
     * @return
     */
    @Override
    public final GraphServiceClient create() {
        try {
            JSONObject msConfig = new JSONObject(emailAccount.getPassword());

            String clientId = msConfig.getString(CLIENT_ID_KEY);
            String tenantId = msConfig.getString(TENANT_ID_KEY);

            // Creation of temp file
            String filePrefix = format("%s_%s", clientId, tenantId);
            String fileSuffix = "pem";
            String certificatePath = storeAsTempFile(filePrefix, fileSuffix, new ByteArrayInputStream(unpackFullCertificatePem(msConfig).getBytes(StandardCharsets.UTF_8)));

            ClientCertificateCredential clientCertificateCredential = wrappedBuilder
                    .clientId(clientId)
                    .tenantId(tenantId)
                    .pemCertificate(certificatePath)
                    .build();

            TokenCredentialAuthProvider tokenCredAuthProvider = new TokenCredentialAuthProvider(list(firstNotBlank(toStringOrNull(msConfig.optString("scope")), "https://graph.microsoft.com/.default")), clientCertificateCredential);

            msGraphClient = GraphServiceClient
                    .builder()
                    .authenticationProvider(tokenCredAuthProvider)
                    .buildClient();
            return msGraphClient;
        } catch (ClientException ex) {
            throw new EmailException(ex, "error creating imap session for account = %s", emailAccount);
        }
    }

    @Override
    public void close() {
        releaseTemporaryData();
        super.close();
    }

    /**
     * Unpack and concatenate <b>both</b> application Private Key PEM and
     * application Certificate PEM <b>in the same PEM (text) format
     *
     * @param msConfig
     * @return
     * @throws JSONException
     */
    private String unpackFullCertificatePem(JSONObject msConfig) throws JSONException {
        return unpack(msConfig.getString(CLIENT_PRIVATE_KEY)) + unpack(msConfig.getString(CLIENT_CERTIFICATE_KEY));
    }

    /**
     *
     * @param filePrefix
     * @param fileSuffix
     * @param byteInputStream
     * @return absolute path of created temporary file
     */
    private String storeAsTempFile(String filePrefix, String fileSuffix, ByteArrayInputStream byteInputStream) {
        byte[] certificateBytes = byteInputStream.readAllBytes();
        _tempCertificateFile = CmIoUtils.currentUserTempFile(
                filePrefix, fileSuffix,
                certificateBytes);

        return _tempCertificateFile.getAbsolutePath();
    }

    private void releaseTemporaryData() {
        if (_tempCertificateFile != null) {
            _tempCertificateFile.delete();
        }
    }
}
