package com.deepdownstudios.smbridge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class PosixEndpoint implements Endpoint {
	private Endpoint connectedEndpoint;
	private ServerSocket serverSocket;
	private Socket clientSocket;

	public PosixEndpoint(int posixPort) throws IOException {
		serverSocket = new ServerSocket(posixPort);
	}

	public void setConnectedEndpoint(Endpoint endpoint) {
		this.connectedEndpoint = endpoint;
	}

	public void process(String message) {
		if(out == null)		// no client connected
			return;
		out.print(message);
		out.flush();
	}

	private PrintWriter out;
	private BufferedReader in;

	public void handleClient() throws IOException {
		System.out.println( "POSIX Server Socket listening on port: " + serverSocket.getLocalPort() );
		try {
			clientSocket = serverSocket.accept();
	        out = new PrintWriter(clientSocket.getOutputStream(), true);
	        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch (IOException e) {
			System.err.println("ERROR: Exception while waiting for POSIX connection: " + e.getMessage());
			throw e;
		}
		
		System.out.println( "POSIX connection established: " + clientSocket.getInetAddress().toString() );
		
		String data;
        try {
        	data = in.readLine();
			while (data != null) {			// null indicates end-of-stream
				if(connectedEndpoint != null)	{
					connectedEndpoint.process(data);
				}
				System.out.println("Sent message from POSIX to WebSocket: " + data);
				data = in.readLine();
			}
		} catch (IOException e) {
			System.err.println("ERROR: Exception while listening to POSIX connection: " + e.getMessage());
			throw e;
		}
	}

	public void close() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			System.err.println("Error closing POSIX connection.");
		}
	}
}
