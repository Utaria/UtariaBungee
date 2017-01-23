package fr.utaria.utariabungee.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class TimeParser {
	
	private static ArrayList<String> errors = new ArrayList<>();

	public static long stringToTime(String time){
		errors.clear();
		
		long ts = -1;
		FormattedTime ft = getFormattedTime(time);
		
		if(ft == null) return -1;
		
		//if(ft.getNumber() < 1){
		//	errors.add("Vous devez entrer un nombre supérieur à 1.");
		//	return -1;
		//}
		
		double factor = getFactorByType(ft.getType());
		if(factor == -1){
			errors.add("Nous n'avons pas pu faire la conversion. Erreur système.");
			return -1;
		}
		
		long rsTime = (long) (ft.getNumber() * factor);
		ts = rsTime * 60 * 1000;
		
		return ts;
	}
	public static String timeToString(Timestamp timestamp){
		return timeToString(timestamp, false);
	}
	public static String timeToString(Timestamp timestamp, boolean reverse){
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
	public static String timesToString(Timestamp timestamp, Timestamp timestamp2){
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
	
	
	private static FormattedTime getFormattedTime(String time){
		String type = time.substring(time.length()-1, time.length());
		String number = time.substring(0, time.length()-1);
		
		if(!isNumeric(number)){
			errors.add("La date a mal été formattée.");
			return null;
		}
		
		if(!typeIsValid(type)){
			errors.add("Le type de date envoyé n'est pas correct.");
			return null;
		}
		
		return new FormattedTime(type, Integer.parseInt(number));
	}
	
	
	private static boolean isNumeric(String s) {
	    return s.matches("\\d+");
	}
	private static boolean typeIsValid(String type){
		return type.equalsIgnoreCase("a") || type.equalsIgnoreCase("m")
				|| type.equalsIgnoreCase("s") || type.equalsIgnoreCase("j")
				|| type.equalsIgnoreCase("h") || type.equalsIgnoreCase("i");
	}
	private static double getFactorByType(String type){
		switch (type) {
			case "a":
				return 525948.766;
			case "m":
				return 43829.0639;
			case "s":
				return 10080;
			case "j":
				return 1440;
			case "h":
				return 60;
			case "i":
				return 1;
			default:
				return -1;
		}
	}
	public static ArrayList<String> getErrors(){
		return errors;
	}
	
	private static class FormattedTime{
		
		private String type;
		private Integer number;
		
		public FormattedTime(String type, Integer number){
			this.type = type;
			this.number = number;
		}
		
		public String getType(){
			return this.type;
		}
		public Integer getNumber(){
			return this.number;
		}
		
		
	}
	
}
