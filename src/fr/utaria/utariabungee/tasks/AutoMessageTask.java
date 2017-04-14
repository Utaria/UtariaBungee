package fr.utaria.utariabungee.tasks;

import fr.utaria.utariabungee.Config;
import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.database.DatabaseSet;
import fr.utaria.utariabungee.utils.Utils;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.concurrent.TimeUnit;

public class AutoMessageTask implements Runnable {

	private static final String TABLE_MESSAGES = "bungee_messages";

	private int current;

	public AutoMessageTask() {
		this.current = -1;

		BungeeCord.getInstance().getScheduler().schedule(UtariaBungee.getInstance(), this, 1, 5, TimeUnit.MINUTES);
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
		BungeeCord.getInstance().broadcast(TextComponent.fromLegacyText(Config.prefix + message));
	}

}
