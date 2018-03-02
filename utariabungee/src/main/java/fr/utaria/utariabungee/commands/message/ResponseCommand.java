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

import java.util.UUID;

public class ResponseCommand extends AbstractCommand {

	public ResponseCommand() {
		super("response", "r");

		this.setMinArgs(1);
	}

	@Override
	public void perform(CommandSender sender) {

	}

	@Override
	public void performPlayer(ProxiedPlayer player, PlayerInfo pInfo) {
		PMManager manager = UtariaBungee.getInstance().getInstance(PMManager.class);

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
		UUID playerUID = manager.getLastSenderFor(player);

		// Tests avant la récupération du destinataire
		if (playerUID == null) {
			player.sendMessage(new TextComponent(Config.ERROR_PREFIX + "§cVous ne pouvez répondre à personne."));
			return;
		} else
			if (playerUID.equals(player.getUniqueId())) {
				player.sendMessage(new TextComponent(Config.ERROR_PREFIX + "§cIl est impossible de se répondre à soi-même, vous êtes fort."));
				return;
			}

		// On tente ensuite de récupérer le destinataire (mais il doit être connecté).
		ProxiedPlayer ppDest = ProxyServer.getInstance().getPlayer(playerUID);
		if (ppDest == null) {
			player.sendMessage(new TextComponent(Config.ERROR_PREFIX + "§cLe joueur qui vous envoyé un message est hors-ligne."));
			return;
		}

		/*--------------------------------*/
		/*  Construction du message       */
		/*--------------------------------*/
		StringBuilder message = new StringBuilder();

		for (int i = 0; i < this.getNbArguments(); i++)
			message.append(this.getArgument(i)).append(" ");


		manager.sendPrivateMessageTo(player, ppDest, message.toString());
	}

	@Override
	public void performConsole(CommandSender sender) {

	}

}
