package org.mule.modules.google.dfp.services;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.mule.modules.google.dfp.exceptions.GetProposalLineItemsException;

import com.google.api.ads.dfp.axis.factory.DfpServices;
import com.google.api.ads.dfp.axis.utils.v201602.StatementBuilder;
import com.google.api.ads.dfp.axis.v201602.ApiException;
import com.google.api.ads.dfp.axis.v201602.DateTime;
import com.google.api.ads.dfp.axis.v201602.ProposalLineItem;
import com.google.api.ads.dfp.axis.v201602.ProposalLineItemPage;
import com.google.api.ads.dfp.axis.v201602.ProposalLineItemServiceInterface;
import com.google.api.ads.dfp.lib.client.DfpSession;
import com.google.api.client.repackaged.com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class ProposalLineItemService {

	private static final Logger logger = Logger
			.getLogger(ProposalLineItemService.class);

	protected ProposalLineItemServiceInterface createProposalLineItemService(
			DfpSession session) {
		DfpServices dfpServices = new DfpServices();

		// Get the ProposalLineItems service.
		ProposalLineItemServiceInterface proposalLineItemService = dfpServices
				.get(session, ProposalLineItemServiceInterface.class);

		return proposalLineItemService;
	}

	public List<ProposalLineItem> getProposalLineItemsByStatement(
			DfpSession session, DateTime lastModifiedDateTime,
			DateTime snapshotDateTime) throws GetProposalLineItemsException {
		try {
			ProposalLineItemServiceInterface proposalLineItemService = createProposalLineItemService(session);

			// Create a statement to only select proposals that were modified
			// recently.
			StatementBuilder statementBuilder = new StatementBuilder()
					.where("lastModifiedDateTime > :lastModifiedDateTime AND lastModifiedDateTime <= :snapshotDateTime")
					.orderBy("id ASC")
					.limit(StatementBuilder.SUGGESTED_PAGE_LIMIT)
					.withBindVariableValue("lastModifiedDateTime",
							lastModifiedDateTime)
					.withBindVariableValue("snapshotDateTime", snapshotDateTime);

			// Default for total result set size.
			int totalResultSetSize = 0;

			List<ProposalLineItem> results = new ArrayList<ProposalLineItem>();

			logger.info("Getting all modified proposal line items.");
			
			ProposalLineItemPage initialPage = proposalLineItemService
					.getProposalLineItemsByStatement(statementBuilder
							.toStatement());
			totalResultSetSize = initialPage.getTotalResultSetSize();
			do {
				// Get proposal line items by statement.
				ProposalLineItemPage page = proposalLineItemService
						.getProposalLineItemsByStatement(statementBuilder
								.toStatement());

				if (page.getResults() != null) {
					for (ProposalLineItem proposalLineItem : page.getResults()) {
						results.add(proposalLineItem);
					}
				}

				statementBuilder
						.increaseOffsetBy(StatementBuilder.SUGGESTED_PAGE_LIMIT);
			} while (statementBuilder.getOffset() < totalResultSetSize);


			logger.info("Number of results found: " + totalResultSetSize);
			logger.info("Number of results retrieved: " + results.size());

			return results;
		} catch (ApiException e) {
			throw new GetProposalLineItemsException(e);
		} catch (RemoteException e) {
			throw new GetProposalLineItemsException(e);
		}
	}

	public List<ProposalLineItem> getProposalLineItemsByProposalId(
			DfpSession session, List<Long> proposalIds)
			throws GetProposalLineItemsException {
		try {
			ProposalLineItemServiceInterface proposalLineItemService = createProposalLineItemService(session);

			List<List<Long>> proposalIdsBatches = Lists.partition(proposalIds,
					400);
			List<ProposalLineItem> results = new ArrayList<ProposalLineItem>();

			for (List<Long> currentBatch : proposalIdsBatches) {

				String whereClauseFilter = Joiner.on(", ").join(currentBatch);
				String whereQueryStatement = "proposalId IN ("
						+ whereClauseFilter + ")";

				// Create a statement to only select proposal lines filtered by
				// proposalID
				StatementBuilder statementBuilder = new StatementBuilder()
						.where(whereQueryStatement).orderBy("id ASC")
						.limit(StatementBuilder.SUGGESTED_PAGE_LIMIT);

				int totalResultSetSize = 0;

				logger.info("Getting the filtered proposal line items.");

				do {
					// Get proposal line items by statement.
					ProposalLineItemPage page = proposalLineItemService
							.getProposalLineItemsByStatement(statementBuilder
									.toStatement());

					if (page.getResults() != null) {
						totalResultSetSize = page.getTotalResultSetSize();
						for (ProposalLineItem proposalLineItem : page
								.getResults()) {
							results.add(proposalLineItem);
						}
					}

					statementBuilder
							.increaseOffsetBy(StatementBuilder.SUGGESTED_PAGE_LIMIT);
				} while (statementBuilder.getOffset() < totalResultSetSize);

				logger.info("Retrieved " + totalResultSetSize
						+ " proposal line items.");

			}

			return results;
		} catch (ApiException e) {
			throw new GetProposalLineItemsException(e);
		} catch (RemoteException e) {
			throw new GetProposalLineItemsException(e);
		}
	}
	
	public List<ProposalLineItem> getProposalLineItemsById(
			DfpSession session, List<Long> ids)
			throws GetProposalLineItemsException {
		try {
			ProposalLineItemServiceInterface proposalLineItemService = createProposalLineItemService(session);

			List<List<Long>> proposalIdsBatches = Lists.partition(ids,
					400);
			List<ProposalLineItem> results = new ArrayList<ProposalLineItem>();

			for (List<Long> currentBatch : proposalIdsBatches) {

				String whereClauseFilter = Joiner.on(", ").join(currentBatch);
				String whereQueryStatement = "id IN ("
						+ whereClauseFilter + ")";

				// Create a statement to only select proposal lines filtered by
				// proposalID
				StatementBuilder statementBuilder = new StatementBuilder()
						.where(whereQueryStatement).orderBy("id ASC")
						.limit(StatementBuilder.SUGGESTED_PAGE_LIMIT);

				int totalResultSetSize = 0;

				logger.info("Getting the filtered proposal line items by ID.");

				do {
					// Get proposal line items by statement.
					ProposalLineItemPage page = proposalLineItemService
							.getProposalLineItemsByStatement(statementBuilder
									.toStatement());

					if (page.getResults() != null) {
						totalResultSetSize = page.getTotalResultSetSize();
						for (ProposalLineItem proposalLineItem : page
								.getResults()) {
							results.add(proposalLineItem);
						}
					}

					statementBuilder
							.increaseOffsetBy(StatementBuilder.SUGGESTED_PAGE_LIMIT);
				} while (statementBuilder.getOffset() < totalResultSetSize);

				logger.info("Retrieved " + totalResultSetSize
						+ " proposal line items.");

			}

			return results;
		} catch (ApiException e) {
			throw new GetProposalLineItemsException(e);
		} catch (RemoteException e) {
			throw new GetProposalLineItemsException(e);
		}
	}

}
