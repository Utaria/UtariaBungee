package fr.utaria.utariabungee.players;

import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.network.ProxyManager;
import fr.utaria.utariabungee.util.UUtil;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public class UtariaPlayer {

	private ProxiedPlayer player;
	private PlayerInfo    playerInfo;

	public UtariaPlayer(ProxiedPlayer player){
		this.player = player;
		this.playerInfo = new PlayerInfo(this);
	}


	public ProxiedPlayer getPlayer        (){
		return this.player;
	}
	public String        getPlayerName    (){
		return this.player.getName();
	}
	public UUID          getPlayerUniqueId(){
		return this.player.getUniqueId();
	}
	public PlayerInfo    getPlayerInfo    (){
		return this.playerInfo;
	}

	public String        getIP            () {
		if (this.player == null) return "0.0.0.0";
		return UUtil.getPlayerIP(this.player);
	}

	public boolean isOnDefaultServer() {
		return this.player.getServer().getInfo().getName().equalsIgnoreCase(
				UtariaBungee.getInstance().getInstance(ProxyManager.class).getDefaultServer().getName()
		);
	}


	public static UtariaPlayer get(ProxiedPlayer player){
		return PlayersManager.getPlayer(player);
	}
}
