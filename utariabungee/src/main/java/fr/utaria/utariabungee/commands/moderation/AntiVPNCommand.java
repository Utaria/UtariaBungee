package fr.utaria.utariabungee.commands.moderation;

import fr.utaria.utariabungee.Config;
import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.antivpn.AntiVPNManager;
import fr.utaria.utariabungee.commands.AbstractCommand;
import fr.utaria.utariabungee.players.PlayerInfo;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class AntiVPNCommand extends AbstractCommand {

	private AntiVPNManager manager;

	public AntiVPNCommand() {
		super("antivpn");

		this.manager = UtariaBungee.getInstance().getInstance(AntiVPNManager.class);

		this.setNbArgs(2);
		this.setPermission("modo.antivpn");
	}

	@Override
	public void perform(CommandSender sender) {
		String action = this.getArgument(0);
		String ip = this.getArgument(1);

		switch (action) {
			case "addexception":
				this.manager.getExceptionList().addException(ip);
				sender.sendMessage(TextComponent.fromLegacyText(Config.MOD_PREFIX + "L'IP §6" + ip + "§7 a été ajoutée à la liste des exceptions Anti-VPN."));
				break;
			case "rmexception":
				this.manager.getExceptionList().removeException(ip);
				sender.sendMessage(TextComponent.fromLegacyText(Config.MOD_PREFIX + "L'IP §6" + ip + "§7 a été supprimée de la liste des exceptions Anti-VPN."));
				break;
			default:
				sender.sendMessage(TextComponent.fromLegacyText(Config.ERROR_PREFIX + "Utilisation: /antivpn <addexception|rmexception> <joueur>"));
				break;
		}
	}

	@Override
	public void performPlayer(ProxiedPlayer player, PlayerInfo pInfo) {

	}

	@Override
	public void performConsole(CommandSender sender) {

	}

}
