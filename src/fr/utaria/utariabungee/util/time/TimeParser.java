package fr.utaria.utariabungee.util.time;

import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeParser {

	public static FormattedTime stringToTime(String time){
		return FormattedTime.fromString(time);
	}
	public static String timeToString(Timestamp timestamp){
		return timeToString(timestamp, false);
	}
	public static String timeToString(Timestamp timestamp, boolean reverse) {
		String r = "";

		long timeRes = timestamp.getTime() - System.currentTimeMillis();
		if(reverse) timeRes = System.currentTimeMillis() - timestamp.getTime();
		timestamp.setTime(timeRes);

		int years  = Integer.parseInt(new SimpleDateFormat("yyyy").format(timestamp)) - 1970;
		int months = Integer.parseInt(new SimpleDateFormat("MM").format(timestamp)) - 1;
		int days   = Integer.parseInt(new SimpleDateFormat("dd").format(timestamp)) - 1;
		int hours  = Integer.parseInt(new SimpleDateFormat("HH").format(timestamp)) - 1;
		int mins   = Integer.parseInt(new SimpleDateFormat("mm").format(timestamp));

		if(years > 0){
			if(years > 1) r += years + " ans ";
			else r += years + " an ";
		}
		else if(months > 0){
			r += months + " mois ";
		}
		else if(days > 0){
				if(days > 1) r += days + " jours ";
				else r += days + " jour ";
			}
			else if(hours > 0){
					if(hours > 1) r += hours + " heures ";
					else r += hours + " heure ";
				}
				else if(mins > 0){
						if(mins > 1) r += mins + " minutes ";
						else r += mins + " minute ";
					}else r += "moins d'une minute ";

		if(r.length() > 1) r = r.substring(0, r.length() - 1);
		return r;
	}
	public static String timesToString(Timestamp timestamp, Timestamp timestamp2) {
		String r = "";

		long timeRes = timestamp.getTime() - timestamp2.getTime();

		timestamp.setTime(timeRes);

		int years  = Integer.parseInt(new SimpleDateFormat("yyyy").format(timestamp)) - 1970;
		int months = Integer.parseInt(new SimpleDateFormat("MM").format(timestamp)) - 1;
		int days   = Integer.parseInt(new SimpleDateFormat("dd").format(timestamp)) - 1;
		int hours  = Integer.parseInt(new SimpleDateFormat("HH").format(timestamp)) - 1;
		int mins   = Integer.parseInt(new SimpleDateFormat("mm").format(timestamp));

		if(years > 0){
			if(years > 1) r += years + " ans ";
			else r += years + " an ";
		}
		else if(months > 0){
			r += months + " mois ";
		}
		else if(days > 0){
				if(days > 1) r += days + " jours ";
				else r += days + " jour ";
			}
			else if(hours > 0){
					if(hours > 1) r += hours + " heures ";
					else r += hours + " heure ";
				}
				else if(mins > 0){
						if(mins > 1) r += mins + " minutes ";
						else r += mins + " minute ";
					}else r += "moins d'une minute ";

		if(r.length() > 1) r = r.substring(0, r.length() - 1);
		return r;
	}
	public static String timeToShortString(Timestamp timestamp, boolean reverse) {
		String r = "";

		long timeRes = timestamp.getTime() - System.currentTimeMillis();
		if(reverse) timeRes = System.currentTimeMillis() - timestamp.getTime();

		timeRes /= 1000;

		if (timeRes > 86400) {
			long days = timeRes / 86400;
			if (days > 1) r += days + " jours ";
			else          r += days + " jour ";

			timeRes -= days * 86400;
		}

		if (timeRes > 3600) {
			long hours = timeRes / 3600;
			if (hours > 1) r += hours + " heures ";
			else           r += hours + " heure ";

			timeRes -= hours * 3600;
		}

		if (timeRes > 60) {
			long mins = timeRes / 60;
			if(mins > 1) r += mins + " minutes ";
			else         r += mins + " minute ";
		}

		if(r.length() > 1) r = r.substring(0, r.length() - 1);
		return r;
	}

	public static String secToHumanReadableString(int sec) {
		String r = "";

		if( sec > 3600 * 24 ) r += "§c" + (sec / (3600 * 24)) + "j ";
		sec -= (sec / (3600 * 24)) * 3600 * 24;

		if( sec > 3600 ) r += (sec / 3600) + "h ";
		sec -= (sec / 3600) * 3600;

		if( sec >   60 ) r += (sec /   60) + "m ";
		sec -= (sec /   60) * 60;

		if( sec >    0 ) r += sec + "s";

		return r;
	}


	private static boolean isNumeric(String s) {
		return s.matches("\\d+");
	}
	private static boolean typeIsValid(String type) {
		return type.equalsIgnoreCase("a") || type.equalsIgnoreCase("o")
				|| type.equalsIgnoreCase("s") || type.equalsIgnoreCase("j")
				|| type.equalsIgnoreCase("h") || type.equalsIgnoreCase("m");
	}


	public static class FormattedTime {

		private long time;

		private FormattedTime(String[][] timeArgs) {
			this.time   = -1;

			this._generateTime(timeArgs);
		}

		public long    getTime  () { return this.time;   }


		public boolean smallerThan(String patternTime) {
			FormattedTime f2 = FormattedTime.fromString(patternTime);
			return this.getTime() < f2.getTime();
		}
		public boolean biggerThan(String patternTime) {
			FormattedTime f2 = FormattedTime.fromString(patternTime);
			return this.getTime() > f2.getTime();
		}


		private void _generateTime(String[][] timeArgs) {
			if (this.time > 0L) return;
			this.time = 0;

			for (String[] arg : timeArgs) {
				double factor = FormattedTime.getFactorByType(arg[1]);
				if (factor == -1 || !StringUtils.isNumeric(arg[0])) continue;
				int number = Integer.parseInt(arg[0]);

				this.time += number * factor;
			}

			this.time *= 1000;
		}



		static FormattedTime fromString(String time) {
			// Détection du formattage de l'heure
			Pattern hourPat = Pattern.compile("([0-9]*h[0-9]+)");
			if (hourPat.matcher(time).find()) time += "m";

			// Détection du formattage des minutes/secondes
			Pattern minPat = Pattern.compile("([0-9]*m[0-9]+)");
			if (minPat.matcher(time).find()) time += "s";

			Pattern        pattern  = Pattern.compile("([0-9]*[aowjhms])");
			Matcher        matcher  = pattern.matcher(time);
			List<String[]> listArgs = new ArrayList<>();

			while (matcher.find()) {
				for (int i = 1; i <= matcher.groupCount(); i++) {
					String grp = matcher.group(i);
					String num = grp.substring(0, grp.length() - 1);
					String fac = grp.substring(grp.length() - 1, grp.length());

					if (!typeIsValid(fac) || !StringUtils.isNumeric(num)) continue;

					listArgs.add(new String[] { num, fac });
				}
			}

			return new FormattedTime(listArgs.toArray(new String[0][0]));
		}

		static double        getFactorByType(String type) {
			switch (type) {
				case "a":              // Année
					return 31_556_925.96;
				case "o":              // Mois
					return 2_629_743.834;
				case "w":              // Semaine
					return 604_800;
				case "j":              // Jour
					return 86_400;
				case "h":              // Heure
					return 3_600;
				case "m":              // Minute
					return 60;
				case "s":              // Seconde
					return 1;
				default:
					return -1;
			}
		}

	}

}
