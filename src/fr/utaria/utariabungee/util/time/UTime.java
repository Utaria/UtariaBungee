package fr.utaria.utariabungee.util.time;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class UTime implements Cloneable {

	private long time;


	public UTime(Timestamp timestamp) {
		this(timestamp.getTime());
	}
	public UTime(String formattedTime) {
		this(TimeParser.stringToTime(formattedTime).getTime());
	}
	public UTime(long timestamp) {
		this.time = timestamp;
	}


	public long      getTime() {
		return this.time;
	}
	public Timestamp getTimestamp() {
		return new Timestamp(this.time);
	}


	public UTime fromNow() {
		return new UTime(this.time - System.currentTimeMillis());
	}
	public UTime toNow() {
		return new UTime(System.currentTimeMillis() - this.time);
	}
	public UTime fromTime(UTime uTime) {
		return new UTime(this.time - uTime.time);
	}
	public UTime toTime(UTime uTime) {
		return new UTime(uTime.time - this.time);
	}

	public boolean after(UTime uTime) {
		return uTime != null && this.time >  uTime.time;
	}
	public boolean afterEq(UTime uTime) {
		return uTime != null && this.time >= uTime.time;
	}
	public boolean before(UTime uTime) {
		return uTime != null && this.time <  uTime.time;
	}
	public boolean beforeEq(UTime uTime) {
		return uTime != null && this.time <= uTime.time;
	}

	public UTime simpleAdd(UTime uTime) {
		this.time += uTime.time;
		return this;
	}
	public UTime simpleSubtract(UTime uTime) {
		this.time -= uTime.time;
		return this;
	}

	public UTime normalize() {
		if (this.time < 86400000) return new UTime(UTime.startOfDay().time + this.time);
		else                      return new UTime(this.time);
	}


	public String toFrenchString() {
		String r = "";

		// Affichage d'un heure particulière
		if (this.time <= 86_400_000) {
			long t = this.time;
			t /= 1000;

			if (t > 3600) {
				int h = (int) Math.floor(t / 3600);

				r += String.format("%02d", h) + "h";
				t -= 3600 * h;
			}
			if (t > 60) {
				int m = (int) Math.floor(t / 60);

				r += String.format("%02d", m) + "m";
				t -= 60 * m;
			}
			if (t > 0)
				r += String.format("%02d", t) + "s";

			return r;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(this.time));

		r += String.format("%02d", cal.get(Calendar.DAY_OF_MONTH)) + " ";

		switch (cal.get(Calendar.MONTH)) {
			case  0: r+= "janvier";   break;
			case  1: r+= "février";   break;
			case  2: r+= "mars";      break;
			case  3: r+= "avril";     break;
			case  4: r+= "mai";       break;
			case  5: r+= "juin";      break;
			case  6: r+= "juillet";   break;
			case  7: r+= "août";      break;
			case  8: r+= "septembre"; break;
			case  9: r+= "octobre";   break;
			case 10: r+= "novembre";  break;
			case 11: r+= "décembre";  break;
		}

		r += " " + cal.get(Calendar.YEAR) + " à ";
		r += String.format("%02d", cal.get(Calendar.HOUR_OF_DAY)) + ":";
		r += String.format("%02d", cal.get(Calendar.MINUTE))      + ":";
		r += String.format("%02d", cal.get(Calendar.SECOND));

		return r;
	}

	@Override
	public UTime clone() {
		return new UTime(this.time);
	}

	public boolean equals(UTime uTime) {
		return this.time == uTime.time;
	}


	public static UTime now() {
		return new UTime(System.currentTimeMillis());
	}

	public static UTime startOfDay() {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));

		cal.setTimeInMillis(System.currentTimeMillis());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);

		return new UTime((cal.getTimeInMillis()/1000)*1000);
	}

	public static UTime fromStartOfDay() {
		return new UTime(System.currentTimeMillis() - UTime.startOfDay().getTime());
	}


}
