package fr.utaria.utariabungee.commands.message;

import fr.utaria.utariabungee.Config;
import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.chat.PMManager;
import fr.utaria.utariabungee.commands.AbstractCommand;
import fr.utaria.utariabungee.moderation.ModerationManager;
import fr.utaria.utariabungee.moderation.sanctions.Mute;
import fr.utaria.utariabungee.players.PlayerInfo;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class MessageCommand extends AbstractCommand {

	public MessageCommand() {
		super("message", "msg", "tell", "m");

		this.setMinArgs(2);
	}

	@Override
	public void perform(CommandSender sender) {

	}

	@Override
	public void performPlayer(ProxiedPlayer player, PlayerInfo pInfo) {
		/*--------------------------------*/
		/*  Joueur et permission          */
		/*--------------------------------*/
		Mute.MuteState muteState = UtariaBungee.getInstance().getInstance(ModerationManager.class).getMuteStateFor(player);

		if (muteState != Mute.MuteState.NO) {
			player.sendMessage(TextComponent.fromLegacyText(Config.ERROR_PREFIX + "Vous ne pouvez pas envoyer de message privé."));
			return;
		}

		/*--------------------------------*/
		/*  Récupération du destinataire  */
		/*--------------------------------*/
		String playername = this.getArgument(0);

		if (playername.equals(player.getName())) {
			player.sendMessage(TextComponent.fromLegacyText(Config.ERROR_PREFIX + "Quelle idée ! Pourquoi vouloir faire ça ?"));
			return;
		}

		ProxiedPlayer ppDest = ProxyServer.getInstance().getPlayer(playername);
		if (ppDest == null) {
			player.sendMessage(TextComponent.fromLegacyText(Config.ERROR_PREFIX + "Le joueur §6\"" + playername + "\"§c est hors-ligne."));
			return;
		}

		/*--------------------------------*/
		/*  Construction du message       */
		/*--------------------------------*/
		StringBuilder message = new StringBuilder();

		for (int i = 1; i < this.getNbArguments(); i++ )
			message.append(this.getArgument(i)).append(" ");


		// Envoi du message !
		UtariaBungee.getInstance().getInstance(PMManager.class).sendPrivateMessageTo(player, ppDest, message.toString());
	}

	@Override
	public void performConsole(CommandSender sender) {

	}
}
