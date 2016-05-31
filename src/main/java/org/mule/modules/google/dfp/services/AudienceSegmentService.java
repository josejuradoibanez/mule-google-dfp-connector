package org.mule.modules.google.dfp.services;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.mule.modules.google.dfp.exceptions.AudienceSegmentException;

import com.google.api.ads.dfp.axis.factory.DfpServices;
import com.google.api.ads.dfp.axis.utils.v201602.StatementBuilder;
import com.google.api.ads.dfp.axis.v201602.ApiException;
import com.google.api.ads.dfp.axis.v201602.AudienceSegment;
import com.google.api.ads.dfp.axis.v201602.AudienceSegmentPage;
import com.google.api.ads.dfp.axis.v201602.AudienceSegmentServiceInterface;
import com.google.api.ads.dfp.axis.v201602.DateTime;
import com.google.api.ads.dfp.lib.client.DfpSession;

public class AudienceSegmentService {
	private static final Logger logger = Logger
			.getLogger(AudienceSegmentService.class);

	protected AudienceSegmentServiceInterface createAudienceSegmentService(
			DfpSession session) {
		DfpServices dfpServices = new DfpServices();

		AudienceSegmentServiceInterface audienceSegmentService = dfpServices
				.get(session, AudienceSegmentServiceInterface.class);

		return audienceSegmentService;
	}

	public List<AudienceSegment> getAudienceSegmentsByStatement(
			DfpSession session) throws AudienceSegmentException {
		try {
			AudienceSegmentServiceInterface audienceSegmentService = createAudienceSegmentService(session);

			//.where("lastModifiedDateTime > :lastModifiedDateTime AND lastModifiedDateTime <= :snapshotDateTime")
			StatementBuilder statementBuilder = new StatementBuilder()
					.orderBy("id ASC")
					.limit(StatementBuilder.SUGGESTED_PAGE_LIMIT);
//					.withBindVariableValue("lastModifiedDateTime",
//							lastModifiedDateTime)
//					.withBindVariableValue("snapshotDateTime", snapshotDateTime);

			logger.info("Retrieving the all audience segments.");

			int totalResultSetSize = 0;
			List<AudienceSegment> results = new ArrayList<AudienceSegment>();

			AudienceSegmentPage initialPage = audienceSegmentService
					.getAudienceSegmentsByStatement(statementBuilder
							.toStatement());
			totalResultSetSize = initialPage.getTotalResultSetSize();

			do {
				AudienceSegmentPage page = audienceSegmentService
						.getAudienceSegmentsByStatement(statementBuilder
								.toStatement());

				if (page.getResults() != null) {
					for (AudienceSegment audience : page.getResults()) {
						results.add(audience);
					}
				}

				statementBuilder
						.increaseOffsetBy(StatementBuilder.SUGGESTED_PAGE_LIMIT);
			} while (statementBuilder.getOffset() < totalResultSetSize);

			logger.info("Number of results found: " + totalResultSetSize);

			logger.info("Number of results retrieved: " + results.size());
			return results;

		} catch (ApiException e) {
			throw new AudienceSegmentException(e);
		} catch (RemoteException e) {
			throw new AudienceSegmentException(e);
		} catch (Exception e) {
			throw new AudienceSegmentException(e);
		}
	}

}
