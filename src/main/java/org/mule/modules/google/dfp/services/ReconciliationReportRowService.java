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
import org.mule.modules.google.dfp.exceptions.ReconciliationReportRowException;
import org.mule.modules.google.dfp.reconciliationreport.ReconciliationQueryParams;

import com.google.api.ads.dfp.axis.factory.DfpServices;
import com.google.api.ads.dfp.axis.utils.v201411.StatementBuilder;
import com.google.api.ads.dfp.axis.v201411.ApiException;
import com.google.api.ads.dfp.axis.v201411.ReconciliationReportRow;
import com.google.api.ads.dfp.axis.v201411.ReconciliationReportRowPage;
import com.google.api.ads.dfp.axis.v201411.ReconciliationReportRowServiceInterface;
import com.google.api.ads.dfp.lib.client.DfpSession;

public class ReconciliationReportRowService {

	private Log logger = LogFactory.getLog(getClass());

	protected ReconciliationReportRowServiceInterface createReconciliationReportRowService(
			DfpSession session) {
		DfpServices dfpServices = new DfpServices();

		ReconciliationReportRowServiceInterface reconciliationReportService = dfpServices
				.get(session, ReconciliationReportRowServiceInterface.class);

		return reconciliationReportService;
	}

	public List<ReconciliationReportRow> getReconciliationReportRows(
			DfpSession session, ReconciliationQueryParams queryParams) throws ReconciliationReportRowException {
		try {
			ReconciliationReportRowServiceInterface reconciliationReportRowService = createReconciliationReportRowService(session);

			// Create a statement to get all reconciliation reports.
			StatementBuilder statementBuilder = new StatementBuilder()
					.where("orderId = :orderId AND lineItemid = :lineItemId AND reconciliationReportId = :reconciliationReportId")
					.withBindVariableValue("orderId", queryParams.getOrderId())
					.withBindVariableValue("lineItemId", queryParams.getLineItemId())
					.withBindVariableValue("reconciliationReportId",
							queryParams.getReconciliationReportId());

			// Default for total result set size.
			int totalResultSetSize = 0;

			// List to store the comments found for the lineItemId.
			List<ReconciliationReportRow> reconciliationRows = new ArrayList<ReconciliationReportRow>();

			do {
				ReconciliationReportRowPage page = reconciliationReportRowService
						.getReconciliationReportRowsByStatement(statementBuilder
								.toStatement());

				if (page.getResults() != null) {
					totalResultSetSize = page.getTotalResultSetSize();
					for (ReconciliationReportRow reconciliationReportRow : page
							.getResults()) {
						reconciliationRows.add(reconciliationReportRow);

						logger.info("Order ID: "
								+ reconciliationReportRow.getOrderId()
								+ ", Line item ID: "
								+ reconciliationReportRow.getLineItemId()
								+ ", and Reconciliation Report ID"
								+ reconciliationReportRow
										.getReconciliationReportId()
								+ " was found. The following comment was found for this record: "
								+ reconciliationReportRow.getComments() + ".");
					}
				}

				statementBuilder
						.increaseOffsetBy(StatementBuilder.SUGGESTED_PAGE_LIMIT);
			} while (statementBuilder.getOffset() < totalResultSetSize);

			return reconciliationRows;
		} catch (ApiException e) {
			throw new ReconciliationReportRowException(e);
		} catch (RemoteException e) {
			throw new ReconciliationReportRowException(e);
		}catch(Exception e){
			throw new ReconciliationReportRowException(e);
		}
	}

}
