package com.jackylaucf.jcriteria.resolver;

public interface Resolver {
    String resolve(Object object);
    Resolver setPattern(String pattern);
}
