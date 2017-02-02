package com.crucerne.www.lottery.info;

public interface ILottery {

	public void showMenu();
	
	public void menuSelection();

	public void generateRandomWinningTicket();

	public void showWinningTicket();

	public void saveWinningTicket();

	public void loadWinningTicket();

	public void retrieveWinningTicketOnline();

	public void setSaveRecurrenceLimit(int new_limit);
	
	public void loadRecurrenceLimit();

	public void generateParallelRandomTickets(int number_of_tickets, int tickets_remaining);

	public void showRandomTickets();

	public void saveRandomTickets();

	public void loadRandomTickets();

	public void deleteRandomTickets();

	public void inputManualTickets();

	public void showManualTickets();

	public void deleteManualTickets();

	public void checkTickets();

	public void loadSettings();

}
