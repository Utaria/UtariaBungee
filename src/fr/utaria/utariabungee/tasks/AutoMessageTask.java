package fr.utaria.utariabungee.tasks;

import fr.utaria.utariabungee.Config;
import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.database.DatabaseSet;
import fr.utaria.utariabungee.utils.PlayerUtils;
import fr.utaria.utariabungee.utils.Utils;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.concurrent.TimeUnit;

public class AutoMessageTask implements Runnable {

	private static final String TABLE_MESSAGES = "bungee_messages";
	private static final int    LINE_WIDTH     = 70;

	private int current;


	public AutoMessageTask() {
		this.current = -1;

		BungeeCord.getInstance().getScheduler().schedule(UtariaBungee.getInstance(), this, 1, Config.autoMessageDelay, TimeUnit.MINUTES);
	}


	@Override
	public void run() {
		// On récupère le nombre de messages depuis la base de données
		int messagesSize = UtariaBungee.getDatabase().find(TABLE_MESSAGES).size();

		// On mets à jour l'indice en construisant le prochain indice
		this.current = (this.current + 1) % messagesSize;

		// On va chercher le message correspondant au nouvel indice
		DatabaseSet set = UtariaBungee.getDatabase().findFirst(TABLE_MESSAGES, DatabaseSet.makeConditions(
			"id", String.valueOf(this.current + 1)
		));

		if (set == null) return;

		// On formate correctement le message automatique
		String message = set.getString("message");
		message = Utils.formatMessageColors(message);

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
		BungeeCord.getInstance().getConsole().sendMessage(TextComponent.fromLegacyText(message));

		for (ProxiedPlayer player : BungeeCord.getInstance().getPlayers())
			PlayerUtils.sendCenteredMessage(player, message);
	}

}
