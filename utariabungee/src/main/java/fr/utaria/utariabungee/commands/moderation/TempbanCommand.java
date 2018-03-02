package fr.utaria.utariabungee.commands.moderation;

import fr.utaria.utariabungee.Config;
import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.commands.AbstractCommand;
import fr.utaria.utariabungee.moderation.ModerationManager;
import fr.utaria.utariabungee.players.PlayerInfo;
import fr.utaria.utariabungee.util.time.UTime;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class TempbanCommand extends AbstractCommand {

	public TempbanCommand() {
		super("tempban");

		this.setPermission("modo.tempban");
		this.setMinArgs(3);
	}

	@Override
	public void perform(CommandSender sender) {
		// On récupère tout d'abord la raison du bannissement temporaire ...
		StringBuilder reason = new StringBuilder();
		for (int i = 2; i < this.getNbArguments(); i++)
			reason.append(this.getArgument(i)).append(" ");

		reason = new StringBuilder(reason.substring(0, reason.length() - 1));

		// ... et on applique la sanction !
		ModerationManager manager = UtariaBungee.getInstance().getInstance(ModerationManager.class);

		if (!manager.ban(this.getArgument(0), reason.toString(), sender, new UTime(this.getArgument(1))))
			sender.sendMessage(TextComponent.fromLegacyText(Config.ERROR_PREFIX + "Format de date §6\"" + this.getArgument(1) + "\"§c invalide !"));
	}

	@Override
	public void performPlayer(ProxiedPlayer player, PlayerInfo pInfo) {

	}

	@Override
	public void performConsole(CommandSender sender) {

	}

}
