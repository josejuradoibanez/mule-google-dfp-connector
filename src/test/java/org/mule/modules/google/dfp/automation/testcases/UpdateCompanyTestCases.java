/**
 * (c) 2003-2015 Ricston, Ltd. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 */

package org.mule.modules.google.dfp.automation.testcases;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.modules.google.dfp.automation.GoogleDfpTestParent;
import org.mule.modules.google.dfp.automation.RegressionTests;
import org.mule.modules.google.dfp.automation.SmokeTests;

import com.google.api.ads.dfp.axis.v201411.Company;

public class UpdateCompanyTestCases extends GoogleDfpTestParent {

	Company company;

	@Before
	public void setup() throws Exception {
		initializeTestRunMessage("getAllCompaniesTestData");
		List<Company> allCompanies = runFlowAndGetPayload("get-all-companies");
		company = allCompanies.get(0);
	}

	@After
	public void tearDown() throws Exception {
		// TODO: Add code to reset sandbox state to the one before the test was
		// run or remove
	}

	@Category({ RegressionTests.class })
	@Test
	public void testUpdateCompany() throws Exception {
		Object result = runFlowAndGetPayload("update-company");
		throw new RuntimeException("NOT IMPLEMENTED METHOD");
	}

}
