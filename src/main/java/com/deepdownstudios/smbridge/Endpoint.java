package com.deepdownstudios.smbridge;

public interface Endpoint {
	public void send(String message);
	public void setConnectedEndpoint(Endpoint posix);
	public void close();
}