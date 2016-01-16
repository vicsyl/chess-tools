package org.virutor.chess.standard;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.virutor.chess.model.Position;

/**
 * TODO check when setup tag is present but FEN is not...
 * @author vaclav
 *
 */
public class SetUpHandler extends AbstractKeyCheckingPropertyHandler implements PropertyHandler {

	private static final Logger LOG = LogManager.getLogger(SetUpHandler.class);
	
	public static final String SET_UP = "SetUp"; 
	
	public static SetUpHandler INSTANCE = new SetUpHandler();
	
	public static boolean shouldAddSetupAndFenTags(PgnGame pgnGame) {
		
		return 	pgnGame.getGame().getHeadGameNode().getPosition().fullMoveClock != 1 ||			
				pgnGame.getGame().getHeadGameNode().getPosition().colorToMove == Position.COLOR_BLACK; 

	} 
	
	private SetUpHandler() {
		super(SET_UP);
	}
	
	@Override
	public void parse(String key, String value, PgnGame pgnGame) {
		checkKey(key); 
		try {
			int i = Integer.parseInt(value);
			if(i == 0 || i == 1) {
				pgnGame.properties.put(key, i);
			} else {
				LOG.debug("SetUp is neither 0 nor 1, ignoring");				
			}
		} catch (Exception e) {
			LOG.debug("SetUp tag not a number, ignoring");
		}
	}

	@Override
	public void format(String key, PgnGame pgnGame, StringBuilder sb) {
		checkKey(key);
		pgnGame.appendProperty(key, pgnGame.properties.get(SET_UP).toString(), sb);
	}

	@Override
	public void chechBeforeParse(PgnGame pgnGame) {
			
		//TODO read specification !!!
		if(shouldAddSetupAndFenTags(pgnGame)) {
			pgnGame.properties.put(SET_UP, new Integer(1));
		}
	}
}
