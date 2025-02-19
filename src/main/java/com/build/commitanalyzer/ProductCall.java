package com.build.commitanalyzer;

public class ProductCall {
    private final String className;
    private final String functionName;

    public ProductCall(String className, String functionName) {
        this.className = className;
        this.functionName = functionName;
    }

    public String getClassName() {
        return className;
    }

    public String getFunctionName() {
        return functionName;
    }

    @Override
    public String toString() {
        return className + "<>" + functionName;
    }
}
