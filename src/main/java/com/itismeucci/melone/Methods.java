package com.itismeucci.melone;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;

public class Methods 
{
	static boolean verbosed;
	static File webdirectory;
	static String filenotfound;

	public Methods(boolean verbose,File WEB_ROOT, String FILE_NOT_FOUND)
	{
		verbosed=verbose;
		webdirectory=WEB_ROOT;
		filenotfound=FILE_NOT_FOUND;
	}

	public byte[] readFileData(File file, int fileLength) throws IOException {
		FileInputStream fileIn = null;
		byte[] fileData = new byte[fileLength];
		
		try {
			fileIn = new FileInputStream(file);
			fileIn.read(fileData);
		} finally {
			if (fileIn != null) 
				fileIn.close();
		}
		
		return fileData;
	}
	
	public String getContentType(String fileRequested) 
	{
		String returncontent="";

		if (fileRequested.endsWith(".htm")  ||  fileRequested.endsWith(".html"))
			returncontent="text/html";

		if(fileRequested.endsWith(".jpg") || fileRequested.endsWith(".jpeg"))
		{
			returncontent="image/jpeg";
		}
		
		if(fileRequested.endsWith(".png"))
		{
			returncontent="image/png";
		}

		if(fileRequested.endsWith(".gif"))
		{
			returncontent="image/gif";
		}

		if(fileRequested.endsWith(".js"))
		{
			returncontent="application/javascript";
		}

		if(fileRequested.endsWith(".css"))
		{
			returncontent="text/css";
		}

		return returncontent;

	}


    public void fileNotFound(PrintWriter out, OutputStream dataOut, String fileRequested) throws IOException {
		File file = new File(webdirectory, filenotfound);
		int fileLength = (int) file.length();
		String content = "text/html";
		byte[] fileData = readFileData(file, fileLength);
		
		out.println("HTTP/1.1 404 File Not Found");
		out.println("Server: Java HTTP Server from SSaurel : 1.0");
		out.println("Date: " + new Date());
		out.println("Content-type: " + content);
		out.println("Content-length: " + fileLength);
		out.println(); // Linea vuota tra header e contenuto
		out.flush(); // flush character output stream buffer
		
		dataOut.write(fileData, 0, fileLength);
		dataOut.flush();
		
		if (verbosed) {
			System.out.println("File " + fileRequested + " not found");
		}
	}
    


}
