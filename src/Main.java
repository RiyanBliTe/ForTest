import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

public class Main {
	
	static String posSite = "http://data.fixer.io/api/latest?access_key=12980c0ae7e6ead0d3751c68a9bf109d";
	
	/* List of objects ObjectOI class */
	static ArrayList<ObjectOI> objects;
	/* List of values that are taken from the site http://fixer.io */
	static Map<String, Double> map;
	/* Dates for comparison */
	static Set<String> dates;
	
	static Scanner scanner;
	
	static boolean runner;
	
	public static void main(String[] arg) throws ParseException{
		scanner = new Scanner(System.in);
		objects = new ArrayList<ObjectOI>();
		dates = new TreeSet<String>();
		map = connection(posSite);
		
		while (runner) {
			String command = scanner.nextLine();
			command = command.trim(); 
			if (!command.startsWith("all") && (command.startsWith("purchase ") ||
					command.startsWith("clear ") || command.startsWith("report ")))
				try {
					switch (command.substring(0, command.indexOf(" "))){
					case "purchase":
						purchaseCommand(command.substring(command.indexOf(" ") + 1, command.length()));
						break;
					case "clear":
						clearCommand(command.substring(command.indexOf(" ") + 1, command.length()));
						break;
					case "report":
						reportCommand(command.substring(command.indexOf(" ") + 1, command.length()));
						break;
					default: System.out.println("Uncorrect command!");
				}
				} catch (Exception e) {
					System.out.println("Uncorrect arguments of command! Try again.");
				}	
			else if (command.startsWith("all")) allCommand();
			else System.out.println("Uncorrect command!");
		}
	}

	private static void reportCommand(String yearValut) {
		String year = yearValut.substring(0, yearValut.indexOf(" "));
		String valut = yearValut.substring(yearValut.indexOf(" ") + 1, yearValut.length());
		double usdSummary = 0.0;
		for (ObjectOI oi: objects) {
			if (oi.getYear().equals(year) && map != null) {
				usdSummary += (oi.getPrice() / map.get(oi.getValut()));
			}
		}
		System.out.println(map.get(valut.toUpperCase()) * usdSummary);
	}

	private static void clearCommand(String date) {
		boolean is = false;
		Iterator<ObjectOI> iterator = objects.iterator();
		while (iterator.hasNext()) {
			ObjectOI objectOI = iterator.next();
			if (objectOI.getStringDate().equals(date)) iterator.remove();
		}
		Iterator<String> iterators = dates.iterator();
		while (iterators.hasNext()) {
			String dates = iterators.next();
			if (dates.equals(date)) {
				iterators.remove();
				is = true;
			}
		}
		if (is == false) System.out.println("There is no such date!");
		out();
	}

	private static void allCommand() {
		out();
	}

	private static void purchaseCommand(String values) throws ParseException {
		String valDate = values.substring(0, values.indexOf(" "));
		SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = sFormat.parse(valDate);
		String yearS, dayS, mS;
		sFormat = new SimpleDateFormat("yyyy");
		yearS = sFormat.format(date);
		sFormat = new SimpleDateFormat("MM");
		mS = sFormat.format(date);
		sFormat = new SimpleDateFormat("dd");
		dayS = sFormat.format(date);
		
		String cost = values.substring(values.indexOf(" ") + 1, values.lastIndexOf(" "));
		String price = cost.substring(0, cost.indexOf(" ")), valut = cost.substring(cost.indexOf(" ") + 1, cost.length());
		
		String name = values.substring(values.lastIndexOf(" ") + 1, values.length());
		if (Integer.valueOf(yearS) > 0000 && Integer.valueOf(yearS) <= 9999 &&
				Integer.valueOf(mS) >= 1 && Integer.valueOf(mS) <= 12 && 
				Integer.valueOf(dayS) >=1 && Integer.valueOf(dayS) <= 31) {
			if (price.matches("[0-9]+") && map.containsKey(valut.toUpperCase())) {	
					objects.add(new ObjectOI(valDate, price.concat(" "+valut.toUpperCase()), name));
					dates.add(valDate);
					out();
			} else System.out.println("Uncorrect type of price.");
		} else System.out.println("Uncorrect date!");
	}
	
	private static void out() {
		for (String sd: dates) {
			System.out.println(sd);
			for (ObjectOI oi: objects) {
				if (oi.getStringDate().equals(sd)) System.out.println(oi);
			}
			System.out.println();
		}
	}
	
	private static Map<String, Double> connection(String pos) {
		try {
			URL url = new URL(pos);
			
			JsonParser jParser = Json.createParser(url.openStream());
			JsonParser.Event event;
			
			Map<String, Double> rates = new HashMap<String, Double>();
			ArrayList<Object> keyValue = new ArrayList<Object>();
					
			boolean is = false;
			while (jParser.hasNext()) {
				event = jParser.next();
					if (event == Event.KEY_NAME && jParser.getString().equals("rates")) {
						is = true; 
						continue;
					}
					if (is) {
						switch(event) {
							case KEY_NAME:
								keyValue.add(jParser.getString());
								break;
							case VALUE_NUMBER:
								keyValue.add(jParser.getString());
								break;
						default:
							break;
						}
					}
			}
			for (int i = 1; i < keyValue.size(); i+=2) {
				rates.put(keyValue.get(i - 1).toString(), Double.valueOf(keyValue.get(i).toString()));
			}
			runner = true;
			return rates;
		} catch (Exception e) {
			System.out.println("Can`t connect with http://fixer.io");
			runner = false;
			return null;
		}
	}
}
