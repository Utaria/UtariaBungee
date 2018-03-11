package fr.utaria.utariabungee.commands.chat;

import fr.utaria.utariabungee.Config;
import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.chat.ChatManager;
import fr.utaria.utariabungee.commands.AbstractCommand;
import fr.utaria.utariabungee.players.PlayerInfo;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class SlowCommand extends AbstractCommand {

	private ChatManager chatManager;

	public SlowCommand() {
		super("slow", "cooldown");
		this.chatManager = UtariaBungee.getInstance().getInstance(ChatManager.class);

		this.setPermission("respmodo.slow");
	}

	@Override
	public void perform(CommandSender sender) {

	}

	@Override
	public void performPlayer(ProxiedPlayer player, PlayerInfo pInfo) {
		int argc = this.getNbArguments();

		if (argc == 0) {
			player.sendMessage(TextComponent.fromLegacyText(Config.INFO_PREFIX + "Slow: 1 message toutes les §3" + this.chatManager.getSlow() + " seconde(s)§7."));
			return;
		}

		try {
			int time = Integer.parseInt(this.getArgument(0));

			if (time < 0 || time > 300) {
				player.sendMessage(TextComponent.fromLegacyText(Config.ERROR_PREFIX + "La valeur doit être comprise entre 0 et 300 secondes."));
				return;
			}

			this.chatManager.setSlow(time, player);

			if (time > 0)
				ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(Config.MOD_PREFIX + "§eSlow-mode activé ! Un message toutes les §6" + time + " seconde(s)§e."));
			else
				ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(Config.MOD_PREFIX + "§eSlow-mode désactivé ! Vous pouvez parler librement."));

		} catch (Exception ex) {
			player.sendMessage(TextComponent.fromLegacyText(" "));
			player.sendMessage(TextComponent.fromLegacyText("  §e/slow §8- §fConnaître le slow actuel"));
			player.sendMessage(TextComponent.fromLegacyText("  §e/slow §6<secondes> §8- §fAppliquer un slow (entre 0 et 300sec)"));
			player.sendMessage(TextComponent.fromLegacyText(" "));
		}
	}

	@Override
	public void performConsole(CommandSender sender) {

	}

}
