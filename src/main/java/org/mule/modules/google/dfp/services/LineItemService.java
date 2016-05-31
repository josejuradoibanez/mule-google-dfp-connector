package org.mule.modules.google.dfp.services;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.mule.modules.google.dfp.exceptions.GetLineItemsException;

import com.google.api.ads.dfp.axis.factory.DfpServices;
import com.google.api.ads.dfp.axis.utils.v201602.StatementBuilder;
import com.google.api.ads.dfp.axis.v201602.DateTime;
import com.google.api.ads.dfp.axis.v201602.LineItem;
import com.google.api.ads.dfp.axis.v201602.LineItemPage;
import com.google.api.ads.dfp.axis.v201602.LineItemServiceInterface;
import com.google.api.ads.dfp.lib.client.DfpSession;
import com.google.api.client.repackaged.com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class LineItemService {

	private static final Logger logger = Logger
			.getLogger(LineItemService.class);

	protected LineItemServiceInterface createLineItemService(DfpSession session) {
		DfpServices dfpServices = new DfpServices();

		// Get the LineItem Service.
		LineItemServiceInterface lineItemService = dfpServices.get(session,
				LineItemServiceInterface.class);

		return lineItemService;
	}

	public List<LineItem> getLineItemsByStatement(DfpSession session,
			DateTime lastModifiedDateTime, DateTime snapshotDateTime)
			throws GetLineItemsException {
		try {

			LineItemServiceInterface lineItemService = createLineItemService(session);

			StatementBuilder statementBuilder = new StatementBuilder()
					.where("lastModifiedDateTime > :lastModifiedDateTime AND lastModifiedDateTime <= :snapshotDateTime")
					.orderBy("id ASC")
					.limit(StatementBuilder.SUGGESTED_PAGE_LIMIT)
					.withBindVariableValue("lastModifiedDateTime",
							lastModifiedDateTime)
					.withBindVariableValue("snapshotDateTime", snapshotDateTime);

			int totalResultSetSize = 0;

			List<LineItem> results = new ArrayList<LineItem>();

			LineItemPage initialPage = lineItemService
					.getLineItemsByStatement(statementBuilder.toStatement());
			totalResultSetSize = initialPage.getTotalResultSetSize();

			do {
				LineItemPage page = lineItemService
						.getLineItemsByStatement(statementBuilder.toStatement());
				logger.debug("Current Offset is "
						+ statementBuilder.getOffset());

				if (page.getResults() != null) {
					logger.debug("Result Set Size:" + totalResultSetSize);
					for (LineItem lineItem : page.getResults()) {
						results.add(lineItem);
					}
				}

				statementBuilder
						.increaseOffsetBy(StatementBuilder.SUGGESTED_PAGE_LIMIT);

				logger.debug("Offset increased to: "
						+ statementBuilder.getOffset());
			} while (statementBuilder.getOffset() < totalResultSetSize);

			logger.info("Number of results found: " + totalResultSetSize
					+ '\n' + "Number of results retrieved: "
					+ results.size());


			return results;

		} catch (RemoteException e) {
			throw new GetLineItemsException(e);
		}

	}

	public List<LineItem> getFilteredLineItemsByStatement(DfpSession session,
			List<Long> orderIds) throws GetLineItemsException {
		try {

			LineItemServiceInterface lineItemService = createLineItemService(session);

			List<List<Long>> orderIdsBatches = Lists.partition(orderIds, 400);
			List<LineItem> results = new ArrayList<LineItem>();

			for (List<Long> currentBatch : orderIdsBatches) {

				String whereClauseFilter = Joiner.on(", ").join(currentBatch);
				String whereQueryStatement = "orderId IN (" + whereClauseFilter
						+ ")";

				StatementBuilder statementBuilder = new StatementBuilder()
						.where(whereQueryStatement).limit(
								StatementBuilder.SUGGESTED_PAGE_LIMIT);

				// Default for total result set size.
				int totalResultSetSize = 0;
				logger.info("Getting the filtered line items");

				LineItemPage initialPage = lineItemService
						.getLineItemsByStatement(statementBuilder.toStatement());
				totalResultSetSize = initialPage.getTotalResultSetSize();

				do {
					// Get line items by statement.
					LineItemPage page = lineItemService
							.getLineItemsByStatement(statementBuilder
									.toStatement());

					if (page.getResults() != null) {
						totalResultSetSize = page.getTotalResultSetSize();
						for (LineItem lineItem : page.getResults()) {
							results.add(lineItem);
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
			throw new GetLineItemsException(e);
		}

	}

	public List<LineItem> getLineItemsById(DfpSession session, List<Long> ids)
			throws GetLineItemsException {
		try {

			LineItemServiceInterface lineItemService = createLineItemService(session);

			List<List<Long>> idsBatches = Lists.partition(ids, 400);
			List<LineItem> results = new ArrayList<LineItem>();

			for (List<Long> currentBatch : idsBatches) {

				String whereClauseFilter = Joiner.on(", ").join(currentBatch);
				String whereQueryStatement = "id IN (" + whereClauseFilter
						+ ")";

				StatementBuilder statementBuilder = new StatementBuilder()
						.where(whereQueryStatement).limit(
								StatementBuilder.SUGGESTED_PAGE_LIMIT);

				// Default for total result set size.
				int totalResultSetSize = 0;
				logger.info("Getting the filtered line items by ID.");

				LineItemPage initialPage = lineItemService
						.getLineItemsByStatement(statementBuilder.toStatement());
				totalResultSetSize = initialPage.getTotalResultSetSize();

				do {
					// Get line items by statement.
					LineItemPage page = lineItemService
							.getLineItemsByStatement(statementBuilder
									.toStatement());

					if (page.getResults() != null) {
						totalResultSetSize = page.getTotalResultSetSize();
						for (LineItem lineItem : page.getResults()) {
							results.add(lineItem);
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
			throw new GetLineItemsException(e);
		}

	}

}
