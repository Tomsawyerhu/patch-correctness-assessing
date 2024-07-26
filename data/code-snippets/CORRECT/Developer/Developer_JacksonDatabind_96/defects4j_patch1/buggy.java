public class test {
    protected void _addExplicitAnyCreator(DeserializationContext ctxt,
            BeanDescription beanDesc, CreatorCollector creators,
            CreatorCandidate candidate)
        throws JsonMappingException
    {
        // Looks like there's bit of magic regarding 1-parameter creators; others simpler:
        if (1 != candidate.paramCount()) {
            // Ok: for delegates, we want one and exactly one parameter without
            // injection AND without name
            int oneNotInjected = candidate.findOnlyParamWithoutInjection();
            if (oneNotInjected >= 0) {
                // getting close; but most not have name
                if (candidate.paramName(oneNotInjected) == null) {
                    _addExplicitDelegatingCreator(ctxt, beanDesc, creators, candidate);
                    return;
                }
            }
            _addExplicitPropertyCreator(ctxt, beanDesc, creators, candidate);
            return;
        }
        AnnotatedParameter param = candidate.parameter(0);
        JacksonInject.Value injectId = candidate.injection(0);
        PropertyName paramName = candidate.explicitParamName(0);
        BeanPropertyDefinition paramDef = candidate.propertyDef(0);

        // If there's injection or explicit name, should be properties-based
        boolean useProps = (paramName != null) || (injectId != null);
        if (!useProps && (paramDef != null)) {
            // One more thing: if implicit name matches property with a getter
            // or field, we'll consider it property-based as well

            // 25-May-2018, tatu: as per [databind#2051], looks like we have to get
            //    not implicit name, but name with possible strategy-based-rename
//            paramName = candidate.findImplicitParamName(0);
            paramName = candidate.findImplicitParamName(0);
            useProps = (paramName != null) && paramDef.couldSerialize();
        }
        if (useProps) {
            SettableBeanProperty[] properties = new SettableBeanProperty[] {
                    constructCreatorProperty(ctxt, beanDesc, paramName, 0, param, injectId)
            };
            creators.addPropertyCreator(candidate.creator(), true, properties);
            return;
        }
        _handleSingleArgumentCreator(creators, candidate.creator(), true, true);

        // one more thing: sever link to creator property, to avoid possible later
        // problems with "unresolved" constructor property
        if (paramDef != null) {
            ((POJOPropertyBuilder) paramDef).removeConstructors();
        }
    }
}