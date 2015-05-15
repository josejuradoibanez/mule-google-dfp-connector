/**
 * (c) 2003-2015 Ricston, Ltd. The software in this package is published under the terms of the CPAL v1.0 license,
 * a copy of which has been included with this distribution in the LICENSE.md file.
 */

package com.ricston.googledemo.transformer;

import java.util.Map;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;

import com.google.api.ads.dfp.axis.v201411.Company;
import com.google.api.ads.dfp.axis.v201411.CompanyType;

public class GoogleCompany extends AbstractMessageTransformer {

	@Override
	public Object transformMessage(MuleMessage message, String outputEncoding)
			throws TransformerException {
		
		Map queryParams = message.getInboundProperty("http.query.params");
		String name = (String) queryParams.get("name");
		
		Company newCompany = new Company();
		newCompany.setName(name);
		newCompany.setAddress("Mosta, Malta");
		newCompany.setType(CompanyType.ADVERTISER);
		newCompany.setEmail("googledfp@ricston.com");

		message.setPayload(newCompany);
		
		return message;
	}
}
