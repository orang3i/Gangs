package com.orang3i.gangs.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class InviteCommand {
    private LiteralArgumentBuilder<CommandSourceStack> invite = CommandRoot.getGangsCommandRoot().then(Commands.literal("invite")).then(Commands.argument("PlayerName" , StringArgumentType.string()).executes(InviteCommand::inviteLogic));

    private static int inviteLogic(CommandContext<CommandSourceStack> ctx) {
       Player invited = Bukkit.getPlayer(ctx.getArgument("PlayerName", String.class));

        return Command.SINGLE_SUCCESS;
    }
}
