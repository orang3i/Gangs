package com.orang3i.gangs.commands;

import com.j256.ormlite.stmt.PreparedQuery;
import com.orang3i.gangs.Gangs;
import com.orang3i.gangs.database.GangsService;
import com.orang3i.gangs.database.entities.PlayerStats;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

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

            PreparedQuery preparedQuery = gangs.getService().getDao().queryBuilder().where().like("gang","tokyomanji").prepare();

            List<PlayerStats> gang_members = gangs.getService().getDao().query(preparedQuery);

            System.out.println(gang_members);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize( "<gradient:#8e28ed:#f52c2c>you belong to " + "mems" +"</gradient>"));
        return true;
    }
}
