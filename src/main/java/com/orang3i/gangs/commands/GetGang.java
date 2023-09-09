package com.orang3i.gangs.commands;

import com.orang3i.gangs.Gangs;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class GetGang implements CommandExecutor {
    private final Gangs gangs;

    public GetGang(Gangs gangs){
        this.gangs = gangs;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;

        try {
            if(!gangs.getService().playerExists(player)){
                System.out.println("player does not exist yet");
                gangs.getService().addPlayer((player));
                System.out.println("player hopefully exists now ");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        try {
            gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize( "<gradient:#8e28ed:#f52c2c>you belong to " + gangs.getService().getPlayerStats(player).getGang() +"</gradient>"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
