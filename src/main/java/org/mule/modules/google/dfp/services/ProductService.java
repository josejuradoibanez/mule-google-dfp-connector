package org.mule.modules.google.dfp.services;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.mule.modules.google.dfp.exceptions.GetProductsByStatementException;

import com.google.api.ads.dfp.axis.factory.DfpServices;
import com.google.api.ads.dfp.axis.utils.v201505.StatementBuilder;
import com.google.api.ads.dfp.axis.v201505.ApiException;
import com.google.api.ads.dfp.axis.v201505.Date;
import com.google.api.ads.dfp.axis.v201505.DateTime;
import com.google.api.ads.dfp.axis.v201505.Product;
import com.google.api.ads.dfp.axis.v201505.ProductPage;
import com.google.api.ads.dfp.axis.v201505.ProductServiceInterface;
import com.google.api.ads.dfp.lib.client.DfpSession;

public class ProductService {

	private static final Logger logger = Logger.getLogger(ProductService.class);

	protected ProductServiceInterface createProductService(DfpSession session) {
		DfpServices dfpServices = new DfpServices();

		// Get the CompanyService.
		ProductServiceInterface productService = dfpServices.get(session,
				ProductServiceInterface.class);

		return productService;
	}

	public List<Product> getProductsByStatement(DfpSession session,
			DateTime lastModifiedDateTime)
			throws GetProductsByStatementException {
		try {
			// Get the CompanyService.
			ProductServiceInterface productService = createProductService(session);

			Date date = new Date(2015, 7, 8);
			DateTime testDate = new DateTime(date, 9, 19, 30, "Etc/GMT");

			// Create a statement to get company by name
			StatementBuilder statementBuilder = new StatementBuilder();
//					.orderBy("lastModifiedDateTime ASC")
//					.where("name = :name")
//					.withBindVariableValue("name", "PH_Commercial_Audio_Mobile")
//					.limit(7);

			// Get companies by statement.
			ProductPage page = productService
					.getProductsByStatement(statementBuilder.toStatement());

			Product[] products = page.getResults();

			List<Product> results = new ArrayList<Product>();
			if (products != null) {
				results = Arrays.asList(products);
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
