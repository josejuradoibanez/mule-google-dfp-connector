package org.mule.modules.google.dfp.services;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.mule.modules.google.dfp.exceptions.GetProductTemplatesException;

import com.google.api.ads.dfp.axis.factory.DfpServices;
import com.google.api.ads.dfp.axis.utils.v201505.StatementBuilder;
import com.google.api.ads.dfp.axis.v201505.DateTime;
import com.google.api.ads.dfp.axis.v201505.ProductTemplate;
import com.google.api.ads.dfp.axis.v201505.ProductTemplatePage;
import com.google.api.ads.dfp.axis.v201505.ProductTemplateServiceInterface;
import com.google.api.ads.dfp.lib.client.DfpSession;

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
			DfpSession session, DateTime lastModifiedDateTime)
			throws GetProductTemplatesException {
		try {

			ProductTemplateServiceInterface productTemplateService = createProductTemplateService(session);

			StatementBuilder statementBuilder = new StatementBuilder()
					.orderBy("lastModifiedDateTime ASC")
					.limit(StatementBuilder.SUGGESTED_PAGE_LIMIT);
//					.where("lastModifiedDateTime > :lastModifiedDateTime")
//					.withBindVariableValue("lastModifiedDateTime",
//							lastModifiedDateTime);

			int totalResultSetSize = 0;

			List<ProductTemplate> results = new ArrayList<ProductTemplate>();
			logger.info("Retrieving modified product templates.");

			do {
				// Get product templates by statement.
				ProductTemplatePage page;
				page = productTemplateService
						.getProductTemplatesByStatement(statementBuilder
								.toStatement());

				if (page.getResults() != null) {
					totalResultSetSize = page.getTotalResultSetSize();
					for (ProductTemplate productTemplate : page.getResults()) {
						results.add(productTemplate);
					}
				}

				statementBuilder
						.increaseOffsetBy(StatementBuilder.SUGGESTED_PAGE_LIMIT);
			} while (statementBuilder.getOffset() < totalResultSetSize);

			logger.info("Retrieved " + totalResultSetSize
					+ " product templates.");

			return results;

		} catch (RemoteException e) {
			throw new GetProductTemplatesException(e);
		}

	}
}
