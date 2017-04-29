package fr.utaria.utariabungee.commands;

import fr.utaria.utariabungee.Config;
import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.utils.BungeeMessages;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class MessageCommand extends Command {


	public MessageCommand() {
		super("message", null, "msg", "tell", "m");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!(sender instanceof ProxiedPlayer)) {
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
		if (args.length < 2) {
			pp.sendMessage(new TextComponent(Config.prefix + "§cUtilisation: /msg <joueur> <message>"));
			return;
		}

		/*--------------------------------*/
		/*  Récupération du destinataire  */
		/*--------------------------------*/
		String playername = args[0];

		if (playername.equals(pp.getName())) {
			pp.sendMessage(new TextComponent(Config.prefix + "§cQuelle idée ! Pourquoi faire ça ?"));
			return;
		}

		ProxiedPlayer ppDest = BungeeCord.getInstance().getPlayer(playername);
		if (ppDest == null) {
			pp.sendMessage(new TextComponent(Config.prefix + "§cLe joueur §6" + playername + "§c est hors-ligne."));
			return;
		}

		/*--------------------------------*/
		/*  Construction du message       */
		/*--------------------------------*/
		StringBuilder message = new StringBuilder();

		for (int i = 1; i < args.length; i++ )
			message.append(args[i]).append(" ");


		UtariaBungee.getPMManager().sendPrivateMessageTo(pp, ppDest, message.toString());
	}
}
