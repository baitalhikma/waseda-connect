package com.bai.android.data.arcamera;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Decompress {
	private String zipFile;
	private String location;
	/*
	 * Unzips archive
	 * @param zip - the filename of the archive
	 * @param location - directory to unzip to
	 */
	//constructor
	public Decompress(String zip, String loc) {
		zipFile = zip;
		location = loc;
		// creates a directory with the name of the location that was passed in to unzip (extract to...)
		dirChecker("");
	}
	/*
	 * Unzips the archive recursively
	 * if the file is a directory, then create that in the unzip location
	 * reads to a buffer to speed it up significantly
	 */
	public void unzip() {
		try {
			FileInputStream fis = new FileInputStream(zipFile);
			ZipInputStream zis = new ZipInputStream(fis);
			ZipEntry ze = null;
			while ((ze = zis.getNextEntry()) != null) {
				if (ze.isDirectory()) {
					dirChecker(ze.getName());
				} else {
					int size;
	                byte[] buffer = new byte[2048];

	                FileOutputStream outStream = new FileOutputStream(location + ze.getName());
	                BufferedOutputStream bufferOut = new BufferedOutputStream(outStream, buffer.length);

	                while((size = zis.read(buffer, 0, buffer.length)) != -1) {
	                    bufferOut.write(buffer, 0, size);
	                }
	                bufferOut.flush();
	                bufferOut.close();
					zis.closeEntry();
				}
			}
			zis.close();
		} catch (Exception e) {
		}

	}
	
	// check if file in archive is a directory
	private void dirChecker(String dir) {
		File f = new File(location + dir);
		// if it's a directory, then create it during decompression
		if (!f.isDirectory()) {
			f.mkdirs();
		}
	}
}