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
import org.mule.modules.google.dfp.exceptions.GetCompanyByIdException;
import org.mule.modules.google.dfp.exceptions.GetCustomFieldsException;
import org.mule.modules.google.dfp.exceptions.GetLineItemsException;
import org.mule.modules.google.dfp.exceptions.GetProductsByStatementException;
import org.mule.modules.google.dfp.exceptions.ReconciliationReportByIdException;
import org.mule.modules.google.dfp.exceptions.ReconciliationReportRowException;
import org.mule.modules.google.dfp.exceptions.ReportDownloadException;
import org.mule.modules.google.dfp.exceptions.TooManyAdvertisersFoundException;
import org.mule.modules.google.dfp.exceptions.TooManyAgenciesFoundException;
import org.mule.modules.google.dfp.exceptions.UpdateFailedException;
import org.mule.modules.google.dfp.reconciliationreport.ReconciliationQueryParams;
import org.mule.modules.google.dfp.strategy.GoogleDfpConnectionStrategy;

import com.google.api.ads.dfp.axis.v201505.Company;
import com.google.api.ads.dfp.axis.v201505.CustomField;
import com.google.api.ads.dfp.axis.v201505.Date;
import com.google.api.ads.dfp.axis.v201505.DateTime;
import com.google.api.ads.dfp.axis.v201505.LineItem;
import com.google.api.ads.dfp.axis.v201505.Product;
import com.google.api.ads.dfp.axis.v201505.ReconciliationReportRow;
import com.google.api.ads.dfp.axis.v201505.ReportJob;

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

    // experimental processor to test out dynamic datasense to get visual selection of Dimension
//    @Processor
//    public Object myProcessor(@MetaDataKeyParam(affects = MetaDataKeyParamAffectsType.BOTH) String dimension, @Default("#[payload]") Map<String, Object> types, Date startDate,
//            Date endDate) throws CreateReportException {
//        return connectionStrategy.getReportService().createReport(connectionStrategy.getSession(), startDate, endDate);
//    }

    /**
     * Creates a report given a date range
     * 
     * {@sample.xml ../../../doc/google-dfp-connector.xml.sample google-dfp:create-report}
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
    public ReportJob createReport(Date startDate, Date endDate) throws CreateReportException {
        return connectionStrategy.getReportService().createReport(connectionStrategy.getSession(), startDate, endDate);
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
    public List<Company> getAllCompanies(@Default("#[payload]") DateTime lastModifiedDate) throws GetAllCompaniesException {
        return connectionStrategy.getCompanyService().getAllCompanies(connectionStrategy.getSession(), lastModifiedDate);
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
     * @throws ReconciliationReportByIdException
     *             Reconciliation Report By ID Exception
     */
    @Processor
    public List<Long> getReconciliationReportIdsByStartDate(@Default("#[payload]") Date startDate) throws ReconciliationReportByIdException {
        try {
            String dateFormat = "%04d-%02d-%02d";
            String stringDate = String.format(dateFormat, startDate.getYear(), startDate.getMonth(), startDate.getDay());
            return connectionStrategy.getReconciliationReportService().getReconciliationReportIdsByStartDate(connectionStrategy.getSession(), stringDate);
        } catch (Exception e) {
            throw new ReconciliationReportByIdException(e);
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
     * {@sample.xml ../../../doc/google-dfp-connector.xml.sample google-dfp:get-products-by-statament}
     * 
     * @return All products
     * @throws GetProductsByStatementException
     *             Get Products Exception
     */
    @Processor
    public List<Product> getProductsByStatement(@Default("#[payload]") DateTime lastModifiedDate) throws GetProductsByStatementException{
        return connectionStrategy.getProductService().getProductsByStatement(connectionStrategy.getSession(), lastModifiedDate);
    }
    
    /**
     * Retrieve line items by modified date
     * 
     * {@sample.xml ../../../doc/google-dfp-connector.xml.sample google-dfp:get-products-by-statament}
     * 
     * @return LineItem 
     * @throws GetLineItemsException
     *             Get Line Items Exception
     */
    @Processor
    public List<LineItem> getLineItemsByStatement(@Default("#[payload]") DateTime lastModifiedDate) throws GetLineItemsException{
        return connectionStrategy.getLineItemService().getLineItemsByStatement(connectionStrategy.getSession(), lastModifiedDate);
    }
    
    
    /**
     * Retrieve custom fields by modified date
     * 
     * {@sample.xml ../../../doc/google-dfp-connector.xml.sample google-dfp:get-products-by-statament}
     * 
     * @return CustomField
     * @throws GetCustomFieldsException
     *            Get custom fields exception
     */
    @Processor
    public List<CustomField> getCustomFieldsByStatement(@Default("#[payload]") DateTime lastModifiedDate) throws GetCustomFieldsException{
        return connectionStrategy.getCustomFieldService().getCustomFieldsByStatement(connectionStrategy.getSession(), lastModifiedDate);
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