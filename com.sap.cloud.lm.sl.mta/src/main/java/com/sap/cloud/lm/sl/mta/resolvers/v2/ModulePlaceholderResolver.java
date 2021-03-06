package com.sap.cloud.lm.sl.mta.resolvers.v2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sap.cloud.lm.sl.common.ContentException;
import com.sap.cloud.lm.sl.mta.builders.v2.ParametersChainBuilder;
import com.sap.cloud.lm.sl.mta.model.Module;
import com.sap.cloud.lm.sl.mta.model.ProvidedDependency;
import com.sap.cloud.lm.sl.mta.model.RequiredDependency;
import com.sap.cloud.lm.sl.mta.resolvers.PlaceholderResolver;
import com.sap.cloud.lm.sl.mta.resolvers.PropertiesPlaceholderResolver;
import com.sap.cloud.lm.sl.mta.resolvers.ResolverBuilder;
import com.sap.cloud.lm.sl.mta.util.PropertiesUtil;

public class ModulePlaceholderResolver extends PlaceholderResolver<Module> {

    protected final Module module;
    protected final ParametersChainBuilder parametersChainBuilder;
    protected final ResolverBuilder propertiesResolverBuilder;
    protected final ResolverBuilder parametersResolverBuilder;

    public ModulePlaceholderResolver(Module module, String prefix, ParametersChainBuilder parametersChainBuilder,
        ResolverBuilder propertiesResolverBuilder, ResolverBuilder parametersResolverBuilder, Map<String, String> singularToPluralMapping) {
        super(module.getName(), prefix, singularToPluralMapping);
        this.module = module;
        this.parametersChainBuilder = parametersChainBuilder;
        this.propertiesResolverBuilder = propertiesResolverBuilder;
        this.parametersResolverBuilder = parametersResolverBuilder;
    }

    @Override
    public Module resolve() throws ContentException {
        Map<String, Object> mergedParameters = getMergedParameters();
        module.setProperties(getResolvedProperties(mergedParameters));
        module.setParameters(getResolvedParameters(mergedParameters));
        module.setRequiredDependencies(getResolvedRequiredDependencies());
        module.setProvidedDependencies(getResolvedProvidedDependencies());
        return module;
    }

    protected Map<String, Object> getMergedParameters() {
        String moduleName = module.getName();
        List<Map<String, Object>> parametersList = parametersChainBuilder.buildModuleChainWithoutDependencies(moduleName);
        addSingularParametersIfNecessary(parametersList);
        Map<String, Object> mergedParameters = PropertiesUtil.mergeProperties(parametersList);
        return mergedParameters;
    }

    protected Map<String, Object> getResolvedProperties(Map<String, Object> mergedParameters) {
        return new PropertiesPlaceholderResolver(propertiesResolverBuilder).resolve(module.getProperties(), mergedParameters, prefix);
    }

    protected Map<String, Object> getResolvedParameters(Map<String, Object> mergedParameters) {
        return new PropertiesPlaceholderResolver(parametersResolverBuilder).resolve(module.getParameters(), mergedParameters, prefix);
    }

    protected List<ProvidedDependency> getResolvedProvidedDependencies() {
        List<ProvidedDependency> resolved = new ArrayList<>();
        for (ProvidedDependency providedDependency : module.getProvidedDependencies()) {
            resolved.add(getProvidedDependencyResolver(providedDependency).resolve());
        }
        return resolved;
    }

    protected ProvidedDependencyPlaceholderResolver getProvidedDependencyResolver(ProvidedDependency providedDependency) {
        return new ProvidedDependencyPlaceholderResolver(module, providedDependency, prefix, parametersChainBuilder,
            propertiesResolverBuilder, singularToPluralMapping);
    }

    protected List<RequiredDependency> getResolvedRequiredDependencies() {
        List<RequiredDependency> resolved = new ArrayList<>();
        for (RequiredDependency requiredDependency : module.getRequiredDependencies()) {
            resolved.add(getRequiredDependencyResolver(requiredDependency).resolve());
        }
        return resolved;
    }

    protected RequiredDependencyPlaceholderResolver getRequiredDependencyResolver(RequiredDependency requiredDependency) {
        return new RequiredDependencyPlaceholderResolver(module, requiredDependency, prefix, parametersChainBuilder,
            propertiesResolverBuilder, parametersResolverBuilder, singularToPluralMapping);
    }

}
