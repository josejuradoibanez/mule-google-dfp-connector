/**
 * (c) 2003-2015 Ricston, Ltd. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 */

package org.mule.modules.google.dfp;

import java.io.InputStream;
import java.util.List;

import javax.annotation.PostConstruct;

import org.mule.api.annotations.Configurable;
import org.mule.api.annotations.ConnectionStrategy;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.lifecycle.Start;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.Optional;
import org.mule.modules.google.dfp.exceptions.CreateFailedException;
import org.mule.modules.google.dfp.exceptions.CreateReportException;
import org.mule.modules.google.dfp.exceptions.GetAdvertiserByNameException;
import org.mule.modules.google.dfp.exceptions.GetAgencyByNameException;
import org.mule.modules.google.dfp.exceptions.GetAllCompaniesException;
import org.mule.modules.google.dfp.exceptions.GetCompanyByIdException;
import org.mule.modules.google.dfp.exceptions.ReconciliationReportByIdException;
import org.mule.modules.google.dfp.exceptions.ReconciliationReportRowException;
import org.mule.modules.google.dfp.exceptions.ReportDownloadException;
import org.mule.modules.google.dfp.exceptions.TooManyAdvertisersFoundException;
import org.mule.modules.google.dfp.exceptions.TooManyAgenciesFoundException;
import org.mule.modules.google.dfp.exceptions.UpdateFailedException;
import org.mule.modules.google.dfp.reconciliationreport.ReconciliationQueryParams;
import org.mule.modules.google.dfp.services.CompanyService;
import org.mule.modules.google.dfp.services.ReconciliationReportRowService;
import org.mule.modules.google.dfp.services.ReconciliationReportService;
import org.mule.modules.google.dfp.services.ReportService;
import org.mule.modules.google.dfp.strategy.GoogleDfpConnectionStrategy;

import com.google.api.ads.dfp.axis.v201411.Company;
import com.google.api.ads.dfp.axis.v201411.Date;
import com.google.api.ads.dfp.axis.v201411.ReconciliationReportRow;
import com.google.api.ads.dfp.axis.v201411.ReportJob;
import com.google.api.ads.dfp.lib.client.DfpSession;

/**
 * Google DFP Connector
 *
 * {@sample.config ../../../doc/google-dfp-connector.xml.sample
 * google-dfp:config}
 * 
 * @author Ricston, Ltd.
 */
@Connector(name = "google-dfp", schemaVersion = "1.0", friendlyName = "GoogleDfp")
public class GoogleDfpConnector {

	@ConnectionStrategy
	GoogleDfpConnectionStrategy connectionStrategy;

	private DfpSession session;
	private ReportService reportService;
	private CompanyService companyService;
	private ReconciliationReportService reconciliationReportService;
	private ReconciliationReportRowService reconciliationReportRowService;

	/**
	 * List of custom IDs
	 */
	@Configurable
	@Optional
	private List<String> customIds;

	@PostConstruct
	public void init() {
		session = connectionStrategy.getSession();
	}

	/**
	 * Initialize all the Google DFP services. Once initialize, each service is
	 * configured if necessary.
	 */
	@Start
	public void initialiseAndConfigureServices() {

		// Initialize and configure Report Service
		reportService = new ReportService();

		if (customIds != null && !customIds.isEmpty()) {
			long[] customFieldIds = new long[customIds.size()];

			for (int i = 0; i < customIds.size(); i++) {
				customFieldIds[i] = Long.parseLong(customIds.get(i));
			}

			reportService.setCustomFieldsIds(customFieldIds);
		}

		// Initialize and configure Company Service
		companyService = new CompanyService();

		// Initialize and configure reconciliation report service
		reconciliationReportService = new ReconciliationReportService();

		// Initialize and configure reconciliation report row service
		reconciliationReportRowService = new ReconciliationReportRowService();
	}

	/**
	 * Creates a report given a date range
	 * 
	 * {@sample.xml ../../../doc/google-dfp-connector.xml.sample
	 * google-dfp:create-report}
	 * 
	 * @param startDate
	 *            the start of the report date
	 * @param endDate
	 *            the end of the report date
	 * @return The report job
	 * @throws CreateReportException
	 *             Create Report Exception
	 * 
	 */
	@Processor
	public ReportJob createReport(Date startDate, Date endDate)
			throws CreateReportException {
		return reportService.createReport(session, startDate, endDate);
	}

	/**
	 * Download a report from the Google DFP services
	 * 
	 * {@sample.xml ../../../doc/google-dfp-connector.xml.sample
	 * google-dfp:download-report}
	 * 
	 * @param reportJob
	 *            Content to be processed
	 * @return Input Stream containing a gz version of the CSV dump of the
	 *         report
	 * @throws ReportDownloadException
	 *             Report Download Exception
	 */
	@Processor
	public InputStream downloadReport(@Default("#[payload]") ReportJob reportJob)
			throws ReportDownloadException {
		return reportService.downloadReport(session, reportJob);
	}

	/**
	 * Retrieve all companies
	 * 
	 * {@sample.xml ../../../doc/google-dfp-connector.xml.sample
	 * google-dfp:get-all-companies}
	 * 
	 * @return All companies
	 * @throws GetAllCompaniesException
	 *             Get All Companies Exception
	 */
	@Processor
	public List<Company> getAllCompanies() throws GetAllCompaniesException {
		return companyService.getAllCompanies(session);
	}

	/**
	 * Retrieve the agency ID by name
	 * 
	 * {@sample.xml ../../../doc/google-dfp-connector.xml.sample
	 * google-dfp:get-agency-by-name}
	 * 
	 * @param agencyName
	 *            The agency name to retrieve
	 * @return The company found
	 * @throws GetAgencyByNameException
	 *             Get Agency exception
	 * @throws TooManyAgenciesFoundException
	 *             Too many agencies found exception
	 */
	@Processor
	public Company getAgencyByName(String agencyName)
			throws GetAgencyByNameException, TooManyAgenciesFoundException {
		return companyService.getAgencyByName(session, agencyName);
	}

	/**
	 * Retrieve the advertiser ID by name
	 * 
	 * {@sample.xml ../../../doc/google-dfp-connector.xml.sample
	 * google-dfp:get-advertiser-by-name}
	 * 
	 * @param advertiserName
	 *            The advertiser name to retrieve
	 * @return The advertiser
	 * @throws GetAdvertiserByNameException
	 *             Get advertiser by name exception
	 * @throws TooManyAdvertisersFoundException
	 *             Too many advertisers found exception
	 */
	@Processor
	public String getAdvertiserByName(String advertiserName)
			throws GetAdvertiserByNameException,
			TooManyAdvertisersFoundException {
		return companyService.getAdvertiserByName(session, advertiserName);
	}

	/**
	 * Retrieve the reconciliation report IDs given the start date
	 * 
	 * {@sample.xml ../../../doc/google-dfp-connector.xml.sample
	 * google-dfp:get-reconciliation-report-ids-by-start-date}
	 * 
	 * @param startDate
	 *            Start date used for searching reconciliation reports
	 * @return List of reconciliation report ids
	 * @throws ReconciliationReportByIdException
	 *             Reconciliation Report By ID Exception
	 */
	@Processor
	public List<Long> getReconciliationReportIdsByStartDate(
			@Default("#[payload]") Date startDate)
			throws ReconciliationReportByIdException {
		try {
			String dateFormat = "%04d-%02d-%02d";
			String stringDate = String.format(dateFormat, startDate.getYear(),
					startDate.getMonth(), startDate.getDay());
			return reconciliationReportService
					.getReconciliationReportIdsByStartDate(session, stringDate);
		} catch (Exception e) {
			throw new ReconciliationReportByIdException(e);
		}

	}

	/**
	 * Retrieve Reconciliation report rows given the reconciliation report ID,
	 * order ID and line item ID
	 * 
	 * {@sample.xml ../../../doc/google-dfp-connector.xml.sample
	 * google-dfp:get-reconciliation-report-rows}
	 * 
	 * @param queryParams
	 *            query parameters to get reconciliation report row
	 * @return List of reconcialtion report rows
	 * @throws ReconciliationReportRowException
	 *             Reconciliation Report Row Exception
	 */
	@Processor
	public List<ReconciliationReportRow> getReconciliationReportRows(
			@Default("#[payload]") ReconciliationQueryParams queryParams)
			throws ReconciliationReportRowException {
		try {
			return reconciliationReportRowService.getReconciliationReportRows(
					session, queryParams);
		} catch (Exception e) {
			throw new ReconciliationReportRowException(e);
		}
	}

	/**
	 * Retrieve the company by ID. Null is returned if the company is not found
	 * 
	 * {@sample.xml ../../../doc/google-dfp-connector.xml.sample
	 * google-dfp:get-company-by-id}
	 * 
	 * @param companyId
	 *            The company ID
	 * @return The company
	 * @throws GetCompanyByIdException
	 *             Get Company By ID Exception
	 */
	@Processor
	public Company getCompanyById(Long companyId)
			throws GetCompanyByIdException {
		return companyService.getCompanyById(session, companyId);
	}

	/**
	 * Create a company by supplying a company object
	 * 
	 * {@sample.xml ../../../doc/google-dfp-connector.xml.sample
	 * google-dfp:create-company}
	 * 
	 * @param company
	 *            The company to create
	 * @return The created company
	 * @throws CreateFailedException
	 *             Create Failed Exception
	 */
	@Processor
	public Company createCompany(@Default("#[payload]") Company company)
			throws CreateFailedException {
		return companyService.createCompany(session, company);
	}

	/**
	 * Update company by supplying a company object
	 * 
	 * {@sample.xml ../../../doc/google-dfp-connector.xml.sample
	 * google-dfp:update-company}
	 * 
	 * @param company
	 *            The company to update
	 * @return The updated company
	 * @throws UpdateFailedException
	 *             Update Failed Exception
	 */
	@Processor
	public Company updateCompany(@Default("#[payload]") Company company)
			throws UpdateFailedException {
		return companyService.updateCompany(session, company);
	}

	/**
	 * @return connection strategy
	 */
	public GoogleDfpConnectionStrategy getConnectionStrategy() {
		return connectionStrategy;
	}

	/**
	 * @param connectionStrategy
	 *            Google DFP connection strategy
	 */
	public void setConnectionStrategy(
			GoogleDfpConnectionStrategy connectionStrategy) {
		this.connectionStrategy = connectionStrategy;
	}

	/**
	 * @return list of custom IDs
	 */
	public List<String> getCustomIds() {
		return customIds;
	}

	/**
	 * @param customIds
	 *            list of custom IDs
	 */
	public void setCustomIds(List<String> customIds) {
		this.customIds = customIds;
	}

}