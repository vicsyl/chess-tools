package org.virutor.chess.uci;

@SuppressWarnings("serial")
public class UciProtocolException extends Exception {

	public UciProtocolException(Throwable cause) {
		super(cause);
	}

	public UciProtocolException() {
		super();
	}

	public UciProtocolException(String message, Throwable cause) {
		super(message, cause);
	}

	public UciProtocolException(String message) {
		super(message);
	}

}
