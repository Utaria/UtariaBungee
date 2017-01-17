package fr.utaria.utariabungee.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;

import fr.utaria.utariabungee.events.ApiMessageEvent;

public class SocketThread implements Runnable{
	
	private SocketServer server;
	
	
	public SocketThread(SocketServer server){
		this.server = server;
	}
	
	@Override
	public void run(){
		try {
			while(true){
				if(this.server.getServerInstance() == null || this.server.getServerInstance().isClosed()) continue;
				Socket socket = this.server.getServerInstance().accept();

				InputStream is        = socket.getInputStream();
				byte[]      buf       = new byte[1024];
				int         bytes_read;

				bytes_read = is.read(buf, 0, buf.length);

                // If the socket is closed, sockInput.read() will return -1.
                if(bytes_read < 0) {
                    System.err.println("Server: Tried to read from socket, read() returned < 0,  Closing socket.");
                    return;
                }

                String r = new String(buf, 0, bytes_read);
                r = r.substring(0, r.length()-2);

                ApiMessageEvent e = new ApiMessageEvent(r);
                e.formatResponse();

                // Send response to the client
                PrintWriter output = new PrintWriter(socket.getOutputStream(),true);
                output.println(e.getResponse());
			}
		} catch (IOException ignored) {}
	}
	
}
