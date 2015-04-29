package com.ricston.googledemo.components;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;
import org.mule.api.lifecycle.Callable;
import org.mule.api.transport.PropertyScope;

import com.google.api.ads.dfp.axis.utils.v201411.DateTimes;
import com.google.api.ads.dfp.axis.v201411.Date;


public class DateCreationComponent implements Callable {

	@Override
	public Object onCall(MuleEventContext eventContext) throws Exception {
		
		MuleMessage message = eventContext.getMessage();
		
		int lookBackPeriod = 2;

		DateTime currentDate = new org.joda.time.DateTime(DateTimeZone.UTC);

		/*
		 * Calculate the start date from the look back period
		 */
		DateTime lookBackPeriodTime = currentDate.minusMonths(lookBackPeriod);
		lookBackPeriodTime = lookBackPeriodTime.withDayOfMonth(1);
		String startDate = lookBackPeriodTime.toString("yyyy-MM-dd");

		/*
		 * Calculate the end date by going one month back and get the amount of
		 * days for that month
		 */
		DateTime lastMonth = currentDate.minusMonths(1);
		lastMonth = lastMonth.withDayOfMonth(lastMonth.dayOfMonth()
				.getMaximumValue());

		String endDate = lastMonth.toString("yyyy-MM-dd");

		/*
		 * Create Google DFP date
		 */
		Date startDateWithTimezone = DateTimes.toDateTime(
				startDate + "T00:00:00", "Europe/London").getDate();
		Date endDateWithTimezone = DateTimes.toDateTime(endDate + "T00:00:00",
				"Europe/London").getDate();

		/*
		 * Setting session properties on current message
		 */
		message.setProperty("startDateWithTimezone", startDateWithTimezone,
				PropertyScope.SESSION);
		message.setProperty("endDateWithTimezone", endDateWithTimezone,
				PropertyScope.SESSION);
		return message;
	}

}
