/*
 * AUTHOR: Alno "Crucerne" Lau
 * DATE CREATED:
 * 04/18/16
 * LAST MODIFIED:
 * 04/28/16
 * DESCRIPTION: 
 */

package com.crucerne.www.lottery.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;


import com.crucerne.www.lottery.info.ILottery;
import com.crucerne.www.lottery.simulator.PowerballSimulator;
import com.crucerne.www.lottery.url.LotteryURL;

public class Lottery {

	// container for storing all supported lottery games
	private static LinkedList<LotteryGames> games = new LinkedList<LotteryGames>();
	// file name for saving lottery global settings
	private static final String GLOBAL_SETTINGS = "GlobalSettings.bin";

	public static void main(String[] args) {

		// LotteryURL debug
		LotteryURL lottery_urls = new LotteryURL();
		Iterator<String> url_iterator = lottery_urls.iterator();
		String html = url_iterator.next();
//		for (String html : lottery_urls) {
			System.out.println(html);
//		}
		
		// debug
		// ILottery lottery = new PowerballSimulator();
		// lottery.loadSettings();
		// lottery.generateParallelRandomTickets(1000, 1000);
		// lottery.showRandomTickets();
		// lottery.showWinningTicket();

		// container for all supported lottery games
		// LotteryGames[] games = {LotteryGames.POWERBALL,
		// LotteryGames.MEGAMILLION};

		// LotteryGames game_choice = games[1];
		// System.out.println(game_choice);
		// System.out.println(count);

		// load global settings
		loadLotteryGames();
		if (games.isEmpty()) {
			setDefaultLotteryGames();
		}

		lotterySelection();

	}

	/*
	 * DESCRIPTION: Method that reads data from the "GlobalSettings.bin" file
	 * and loads previously selected lottery list(if any) into the "games"
	 * variable.
	 */
	@SuppressWarnings("unchecked")
	private static void loadLotteryGames() {
		File file = new File(GLOBAL_SETTINGS);
		if (file.exists()) {
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(GLOBAL_SETTINGS))) {
				games = (LinkedList<LotteryGames>) ois.readObject();
			} catch (FileNotFoundException e) {
				System.out.println("ERROR: File not found!");
			} catch (IOException e) {
				System.out.println("ERROR: Unexpected issue occurred while reading lottery games from file!");
			} catch (ClassNotFoundException e) {
				System.out.println("ERROR: Failed to load data from file!");
			}
		}
	}

	/*
	 * DESCRIPTION: Method that saves the current lottery list order by writing
	 * the object, "games" onto the file, "GlobalSettings.bin".
	 */
	private static void saveLotteryGames() {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(GLOBAL_SETTINGS, false))) {
			oos.writeObject(games);
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: File not found!");
		} catch (IOException e) {
			System.out.println("ERROR: Unexpected issue occurred while writing lottery games to file!");
		}
	}

	/*
	 * DESCRIPTION: Method that sets the default lottery list and order. This
	 * method should only be called during the first-time usage of the
	 * application.
	 */
	private static void setDefaultLotteryGames() {
		// debug
		System.out.println("Setting default lottery games");
		games.add(LotteryGames.POWERBALL);
		games.add(LotteryGames.MEGAMILLION);
	}

	/*
	 * DESCRIPTION: Method that takes input from the user and based upon the
	 * input, the method will perform 3 possible procedures: 1) The input is a
	 * valid command will initialize a corresponding lottery game object, enable
	 * the user to select methods for that lottery game, and saves the current
	 * lottery list into a file. 2) The input is an invalid command and will
	 * prompt the user of the error and show the supported lottery games again.
	 * 3) The input is "e", which signifies the user has chosen to exit the
	 * application.
	 */
	private static void lotterySelection() {
		ILottery lottery;
		Scanner scanner = new Scanner(System.in);
		StringBuilder lottery_choice = new StringBuilder();
		do {
			showLotteryList();
			lottery_choice.setLength(0);
			lottery_choice.append(scanner.nextLine().toString());
			// Powerball lottery chosen
			if (lottery_choice.toString().equals("pb")) {
				reorderLotteryList(LotteryGames.POWERBALL);
				saveLotteryGames();
				lottery = new PowerballSimulator(scanner);
				lottery.menuSelection();
			}
			// MegaMillion lottery chosen
			else if (lottery_choice.toString().equals("mm")) {
				reorderLotteryList(LotteryGames.MEGAMILLION);
				saveLotteryGames();
			}
			// exit the application
			else if (lottery_choice.toString().equals("e")) {
				System.out.println("Exiting application...");
				scanner.close();
			}
			// invalid choice entered by user
			else {
				System.out.println("Invalid input detected; please enter a valid command from the list:");
			}
		} while (!lottery_choice.toString().equals("e"));
	}

	/*
	 * DESCRIPTION: Method that shows the user a list of supported lottery games
	 * with its corresponding command string. The order of the list depends on
	 * which lottery game(s) the user has played previously.
	 */
	private static void showLotteryList() {
		System.out.println(
				"Enter the command letter(s) contained within the brackets to select the corresponding lottery game:");
		int count = 1;
		for (LotteryGames lg : games) {
			System.out.printf("%2d.%s\n", count, lg);
			count++;
		}
		System.out.printf("%7s Exit Application\n", "[e]");
	}

	/*
	 * DESCRIPTION: Method that reorders the lottery games list. Selecting a
	 * lottery game from the selection menu will move the selected lottery game
	 * to the beginning of the list.
	 */
	private static void reorderLotteryList(LotteryGames lg) {
		games.remove(lg);
		games.push(lg);
	}
}
