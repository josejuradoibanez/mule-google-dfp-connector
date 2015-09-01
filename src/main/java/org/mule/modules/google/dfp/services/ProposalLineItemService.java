package org.mule.modules.google.dfp.services;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.mule.modules.google.dfp.exceptions.GetProposalLineItemsException;

import com.google.api.ads.dfp.axis.factory.DfpServices;
import com.google.api.ads.dfp.axis.utils.v201505.StatementBuilder;
import com.google.api.ads.dfp.axis.v201505.ApiException;
import com.google.api.ads.dfp.axis.v201505.DateTime;
import com.google.api.ads.dfp.axis.v201505.ProposalLineItem;
import com.google.api.ads.dfp.axis.v201505.ProposalLineItemPage;
import com.google.api.ads.dfp.axis.v201505.ProposalLineItemServiceInterface;
import com.google.api.ads.dfp.lib.client.DfpSession;

public class ProposalLineItemService {

	private static final Logger logger = Logger.getLogger(OrderService.class);

	protected ProposalLineItemServiceInterface createProposalLineItemService(
			DfpSession session) {
		DfpServices dfpServices = new DfpServices();

		// Get the ProposalLineItems service.
		ProposalLineItemServiceInterface proposalLineItemService = dfpServices
				.get(session, ProposalLineItemServiceInterface.class);

		return proposalLineItemService;
	}

	public List<ProposalLineItem> getProposalLineItemsByStatement(
			DfpSession session, DateTime lastModifiedDate)
			throws GetProposalLineItemsException {
		try {
			ProposalLineItemServiceInterface proposalLineItemService = createProposalLineItemService(session);

			// Create a statement to only select proposals that were modified
			// recently.
			StatementBuilder statementBuilder = new StatementBuilder()
					.where("lastModifiedDateTime = :lastModifiedDateTime")
					.orderBy("id ASC")
					.limit(StatementBuilder.SUGGESTED_PAGE_LIMIT)
					.withBindVariableValue("lastModifiedDateTime",
							lastModifiedDate);

			// Default for total result set size.
			int totalResultSetSize = 0;

			List<ProposalLineItem> results = new ArrayList<ProposalLineItem>();

			logger.info("Getting all modified proposal line items");

			do {
				// Get proposal line items by statement.
				ProposalLineItemPage page = proposalLineItemService
						.getProposalLineItemsByStatement(statementBuilder
								.toStatement());

				if (page.getResults() != null) {
					totalResultSetSize = page.getTotalResultSetSize();
					for (ProposalLineItem proposalLineItem : page.getResults()) {
						results.add(proposalLineItem);
					}
				}

				statementBuilder
						.increaseOffsetBy(StatementBuilder.SUGGESTED_PAGE_LIMIT);
			} while (statementBuilder.getOffset() < totalResultSetSize);

			logger.info("Retrieved " + totalResultSetSize
					+ " proposal line items.");

			return results;
		} catch (ApiException e) {
			throw new GetProposalLineItemsException(e);
		} catch (RemoteException e) {
			throw new GetProposalLineItemsException(e);
		}
	}

}
