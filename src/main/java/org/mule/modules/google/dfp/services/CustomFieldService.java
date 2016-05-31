package org.mule.modules.google.dfp.services;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.mule.modules.google.dfp.exceptions.GetCustomFieldsException;

import com.google.api.ads.dfp.axis.factory.DfpServices;
import com.google.api.ads.dfp.axis.utils.v201602.StatementBuilder;
import com.google.api.ads.dfp.axis.v201602.ApiException;
import com.google.api.ads.dfp.axis.v201602.CustomField;
import com.google.api.ads.dfp.axis.v201602.CustomFieldOption;
import com.google.api.ads.dfp.axis.v201602.CustomFieldPage;
import com.google.api.ads.dfp.axis.v201602.CustomFieldServiceInterface;
import com.google.api.ads.dfp.lib.client.DfpSession;
import com.google.api.client.repackaged.com.google.common.base.Joiner;
import com.google.common.collect.Lists;

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
					.getCustomFieldsByStatement(statementBuilder.toStatement());
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

			logger.info("Number of results found: " + totalResultSetSize + '\n'
					+ "Number of results retrieved: " + results.size());

			return results;

		} catch (ApiException e) {
			throw new GetCustomFieldsException(e);
		} catch (RemoteException e) {
			throw new GetCustomFieldsException(e);
		}
	}

	public List<CustomField> getCustomFieldsById(DfpSession session,
			List<Long> ids) throws GetCustomFieldsException {
		try {

			CustomFieldServiceInterface customFieldService = createCustomFieldService(session);

			List<List<Long>> idsBatches = Lists.partition(ids, 400);
			List<CustomField> results = new ArrayList<CustomField>();

			for (List<Long> currentBatch : idsBatches) {

				String whereClauseFilter = Joiner.on(", ").join(currentBatch);
				String whereQueryStatement = "id IN (" + whereClauseFilter
						+ ")";

				StatementBuilder statementBuilder = new StatementBuilder()
						.where(whereQueryStatement).limit(
								StatementBuilder.SUGGESTED_PAGE_LIMIT);

				// Default for total result set size.
				int totalResultSetSize = 0;

				CustomFieldPage initialPage = customFieldService
						.getCustomFieldsByStatement(statementBuilder
								.toStatement());
				totalResultSetSize = initialPage.getTotalResultSetSize();

				do {
					CustomFieldPage page = customFieldService
							.getCustomFieldsByStatement(statementBuilder
									.toStatement());

					if (page.getResults() != null) {
						totalResultSetSize = page.getTotalResultSetSize();
						for (CustomField customField : page.getResults()) {
							results.add(customField);
						}
					}

					statementBuilder
							.increaseOffsetBy(StatementBuilder.SUGGESTED_PAGE_LIMIT);
				} while (statementBuilder.getOffset() < totalResultSetSize);

				logger.info("Number of results found: " + totalResultSetSize
						+ '\n' + "Number of results retrieved: "
						+ results.size());

			}
			return results;

		} catch (RemoteException e) {
			throw new GetCustomFieldsException(e);
		}
	}

}
