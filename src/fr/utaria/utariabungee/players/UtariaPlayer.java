package fr.utaria.utariabungee.players;

import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.utils.Utils;
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
		return Utils.getPlayerIP(this.player);
	}

	public static UtariaPlayer get(ProxiedPlayer player){
		return UtariaBungee.getPlayer(player);
	}
}
