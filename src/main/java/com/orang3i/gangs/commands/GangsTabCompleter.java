package com.orang3i.gangs.commands;

import com.orang3i.gangs.Gangs;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GangsTabCompleter implements TabCompleter {

    private final Gangs gangs;

    public GangsTabCompleter(Gangs gangs) {
        this.gangs = gangs;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> list = new ArrayList<String>();
        if (command.getName().equalsIgnoreCase("gangs") ) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if(args.length<=1) {
                    list.add("invite");
                    list.add("set-rank");
                    list.add("create");
                    list.add("leave");
                    list.add("kick");
                }

                List<String> cmds1i = new ArrayList<>();
                cmds1i.add("invite");
                cmds1i.add("kick");
                if(cmds1i.contains(args[0]) && args.length<=2){

                   List<Player> onlinePlayers = (List<Player>) Bukkit.getOnlinePlayers();
                   onlinePlayers.forEach(l-> list.add(l.getDisplayName()));
                }

                List<String> cmds2i = new ArrayList<>();
                cmds2i.add("set-rank");
                if(cmds2i.contains(args[0]) && args.length==2){
                    List<Player> onlinePlayers = (List<Player>) Bukkit.getOnlinePlayers();
                    onlinePlayers.forEach(l-> list.add(l.getDisplayName()));

                }
                if(cmds2i.contains(args[0]) && args.length==3){


                    List<String> ranks = (List<String>) gangs.getConfig().getList("gangs.ranks");
                    ranks.forEach(l-> list.add(l));
                }


            }
        }

        return list;
    }
}

