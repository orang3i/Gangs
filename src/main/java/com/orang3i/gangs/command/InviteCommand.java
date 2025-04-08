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

public class InviteCommand {
    private LiteralArgumentBuilder<CommandSourceStack> invite = CommandRoot.getGangsCommandRoot().then(Commands.literal("invite")).then(Commands.argument("PlayerName", StringArgumentType.string()).executes(InviteCommand::inviteLogic));

    private static int inviteLogic(CommandContext<CommandSourceStack> ctx) {
        Player invited = Bukkit.getPlayer(ctx.getArgument("PlayerName", String.class));
        Player inviter = (Player) ctx.getSource().getExecutor();
        try {
            Component invite = MiniMessageDeserializer.mm("You are invited by " + invited.getName() + " to become a member of " + Gangs.getPluginStatic().getDAO().getPlayerGang(inviter.getUniqueId()));
            Component accept = MiniMessageDeserializer.mm("[ACCEPT]", true).clickEvent(ClickEvent.callback(audience -> {
                try {
                    Gangs.getPluginStatic().getDAO().setPlayerGang(invited.getUniqueId(), Gangs.getPluginStatic().getDAO().getPlayerGang(inviter.getUniqueId()));
                    Gangs.getPluginStatic().getDAO().setPlayerRank(invited.getUniqueId(), "rookie");
                    invited.sendMessage(MiniMessageDeserializer.mm("You are now a rookie at "+ Gangs.getPluginStatic().getDAO().getPlayerGang(invited.getUniqueId()),true));
                    inviter.sendMessage(MiniMessageDeserializer.mm(invited.getName()+" has accepted the invite to become a member of your gang!"));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }));
            invited.sendMessage(invite);
            invited.sendMessage(accept);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Command.SINGLE_SUCCESS;
    }
}
