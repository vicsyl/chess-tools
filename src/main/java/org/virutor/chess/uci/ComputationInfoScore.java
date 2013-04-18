package org.virutor.chess.uci;

import org.virutor.chess.uci.ComputationInfo.InfoPropertyParser;

public class ComputationInfoScore {
	
	
	private static final String CP = "cp";
	private static final String MATE = "mate";
	private static final String LOWER_BOUND = "lowerbound";
	private static final String UPPER_BOUND = "upperbound";
	
	public static class ComputationInfoScorePropertyParser implements InfoPropertyParser<ComputationInfoScore> {

		@Override
		public ComputationInfoScore parse(StringBuilder restOfTheLine) {
						
			ComputationInfoScore ret = new ComputationInfoScore();
			
			while(restOfTheLine != null && restOfTheLine.length() > 0) {
				
						
				String original = ComputationInfo.getWordWhiteSpacePrefixed(restOfTheLine);
				String trimmed = original.trim();
							
				if(CP.equals(trimmed)) {
					restOfTheLine.delete(0, original.length());
					Integer cp = ComputationInfo.INTEGER_PROPERTY_PARSER.parse(restOfTheLine);
					if(cp != null) {
						ret.centiPawns = cp; 
					} else {
						break;
					}
				} else if(MATE.equals(trimmed)) {
					restOfTheLine.delete(0, original.length());
					Integer cp = ComputationInfo.INTEGER_PROPERTY_PARSER.parse(restOfTheLine);
					if(cp != null) {
						ret.mated = cp; 
					} else {
						break;
					}					
				} else if(LOWER_BOUND.equals(trimmed)) {
					restOfTheLine.delete(0, original.length());
					ret.isLowerBound = true;
				} else if(UPPER_BOUND.equals(trimmed)) {
					restOfTheLine.delete(0, original.length());
					ret.isUpperBound = true;
				} else {
					break;	
				}
				
			
			}
		
			return ret;
		}			

	}

	private static final int NOT_MATED_MAGIC_VALUE = Integer.MAX_VALUE;

	private int centiPawns;
	private int mated = NOT_MATED_MAGIC_VALUE;
	private boolean isUpperBound;
	private boolean isLowerBound;

	public int getCentiPawns() {
		return centiPawns;
	}

	public int getMated() {
		return mated;
	}

	public boolean isUpperBound() {
		return isUpperBound;
	}

	public boolean isLowerBound() {
		return isLowerBound;
	}

	public boolean isMated() {
		return mated != NOT_MATED_MAGIC_VALUE;
	}

	public boolean isCentipawns() {
		return !isMated();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if(mated != NOT_MATED_MAGIC_VALUE) {			
			sb.append(mated < 0 ? "gets mated in " : "will mate in ");
			sb.append(mated);
			if(isLowerBound ^ isUpperBound) {
				boolean b1 = isLowerBound;
				boolean b2 = mated > 0;
				sb.append((b1 ^ b2) ? " or less" : " or more");
				sb.append("(experimental feature)");
			}
			
		} else {
			if(isLowerBound ^ isUpperBound) {
				sb.append(isLowerBound ? "<=" : ">=");
			}			
			if(centiPawns == 0) {
				sb.append("0.00");
			} else {				
				int absCp = Math.abs(centiPawns);	
				int perc = (absCp % 100);
				String percents = perc < 10 ? ("0" +perc) : ("" + perc); 
				sb.append(centiPawns > 0 ? "+" : "-");				
				sb.append((absCp / 100) + "." + percents);				
			}
		}		
		return sb.toString();
	}
	
	
}
