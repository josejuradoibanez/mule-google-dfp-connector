package org.mule.modules.google.dfp.services;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.mule.modules.google.dfp.exceptions.GetProductsByStatementException;

import com.google.api.ads.dfp.axis.factory.DfpServices;
import com.google.api.ads.dfp.axis.utils.v201602.StatementBuilder;
import com.google.api.ads.dfp.axis.v201602.ApiException;
import com.google.api.ads.dfp.axis.v201602.DateTime;
import com.google.api.ads.dfp.axis.v201602.Product;
import com.google.api.ads.dfp.axis.v201602.ProductPage;
import com.google.api.ads.dfp.axis.v201602.ProductServiceInterface;
import com.google.api.ads.dfp.lib.client.DfpSession;
import com.google.api.client.repackaged.com.google.common.base.Joiner;
import com.google.common.collect.Lists;

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
					.orderBy("id ASC")
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

			logger.info("Number of results found: " + totalResultSetSize + '\n'
					+ "Number of results retrieved: " + results.size());

			return results;

		} catch (ApiException e) {
			throw new GetProductsByStatementException(e);
		} catch (RemoteException e) {
			throw new GetProductsByStatementException(e);
		} catch (Exception e) {
			throw new GetProductsByStatementException(e);
		}
	}

	public List<Product> getProductsById(DfpSession session, List<Long> ids)
			throws GetProductsByStatementException {
		try {

			ProductServiceInterface productService = createProductService(session);

			List<List<Long>> idsBatches = Lists.partition(ids, 400);
			List<Product> results = new ArrayList<Product>();

			for (List<Long> currentBatch : idsBatches) {

				String whereClauseFilter = Joiner.on(", ").join(currentBatch);
				String whereQueryStatement = "id IN (" + whereClauseFilter
						+ ")";

				StatementBuilder statementBuilder = new StatementBuilder()
						.where(whereQueryStatement).limit(
								StatementBuilder.SUGGESTED_PAGE_LIMIT);

				// Default for total result set size.
				int totalResultSetSize = 0;

				ProductPage initialPage = productService
						.getProductsByStatement(statementBuilder.toStatement());
				totalResultSetSize = initialPage.getTotalResultSetSize();

				do {
					ProductPage page = productService
							.getProductsByStatement(statementBuilder.toStatement());

					if (page.getResults() != null) {
						totalResultSetSize = page.getTotalResultSetSize();
						for (Product product : page.getResults()) {
							results.add(product);
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

		} catch (ApiException e) {
			throw new GetProductsByStatementException(e);
		} catch (RemoteException e) {
			throw new GetProductsByStatementException(e);
		} catch (Exception e) {
			throw new GetProductsByStatementException(e);
		}
	}
}
