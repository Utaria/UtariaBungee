package fr.utaria.utariabungee.tasks;

import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.network.ProxyManager;
import fr.utaria.utariabungee.util.UUtil;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.concurrent.TimeUnit;

public class TabHeadersTask implements Runnable {

	private ProxyManager proxy;

	private String[] headerSteps;

	private int headerIndex;

	public TabHeadersTask() {
		this.prepare();

		this.proxy = UtariaBungee.getInstance().getInstance(ProxyManager.class);

		ProxyServer.getInstance().getScheduler().schedule(UtariaBungee.getInstance(), this, 0, 250, TimeUnit.MILLISECONDS);
	}

	@Override
	public void run() {
		BaseComponent[] header = TextComponent.fromLegacyText(this.headerSteps[this.headerIndex]);

		for (ProxiedPlayer pp : ProxyServer.getInstance().getPlayers()) {
			ServerInfo info = (pp.getServer() != null) ? pp.getServer().getInfo() : null;
			String server = "";

			if (info != null && !info.getName().equalsIgnoreCase("default"))
				server = "(" + UUtil.ucfirst(info.getName()) + ")";

			BaseComponent[] footer = TextComponent.fromLegacyText(
					" \n§b" + ProxyServer.getInstance().getOnlineCount() + "§7/" + this.proxy.getMaxPlayers() + " " + server + "\n "
			);

			pp.setTabHeader(header, footer);
		}

		this.headerIndex = (this.headerIndex + 1) % this.headerSteps.length;
	}

	private void prepare() {
		String blank = "      ";
		String secLine = "\n§7mc.utaria.fr\n ";
		String secLine2 = "\n§7boutique.utaria.fr\n ";

		this.headerSteps = new String[]{
				"§l" + blank + "UTARIA" + blank + secLine,
				"§l" + blank + "UTARIA" + blank + secLine,
				"§l" + blank + "UTARIA" + blank + secLine,
				"§l" + blank + "UTARIA" + blank + secLine,
				"§l" + blank + "UTARIA" + blank + secLine,
				"§l" + blank + "UTARIA" + blank + secLine,
				"§l" + blank + "UTARIA" + blank + secLine,
				"§l" + blank + "UTARIA" + blank + secLine,
				"§l" + blank + "UTARIA" + blank + secLine,
				"§l" + blank + "§6§lU§f§lTARIA" + blank + secLine,
				"§l" + blank + "§e§lU§6§lT§f§lARIA" + blank + secLine,
				"§l" + blank + "§e§lUT§6§lA§f§lRIA" + blank + secLine,
				"§l" + blank + "§e§lUTA§6§lR§f§lIA" + blank + secLine,
				"§l" + blank + "§e§lUTAR§6§lI§f§lA" + blank + secLine,
				"§l" + blank + "§e§lUTARI§6§lA" + blank + secLine,
				"§l" + blank + "§e§lUTARIA" + blank + secLine,
				"§l" + blank + "§e§lUTARIA" + blank + secLine,
				"§l" + blank + "§e§lUTARIA" + blank + secLine,
				"§l" + blank + "UTARIA" + blank + secLine,
				"§l" + blank + "UTARIA" + blank + secLine,
				"§l" + blank + "§e§lUTARIA" + blank + secLine,
				"§l" + blank + "§e§lUTARIA" + blank + secLine,

				"§l" + blank + "UTARIA" + blank + secLine2,
				"§l" + blank + "UTARIA" + blank + secLine2,
				"§l" + blank + "UTARIA" + blank + secLine2,
				"§l" + blank + "UTARIA" + blank + secLine2,
				"§l" + blank + "UTARIA" + blank + secLine2,
				"§l" + blank + "UTARIA" + blank + secLine2,
				"§l" + blank + "UTARIA" + blank + secLine2,
				"§l" + blank + "UTARIA" + blank + secLine2,
				"§l" + blank + "UTARIA" + blank + secLine2,
				"§l" + blank + "§6§lU§f§lTARIA" + blank + secLine2,
				"§l" + blank + "§e§lU§6§lT§f§lARIA" + blank + secLine2,
				"§l" + blank + "§e§lUT§6§lA§f§lRIA" + blank + secLine2,
				"§l" + blank + "§e§lUTA§6§lR§f§lIA" + blank + secLine2,
				"§l" + blank + "§e§lUTAR§6§lI§f§lA" + blank + secLine2,
				"§l" + blank + "§e§lUTARI§6§lA" + blank + secLine2,
				"§l" + blank + "§e§lUTARIA" + blank + secLine2,
				"§l" + blank + "§e§lUTARIA" + blank + secLine2,
				"§l" + blank + "§e§lUTARIA" + blank + secLine2,
				"§l" + blank + "UTARIA" + blank + secLine2,
				"§l" + blank + "UTARIA" + blank + secLine2,
				"§l" + blank + "§e§lUTARIA" + blank + secLine2,
				"§l" + blank + "§e§lUTARIA" + blank + secLine2
		};
	}

}
