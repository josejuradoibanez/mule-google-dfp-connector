/**
 * (c) 2003-2015 Ricston, Ltd. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 */

package org.mule.modules.google.dfp;

import java.io.InputStream;
import java.util.List;

import org.mule.api.annotations.Config;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.MetaDataScope;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.param.Default;
import org.mule.modules.google.dfp.exceptions.CreateFailedException;
import org.mule.modules.google.dfp.exceptions.CreateReportException;
import org.mule.modules.google.dfp.exceptions.GetAdvertiserByNameException;
import org.mule.modules.google.dfp.exceptions.GetAgencyByNameException;
import org.mule.modules.google.dfp.exceptions.GetAllCompaniesException;
import org.mule.modules.google.dfp.exceptions.GetAllContactsException;
import org.mule.modules.google.dfp.exceptions.GetCompanyByIdException;
import org.mule.modules.google.dfp.exceptions.GetCustomFieldsException;
import org.mule.modules.google.dfp.exceptions.GetLineItemsException;
import org.mule.modules.google.dfp.exceptions.GetOrdersException;
import org.mule.modules.google.dfp.exceptions.GetProductTemplatesException;
import org.mule.modules.google.dfp.exceptions.GetProductsByStatementException;
import org.mule.modules.google.dfp.exceptions.GetProposalLineItemsException;
import org.mule.modules.google.dfp.exceptions.GetProposalsException;
import org.mule.modules.google.dfp.exceptions.GetUsersException;
import org.mule.modules.google.dfp.exceptions.ReconciliationReportException;
import org.mule.modules.google.dfp.exceptions.ReconciliationReportRowException;
import org.mule.modules.google.dfp.exceptions.ReportDownloadException;
import org.mule.modules.google.dfp.exceptions.TooManyAdvertisersFoundException;
import org.mule.modules.google.dfp.exceptions.TooManyAgenciesFoundException;
import org.mule.modules.google.dfp.exceptions.UpdateFailedException;
import org.mule.modules.google.dfp.reconciliationreport.ReconciliationQueryParams;
import org.mule.modules.google.dfp.strategy.GoogleDfpConnectionStrategy;

import com.google.api.ads.dfp.axis.v201505.Company;
import com.google.api.ads.dfp.axis.v201505.Contact;
import com.google.api.ads.dfp.axis.v201505.CustomField;
import com.google.api.ads.dfp.axis.v201505.CustomFieldOption;
import com.google.api.ads.dfp.axis.v201505.Date;
import com.google.api.ads.dfp.axis.v201505.DateTime;
import com.google.api.ads.dfp.axis.v201505.LineItem;
import com.google.api.ads.dfp.axis.v201505.Order;
import com.google.api.ads.dfp.axis.v201505.Product;
import com.google.api.ads.dfp.axis.v201505.ProductTemplate;
import com.google.api.ads.dfp.axis.v201505.Proposal;
import com.google.api.ads.dfp.axis.v201505.ProposalLineItem;
import com.google.api.ads.dfp.axis.v201505.ReconciliationReport;
import com.google.api.ads.dfp.axis.v201505.ReconciliationReportRow;
import com.google.api.ads.dfp.axis.v201505.ReportJob;
import com.google.api.ads.dfp.axis.v201505.User;

/**
 * Google DFP Connector
 *
 * {@sample.config ../../../doc/google-dfp-connector.xml.sample google-dfp:config}
 * 
 * @author Ricston, Ltd.
 */
@MetaDataScope(DimensionCategory.class)
@Connector(name = "google-dfp", schemaVersion = "1.0", friendlyName = "GoogleDfp")
public class GoogleDfpConnector {

    @Config
    GoogleDfpConnectionStrategy connectionStrategy;

    /**
     * Creates a report given a date range
     * 
     * {@sample.xml ../../../doc/google-dfp-connector.xml.sample google-dfp:create-contracted-report}
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
    public ReportJob createContractedReport(Date startDate, Date endDate, List<Long> ids) throws CreateReportException {
        return connectionStrategy.getReportService().createContractedProposalLineItemsReport(connectionStrategy.getSession(), startDate, endDate,ids);
    }
    
    /**
     * Creates a report given a date range
     * 
     * {@sample.xml ../../../doc/google-dfp-connector.xml.sample google-dfp:create-contracted-report-with-ad-units}
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
    public ReportJob createContractedReportWithAdUnits(Date startDate, Date endDate, List<Long> ids) throws CreateReportException {
        return connectionStrategy.getReportService().createContractedProposalLineItemsReportWithAdUnits(connectionStrategy.getSession(), startDate, endDate,ids);
    }
    
    /**
     * Creates a report given a date range
     * 
     * {@sample.xml ../../../doc/google-dfp-connector.xml.sample google-dfp:create-reach-report}
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
    public ReportJob createReachReport(Date startDate, Date endDate) throws CreateReportException {
        return connectionStrategy.getReportService().createReachReport(connectionStrategy.getSession(), startDate, endDate);
    }
    
    /**
     * Creates a report given a date range
     * 
     * {@sample.xml ../../../doc/google-dfp-connector.xml.sample google-dfp:create-if-report-is-ready}
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
    public ReportJob createIfReportIsReady(Date startDate, Date endDate) throws CreateReportException {
        return connectionStrategy.getReportService().checkIfReportIsReady(connectionStrategy.getSession(), startDate, endDate);
    }
    
    
    /**
     * Creates a report given a date range
     * 
     * {@sample.xml ../../../doc/google-dfp-connector.xml.sample google-dfp:create-reach-lifetime-report}
     * 
     * @return The report job
     * @throws CreateReportException
     *             Create Report Exception
     * 
     */
    @Processor
    public ReportJob createReachLifetimeReport() throws CreateReportException {
        return connectionStrategy.getReportService().createReachLifetimeReport(connectionStrategy.getSession());
    }
    
    /**
     * Creates a report given a date range
     * 
     * {@sample.xml ../../../doc/google-dfp-connector.xml.sample google-dfp:create-reach-lifetime-report}
     * 
     * @return The report job
     * @throws CreateReportException
     *             Create Report Exception
     * 
     */
    @Processor
    public ReportJob createAllActiveLineItemsReport(Date startDate, Date endDate) throws CreateReportException {
        return connectionStrategy.getReportService().createAllActiveLineItemsReport(connectionStrategy.getSession(),startDate,endDate);
    }
    
    
    /**
     * Creates a report given a date range
     * 
     * {@sample.xml ../../../doc/google-dfp-connector.xml.sample google-dfp:create-actuals-report}
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
    public ReportJob createActualsReports(Date startDate, Date endDate, List<Long> ids) throws CreateReportException {
        return connectionStrategy.getReportService().createActualsReport(connectionStrategy.getSession(), startDate, endDate,ids);
    }

    
    /**
     * Creates a report given a date range
     * 
     * {@sample.xml ../../../doc/google-dfp-connector.xml.sample google-dfp:create-actuals-without-ads-report}
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
    public ReportJob createActualsReportsWithoutAds(Date startDate, Date endDate, List<Long> ids) throws CreateReportException {
        return connectionStrategy.getReportService().createActualsReportWithoutAds(connectionStrategy.getSession(), startDate, endDate,ids);
    }

    /**
     * Creates a report given a date range
     * 
     * {@sample.xml ../../../doc/google-dfp-connector.xml.sample google-dfp:active-line-items-report}
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
    public ReportJob activeLineItemsReport(Date startDate, Date endDate) throws CreateReportException {
        return connectionStrategy.getReportService().activeLineItemsReport(connectionStrategy.getSession(), startDate, endDate);
    }
    
    /**
     * Creates a report given a date range
     * 
     * {@sample.xml ../../../doc/google-dfp-connector.xml.sample google-dfp:age-and-gender-report}
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
    public ReportJob ageAndGenderReport(Date startDate, Date endDate, List<Integer> lineItems) throws CreateReportException {
        return connectionStrategy.getReportService().ageAndGenderReport(connectionStrategy.getSession(), startDate, endDate, lineItems);
    }
    
    /**
     * Download a report from the Google DFP services
     * 
     * {@sample.xml ../../../doc/google-dfp-connector.xml.sample google-dfp:download-report}
     * 
     * @param reportJob
     *            Content to be processed
     * @return Input Stream containing a gz version of the CSV dump of the report
     * @throws ReportDownloadException
     *             Report Download Exception
     */
    @Processor
    public InputStream downloadReport(@Default("#[payload]") ReportJob reportJob) throws ReportDownloadException {
        return connectionStrategy.getReportService().downloadReport(connectionStrategy.getSession(), reportJob);
    }

    /**
     * Retrieve all companies
     * 
     * {@sample.xml ../../../doc/google-dfp-connector.xml.sample google-dfp:get-all-companies}
     * 
     * @return All companies
     * @throws GetAllCompaniesException
     *             Get All Companies Exception
     */
    @Processor
    public List<Company> getAllCompanies(DateTime lastModifiedDate, DateTime snapshotDateTime) throws GetAllCompaniesException {
        return connectionStrategy.getCompanyService().getAllCompanies(connectionStrategy.getSession(), lastModifiedDate, snapshotDateTime);
    }

    /**
     * Retrieve the agency ID by name
     * 
     * {@sample.xml ../../../doc/google-dfp-connector.xml.sample google-dfp:get-agency-by-name}
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
    public Company getAgencyByName(String agencyName) throws GetAgencyByNameException, TooManyAgenciesFoundException {
        return connectionStrategy.getCompanyService().getAgencyByName(connectionStrategy.getSession(), agencyName);
    }

    /**
     * Retrieve the advertiser ID by name
     * 
     * {@sample.xml ../../../doc/google-dfp-connector.xml.sample google-dfp:get-advertiser-by-name}
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
    public String getAdvertiserByName(String advertiserName) throws GetAdvertiserByNameException, TooManyAdvertisersFoundException {
        return connectionStrategy.getCompanyService().getAdvertiserByName(connectionStrategy.getSession(), advertiserName);
    }

    /**
     * Retrieve the reconciliation report IDs given the start date
     * 
     * {@sample.xml ../../../doc/google-dfp-connector.xml.sample google-dfp:get-reconciliation-report-ids-by-start-date}
     * 
     * @param startDate
     *            Start date used for searching reconciliation reports
     * @return List of reconciliation report ids
     * @throws ReconciliationReportException
     *             Reconciliation Report By ID Exception
     */
    @Processor
    public List<ReconciliationReport> getReconciliationReportIdsByStartDate(@Default("#[payload]") Date startDate) throws ReconciliationReportException {
        try {
            String dateFormat = "%04d-%02d-%02d";
            String stringDate = String.format(dateFormat, startDate.getYear(), startDate.getMonth(), startDate.getDay());
            return connectionStrategy.getReconciliationReportService().getReconciliationReportByStartDate(connectionStrategy.getSession(), stringDate);
        } catch (Exception e) {
            throw new ReconciliationReportException(e);
        }
    }
    
    /**
     * Retrieve Reconciliation report rows given the reconciliation report ID, order ID and line item ID
     * 
     * {@sample.xml ../../../doc/google-dfp-connector.xml.sample google-dfp:get-reconciliation-report-rows}
     * 
     * @param queryParams
     *            query parameters to get reconciliation report row
     * @return List of reconcialtion report rows
     * @throws ReconciliationReportRowException
     *             Reconciliation Report Row Exception
     */
    @Processor
    public List<ReconciliationReportRow> getReconciliationReportRows(@Default("#[payload]") ReconciliationQueryParams queryParams) throws ReconciliationReportRowException {
        try {
            return connectionStrategy.getReconciliationReportRowService().getReconciliationReportRows(connectionStrategy.getSession(), queryParams);
        } catch (Exception e) {
            throw new ReconciliationReportRowException(e);
        }
    }

    /**
     * Retrieve the company by ID. Null is returned if the company is not found
     * 
     * {@sample.xml ../../../doc/google-dfp-connector.xml.sample google-dfp:get-company-by-id}
     * 
     * @param companyId
     *            The company ID
     * @return The company
     * @throws GetCompanyByIdException
     *             Get Company By ID Exception
     */
    @Processor
    public Company getCompanyById(Long companyId) throws GetCompanyByIdException {
        return connectionStrategy.getCompanyService().getCompanyById(connectionStrategy.getSession(), companyId);
    }

    /**
     * Create a company by supplying a company object
     * 
     * {@sample.xml ../../../doc/google-dfp-connector.xml.sample google-dfp:create-company}
     * 
     * @param company
     *            The company to create
     * @return The created company
     * @throws CreateFailedException
     *             Create Failed Exception
     */
    @Processor
    public Company createCompany(@Default("#[payload]") Company company) throws CreateFailedException {
        return connectionStrategy.getCompanyService().createCompany(connectionStrategy.getSession(), company);
    }

    /**
     * Update company by supplying a company object
     * 
     * {@sample.xml ../../../doc/google-dfp-connector.xml.sample google-dfp:update-company}
     * 
     * @param company
     *            The company to update
     * @return The updated company
     * @throws UpdateFailedException
     *             Update Failed Exception
     */
    @Processor
    public Company updateCompany(@Default("#[payload]") Company company) throws UpdateFailedException {
        return connectionStrategy.getCompanyService().updateCompany(connectionStrategy.getSession(), company);
    }

    
    /**
     * Retrieve all products
     * 
     * {@sample.xml ../../../doc/google-dfp-connector.xml.sample google-dfp:get-products-by-statement}
     * 
     * @return All products
     * @throws GetProductsByStatementException
     *             Get Products Exception
     */
    @Processor
    public List<Product> getProductsByStatement(DateTime lastModifiedDate, DateTime snapshotDateTime) throws GetProductsByStatementException{
        return connectionStrategy.getProductService().getProductsByStatement(connectionStrategy.getSession(), lastModifiedDate , snapshotDateTime );
    }
    
    /**
     * Retrieve all product templates
     * 
     * {@sample.xml ../../../doc/google-dfp-connector.xml.sample google-dfp:get-product-templates-by-statement}
     * 
     * @return All product templates
     * @throws GetProductTemplatesException
     *             Get Product Templates Exception
     */
    @Processor
    public List<ProductTemplate> getProductTemplatesByStatement(DateTime lastModifiedDate, DateTime snapshotDateTime) throws GetProductTemplatesException{
        return connectionStrategy.getProductTemplateService().getProductTemplatesByStatement(connectionStrategy.getSession(), lastModifiedDate, snapshotDateTime );
    }
    
    
    /**
     * Retrieve line items by modified date
     * 
     * {@sample.xml ../../../doc/google-dfp-connector.xml.sample google-dfp:get-line-items-by-statament}
     * 
     * @return LineItem 
     * @throws GetLineItemsException
     *             Get Line Items Exception
     */
    @Processor
    public List<LineItem> getLineItemsByStatement(DateTime lastModifiedDate, DateTime snapshotDateTime) throws GetLineItemsException{
        return connectionStrategy.getLineItemService().getLineItemsByStatement(connectionStrategy.getSession(), lastModifiedDate, snapshotDateTime);
    }
    
    /**
     * Retrieve line items filtered by order IDs
     * 
     * {@sample.xml ../../../doc/google-dfp-connector.xml.sample google-dfp:get-filtered-line-items-by-statement}
     * 
     * @return LineItem 
     * @throws GetLineItemsException
     *             Get Line Items Exception
     */
    @Processor
    public List<LineItem> getFilteredLineItemsByStatement(@Default("#[payload]") List<Long> orderIds) throws GetLineItemsException{
        return connectionStrategy.getLineItemService().getFilteredLineItemsByStatement(connectionStrategy.getSession(), orderIds);
    }
    
    /**
     * Retrieve line items filtered by order IDs
     * 
     * {@sample.xml ../../../doc/google-dfp-connector.xml.sample google-dfp:get-filtered-line-items-by-statement}
     * 
     * @return LineItem 
     * @throws GetLineItemsException
     *             Get Line Items Exception
     */
    @Processor
    public List<LineItem> getLineItemsById(@Default("#[payload]") List<Long> ids) throws GetLineItemsException{
        return connectionStrategy.getLineItemService().getLineItemsById(connectionStrategy.getSession(),ids);
    }
    
    /**
     * Retrieve custom fields by modified date
     * 
     * {@sample.xml ../../../doc/google-dfp-connector.xml.sample google-dfp:get-custom-fields-by-statement}
     * 
     * @return CustomField
     * @throws GetCustomFieldsException
     *            Get custom fields exception
     */
    @Processor
    public List<CustomField> getCustomFieldsByStatement() throws GetCustomFieldsException{
        return connectionStrategy.getCustomFieldService().getCustomFieldsByStatement(connectionStrategy.getSession());
    }
    
    
    /**
     * Retrieve custom field options
     * 
     * {@sample.xml ../../../doc/google-dfp-connector.xml.sample google-dfp:get-custom-field-option}
     * 
     * @return CustomField
     * @throws GetCustomFieldsException
     *            Get custom fields exception
     */
    @Processor
    public CustomFieldOption getCustomFieldOption(Long id) throws GetCustomFieldsException{
        return connectionStrategy.getCustomFieldService().getCustomFieldOption(connectionStrategy.getSession(), id);
    }
    
    /**
     * Retrieve custom fields by modified date
     * 
     * {@sample.xml ../../../doc/google-dfp-connector.xml.sample google-dfp:get-products-by-statement}
     * 
     * @return List of CustomField
     * @throws GetCustomFieldsException
     *            Get custom fields exception
     */
    @Processor
    public List<Order> getOrdersByStatement(DateTime lastModifiedDate, DateTime snapshotDateTime) throws GetOrdersException{
        return connectionStrategy.getOrderService().getOrdersByStatement(connectionStrategy.getSession(), lastModifiedDate , snapshotDateTime);
    }
    
    /**
     * Retrieve proposals by modified date
     * 
     * {@sample.xml ../../../doc/google-dfp-connector.xml.sample google-dfp:get-proposals-by-statement}
     * 
     * @return List of Proposals
     * @throws GetProposalsException
     *            Get proposals exception
     */
    @Processor
    public List<Proposal> getProposalsByStatement(DateTime lastModifiedDate, DateTime snapshotDateTime) throws GetProposalsException{
        return connectionStrategy.getProposalService().getProposalsByStatement(connectionStrategy.getSession(), lastModifiedDate, snapshotDateTime);
    }
    
    
    /**
     * Retrieve proposals by modified date
     * 
     * {@sample.xml ../../../doc/google-dfp-connector.xml.sample google-dfp:get-proposal-line-items-by-statement}
     * 
     * @return List of Proposal LineItems
     * @throws GetProposalsException
     *            Get proposals exception
     */
    @Processor
    public List<ProposalLineItem> getProposalLineItemsByStatement( DateTime lastModifiedDate,DateTime snapshotDateTime) throws GetProposalLineItemsException{
        return connectionStrategy.getProposalLineItemService().getProposalLineItemsByStatement(connectionStrategy.getSession(), lastModifiedDate, snapshotDateTime);
    }
    
    /**
     * Retrieve proposals by IDs
     * 
     * {@sample.xml ../../../doc/google-dfp-connector.xml.sample google-dfp:get-proposal-line-items-by-id}
     * 
     * @return List of Proposal LineItems
     * @throws GetProposalsException
     *            Get proposals exception
     */
    @Processor
    public List<ProposalLineItem> getProposalLineItemsById(@Default("#[payload]")List<Long> ids) throws GetProposalLineItemsException{
        return connectionStrategy.getProposalLineItemService().getProposalLineItemsById(connectionStrategy.getSession(), ids);
    }
    /**
     * Retrieve proposals by proposal IDs
     * 
     * {@sample.xml ../../../doc/google-dfp-connector.xml.sample google-dfp:get-proposal-line-items-by-statement-filter}
     * 
     * @return List of Proposal LineItems
     * @throws GetProposalsException
     *            Get proposals exception
     */
    @Processor
    public List<ProposalLineItem> getProposalLineItemsByStatementFilter(@Default("#[payload]")List<Long> proposalIds) throws GetProposalLineItemsException{
        return connectionStrategy.getProposalLineItemService().getProposalLineItemsByStatementByFilter(connectionStrategy.getSession(), proposalIds);
    }
    
    /**
     * Retrieve contacts
     * 
     * {@sample.xml ../../../doc/google-dfp-connector.xml.sample google-dfp:get-contacts-by-statement}
     * 
     * @return List of Contacts
     * @throws GetAllContactsException
     *            Get all contacts exception
     */
    @Processor
    public List<Contact> getContactsByStatement() throws GetAllContactsException{
        return connectionStrategy.getContactService().getContactsByStatement(connectionStrategy.getSession());
    }
    
    
    /**
     * Retrieve users
     * 
     * {@sample.xml ../../../doc/google-dfp-connector.xml.sample google-dfp:get-users-by-statement}
     * 
     * @return List of Users
     * @throws GetUsersException
     *            Get users exception
     */
    @Processor
    public List<User> getUsersByStatement() throws GetUsersException{
        return connectionStrategy.getUserService().getUsersByStatement(connectionStrategy.getSession());
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
    public void setConnectionStrategy(GoogleDfpConnectionStrategy connectionStrategy) {
        this.connectionStrategy = connectionStrategy;
    }

}