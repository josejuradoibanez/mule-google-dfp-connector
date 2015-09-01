package org.mule.modules.google.dfp.services;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.mule.modules.google.dfp.exceptions.GetLineItemsException;

import com.google.api.ads.dfp.axis.factory.DfpServices;
import com.google.api.ads.dfp.axis.utils.v201505.StatementBuilder;
import com.google.api.ads.dfp.axis.v201505.DateTime;
import com.google.api.ads.dfp.axis.v201505.LineItem;
import com.google.api.ads.dfp.axis.v201505.LineItemPage;
import com.google.api.ads.dfp.axis.v201505.LineItemServiceInterface;
import com.google.api.ads.dfp.lib.client.DfpSession;

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
			DateTime lastModifiedDateTime) throws GetLineItemsException {
		try {

			LineItemServiceInterface lineItemService = createLineItemService(session);

			// Create a statement to only select line items updated or created
			// since the lastModifiedDateTime.
			StatementBuilder statementBuilder = new StatementBuilder()
					.where("lastModifiedDateTime >= :lastModifiedDateTime")
					.orderBy("id ASC")
					.limit(StatementBuilder.SUGGESTED_PAGE_LIMIT)
					.withBindVariableValue("lastModifiedDateTime",
							lastModifiedDateTime);

			// Default for total result set size.
			int totalResultSetSize = 0;

			List<LineItem> results = new ArrayList<LineItem>();

			logger.info("Getting all modified line items");

			do {
				// Get line items by statement.
				LineItemPage page = lineItemService
						.getLineItemsByStatement(statementBuilder.toStatement());

				if (page.getResults() != null) {
					totalResultSetSize = page.getTotalResultSetSize();
					for (LineItem lineItem : page.getResults()) {
						results.add(lineItem);
					}
				}

				statementBuilder
						.increaseOffsetBy(StatementBuilder.SUGGESTED_PAGE_LIMIT);
			} while (statementBuilder.getOffset() < totalResultSetSize);

			logger.info("Retrieved " + totalResultSetSize + " line items.");

			return results;

		} catch (RemoteException e) {
			throw new GetLineItemsException(e);
		}

	}

}