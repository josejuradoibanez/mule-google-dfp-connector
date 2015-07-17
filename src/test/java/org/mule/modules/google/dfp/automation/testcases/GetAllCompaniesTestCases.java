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
import org.mule.tools.devkit.ctf.junit.AbstractTestCase;
import org.mule.tools.devkit.ctf.junit.RegressionTests;

import com.google.api.ads.dfp.axis.v201505.Company;

public class GetAllCompaniesTestCases extends AbstractTestCase {

    @Before
    public void setup() throws Exception {
        // initializeTestRunMessage("getAllCompaniesTestData");
    }

    @SuppressWarnings("unchecked")
    @Category({ RegressionTests.class })
    @Test
    public void testGetAllCompanies() throws Exception {
        // Object result = runFlowAndGetPayload("get-all-companies");
        // Assert.assertTrue(result instanceof List);
        // Assert.assertTrue(((List<Object>) result).get(0) instanceof Company);
    }

}
