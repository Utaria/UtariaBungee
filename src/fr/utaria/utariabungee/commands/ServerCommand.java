package fr.utaria.utariabungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class ServerCommand extends Command {

	public ServerCommand() {
		super("tpto");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		sender.sendMessage(new TextComponent("Serveur!"));
	}

}