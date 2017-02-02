package com.crucerne.www.lottery.simulator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import com.crucerne.www.lottery.info.ILottery;

public final class PowerballSimulator implements ILottery {

	// Scanner object that will be assigned during class object instantiation
	private Scanner scanner;
	// container for the winning ticket numbers
	private Powerball winning_powerball = new Powerball();
	// container for randomly generated tickets
	private volatile ConcurrentSkipListSet<Powerball> random_tickets = new ConcurrentSkipListSet<Powerball>();
	// placeholder for manual tickets container

	// recurrence limit for differentiating duplicate tickets and unique tickets
	// (default of 3)
	private int recurrence_limit = 3;
	// file name for saving Powerball global settings
	private final String POWERBALL_SETTINGS = "PowerballSettings.bin";
	// filename for saving winning Powerball(s)
	private final String POWERBALL_WIN = "PowerballWin.bin";
	// file name for saving randomly generated Powerball tickets
	private final String POWERBALL_RANDOM = "PowerballRandom.bin";
	// file name for saving manually entered Powerball tickets
	// placeholder

	// class for creating Powerball ticket objects
	class Powerball implements Serializable, Comparable<Powerball>, Runnable {

		// serial version last modified:
		private static final long serialVersionUID = 5012598642160227358L;
		// possible value ranges for Powerball's white and red balls
		public static final int WHITE_MIN = 1;
		public static final int WHITE_MAX = 69;
		public static final int RED_MIN = 1;
		public static final int RED_MAX = 26;

		// Powerball fields
		private TreeSet<Integer> p_white_numbers = new TreeSet<Integer>();
		private int p_red_number = 6;

		// #REGION Powerball CONSTRUCTORS

		/*
		 * DESCRIPTION: Constructors for Powerball objects. 1) The constructor
		 * that takes no arguments will instantiate a new Powerball object and
		 * assign a random white and red numbers to it. 2) Constructors that
		 * takes arguments(maximum of 6) will instantiate a Powerball object and
		 * assign the argument integer values as its white and red numbers with
		 * the 6th argument(if provided) being its red number. Any missing
		 * arguments will assign the default values of 2, 3, 4, 5, and 6
		 * respectively. 3) The constructor that takes another Powerball object
		 * as argument will make a copy of other object by copying the other
		 * Powerball's white and red numbers.
		 */
		public Powerball() {
			while (this.p_white_numbers.size() != 5) {
				this.p_white_numbers.add(ThreadLocalRandom.current().nextInt(WHITE_MAX) + WHITE_MIN);
			}
			this.p_red_number = ThreadLocalRandom.current().nextInt(RED_MAX) + RED_MIN;
		}

		public Powerball(Powerball pball) {
			this.p_white_numbers = new TreeSet<Integer>(pball.p_white_numbers);
			this.p_red_number = new Integer(pball.p_red_number);
		}

		public Powerball(int first) {
			this(first, 2, 3, 4, 5, 1);
		}

		public Powerball(int first, int second) {
			this(first, second, 3, 4, 5, 1);
		}

		public Powerball(int first, int second, int third) {
			this(first, second, third, 4, 5, 1);
		}

		public Powerball(int first, int second, int third, int fourth) {
			this(first, second, third, fourth, 5, 1);
		}

		public Powerball(int first, int second, int third, int fourth, int fifth) {
			this(first, second, third, fourth, fifth, 1);
		}

		public Powerball(int first, int second, int third, int fourth, int fifth, int sixth) {
			this.p_white_numbers.add(first);
			this.p_white_numbers.add(second);
			this.p_white_numbers.add(third);
			this.p_white_numbers.add(fourth);
			this.p_white_numbers.add(fifth);
			this.p_red_number = sixth;
		}
		// #END CONSTRUCTORS

		@Override
		public void run() {
			random_tickets.add(this);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((p_white_numbers == null) ? 0 : p_white_numbers.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Powerball other = (Powerball) obj;
			if (p_white_numbers == null) {
				if (other.p_white_numbers != null)
					return false;
			}
			Powerball temp1 = new Powerball(this);
			temp1.p_white_numbers.retainAll(other.p_white_numbers);
			if (temp1.p_white_numbers.size() < recurrence_limit)
				return false;
			return true;
		}

		@Override
		public String toString() {
			return String.format("%20s [%2s]", p_white_numbers, p_red_number);
		}

		@Override
		public int compareTo(Powerball pball) {
			int count = 0;
			Iterator<Integer> thisIterator = this.p_white_numbers.iterator();
			Iterator<Integer> otherIterator = pball.p_white_numbers.iterator();
			int thisInt = 0;
			int otherInt = 0;
			int recur_limit = recurrence_limit;
			while (count != recur_limit) {
				thisInt = thisIterator.next();
				otherInt = otherIterator.next();
				if (thisInt < otherInt) {
					return -1;
				} else if (thisInt > otherInt) {
					return 1;
				} else {
					count++;
				}
			}
			return 0;
		}

	}

	// PowerballSimulator Constructors
	public PowerballSimulator(Scanner scanner) {
		this.scanner = scanner;
	}

	// #REGION ILottery OVERRIDES

	@Override
	public void showMenu() {
		System.out.println("[POWERBALL LOTTERY]");
		System.out.printf(
				"%7s Shows Randomly Generated Tickets\n"
				+ "%7s Save Randomly Generated Tickets\n"
				+ "%7s Deletes Current and Saved Randomly Generated Tickets\n"
				+ "%7s Shows Manually Entered Tickets\n"
				+ "%7s Save Manually Entered Tickets\n"
				+ "%7s Deletes Manually Entered Tickets\n"
				+ "%7s Retrieves Winning Lottery Numbers Online\n"
				+ "%7s Check Tickets for Any Winning Tickets\n"
				+ "%7s Show and Change Recurrence Limit\n"
				+ "%7s List More Details for Options\n",
				"[ShowR]", "[SaveR]", "[DelR]", "[ShowM]", "[SaveM]", "[DelM]", "[WinO]", "[WinC]", "[Recur]", "[Help]");

	}

	@Override
	public void menuSelection() {
		// Scanner scanner = new Scanner(System.in);
		StringBuilder choice = new StringBuilder();
		do {
			showMenu();
			choice.setLength(0);
			choice.append(scanner.nextLine().toString());
			//
			if (choice.toString().equals("pb")) {

			}
			//
			else if (choice.toString().equals("mm")) {
			}
			// quit to lottery game selection
			else if (choice.toString().equals("q")) {
				System.out.println("Quitting Powerball lottery...");
				// scanner.close();
			}
			// invalid choice entered by user
			else {
				System.out.println("Invalid input detected; please enter a valid command from the list:");
			}

		} while (!choice.toString().equals("q"));
	}

	@Override
	public void generateRandomWinningTicket() {
		if (winning_powerball != null) {
			winning_powerball.p_white_numbers.clear();
		}
		winning_powerball = new Powerball();
	}

	@Override
	public void showWinningTicket() {
		System.out.println("Winning Powerball: " + winning_powerball);
	}

	@Override
	public void saveWinningTicket() {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(POWERBALL_WIN, false))) {
			oos.writeObject(winning_powerball);
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: File not found!");
		} catch (IOException e) {
			System.out.println("ERROR: Unexpected issue occurred while writing winning Powerball to file!");
		}
	}

	@Override
	public void loadWinningTicket() {
		File file = new File(POWERBALL_WIN);
		if (file.exists()) {
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(POWERBALL_WIN))) {
				winning_powerball = (Powerball) ois.readObject();
			} catch (FileNotFoundException e) {
				System.out.println("ERROR: File not found!");
			} catch (IOException e) {
				System.out.println("ERROR: Unexpected issue occurred while reading winning Powerball from file!");
			} catch (ClassNotFoundException e) {
				System.out.println("ERROR: Failed to load data from file!");
			}
		}
	}

	@Override
	public void retrieveWinningTicketOnline() {
		
		
	}

	@Override
	public void setSaveRecurrenceLimit(int new_limit) {
		recurrence_limit = new_limit;
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(POWERBALL_SETTINGS, false))) {
			oos.writeInt(recurrence_limit);
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: File not found!");
		} catch (IOException e) {
			System.out.println("ERROR: Unexpected issue occurred while writing recurrence limit to file!");
		}
	}

	@Override
	public void loadRecurrenceLimit() {
		File file = new File(POWERBALL_SETTINGS);
		if (file.exists()) {
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(POWERBALL_SETTINGS))) {
				recurrence_limit = ois.readInt();
			} catch (FileNotFoundException e) {
				System.out.println("ERROR: File not found!");
			} catch (IOException e) {
				System.out.println("ERROR: Unexpected issue occurred while reading recurrence limit from file!");
			}
		}
	}

	@Override
	public void generateParallelRandomTickets(int number_of_tickets, int tickets_remaining) {
		ExecutorService executor = Executors.newFixedThreadPool(tickets_remaining);
		for (int i = 0; i < tickets_remaining; i++) {
			executor.submit(new Powerball());
		}
		executor.shutdown();
		try {
			executor.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			System.out.println("ERROR: Thread pool exceeded allotted time.");
		}
		if (random_tickets.size() < number_of_tickets) {
			generateParallelRandomTickets(number_of_tickets, number_of_tickets - random_tickets.size());
		}
	}

	@Override
	public void showRandomTickets() {
		if (!random_tickets.isEmpty()) {
			int count = 1;
			for (Powerball pball : random_tickets) {
				System.out.printf("%-5d: %s\n", count, pball);
				count++;
			}
			// System.out.println("The current number of tickets is: " +
			// random_tickets.size());
		} else {
			System.out.println("No tickets generated yet!");
		}
	}

	@Override
	public void saveRandomTickets() {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(POWERBALL_RANDOM, false))) {
			// oos.writeInt(Powerball.s_powerball_tickets.size());
			oos.writeObject(random_tickets);
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: File not found!");
		} catch (IOException e) {
			System.out.println("ERROR: Unexpected issue occurred while writing randomly generated tickets to file!");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void loadRandomTickets() {
		File file = new File(POWERBALL_RANDOM);
		if (file.exists()) {
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(POWERBALL_RANDOM))) {
				random_tickets = (ConcurrentSkipListSet<Powerball>) ois.readObject();
			} catch (FileNotFoundException e) {
				System.out.println("ERROR: File not found!");
			} catch (IOException e) {
				System.out.println(
						"ERROR: Unexpected issue occurred while reading randomly generated tickets from file!");
			} catch (ClassNotFoundException e) {
				System.out.println("ERROR: Failed to load data from file!");
			}
		}
	}

	@Override
	public void deleteRandomTickets() {
		random_tickets.clear();
		saveRandomTickets();
	}

	@Override
	public void inputManualTickets() {
		// TODO Auto-generated method stub

	}

	@Override
	public void showManualTickets() {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteManualTickets() {
		// TODO Auto-generated method stub

	}

	@Override
	public void checkTickets() {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadSettings() {
		loadRecurrenceLimit();
		loadWinningTicket();
		loadRandomTickets();
	}

	// #END OVERRIDES

}
