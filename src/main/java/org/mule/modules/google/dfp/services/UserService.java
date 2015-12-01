package org.mule.modules.google.dfp.services;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.mule.modules.google.dfp.exceptions.GetUsersException;

import com.google.api.ads.dfp.axis.factory.DfpServices;
import com.google.api.ads.dfp.axis.utils.v201505.StatementBuilder;
import com.google.api.ads.dfp.axis.v201505.User;
import com.google.api.ads.dfp.axis.v201505.UserPage;
import com.google.api.ads.dfp.axis.v201505.UserServiceInterface;
import com.google.api.ads.dfp.lib.client.DfpSession;

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

			logger.info("Getting all users.");
			do {
				// Get contacts by statement.
				UserPage page;
				page = userService.getUsersByStatement(statementBuilder
						.toStatement());

				if (page.getResults() != null) {
					totalResultSetSize = page.getTotalResultSetSize();
					for (User user : page.getResults()) {
						results.add(user);
					}
				}

				statementBuilder
						.increaseOffsetBy(StatementBuilder.SUGGESTED_PAGE_LIMIT);
			} while (statementBuilder.getOffset() < totalResultSetSize);

			logger.info("Retrieved " + totalResultSetSize + " users.");

			return results;
		} catch (RemoteException e) {
			throw new GetUsersException(e);
		}
	}

}
