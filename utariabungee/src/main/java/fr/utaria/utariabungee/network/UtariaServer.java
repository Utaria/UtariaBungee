package fr.utaria.utariabungee.network;

import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.socket.SocketClient;
import fr.utaria.utariabungee.socket.custompackets.PacketOutRestart;
import fr.utaria.utariabungee.socket.packets.SendingPacket;
import net.md_5.bungee.api.config.ServerInfo;

import java.net.InetSocketAddress;

public class UtariaServer {

	private int _id;

	private String _name;

	private String _ip;

	private int _port;

	private boolean _default;

	private int _rankLevelNeeded;

	private int _socketServerPort;

	private ServerInfo _info;

	public UtariaServer(int id, String name) {
		this(id, name, "127.0.0.1", 25565);
	}

	public UtariaServer(int id, String name, String ip, int port) {
		this(id, name, ip, port, 1);
	}

	public UtariaServer(int id, String name, String ip, int port, int rankLevelNeeded) {
		this._id = id;
		this._name = name;
		this._ip = ip;
		this._port = port;
		this._rankLevelNeeded = rankLevelNeeded;

		this._default = false;

		this._generateServerInfo();
	}

	public int getId() {
		return this._id;
	}

	public String getName() {
		return this._name;
	}

	public String getIp() {
		return this._ip;
	}

	public int getPort() {
		return this._port;
	}

	public int getRankLevelNeeded() {
		return this._rankLevelNeeded;
	}

	public int getSocketServerPort() {
		return this._socketServerPort;
	}

	public boolean isDefault() {
		return this._default;
	}

	void setDefault(boolean b) {
		this._default = b;
	}

	void setSocketServerPort(int port) {
		this._socketServerPort = port;
	}

	public ServerInfo getServerInfo() {
		return this._info;
	}

	public boolean restart() {
		this.sendPacket(new PacketOutRestart());
		return true;
	}

	public void sendPacket(SendingPacket packet) {
		System.out.println("ip = " + this.getIp() + " / port = " + this.getSocketServerPort());
		SocketClient client = new SocketClient(this.getIp(), this.getSocketServerPort());

		client.sendPacket(packet);
		client.disconnect();
	}

	private void _generateServerInfo() {
		InetSocketAddress address = new InetSocketAddress(this._ip, this._port);

		this._info = UtariaBungee.getInstance().getProxy().constructServerInfo(this._name, address, "utaria", false);
	}

	@Override
	public String toString() {
		return "{UtariaServer (id=" + this._id + " name=" + this._name + " ip=" + this._ip + " port=" + this._port + " default=" + this._default + ")}";
	}

}
