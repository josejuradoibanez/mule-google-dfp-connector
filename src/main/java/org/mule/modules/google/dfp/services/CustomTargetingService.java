package org.mule.modules.google.dfp.services;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.mule.modules.google.dfp.exceptions.CustomTargetingException;

import com.google.api.ads.dfp.axis.factory.DfpServices;
import com.google.api.ads.dfp.axis.utils.v201602.StatementBuilder;
import com.google.api.ads.dfp.axis.v201602.ApiException;
import com.google.api.ads.dfp.axis.v201602.CustomTargetingKey;
import com.google.api.ads.dfp.axis.v201602.CustomTargetingKeyPage;
import com.google.api.ads.dfp.axis.v201602.CustomTargetingServiceInterface;
import com.google.api.ads.dfp.axis.v201602.CustomTargetingValue;
import com.google.api.ads.dfp.axis.v201602.CustomTargetingValuePage;
import com.google.api.ads.dfp.lib.client.DfpSession;

public class CustomTargetingService {

	private static final Logger logger = Logger
			.getLogger(CustomTargetingService.class);

	protected CustomTargetingServiceInterface createCustomTargetingService(
			DfpSession session) {
		DfpServices dfpServices = new DfpServices();

		CustomTargetingServiceInterface customTargetingService = dfpServices
				.get(session, CustomTargetingServiceInterface.class);

		return customTargetingService;
	}

	public List<CustomTargetingKey> getCustomTargetingKeysByStatement(
			DfpSession session) throws CustomTargetingException {
		try {
			CustomTargetingServiceInterface customTargetingService = createCustomTargetingService(session);

			StatementBuilder statementBuilder = new StatementBuilder().orderBy(
					"id ASC").limit(StatementBuilder.SUGGESTED_PAGE_LIMIT);

			logger.info("Retrieving custom targeting keys.");

			int totalResultSetSize = 0;
			List<CustomTargetingKey> results = new ArrayList<CustomTargetingKey>();

			CustomTargetingKeyPage initialPage = customTargetingService
					.getCustomTargetingKeysByStatement(statementBuilder
							.toStatement());
			totalResultSetSize = initialPage.getTotalResultSetSize();

			do {
				CustomTargetingKeyPage page = customTargetingService
						.getCustomTargetingKeysByStatement(statementBuilder
								.toStatement());

				if (page.getResults() != null) {
					for (CustomTargetingKey customTargetingKey : page
							.getResults()) {
						results.add(customTargetingKey);
					}
				}

				statementBuilder
						.increaseOffsetBy(StatementBuilder.SUGGESTED_PAGE_LIMIT);
			} while (statementBuilder.getOffset() < totalResultSetSize);

			logger.info("Number of results found: " + totalResultSetSize);

			logger.info("Number of results retrieved: " + results.size());
			return results;

		} catch (ApiException e) {
			throw new CustomTargetingException(e);
		} catch (RemoteException e) {
			throw new CustomTargetingException(e);
		} catch (Exception e) {
			throw new CustomTargetingException(e);
		}
	}

	public List<CustomTargetingValue> getCustomTargetingValuesByStatement(
			DfpSession session) throws CustomTargetingException {
		try {
			CustomTargetingServiceInterface customTargetingService = createCustomTargetingService(session);

			StatementBuilder statementBuilder = new StatementBuilder().orderBy(
					"id ASC").limit(StatementBuilder.SUGGESTED_PAGE_LIMIT);

			logger.info("Retrieving custom targeting values.");

			int totalResultSetSize = 0;
			List<CustomTargetingValue> results = new ArrayList<CustomTargetingValue>();

			CustomTargetingValuePage initialPage = customTargetingService
					.getCustomTargetingValuesByStatement(statementBuilder
							.toStatement());
			totalResultSetSize = initialPage.getTotalResultSetSize();

			do {
				CustomTargetingValuePage page = customTargetingService
						.getCustomTargetingValuesByStatement(statementBuilder
								.toStatement());

				if (page.getResults() != null) {
					for (CustomTargetingValue customTargetingValue : page
							.getResults()) {
						results.add(customTargetingValue);
					}
				}

				statementBuilder
						.increaseOffsetBy(StatementBuilder.SUGGESTED_PAGE_LIMIT);
			} while (statementBuilder.getOffset() < totalResultSetSize);

			logger.info("Number of results found: " + totalResultSetSize);

			logger.info("Number of results retrieved: " + results.size());
			return results;

		} catch (ApiException e) {
			throw new CustomTargetingException(e);
		} catch (RemoteException e) {
			throw new CustomTargetingException(e);
		} catch (Exception e) {
			throw new CustomTargetingException(e);
		}
	}
}
