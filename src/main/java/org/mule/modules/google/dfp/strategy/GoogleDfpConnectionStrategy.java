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
import org.mule.api.annotations.param.ConnectionKey;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.Optional;
import org.mule.modules.google.dfp.services.AudienceSegmentService;
import org.mule.modules.google.dfp.services.CompanyService;
import org.mule.modules.google.dfp.services.ContactService;
import org.mule.modules.google.dfp.services.CustomFieldService;
import org.mule.modules.google.dfp.services.CustomTargetingService;
import org.mule.modules.google.dfp.services.LineItemService;
import org.mule.modules.google.dfp.services.OrderService;
import org.mule.modules.google.dfp.services.PqlService;
import org.mule.modules.google.dfp.services.ProductService;
import org.mule.modules.google.dfp.services.ProductTemplateService;
import org.mule.modules.google.dfp.services.ProposalLineItemService;
import org.mule.modules.google.dfp.services.ProposalService;
import org.mule.modules.google.dfp.services.RateCardService;
import org.mule.modules.google.dfp.services.ReconciliationReportRowService;
import org.mule.modules.google.dfp.services.ReconciliationReportService;
import org.mule.modules.google.dfp.services.ReportService;
import org.mule.modules.google.dfp.services.UserService;

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
	private ContactService contactService;
	private CompanyService companyService;
	private LineItemService lineItemService;
	private CustomFieldService customFieldService;
	private OrderService orderService;
	private ProposalService proposalService;
	private ProposalLineItemService proposalLineItemService;
	private ProductService productService;
	private UserService userService;
	private ProductTemplateService productTemplateService;
	private ReconciliationReportService reconciliationReportService;
	private ReconciliationReportRowService reconciliationReportRowService;
	private RateCardService rateCardService;
	private PqlService pqlService;
	private AudienceSegmentService audienceSegmentService;
	private CustomTargetingService customTargetingService;

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
	public void connect(@ConnectionKey String clientId,
			@Password String clientSecret) throws ConnectionException {
		try {

			/**
			 * Initialise all the Google DFP services on startup. Once
			 * initialised, each service is configured if necessary.
			 */

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

			// Initialize and configure Contact Service
			contactService = new ContactService();

			// Initialize and configure Product Service
			productService = new ProductService();

			// Initialize and configure Product Template Service
			productTemplateService = new ProductTemplateService();

			// Initialize and configure LineItem Service
			lineItemService = new LineItemService();

			// Initialize and configure Custom Field Service
			customFieldService = new CustomFieldService();

			// Initialize and configure Order Service
			orderService = new OrderService();

			// Initialize and configure Proposal Service
			proposalService = new ProposalService();

			userService = new UserService();

			// Initialize and configure Proposal Line Item Service
			proposalLineItemService = new ProposalLineItemService();

			// Initialize and configure reconciliation report service
			reconciliationReportService = new ReconciliationReportService();

			// Initialize and configure reconciliation report row service
			reconciliationReportRowService = new ReconciliationReportRowService();

			rateCardService = new RateCardService();
			audienceSegmentService = new AudienceSegmentService();
			customTargetingService = new CustomTargetingService();

			pqlService = new PqlService();

			/*
			 * Generate a refreshable OAuth2 credential similar to a ClientLogin
			 * token and can be used in place of a service account.
			 */
			Credential oAuth2Credential = new OfflineCredentials.Builder()
					.forApi(Api.DFP).withClientSecrets(clientId, clientSecret)
					.withRefreshToken(refreshToken)
					.withTokenUrlServer(tokenServerUrl).build()
					.generateCredential();

			/*
			 * Construct a DfpSession.
			 */
			session = new DfpSession.Builder()
					.withApplicationName(applicationName)
					.withEndpoint(endpoint)
					.withOAuth2Credential(oAuth2Credential)
					.withNetworkCode(networkCode).build();
		} catch (OAuthException e) {
			throw new ConnectionException(
					ConnectionExceptionCode.INCORRECT_CREDENTIALS, "001",
					e.getMessage(), e);
		} catch (ValidationException e) {
			throw new ConnectionException(ConnectionExceptionCode.UNKNOWN,
					"002", e.getMessage(), e);
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

	public ContactService getContactService() {
		return contactService;
	}

	public void setContactService(ContactService contactService) {
		this.contactService = contactService;
	}

	public ProductService getProductService() {
		return productService;
	}

	public void setProductService(ProductService productService) {
		this.productService = productService;
	}

	public ProductTemplateService getProductTemplateService() {
		return productTemplateService;
	}

	public void setProductTemplateService(
			ProductTemplateService productTemplateService) {
		this.productTemplateService = productTemplateService;
	}

	public LineItemService getLineItemService() {
		return lineItemService;
	}

	public void setLineItemService(LineItemService lineItemService) {
		this.lineItemService = lineItemService;
	}

	public CustomFieldService getCustomFieldService() {
		return customFieldService;
	}

	public void setCustomFieldService(CustomFieldService customFieldService) {
		this.customFieldService = customFieldService;
	}

	public OrderService getOrderService() {
		return orderService;
	}

	public void setOrderService(OrderService orderService) {
		this.orderService = orderService;
	}

	public ProposalService getProposalService() {
		return proposalService;
	}

	public void setProposalService(ProposalService proposalService) {
		this.proposalService = proposalService;
	}

	public ProposalLineItemService getProposalLineItemService() {
		return proposalLineItemService;
	}

	public void setProposalLineItemService(
			ProposalLineItemService proposalLineItemService) {
		this.proposalLineItemService = proposalLineItemService;
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

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public ReconciliationReportService getReconciliationReportService() {
		return reconciliationReportService;
	}

	public void setReconciliationReportService(
			ReconciliationReportService reconciliationReportService) {
		this.reconciliationReportService = reconciliationReportService;
	}

	public ReconciliationReportRowService getReconciliationReportRowService() {
		return reconciliationReportRowService;
	}

	public void setReconciliationReportRowService(
			ReconciliationReportRowService reconciliationReportRowService) {
		this.reconciliationReportRowService = reconciliationReportRowService;
	}

	public RateCardService getRateCardService() {
		return rateCardService;
	}

	public void setRateCardService(RateCardService rateCardService) {
		this.rateCardService = rateCardService;
	}

	public AudienceSegmentService getAudienceSegmentService() {
		return audienceSegmentService;
	}

	public void setAudienceSegmentService(
			AudienceSegmentService audienceSegmentService) {
		this.audienceSegmentService = audienceSegmentService;
	}

	public CustomTargetingService getCustomTargetingService() {
		return customTargetingService;
	}

	public void setCustomTargetingService(
			CustomTargetingService customTargetingService) {
		this.customTargetingService = customTargetingService;
	}

	public PqlService getPqlService() {
		return pqlService;
	}

	public void setPqlService(PqlService pqlService) {
		this.pqlService = pqlService;
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