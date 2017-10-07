package fr.utaria.utariabungee.network;

import fr.utaria.utariabungee.Config;
import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.players.PlayersManager;
import fr.utaria.utariabungee.players.UtariaRank;
import fr.utaria.utariadatabase.util.ConfigTableAccessor;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.concurrent.TimeUnit;

public class MaintenanceMode implements Runnable {

	private boolean active;
	private String  motd;
	private String  kickMessage;


	MaintenanceMode() {
		ProxyServer.getInstance().getScheduler().schedule(UtariaBungee.getInstance(), this, 10, 10, TimeUnit.SECONDS);
		this.run(true);
	}

	@Override
	public void run() {
		this.run(false);
	}
	public void run(boolean first) {
		boolean wasActive = this.active;

		this.active      = ConfigTableAccessor.getBoolean("maintenance");
		this.motd        = ConfigTableAccessor.getString("maintenance_motd");
		this.kickMessage = ConfigTableAccessor.getString("maintenance_kick_message");

		// Le mode vient d'être activé !
		if (!wasActive && this.active && !first)
			this.startMode();
	}


	public boolean isActive() {
		return this.active;
	}

	public String getMotd() {
		return this.motd;
	}

	public String getKickMessage() {
		return this.kickMessage;
	}


	private void startMode() {
		ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(
				Config.ERROR_PREFIX + "Le mode maintenance vient d'être activé sur notre réseau." +
				"Vous allez être déconnectés dans §6" + Config.MAINTENANCE_LOGOUT_DELAY + " secondes§c."
		));

		ProxyServer.getInstance().getScheduler().schedule(UtariaBungee.getInstance(), () -> {
			for(ProxiedPlayer player : BungeeCord.getInstance().getPlayers()){
				if (!PlayersManager.playerHasRankLevel(player, Config.MAINTENANCE_MIN_LEVEL)) {
					player.disconnect(TextComponent.fromLegacyText("§cVous venez d'être déconnecté pour cause d'une maintenance."));
				} else {
					UtariaRank playerRank = PlayersManager.getPlayerHighestRank(player);

					assert playerRank != null;

					player.sendMessage(TextComponent.fromLegacyText(
							Config.INFO_PREFIX + "En tant que " + playerRank.getPrefix() + "§7, vous n'avez pas été exclu."
					));
				}
			}
		}, Config.MAINTENANCE_LOGOUT_DELAY, TimeUnit.SECONDS);
	}

}
