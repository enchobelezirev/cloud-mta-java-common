package com.sap.cloud.lm.sl.mta.validators.v3;

import java.util.Map;

import com.sap.cloud.lm.sl.common.ContentException;
import com.sap.cloud.lm.sl.mta.message.Constants;
import com.sap.cloud.lm.sl.mta.message.Messages;
import com.sap.cloud.lm.sl.mta.model.ElementContext;
import com.sap.cloud.lm.sl.mta.model.Metadata;
import com.sap.cloud.lm.sl.mta.model.ParametersContainer;
import com.sap.cloud.lm.sl.mta.model.ParametersWithMetadataContainer;
import com.sap.cloud.lm.sl.mta.model.PropertiesContainer;
import com.sap.cloud.lm.sl.mta.model.PropertiesWithMetadataContainer;
import com.sap.cloud.lm.sl.mta.util.ValidatorUtil;

public class DefaultDescriptorValidationRules extends com.sap.cloud.lm.sl.mta.validators.v2.DefaultDescriptorValidationRules {

    @Override
    public void validateProperties(ElementContext elementContext, PropertiesContainer propertiesContainer) throws ContentException {
        PropertiesWithMetadataContainer propertiesWithMetadata = (PropertiesWithMetadataContainer) propertiesContainer;
        validateOptionalProperties(propertiesWithMetadata.getProperties(), propertiesWithMetadata.getPropertiesMetadata(), elementContext,
            Constants.PROPERTY_ELEMENT_TYPE_NAME);
    }

    @Override
    public void validateParameters(ElementContext elementContext, ParametersContainer parametersContainer) throws ContentException {
        ParametersWithMetadataContainer parametersWithMetadata = (ParametersWithMetadataContainer) parametersContainer;
        validateOptionalProperties(parametersWithMetadata.getParameters(), parametersWithMetadata.getParametersMetadata(), elementContext,
            Constants.PARAMETER_ELEMENT_TYPE_NAME);
    }

    protected void validateOptionalProperties(Map<String, Object> properties, Metadata metadata, ElementContext elementContext,
        String elementType) {
        for (String propertyName : properties.keySet()) {
            if (!isPropertyValid(properties.get(propertyName), propertyName, metadata)) {
                throw new ContentException(Messages.MANDATORY_ELEMENT_HAS_NO_VALUE, elementType,
                    ValidatorUtil.getPrefixedName(elementContext.getPrefixedName(), propertyName));
            }
        }
    }

    protected boolean isPropertyValid(Object property, String propertyName, Metadata metadata) {
        boolean isOptional = metadata.getOptionalMetadata(propertyName);
        return isOptional || property != null;
    }

}
