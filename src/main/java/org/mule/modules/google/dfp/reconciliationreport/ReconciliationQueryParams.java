/**
 * (c) 2003-2015 Ricston, Ltd. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 */

package org.mule.modules.google.dfp.reconciliationreport;


public class ReconciliationQueryParams {

	private long reconciliationReportId;
	private long orderId;
	private long lineItemId;
	
	public long getReconciliationReportId() {
		return reconciliationReportId;
	}
	public void setReconciliationReportId(long reconciliationReportId) {
		this.reconciliationReportId = reconciliationReportId;
	}
	public long getOrderId() {
		return orderId;
	}
	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}
	public long getLineItemId() {
		return lineItemId;
	}
	public void setLineItemId(long lineItemId) {
		this.lineItemId = lineItemId;
	}
	
}
