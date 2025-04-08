package com.orang3i.gangs.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class CommandRoot {
    private static LiteralArgumentBuilder<CommandSourceStack> gangsCommandRoot = Commands.literal("gangs");

    public static LiteralArgumentBuilder<CommandSourceStack> getGangsCommandRoot() {
        return gangsCommandRoot;
    }

    public static void setGangsCommandRoot(LiteralArgumentBuilder<CommandSourceStack> gangsCommandRoot) {
        CommandRoot.gangsCommandRoot = gangsCommandRoot;
    }
}
