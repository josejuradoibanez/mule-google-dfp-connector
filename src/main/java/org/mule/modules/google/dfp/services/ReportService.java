/**
 * (c) 2003-2015 Ricston, Ltd. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 */

package org.mule.modules.google.dfp.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.List;

import org.apache.log4j.Logger;
import org.mule.modules.google.dfp.exceptions.CreateReportException;
import org.mule.modules.google.dfp.exceptions.ReportDownloadException;

import com.google.api.ads.dfp.axis.factory.DfpServices;
import com.google.api.ads.dfp.axis.utils.v201505.ReportDownloader;
import com.google.api.ads.dfp.axis.utils.v201505.StatementBuilder;
import com.google.api.ads.dfp.axis.v201505.ApiException;
import com.google.api.ads.dfp.axis.v201505.Column;
import com.google.api.ads.dfp.axis.v201505.Date;
import com.google.api.ads.dfp.axis.v201505.DateRangeType;
import com.google.api.ads.dfp.axis.v201505.Dimension;
import com.google.api.ads.dfp.axis.v201505.ExportFormat;
import com.google.api.ads.dfp.axis.v201505.NumberValue;
import com.google.api.ads.dfp.axis.v201505.ReportDownloadOptions;
import com.google.api.ads.dfp.axis.v201505.ReportJob;
import com.google.api.ads.dfp.axis.v201505.ReportQuery;
import com.google.api.ads.dfp.axis.v201505.ReportQueryAdUnitView;
import com.google.api.ads.dfp.axis.v201505.ReportServiceInterface;
import com.google.api.ads.dfp.axis.v201505.Statement;
import com.google.api.ads.dfp.axis.v201505.String_ValueMapEntry;
import com.google.api.ads.dfp.lib.client.DfpSession;
import com.google.api.client.repackaged.com.google.common.base.Joiner;
import com.google.common.io.Resources;

public class ReportService {

	private static final Logger logger = Logger.getLogger(ReportService.class);

	// Set the ID of the custom field to include in the report.
	private long[] customFieldsIds;

	// Dimensions to include in the report
	private static final Dimension[] dimensions = new Dimension[] {
			Dimension.PROPOSAL_LINE_ITEM_ID, Dimension.DATE,
			Dimension.PRODUCT_TEMPLATE_ID };

	// Columns to include in the report
	private static final Column[] columns = new Column[] {
			Column.CONTRACTED_REVENUE_LOCAL_CONTRACTED_AGENCY_COMMISSION,
			Column.CONTRACTED_REVENUE_LOCAL_CONTRACTED_GROSS_REVENUE,
			Column.CONTRACTED_REVENUE_LOCAL_CONTRACTED_NET_REVENUE,
			Column.SALES_CONTRACT_CONTRACTED_IMPRESSIONS,
			Column.SALES_CONTRACT_CONTRACTED_CLICKS,
			Column.CONTRACTED_REVENUE_LOCAL_CONTRACTED_VAT };

	// Dimension Attributes to include in the report
	// private static final DimensionAttribute[] dimensionAttributes = new
	// DimensionAttribute[] {
	// DimensionAttribute.ADVERTISER_EXTERNAL_ID,
	// DimensionAttribute.PROPOSAL_AGENCY_EXTERNAL_ID,
	// DimensionAttribute.PROPOSAL_CURRENCY,
	// DimensionAttribute.PROPOSAL_PRIMARY_SALESPERSON,
	// DimensionAttribute.PROPOSAL_START_DATE_TIME,
	// DimensionAttribute.PROPOSAL_END_DATE_TIME,
	// DimensionAttribute.PROPOSAL_PO_NUMBER,
	// DimensionAttribute.LINE_ITEM_START_DATE_TIME,
	// DimensionAttribute.LINE_ITEM_END_DATE_TIME,
	// DimensionAttribute.PROPOSAL_LINE_ITEM_START_DATE_TIME,
	// DimensionAttribute.PROPOSAL_LINE_ITEM_END_DATE_TIME, };

	protected ReportServiceInterface createReportService(DfpSession session) {
		DfpServices dfpServices = new DfpServices();

		// Get the ReportService.
		ReportServiceInterface reportService = dfpServices.get(session,
				ReportServiceInterface.class);

		return reportService;
	}

	public ReportJob createContractedProposalLineItemsReport(
			DfpSession session, Date startDateWithTimezone,
			Date endDateWithTimezone, List<Long> proposalLineIds)
			throws CreateReportException {

		logger.info("Creating a report");
		long[] proposalLineItemIds = null;

		if (proposalLineIds != null && !proposalLineIds.isEmpty()) {
			proposalLineItemIds = new long[proposalLineIds.size()];

			for (int i = 0; i < proposalLineIds.size(); i++) {
				proposalLineItemIds[i] = proposalLineIds.get(i);
			}
		}

//		Long[] testArray = new Long[2];
//		testArray[0] = (long) 463452;
//		testArray[1] = (long) 463452;

		String test = Joiner.on(", ").join(proposalLineIds);


		// long i = 441732;
		// long j = 441852;
		// String result = i.concat

		try {
			// Get the ReportService.
			ReportServiceInterface reportService = createReportService(session);

			// Create report query.
			ReportQuery reportQuery = new ReportQuery();
			reportQuery.setDimensions(dimensions);
			reportQuery.setAdUnitView(ReportQueryAdUnitView.TOP_LEVEL);
			reportQuery.setColumns(columns);
			// reportQuery.setDimensionAttributes(dimensionAttributes);
			reportQuery.setCustomFieldIds(customFieldsIds);

			// Create report date range
			reportQuery.setDateRangeType(DateRangeType.CUSTOM_DATE);

			reportQuery.setStartDate(startDateWithTimezone);
			reportQuery.setEndDate(endDateWithTimezone);
			
			String testing = "PROPOSAL_LINE_ITEM_ID IN (" + test + ")";

			StatementBuilder statementBuilder = new StatementBuilder().where(testing);

//			String_ValueMapEntry[] values = new String_ValueMapEntry[2];
//			values[0] = new String_ValueMapEntry("id",  );
//			
//			Statement statement = new Statement();
//			statement.setQuery("WHERE PROPOSAL_LINE_ITEM_ID = :id");
//			statement.setValues(values);

			reportQuery.setStatement(statementBuilder.toStatement());

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

			// Create report downloader.
			final ReportDownloader reportDownloader = new ReportDownloader(
					reportService, reportJob.getId());

			// Wait for the report to be ready.
			boolean success = reportDownloader.waitForReportReady();

			if (!success) {
				throw new ReportDownloadException();
			}

			ReportDownloadOptions options = new ReportDownloadOptions();
			options.setExportFormat(ExportFormat.CSV_DUMP);
			options.setUseGzipCompression(true);
			URL url = reportDownloader.getDownloadUrl(options);

			return Resources.asByteSource(url).openStream();

		} catch (ApiException e) {
			logger.debug("API Exception", e);
			throw new ReportDownloadException();
		} catch (RemoteException e) {
			logger.debug("Remote Exception", e);
			throw new ReportDownloadException();
		} catch (IOException e) {
			logger.debug("IO Exception", e);
			throw new ReportDownloadException();
		} catch (Exception e) {
			logger.debug("Exception", e);
			throw new ReportDownloadException();
		}
	}

	public long[] getCustomFieldsIds() {
		return customFieldsIds;
	}

	public void setCustomFieldsIds(long[] customFieldsIds) {
		this.customFieldsIds = customFieldsIds;
	}

}
