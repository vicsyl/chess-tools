package org.virutor.chess.standard;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.virutor.chess.standard.time.IncrementalTimeControl;
import org.virutor.chess.standard.time.MovesInSecondsTimeControl;
import org.virutor.chess.standard.time.SandClockTimeControl;
import org.virutor.chess.standard.time.SuddenDeathTimeControl;
import org.virutor.chess.standard.time.TimeControl;

public class PgnTimeControlHandler extends AbstractKeyCheckingPropertyHandler {

	public static final String TIME_CONTROL = "TimeControl"; 
	
	public static final PgnTimeControlHandler INSTANCE = new PgnTimeControlHandler(); 
	
	private static final String UNKNOWN_STRING = "?";
	private static final String NO_TIME_CONTROL_STRING = "-";
	
	private PgnTimeControlHandler() {
		super(TIME_CONTROL);
		
	}	

	private static final Pattern MOVES_IN_SECONDS_PATTERN = Pattern.compile("^([1-9]\\d*)/([1-9]\\d*)$");  
	private static final Pattern SUDDEN_DEATH_PATTERN = Pattern.compile("^[1-9]\\d*$");  
	private static final Pattern INCREMENTAL = Pattern.compile("^([1-9]\\d*)\\+([1-9]\\d*|0)$");  
	private static final Pattern SANDCLOCK = Pattern.compile("^\\*([1-9]\\d*)$");  
			
	@Override
	public void parse(String key, String value, PgnGame pgnGame) {		
		
		checkKey(key);		
		TimeControl timeControl = parse(value);
		if(!pgnGame.properties.containsKey(TIME_CONTROL)) {
			pgnGame.properties.put(TIME_CONTROL, new ArrayList<TimeControl>());
		}
		((List<TimeControl>)(pgnGame.properties.get(TIME_CONTROL))).add(timeControl);
		
	}
	
	private static TimeControl parse(String value) {
		
		if(StringUtils.isEmpty(value)) {
			return TimeControl.UNKNOWN;
		}
		if(UNKNOWN_STRING.equals(value)) {
			return TimeControl.UNKNOWN;
		}
		if(NO_TIME_CONTROL_STRING.equals(value)) {
			return TimeControl.NO_CONTROL;
		}

		try { 
			Matcher matcher = MOVES_IN_SECONDS_PATTERN.matcher(value);
			if(matcher.find()) {
				return new MovesInSecondsTimeControl(Integer.valueOf(matcher.group(1)), Integer.valueOf(matcher.group(2)));
			}
			matcher = SUDDEN_DEATH_PATTERN.matcher(value);
			if(matcher.find()) {
				return new SuddenDeathTimeControl(Integer.valueOf(matcher.group(0)));
			}		
			matcher = INCREMENTAL.matcher(value);
			if(matcher.find()) {
				return new IncrementalTimeControl(Integer.valueOf(matcher.group(1)), Integer.valueOf(matcher.group(2)));
			}
			matcher = SANDCLOCK.matcher(value);
			if(matcher.find()) {
				return new SandClockTimeControl(Integer.valueOf(matcher.group(1)));
			}
		} catch(Exception e) {	}
		
		return TimeControl.UNKNOWN;
	}
	
	@Override
	public void format(String key, PgnGame pgnGame, StringBuilder sb) {
		checkKey(key);
		
		List<TimeControl> timeControls = (List<TimeControl>)pgnGame.properties.get(TIME_CONTROL);
		if(timeControls == null) {
			return;
		}
		for(TimeControl timeControl : timeControls) {
			pgnGame.appendProperty(TIME_CONTROL, format(timeControl), sb);	
		}
	}
	
	//TODO rewrite this - add the method to TimeControl class?
	public static String format(TimeControl timeControl) {
		
		if(timeControl == TimeControl.NO_CONTROL) {
			return NO_TIME_CONTROL_STRING;
		}
		if(timeControl == TimeControl.UNKNOWN) {
			return UNKNOWN_STRING;
		}
		if(timeControl instanceof SandClockTimeControl) {
			return "*" + ((SandClockTimeControl)timeControl).getSandclockSeconds();
		}
		if(timeControl instanceof IncrementalTimeControl) {
			IncrementalTimeControl inc = (IncrementalTimeControl)timeControl;
			return inc.getBase() + "+" + inc.getIncrement();
		}
		if(timeControl instanceof MovesInSecondsTimeControl) {
			MovesInSecondsTimeControl movesInSec = (MovesInSecondsTimeControl)timeControl;
			return movesInSec.getMoves() + "/" + movesInSec.getSeconds();
		}
		if(timeControl instanceof SuddenDeathTimeControl) {
			SuddenDeathTimeControl suddenDeath = (SuddenDeathTimeControl)timeControl;
			return "" + suddenDeath.getSeconds();
		}
		
		throw new IllegalArgumentException("Unknown type of time control: " + timeControl.getClass());

	}
	
}
