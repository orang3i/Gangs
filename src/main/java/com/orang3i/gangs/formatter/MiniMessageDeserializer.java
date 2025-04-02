package com.orang3i.gangs.formatter;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class MiniMessageDeserializer {

    private static final String gradient = "<gradient:#8e28ed:#f52c2c>";

    public static Component mm(String miniMessageString) {
        String finalMiniMessage = gradient + miniMessageString + "</gradient>";
        return MiniMessage.miniMessage().deserialize(finalMiniMessage);
    }

    public static Component mm(String miniMessageString, Boolean bold) {
        String finalMiniMessage;

        if (bold) {
            finalMiniMessage = gradient + "<bold>" + miniMessageString + "</gradient>";
        } else {
            finalMiniMessage = "<gradient:#8e28ed:#f52c2c>" + miniMessageString + "</gradient>";
        }
        return MiniMessage.miniMessage().deserialize(finalMiniMessage);
    }

}
