package com.jackylaucf.jcriteria.resolver;

class BigDecimalResolver implements Resolver{

    private String pattern;

    @Override
    public String resolve(Object object) {
        if(this.pattern!=null){

        }
        return null;
    }

    @Override
    public Resolver setPattern(String pattern) {
        this.pattern = pattern;
        return this;
    }


}
