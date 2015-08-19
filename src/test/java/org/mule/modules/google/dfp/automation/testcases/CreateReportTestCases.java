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
import org.mule.modules.google.dfp.automation.GoogleDfpTestParent;
import org.mule.modules.google.dfp.automation.RegressionTests;
import org.mule.modules.google.dfp.automation.SmokeTests;

import com.google.api.ads.dfp.axis.v201505.ReportJob;

public class CreateReportTestCases
    extends GoogleDfpTestParent
{

    @Before
    public void setup()
        throws Exception
    {
        initializeTestRunMessage("createReportTestData");
    }
	
    @Category({
        RegressionTests.class,
        SmokeTests.class
    })
    @Test
    @Ignore
    public void testCreateReport()
        throws Exception
    {
        Object result = runFlowAndGetPayload("create-report");
        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof ReportJob);
    }

}
