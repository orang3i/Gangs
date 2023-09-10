package com.orang3i.gangs.commands;

import com.orang3i.gangs.Gangs;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

public class AdventureCommand implements CommandExecutor {

    private final Gangs gangs;
    public AdventureCommand(Gangs gangs){
        this.gangs = gangs;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args[0].equals("sendinvite")){
            try {
                StringBuilder concat_gang = new StringBuilder();

                for(int i = 2;i<=args.length-2;i++){
                    concat_gang.append(args[i]+" ");
                }
                String gang_concacted = concat_gang.toString();
                gangs.getService().setPlayerGang(Bukkit.getPlayer(args[1]),gang_concacted.trim());
                List<String> ranks = (List<String>) gangs.getConfig().getList("gangs.ranks");

                gangs.getService().setPlayerRank(Bukkit.getPlayer(args[1]),ranks.get(0));
                gangs.adventure().player(Bukkit.getPlayer(args[1])).sendMessage(MiniMessage.miniMessage().deserialize( "<gradient:#8e28ed:#f52c2c>you are now a "+ gangs.getService().getPlayerStats(Bukkit.getPlayer(args[1])).getRank()+ " at "+gangs.getService().getPlayerStats(Bukkit.getPlayer(args[1])).getGang()+"! </gradient>"));
                if(Bukkit.getPlayerExact(args[args.length-1])!=null) {
                    Player sent = Bukkit.getPlayer(args[args.length - 1]);
                    gangs.adventure().player(sent).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>" + args[1] + " is now a " + gangs.getService().getPlayerStats(Bukkit.getPlayer(args[1])).getRank() + " at " + gangs.getService().getPlayerStats(Bukkit.getPlayer(args[1])).getGang() + "! </gradient>"));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }
}
