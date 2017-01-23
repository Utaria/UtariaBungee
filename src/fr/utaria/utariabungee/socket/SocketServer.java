package fr.utaria.utariabungee.socket;

import java.io.IOException;
import java.net.ServerSocket;

import fr.utaria.utariabungee.Config;

public class SocketServer {

	private ServerSocket server;
	private Integer      port = Config.socketServerPort;
	private Thread       serverThread;
	
	public SocketServer(){
		this.start();
	}
	
	
	public ServerSocket getServerInstance(){
		return this.server;
	}
	
	private void start(){
		try{
			this.server = new ServerSocket(this.port);
		} catch(Exception e){
			// e.printStackTrace();
			System.out.println("[UtariaBungee] Server start with an error : " + e.getMessage());
		}
		
		this.serverThread = new Thread(new SocketThread(this));
		this.serverThread.start();
	}
	public  void stop(){
		if(this.server == null) return ;
		
		if(serverThread != null){
			serverThread.interrupt();
			serverThread = null;
		}
		
		try {
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
