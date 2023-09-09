package com.orang3i.gangs.commands;

import com.orang3i.gangs.Gangs;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class SetGang implements CommandExecutor {
    private final Gangs gangs;

    public SetGang(Gangs gangs){
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
            StringBuilder concat_gang = new StringBuilder();

            for(int i = 0;i<=args.length-1;i++){
                concat_gang.append(args[i]+" ");
            }
            String gang_concacted = concat_gang.toString();
            System.out.println(gang_concacted);
            gangs.getService().setPlayerGang(player,gang_concacted);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize( "<gradient:#8e28ed:#f52c2c>successfully set gang " + gangs.getService().getPlayerStats(player).getGang() +"</gradient>"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
