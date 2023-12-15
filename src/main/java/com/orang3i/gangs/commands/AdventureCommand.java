package com.orang3i.gangs.commands;

import com.orang3i.gangs.Gangs;
import com.orang3i.gangs.listeners.PlayerMoveEventListener;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
        if(args[0].equals("sendallyinvite")){
            try {
                StringBuilder sb = new StringBuilder();

                // Appends characters one by one
                //sendallyinvite 9 7 tokyo sus sus men ORANG3I orang3ikr
                //0123456789012345678
                // convert in string

                String str = String.join(" ", args);
                String concat_ganga = StringUtils.substringBetween(str, "$%A", "$%A");
                String concat_gangb =StringUtils.substringBetween(str, "$%B", "$%B") ;
                System.out.println(concat_ganga.trim());
                System.out.println(concat_gangb.trim());
                if(gangs.getService().getAllies(concat_gangb.trim()).contains(concat_ganga.trim())){

                    if (Bukkit.getPlayerExact(args[args.length - 1]) != null) {
                        System.out.println(args[args.length - 1]);
                        Player sent = Bukkit.getPlayer(gangs.getService().getPlayerUUID(args[args.length - 1]));
                        gangs.adventure().player(sent).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>you are already allies with " + concat_ganga + "! </gradient>"));
                    }
                }else {
                    gangs.getService().setAllies(concat_gangb.trim(), concat_ganga.trim());
                    gangs.getService().setAllies(concat_ganga.trim(), concat_gangb.trim());
                    gangs.adventure().player(Bukkit.getPlayer(args[args.length - 1])).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>your gang is now an ally of " + concat_gangb + "! </gradient>"));
                    if (Bukkit.getPlayerExact(args[args.length - 2]) != null) {
                        Player sent = Bukkit.getPlayer(args[args.length - 2]);
                        gangs.adventure().player(sent).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>your gang is now an ally of " + concat_ganga + "! </gradient>"));
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        if(args[0].equals("sendallyfriendlyinvite")){
            try {

                // Appends characters one by one

                //sendallyinvite 9 7 tokyo sus sus men ORANG3I orang3ikr
                //0123456789012345678
                //sendallyfriendlyinvite 6 8 nycmen tokyomen ORANG3I orang3ikr
                //012345678901234567890234567
                // convert in string
                String str = String.join(" ", args);
                String concat_ganga = StringUtils.substringBetween(str, "$%A", "$%A");
                String concat_gangb =StringUtils.substringBetween(str, "$%B", "$%B") ;

                System.out.println(concat_ganga.trim());
                System.out.println(concat_gangb.trim());
                if(gangs.getService().getAllies(concat_gangb.trim()).contains(concat_ganga.trim())){
                    System.out.println(args.length);
                    System.out.println(args[args.length-1]);
                    if(args[args.length-1].equals("true")){
                        if(gangs.getService().getAlliesFriendlyFire(concat_ganga).contains(concat_gangb)){
                            if (Bukkit.getPlayerExact(args[args.length - 2]) != null) {
                                System.out.println(args[args.length - 2]);
                                Player sent = Bukkit.getPlayer(gangs.getService().getPlayerUUID(args[args.length - 2]));
                                gangs.adventure().player(sent).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>friendly fire is already on!</gradient>"));
                            }                     }else {
                            System.out.println("wee");
                            gangs.getService().setFriendlyFireAllies(concat_ganga.trim(), concat_gangb.trim());
                            gangs.getService().setFriendlyFireAllies(concat_gangb.trim(), concat_ganga.trim());
                            if (Bukkit.getPlayerExact(args[args.length - 2]) != null) {
                                System.out.println(args[args.length - 2]);
                                Player sent = Bukkit.getPlayer(gangs.getService().getPlayerUUID(args[args.length - 2]));
                                gangs.adventure().player(sent).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>friendly fire is on</gradient>"));
                            }
                            if (Bukkit.getPlayerExact(args[args.length - 3]) != null) {
                                Player sent = Bukkit.getPlayer(args[args.length - 3]);
                                gangs.adventure().player(sent).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>friendly fire is on</gradient>"));
                            }
                        }
                    }else {
                        gangs.getService().removeAlliesFriendlyFire(concat_ganga.trim(),concat_gangb.trim());
                        gangs.getService().removeAlliesFriendlyFire(concat_gangb.trim(),concat_ganga.trim());
                        if (Bukkit.getPlayerExact(args[args.length - 2]) != null) {
                            System.out.println(args[args.length - 2]);
                            Player sent = Bukkit.getPlayer(gangs.getService().getPlayerUUID(args[args.length - 2]));
                            gangs.adventure().player(sent).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>friendly fire is off</gradient>"));
                        }
                        if (Bukkit.getPlayerExact(args[args.length - 3]) != null) {
                            Player sent = Bukkit.getPlayer(args[args.length - 3]);
                            gangs.adventure().player(sent).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>friendly fire is off</gradient>"));
                        }
                    }
                }else {
                    if (Bukkit.getPlayerExact(args[args.length - 1]) != null) {
                        System.out.println(args[args.length - 1]);
                        Player sent = Bukkit.getPlayer(gangs.getService().getPlayerUUID(args[args.length - 1]));
                        gangs.adventure().player(sent).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>you are not allies</gradient>"));
                    }                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        if(args[0].equals("summon")){


            Player p = Bukkit.getPlayer(args[1]);

            System.out.println(args[1]+" "+args[2]);
            Player summoner = Bukkit.getPlayer(args[2]);

            PlayerMoveEventListener.appendTeleport(p);
            gangs.adventure().player(p).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>teloporting don't move for 5 seconds</gradient>"));

            gangs.getServer().getScheduler().scheduleSyncDelayedTask(gangs, new Runnable() {
                public void run() {
                    if (PlayerMoveEventListener.teleport.contains(p)) {
                        System.out.println("WAited");
                        Location loc = new Location(summoner.getWorld(), summoner.getLocation().getX(), summoner.getLocation().getY(), summoner.getLocation().getZ());
                        p.teleport(loc);
                        PlayerMoveEventListener.popTeleport(p);
                    }

                }
            }, 20 * 5); // 20 (one second in ticks) * 5 (seconds to wait)

        }
        return true;
    }
}
