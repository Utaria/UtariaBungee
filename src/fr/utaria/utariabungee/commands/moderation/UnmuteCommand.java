package fr.utaria.utariabungee.commands.moderation;

import fr.utaria.utariabungee.Config;
import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.commands.AbstractCommand;
import fr.utaria.utariabungee.moderation.ModerationManager;
import fr.utaria.utariabungee.players.PlayerInfo;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class UnmuteCommand extends AbstractCommand {

	public UnmuteCommand() {
		super("unmute");

		this.setRequiredRank("Modérateur");
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
		if (!UtariaBungee.getInstance().getInstance(ModerationManager.class).unmute(this.getArgument(0), reason.toString(), sender))
			sender.sendMessage(TextComponent.fromLegacyText(Config.ERROR_PREFIX + "Aucune mise en sourdine valide n'a été trouvée pour §6\"" + this.getArgument(0) + "\"§c."));
		else
			sender.sendMessage(TextComponent.fromLegacyText(Config.SUCCESS_PREFIX + "§e\"" + this.getArgument(0) + "\" §7peut maintenant §aparler§7."));
	}

	@Override
	public void performPlayer(ProxiedPlayer player, PlayerInfo pInfo) {

	}

	@Override
	public void performConsole(CommandSender sender) {

	}
}
