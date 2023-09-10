package com.orang3i.gangs.commands;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;
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
import java.util.UUID;

import static net.kyori.adventure.text.event.ClickEvent.runCommand;

public class GangsCommands implements CommandExecutor {

    private final Gangs gangs;
    public GangsCommands(Gangs gangs){
        this.gangs = gangs;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        Dao<PlayerStats, String> dao = gangs.getService().getDao();

        //START OF INVITE SUBCOMMAND
        if (args[0].equals("invite") && args.length == 2) {

            List<String> ranks = (List<String>) gangs.getConfig().getList("gangs.ranks-with-invite-perms");
            try {
                if (ranks.contains(gangs.getService().getPlayerStats(player).getRank())) {
                    Player invitee = null;
                    if (Bukkit.getPlayerExact(args[1]) != null) {
                        invitee = Bukkit.getPlayer(args[1]);
                    }

                    if (invitee != null) {
                        if(!gangs.getService().getPlayerStats(player).getGang().equals(gangs.getService().getPlayerStats(invitee).getGang())) {
                            gangs.adventure().player(invitee).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>you are invited to join " + gangs.getService().getPlayerStats(player).getGang() + "</gradient>"));

                            gangs.adventure().player(invitee).sendMessage((MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c><bold>[ACCEPT]</gradient>")).clickEvent(runCommand("/adventurecommand sendinvite " + args[1] + " " + gangs.getService().getPlayerStats(player).getGang() + " " + player.getDisplayName())).hoverEvent(HoverEvent.showText(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>click me to join " + gangs.getService().getPlayerStats(player).getGang()))));

                            gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>successfully invited " + args[1] + "</gradient>"));
                        }else {
                            gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>invite not sent " + args[1] + " is already in your gang</gradient>"));

                        }
                    } else {
                        gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>invite not sent " + args[1] + " is offline</gradient>"));
                    }

                } else {

                    gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>you are not allowed to send invites on behalf of " + gangs.getService().getPlayerStats(player).getGang() + "</gradient>"));

                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            //END OF INVITE SUBCOMMAND
        }
        //START OF SET-RANK SUBCOMMAND
        if (args[0].equals("set-rank") && args.length == 3) {

            List<String> ranks = (List<String>) gangs.getConfig().getList("gangs.ranks-with-rank-perms");
            try {
                if (ranks.contains(gangs.getService().getPlayerStats(player).getRank())) {


                    Player ranker = null;
                    if (Bukkit.getPlayerExact(args[1]) != null) {

                        ranker = Bukkit.getPlayer(args[1]);
                    }

                    if (gangs.getService().getPlayerUUID(args[1]) != null) {

                    if (gangs.getService().getPlayerStats(gangs.getService().getPlayerUUID(args[1])).getGang().equals(gangs.getService().getPlayerStats(player).getGang())) {
                        List<String> validRanks = (List<String>) gangs.getConfig().getList("gangs.ranks");
                        if (validRanks.contains(args[2])) {
                            gangs.getService().setPlayerRank(gangs.getService().getPlayerUUID(args[1]), args[2]);


                            if (ranker != null) {
                                gangs.adventure().player(ranker).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>you are now ranked as a " + gangs.getService().getPlayerStats(gangs.getService().getPlayerUUID(args[1])).getRank() + "</gradient>"));
                            }
                            gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>" + args[1] + " is now a " + gangs.getService().getPlayerStats(gangs.getService().getPlayerUUID(args[1])).getRank() + "</gradient>"));

                        }
                    }else{
                        gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>" + args[1] + " is not part of your gang</gradient>"));

                    }
                }else{
                        gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>" + args[1] + " is not part of your gang</gradient>"));

                    }
            }
        } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        //END OF SET-RANK SUBCOMMAND
        return true;
    }
}
