/**
 * (c) 2003-2015 Ricston, Ltd. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 */

package org.mule.modules.google.dfp.services;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.mule.modules.google.dfp.exceptions.ReconciliationReportException;

import com.google.api.ads.dfp.axis.factory.DfpServices;
import com.google.api.ads.dfp.axis.utils.v201505.StatementBuilder;
import com.google.api.ads.dfp.axis.v201505.ApiException;
import com.google.api.ads.dfp.axis.v201505.ReconciliationReport;
import com.google.api.ads.dfp.axis.v201505.ReconciliationReportPage;
import com.google.api.ads.dfp.axis.v201505.ReconciliationReportServiceInterface;
import com.google.api.ads.dfp.lib.client.DfpSession;

public class ReconciliationReportService {

	private static final Logger logger = Logger
			.getLogger(ReconciliationReportService.class);

	protected ReconciliationReportServiceInterface createReconciliationReportService(
			DfpSession session) {
		DfpServices dfpServices = new DfpServices();

		ReconciliationReportServiceInterface reconciliationReportService = dfpServices
				.get(session, ReconciliationReportServiceInterface.class);

		return reconciliationReportService;
	}

	public List<ReconciliationReport> getReconciliationReportByStartDate(
			DfpSession session, String startDate)
			throws ReconciliationReportException {

		try {
			ReconciliationReportServiceInterface reconciliationReportService = createReconciliationReportService(session);

			StatementBuilder statementBuilder = new StatementBuilder()
					.where("startDate = :startDate")
					.withBindVariableValue("startDate", startDate)
					.limit(StatementBuilder.SUGGESTED_PAGE_LIMIT);

			int totalResultSetSize = 0;

			// List to store the report returned by the query.
			List<ReconciliationReport> reconciliationReportList = new ArrayList<ReconciliationReport>();

			do {
				ReconciliationReportPage page = reconciliationReportService
						.getReconciliationReportsByStatement(statementBuilder
								.toStatement());

				if (page.getResults() != null) {
					totalResultSetSize = page.getTotalResultSetSize();
					for (ReconciliationReport reconciliationReport : page
							.getResults()) {
						reconciliationReportList.add(reconciliationReport);
					}
				}

				statementBuilder
						.increaseOffsetBy(StatementBuilder.SUGGESTED_PAGE_LIMIT);
			} while (statementBuilder.getOffset() < totalResultSetSize);

			logger.info("Number of Reconciliation Reports found: " + totalResultSetSize + ".");
			return reconciliationReportList;
			
		} catch (NullPointerException e) {
			throw new ReconciliationReportException(e);
		} catch (ApiException e) {
			throw new ReconciliationReportException(e);
		} catch (RemoteException e) {
			throw new ReconciliationReportException(e);
		}
	}

}
