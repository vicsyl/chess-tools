package org.virutor.chess.standard;

import org.virutor.chess.model.Position;

public class FenPropertyHandler extends AbstractKeyCheckingPropertyHandler {

	public static final String FEN = "FEN";
	
	public static final FenPropertyHandler INSTANCE = new FenPropertyHandler();
	
	private FenPropertyHandler() {
		super(FEN);
	}
	
	@Override
	public void parse(String key, String value, PgnGame pgnGame) {
		checkKey(key);
		Position position = new Position();
		try { 
			FenUtils.setFen(value, position);
			pgnGame.getGame().setUpStartingPosition(position);
			pgnGame.properties.put(key, value);
		} catch (Exception e) {
			throw new IllegalArgumentException("Illegal FEN string : " + value, e);
		}				
	}
	
	@Override
	public void chechBeforeParse(PgnGame pgnGame) {

		//TODO read specification
		if(SetUpHandler.shouldAddSetupAndFenTags(pgnGame) ||
		   new Integer(1).equals(pgnGame.getProperties().get(SetUpHandler.SET_UP))) {
			pgnGame.properties.put(FEN, FenUtils.positionToFen(pgnGame.getGame().getHeadGameNode().getPosition()));
		}

	}

}
