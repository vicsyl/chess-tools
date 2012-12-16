package org.virutor.chess.model.ui;

import java.util.Date;

import org.virutor.chess.model.Game;
import org.virutor.chess.model.Game.Result;
import org.virutor.chess.model.Position;

public class GameData {

	
	public static class PlayerData {
		private String name;
		private int elo;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public int getElo() {
			return elo;
		}
		public void setElo(int elo) {
			this.elo = elo;
		}
		public PlayerData(String name, int elo) {
			this.name = name;
			this.elo = elo;
		}
	}

	
	private PlayerData[] playerData = new PlayerData[2];
	private String event;
	private String site;
	private Date date;
	private int round;
	private Game.Result result;

	public GameData() {
		playerData[Position.COLOR_WHITE] = new PlayerData("White", 1250); 
		playerData[Position.COLOR_BLACK] = new PlayerData("Black", 1250);
		date = new Date();
		result = Result.UNRESOLVED;
	}
	
	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}



	public String getSite() {
		return site;
	}



	public void setSite(String site) {
		this.site = site;
	}



	public Date getDate() {
		return date;
	}



	public void setDate(Date date) {
		this.date = date;
	}



	public int getRound() {
		return round;
	}



	public void setRound(int round) {
		this.round = round;
	}



	public Game.Result getResult() {
		return result;
	}



	public void setResult(Game.Result result) {
		this.result = result;
	}



	public void setPlayerData(PlayerData[] playerData) {
		this.playerData = playerData;
	}



	public PlayerData[] getPlayerData() {
		return playerData;
	}
	
	
	
}
