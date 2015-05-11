/**
 * (c) 2003-2015 Ricston, Ltd. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 */

package org.mule.modules.google.dfp.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.rmi.RemoteException;

import org.apache.log4j.Logger;
import org.mule.modules.google.dfp.exceptions.CreateReportException;
import org.mule.modules.google.dfp.exceptions.ReportDownloadException;

import com.google.api.ads.dfp.axis.factory.DfpServices;
import com.google.api.ads.dfp.axis.utils.v201411.ReportDownloader;
import com.google.api.ads.dfp.axis.v201411.ApiException;
import com.google.api.ads.dfp.axis.v201411.Column;
import com.google.api.ads.dfp.axis.v201411.Date;
import com.google.api.ads.dfp.axis.v201411.DateRangeType;
import com.google.api.ads.dfp.axis.v201411.Dimension;
import com.google.api.ads.dfp.axis.v201411.DimensionAttribute;
import com.google.api.ads.dfp.axis.v201411.ExportFormat;
import com.google.api.ads.dfp.axis.v201411.ReportJob;
import com.google.api.ads.dfp.axis.v201411.ReportQuery;
import com.google.api.ads.dfp.axis.v201411.ReportQueryAdUnitView;
import com.google.api.ads.dfp.axis.v201411.ReportServiceInterface;
import com.google.api.ads.dfp.lib.client.DfpSession;
import com.google.api.ads.dfp.lib.utils.ReportCallback;

public class ReportService {

	private static final Logger logger = Logger.getLogger(ReportService.class);

	// Set the ID of the custom field to include in the report.
	private long[] customFieldsIds;

	// Dimensions to include in the report
	private static final Dimension[] dimensions = new Dimension[] {
			Dimension.PROPOSAL_ID, Dimension.ORDER_ID, Dimension.LINE_ITEM_ID,
			Dimension.PROPOSAL_LINE_ITEM_ID, Dimension.ADVERTISER_NAME,
			Dimension.PROPOSAL_NAME, Dimension.PROPOSAL_AGENCY_NAME,
			Dimension.MONTH_AND_YEAR, Dimension.RATE_CARD_ID,
			Dimension.RATE_CARD_NAME, Dimension.PROPOSAL_LINE_ITEM_NAME };

	// Columns to include in the report
	private static final Column[] columns = new Column[] {
			Column.BILLING_LOCAL_BILLABLE_NET_REVENUE,
			Column.RECONCILIATION_RECONCILED_REVENUE,
			Column.RECONCILIATION_LAST_DATE_TIME,
			Column.RECONCILIATION_RECONCILIATION_STATUS, };

	// Dimension Attributes to include in the report
	private static final DimensionAttribute[] dimensionAttributes = new DimensionAttribute[] {
			DimensionAttribute.ADVERTISER_EXTERNAL_ID,
			DimensionAttribute.PROPOSAL_AGENCY_EXTERNAL_ID,
			DimensionAttribute.PROPOSAL_CURRENCY,
			DimensionAttribute.PROPOSAL_PRIMARY_SALESPERSON,
			DimensionAttribute.PROPOSAL_START_DATE_TIME,
			DimensionAttribute.PROPOSAL_END_DATE_TIME,
			DimensionAttribute.PROPOSAL_PO_NUMBER,
			DimensionAttribute.LINE_ITEM_START_DATE_TIME,
			DimensionAttribute.LINE_ITEM_END_DATE_TIME,
			DimensionAttribute.PROPOSAL_LINE_ITEM_START_DATE_TIME,
			DimensionAttribute.PROPOSAL_LINE_ITEM_END_DATE_TIME, };

	protected ReportServiceInterface createReportService(DfpSession session) {
		DfpServices dfpServices = new DfpServices();

		// Get the ReportService.
		ReportServiceInterface reportService = dfpServices.get(session,
				ReportServiceInterface.class);

		return reportService;
	}

	public ReportJob createReport(DfpSession session,
			Date startDateWithTimezone, Date endDateWithTimezone)
			throws CreateReportException {

		logger.info("Creating a report");

		try {
			// Get the ReportService.
			ReportServiceInterface reportService = createReportService(session);

			// Create report query.
			ReportQuery reportQuery = new ReportQuery();
			reportQuery.setDimensions(dimensions);
			reportQuery.setAdUnitView(ReportQueryAdUnitView.TOP_LEVEL);
			reportQuery.setColumns(columns);
			reportQuery.setDimensionAttributes(dimensionAttributes);
			reportQuery.setCustomFieldIds(customFieldsIds);

			// Create report date range
			reportQuery.setDateRangeType(DateRangeType.CUSTOM_DATE);

			reportQuery.setStartDate(startDateWithTimezone);
			reportQuery.setEndDate(endDateWithTimezone);

			// Create report job.
			ReportJob reportJob = new ReportJob();
			reportJob.setReportQuery(reportQuery);

			// Run report job.
			return reportService.runReportJob(reportJob);

		} catch (ApiException e) {
			throw new CreateReportException(e);
		} catch (RemoteException e) {
			throw new CreateReportException(e);
		} catch (Exception e) {
			throw new CreateReportException(e);
		}

	}

	public InputStream downloadReport(DfpSession session, ReportJob reportJob)
			throws ReportDownloadException {
		try {
			// Get the ReportService.
			ReportServiceInterface reportService = createReportService(session);

			final PipedOutputStream pipedOutputStream = new PipedOutputStream();
			PipedInputStream pipedInputStream = new PipedInputStream(
					pipedOutputStream);

			// Create report downloader.
			final ReportDownloader reportDownloader = new ReportDownloader(
					reportService, reportJob.getId());

			// Wait for the report to be ready.
			boolean success = reportDownloader.waitForReportReady();

			if (!success) {
				throw new ReportDownloadException();
			}

			createDownloaderThread(reportDownloader, pipedOutputStream);

			return pipedInputStream;
		} catch (ApiException e) {
			logger.debug("API Exception", e);
			throw new ReportDownloadException();
		} catch (RemoteException e) {
			logger.debug("Remote Exception", e);
			throw new ReportDownloadException();
		} catch (InterruptedException e) {
			logger.debug("Interrupted Exception", e);
			throw new ReportDownloadException();
		} catch (IOException e) {
			logger.debug("IO Exception", e);
			throw new ReportDownloadException();
		} catch (Exception e) {
			logger.debug("Exception", e);
			throw new ReportDownloadException();
		}
	}

	private void createDownloaderThread(
			final ReportDownloader reportDownloader,
			final PipedOutputStream pipedOutputStream) {
		reportDownloader.whenReportReady(new ReportCallback() {
			public void onSuccess() {
				try {

					logger.info("Downloading report");
					// Download the report.
					reportDownloader.downloadReport(ExportFormat.CSV_DUMP,
							pipedOutputStream);
					logger.info("Downloading report complete");
				} catch (IOException e) {
					logger.error("Report download failed." , e);
				}
			}

			public void onInterruption() {
				logger.error("Report download interupted.");
			}

			public void onFailure() {
				logger.error("Report download failed.");
			}

			public void onException(Exception e) {
				logger.error("Report download failed.");
			}
		});

	}

	public long[] getCustomFieldsIds() {
		return customFieldsIds;
	}

	public void setCustomFieldsIds(long[] customFieldsIds) {
		this.customFieldsIds = customFieldsIds;
	}

}
