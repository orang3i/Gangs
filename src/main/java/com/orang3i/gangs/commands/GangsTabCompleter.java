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
                    list.add("disband");
                    list.add("friendlyfire");
                    list.add("gang-chat-toggle");
                    list.add("ally-chat-toggle");
                    list.add("ally-request");
                    list.add("ally-neutral");
                    list.add("friendlyfire-allies ");
                    list.add("deposit");
                    list.add("withdraw");
                    list.add("balance");
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

                List<String> cmds3i = new ArrayList<>();
                cmds3i.add("friendlyfire");
                if(cmds3i.contains(args[0]) && args.length==2){
                    List<String> vals = new ArrayList<>();
                    vals.add("true");
                    vals.add("false");
                    vals.forEach(l-> list.add(l));
                }


            }
        }

        return list;
    }
}

