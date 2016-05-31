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
import com.google.api.ads.dfp.axis.utils.v201602.ReportDownloader;
import com.google.api.ads.dfp.axis.utils.v201602.StatementBuilder;
import com.google.api.ads.dfp.axis.v201602.ApiException;
import com.google.api.ads.dfp.axis.v201602.Column;
import com.google.api.ads.dfp.axis.v201602.Date;
import com.google.api.ads.dfp.axis.v201602.DateRangeType;
import com.google.api.ads.dfp.axis.v201602.Dimension;
import com.google.api.ads.dfp.axis.v201602.DimensionAttribute;
import com.google.api.ads.dfp.axis.v201602.ExportFormat;
import com.google.api.ads.dfp.axis.v201602.ReportDownloadOptions;
import com.google.api.ads.dfp.axis.v201602.ReportJob;
import com.google.api.ads.dfp.axis.v201602.ReportQuery;
import com.google.api.ads.dfp.axis.v201602.ReportQueryAdUnitView;
import com.google.api.ads.dfp.axis.v201602.ReportServiceInterface;
import com.google.api.ads.dfp.lib.client.DfpSession;
import com.google.api.client.repackaged.com.google.common.base.Joiner;
import com.google.common.io.Resources;

public class ReportService {

	private static final Logger logger = Logger.getLogger(ReportService.class);

	// Set the ID of the custom field to include in the report.
	private long[] customFieldsIds;

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

		logger.info("Creating the contracted Proposal Line Items report");

		// Dimensions to include in the report
		Dimension[] dimensions = new Dimension[] {
				Dimension.PROPOSAL_LINE_ITEM_ID, Dimension.DATE };

		DimensionAttribute[] dimensionAttributes = new DimensionAttribute[] { DimensionAttribute.PROPOSAL_LINE_ITEM_SIZE };

		// Columns to include in the report
		Column[] columns = new Column[] {
				Column.CONTRACTED_REVENUE_LOCAL_CONTRACTED_AGENCY_COMMISSION,
				Column.CONTRACTED_REVENUE_LOCAL_CONTRACTED_GROSS_REVENUE,
				Column.CONTRACTED_REVENUE_LOCAL_CONTRACTED_NET_REVENUE,
				Column.SALES_CONTRACT_CONTRACTED_IMPRESSIONS,
				Column.SALES_CONTRACT_CONTRACTED_CLICKS,
				Column.CONTRACTED_REVENUE_LOCAL_CONTRACTED_VAT };

		try {
			// Get the ReportService.
			ReportServiceInterface reportService = createReportService(session);

			// Create report query.
			ReportQuery reportQuery = new ReportQuery();
			reportQuery.setDimensions(dimensions);
			reportQuery.setDimensionAttributes(dimensionAttributes);
			reportQuery.setAdUnitView(ReportQueryAdUnitView.TOP_LEVEL);
			reportQuery.setColumns(columns);
			reportQuery.setCustomFieldIds(customFieldsIds);

			// Create report date range
			reportQuery.setDateRangeType(DateRangeType.CUSTOM_DATE);

			reportQuery.setStartDate(startDateWithTimezone);
			reportQuery.setEndDate(endDateWithTimezone);

			String whereClauseFilter = Joiner.on(", ").join(proposalLineIds);
			String queryStatement = "PROPOSAL_LINE_ITEM_ID IN ("
					+ whereClauseFilter + ")";

			StatementBuilder statementBuilder = new StatementBuilder()
					.where(queryStatement);

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

	public ReportJob createContractedProposalLineItemsReportWithAdUnits(
			DfpSession session, Date startDateWithTimezone,
			Date endDateWithTimezone, List<Long> proposalLineIds)
			throws CreateReportException {

		logger.info("Creating the contracted Proposal Line Items with Ad Units report");

		// Dimensions to include in the report
		Dimension[] dimensions = new Dimension[] {
				Dimension.PROPOSAL_LINE_ITEM_ID, Dimension.DATE,
				Dimension.AD_UNIT_ID, Dimension.AD_UNIT_NAME };

		// Columns to include in the report
		Column[] columns = new Column[] {
				Column.UNIFIED_REVENUE_LOCAL_UNIFIED_AGENCY_COMMISSION,
				Column.UNIFIED_REVENUE_LOCAL_UNIFIED_GROSS_REVENUE,
				Column.UNIFIED_REVENUE_LOCAL_UNIFIED_NET_REVENUE };

		try {
			// Get the ReportService.
			ReportServiceInterface reportService = createReportService(session);

			// Create report query.
			ReportQuery reportQuery = new ReportQuery();
			reportQuery.setDimensions(dimensions);
			reportQuery.setAdUnitView(ReportQueryAdUnitView.HIERARCHICAL);
			reportQuery.setColumns(columns);

			// Create report date range
			reportQuery.setDateRangeType(DateRangeType.CUSTOM_DATE);

			reportQuery.setStartDate(startDateWithTimezone);
			reportQuery.setEndDate(endDateWithTimezone);

			String whereClauseFilter = Joiner.on(", ").join(proposalLineIds);
			String queryStatement = "PROPOSAL_LINE_ITEM_ID IN ("
					+ whereClauseFilter + ")";

			StatementBuilder statementBuilder = new StatementBuilder()
					.where(queryStatement);

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

	public ReportJob createReachLifetimeReport(DfpSession session)
			throws CreateReportException {

		logger.info("Creating the reach lifetime report");

		// Dimensions to include in the report
		Dimension[] dimensions = new Dimension[] { Dimension.LINE_ITEM_ID };

		// Columns to include in the report
		Column[] columns = new Column[] { Column.REACH,
				Column.REACH_AVERAGE_REVENUE, Column.REACH_FREQUENCY };

		try {
			// Get the ReportService.
			ReportServiceInterface reportService = createReportService(session);

			// Create report query.
			ReportQuery reportQuery = new ReportQuery();
			reportQuery.setDimensions(dimensions);
			reportQuery.setAdUnitView(ReportQueryAdUnitView.TOP_LEVEL);
			reportQuery.setColumns(columns);

			// Create report date range
			reportQuery.setDateRangeType(DateRangeType.REACH_LIFETIME);

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

	public ReportJob createReachReport(DfpSession session,
			Date startDateWithTimezone, Date endDateWithTimezone)
			throws CreateReportException {

		logger.info("Creating the reach report");

		// Dimensions to include in the report
		Dimension[] dimensions = new Dimension[] { Dimension.LINE_ITEM_ID,
				Dimension.MONTH_AND_YEAR };

		// Columns to include in the report
		Column[] columns = new Column[] { Column.REACH,
				Column.REACH_AVERAGE_REVENUE, Column.REACH_FREQUENCY };

		try {
			// Get the ReportService.
			ReportServiceInterface reportService = createReportService(session);

			// Create report query.
			ReportQuery reportQuery = new ReportQuery();
			reportQuery.setDimensions(dimensions);
			reportQuery.setAdUnitView(ReportQueryAdUnitView.TOP_LEVEL);
			reportQuery.setColumns(columns);

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

	public ReportJob createActualsReport(DfpSession session,
			Date startDateWithTimezone, Date endDateWithTimezone,
			List<Long> lineItemIds) throws CreateReportException {

		logger.info("Creating the actuals report");

		// Dimensions to include in the report
		Dimension[] dimensions = new Dimension[] { Dimension.LINE_ITEM_ID,
				Dimension.DATE, Dimension.AD_UNIT_ID, Dimension.AD_UNIT_NAME };

		// Columns to include in the report
		Column[] columns = new Column[] {
				Column.TOTAL_LINE_ITEM_LEVEL_IMPRESSIONS,
				Column.TOTAL_LINE_ITEM_LEVEL_CLICKS,
				Column.TOTAL_LINE_ITEM_LEVEL_ALL_REVENUE,
				Column.TOTAL_LINE_ITEM_LEVEL_CTR,
				Column.UNIFIED_REVENUE_LOCAL_UNRECONCILED_NET_REVENUE,
				Column.UNIFIED_REVENUE_LOCAL_UNRECONCILED_GROSS_REVENUE,
				Column.UNIFIED_REVENUE_LOCAL_UNIFIED_AGENCY_COMMISSION,
				Column.UNIFIED_REVENUE_LOCAL_UNIFIED_GROSS_REVENUE,
				Column.UNIFIED_REVENUE_LOCAL_UNIFIED_NET_REVENUE,
				Column.UNIFIED_REVENUE_UNIFIED_NET_REVENUE,
				Column.UNIFIED_REVENUE_UNIFIED_AGENCY_COMMISSION,
				Column.UNIFIED_REVENUE_UNIFIED_GROSS_REVENUE };

		try {
			// Get the ReportService.
			ReportServiceInterface reportService = createReportService(session);

			// Create report query.
			ReportQuery reportQuery = new ReportQuery();
			reportQuery.setDimensions(dimensions);
			reportQuery.setAdUnitView(ReportQueryAdUnitView.HIERARCHICAL);
			reportQuery.setColumns(columns);

			// Create report date range
			reportQuery.setDateRangeType(DateRangeType.CUSTOM_DATE);

			reportQuery.setStartDate(startDateWithTimezone);
			reportQuery.setEndDate(endDateWithTimezone);

			String whereClauseFilter = Joiner.on(", ").join(lineItemIds);
			String queryStatement = "LINE_ITEM_ID IN (" + whereClauseFilter
					+ ")";

			StatementBuilder statementBuilder = new StatementBuilder()
					.where(queryStatement);

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

	public ReportJob createActualsReportWithoutAds(DfpSession session,
			Date startDateWithTimezone, Date endDateWithTimezone,
			List<Long> lineItemIds) throws CreateReportException {

		logger.info("Creating the actuals report");

		// Dimensions to include in the report
		Dimension[] dimensions = new Dimension[] { Dimension.LINE_ITEM_ID,
				Dimension.DATE };

		// Columns to include in the report
		Column[] columns = new Column[] {
				Column.TOTAL_LINE_ITEM_LEVEL_IMPRESSIONS,
				Column.TOTAL_LINE_ITEM_LEVEL_CLICKS,
				Column.TOTAL_LINE_ITEM_LEVEL_ALL_REVENUE,
				Column.TOTAL_LINE_ITEM_LEVEL_CTR,
				Column.UNIFIED_REVENUE_LOCAL_UNRECONCILED_NET_REVENUE,
				Column.UNIFIED_REVENUE_LOCAL_UNRECONCILED_GROSS_REVENUE,
				Column.UNIFIED_REVENUE_LOCAL_UNIFIED_AGENCY_COMMISSION,
				Column.UNIFIED_REVENUE_LOCAL_UNIFIED_GROSS_REVENUE,
				Column.UNIFIED_REVENUE_LOCAL_UNIFIED_NET_REVENUE,
				Column.UNIFIED_REVENUE_UNIFIED_NET_REVENUE,
				Column.UNIFIED_REVENUE_UNIFIED_AGENCY_COMMISSION,
				Column.UNIFIED_REVENUE_UNIFIED_GROSS_REVENUE };

		try {
			// Get the ReportService.
			ReportServiceInterface reportService = createReportService(session);

			// Create report query.
			ReportQuery reportQuery = new ReportQuery();
			reportQuery.setDimensions(dimensions);
			reportQuery.setAdUnitView(ReportQueryAdUnitView.HIERARCHICAL);
			reportQuery.setColumns(columns);

			// Create report date range
			reportQuery.setDateRangeType(DateRangeType.CUSTOM_DATE);

			reportQuery.setStartDate(startDateWithTimezone);
			reportQuery.setEndDate(endDateWithTimezone);

			String whereClauseFilter = Joiner.on(", ").join(lineItemIds);
			String queryStatement = "LINE_ITEM_ID IN (" + whereClauseFilter
					+ ")";

			StatementBuilder statementBuilder = new StatementBuilder()
					.where(queryStatement);

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

	public ReportJob activeLineItemsReport(DfpSession session,
			Date startDateWithTimezone, Date endDateWithTimezone)
			throws CreateReportException {

		logger.info("Creating the actuals report");

		// Dimensions to include in the report
		Dimension[] dimensions = new Dimension[] { Dimension.LINE_ITEM_ID,
				Dimension.DATE };

		// Columns to include in the report
		Column[] columns = new Column[] {
				Column.TOTAL_LINE_ITEM_LEVEL_IMPRESSIONS,
				Column.TOTAL_LINE_ITEM_LEVEL_CLICKS,
				Column.TOTAL_LINE_ITEM_LEVEL_ALL_REVENUE,
				Column.TOTAL_LINE_ITEM_LEVEL_CTR,
				Column.UNIFIED_REVENUE_LOCAL_UNRECONCILED_NET_REVENUE,
				Column.UNIFIED_REVENUE_LOCAL_UNRECONCILED_GROSS_REVENUE,
				Column.UNIFIED_REVENUE_LOCAL_UNIFIED_AGENCY_COMMISSION,
				Column.UNIFIED_REVENUE_LOCAL_UNIFIED_GROSS_REVENUE,
				Column.UNIFIED_REVENUE_LOCAL_UNIFIED_NET_REVENUE,
				Column.UNIFIED_REVENUE_UNIFIED_NET_REVENUE,
				Column.UNIFIED_REVENUE_UNIFIED_AGENCY_COMMISSION,
				Column.UNIFIED_REVENUE_UNIFIED_GROSS_REVENUE };

		try {
			// Get the ReportService.
			ReportServiceInterface reportService = createReportService(session);

			// Create report query.
			ReportQuery reportQuery = new ReportQuery();
			reportQuery.setDimensions(dimensions);
			reportQuery.setAdUnitView(ReportQueryAdUnitView.HIERARCHICAL);
			reportQuery.setColumns(columns);

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

	public ReportJob createAllActiveLineItemsReport(DfpSession session,
			Date startDateWithTimezone, Date endDateWithTimezone)
			throws CreateReportException {

		logger.info("Creating the ongoing line items report");

		// Dimensions to include in the report
		Dimension[] dimensions = new Dimension[] { Dimension.LINE_ITEM_ID,
				Dimension.PROPOSAL_LINE_ITEM_ID };

		// Columns to include in the report
		Column[] columns = new Column[] {
				Column.TOTAL_LINE_ITEM_LEVEL_IMPRESSIONS,
				Column.UNIFIED_REVENUE_LOCAL_UNIFIED_GROSS_REVENUE,
				Column.UNIFIED_REVENUE_LOCAL_UNIFIED_NET_REVENUE };

		try {
			// Get the ReportService.
			ReportServiceInterface reportService = createReportService(session);

			// Create report query.
			ReportQuery reportQuery = new ReportQuery();
			reportQuery.setDimensions(dimensions);
			reportQuery.setAdUnitView(ReportQueryAdUnitView.TOP_LEVEL);
			reportQuery.setColumns(columns);

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

	public ReportJob checkIfReportIsReady(DfpSession session,
			Date startDateWithTimezone, Date endDateWithTimezone)
			throws CreateReportException {

		logger.info("Checking if actuals reports are ready");

		// Dimensions to include in the report
		Dimension[] dimensions = new Dimension[] { Dimension.DATE,
				Dimension.HOUR };

		// Columns to include in the report
		Column[] columns = new Column[] { Column.TOTAL_INVENTORY_LEVEL_IMPRESSIONS };

		try {
			// Get the ReportService.
			ReportServiceInterface reportService = createReportService(session);

			// Create report query.
			ReportQuery reportQuery = new ReportQuery();
			reportQuery.setDimensions(dimensions);
			reportQuery.setAdUnitView(ReportQueryAdUnitView.TOP_LEVEL);
			reportQuery.setColumns(columns);

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

	public ReportJob ageAndGenderReport(DfpSession session,
			Date startDateWithTimezone, Date endDateWithTimezone,
			List<Integer> lineItems) throws CreateReportException {

		logger.info("Creating the age and gender report");

		// Dimensions to include in the report
		Dimension[] dimensions = new Dimension[] { Dimension.CUSTOM_CRITERIA,
				Dimension.DATE, Dimension.LINE_ITEM_ID, Dimension.PROPOSAL_ID };

		// Columns to include in the report
		Column[] columns = new Column[] {
				Column.TOTAL_LINE_ITEM_LEVEL_IMPRESSIONS,
				Column.TOTAL_LINE_ITEM_LEVEL_CLICKS };

		try {
			// Get the ReportService.
			ReportServiceInterface reportService = createReportService(session);

			// Create report query.
			ReportQuery reportQuery = new ReportQuery();
			reportQuery.setDimensions(dimensions);
			reportQuery.setColumns(columns);

			// Create report date range
			reportQuery.setDateRangeType(DateRangeType.CUSTOM_DATE);

			reportQuery.setStartDate(startDateWithTimezone);
			reportQuery.setEndDate(endDateWithTimezone);

			String whereClauseFilter = Joiner.on(", ").join(lineItems);
			String queryStatement = "LINE_ITEM_ID IN (" + whereClauseFilter
					+ ")";
			StatementBuilder statementBuilder = new StatementBuilder()
					.where(queryStatement);

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

	public ReportJob totalContractedImpressions(DfpSession session,
			Date startDateWithTimezone, Date endDateWithTimezone)
			throws CreateReportException {

		logger.info("Creating total contracted impressions report");

		// Dimensions to include in the report
		Dimension[] dimensions = new Dimension[] {
				Dimension.PROPOSAL_LINE_ITEM_ID, Dimension.MONTH_AND_YEAR };

		DimensionAttribute[] dimensionAttributes = new DimensionAttribute[] {
				DimensionAttribute.PROPOSAL_LINE_ITEM_START_DATE_TIME,
				DimensionAttribute.PROPOSAL_LINE_ITEM_END_DATE_TIME };

		// Columns to include in the report
		Column[] columns = new Column[] {
				Column.CONTRACTED_REVENUE_LOCAL_CONTRACTED_GROSS_REVENUE,
				Column.SALES_CONTRACT_CONTRACTED_IMPRESSIONS };

		try {
			// Get the ReportService.
			ReportServiceInterface reportService = createReportService(session);

			// Create report query.
			ReportQuery reportQuery = new ReportQuery();
			reportQuery.setDimensions(dimensions);
			reportQuery.setDimensionAttributes(dimensionAttributes);
			reportQuery.setAdUnitView(ReportQueryAdUnitView.TOP_LEVEL);
			reportQuery.setColumns(columns);

			// Create report date range
			reportQuery.setDateRangeType(DateRangeType.CUSTOM_DATE);

			reportQuery.setStartDate(startDateWithTimezone);
			reportQuery.setEndDate(endDateWithTimezone);

			StatementBuilder statementBuilder = new StatementBuilder();

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

	public ReportJob totalDeliveredImpressions(DfpSession session,
			Date startDateWithTimezone, Date endDateWithTimezone)
			throws CreateReportException {

		logger.info("Creating total delivered impressons report");

		// Dimensions to include in the report
		Dimension[] dimensions = new Dimension[] { Dimension.LINE_ITEM_ID,
				Dimension.PROPOSAL_LINE_ITEM_ID, Dimension.MONTH_AND_YEAR };

		DimensionAttribute[] dimensionAttributes = new DimensionAttribute[] {
				DimensionAttribute.LINE_ITEM_START_DATE_TIME,
				DimensionAttribute.LINE_ITEM_END_DATE_TIME };

		// Columns to include in the report
		Column[] columns = new Column[] {
				Column.TOTAL_LINE_ITEM_LEVEL_IMPRESSIONS,
				Column.UNIFIED_REVENUE_LOCAL_UNIFIED_GROSS_REVENUE };

		try {
			// Get the ReportService.
			ReportServiceInterface reportService = createReportService(session);

			// Create report query.
			ReportQuery reportQuery = new ReportQuery();
			reportQuery.setDimensions(dimensions);
			reportQuery.setDimensionAttributes(dimensionAttributes);
			reportQuery.setAdUnitView(ReportQueryAdUnitView.TOP_LEVEL);
			reportQuery.setColumns(columns);

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

	public ReportJob createTargetingReport(DfpSession session,
			Date startDateWithTimezone, Date endDateWithTimezone)
			throws CreateReportException {

		logger.info("Creating Targeting report");

		// Dimensions to include in the report
		Dimension[] dimensions = new Dimension[] { Dimension.LINE_ITEM_ID,
				Dimension.TARGETING, Dimension.MONTH_AND_YEAR };

		Column[] columns = new Column[] {
				Column.TOTAL_LINE_ITEM_LEVEL_IMPRESSIONS,
				Column.TOTAL_LINE_ITEM_LEVEL_CLICKS,
				Column.TOTAL_LINE_ITEM_LEVEL_CPM_AND_CPC_REVENUE };

		try {
			// Get the ReportService.
			ReportServiceInterface reportService = createReportService(session);

			// Create report query.
			ReportQuery reportQuery = new ReportQuery();
			reportQuery.setDimensions(dimensions);
			reportQuery.setAdUnitView(ReportQueryAdUnitView.TOP_LEVEL);
			reportQuery.setColumns(columns);

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

	public ReportJob createAudienceReport(DfpSession session,
			Date startDateWithTimezone, Date endDateWithTimezone)
			throws CreateReportException {

		logger.info("Creating Audience report");

		// Dimensions to include in the report
		Dimension[] dimensions = new Dimension[] { Dimension.LINE_ITEM_ID,
				Dimension.AUDIENCE_SEGMENT_ID, Dimension.MONTH_AND_YEAR,
				Dimension.AD_UNIT_ID, Dimension.AD_UNIT_NAME };

		// Columns to include in the report
		Column[] columns = new Column[] { Column.AD_SERVER_IMPRESSIONS,
				Column.TOTAL_LINE_ITEM_LEVEL_CLICKS,
				Column.TOTAL_LINE_ITEM_LEVEL_CPM_AND_CPC_REVENUE };

		try {
			// Get the ReportService.
			ReportServiceInterface reportService = createReportService(session);

			// Create report query.
			ReportQuery reportQuery = new ReportQuery();
			reportQuery.setDimensions(dimensions);
			// reportQuery.setDimensionAttributes(dimensionAttributes);
			reportQuery.setAdUnitView(ReportQueryAdUnitView.TOP_LEVEL);
			reportQuery.setColumns(columns);

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

			// Create report downloader.
			final ReportDownloader reportDownloader = new ReportDownloader(
					reportService, reportJob.getId());

			// Wait for the report to be ready.
			boolean success = reportDownloader.waitForReportReady();

			if (!success) {
				throw new ReportDownloadException(
						new Throwable(
								"Cannot download report. Google DFP failed to return the report."));
			}

			ReportDownloadOptions options = new ReportDownloadOptions();
			options.setExportFormat(ExportFormat.CSV_DUMP);
			options.setUseGzipCompression(true);
			URL url = reportDownloader.getDownloadUrl(options);

			return Resources.asByteSource(url).openStream();

		} catch (ApiException e) {
			logger.error("API Exception", e);
			throw new ReportDownloadException(e);
		} catch (RemoteException e) {
			logger.error("Remote Exception", e);
			throw new ReportDownloadException(e);
		} catch (IOException e) {
			logger.error("IO Exception", e);
			throw new ReportDownloadException(e);
		} catch (Exception e) {
			logger.error("Exception", e);
			throw new ReportDownloadException(e);
		}
	}

	public long[] getCustomFieldsIds() {
		return customFieldsIds;
	}

	public void setCustomFieldsIds(long[] customFieldsIds) {
		this.customFieldsIds = customFieldsIds;
	}

}
