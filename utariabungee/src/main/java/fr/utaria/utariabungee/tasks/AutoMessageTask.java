package fr.utaria.utariabungee.tasks;

import fr.utaria.utariabungee.Config;
import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.util.PlayerUtil;
import fr.utaria.utariabungee.util.UUtil;
import fr.utaria.utariadatabase.database.Database;
import fr.utaria.utariadatabase.database.DatabaseManager;
import fr.utaria.utariadatabase.result.DatabaseSet;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.concurrent.TimeUnit;

public class AutoMessageTask implements Runnable {

	private static final String TABLE_MESSAGES = "automessages";

	private static final int LINE_WIDTH = 70;

	private int current;

	public AutoMessageTask() {
		this.current = -1;

		ProxyServer.getInstance().getScheduler().schedule(UtariaBungee.getInstance(), this, 1, Config.autoMessageDelay, TimeUnit.MINUTES);
	}

	@Override
	public void run() {
		Database db = DatabaseManager.getDB("global");

		// On récupère le nombre de messages depuis la base de données
		int messagesSize = db.select().from(TABLE_MESSAGES).findAll().size();
		if (messagesSize == 0) return;

		// On mets à jour l'indice en construisant le prochain indice
		this.current = (this.current + 1) % messagesSize;

		// On va chercher le message correspondant au nouvel indice
		DatabaseSet set = db.select().from(TABLE_MESSAGES).where("id = ?").attributes(String.valueOf(this.current + 1)).find();
		if (set == null) return;

		// On formate correctement le message automatique
		String message = set.getString("message");
		message = UUtil.formatMessageColors(message);

		// Et on l'envoi à tout le monde ! :P
		// (on oublie pas de formater le messager avant)
		String[] parts = this.splitMessage(message);

		this.broadcast(" ");
		for (String part : parts)
			this.broadcast(part);
		this.broadcast(" ");
	}

	private String[] splitMessage(String mess) {
		StringBuilder sb = new StringBuilder(mess);

		// Le message est déjà découpé
		if (mess.contains("\n")) return mess.split("\\n");

		int i = 0;
		while (i + LINE_WIDTH < sb.length() && (i = sb.lastIndexOf(" ", i + LINE_WIDTH)) != -1)
			sb.replace(i, i + 1, "\n");

		return sb.toString().split("\\n");
	}

	private void broadcast(String message) {
		// On l'envoie aussi à la console
		ProxyServer.getInstance().getConsole().sendMessage(new TextComponent(message));

		for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers())
			PlayerUtil.sendCenteredMessage(player, message);
	}

}
