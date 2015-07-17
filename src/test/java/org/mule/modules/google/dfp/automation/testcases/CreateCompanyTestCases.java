/**
 * (c) 2003-2015 Ricston, Ltd. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 */

package org.mule.modules.google.dfp.automation.testcases;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.tools.devkit.ctf.junit.AbstractTestCase;
import org.mule.tools.devkit.ctf.junit.RegressionTests;

import com.google.api.ads.dfp.axis.v201505.Company;
import com.google.api.ads.dfp.axis.v201505.CompanyType;

public class CreateCompanyTestCases extends AbstractTestCase {

    Company newCompany;

    @Before
    public void setup() throws Exception {
        newCompany = new Company();
        newCompany.setName("Ricston Ltd");
        newCompany.setAddress("Mosta Malta");
        newCompany.setEmail("dfp@ricston.com");
        newCompany.setType(CompanyType.AGENCY);
    }

    @Category({ RegressionTests.class })
    @Test
    @Ignore
    public void testCreateCompany() throws Exception {
//        MuleEvent response = runFlow("create-company", newCompany);
//        Assert.assertNotNull(response);
//        Assert.assertNotNull(response.getMessage());
//        Assert.assertNotNull(response.getMessage().getPayload());
//        Assert.assertTrue(response.getMessage().getPayload() instanceof Company);
//
//        Company createdCompany = (Company) response.getMessage().getPayload();
//        Assert.assertEquals(newCompany.getName(), createdCompany.getName());
    }

}
