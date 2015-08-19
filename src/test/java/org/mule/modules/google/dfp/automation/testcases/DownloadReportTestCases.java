/**
 * (c) 2003-2015 Ricston, Ltd. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 */

package org.mule.modules.google.dfp.automation.testcases;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.modules.google.dfp.automation.GoogleDfpTestParent;
import org.mule.modules.google.dfp.automation.RegressionTests;

import com.google.api.ads.dfp.axis.v201505.ReportJob;

public class DownloadReportTestCases extends GoogleDfpTestParent {

	ReportJob createdReport;

	@Before
	public void setup() throws Exception {
		initializeTestRunMessage("createReportTestData");
		createdReport = (ReportJob) runFlowAndGetPayload("create-report");
	}

	protected InputStream downloadReport(ReportJob reportJob) throws Exception {
		MuleEvent response = runFlow("download-report", reportJob);
    	Assert.assertNotNull(response);
        Assert.assertNotNull(response.getMessage());
        Assert.assertNotNull(response.getMessage().getPayload());
        Assert.assertTrue(response.getMessage().getPayload() instanceof InputStream);
        
        return (InputStream) response.getMessage().getPayload();
	}

	@Category({ RegressionTests.class })
	@Test
	@Ignore
	public void testDownloadReport() throws Exception {
		InputStream report = downloadReport(createdReport);
		report.close();
	}

}
