package fr.utaria.utariabungee.players;

import fr.utaria.utariabungee.util.time.UTime;

import java.sql.Timestamp;
import java.util.regex.Pattern;

public class Permission {

	private final String name;

	private final Pattern pattern;

	private final Object value;

	private UTime expiration;

	public Permission(String name, Object value, Timestamp expiration) {
		this.name = name;
		this.value = value;

		if (expiration != null)
			this.expiration = new UTime(expiration);

		this.pattern = this.generatePattern(name);
	}

	public String getName() {
		return this.name;
	}

	public Object getValue() {
		return this.value;
	}

	public boolean isValid() {
		return this.expiration == null || UTime.now().before(this.expiration);
	}

	public boolean matchPermission(String perm) {
		return this.pattern.matcher(perm).matches();
	}

	private Pattern generatePattern(String name) {
		return Pattern.compile(name.replaceAll("\\.", "\\\\.").replaceAll("\\*", ".+"));
	}

}
