package org.mule.modules.google.dfp.services;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.mule.modules.google.dfp.exceptions.GetOrdersException;

import com.google.api.ads.dfp.axis.factory.DfpServices;
import com.google.api.ads.dfp.axis.utils.v201602.StatementBuilder;
import com.google.api.ads.dfp.axis.v201602.ApiException;
import com.google.api.ads.dfp.axis.v201602.DateTime;
import com.google.api.ads.dfp.axis.v201602.Order;
import com.google.api.ads.dfp.axis.v201602.OrderPage;
import com.google.api.ads.dfp.axis.v201602.OrderServiceInterface;
import com.google.api.ads.dfp.lib.client.DfpSession;
import com.google.api.client.repackaged.com.google.common.base.Joiner;
import com.google.common.collect.Lists;

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
					.orderBy("id ASC")
					.limit(StatementBuilder.SUGGESTED_PAGE_LIMIT)
					.withBindVariableValue("lastModifiedDateTime",
							lastModifiedDateTime)
					.withBindVariableValue("snapshotDateTime", snapshotDateTime);

			// Default for total result set size.
			int totalResultSetSize = 0;
			List<Order> results = new ArrayList<Order>();

			logger.info("Getting all modified orders.");

			OrderPage initialPage = ordersInterface
					.getOrdersByStatement(statementBuilder.toStatement());
			totalResultSetSize = initialPage.getTotalResultSetSize();

			do {
				// Get orders by statement.
				OrderPage page = ordersInterface
						.getOrdersByStatement(statementBuilder.toStatement());

				if (page.getResults() != null) {

					for (Order order : page.getResults()) {
						results.add(order);
					}
				}

				statementBuilder
						.increaseOffsetBy(StatementBuilder.SUGGESTED_PAGE_LIMIT);
			} while (statementBuilder.getOffset() < totalResultSetSize);

			logger.info("Number of results found: " + totalResultSetSize + '\n'
					+ "Number of results retrieved: " + results.size());

			return results;
		} catch (ApiException e) {
			throw new GetOrdersException(e);
		} catch (RemoteException e) {
			throw new GetOrdersException(e);
		}

	}

	public List<Order> getOrdersById(DfpSession session, List<Long> ids)
			throws GetOrdersException {
		try {

			OrderServiceInterface orderService = createOrderService(session);

			List<List<Long>> idsBatches = Lists.partition(ids, 400);
			List<Order> results = new ArrayList<Order>();

			for (List<Long> currentBatch : idsBatches) {

				String whereClauseFilter = Joiner.on(", ").join(currentBatch);
				String whereQueryStatement = "id IN (" + whereClauseFilter
						+ ")";

				StatementBuilder statementBuilder = new StatementBuilder()
						.where(whereQueryStatement).limit(
								StatementBuilder.SUGGESTED_PAGE_LIMIT);

				// Default for total result set size.
				int totalResultSetSize = 0;

				OrderPage initialPage = orderService.getOrdersByStatement(statementBuilder
								.toStatement());
				totalResultSetSize = initialPage.getTotalResultSetSize();

				do {
					OrderPage page = orderService.getOrdersByStatement(statementBuilder
							.toStatement());

					if (page.getResults() != null) {
						totalResultSetSize = page.getTotalResultSetSize();
						for (Order order : page.getResults()) {
							results.add(order);
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
			throw new GetOrdersException(e);
		} catch (RemoteException e) {
			throw new GetOrdersException(e);
		}

	}

}
