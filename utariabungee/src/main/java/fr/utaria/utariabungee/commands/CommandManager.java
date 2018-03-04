package fr.utaria.utariabungee.commands;

import fr.utaria.utariabungee.AbstractManager;
import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.commands.message.MessageCommand;
import fr.utaria.utariabungee.commands.message.ResponseCommand;
import fr.utaria.utariabungee.commands.message.SpyCommand;
import fr.utaria.utariabungee.commands.moderation.*;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CommandManager extends AbstractManager {

	public CommandManager() {
		super(UtariaBungee.getInstance());
	}

	@Override
	public void initialize() {
		// Message
		new MessageCommand();
		new ResponseCommand();
		new SpyCommand();

		// Modération
		new BanCommand();
		new KickCommand();
		new LookupCommand();
		new TempbanCommand();
		new TempMuteCommand();
		new UnbanCommand();
		new UnmuteCommand();

		new RestartCommand();
	}

	@EventHandler
	public void onTabComplete(TabCompleteEvent event) {
		String cursor = event.getCursor();
		String command = this.getCommand(cursor);
		int argIndex = cursor.split(" ").length - 1;
		String currArg = cursor.split(" ")[argIndex];

		switch (command) {
			case "message":
			case "msg":
			case "tell":
			case "m":
			case "tempmute":
			case "mute":
			case "tempban":
			case "lookup":
			case "lk":
			case "kick":
			case "ban":
			case "unmute":
			case "unban":
				if (argIndex != 1) return;

				List<String> customCompletions = new ArrayList<>();
				Iterator<String> completionsIterator = this.getOnlinePlayers().iterator();
				List<String> matchedCompletions = new ArrayList<>();
				String completion;

				while (true) {
					if (!completionsIterator.hasNext()) {
						matchedCompletions.sort(String.CASE_INSENSITIVE_ORDER);
						customCompletions.addAll(matchedCompletions);
						break;
					}

					completion = completionsIterator.next();

					if (completion.toLowerCase().startsWith(currArg.toLowerCase()))
						matchedCompletions.add(completion);
				}

				for (String custom : customCompletions)
					event.getSuggestions().add(custom);

				break;
		}
	}

	private List<String> getOnlinePlayers() {
		List<String> players = new ArrayList<>();

		for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers())
			players.add(player.getName());

		return players;
	}

	private String getCommand(String cursor) {
		String[] words = cursor.split(" ");

		if (words.length == 0) return cursor.replace("/", "");
		else return words[0].replace("/", "");
	}

}
