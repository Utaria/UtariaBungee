package fr.utaria.utariabungee.commands;

import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.players.PlayerInfo;
import fr.utaria.utariabungee.utils.BungeeMessages;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class ForceRestartCommand extends Command {

	public ForceRestartCommand() {
		super("forcerestart");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {

		if(sender instanceof ProxiedPlayer ) {
			ProxiedPlayer pp = (ProxiedPlayer) sender;
			if ( PlayerInfo.get(pp).getRankLevel() < 40 ) {
				BungeeMessages.cannotUseCommand(sender);
				return;
			}
		}

		// On force le redémarrage
		sender.sendMessage(new TextComponent("§aRedémarrage forcé avec succès ! §2Redémarrage en cours..."));
		UtariaBungee.getAutoRestartManager().doRestart();

	}

}
