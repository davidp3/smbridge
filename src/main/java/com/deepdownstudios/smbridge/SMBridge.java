package com.deepdownstudios.smbridge;

import java.io.IOException;

import org.java_websocket.WebSocketImpl;

public class SMBridge {
	private static int DEFAULT_WS_PORT = 8887;
	private static int DEFAULT_POSIX_PORT = 9296;
	
	private static void showUsage()	{
		System.out.println("Usage: java "+SMBridge.class.getName()+" [ws_port_number posix_port_number]\n\t" + 
				"where ws_port_number is the port for the WebSocket server endpoint (default "+ DEFAULT_WS_PORT +")\n\t" +
				"and posix_port_number is the port for the POSIX socket (default " + DEFAULT_POSIX_PORT+")");
	}
	
	public static void main( String[] args ) throws InterruptedException , IOException {
		WebSocketImpl.DEBUG = true;
		int websocketPort = DEFAULT_WS_PORT; 				// 843 flash policy port
		int posixPort = DEFAULT_POSIX_PORT;
		
		if(args.length == 1 && (args[0].equals("--help") || args[0].equals("-h")))	{
			showUsage();
			return;
		}
			
		if(args.length > 0)		{
			try {
				websocketPort = Integer.parseInt( args[ 0 ] );
				posixPort = Integer.parseInt( args[ 1 ] );
			} catch ( Exception ex ) {
				showUsage();
				return;
			}
		}
		
		final WebSocketEndpoint websocket = new WebSocketEndpoint( websocketPort );
		final PosixEndpoint posix = new PosixEndpoint(posixPort);
		websocket.setConnectedEndpoint(posix);
		posix.setConnectedEndpoint(websocket);

		websocket.start();
		System.out.println( "WebSocket Server started on port: " + websocket.getPort() );
	
		// Since we expect to kill this program to stop it (say, using CTRL+C or 'kill'), we
		// close the ports we opened at shutdown.
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				posix.close();
				websocket.close();
			}
		}));
		
		// Keep serially accepting new POSIX clients until the user kills the app (with CTRL-C or 'kill' or something)
		// Only an exception will break out of this.
		try	{
			while(true)		{
				posix.handleClient();
			}
		} catch(Exception e) {
			System.err.println("Terminating with message: " + e.getMessage());
		}
	}
}
