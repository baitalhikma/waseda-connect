package com.bai.android.data.arcamera;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.os.Environment;



/**
 * Download a file via the Internet 
 */
public class DownloadContent {
	private URL downloadUrl;
	private int file;
	private String localFileLocation;
	private String unzipLocation;
	
	/**
	 * Takes the url of the file selected for download
	 * @param downloadUrl - url of the file to download
	*/
	// constructor
	public DownloadContent(int fileWhich){
		String dlFilename = null;
		try {
			this.file = fileWhich;
			if (file == Constant.DownloadFileCode.TESS) {
				// tesseract trained data
				unzipLocation = Constant.DATA_PATH + Constant.TESS_FOLDER; // dir to unzip
				localFileLocation = Constant.DATA_PATH+Constant.TESS_FOLDER+Constant.TESS_ZIP_FILENAME; // location of the zip file (after download)
				dlFilename = Constant.TESS_FOLDER+Constant.TESS_ZIP_FILENAME; // filename of archive on server with subdir (e.g. "tessdata/jpn.zip")
			} 
			else if (file == Constant.DownloadFileCode.DICT) {
				// dictionary db
				unzipLocation = Constant.DATA_PATH + Constant.DICT_FOLDER;
				localFileLocation = Constant.DATA_PATH+Constant.DICT_FOLDER+Constant.DICT_ZIP_FILENAME;
				dlFilename = Constant.DICT_FOLDER+ Constant.DICT_ZIP_FILENAME;
			}
			else {
			}
			this.downloadUrl 	= new URL(Constant.SERVER_LOC+dlFilename);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * The method that carries out the download and saving of the file to the according path 
	 */
	public void downloadFile() throws Exception{
			ManageFileService manageFileService = new ManageFileService(localFileLocation);
			if (manageFileService.cardChecks()==true) {
				HttpParams httpParameters = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParameters, 20000);
				HttpConnectionParams.setSoTimeout(httpParameters, 15000);
				HttpConnectionParams.setTcpNoDelay(httpParameters, true);
	
				DefaultHttpClient client = new DefaultHttpClient(httpParameters);
				
				HttpGet request = new HttpGet(downloadUrl.toString());
				
				//get path for file save and prepare output stream to write the file;
			
				FileOutputStream fileOutput			= new FileOutputStream(manageFileService.createFileForDownload());
				
				// Get the response
				HttpResponse response = client.execute(request);
				
				//start getting the data from the url input stream
				InputStream inputStream = response.getEntity().getContent();
				
				//create a buffer
				byte[] buffer = new byte[1024];
				int bufferLength = 0; //used to store a temporary size of the buffer
	
				//read through the input buffer and write the contents to the file
				while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
					//add the data in the buffer to the file in the file output stream
					fileOutput.write(buffer, 0, bufferLength);
				}
				//close the output stream when done
				fileOutput.close();
			} else {
				//TODO toast error
			}
			if (manageFileService.fileExists()==true) {
				String zipFile = localFileLocation;
				Decompress d = new Decompress(zipFile, unzipLocation); 
				d.unzip();
				manageFileService.deleteFile();	
			} else {
				//TODO toast error
			}
	}
}