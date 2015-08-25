/**
 * (c) 2003-2015 Ricston, Ltd. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 */

package org.mule.modules.google.dfp.services;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.mule.modules.google.dfp.exceptions.CreateFailedException;
import org.mule.modules.google.dfp.exceptions.GetAdvertiserByNameException;
import org.mule.modules.google.dfp.exceptions.GetAgencyByNameException;
import org.mule.modules.google.dfp.exceptions.GetAllCompaniesException;
import org.mule.modules.google.dfp.exceptions.GetCompanyByIdException;
import org.mule.modules.google.dfp.exceptions.GetCompanyCommentByNameException;
import org.mule.modules.google.dfp.exceptions.TooManyAdvertisersFoundException;
import org.mule.modules.google.dfp.exceptions.TooManyAgenciesFoundException;
import org.mule.modules.google.dfp.exceptions.TooManyCompaniesFoundException;
import org.mule.modules.google.dfp.exceptions.UpdateFailedException;

import com.google.api.ads.dfp.axis.factory.DfpServices;
import com.google.api.ads.dfp.axis.utils.v201505.StatementBuilder;
import com.google.api.ads.dfp.axis.v201505.ApiException;
import com.google.api.ads.dfp.axis.v201505.Company;
import com.google.api.ads.dfp.axis.v201505.CompanyPage;
import com.google.api.ads.dfp.axis.v201505.CompanyServiceInterface;
import com.google.api.ads.dfp.axis.v201505.CompanyType;
import com.google.api.ads.dfp.axis.v201505.DateTime;
import com.google.api.ads.dfp.lib.client.DfpSession;

public class CompanyService {

	private static final Logger logger = Logger.getLogger(CompanyService.class);

	protected CompanyServiceInterface createCompanyService(DfpSession session) {
		DfpServices dfpServices = new DfpServices();

		// Get the CompanyService.
		CompanyServiceInterface companyService = dfpServices.get(session,
				CompanyServiceInterface.class);

		return companyService;
	}

	public List<Company> getAllCompanies(DfpSession session,
			DateTime lastModifiedDateTime) throws GetAllCompaniesException {
		try {
			// Get the CompanyService.
			CompanyServiceInterface companyService = createCompanyService(session);

			// Create a statement to get company by name
			StatementBuilder statementBuilder = new StatementBuilder().where("lastModifiedDateTime > :lastModifiedDateTime")
					.withBindVariableValue("lastModifiedDateTime",
							lastModifiedDateTime);
//					.limit(StatementBuilder.SUGGESTED_PAGE_LIMIT);

			// Get companies by statement.
			CompanyPage page = companyService
					.getCompaniesByStatement(statementBuilder.toStatement());

			Company[] companies = page.getResults();

			
			List<Company> results = new ArrayList<Company>();
			if(companies != null){
				results = Arrays.asList(companies);
			}
			
			return results;
	
		} catch (ApiException e) {
			throw new GetAllCompaniesException(e);
		} catch (RemoteException e) {
			throw new GetAllCompaniesException(e);
		} catch (Exception e) {
			throw new GetAllCompaniesException(e);
		}
	}

	public String getCompanyCommentByName(DfpSession session, String name)
			throws GetCompanyCommentByNameException,
			TooManyCompaniesFoundException {
		try {
			// Get the CompanyService.
			CompanyServiceInterface companyService = createCompanyService(session);

			// Create a statement to get company by name
			StatementBuilder statementBuilder = new StatementBuilder().where(
					"name = :name").withBindVariableValue("name", name);

			// Get companies by statement.
			CompanyPage page = companyService
					.getCompaniesByStatement(statementBuilder.toStatement());

			if (page.getResults() == null || page.getTotalResultSetSize() != 1) {
				throw new TooManyCompaniesFoundException(
						page.getTotalResultSetSize());
			}

			Company company = page.getResults(0);

			logger.info("Company with ID " + company.getId() + " , name "
					+ company.getName() + " type " + company.getType()
					+ " and comment " + company.getComment() + " was found.");

			return company.getComment();
		} catch (ApiException e) {
			throw new GetCompanyCommentByNameException(e);
		} catch (RemoteException e) {
			throw new GetCompanyCommentByNameException(e);
		} catch (Exception e) {
			throw new GetCompanyCommentByNameException(e);
		}
	}

	public String getAdvertiserByName(DfpSession session, String name)
			throws GetAdvertiserByNameException,
			TooManyAdvertisersFoundException {
		try {
			CompanyServiceInterface companyService = createCompanyService(session);

			// Create a statement to only select companies that are advertisers.
			StatementBuilder statementBuilder = new StatementBuilder()
					.where("type = :type AND name = :name")
					.orderBy("id ASC")
					.limit(StatementBuilder.SUGGESTED_PAGE_LIMIT)
					.withBindVariableValue("type",
							CompanyType.ADVERTISER.toString())
					.withBindVariableValue("name", name);

			// Default for total result set size.
			int totalResultSetSize = 0;
			String externalId = "";

			do {
				// Get companies by statement.
				CompanyPage page = companyService
						.getCompaniesByStatement(statementBuilder.toStatement());

				if (page.getResults() != null) {
					totalResultSetSize = page.getTotalResultSetSize();
					if (totalResultSetSize > 1) {
						logger.info("Duplicate Advertisers with the name: "
								+ name);
						throw new TooManyAdvertisersFoundException(
								totalResultSetSize);
					} else {
						for (Company company : page.getResults()) {
							externalId = company.getExternalId();
						}
					}

				} else {
					logger.info("No Advertisers with the name: " + name
							+ " were found.");
				}

				statementBuilder
						.increaseOffsetBy(StatementBuilder.SUGGESTED_PAGE_LIMIT);
			} while (statementBuilder.getOffset() < totalResultSetSize);

			logger.info("Number of Advertisers found:" + totalResultSetSize);
			return externalId;
		} catch (ApiException e) {
			throw new GetAdvertiserByNameException(e);
		} catch (RemoteException e) {
			throw new GetAdvertiserByNameException(e);
		} catch (Exception e) {
			throw new GetAdvertiserByNameException(e);
		}
	}

	public Company getAgencyByName(DfpSession session, String name)
			throws GetAgencyByNameException, TooManyAgenciesFoundException {

		try {
			CompanyServiceInterface companyService = createCompanyService(session);

			// Create a statement to only select companies that are advertisers.
			StatementBuilder statementBuilder = new StatementBuilder()
					.where("type = :type AND name = :name")
					.orderBy("id ASC")
					.limit(StatementBuilder.SUGGESTED_PAGE_LIMIT)
					.withBindVariableValue("type",
							CompanyType.AGENCY.toString())
					.withBindVariableValue("name", name);

			// Default for total result set size.
			int totalResultSetSize = 0;
			Company companyFound = null;

			do {
				// Get companies by statement.
				CompanyPage page = companyService
						.getCompaniesByStatement(statementBuilder.toStatement());

				if (page.getResults() != null) {
					totalResultSetSize = page.getTotalResultSetSize();
					if (totalResultSetSize > 1) {
						logger.info("Duplicate Agencies with the name: " + name);
						throw new TooManyAgenciesFoundException(
								totalResultSetSize);
					} else {
						companyFound = page.getResults(0);
					}

				} else {
					logger.info("No Agency with the name: " + name
							+ " were found.");
				}

				statementBuilder
						.increaseOffsetBy(StatementBuilder.SUGGESTED_PAGE_LIMIT);
			} while (statementBuilder.getOffset() < totalResultSetSize);

			logger.info("Number of Agencies found:" + totalResultSetSize);
			return companyFound;
		} catch (ApiException e) {
			throw new GetAgencyByNameException(e);
		} catch (RemoteException e) {
			throw new GetAgencyByNameException(e);
		} catch (Exception e) {
			throw new GetAgencyByNameException(e);
		}
	}

	public Company getCompanyById(DfpSession session, Long companyId)
			throws GetCompanyByIdException {
		try {

			// Get the CompanyService.
			CompanyServiceInterface companyService = createCompanyService(session);

			// Create a statement to get all companies.
			StatementBuilder statementBuilder = new StatementBuilder().where(
					"id = :id").withBindVariableValue("id", companyId);

			Company company = null;

			// Get companies by statement.
			CompanyPage page = companyService
					.getCompaniesByStatement(statementBuilder.toStatement());

			if (page.getResults() != null) {
				company = page.getResults(0);
				logger.info("Company with ID " + company.getId() + " , name "
						+ company.getName() + " type " + company.getType()
						+ " was found.");
			}

			return company;
		} catch (ApiException e) {
			throw new GetCompanyByIdException(e);
		} catch (RemoteException e) {
			throw new GetCompanyByIdException(e);
		} catch (Exception e) {
			throw new GetCompanyByIdException(e);
		}
	}

	public Company createCompany(DfpSession session, Company company)
			throws CreateFailedException {

		try {
			// Get the CompanyService.
			CompanyServiceInterface companyService = createCompanyService(session);

			Company[] companies;

			companies = companyService
					.createCompanies(new Company[] { company });

			Company createdCompany = companies[0];

			logger.info(String
					.format("A company with ID \"%d\", name \"%s\", and type \"%s\" was created.\"%n\"",
							createdCompany.getId(), createdCompany.getName(),
							createdCompany.getType()));

			return createdCompany;

		} catch (ApiException e) {
			throw new CreateFailedException(e);
		} catch (RemoteException e) {
			throw new CreateFailedException(e);
		} catch (Exception e) {
			throw new CreateFailedException(e);
		}
	}

	public Company updateCompany(DfpSession session, Company company)
			throws UpdateFailedException {

		try {
			CompanyServiceInterface companyService = createCompanyService(session);

			// Update the company on the server.
			Company[] companies = companyService
					.updateCompanies(new Company[] { company });

			Company updatedCompany = companies[0];

			logger.info(String
					.format("Company with ID \"%d\", name \"%s\", and comment \"%s\" was updated.\"%n\"",
							updatedCompany.getId(), updatedCompany.getName(),
							updatedCompany.getComment()));

			return updatedCompany;
		} catch (ApiException e) {
			throw new UpdateFailedException(e);
		} catch (RemoteException e) {
			throw new UpdateFailedException(e);
		} catch (Exception e) {
			throw new UpdateFailedException(e);
		}
	}

}
