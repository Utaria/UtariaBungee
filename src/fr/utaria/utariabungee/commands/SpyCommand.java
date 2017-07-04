package fr.utaria.utariabungee.commands;

import fr.utaria.utariabungee.Config;
import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.managers.PlayersManager;
import fr.utaria.utariabungee.players.PlayerInfo;
import fr.utaria.utariabungee.utils.BungeeMessages;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class SpyCommand extends Command {

	public SpyCommand() {
		super("spy");
	}


	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!(sender instanceof ProxiedPlayer)) return;
		ProxiedPlayer pp = (ProxiedPlayer) sender;

		// On vérifie si le joueur a la permission de faire ça
		if (!PlayersManager.playerHasRankLevel(pp, Config.adminMinLevel)) {
			BungeeMessages.cannotUseCommand(sender);
			return;
		}

		// On active/désactive le mode SPY pour le joueur
		if (UtariaBungee.getPMManager().togglePlayerSpyMode(pp))
			pp.sendMessage(new TextComponent(Config.prefix + "§aLe mode §2SPY §aest maintenant activé."));
		else
			pp.sendMessage(new TextComponent(Config.prefix + "§cLe mode §6SPY §cest maintenant désactivé."));

	}

}
