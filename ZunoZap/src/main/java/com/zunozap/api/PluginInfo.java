package com.zunozap.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.TYPE)
public @interface PluginInfo {

    public String name();
    public String description() default "No description";
    public String version();
    public double minBrowserVersion() default 0.7;

}
