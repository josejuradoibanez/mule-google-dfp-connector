package org.mule.modules.google.dfp.services;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.mule.modules.google.dfp.exceptions.GetProposalsException;

import com.google.api.ads.dfp.axis.factory.DfpServices;
import com.google.api.ads.dfp.axis.utils.v201602.StatementBuilder;
import com.google.api.ads.dfp.axis.v201602.ApiException;
import com.google.api.ads.dfp.axis.v201602.DateTime;
import com.google.api.ads.dfp.axis.v201602.Proposal;
import com.google.api.ads.dfp.axis.v201602.ProposalPage;
import com.google.api.ads.dfp.axis.v201602.ProposalServiceInterface;
import com.google.api.ads.dfp.lib.client.DfpSession;
import com.google.api.client.repackaged.com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class ProposalService {

	private static final Logger logger = Logger
			.getLogger(ProposalService.class);

	protected ProposalServiceInterface createProposalService(DfpSession session) {
		DfpServices dfpServices = new DfpServices();

		// Get the Proposal service.
		ProposalServiceInterface proposalsService = dfpServices.get(session,
				ProposalServiceInterface.class);

		return proposalsService;
	}

	public List<Proposal> getProposalsByStatement(DfpSession session,
			DateTime lastModifiedDateTime, DateTime snapshotDateTime)
			throws GetProposalsException {
		try {
			ProposalServiceInterface proposalService = createProposalService(session);

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

			List<Proposal> results = new ArrayList<Proposal>();

			ProposalPage initialPage = proposalService
					.getProposalsByStatement(statementBuilder.toStatement());
			totalResultSetSize = initialPage.getTotalResultSetSize();

			logger.info("Getting all modified proposals.");

			do {
				// Get proposals by statement.
				ProposalPage page = proposalService
						.getProposalsByStatement(statementBuilder.toStatement());

				if (page.getResults() != null) {
					for (Proposal proposal : page.getResults()) {
						results.add(proposal);
					}
				}

				statementBuilder
						.increaseOffsetBy(StatementBuilder.SUGGESTED_PAGE_LIMIT);
			} while (statementBuilder.getOffset() < totalResultSetSize);

			logger.info("Number of results found: " + totalResultSetSize
					+ '\n' + "Number of results retrieved: "
					+ results.size());
			
			return results;
		} catch (ApiException e) {
			throw new GetProposalsException(e);
		} catch (RemoteException e) {
			throw new GetProposalsException(e);
		}
	}
	
	public List<Proposal> getProposalsById(DfpSession session,
			List<Long> ids)
			throws GetProposalsException {
		try {
			ProposalServiceInterface proposalService = createProposalService(session);

			List<List<Long>> idsBatches = Lists.partition(ids, 400);
			List<Proposal> results = new ArrayList<Proposal>();

			for (List<Long> currentBatch : idsBatches) {

				String whereClauseFilter = Joiner.on(", ").join(currentBatch);
				String whereQueryStatement = "id IN (" + whereClauseFilter
						+ ")";

				StatementBuilder statementBuilder = new StatementBuilder()
						.where(whereQueryStatement).limit(
								StatementBuilder.SUGGESTED_PAGE_LIMIT);

				// Default for total result set size.
				int totalResultSetSize = 0;

				ProposalPage initialPage = proposalService.getProposalsByStatement(statementBuilder
								.toStatement());
				totalResultSetSize = initialPage.getTotalResultSetSize();

				do {
					ProposalPage page = proposalService.getProposalsByStatement(statementBuilder
							.toStatement());

					if (page.getResults() != null) {
						totalResultSetSize = page.getTotalResultSetSize();
						for (Proposal proposal : page
								.getResults()) {
							results.add(proposal);
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
			throw new GetProposalsException(e);
		} catch (RemoteException e) {
			throw new GetProposalsException(e);
		}
	}


}
