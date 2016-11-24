package me.isaiah.zunozap;

import javafx.application.Application;
 
/**
 * An wrapper around javafx's Application class
 * 
 * @author ZunoZap Devs
 * @since ZunoZap v0.1.0
 * @see javafx.application.Application
 * */
public abstract class ZunoApp extends Application {
	
	public static ZunoUtils ZU;    
    
    /**
     * Gets the launch target
     * */
    public static String getLaunchTarget() {
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
    
    public static void main(String args[]) {
        System.out.println("[ZunoApp] Launching wrapped Application {"+getLaunchTarget()+"}");
        ZunoZap.main(args);
    }
    
}
