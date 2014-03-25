package com.bai.android.data.arcamera;

import java.io.File;
import android.os.Environment;

/**Implements file operations on sdcard and internal storage: check if exists, create, mounted + enough space
 */
public class ManageFileService {
	String outputFile = "Waseda Connect temp";

	//name of the file to save
	private String filePath;

	/**
	 * Constructor
	 * @param appContext application Context
	 * @param fileName name of the file to save
	 */
	public ManageFileService(String filePath){
		this.filePath = filePath;
	}

	/**
	 * Helper method that creates file based on it's path
	 * @return folder to save the file to
	 */
	public File createFileForDownload(){
		// separate folder path into string
		String folder = filePath.substring(0,filePath.lastIndexOf("/"));
		// separate file name into string
		String name = filePath.substring(filePath.lastIndexOf("/"));
		// create folder if it doesn't exist, NOTE not using ".getDir()" because it creates folders "app_NAMEOFFOLDER"
		createFolder(folder);
		// create the file and return it
		File file = new File(folder, name);
		return file;
	}

	/**creates a folder if it doesn't exist*/
	private File createFolder(String folder){
		File file = new File(folder);
		file.mkdirs();
		return file;
	}

	/**
	 * Check if a specified file exists in the device storage
	 */
	public boolean fileExists(){
		File file = new File(filePath);
		if (file.exists()) return true;
		else return false;
	}
	public static boolean fileExists(String filePath){
		File file = new File(filePath);
		if (file.exists()) return true;
		else return false;
	}
	
	/** 
	 * Deletes a file from the file system 
	 */
	public boolean deleteFile(){
		File file = new File(filePath);
		return file.delete();
	}
	
	/** 
	 * Checks if there is enough space on the sdcard and if sdcard is mounted
	 * returns false if any of the above conditions aren't true
	 */
	public boolean cardChecks() {
		// get free space on sdcard in bytes
		File f = new File(Environment.getExternalStorageDirectory().toString());
		long howMuch=f.getFreeSpace();
		// get state of sdcard
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED) && howMuch>80000000) { // need at least max. 80 MB and card mounted
			return true;
		} else {
			return false;
		}
	}
}