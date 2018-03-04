package fr.utaria.utariabungee.commands.moderation;

import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.commands.AbstractCommand;
import fr.utaria.utariabungee.network.AutoRestartManager;
import fr.utaria.utariabungee.players.PlayerInfo;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class RestartCommand extends AbstractCommand {

	public RestartCommand() {
		super("restart");

		this.setPermission("restart");
	}

	@Override
	public void perform(CommandSender sender) {
		UtariaBungee.getInstance().getInstance(AutoRestartManager.class).doRestart();
	}

	@Override
	public void performPlayer(ProxiedPlayer player, PlayerInfo pInfo) {

	}

	@Override
	public void performConsole(CommandSender sender) {

	}

}
