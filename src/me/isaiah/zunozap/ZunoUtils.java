package me.isaiah.zunozap;

/**
 * ZunoZap's API (The ZunoAPI used in ZunoZap v0.1.0+)
 * 
 * @author Isaiah Patton
 */
public interface ZunoUtils {
	
	/**
	 * The version of the program
	 */
	public abstract String getVersion();

	/**
	 * The styles of the buttons! 
	 */
	public abstract String getButtonCSS();
	
	/**
	 * Launched Code
	 * @deprecated
	 */
	@Deprecated
	static void runAtStart_DEPRECATED() {}
}
