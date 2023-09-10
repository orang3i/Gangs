package com.orang3i.gangs.commands;

import com.j256.ormlite.dao.Dao;
import com.orang3i.gangs.Gangs;
import com.orang3i.gangs.database.entities.PlayerStats;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

import static net.kyori.adventure.text.event.ClickEvent.runCommand;

public class GangsCommands implements CommandExecutor {

    private final Gangs gangs;
    public GangsCommands(Gangs gangs){
        this.gangs = gangs;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player =  (Player) sender;
        Dao<PlayerStats,String> dao = gangs.getService().getDao();

        //START OF INVITE SUBCOMMAND
        if(args[0].equals("invite") && args.length==2){

            List<String> ranks = (List<String>) gangs.getConfig().getList("gangs.ranks-with-invite-perms");
            try {
                if(ranks.contains(gangs.getService().getPlayerStats(player).getRank())){
                    Player invitee = null;
                    if(Bukkit.getPlayer(args[1])!=null){
                        invitee = Bukkit.getPlayer(args[1]);
                    }

                    if(invitee != null) {
                        gangs.adventure().player(invitee).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>you are invited to join " + gangs.getService().getPlayerStats(player).getGang() + "</gradient>"));

                        gangs.adventure().player(invitee).sendMessage((MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c><bold>[ACCEPT]</gradient>")).clickEvent(runCommand("/adventurecommand sendinvite " + args[1] + " " + gangs.getService().getPlayerStats(player).getGang())).hoverEvent(HoverEvent.showText(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>click me to join " + gangs.getService().getPlayerStats(player).getGang()))));

                        gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>successfully invited " + args[1] + "</gradient>"));
                    }else{
                        gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>invite not sent "+ args[1]  + " is offline</gradient>"));
                    }

                }
                else {

                    gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize( "<gradient:#8e28ed:#f52c2c>you are not allowed to send invites on behalf of "+gangs.getService().getPlayerStats(player).getGang()+"</gradient>"));

                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        //END OF INVITE SUBCOMMAND
        }
        return true;
    }
}
