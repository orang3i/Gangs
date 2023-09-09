package com.orang3i.gangs.commands;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.orang3i.gangs.Gangs;
import com.orang3i.gangs.database.GangsService;
import com.orang3i.gangs.database.entities.PlayerStats;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.mysql.cj.util.SaslPrep.prepare;

public class GetGangMembers implements CommandExecutor {
    private final Gangs gangs;

    public GetGangMembers(Gangs gangs){
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

            Dao<PlayerStats,String> dao= gangs.getService().getDao();
            QueryBuilder<PlayerStats,String> qb = dao.queryBuilder();
// select 2 aggregate functions as the return
            qb.where().eq("gang","tokyo manji");
// the results will contain 2 string values for the min and max
           GenericRawResults<String[]> rawResults = dao.queryRaw(qb.prepareStatementString());
            // page through the results
            List<String[]> results = rawResults.getResults();

            results.forEach(s -> System.out.println(s[1]));

            results.forEach(s -> {
                try {
                    System.out.println(s[0]);
                    gangs.adventure().player(Bukkit.getPlayer(UUID.fromString(s[0].toString()))).sendMessage(MiniMessage.miniMessage().deserialize( "<gradient:#8e28ed:#f52c2c>you are summoned by leader of " + gangs.getService().getPlayerStats(Bukkit.getPlayer(UUID.fromString(s[0].toString()))).getGang()  +"</gradient>"));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });



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
