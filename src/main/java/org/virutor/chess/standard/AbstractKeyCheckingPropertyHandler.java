package org.virutor.chess.standard;



public abstract class AbstractKeyCheckingPropertyHandler implements PropertyHandler {

	private String key;

	protected AbstractKeyCheckingPropertyHandler(String key) {
		if(key == null) {
			throw new NullPointerException();
		}
		this.key = key;
	}

	protected void checkKey(String key) {
		if(!this.key.equals(key)) {
			throw new IllegalArgumentException("Handler for key '" + this.key + "' called for another key('" + key + "')");
		}
	}

	@Override
	public void format(String key, PgnGame pgnGame, StringBuilder sb) {
		checkKey(key);
		pgnGame.appendProperty(key, pgnGame.properties.get(key).toString(), sb);
	}

	@Override
	public void chechBeforeParse(PgnGame pgnGame) {	} 
}
