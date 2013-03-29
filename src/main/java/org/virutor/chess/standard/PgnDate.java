package org.virutor.chess.standard;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PgnDate {

	private static final String UNKNOWN = "????.??.??";
	
	private final String originalString;
	private final String stringRepresentation;
	private Date exactDate;
	
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd");
	private static final String PATTERN_STRING = "[\\d\\?]{4}\\.[\\d\\?]{2}\\.[\\d\\?]{2}";
	private static final Pattern PATTERN = Pattern.compile(PATTERN_STRING);
	
	
	public PgnDate(Date date) {
		exactDate = date;
		stringRepresentation = DATE_FORMAT.format(date);		
		originalString = stringRepresentation;
	}

	public PgnDate(String stringRepresentation) {

		originalString = stringRepresentation; 
		Matcher matcher = PATTERN.matcher(stringRepresentation);
		if(!matcher.find()) {			
			this.stringRepresentation = UNKNOWN;
		} else {
			this.stringRepresentation = stringRepresentation;
			if(!stringRepresentation.contains("?")) {
				try {
					exactDate = DATE_FORMAT.parse(stringRepresentation);
				} catch (ParseException e) { /* TODO ignore (?)*/	}
			}
		}
		
	}

	public String toString() {
		return stringRepresentation;
	}
	
	public String getOriginalString() {
		return originalString;
	}

	public Date getExactDate() {
		return exactDate;
	}
	
}
