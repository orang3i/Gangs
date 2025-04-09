package com.orang3i.gangs.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.orang3i.gangs.Gangs;
import com.orang3i.gangs.formatter.MiniMessageDeserializer;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;

public class InviteCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> invite = Commands.literal("invite").then(Commands.argument("PlayerName", StringArgumentType.string()).executes(InviteCommand::inviteLogic));

    private static int inviteLogic(CommandContext<CommandSourceStack> ctx) {
        try {
            Player subject = Bukkit.getPlayer(ctx.getArgument("PlayerName", String.class));
            Player executor = (Player) ctx.getSource().getExecutor();
            Component invite = MiniMessageDeserializer.mm("You are invited by " + subject.getName() + " to become a member of " + Gangs.getPluginStatic().getDAO().getPlayerGang(executor.getUniqueId()));
            Component accept = MiniMessageDeserializer.mm("[ACCEPT]", true).clickEvent(ClickEvent.callback(audience -> {
                try {
                    String gang = Gangs.getPluginStatic().getDAO().getPlayerGang(executor.getUniqueId());
                    List<String> ranks = Gangs.getPluginStatic().getConfig().getStringList("gangs.ranks");
                    Gangs.getPluginStatic().getDAO().setPlayerGang(subject.getUniqueId(), gang);
                    Gangs.getPluginStatic().getDAO().setPlayerRank(subject.getUniqueId(), ranks.getFirst());
                    subject.sendMessage(MiniMessageDeserializer.mm("You are now a " + ranks.getFirst() + " at " + gang, true));
                    executor.sendMessage(MiniMessageDeserializer.mm(subject.getName() + " has accepted your invite and is now a " + ranks.getFirst() + " at " + gang, true));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }));
            subject.sendMessage(invite);
            subject.sendMessage(accept);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Command.SINGLE_SUCCESS;
    }
}
