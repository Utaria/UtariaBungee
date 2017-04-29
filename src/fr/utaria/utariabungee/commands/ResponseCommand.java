package fr.utaria.utariabungee.commands;

import fr.utaria.utariabungee.Config;
import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.utils.BungeeMessages;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

public class ResponseCommand extends Command {

	public ResponseCommand() {
		super("response", null, "r", "repondre");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if( !(sender instanceof ProxiedPlayer) ) {
			BungeeMessages.cannotUseCommand(sender);
			return;
		}

		/*--------------------------------*/
		/*  Joueur et permission          */
		/*--------------------------------*/
		ProxiedPlayer pp = (ProxiedPlayer) sender;

		if (UtariaBungee.getModerationManager().playerIsTempMuted(pp)) {
			pp.sendMessage(TextComponent.fromLegacyText(Config.prefix + "§cVous ne pouvez pas envoyer de message privé en étant muté."));
			return;
		}

		/*--------------------------------*/
		/*  Récupération des arguments    */
		/*--------------------------------*/
		if (args.length < 1) {
			pp.sendMessage(new TextComponent(Config.prefix + "§cUtilisation: /r <message>"));
			return;
		}

		/*--------------------------------*/
		/*  Récupération du destinataire  */
		/*--------------------------------*/
		UUID  playerUID = UtariaBungee.getPMManager().getLastSenderFor(pp);

		// Tests avant la récupération du destinataire
		if (playerUID == null) {
			pp.sendMessage(new TextComponent(Config.prefix + "§cAttendez qu'un joueur vous écrive avant."));
			return;
		} else if (playerUID.equals(pp.getUniqueId())) {
			pp.sendMessage(new TextComponent(Config.prefix + "§cAction impossible."));
			return;
		}

		// On tente ensuite de récupérer le destinataire (mais il doit être connecté).
		ProxiedPlayer ppDest = BungeeCord.getInstance().getPlayer(playerUID);
		if (ppDest == null) {
			pp.sendMessage(new TextComponent(Config.prefix + "§cLe joueur qui vous envoyé un message est hors-ligne."));
			return;
		}

		/*--------------------------------*/
		/*  Construction du message       */
		/*--------------------------------*/
		StringBuilder message = new StringBuilder();

		for (String arg : args)
			message.append(arg).append(" ");


		UtariaBungee.getPMManager().sendPrivateMessageTo(pp, ppDest, message.toString());
	}

}