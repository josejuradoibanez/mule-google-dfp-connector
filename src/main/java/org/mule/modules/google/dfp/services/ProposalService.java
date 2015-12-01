package org.mule.modules.google.dfp.services;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.mule.modules.google.dfp.exceptions.GetProposalsException;

import com.google.api.ads.dfp.axis.factory.DfpServices;
import com.google.api.ads.dfp.axis.utils.v201505.StatementBuilder;
import com.google.api.ads.dfp.axis.v201505.ApiException;
import com.google.api.ads.dfp.axis.v201505.DateTime;
import com.google.api.ads.dfp.axis.v201505.Proposal;
import com.google.api.ads.dfp.axis.v201505.ProposalPage;
import com.google.api.ads.dfp.axis.v201505.ProposalServiceInterface;
import com.google.api.ads.dfp.lib.client.DfpSession;

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
					.orderBy("lastModifiedDateTime ASC")
					.limit(StatementBuilder.SUGGESTED_PAGE_LIMIT)
					.withBindVariableValue("lastModifiedDateTime",
							lastModifiedDateTime)
					.withBindVariableValue("snapshotDateTime", snapshotDateTime);

			// Default for total result set size.
			int totalResultSetSize = 0;

			List<Proposal> results = new ArrayList<Proposal>();

			logger.info("Getting all modified proposals.");

			do {
				// Get proposals by statement.
				ProposalPage page = proposalService
						.getProposalsByStatement(statementBuilder.toStatement());

				if (page.getResults() != null) {
					totalResultSetSize = page.getTotalResultSetSize();
					for (Proposal proposal : page.getResults()) {
						results.add(proposal);
					}
				}

				statementBuilder
						.increaseOffsetBy(StatementBuilder.SUGGESTED_PAGE_LIMIT);
			} while (statementBuilder.getOffset() < totalResultSetSize);

			logger.info("Retrieved " + totalResultSetSize + " proposals.");

			return results;
		} catch (ApiException e) {
			throw new GetProposalsException(e);
		} catch (RemoteException e) {
			throw new GetProposalsException(e);
		}
	}

}
