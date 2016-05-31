package org.mule.modules.google.dfp.services;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.mule.modules.google.dfp.exceptions.GetRateCardsException;

import com.google.api.ads.dfp.axis.factory.DfpServices;
import com.google.api.ads.dfp.axis.utils.v201602.StatementBuilder;
import com.google.api.ads.dfp.axis.v201602.ApiException;
import com.google.api.ads.dfp.axis.v201602.DateTime;
import com.google.api.ads.dfp.axis.v201602.RateCard;
import com.google.api.ads.dfp.axis.v201602.RateCardPage;
import com.google.api.ads.dfp.axis.v201602.RateCardServiceInterface;
import com.google.api.ads.dfp.lib.client.DfpSession;

public class RateCardService {

	private static final Logger logger = Logger
			.getLogger(RateCardService.class);

	protected RateCardServiceInterface createRateCardService(DfpSession session) {
		DfpServices dfpServices = new DfpServices();

		// Get the RateCardService.
		RateCardServiceInterface rateCardService = dfpServices.get(session,
				RateCardServiceInterface.class);

		return rateCardService;
	}

	public List<RateCard> getRateCardsByLastModifiedDate(DfpSession session,
			DateTime lastModifiedDateTime, DateTime snapshotDateTime)
			throws GetRateCardsException {
		try {
			// Get the RateCardService.
			RateCardServiceInterface rateCardService = createRateCardService(session);

			// Create a statement to get RateCard by name
			StatementBuilder statementBuilder = new StatementBuilder()
					.where("lastModifiedDateTime > :lastModifiedDateTime AND lastModifiedDateTime <= :snapshotDateTime")
					.orderBy("id ASC")
					.limit(StatementBuilder.SUGGESTED_PAGE_LIMIT)
					.withBindVariableValue("lastModifiedDateTime",
							lastModifiedDateTime)
					.withBindVariableValue("snapshotDateTime", snapshotDateTime);

			logger.info("Retrieving the last modified rate cards.");

			int totalResultSetSize = 0;
			List<RateCard> results = new ArrayList<RateCard>();

			RateCardPage initialPage = rateCardService
					.getRateCardsByStatement(statementBuilder.toStatement());
			totalResultSetSize = initialPage.getTotalResultSetSize();

			do {
				// Get companies by statement.
				RateCardPage page = rateCardService
						.getRateCardsByStatement(statementBuilder.toStatement());

				if (page.getResults() != null) {
					for (RateCard RateCard : page.getResults()) {
						results.add(RateCard);
					}
				}

				statementBuilder
						.increaseOffsetBy(StatementBuilder.SUGGESTED_PAGE_LIMIT);
			} while (statementBuilder.getOffset() < totalResultSetSize);

			logger.info("Number of results found: " + totalResultSetSize + '\n'
					+ "Number of results retrieved: " + results.size());

			return results;

		} catch (ApiException e) {
			throw new GetRateCardsException(e);
		} catch (RemoteException e) {
			throw new GetRateCardsException(e);
		} catch (Exception e) {
			throw new GetRateCardsException(e);
		}
	}
}
