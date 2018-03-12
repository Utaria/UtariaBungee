package fr.utaria.utariabungee.chat;

import fr.utaria.utariabungee.AbstractManager;
import fr.utaria.utariabungee.Config;
import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.players.UtariaPlayer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.event.EventHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class ChatManager extends AbstractManager {

	private final static Pattern IP_PATTERN = Pattern.compile("(?:\\d{1,3}[.,\\-_:;\\/()=?}+ ]{1,4}){3}\\d{1,3}");

	private final static Pattern URL_PATTERN = Pattern.compile("[-a-zA-Z0-9@:%_\\+.~#?&//=]{2,256}\\.(com|ru|net|org|de|jp|uk|br|pl|in|it|fr|au|info|nl|cn|ir|es|cz|biz|ca|kr|eu|ua|za|co|gr|ro|se|tw|vn|mx|ch|tr|at|be|hu|dk|tv|me|ar|us|no|sk|fi|id|cl|nz|by|pt)\\b(\\/[-a-zA-Z0-9@:%_\\+.~#?&//=]*)?");

	private final static int PERCENT_CAPS = 50;

	private final static int MAX_LENGTH_WORD = 15;

	private int slow;

	private UUID slowModeOwner;

	private Map<UUID, Long> lastSpeak;

	public ChatManager() {
		super(UtariaBungee.getInstance());
	}

	@Override
	public void initialize() {
		this.lastSpeak = new HashMap<>();
	}

	@EventHandler
	public void onChat(ChatEvent event) {
		if (!(event.getSender() instanceof ProxiedPlayer)) return;
		ProxiedPlayer player = (ProxiedPlayer) event.getSender();

		// On ne prend pas en compte les commandes.
		// Ni les joueurs qui ont un droit complet sur le chat.
		if (event.isCommand() || UtariaPlayer.get(player).hasPerm("chat.bypass")) return;

		// Chat slow
		if (this.slow > 0) {
			long lastSpeakTemp = this.lastSpeak.getOrDefault(player.getUniqueId(), 0L);
			long now = System.currentTimeMillis();
			long diff = now - lastSpeakTemp;

			if (diff < this.slow * 1000) {
				event.setCancelled(true);
				player.sendMessage(TextComponent.fromLegacyText(Config.ERROR_PREFIX + "Le chat est ralenti... un message toutes les §6" + this.slow + " seconde(s)§c."));
				return;
			}

			this.lastSpeak.put(player.getUniqueId(), now);
		}
		// Fin chat slow

		// Filtrage du chat
		if (this.checkForFlood(event.getMessage())) {
			event.setCancelled(true);
			player.sendMessage(TextComponent.fromLegacyText(Config.ERROR_PREFIX + "Votre message ne suit pas le règlement."));
			return;
		}

		if (this.checkForAdvertising(event.getMessage())) {
			event.setCancelled(true);
			player.sendMessage(TextComponent.fromLegacyText(Config.ERROR_PREFIX + "Merci de ne pas poster d'IPs ou de liens."));
			return;
		}

		UtariaBungee.getInstance().getLogger().info("url matcher: " + URL_PATTERN.matcher(event.getMessage().trim()).find());

		event.setMessage(this.filterCaps(event.getMessage()));
		// Fin du filtrage
	}

	@EventHandler
	public void onPlayerDisconnect(PlayerDisconnectEvent event) {
		this.lastSpeak.remove(event.getPlayer().getUniqueId());

		// On désactive le slow mode lors de la déconnexion du modérateur !
		if (this.slowModeOwner != null) {
			this.slowModeOwner = null;

			if (this.slow > 0) {
				this.slow = 0;
				ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(Config.MOD_PREFIX + "§3Le modérateur qui a activé le slow-mode s'est déconnecté. Il a donc été désactivé."));
			}
		}
	}

	public void setSlow(int slow, ProxiedPlayer owner) {
		if (slow == 0) this.lastSpeak.clear();

		this.slowModeOwner = owner.getUniqueId();
		this.slow = slow;
	}

	public int getSlow() {
		return this.slow;
	}

	private boolean checkForAdvertising(String message) {
		message = message.toLowerCase();
		return IP_PATTERN.matcher(message).find() || URL_PATTERN.matcher(message).find();
	}

	private boolean checkForFlood(String message) {
		for (String word : message.split("\\s+"))
			if (word.length() >= MAX_LENGTH_WORD && MAX_LENGTH_WORD != 0)
				return true;

		return false;
	}

	private String filterCaps(String message) {
		if (message.length() < 4) return message;

		double counter = 0.0D;
		double total = message.length();

		for (char c : message.toCharArray()) {
			if (Character.isUpperCase(c)) {
				counter += 1.0D;
			} else if (c == ' ') {
				total -= 1.0D;
			}
		}

		int capsPerc = (int) Math.round(counter / total * 100.0D);
		if (capsPerc > PERCENT_CAPS) return message.toLowerCase();

		return message;
	}

}
