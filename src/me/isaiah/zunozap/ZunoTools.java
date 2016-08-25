package me.isaiah.zunozap;

import javafx.scene.control.Button;

public class ZunoTools extends ZunoAPI {
	public static void addStyle(Button t, String e) {
		t.getStylesheets().add(e);
	}
	
	public static void addStyleCSSFileToButtons(String e) {
		ZunoTools.addStyle(Main.googleBar, e);
		addStyle(Main.sourcebutton, e);
	  	addStyle(Main.aboutbutton, e);
	  	addStyle(Main.aboutAPIbutton, e);
	  	addStyle(Main.GOButton, e);
	  	addStyle(Main.BackButton, e);
	  	addStyle(Main.ForwardButton, e);
	  	Main.addressBar.getStylesheets().add(e);
	}
}
