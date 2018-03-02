package fr.utaria.utariabungee.commands;

import fr.utaria.utariabungee.Config;
import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.players.PlayerInfo;
import fr.utaria.utariabungee.players.UtariaPlayer;
import fr.utaria.utariabungee.util.PlayerUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractCommand extends Command {

	private String permission;

	private List<String> args;

	private Integer minArgs;

	private Integer nbArgs;

	public AbstractCommand(String name, String... aliases) {
		super(name, null, aliases);

		// Enregistrement de la commande pour le serveur
		ProxyServer.getInstance().getPluginManager().registerCommand(UtariaBungee.getInstance(), this);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (sender == null) return;

		// Vérification d'un nombre d'argument
		if (this.minArgs != null && this.minArgs > args.length) {
			sender.sendMessage(Config.ERROR_PREFIX + "Nombre d'arguments incorrect. Consultez le §d/aide §c!");
			return;
		}
		if (this.nbArgs != null && this.nbArgs != args.length) {
			sender.sendMessage(Config.ERROR_PREFIX + "Nombre d'arguments incorrect. Consultez le §d/aide §c!");
			return;
		}

		this.args = Arrays.asList(args);

		if (sender instanceof ProxiedPlayer) {
			ProxiedPlayer player = (ProxiedPlayer) sender;
			PlayerInfo pInfo = PlayerInfo.get(player);

			if (pInfo == null) {
				PlayerUtil.sendErrorMessage(player, "Merci d'attendre que vos données chargent...");
				return;
			}

			if (!this.hasRequiredPermission(player)) {
				PlayerUtil.sendErrorMessage(player, "Vous n'avez pas la permission de taper cette commande§c.");
				return;
			}

			this.perform(sender);
			this.performPlayer(player, pInfo);
		} else if(sender == ProxyServer.getInstance().getConsole()) {
			this.perform(sender);
			this.performConsole(sender);
		}
	}

	public abstract void perform(CommandSender sender);

	public abstract void performPlayer(ProxiedPlayer player, PlayerInfo pInfo);

	public abstract void performConsole(CommandSender sender);

	public void setPermission(String permission) {
		this.permission = permission;
	}

	private boolean hasRequiredPermission(ProxiedPlayer player) {
		return this.permission == null || UtariaPlayer.get(player).hasPerm(this.permission);
	}

	protected void setNbArgs(int nbArgs) {
		this.minArgs = null;
		this.nbArgs = nbArgs;
	}

	protected void setMinArgs(int minArgs) {
		this.minArgs = minArgs;
		this.nbArgs = null;
	}

	protected int getNbArguments() {
		return this.args.size();
	}

	protected String getArgument(int index) {
		return (index >= 0 && index < this.args.size()) ? this.args.get(index) : null;
	}

}
