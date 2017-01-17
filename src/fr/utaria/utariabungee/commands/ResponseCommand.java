package fr.utaria.utariabungee.commands;

import fr.utaria.utariabungee.Config;
import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.utils.BungeeMessages;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

public class ResponseCommand extends Command {

	public ResponseCommand() {
		super("response", null, "r", "repondre");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if( !(sender instanceof ProxiedPlayer) ) {
			BungeeMessages.cannotUseCommand(sender);
			return;
		}

		ProxiedPlayer pp = (ProxiedPlayer) sender;

		if( args.length < 1 ) {
			pp.sendMessage(new TextComponent(Config.prefix + "§cUtilisation: /r <message>"));
			return;
		}

		UUID  playerUID = UtariaBungee.getPMManager().getLastSenderFor(pp);
		String message  = "";

		for (String arg : args) message += "§7" + arg + " ";

		if( playerUID == null ) {
			pp.sendMessage(new TextComponent(Config.prefix + "§cVous ne pouvez répondre à personne."));
			return;
		}

		if( playerUID.equals(pp.getUniqueId()) ) {
			pp.sendMessage(new TextComponent(Config.prefix + "§cAction impossible."));
			return;
		}

		if( BungeeCord.getInstance().getPlayer(playerUID) == null ) {
			pp.sendMessage(new TextComponent(Config.prefix + "§cLe joueur qui vous envoyé un message est hors-ligne."));
			return;
		}

		ProxiedPlayer ppDest = BungeeCord.getInstance().getPlayer(playerUID);

		UtariaBungee.getPMManager().sendPrivateMessageTo(pp, ppDest, message);
	}

}