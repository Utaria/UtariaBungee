package fr.utaria.utariabungee.commands;

import fr.utaria.utariabungee.Config;
import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.utils.BungeeMessages;
import fr.utaria.utariabungee.utils.PlayerUtils;
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

		ProxiedPlayer pp = (ProxiedPlayer) sender;

		if (args.length < 2) {
			pp.sendMessage(new TextComponent(Config.prefix + "§cUtilisation: /msg <joueur> <message>"));
			return;
		}

		String playername = args[0];
		String message    = "";

		for (int i = 1; i < args.length; i++ )
			message += "§7" + args[i] + " ";



		if (playername.equals(pp.getName())) {
			pp.sendMessage(new TextComponent(Config.prefix + "§cPourquoi vouloir faire ça ?"));
			return;
		}

		ProxiedPlayer ppDest = BungeeCord.getInstance().getPlayer(playername);

		if (ppDest == null) {
			pp.sendMessage(new TextComponent(Config.prefix + "§cLe joueur §6" + playername + "§c est hors-ligne."));
			return;
		}


		UtariaBungee.getPMManager().sendPrivateMessageTo(pp, ppDest, message);
	}
}
