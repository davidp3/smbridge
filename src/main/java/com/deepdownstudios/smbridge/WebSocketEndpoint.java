package com.deepdownstudios.smbridge;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collection;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class WebSocketEndpoint  extends WebSocketServer implements Endpoint {

	public WebSocketEndpoint( int port ) throws UnknownHostException {
		this( new InetSocketAddress( port ) );
	}

	public WebSocketEndpoint( InetSocketAddress address ) {
		super( address );
	}

	@Override
	public void onOpen( WebSocket conn, ClientHandshake handshake ) {
		System.out.println( "WebSocket connection established: " + conn.getRemoteSocketAddress().getAddress().getHostAddress() );
	}

	@Override
	public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
		System.out.println( "WebSocket connection ended: " + conn.getRemoteSocketAddress().getAddress().getHostAddress() );
	}

	@Override
	public void onMessage( WebSocket conn, String message ) {
		if(connectedEndpoint != null)	{
			connectedEndpoint.send(message);
		}
	
		//System.out.println( conn + ": " + message );
	}

	/*
	 * DLP: This does not exist in Java-Websocket 1.3.0 (apparently it'll be new in 1.4.0).
	@Override
	public void onFragment( WebSocket conn, Framedata fragment ) {
		System.out.println( "received fragment: " + fragment );
	}
	*/
	
	@Override
	public void onError( WebSocket conn, Exception e ) {
		System.err.println("WebSocket connection error: " + e.getMessage());
		e.printStackTrace();
		if( conn != null ) {
			// some errors like port binding failed may not be assignable to a specific websocket
		}
	}

	/**
	 * Sends <var>text</var> to all currently connected WebSocket clients.
	 * @param text	The String to send across the network.
	 * @throws InterruptedException  When socket related I/O errors occur.
	 */
	public void send( String text ) {
		Collection<WebSocket> con = connections();
		synchronized ( con ) {
			for( WebSocket c : con ) {
				c.send( text );
			}
		}
	}

	Endpoint connectedEndpoint = null;
	
	public void setConnectedEndpoint(Endpoint posix) {
		connectedEndpoint = posix;
	}

	public void close() {
		try {
			stop();
		} catch (IOException e) {
			System.err.println( "I/O error while halting websocket: " + e.getMessage() );
		} catch (InterruptedException e) {
			System.err.println( "Interrupted while halting websocket: " + e.getMessage() );
		}
	}
}
