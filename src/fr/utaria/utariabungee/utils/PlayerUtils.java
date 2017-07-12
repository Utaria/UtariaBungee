package fr.utaria.utariabungee.utils;

import fr.utaria.utariabungee.utils.text.FontInfo;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;


public class PlayerUtils {

    public final static int TCHATBOX_WIDTH = 154;


    public static void sendCenteredMessage(ProxiedPlayer player, String message) {
        if(message == null || message.equals("")) {
            player.sendMessage("");
            return;
        }

        message = ChatColor.translateAlternateColorCodes('&', message);

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for(char c : message.toCharArray()){
            if(c == '§'){
                previousCode = true;
                continue;
            }else if( previousCode ){
                previousCode = false;
                if(c == 'l' || c == 'L'){
                    isBold = true;
                    continue;
                }else isBold = false;
            }else{
                FontInfo fI = FontInfo.getFontInfo(c);
                messagePxSize += isBold ? fI.getBoldLength() : fI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = TCHATBOX_WIDTH - halvedMessageSize;
        int spaceLength = FontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while(compensated < toCompensate){
            sb.append(" ");
            compensated += spaceLength;
        }
        player.sendMessage(sb.toString() + message);
    }
    public static void sendHorizontalLine(ProxiedPlayer player) {
        sendHorizontalLine(player, null);
    }
    public static void sendHorizontalLine(ProxiedPlayer player, ChatColor color) {
        if(color == null) color = ChatColor.WHITE;
        sendCenteredMessage(player, color + PlayerUtils._repeatText("§m-", 53));
    }
    public static void sendHorizontalLineWithText(ProxiedPlayer player, String text, ChatColor color) {
        int n = 55;
        if(color == null) color = ChatColor.WHITE;

        n -= text.length() + 4;

        sendCenteredMessage(player, color + PlayerUtils._repeatText("§m-", n / 2) + "§r" + color + "[ " + text + color + " ]" + PlayerUtils._repeatText("§m-", n / 2));
    }


    private static String _repeatText(String str, int times) {
        String r = "";
        for( int i = 0; i < times; i++ ) r += str;
        return r;
    }

}
