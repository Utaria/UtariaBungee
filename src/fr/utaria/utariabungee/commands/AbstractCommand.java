package fr.utaria.utariabungee.commands;

import fr.utaria.utariabungee.Config;
import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.players.PlayerInfo;
import fr.utaria.utariabungee.players.PlayersManager;
import fr.utaria.utariabungee.players.UtariaRank;
import fr.utaria.utariabungee.util.PlayerUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.command.ConsoleCommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractCommand extends Command {

	private List<UtariaRank> requiredRanks;
	private UtariaRank       lowerRequiredRank;

	private List<String> args;
	private Integer      minArgs;
	private Integer      nbArgs;


	public AbstractCommand(String name, String... aliases) {
		super(name, null, aliases);

		this.requiredRanks = new ArrayList<>();

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
			ProxiedPlayer     player = (ProxiedPlayer) sender;
			PlayerInfo pInfo  = PlayerInfo.get(player);

			if (pInfo == null) {
				PlayerUtil.sendErrorMessage(player, "Merci d'attendre que vos données chargent...");
				return;
			}

			if (this.requiredRanks.size() > 0 && !this.hasRequiredRank(player)) {
				PlayerUtil.sendErrorMessage(player, "Cette commande est disponible à partir de " + this.lowerRequiredRank.getPrefix().trim() + "§c.");
				return;
			}

			this.perform(sender);
			this.performPlayer(player, pInfo);
		} else if (sender instanceof ConsoleCommandSender) {
			this.perform(sender);
			this.performConsole(sender);
		}
	}


	public abstract void perform(CommandSender sender);

	public abstract void performPlayer(ProxiedPlayer player, PlayerInfo pInfo);

	public abstract void performConsole(CommandSender sender);


	protected void setRequiredRank(String... ranksName) {
		this.requiredRanks.clear();

		for (String rankName : ranksName) {
			UtariaRank rank = PlayersManager.getRankByName(rankName);

			if (rank == null)
				throw new NullPointerException("Le grade '" + rankName + "' n'existe pas (la commande " + this.getName() + " est donc mal configurée) !");

			if (this.lowerRequiredRank == null || this.lowerRequiredRank.getLevel() >= rank.getLevel())
				this.lowerRequiredRank = rank;

			this.requiredRanks.add(rank);
		}
	}

	private boolean hasRequiredRank(ProxiedPlayer player) {
		for (UtariaRank rank : this.requiredRanks)
			if (PlayersManager.playerHasRankLevel(player, rank.getLevel()))
				return true;

		return false;
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
