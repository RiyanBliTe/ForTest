import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * This class stores information from the PURCHASE command
 * */
public class ObjectOI {
	Date date;
	String cost, name;

	public ObjectOI(String date, String cost, String name) throws ParseException {
		SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd");
		this.date = sFormat.parse(date);
		this.cost = cost;
		this.name = name;
	}
	public String getStringDate() {
		SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd");
		String date = sFormat.format(this.date);
		return date;
	}
	public String getYear() {
		SimpleDateFormat sFormat = new SimpleDateFormat("yyyy");
		String year = sFormat.format(date);
		return year;
	}
	@Override
	public String toString() {
		return this.name + " " + this.cost;
	}
	public double getPrice() {
		String price = this.cost;
		return Double.valueOf(price.substring(0, price.indexOf(" ")));
	}
	public String getValut() {
		String valut = this.cost;
		return valut.substring(valut.indexOf(" ") + 1, valut.length());	
		}
}
