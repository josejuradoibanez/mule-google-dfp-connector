/**
 * (c) 2003-2015 Ricston, Ltd. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 */

package org.mule.modules.google.dfp.services;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.modules.google.dfp.exceptions.ReconciliationReportByIdException;

import com.google.api.ads.dfp.axis.factory.DfpServices;
import com.google.api.ads.dfp.axis.utils.v201411.StatementBuilder;
import com.google.api.ads.dfp.axis.v201411.ApiException;
import com.google.api.ads.dfp.axis.v201411.ReconciliationReport;
import com.google.api.ads.dfp.axis.v201411.ReconciliationReportPage;
import com.google.api.ads.dfp.axis.v201411.ReconciliationReportServiceInterface;
import com.google.api.ads.dfp.lib.client.DfpSession;

public class ReconciliationReportService {

	private Log logger = LogFactory.getLog(getClass());

	protected ReconciliationReportServiceInterface createReconciliationReportService(
			DfpSession session) {
		DfpServices dfpServices = new DfpServices();

		ReconciliationReportServiceInterface reconciliationReportService = dfpServices
				.get(session, ReconciliationReportServiceInterface.class);

		return reconciliationReportService;
	}

	public List<Long> getReconciliationReportIdsByStartDate(DfpSession session,
			String startDate) throws ReconciliationReportByIdException {

		try {
			ReconciliationReportServiceInterface reconciliationReportService = createReconciliationReportService(session);

			// Create statement to get the report for the specified date and
			// status.
			StatementBuilder statementBuilder = new StatementBuilder()
					.where("startDate = :startDate")
					.withBindVariableValue("startDate", startDate)
					.limit(StatementBuilder.SUGGESTED_PAGE_LIMIT);
			// Default for total result set size.
			int totalResultSetSize = 0;

			// List to store the report IDs returned by the query.
			List<Long> reconciliationReportIds = new ArrayList<Long>();

			do {
				ReconciliationReportPage page = reconciliationReportService
						.getReconciliationReportsByStatement(statementBuilder
								.toStatement());

				if (page.getResults() != null) {
					totalResultSetSize = page.getTotalResultSetSize();
					for (ReconciliationReport reconciliationReport : page
							.getResults()) {
						reconciliationReportIds.add(reconciliationReport
								.getId());
					}
				}

				statementBuilder
						.increaseOffsetBy(StatementBuilder.SUGGESTED_PAGE_LIMIT);
			} while (statementBuilder.getOffset() < totalResultSetSize);

			logger.info("Number of results found: " + totalResultSetSize + ".");
			return reconciliationReportIds;
		} catch (NullPointerException e) {
			throw new ReconciliationReportByIdException(e);
		} catch (ApiException e) {
			throw new ReconciliationReportByIdException(e);
		} catch (RemoteException e) {
			throw new ReconciliationReportByIdException(e);
		}
	}

}
