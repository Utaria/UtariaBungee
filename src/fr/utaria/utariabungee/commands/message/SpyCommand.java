package fr.utaria.utariabungee.commands.message;

import fr.utaria.utariabungee.Config;
import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.chat.PMManager;
import fr.utaria.utariabungee.commands.AbstractCommand;
import fr.utaria.utariabungee.players.PlayerInfo;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class SpyCommand extends AbstractCommand {

	public SpyCommand() {
		super("spy");

		this.setRequiredRank("Modérateur");
	}

	@Override
	public void perform(CommandSender sender) {

	}

	@Override
	public void performPlayer(ProxiedPlayer player, PlayerInfo pInfo) {
		// On active/désactive le mode SPY pour le joueur
		if (UtariaBungee.getInstance().getInstance(PMManager.class).togglePlayerSpyMode(player))
			player.sendMessage(TextComponent.fromLegacyText(Config.INFO_PREFIX + "Le mode §2SPY §7est maintenant §aactivé§7."));
		else
			player.sendMessage(TextComponent.fromLegacyText(Config.INFO_PREFIX + "Le mode §2SPY §7est maintenant §cdésactivé§7."));
	}

	@Override
	public void performConsole(CommandSender sender) {

	}
}
