package com.itismeucci.melone;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.StringTokenizer;


//Ogni connessione del client viene gestita in un thread dedicato
public class JavaHTTPServerThread implements Runnable{ 
	
	static final File WEB_ROOT = new File(".");
	static final String DEFAULT_FILE = "index.html";
	static final String FILE_NOT_FOUND = "404.html";
	static final String METHOD_NOT_SUPPORTED = "not_supported.html";
	// port to listen connection
	//porta per l'ascolto della connessione
	
	// verbose mode
	static boolean verbosed;

	
	//Connessione del client tramite la classe Socket
	private Socket connect;
	
	private Methods user= new Methods(verbosed, WEB_ROOT, FILE_NOT_FOUND);


	public JavaHTTPServerThread(Socket c, boolean verbose) {
		connect = c;
		verbosed=verbose;
	}
	


	@Override
	public void run() {
		//Gestione della connessione client
		BufferedReader in = null; PrintWriter out = null; BufferedOutputStream dataOut = null;
		String fileRequested = null;
		
		try {
			//Legge i caratteri dal client attraverso l'input stream dentro il socket
			in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
			//Prende l'output stream del carattere al client (per gli header)
			out = new PrintWriter(connect.getOutputStream());
			//Prende output stream binario al client (per dati richiesti)
			dataOut = new BufferedOutputStream(connect.getOutputStream());
			
			//Prende la prima linea della richiesta dal client
			String input = in.readLine();
			//Analizza la richiesta tramite un token di una stringa
			StringTokenizer parse = new StringTokenizer(input);
			String method = parse.nextToken().toUpperCase(); // Prende il metodo HTTP dal client
			//Prende il file richiesto
			fileRequested = parse.nextToken().toLowerCase();
			
			//Controllo metodi GET e HEAD
			if (!method.equals("GET")  &&  !method.equals("HEAD")) {
				if (verbosed) {
					System.out.println("501 Not Implemented : " + method + " method.");
				}
				
				//Ritorna il file non supportato al client
				File file = new File(WEB_ROOT, METHOD_NOT_SUPPORTED);
				int fileLength = (int) file.length();
				String contentMimeType = "text/html";
				//Legge il contenuto da far ritornare al client
				byte[] fileData = user.readFileData(file, fileLength);
					
				// Manda gli header HTTO con i dati al client
				out.println("HTTP/1.1 501 Not Implemented");
				out.println("Server: Java HTTP Server from SSaurel : 1.0");
				out.println("Date: " + new Date());
				out.println("Content-type: " + contentMimeType);
				out.println("Content-length: " + fileLength);
				out.println(); // Linea vuota tra header e contenuto
				out.flush(); // flush character output stream buffer
				// file
				dataOut.write(fileData, 0, fileLength);
				dataOut.flush();
				
			} else {
				// Metodo GET o HEAD
				if (fileRequested.endsWith("/")) {
					fileRequested += DEFAULT_FILE;
				}
				
				File file = new File(WEB_ROOT, fileRequested);
				int fileLength = (int) file.length();
				String content = user.getContentType(fileRequested);
				
				if (method.equals("GET")) { //Metodo GET, quindi ritorna il contenuto
					byte[] fileData = user.readFileData(file, fileLength);
					
					// send HTTP Headers
					out.println("HTTP/1.1 200 OK");
					out.println("Server: Java HTTP Server from SSaurel : 1.0");
					out.println("Date: " + new Date());
					out.println("Content-type: " + content);
					out.println("Content-length: " + fileLength);
					out.println(); // Linea vuota tra header e contenuto
					out.flush(); // flush character output stream buffer
					
					dataOut.write(fileData, 0, fileLength);
					dataOut.flush();
				}
				
				if (verbosed) {
					System.out.println("File " + fileRequested + " of type " + content + " returned");
				}
				
			}
			
		} catch (FileNotFoundException fnfe) {
			try {
				user.fileNotFound(out, dataOut, fileRequested);
			} catch (IOException ioe) {
				System.err.println("Error with file not found exception : " + ioe.getMessage());
			}
			
		} catch (IOException ioe) {
			System.err.println("Server error : " + ioe);
		} finally {
			try {
				in.close();
				out.close();
				dataOut.close();
				connect.close(); //Chiusura connessione socket
			} catch (Exception e) {
				System.err.println("Error closing stream : " + e.getMessage());
			} 
			
			if (verbosed) {
				System.out.println("Connection closed.\n");
			}
		}
		
		
	}
	

}