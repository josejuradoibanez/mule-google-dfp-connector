package org.mule.modules.google.dfp.services;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.mule.modules.google.dfp.exceptions.GetOrdersException;

import com.google.api.ads.dfp.axis.factory.DfpServices;
import com.google.api.ads.dfp.axis.utils.v201505.StatementBuilder;
import com.google.api.ads.dfp.axis.v201505.ApiException;
import com.google.api.ads.dfp.axis.v201505.DateTime;
import com.google.api.ads.dfp.axis.v201505.Order;
import com.google.api.ads.dfp.axis.v201505.OrderPage;
import com.google.api.ads.dfp.axis.v201505.OrderServiceInterface;
import com.google.api.ads.dfp.lib.client.DfpSession;

public class OrderService {
	private static final Logger logger = Logger.getLogger(OrderService.class);

	protected OrderServiceInterface createOrderService(DfpSession session) {
		DfpServices dfpServices = new DfpServices();

		// Get the Order service.
		OrderServiceInterface ordersService = dfpServices.get(session,
				OrderServiceInterface.class);

		return ordersService;
	}

	public List<Order> getOrdersByStatement(DfpSession session,
			DateTime lastModifiedDateTime, DateTime snapshotDateTime)
			throws GetOrdersException {
		try {

			OrderServiceInterface ordersInterface = createOrderService(session);

			// Create a statement to select all orders modified since
			// lastModifiedDateTime.
			StatementBuilder statementBuilder = new StatementBuilder()
					.where("lastModifiedDateTime > :lastModifiedDateTime AND lastModifiedDateTime <= :snapshotDateTime")
					.orderBy("lastModifiedDateTime ASC")
					.limit(StatementBuilder.SUGGESTED_PAGE_LIMIT)
					.withBindVariableValue("lastModifiedDateTime",
							lastModifiedDateTime)
					.withBindVariableValue("snapshotDateTime", snapshotDateTime);

			// Default for total result set size.
			int totalResultSetSize = 0;
			List<Order> results = new ArrayList<Order>();

			logger.info("Getting all modified orders.");

			do {
				// Get orders by statement.
				OrderPage page = ordersInterface
						.getOrdersByStatement(statementBuilder.toStatement());

				if (page.getResults() != null) {
					totalResultSetSize = page.getTotalResultSetSize();
					for (Order order : page.getResults()) {
						results.add(order);
					}
				}

				statementBuilder
						.increaseOffsetBy(StatementBuilder.SUGGESTED_PAGE_LIMIT);
			} while (statementBuilder.getOffset() < totalResultSetSize);

			logger.info("Retrieved " + totalResultSetSize + " orders.");

			return results;
		} catch (ApiException e) {
			throw new GetOrdersException(e);
		} catch (RemoteException e) {
			throw new GetOrdersException(e);
		}

	}

}
