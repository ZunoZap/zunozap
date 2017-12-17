package me.isaiah.zunozap;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.TYPE)
public @interface Info {
    public String name() default "Unknown";
    public String version() default "Unknown";
    public String updateURL() default "https://raw.githubusercontent.com/ZunoZap/zunozap/master/LATEST-RELEASE.md";
    public boolean enableGC() default true;
    public UniversalEngine.Engine engine();
}