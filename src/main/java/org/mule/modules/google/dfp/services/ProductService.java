package org.mule.modules.google.dfp.services;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.mule.modules.google.dfp.exceptions.GetProductsByStatementException;

import com.google.api.ads.dfp.axis.factory.DfpServices;
import com.google.api.ads.dfp.axis.utils.v201505.StatementBuilder;
import com.google.api.ads.dfp.axis.v201505.ApiException;
import com.google.api.ads.dfp.axis.v201505.DateTime;
import com.google.api.ads.dfp.axis.v201505.Product;
import com.google.api.ads.dfp.axis.v201505.ProductPage;
import com.google.api.ads.dfp.axis.v201505.ProductServiceInterface;
import com.google.api.ads.dfp.lib.client.DfpSession;

public class ProductService {

	private static final Logger logger = Logger.getLogger(ProductService.class);

	protected ProductServiceInterface createProductService(DfpSession session) {
		DfpServices dfpServices = new DfpServices();

		// Get the Product Service.
		ProductServiceInterface productService = dfpServices.get(session,
				ProductServiceInterface.class);

		return productService;
	}

	public List<Product> getProductsByStatement(DfpSession session,
			DateTime lastModifiedDateTime, DateTime snapshotDateTime)
			throws GetProductsByStatementException {
		try {

			ProductServiceInterface productService = createProductService(session);

			StatementBuilder statementBuilder = new StatementBuilder()
					.where("lastModifiedDateTime > :lastModifiedDateTime AND lastModifiedDateTime <= :snapshotDateTime")
					.orderBy("lastModifiedDateTime ASC")
					.limit(StatementBuilder.SUGGESTED_PAGE_LIMIT)
					.withBindVariableValue("lastModifiedDateTime",
							lastModifiedDateTime)
					.withBindVariableValue("snapshotDateTime", snapshotDateTime);

			int totalResultSetSize = 0;
			List<Product> results = new ArrayList<Product>();
			logger.info("Retrieving modified products.");

			
			ProductPage initialPage = productService
					.getProductsByStatement(statementBuilder.toStatement());
			totalResultSetSize = initialPage.getTotalResultSetSize();
			
			do {
				// Get products by statement.
				ProductPage page = productService
						.getProductsByStatement(statementBuilder.toStatement());

				if (page.getResults() != null) {
				

					for (Product product : page.getResults()) {
						results.add(product);
					}
				}

				statementBuilder
						.increaseOffsetBy(StatementBuilder.SUGGESTED_PAGE_LIMIT);
			} while (statementBuilder.getOffset() < totalResultSetSize);

			logger.info("Number of results found: " + totalResultSetSize);
			logger.info("Number of results retrieved: " + results.size());

			return results;

		} catch (ApiException e) {
			throw new GetProductsByStatementException(e);
		} catch (RemoteException e) {
			throw new GetProductsByStatementException(e);
		} catch (Exception e) {
			throw new GetProductsByStatementException(e);
		}
	}
}
