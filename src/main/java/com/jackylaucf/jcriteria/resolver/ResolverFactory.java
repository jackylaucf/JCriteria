package com.jackylaucf.jcriteria.resolver;

import java.util.Map;

public class ResolverFactory {

    private Map<Class, Resolver> resolverRegistry;

    {
        resolverRegistry.put(String.class, new StringResolver());
    }


}
