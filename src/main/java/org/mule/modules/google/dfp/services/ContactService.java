package org.mule.modules.google.dfp.services;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.mule.modules.google.dfp.exceptions.GetAllContactsException;

import com.google.api.ads.dfp.axis.factory.DfpServices;
import com.google.api.ads.dfp.axis.utils.v201505.StatementBuilder;
import com.google.api.ads.dfp.axis.v201505.Contact;
import com.google.api.ads.dfp.axis.v201505.ContactPage;
import com.google.api.ads.dfp.axis.v201505.ContactServiceInterface;
import com.google.api.ads.dfp.lib.client.DfpSession;

public class ContactService {

	private static final Logger logger = Logger.getLogger(ContactService.class);

	protected ContactServiceInterface createContactService(DfpSession session) {
		DfpServices dfpServices = new DfpServices();

		// Get the CustomField service.
		ContactServiceInterface contactService = dfpServices.get(session,
				ContactServiceInterface.class);

		return contactService;
	}

	public List<Contact> getContactsByStatement(DfpSession session)
			throws GetAllContactsException {
		try {

			// Get the ContactService.
			ContactServiceInterface contactService = createContactService(session);

			// Create a statement to get all contacts.
			StatementBuilder statementBuilder = new StatementBuilder().orderBy(
					"id ASC").limit(StatementBuilder.SUGGESTED_PAGE_LIMIT);

			// Default for total result set size.
			int totalResultSetSize = 0;
			List<Contact> results = new ArrayList<Contact>();

			ContactPage initialPage = contactService.getContactsByStatement(statementBuilder
					.toStatement());
			totalResultSetSize = initialPage.getTotalResultSetSize();
			
			logger.info("Getting all contacts.");
			do {
				// Get contacts by statement.
				ContactPage page = contactService.getContactsByStatement(statementBuilder
						.toStatement());

				if (page.getResults() != null) {
					for (Contact contact : page.getResults()) {
						results.add(contact);
					}
				}

				statementBuilder
						.increaseOffsetBy(StatementBuilder.SUGGESTED_PAGE_LIMIT);
			} while (statementBuilder.getOffset() < totalResultSetSize);

			logger.info("Retrieved " + totalResultSetSize + " contacts.");

			return results;
		} catch (RemoteException e) {
			throw new GetAllContactsException(e);
		}
	}

}
