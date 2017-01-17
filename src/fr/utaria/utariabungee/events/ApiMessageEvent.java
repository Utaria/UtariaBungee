package fr.utaria.utariabungee.events;

import java.util.ArrayList;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

import fr.utaria.utariabungee.UtariaBungee;

public class ApiMessageEvent extends Event{

	private String action;
	private ArrayList<String> params;
	
	private String response = "";
	
	public ApiMessageEvent(String message){
		String[] parts = message.split("/");
		
		String action = parts[0];
		ArrayList<String> params = new ArrayList<>();
		
		for(String param : parts[1].split(",")){
			param = param.replace("\n", "");
			params.add(param);
		}
		
		this.action = action;
		this.params = params;
	}
	
	
	public String getAction(){
		return this.action;
	}
	public ArrayList<String> getParams(){
		return this.params;
	}
	public String getParam(Integer index){
		return this.params.get(index);
	}
	
	public void setResponse(String response){
		this.response = response;
	}
	public String getResponse(){
		return response;
	}

	
	public void formatResponse(){
		switch (this.getAction()) {
			case "getplayerserver":
				ProxiedPlayer player = UtariaBungee.getInstance().getProxy().getPlayer(this.getParam(0));
				
				if(player == null) this.setResponse("null");
				else this.setResponse(player.getServer().getInfo().getName());
				break;
			
			case "getserverplayercount":
				ServerInfo serverInfo = UtariaBungee.getInstance().getProxy().getServerInfo(this.getParam(0));
				
				if(serverInfo == null) this.setResponse("-1");
				else this.setResponse(String.valueOf(serverInfo.getPlayers().size()));
				break;
	
			
			default:
				break;
		}
	}
}
