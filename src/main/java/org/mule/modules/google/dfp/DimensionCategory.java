package org.mule.modules.google.dfp;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.mule.api.annotations.MetaDataKeyRetriever;
import org.mule.api.annotations.MetaDataRetriever;
import org.mule.api.annotations.components.MetaDataCategory;
import org.mule.common.metadata.DefaultMetaData;
import org.mule.common.metadata.DefaultMetaDataKey;
import org.mule.common.metadata.MetaData;
import org.mule.common.metadata.MetaDataKey;
import org.mule.common.metadata.MetaDataModel;
import org.mule.common.metadata.builder.DefaultMetaDataBuilder;
import org.mule.common.metadata.builder.DynamicObjectBuilder;
import org.mule.common.metadata.datatype.DataType;

import com.google.api.ads.dfp.axis.v201505.Dimension;

@MetaDataCategory
public class DimensionCategory {

	@Inject
	private GoogleDfpConnector connector;
	
	public GoogleDfpConnector getConnector() {
		return connector;
	}

	public void setConnector(GoogleDfpConnector connector) {
		this.connector = connector;
	}

	@MetaDataKeyRetriever
	public List<MetaDataKey> getMetaDataKeys() throws Exception {
	    List<MetaDataKey> keys = new ArrayList<MetaDataKey>();

	    keys.add(new DefaultMetaDataKey("dimension_id", "Dimension"));

	    return keys;
	}
	
	@MetaDataRetriever
	public MetaData getMetaData(MetaDataKey key) throws Exception {
//	    if ("id1".equals(key.getId())) {
//	    	DefaultMetaDataBuilder builder = new DefaultMetaDataBuilder();
//	    	
//	    	DynamicObjectBuilder<?> dynamicObject = builder.createDynamicObject("Dimension");
//	    	
//	    	for (Field dimension : Class.forName("com.google.api.ads.dfp.axis.v201505.Dimension").getDeclaredFields()) {
//	    		dynamicObject.addSimpleField(dimension.getName(), DataType.STRING);
//	    	}
//	    	
//	    	MetaDataModel model = builder.build();
//	    	
//	    	return new DefaultMetaData(model);
//	    }
//	    throw new RuntimeException(String.format("This entity %s is not supported",key.getId()));
	    
	    
	    if ("dimension_id".equals(key.getId())) {
	        MetaDataModel authorModel =  new DefaultMetaDataBuilder().createPojo(Dimension.class).build();
	        return new DefaultMetaData(authorModel);
	    }
	    throw new RuntimeException(String.format("This entity %s is not supported",key.getId()));
	}
}
