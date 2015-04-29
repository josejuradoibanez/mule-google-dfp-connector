package com.ricston.googledemo.transformer;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;

import com.google.api.ads.dfp.axis.v201411.Company;
import com.google.api.ads.dfp.axis.v201411.CompanyType;

public class GoogleCompany extends AbstractMessageTransformer {

	@Override
	public Object transformMessage(MuleMessage message, String outputEncoding)
			throws TransformerException {
		
		Company newCompany = new Company();
		newCompany.setName("DFP");
		newCompany.setAddress("Mosta, Malta");
		newCompany.setType(CompanyType.AGENCY);
		newCompany.setEmail("googledfp@ricston.com");

		message.setPayload(newCompany);
		
		return message;
	}
}
