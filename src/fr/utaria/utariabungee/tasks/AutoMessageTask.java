package fr.utaria.utariabungee.tasks;

import fr.utaria.utariabungee.Config;
import fr.utaria.utariabungee.UtariaBungee;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AutoMessageTask implements Runnable {

	private List<String> messages = new ArrayList<>();
	private int          current;


	public AutoMessageTask() {

		this.messages = Arrays.asList(
			"§eRejoignez §enotre §ecommunauté §een §evous §econnectant §esur §enotre §eserveur §evocal §e: §6§lts.utaria.fr§7.",
			"§eDes §ebugs, §eou §esimplement §edes §eavis §e? §ePostez-les §esur §enotre §esite §edédié §a§lfeedback.utaria.fr§7.",
			// "§eVous pouvez postuler à cette adresse pour rejoindre §enotre §estaff §e: §6recrutement@utaria.fr§e.",
			"§eLe serveur est en version §b§lALPHA§e : §edes §ebugs §epeuvent §eapparaitre, §emerci §ed'être §atolérant§e.",
			"§eL'économie a évolué, vous §eavez §cperdu §cla §cmoitié §cde §cvotre §cargent... §eMais §eles §evillageois §evous §eattendent §epour §eéchanger §eavec §evous §e!",
			"§eEnvie §ede §ePVP §e? §eTestez §ele §enouveau §emini-jeu §61vs1 §edisponible §eau §evillage §e"
		);
		this.current = -1;

		BungeeCord.getInstance().getScheduler().schedule(UtariaBungee.getInstance(), this, 1, 5, TimeUnit.MINUTES);
	}


	@Override
	public void run() {
		this.current = (this.current + 1) % this.messages.size();

		String message = this.messages.get(this.current);
		BungeeCord.getInstance().broadcast(new TextComponent(Config.prefix + message));
	}

}
