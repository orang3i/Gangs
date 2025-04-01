package com.orang3i.gangs.Adventure;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class MiniMessageDeserializer {
    public static Component mm(String miniMessageString) {
        String finalMiniMessage = "<gradient:#8e28ed:#f52c2c>"+miniMessageString+"</gradient>";
        return MiniMessage.miniMessage().deserialize(finalMiniMessage);
    }
}
