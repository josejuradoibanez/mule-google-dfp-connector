/**
 * (c) 2003-2015 Ricston, Ltd. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 */

package org.mule.modules.google.dfp.automation.testcases;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.modules.google.dfp.automation.GoogleDfpTestParent;
import org.mule.modules.google.dfp.automation.RegressionTests;
import org.mule.transport.NullPayload;

import com.google.api.ads.dfp.axis.v201411.Company;

public class GetCompanyByIdTestCases extends GoogleDfpTestParent {

	Company company;
	
	@Before
	public void setup() throws Exception {
		initializeTestRunMessage("getAllCompaniesTestData");
		List<Company> allCompanies = runFlowAndGetPayload("get-all-companies");
		company = allCompanies.get(0);
	}

	@Category({ RegressionTests.class, })
	@Test
	public void testGetCompanyById() throws Exception {
		MuleEvent response = runFlow("get-company-by-id", company.getId());
		Assert.assertNotNull(response);
        Assert.assertNotNull(response.getMessage());
        Assert.assertNotNull(response.getMessage().getPayload());
		Assert.assertTrue(response.getMessage().getPayload() instanceof Company);
		Assert.assertEquals(response.getMessage().getPayload(), company);
	}

	@Category({ RegressionTests.class, })
	@Test
	public void testGetCompanyByIdNotFound() throws Exception {
		MuleEvent event = runFlow("get-company-by-id", 1L);
		Assert.assertTrue(event.getMessage().getPayload() instanceof NullPayload);
	}

}
