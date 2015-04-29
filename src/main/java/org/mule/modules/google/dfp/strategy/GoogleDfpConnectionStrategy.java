/**
 * (c) 2003-2015 Ricston, Ltd. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 */

/**
 * (c) 2003-2015 MuleSoft, Inc. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 */

package org.mule.modules.google.dfp.strategy;

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
	@Default(value = "https://ads.google.com/")
	private String endpoint;

	/**
	 * The network code for Google DFP
	 */
	@Configurable
	@Required
	private String networkCode;

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