package fr.utaria.utariabungee.util;

import fr.utaria.utariabungee.Config;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class BungeeMessages {

	public static void cannotUseCommand(CommandSender sender){
		sender.sendMessage(new TextComponent(Config.prefix + "§cVous ne pouvez pas taper cette commande."));
	}
	
}
