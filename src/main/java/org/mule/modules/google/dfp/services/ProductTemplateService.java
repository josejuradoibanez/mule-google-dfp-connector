package org.mule.modules.google.dfp.services;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.mule.modules.google.dfp.exceptions.GetProductTemplatesException;

import com.google.api.ads.dfp.axis.factory.DfpServices;
import com.google.api.ads.dfp.axis.utils.v201602.StatementBuilder;
import com.google.api.ads.dfp.axis.v201602.DateTime;
import com.google.api.ads.dfp.axis.v201602.ProductTemplate;
import com.google.api.ads.dfp.axis.v201602.ProductTemplatePage;
import com.google.api.ads.dfp.axis.v201602.ProductTemplateServiceInterface;
import com.google.api.ads.dfp.lib.client.DfpSession;
import com.google.api.client.repackaged.com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class ProductTemplateService {

	private static final Logger logger = Logger
			.getLogger(ProductTemplateService.class);

	protected ProductTemplateServiceInterface createProductTemplateService(
			DfpSession session) {
		DfpServices dfpServices = new DfpServices();

		// Get the Product Service.
		ProductTemplateServiceInterface productTemplateService = dfpServices
				.get(session, ProductTemplateServiceInterface.class);

		return productTemplateService;
	}

	public List<ProductTemplate> getProductTemplatesByStatement(
			DfpSession session, DateTime lastModifiedDateTime,
			DateTime snapshotDateTime) throws GetProductTemplatesException {
		try {

			ProductTemplateServiceInterface productTemplateService = createProductTemplateService(session);

			StatementBuilder statementBuilder = new StatementBuilder()
					.where("lastModifiedDateTime > :lastModifiedDateTime AND lastModifiedDateTime <= :snapshotDateTime")
					.orderBy("id ASC")
					.limit(StatementBuilder.SUGGESTED_PAGE_LIMIT)
					.withBindVariableValue("lastModifiedDateTime",
							lastModifiedDateTime)
					.withBindVariableValue("snapshotDateTime", snapshotDateTime);

			int totalResultSetSize = 0;

			List<ProductTemplate> results = new ArrayList<ProductTemplate>();
			logger.info("Retrieving modified product templates.");

			ProductTemplatePage initialPage = productTemplateService
					.getProductTemplatesByStatement(statementBuilder
							.toStatement());
			totalResultSetSize = initialPage.getTotalResultSetSize();

			do {
				// Get product templates by statement.
				ProductTemplatePage page = productTemplateService
						.getProductTemplatesByStatement(statementBuilder
								.toStatement());

				if (page.getResults() != null) {
					for (ProductTemplate productTemplate : page.getResults()) {
						results.add(productTemplate);
					}
				}

				statementBuilder
						.increaseOffsetBy(StatementBuilder.SUGGESTED_PAGE_LIMIT);
			} while (statementBuilder.getOffset() < totalResultSetSize);

			logger.info("Number of results found: " + totalResultSetSize
					+ '\n' + "Number of results retrieved: "
					+ results.size());
			
			return results;

		} catch (RemoteException e) {
			throw new GetProductTemplatesException(e);
		}
	}

	public List<ProductTemplate> getProductTemplatesById(DfpSession session,
			List<Long> ids) throws GetProductTemplatesException {
		try {

			ProductTemplateServiceInterface productTemplateService = createProductTemplateService(session);

			List<List<Long>> idsBatches = Lists.partition(ids, 400);
			List<ProductTemplate> results = new ArrayList<ProductTemplate>();

			for (List<Long> currentBatch : idsBatches) {

				String whereClauseFilter = Joiner.on(", ").join(currentBatch);
				String whereQueryStatement = "id IN (" + whereClauseFilter
						+ ")";

				StatementBuilder statementBuilder = new StatementBuilder()
						.where(whereQueryStatement).limit(
								StatementBuilder.SUGGESTED_PAGE_LIMIT);

				// Default for total result set size.
				int totalResultSetSize = 0;

				ProductTemplatePage initialPage = productTemplateService
						.getProductTemplatesByStatement(statementBuilder
								.toStatement());
				totalResultSetSize = initialPage.getTotalResultSetSize();

				do {
					ProductTemplatePage page = productTemplateService
							.getProductTemplatesByStatement(statementBuilder
									.toStatement());

					if (page.getResults() != null) {
						totalResultSetSize = page.getTotalResultSetSize();
						for (ProductTemplate productTemplate : page
								.getResults()) {
							results.add(productTemplate);
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
			throw new GetProductTemplatesException(e);
		}

	}
}
