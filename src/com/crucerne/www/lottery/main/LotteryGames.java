/*
 * AUTHOR: Alno "Crucerne" Lau
 * DATE CREATED:
 * LAST MODIFIED:
 * DESCRIPTION: Enumeration for lottery games. Enumerated lottery games so far:
 * 1) Powerball
 * 2) MegaMillion
 */

package com.crucerne.www.lottery.main;

public enum LotteryGames {

	POWERBALL("Powerball", "pb"), MEGAMILLION("MegaMillion", "mm");

	private String name;
	private String command;

	LotteryGames(String name, String command) {
		this.name = name;
		this.command = command;
	}

	public String toString() {
		return "[" + command + "] " + name;
	}
}
