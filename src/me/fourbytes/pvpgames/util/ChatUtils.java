package me.fourbytes.pvpgames.util;

import org.bukkit.util.ChatPaginator;

/**
 * Created by fourbytes on 15/07/13.
 */
public class ChatUtils {
    public static String centerText(String message, String character) {
        String newmessage = "";
        for (int x = 0; x <= ((ChatPaginator.AVERAGE_CHAT_PAGE_WIDTH / 2) - message.length()); x++) {
            newmessage += character;
        }

        newmessage += message;

        for (int x = 0; x <= ((ChatPaginator.AVERAGE_CHAT_PAGE_WIDTH / 2) - message.length()); x++) {
            newmessage += character;
        }

        return newmessage;
    }
}
