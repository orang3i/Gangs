package com.orang3i.gangs.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.orang3i.gangs.Gangs;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class CommandRoot {
    public static LiteralArgumentBuilder<CommandSourceStack> gangsCommandRoot = Commands.literal("gangs");
    public static void register() {
        gangsCommandRoot.then(CreateCommand.create);
        gangsCommandRoot.then(InviteCommand.invite);
        gangsCommandRoot.then(SetRankCommand.setRank);
    }
}
