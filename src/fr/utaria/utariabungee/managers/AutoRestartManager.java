package fr.utaria.utariabungee.managers;

import fr.utaria.utariabungee.Config;
import fr.utaria.utariabungee.UtariaBungee;
import fr.utaria.utariabungee.socket.SocketClient;
import fr.utaria.utariabungee.socket.custompackets.PacketInRestart;
import fr.utaria.utariabungee.tasks.AutoRestartTask;
import fr.utaria.utariabungee.utils.Utils;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AutoRestartManager {

    private final int   SEC_PER_DAY  = 86400;
    private final int[] BROADCAST_AT = new int[]{1800, 900, 600, 300, 60, 30, 10};

    private boolean _disabled   = false;
    private boolean refreshTime = false;
    private boolean canRestart  = true;
    private int[]   broadcasts  = new int[this.BROADCAST_AT.length];

    private boolean _restartInProgress = false;
    private int     _restartHour;
    private String  _lastRestartHour = "";
    private long    _lastTime;
    private long    _startTime;
    private int     _upTime;


    public AutoRestartManager() {
        this._startTime = System.currentTimeMillis();
        this._lastTime  = this._startTime;

        this._checkForDisable();
        this._reloadRefreshHour();

        new AutoRestartTask();
    }


    private long    getSecondsOnDay() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY) * 3600 +
               Calendar.getInstance().get(Calendar.MINUTE)      *   60 +
               Calendar.getInstance().get(Calendar.SECOND)             ;
    }
    private int     getSecondsPerDay() {
        return this.SEC_PER_DAY;
    }
    public  int     getRestartHour() {
        return this._restartHour;
    }
    public  int     getUpTime() {
        return this._upTime;
    }
    public  boolean restartIsInProgress() {
        return this._restartInProgress;
    }
    public  int     getSecondsBetweenStart() {
        return (int) (System.currentTimeMillis() - this._startTime) / 1000;
    }
	public  boolean isDisabled() {
		return this._disabled;
	}

    public void update() {
        long now  = System.currentTimeMillis();

        // Si le mode est désactivé, on recharge actualise seulement le temps en mémoire
        if( this._disabled ) {
            this._lastTime = now;
            return;
        }

        long diff = now - this._lastTime;

        this._upTime  -= diff;
        this._lastTime = now;

        // On raffraîchit l'heure de redémarrage et la désactivation depuis la base de données
        if( refreshTime ) {
            this._reloadRefreshHour();
            this._checkForDisable();
        }

        refreshTime = !refreshTime;


        // On regarde si un message doit être envoyé
        if( this._isBroadcastNeeded(this._upTime) )
            this._nextBroadcast();

        // Et enfin on regarde si le restart doit être effectué
        if( this._upTime <= 0 )
            this.doRestart();
    }

    public void doRestart() {
        if( !this.canRestart ) return;
        this.canRestart = false;

        // 1) On ejecte tous les joueurs
        Utils.kickAllPlayers(Config.autoRestartMessage);


        // 2) On passe le Bungee en mode restart pour éviter les nouvelles connexions
        this._restartInProgress = true;


        // 3) On redémarre le serveur survie en lui envoyant un packet de redémarrage
        UtariaBungee.getServerManager().getServerWithName("survie").restart();


        // 4) On redémarre les autres serveurs
		// Pas besoin pour le moment.


        // 5) On redémarre le serveur central
        // (maintenant fait par le SocketServerListener) (temporaire)
    }

    /*------------------------------*/
	/*  MISE A JOUR DES PLUGINS     */
	/*------------------------------*/
    public boolean updatePlugins() {
        File folderUptodatePlugins = new File(Config.UPTODATE_PLUGINS_FOLDER);
        List<File[]> pluginsToUpdate       = new ArrayList<>();


        // On créé le dossier avec les plugins à jour si besoin.
        if (!folderUptodatePlugins.exists())
            if (!folderUptodatePlugins.mkdirs())
                return false;

        // On regarde tous les plugins qu'il y a dans ce dossier,
        // et on enregistre ceux à mettre à jour.
        File[] pluginFiles = folderUptodatePlugins.listFiles();
        if (pluginFiles == null) return false;

        for (File pluginFile : pluginFiles) {
            File curPluginFile = new File(UtariaBungee.getInstance().getDataFolder().getParent() + "/" + pluginFile.getName());

            try {
                if (!curPluginFile.exists() || !com.google.common.io.Files.equal(curPluginFile, pluginFile))
                    pluginsToUpdate.add(new File[] {pluginFile, curPluginFile});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (pluginsToUpdate.size() == 0) return false;

        // Si des plugins doivent être mis à jour, on le fait,
        // en déplaçant les nouveaux dans le dossier "plugins/".
        for (File[] pluginOperation : pluginsToUpdate) {
            File from = pluginOperation[0];
            File to   = pluginOperation[1];

            InputStream in  = null;
            OutputStream out = null;

            // On copie le fichier via les méthodes binaires, pour éviter
            // d'avoir une erreur car le fichier est utilisé par un autre
            // processus.
            try {
                in  = new BufferedInputStream(new FileInputStream(from));
                out = new BufferedOutputStream(new FileOutputStream(to));

                byte[] buffer = new byte[1024];
                int    numRead;

                while ((numRead = in.read(buffer)) != -1)
                    out.write(buffer, 0, numRead);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (in  != null) in.close();
                    if (out != null) out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        return true;
    }


    private void _checkForDisable() {
        String disableRestart = Utils.getConfigValue("disable_autorestart");
		if( disableRestart == null ) return;

        if( disableRestart.equals("true") && !this._disabled ) {
            this._disabled = true;
            System.out.println(" Auto-restart disabled. ");
        }

        if( disableRestart.equals("false") && this._disabled ) {
            this._disabled = false;
            System.out.println(" Auto-restart enabled. ");
        }

    }
    private void _reloadUpTime() {
        int secs    = (int) this.getSecondsOnDay();
        int restart = this.getRestartHour();

        int diff    = restart - secs;
        if( diff < 0 ) diff = this.getSecondsPerDay() - secs + restart;

        this._upTime = diff * 1000;
    }
    private void _reloadRefreshHour() {
        String restartHour = Utils.getConfigValue("autorestart_hour");

        if( !this._lastRestartHour.equals(restartHour) ) {
            Config.autoRestartTime = restartHour;

            this._restartHour = Utils.timeToInt( Config.autoRestartTime );
            this._reloadUpTime();

            this._lastRestartHour = restartHour;
        }
    }

    private void    _nextBroadcast() {
        int    timeToBroadcast = -1;
        int    i               =  0;
        String castEnd;

        // On regarde quel temps il reste pour ensuite l'afficher aux joueurs.
        for (int time : this.BROADCAST_AT) {
            if (this._upTime / 1000.0 <= time && !Utils.intArrayContains(this.broadcasts, time)) {
                timeToBroadcast = time;
                this.broadcasts[i] = time;
            }

            i++;
        }

        if( timeToBroadcast == -1 ) return;

        if( timeToBroadcast <= 60 ) castEnd = timeToBroadcast        + " secondes";
        else                        castEnd = (timeToBroadcast / 60) + " minutes" ;

        BungeeCord.getInstance().broadcast(new TextComponent(Config.prefix + Utils.formatMessageColors("&cLe serveur va redémarrer automatiquement dans &6" + castEnd + "&c.")));

    }
    private boolean _isBroadcastNeeded(int upTime) {
        int i = 0;
        for (int time : this.BROADCAST_AT) {

            // Si le temps avant le redémarrage actuel est plus grand que le dernier temps affiché,
            // alors on le supprime : cela signifie qu'il y a eu un décallage de l'heure depuis la
            // base de données.
            if( this._upTime / 1000.0 > time && Utils.intArrayContains(this.broadcasts, time) )
                this.broadcasts[i] = 0;

            // Si le temps avant le redémarrage est inférieur au temps testé, alors on retourne true
            // pour pouvoir afficher ce temps testé aux joueurs.
            if (upTime / 1000.0 <= time && !Utils.intArrayContains(this.broadcasts, time))
                return true;

            i++;
        }

        return false;
    }

}
