package fr.utaria.utariabungee.listeners;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class TabCompletionListener implements Listener {

	@EventHandler
	public void onTabComplete(TabCompleteEvent e) {
		String       cursor      = e.getCursor();

		String command  = this.getCommand(cursor);
		int    argIndex = this.getArgumentIndex(cursor);
		String currArg  = this.getCurrentArgument(cursor);


		// En fonction de la commande tapée, et de l'argument sur lequel on se situe, on modifie les sggestions
		switch (command) {
			case "message":
			case "msg":
			case "tell":
			case "m":
				if (argIndex != 1) return;

				for (ProxiedPlayer player : BungeeCord.getInstance().getPlayers())
					if(player.getName().indexOf(currArg) == 0)
						e.getSuggestions().add(player.getName());

				break;

			case "tempmute":
			case "tempban":
			case "lookup":
			case "kick":
			case "ban":
			case "unmute":
			case "unban":
				if (argIndex != 1) return;

				for (ProxiedPlayer player : BungeeCord.getInstance().getPlayers())
					if(player.getName().indexOf(currArg) == 0)
						e.getSuggestions().add(player.getName());

				break;
		}
	}



	private String getCommand(String cursor) {
		String[] words = cursor.split(" ");

		if ( words.length == 0 ) return cursor.replace("/", "");
		else                     return words[0].replace("/", "");
	}

	private int getArgumentIndex(String cursor) {
		return cursor.split(" ").length - 1;
	}

	private String getCurrentArgument(String cursor) {
		int index = this.getArgumentIndex(cursor);
		return cursor.split(" ")[index];
	}

}
