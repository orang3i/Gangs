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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;


public class SetRankCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> setRank = Commands.literal("set-rank").then(Commands.argument("PlayerName", StringArgumentType.string()).then(Commands.argument("Rank", StringArgumentType.string())).executes(SetRankCommand::setRankLogic));

    private static int setRankLogic(CommandContext<CommandSourceStack> ctx) {
        try {
            Player subject = Bukkit.getPlayer(ctx.getArgument("PlayerName", String.class));
            Player executor = (Player) ctx.getSource().getExecutor();
            String finalRank = ctx.getArgument("Rank", String.class);
            String subjectRank = Gangs.getPluginStatic().getDAO().getPlayerRank(subject.getUniqueId());
            String executorRank = Gangs.getPluginStatic().getDAO().getPlayerRank(executor.getUniqueId());
            List<String> rankerRanks = Gangs.getPluginStatic().getConfig().getStringList("gangs.ranks-with-rank-perms");
            List<String> ranks = Gangs.getPluginStatic().getConfig().getStringList("gangs.ranks");
            int subjectRankIndex = rankerRanks.indexOf(subjectRank);
            int executorRankIndex = rankerRanks.indexOf(executorRank);
            int finalRankIndex = rankerRanks.indexOf(finalRank);
            if (rankerRanks.contains(executorRank)) {
                if (executorRankIndex > finalRankIndex && executorRankIndex > subjectRankIndex) {
                    Gangs.getPluginStatic().getDAO().setPlayerRank(subject.getUniqueId(), finalRank);
                    Component subjectMessage;
                    if (finalRankIndex > subjectRankIndex) {
                        subjectMessage = MiniMessageDeserializer.mm("You are now promoted to " + finalRank);
                    } else if (finalRankIndex < subjectRankIndex) {
                        subjectMessage = MiniMessageDeserializer.mm("You are now promoted to " + finalRank);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Command.SINGLE_SUCCESS;
    }
}
