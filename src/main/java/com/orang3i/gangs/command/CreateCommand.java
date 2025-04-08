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


public class CreateCommand {
    private LiteralArgumentBuilder<CommandSourceStack> create = CommandRoot.getGangsCommandRoot().then(Commands.literal("create")).then(Commands.argument("GangName", StringArgumentType.string()).executes(CreateCommand::createLogic));

    private static int createLogic(CommandContext<CommandSourceStack> ctx){
        String gangName = ctx.getArgument("GangName", String.class);
        try {
            Boolean success = Gangs.getPluginStatic().getDAO().createGang(gangName);
            Player executor = (Player) ctx.getSource().getExecutor();
            if(success){
                Gangs.getPluginStatic().getDAO().setPlayerRank(executor.getUniqueId(), gangName);
                Component message = MiniMessageDeserializer.mm("You are now the leader of " + gangName,true);
                executor.sendMessage(message);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return Command.SINGLE_SUCCESS;
    }
}