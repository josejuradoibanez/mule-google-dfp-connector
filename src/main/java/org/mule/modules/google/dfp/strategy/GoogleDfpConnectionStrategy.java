/**
 * (c) 2003-2015 Ricston, Ltd. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 */

/**
 * (c) 2003-2015 MuleSoft, Inc. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 */

package org.mule.modules.google.dfp.strategy;

import java.util.List;

import javax.annotation.PostConstruct;

import org.mule.api.ConnectionException;
import org.mule.api.ConnectionExceptionCode;
import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.Connect;
import org.mule.api.annotations.ConnectionIdentifier;
import org.mule.api.annotations.Disconnect;
import org.mule.api.annotations.Required;
import org.mule.api.annotations.TestConnectivity;
import org.mule.api.annotations.ValidateConnection;
import org.mule.api.annotations.components.ConnectionManagement;
import org.mule.api.annotations.display.Password;
import org.mule.api.annotations.lifecycle.Start;
import org.mule.api.annotations.param.ConnectionKey;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.Optional;
import org.mule.modules.google.dfp.services.CompanyService;
import org.mule.modules.google.dfp.services.ReconciliationReportRowService;
import org.mule.modules.google.dfp.services.ReconciliationReportService;
import org.mule.modules.google.dfp.services.ReportService;

import com.google.api.ads.common.lib.auth.OfflineCredentials;
import com.google.api.ads.common.lib.auth.OfflineCredentials.Api;
import com.google.api.ads.common.lib.exception.OAuthException;
import com.google.api.ads.common.lib.exception.ValidationException;
import com.google.api.ads.dfp.lib.client.DfpSession;
import com.google.api.client.auth.oauth2.Credential;

/**
 * Configuration type Strategy
 *
 * @author MuleSoft, Inc.
 */
@ConnectionManagement(friendlyName = "Config")
public class GoogleDfpConnectionStrategy {

    private DfpSession session;
    private ReportService reportService;
    private CompanyService companyService;
    private ReconciliationReportService reconciliationReportService;
    private ReconciliationReportRowService reconciliationReportRowService;

    /**
     * The refresh token for Google DFP
     */
    @Configurable
    @Required
    private String refreshToken;

    /**
     * The token server URL for Google DFP
     */
    @Configurable
    @Optional
    @Default(value = "https://accounts.google.com/o/oauth2/token")
    private String tokenServerUrl;

    /**
     * The application name for Google DFP
     */
    @Configurable
    @Required
    private String applicationName;

    /**
     * The endpoint for Google DFP
     */
    @Configurable
    @Optional
    @Default(value = "https://ads.google.com/")
    private String endpoint;

    /**
     * The network code for Google DFP
     */
    @Configurable
    @Required
    private String networkCode;

    /**
     * List of custom IDs
     */
    @Configurable
    @Optional
    private List<String> customIds;



    // Commented out @Start method since it is not being accessed as of devkit-3.7
    

//    /**
//     * Initialize all the Google DFP services. Once initialize, each service is configured if necessary.
//     */
//    @Start
//    public void initialiseAndConfigureServices() {
//
//        // Initialize and configure Report Service
//        reportService = new ReportService();
//
//        if (customIds != null && !customIds.isEmpty()) {
//            long[] customFieldIds = new long[customIds.size()];
//
//            for (int i = 0; i < customIds.size(); i++) {
//                customFieldIds[i] = Long.parseLong(customIds.get(i));
//            }
//
//            reportService.setCustomFieldsIds(customFieldIds);
//        }
//
//        // Initialize and configure Company Service
//        companyService = new CompanyService();
//
//        // Initialize and configure reconciliation report service
//        reconciliationReportService = new ReconciliationReportService();
//
//        // Initialize and configure reconciliation report row service
//        reconciliationReportRowService = new ReconciliationReportRowService();
//    }

    // testing without...
//    @PostConstruct
//    public void init() {
//        session = getSession();
//    }

    /**
     * Connect
     * 
     * @param clientId
     *            The client Id
     * @param clientSecret
     *            The client secret
     * @throws ConnectionException
     */
    @Connect
    @TestConnectivity(label = "Test Connection")
    public void connect(@ConnectionKey String clientId, @Password String clientSecret) throws ConnectionException {
        try {

            // Initialize and configure Report Service
            reportService = new ReportService();

            if (customIds != null && !customIds.isEmpty()) {
                long[] customFieldIds = new long[customIds.size()];

                for (int i = 0; i < customIds.size(); i++) {
                    customFieldIds[i] = Long.parseLong(customIds.get(i));
                }

                reportService.setCustomFieldsIds(customFieldIds);
            }

            // Initialize and configure Company Service
            companyService = new CompanyService();

            // Initialize and configure reconciliation report service
            reconciliationReportService = new ReconciliationReportService();

            // Initialize and configure reconciliation report row service
            reconciliationReportRowService = new ReconciliationReportRowService();

            /*
             * Generate a refreshable OAuth2 credential similar to a ClientLogin token and can be used in place of a service account.
             */
            Credential oAuth2Credential = new OfflineCredentials.Builder().forApi(Api.DFP).withClientSecrets(clientId, clientSecret).withRefreshToken(refreshToken)
                    .withTokenUrlServer(tokenServerUrl).build().generateCredential();

            /*
             * Construct a DfpSession.
             */
            session = new DfpSession.Builder().withApplicationName(applicationName).withEndpoint(endpoint).withOAuth2Credential(oAuth2Credential).withNetworkCode(networkCode)
                    .build();
        } catch (OAuthException e) {
            throw new ConnectionException(ConnectionExceptionCode.INCORRECT_CREDENTIALS, "001", e.getMessage(), e);
        } catch (ValidationException e) {
            throw new ConnectionException(ConnectionExceptionCode.UNKNOWN, "002", e.getMessage(), e);
        }
    }

    /**
     * Disconnect
     */
    @Disconnect
    public void disconnect() {
        session = null;
    }

    /**
     * Are we connected
     */
    @ValidateConnection
    public boolean isConnected() {
        return session != null;
    }

    /**
     * Are we connected
     */
    @ConnectionIdentifier
    public String connectionId() {
        return "001";
    }

    /**
     * @return The DFP session
     */
    public DfpSession getSession() {
        return session;
    }

    /**
     * @param session
     *            The DFP session
     */
    public void setSession(DfpSession session) {
        this.session = session;
    }
    
    
    public ReportService getReportService() {
        return reportService;
    }
    

    
    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }
    

    
    public CompanyService getCompanyService() {
        return companyService;
    }
    

    
    public void setCompanyService(CompanyService companyService) {
        this.companyService = companyService;
    }
    

    public ReconciliationReportService getReconciliationReportService() {
        return reconciliationReportService;
    }

    
    public void setReconciliationReportService(ReconciliationReportService reconciliationReportService) {
        this.reconciliationReportService = reconciliationReportService;
    }

    
    public ReconciliationReportRowService getReconciliationReportRowService() {
        return reconciliationReportRowService;
    }

    
    public void setReconciliationReportRowService(ReconciliationReportRowService reconciliationReportRowService) {
        this.reconciliationReportRowService = reconciliationReportRowService;
    }

    
    public List<String> getCustomIds() {
        return customIds;
    }

    
    public void setCustomIds(List<String> customIds) {
        this.customIds = customIds;
    }

    /**
     * @return The refresh token for Google DFP
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * @param refreshToken
     *            The refresh token for Google DFP
     */
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    /**
     * @return The token server URL for Google DFP
     */
    public String getTokenServerUrl() {
        return tokenServerUrl;
    }

    /**
     * @param tokenServerUrl
     *            The token server URL for Google DFP
     */
    public void setTokenServerUrl(String tokenServerUrl) {
        this.tokenServerUrl = tokenServerUrl;
    }

    /**
     * @return The application name for Google DFP
     */
    public String getApplicationName() {
        return applicationName;
    }

    /**
     * @param applicationName
     *            The application name for Google DFP
     */
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    /**
     * @return The endpoint for Google DFP
     */
    public String getEndpoint() {
        return endpoint;
    }

    /**
     * @param endpoint
     *            The endpoint for Google DFP
     */
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    /**
     * 
     * @return The network code for Google DFP
     */
    public String getNetworkCode() {
        return networkCode;
    }

    /**
     * @param networkCode
     *            The network code for Google DFP
     */
    public void setNetworkCode(String networkCode) {
        this.networkCode = networkCode;
    }
}