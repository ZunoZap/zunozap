 package me.isaiah.zunozap;

/**
 * An wrapper around javafx's Application class
 * 
 * @author ZunoZap Devs
 * @since ZunoZap 1.0
 * @see javafx.application.Application
 * */
public abstract class ZunoApp extends javafx.application.Application {
	
	public static ZunoUtils ZU;    
    
    /**
     * Gets the launch target
     * */
    public String getLaunchTarget() {
    	return "javafx.application.Application";
    }
    
    /**
     * Is ZunoAPI included? 
     */
    public boolean isAPIincluded() {
    	try {
    		@SuppressWarnings("unused")
			Class<?> t = Class.forName("me.isaiah.zunozap.ZunoAPI"); 
    		return true;
    	} catch (Exception NotIncluded){
    		return false;
    	}
	}
    
}
