package com.itismeucci.melone;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Date;


public class main
{
    static final int PORT = 8080;
    static final boolean verbose = true;
    public static void main(String[] args) 
    {
        
		try {
			ServerSocket serverConnect = new ServerSocket(PORT);
			System.out.println("Server started.\nListening for connections on port : " + PORT + " ...\n");
			
			//Sta in ascolto finché l'utente non ferma l'esecuzione del server
			while (true) {
				JavaHTTPServerThread myServer = new JavaHTTPServerThread(serverConnect.accept(),verbose);
				
				if (verbose) {
					System.out.println("Connection opened. (" + new Date() + ")");
				}
				
				//Crea un thread dedicato per gestire la connessione con il client
				Thread thread = new Thread(myServer);
				thread.start();
			}
			
		} catch (IOException e) {
			System.err.println("Server Connection error : " + e.getMessage());
		}
	}    
}
