package com.deepdownstudios.smbridge;

public interface Endpoint {
	public void process(String message);
	public void setConnectedEndpoint(Endpoint posix);
	public void close();
}