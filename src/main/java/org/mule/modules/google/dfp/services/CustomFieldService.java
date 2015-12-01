package org.mule.modules.google.dfp.services;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.mule.modules.google.dfp.exceptions.GetCustomFieldsException;

import com.google.api.ads.dfp.axis.factory.DfpServices;
import com.google.api.ads.dfp.axis.utils.v201505.StatementBuilder;
import com.google.api.ads.dfp.axis.v201505.ApiException;
import com.google.api.ads.dfp.axis.v201505.CustomField;
import com.google.api.ads.dfp.axis.v201505.CustomFieldOption;
import com.google.api.ads.dfp.axis.v201505.CustomFieldPage;
import com.google.api.ads.dfp.axis.v201505.CustomFieldServiceInterface;
import com.google.api.ads.dfp.lib.client.DfpSession;

public class CustomFieldService {

	private static final Logger logger = Logger
			.getLogger(CustomFieldService.class);

	protected CustomFieldServiceInterface createCustomFieldService(
			DfpSession session) {
		DfpServices dfpServices = new DfpServices();

		// Get the CustomField service.
		CustomFieldServiceInterface customFieldsService = dfpServices.get(
				session, CustomFieldServiceInterface.class);

		return customFieldsService;
	}

	public CustomFieldOption getCustomFieldOption(DfpSession session, Long id)
			throws GetCustomFieldsException {
		try {
			CustomFieldServiceInterface customFieldService = createCustomFieldService(session);
			logger.info("Getting all custom fields options.");

			// Default for total result set size.
			CustomFieldOption result = customFieldService
					.getCustomFieldOption(id);

			return result;
		} catch (ApiException e) {
			throw new GetCustomFieldsException(e);
		} catch (RemoteException e) {
			throw new GetCustomFieldsException(e);
		}

	}

	public List<CustomField> getCustomFieldsByStatement(DfpSession session)
			throws GetCustomFieldsException {
		try {

			CustomFieldServiceInterface customFieldService = createCustomFieldService(session);

			// Create a statement to only select customFields updated or created
			// since the lastModifiedDateTime.
			StatementBuilder statementBuilder = new StatementBuilder().orderBy(
					"id ASC").limit(StatementBuilder.SUGGESTED_PAGE_LIMIT);

			logger.info("Getting all custom fields.");

			// Default for total result set size.
			int totalResultSetSize = 0;
			List<CustomField> results = new ArrayList<CustomField>();
			
			CustomFieldPage initialPage = customFieldService
					.getCustomFieldsByStatement(statementBuilder
							.toStatement());
			totalResultSetSize = initialPage.getTotalResultSetSize();
			
			do {
				CustomFieldPage page = customFieldService
						.getCustomFieldsByStatement(statementBuilder
								.toStatement());

				if (page.getResults() != null) {
					for (CustomField customField : page.getResults()) {
						results.add(customField);
					}
				}

				statementBuilder
						.increaseOffsetBy(StatementBuilder.SUGGESTED_PAGE_LIMIT);
			} while (statementBuilder.getOffset() < totalResultSetSize);

			logger.info("Number of results found: " + totalResultSetSize);

			logger.info("Number of results retrieved: " + results.size());

			return results;

		} catch (ApiException e) {
			throw new GetCustomFieldsException(e);
		} catch (RemoteException e) {
			throw new GetCustomFieldsException(e);
		}
	}

}
