package org.mule.modules.google.dfp.services;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.mule.modules.google.dfp.exceptions.GetUsersException;

import com.google.api.ads.dfp.axis.factory.DfpServices;
import com.google.api.ads.dfp.axis.utils.v201602.StatementBuilder;
import com.google.api.ads.dfp.axis.v201602.User;
import com.google.api.ads.dfp.axis.v201602.UserPage;
import com.google.api.ads.dfp.axis.v201602.UserServiceInterface;
import com.google.api.ads.dfp.lib.client.DfpSession;
import com.google.api.client.repackaged.com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class UserService {

	private static final Logger logger = Logger.getLogger(UserService.class);

	protected UserServiceInterface createUserService(DfpSession session) {
		DfpServices dfpServices = new DfpServices();

		// Get the CustomField service.
		UserServiceInterface userService = dfpServices.get(session,
				UserServiceInterface.class);

		return userService;
	}

	public List<User> getUsersByStatement(DfpSession session)
			throws GetUsersException {
		try {

			// Get the UserService.
			UserServiceInterface userService = createUserService(session);

			// Create a statement to get all users.
			StatementBuilder statementBuilder = new StatementBuilder().orderBy(
					"id ASC").limit(StatementBuilder.SUGGESTED_PAGE_LIMIT);

			// Default for total result set size.
			int totalResultSetSize = 0;
			List<User> results = new ArrayList<User>();

			UserPage initialPage = userService
					.getUsersByStatement(statementBuilder.toStatement());
			totalResultSetSize = initialPage.getTotalResultSetSize();
			logger.info("Getting all users.");
			do {
				// Get contacts by statement.
				UserPage page = userService
						.getUsersByStatement(statementBuilder.toStatement());

				if (page.getResults() != null) {
					for (User user : page.getResults()) {
						results.add(user);
					}
				}

				statementBuilder
						.increaseOffsetBy(StatementBuilder.SUGGESTED_PAGE_LIMIT);
			} while (statementBuilder.getOffset() < totalResultSetSize);

			logger.info("Number of results found: " + totalResultSetSize);
			logger.info("Number of results retrieved: " + results.size());

			return results;
		} catch (RemoteException e) {
			throw new GetUsersException(e);
		}
	}

	public List<User> getUsersById(DfpSession session, List<Long> ids)
			throws GetUsersException {
		try {

			UserServiceInterface userService = createUserService(session);

			List<List<Long>> idsBatches = Lists.partition(ids, 400);
			List<User> results = new ArrayList<User>();

			for (List<Long> currentBatch : idsBatches) {

				String whereClauseFilter = Joiner.on(", ").join(currentBatch);
				String whereQueryStatement = "id IN (" + whereClauseFilter
						+ ")";

				StatementBuilder statementBuilder = new StatementBuilder()
						.where(whereQueryStatement).limit(
								StatementBuilder.SUGGESTED_PAGE_LIMIT);

				// Default for total result set size.
				int totalResultSetSize = 0;

				UserPage initialPage = userService
						.getUsersByStatement(statementBuilder.toStatement());
				totalResultSetSize = initialPage.getTotalResultSetSize();

				do {
					UserPage page = userService
							.getUsersByStatement(statementBuilder.toStatement());

					if (page.getResults() != null) {
						totalResultSetSize = page.getTotalResultSetSize();
						for (User user : page.getResults()) {
							results.add(user);
						}
					}

					statementBuilder
							.increaseOffsetBy(StatementBuilder.SUGGESTED_PAGE_LIMIT);
				} while (statementBuilder.getOffset() < totalResultSetSize);

				logger.info("Number of results found: " + totalResultSetSize
						+ '\n' + "Number of results retrieved: "
						+ results.size());

			}

			return results;
		} catch (RemoteException e) {
			throw new GetUsersException(e);
		}
	}

}
