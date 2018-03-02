package fr.utaria.utariabungee.commands.moderation;

import fr.utaria.utariabungee.Config;
import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.commands.AbstractCommand;
import fr.utaria.utariabungee.moderation.ModerationManager;
import fr.utaria.utariabungee.players.PlayerInfo;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class KickCommand extends AbstractCommand {

	public KickCommand() {
		super("kick");

		this.setPermission("modo.kick");
		this.setMinArgs(2);
	}

	@Override
	public void perform(CommandSender sender) {
		// On récupère tout d'abord la raison du bannissement ...
		StringBuilder reason = new StringBuilder();
		for (int i = 1; i < this.getNbArguments(); i++)
			reason.append(this.getArgument(i)).append(" ");

		reason = new StringBuilder(reason.substring(0, reason.length() - 1));

		// ... et on applique la sanction !
		if (!UtariaBungee.getInstance().getInstance(ModerationManager.class).kick(this.getArgument(0), reason.toString(), sender))
			sender.sendMessage(TextComponent.fromLegacyText(Config.ERROR_PREFIX + "Votre sanction ne s'applique à aucun joueur."));
	}

	@Override
	public void performPlayer(ProxiedPlayer player, PlayerInfo pInfo) {

	}

	@Override
	public void performConsole(CommandSender sender) {

	}
}
