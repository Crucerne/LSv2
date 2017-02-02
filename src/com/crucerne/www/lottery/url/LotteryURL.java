package com.crucerne.www.lottery.url;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;

public class LotteryURL implements Iterable<String> {

	private LinkedList<String> lottery_urls = new LinkedList<String>();

	// inner class for fetching HTML from the URL
	private class URLIterator implements Iterator<String> {

		private int index = 0;

		@Override
		public boolean hasNext() {
			return index < lottery_urls.size();
		}

		@Override
		public String next() {
			StringBuilder sb = new StringBuilder();
			try {
				URL url = new URL(lottery_urls.get(index));
				BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
				String line = null;
				while ((line = br.readLine()) != null)
				{
					sb.append(line);
					sb.append("\n");
				}
				br.close();
			} catch (Exception e) {
				System.out.println(
						"ERROR: Failed to connect to URL; ensure internet connection is working properly and try again.");
			}
			index++;
			return sb.toString();
		}

		@Override
		public void remove() {
			lottery_urls.remove(index);
		}
	}

	// Constructor
	public LotteryURL() {
		lottery_urls.add("http://www.powerball.com/powerball/pb_numbers.asp");
		lottery_urls.add("http://www.powerball.com/megamillions/mm_numbers.asp");
	}

	@Override
	public Iterator<String> iterator() {
		return new URLIterator();
	}

}
