package me.isaiah.zunozap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import me.isaiah.zunozap.Main;
public class FileManager {
	
	public static File folder = new File(System.getProperty("user.home") + File.separator + Main.name + File.separator);
	public static File Dfolder = new File(folder + File.separator + "Downloads");
  	public static File DPfolder = new File(folder + File.separator + "Saved Pages");
	public static File programsettings = new File(folder + File.separator + "settings.properties");
	public static File OLDprogramsettings = new File(folder +File.separator+ "settings.txt");
	public static File DataFolder = new File(folder + File.separator + "data");
	public static File UASS = new File(folder + File.separator + "style.css");
	
	public FileManager() { System.out.println("[ZunoAPI] File manager loaded!"); }
	
	public static void SetupZunoZapFiles(File folder, File DPfolder, File programsettings, File Dfolder){
    	if (!folder.exists()) {
            folder.mkdir();
        }

        if (!DPfolder.exists()) {
            DPfolder.mkdir();
        }
        
        if (!Dfolder.exists()) {
        	Dfolder.mkdir();
        }
        
        if (!DataFolder.exists()) {
        	DataFolder.mkdir();
        }
        
        if (!UASS.exists()) {
        	try {
				UASS.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
        }
        
        try {
			if (ConfigUtils.viewProp(programsettings.toString(), "enableJavaScript") == null) {
				ConfigUtils.changeProp(programsettings.toString(), "enableJavaScript", "true");
			}
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}
        
        if (!programsettings.exists()) {
        	try {
				programsettings.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
        	
        	try {
				ConfigUtils.changeProp("settings.properties", "enableJavaScript", "true");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
        }
    
        if (OLDprogramsettings.exists()) {
			System.out.println("Removing " + folder + File.separator + "settings.txt to update");
			OLDprogramsettings.delete();
		}
    }
	
	public static void loadManager(){
		@SuppressWarnings("unused")
		FileManager FileMan = new FileManager();
	}
}
