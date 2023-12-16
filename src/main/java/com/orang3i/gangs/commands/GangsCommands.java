package com.orang3i.gangs.commands;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;
import com.orang3i.gangs.Gangs;
import com.orang3i.gangs.database.entities.PlayerStats;
import com.orang3i.gangs.database.entities.ServerStats;
import com.orang3i.gangs.listeners.PlayerMoveEventListener;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.lang.Math;

import static net.kyori.adventure.text.event.ClickEvent.runCommand;

public class GangsCommands implements CommandExecutor {

    private final Gangs gangs;
    public GangsCommands(Gangs gangs){
        this.gangs = gangs;
    }

    public static HashMap<String, Long> tpCooldowns = new HashMap<String, Long>();
    public static HashMap<String, Long> challengeCooldowns = new HashMap<String, Long>();

    public static HashMap<String, Long> allyreqCooldowns = new HashMap<String, Long>();

    public static HashMap<String, Long> inviteCooldowns = new HashMap<String, Long>();
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        Dao<PlayerStats, String> dao = gangs.getService().getPlayerStatsDao();

        //START OF RELOAD COMMAND
        if (args[0].equals("reload") && args.length == 1) {

            gangs.reloadConfig();
            gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>gangs successfully reloaded</gradient>"));


        }
        //END OF RELOAD COMMAND


        //START OF INVITE SUBCOMMAND
        if (args[0].equals("invite") && args.length == 2) {

            List<String> ranks = (List<String>) gangs.getConfig().getList("gangs.ranks-with-invite-perms");
            try {
                if (ranks.contains(gangs.getService().getPlayerStats(player).getRank())) {
                    long cooldownTime = gangs.getConfig().getLong("gangs.invite-cooldown"); // Get number of seconds from wherever you want
                    if (!inviteCooldowns.containsKey(sender.getName())) {
                        inviteCooldowns.put(sender.getName(), System.currentTimeMillis());

                    }
                    long secondsLeft = ((inviteCooldowns.get(sender.getName()) / 1000) + cooldownTime) - (System.currentTimeMillis() / 1000);
                    if (secondsLeft > 0) {
                        // Still cooling down

                        gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>please wait " + secondsLeft + " seconds before using that command</gradient>"));
                    }

                    else {

                        inviteCooldowns.put(sender.getName(), System.currentTimeMillis());

                    Player invitee = null;
                    if (Bukkit.getPlayerExact(args[1]) != null) {
                        invitee = Bukkit.getPlayer(args[1]);
                    }

                    if (invitee != null) {
                        if (!gangs.getService().getPlayerStats(player).getGang().equals(gangs.getService().getPlayerStats(invitee).getGang())) {
                            gangs.adventure().player(invitee).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>you are invited to join " + gangs.getService().getPlayerStats(player).getGang() + "</gradient>"));

                            gangs.adventure().player(invitee).sendMessage((MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c><bold>[ACCEPT]</gradient>")).clickEvent(runCommand("/adventurecommand sendinvite " + args[1] + " " + gangs.getService().getPlayerStats(player).getGang() + " " + player.getName())).hoverEvent(HoverEvent.showText(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>click me to join " + gangs.getService().getPlayerStats(player).getGang()))));

                            gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>successfully invited " + args[1] + "</gradient>"));
                        } else {
                            gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>invite not sent " + args[1] + " is already in your gang</gradient>"));

                        }
                    } else {
                        gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>invite not sent " + args[1] + " is offline</gradient>"));
                    }

                }
            }
                else {

                    gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>you are not allowed to send invites on behalf of " + gangs.getService().getPlayerStats(player).getGang() + "</gradient>"));

                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
        //END OF INVITE SUBCOMMAND

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
            }else {
                    gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>your rank doesn't allow you to set rank to poeple</gradient>"));

                }
        } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        //END OF SET-RANK SUBCOMMAND

        //START OF CREATE SUBCOMMAND
        if(args[0].equals("create") && args.length>=2) {
            try {
                if(gangs.getService().getPlayerStats(player).getGang().equals("none")) {
                    StringBuilder concat_gang = new StringBuilder();

                    for (int i = 1; i <= args.length - 1; i++) {
                        concat_gang.append(args[i] + " ");
                    }

                    String gang_concacted = concat_gang.toString();
                    Boolean gangExists = false;
                    try {

                        try {
                            System.out.println("gang exists");
                            gangExists = gangs.getService().getRawResults("gangs",gang_concacted.trim()).get(0)[0].equals(gang_concacted.trim());
                        } catch (IndexOutOfBoundsException e) {

                        }

                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                    if (!gangExists) {
                        try {

                            gangs.getService().addGangs(gang_concacted.trim());
                            gangs.getService().setPlayerGang(player, gang_concacted.trim());
                            List<String> ranks = (List<String>) gangs.getConfig().getList("gangs.ranks");
                            gangs.getService().setPlayerRank(player, ranks.get(ranks.size() - 1));
                            gangs.getService().tempSolGangCreateAlly(gang_concacted.trim());
                            gangs.getService().tempSolGangCreateBase(gang_concacted.trim());
                            gangs.getService().tempSolGangCreateAllyFriendlyFire(gang_concacted.trim());
                            System.out.println(gangs.getService().getPlayerStats(player).getGang());
                            System.out.println(gangs.getService().getPlayerStats(player).getRank());
                            gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>gang successfully created you are now the leader of " + gang_concacted.trim() + "</gradient>"));

                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>the name " + gang_concacted.trim() + " is already taken</gradient>"));

                    }
                }else {
                    gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>leave your current gang to create a new gang</gradient>"));
                }
            } catch (SQLException e) {

                throw new RuntimeException(e);
            }
        }

        //END OF CREATE SUBCOMMAND

        //START OF LEAVE SUBCOMMAND
        if(args[0].equals("leave") && args.length<=1) {

            try {
                if(!gangs.getService().getPlayerStats(player).getGang().equals("none")){
                    String exgang =gangs.getService().getPlayerStats(player).getGang();
                    gangs.getService().setPlayerGang(player,"none");
                    gangs.getService().setPlayerRank(player,"none");
                    gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>you are no longer a member of "+exgang+"</gradient>"));
                }else {
                    gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>you are not part of any gang</gradient>"));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        //END OF LEAVE SUBCOMMAND

        //START OF KICK SUBCOMMAND

        if (args[0].equals("kick") && args.length == 2) {

            List<String> ranks = (List<String>) gangs.getConfig().getList("gangs.ranks-with-kick-perms");
            try {
                if (ranks.contains(gangs.getService().getPlayerStats(player).getRank())) {


                    Player ranker = null;
                    if (Bukkit.getPlayerExact(args[1]) != null) {

                        ranker = Bukkit.getPlayer(args[1]);
                    }

                    if (gangs.getService().getPlayerUUID(args[1]) != null) {

                        if (gangs.getService().getPlayerStats(gangs.getService().getPlayerUUID(args[1])).getGang().equals(gangs.getService().getPlayerStats(player).getGang())) {
                            gangs.getService().setPlayerGang(gangs.getService().getPlayerUUID(args[1]), "none");
                            gangs.getService().setPlayerRank(gangs.getService().getPlayerUUID(args[1]), "none");

                            if (ranker != null) {
                                    gangs.adventure().player(ranker).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>you were kicked from "+ gangs.getService().getPlayerStats(player).getGang()+" by "+ player.getName()+"</gradient>"));
                            }
                            gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>" + args[1] + " is now kicked from " + gangs.getService().getPlayerStats(player).getGang() + "</gradient>"));


                        }else{
                            gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>" + args[1] + " is not part of your gang</gradient>"));

                        }
                    }else{
                        gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>" + args[1] + " is not part of your gang</gradient>"));

                    }
                }else {
                    gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>your rank doesn't allow you to kick poeple</gradient>"));

                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        //END OF KICK SUBCOMMAND

        //START OF DISBAND SUBCOMMAND
        if (args[0].equals("disband") && args.length <= 1) {

            List<String> ranks = (List<String>) gangs.getConfig().getList("gangs.ranks-with-disband-perms");
            try {
                if(!gangs.getService().getPlayerStats(player).getGang().equals("none")) {
                    if (ranks.contains(gangs.getService().getPlayerStats(player).getRank())) {
                        List<String[]> gang_members = gangs.getService().getRawPlayerResults("gang", gangs.getService().getPlayerStats(player).getGang());
                        gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>"+gangs.getService().getPlayerStats(player).getGang()+" has been disbanded</gradient>"));
                        ArrayList<String> allies = gangs.getService().getAllies(gangs.getService().getPlayerStats(player).getGang());
                        allies.remove(gangs.getService().getPlayerStats(player).getGang());
                        allies.remove("none");
                        allies.forEach(ally->{
                            try {
                                if(!ally.equals(gangs.getService().getPlayerStats(player).getGang())) {
                                    gangs.getService().removeAllies(ally,gangs.getService().getPlayerStats(player).getGang());
                                }
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }

                        });
                        gangs.getService().deleteGangs(gangs.getService().getPlayerStats(player).getGang());
                        gangs.getService().setPlayerRank(player,"none");
                        gangs.getService().setPlayerGang(player,"none");

                        gang_members.forEach(strings -> {
                            try {

                                System.out.println(strings[0] + "here");
                                if(!UUID.fromString(strings[0]).equals(gangs.getService().getPlayerUUID(player.getName()))){
                                Player bander = null;
                                if (Bukkit.getPlayerExact(Bukkit.getPlayer(UUID.fromString(strings[0])).getName()) != null) {
                                    bander = Bukkit.getPlayer(UUID.fromString(strings[0]));
                                    gangs.adventure().player(bander).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>"+gangs.getService().getPlayerStats(bander).getGang()+" has been disbanded</gradient>"));
                                }

                                gangs.getService().setPlayerRank(UUID.fromString(strings[0]),"none");
                                gangs.getService().setPlayerGang(UUID.fromString(strings[0]),"none");
                                }
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        });

                    } else {
                        gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>your rank doesn't allow you to disband your gang</gradient>"));

                    }
                }else {
                    gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>you are not part of any gang</gradient>"));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        //END OF DISBAND SUBCOMMAND

        //START OF FRIENDLY FIRE SUBCOMMAND
        if (args[0].equals("friendlyfire") && args.length ==2) {

            List<String> ranks = (List<String>) gangs.getConfig().getList("gangs.ranks-with-friendly-fire-perms");
            try {
                if (ranks.contains(gangs.getService().getPlayerStats(player).getRank())) {

                    gangs.getService().setFriendlyFire(gangs.getService().getPlayerStats(player).getGang(),args[1]);
                    gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>friendly fire is now "+args[1]+"</gradient>"));

                }else {
                    gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>your rank doesn't allow you to set friendly fire</gradient>"));

                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        //END OF FRIENDLY FIRE SUBCOMMAND
        //START OF TOGGLEGANGCHAT SUBCOMMAND
        if (args[0].equals("gang-chat-toggle")) {
            System.out.println("dsds");

            try {
                if(!gangs.getService().getPlayerStats(player).getGang().equals("none")) {
                    if (gangs.getService().getPlayerStats(player).getGangchat().equals("false")) {
                        gangs.getService().setPlayerGangChat(player, "true");
                        System.out.println("was false");
                        gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>gang only chat enabled</gradient>"));
                    } else {
                        if (gangs.getService().getPlayerStats(player).getGangchat().equals("true")) {
                            gangs.getService().setPlayerGangChat(player, "false");
                            System.out.println("was true");
                            gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>gang only chat disabled</gradient>"));
                        }
                    }
                }else {
                    gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>you are not a member of any gang</gradient>"));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        //END OF TOGGLEGANGCHAT SUBCOMMAND
        //START OF ALLY-REQUEST COMMAND
        if (args[0].equals("ally-request") && args.length >= 2) {

            List<String> ranks = (List<String>) gangs.getConfig().getList("gangs.ranks-with-ally-perms");
            try {
                if (ranks.contains(gangs.getService().getPlayerStats(player).getRank())) {

                    long cooldownTime = gangs.getConfig().getLong("gangs.ally-request-cooldown"); // Get number of seconds from wherever you want
                    if (!allyreqCooldowns.containsKey(sender.getName())) {
                        allyreqCooldowns.put(sender.getName(), System.currentTimeMillis());

                    }
                    long secondsLeft = ((allyreqCooldowns.get(sender.getName()) / 1000) + cooldownTime) - (System.currentTimeMillis() / 1000);
                    if (secondsLeft > 0) {
                        // Still cooling down

                        gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>please wait " + secondsLeft + " seconds before using that command</gradient>"));
                    }

                    else {

                        allyreqCooldowns.put(sender.getName(), System.currentTimeMillis());


                    StringBuilder concat_gang = new StringBuilder();

                    for (int i = 1; i <= (args.length - 1); i++) {
                        concat_gang.append(args[i] + " ");
                    }
                    String gang_concacted = concat_gang.toString().trim();
                    if (gangs.getService().getAllies(gangs.getService().getPlayerStats(player).getGang()).contains(gang_concacted)) {
                        gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>you are aleady allies with " + gang_concacted + "</gradient>"));
                    } else {
                        AtomicInteger count = new AtomicInteger();
                        Bukkit.getOnlinePlayers().forEach(p -> {
                            try {

                                if ((gangs.getService().getPlayerStats(p).getGang().equals(gang_concacted)) && ranks.contains(gangs.getService().getPlayerStats(p).getRank())) {
                                    gangs.adventure().player(p).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>your gang is invited to be an ally of " + gangs.getService().getPlayerStats(player).getGang() + "</gradient>"));

                                    int lenganga = gang_concacted.length();
                                    int lengangb = gangs.getService().getPlayerStats(player).getGang().length();
                                    gangs.adventure().player(p).sendMessage((MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c><bold>[ACCEPT]</gradient>")).clickEvent(runCommand("/adventurecommand sendallyinvite $%A" + gang_concacted + "$%A $%B" + gangs.getService().getPlayerStats(player).getGang() + "$%B " + player.getName() + " " + p.getName())).hoverEvent(HoverEvent.showText(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>click me to join " + gangs.getService().getPlayerStats(player).getGang()))));
                                    count.addAndGet(1);
                                }
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        if (count.get() >= 1) {
                            gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>successfully invited " + gang_concacted + "</gradient>"));
                        } else {
                            gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>No current online players to accept invite</gradient>"));
                        }
                    }
                }
            }
                else {

                    gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>you are not allowed to send ally invites on behalf of " + gangs.getService().getPlayerStats(player).getGang() + "</gradient>"));

                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
        //END OF ALLY-REQUEST COMMAND
        //START OF ALLY-NEUTRAL COMMAND
        if (args[0].equals("ally-neutral") && args.length >= 2) {

            List<String> ranks = (List<String>) gangs.getConfig().getList("gangs.ranks-with-ally-perms");
            try {
                if (ranks.contains(gangs.getService().getPlayerStats(player).getRank())) {



                    StringBuilder concat_gang = new StringBuilder();

                    for(int i = 1;i<=(args.length-1);i++){
                        concat_gang.append(args[i]+" ");
                    }
                    String gang_concacted = concat_gang.toString().trim();
                    if(gangs.getService().getAllies(gangs.getService().getPlayerStats(player).getGang()).contains(gang_concacted) && !gangs.getService().getPlayerStats(player).getGang().equals(gang_concacted) ){

                        gangs.getService().removeAllies(gangs.getService().getPlayerStats(player).getGang(),gang_concacted);
                        Bukkit.getOnlinePlayers().forEach(p -> {
                            try {
                                if ((gangs.getService().getPlayerStats(p).getGang().equals(gang_concacted))) {
                                    gangs.adventure().player(p).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>your gang is not allies anymore with "+gangs.getService().getPlayerStats(player).getGang()+"</gradient>"));
                                }
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        Bukkit.getOnlinePlayers().forEach(p -> {
                            try {
                                if (gangs.getService().getPlayerStats(p).getGang().equals(gangs.getService().getPlayerStats(player).getGang())) {
                                    gangs.adventure().player(p).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>your gang is not allies anymore with "+gang_concacted.trim()+"</gradient>"));
                                }
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }else {

                        gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>you are not allies with "+gang_concacted.trim()+"</gradient>"));
                    }
                } else {

                    gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>you are not allowed to set allies as neutral on behalf of " + gangs.getService().getPlayerStats(player).getGang() + "</gradient>"));

                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
        //END OF ALLY-NEUTRAL COMMAND
        //START OF TOGGLEALLYCHAT SUBCOMMAND
        if (args[0].equals("ally-chat-toggle")) {
            System.out.println("dsds");

            try {if(gangs.getService().getAllies(gangs.getService().getPlayerStats(player).getGang()).size()>=2){
                if (gangs.getService().getPlayerStats(player).getAllychat().equals("false")) {
                    gangs.getService().setPlayerAllyChat(player, "true");
                    System.out.println("was false");
                    gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>ally only chat enabled</gradient>"));
                } else {
                    if (gangs.getService().getPlayerStats(player).getAllychat().equals("true")) {
                        gangs.getService().setPlayerAllyChat(player, "false");
                        System.out.println("was true");
                        gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>ally only chat disabled</gradient>"));
                    }
                }
            }else {
                System.out.println("your gang has no allies");
            }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        //END OF TOGGLEALLYCHAT SUBCOMMAND
        //START OF FRIENDLY FIRE ALLIES SUBCOMMAND
        if (args[0].equals("friendlyfire-allies") && args.length >=2) {
            List<String> ranks = (List<String>) gangs.getConfig().getList("gangs.ranks-with-friendly-fire-perms");
            try {

                if (ranks.contains(gangs.getService().getPlayerStats(player).getRank())) {

                    long cooldownTime = gangs.getConfig().getLong("gangs.ally-request-cooldown"); // Get number of seconds from wherever you want
                    if (!allyreqCooldowns.containsKey(sender.getName())) {
                        allyreqCooldowns.put(sender.getName(), System.currentTimeMillis());

                    }
                    long secondsLeft = ((allyreqCooldowns.get(sender.getName()) / 1000) + cooldownTime) - (System.currentTimeMillis() / 1000);
                    if (secondsLeft > 0) {
                        // Still cooling down

                        gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>please wait " + secondsLeft + " seconds before using that command</gradient>"));
                    }

                    else {

                        System.out.println("it got here");

                        allyreqCooldowns.put(sender.getName(), System.currentTimeMillis());

                    StringBuilder concat_gang = new StringBuilder();

                    for (int i = 1; i <= args.length - 2; i++) {
                        concat_gang.append(args[i] + " ");
                    }

                    String gang_concacted = concat_gang.toString().trim();

                    if(!gang_concacted.equals(gangs.getService().getPlayerStats(player).getGang())){
                    System.out.println(gang_concacted + " here");
                    AtomicInteger count = new AtomicInteger();
                    Bukkit.getOnlinePlayers().forEach(p -> {
                        try {

                            if ((gangs.getService().getPlayerStats(p).getGang().equals(gang_concacted)) && ranks.contains(gangs.getService().getPlayerStats(p).getRank())) {
                                System.out.println("hhh");
                                System.out.println(args[args.length - 1]);
                                if (args[args.length - 1].equals("true")) {
                                    System.out.println("wjhwhe");
                                    gangs.adventure().player(p.getPlayer()).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>your gang is requested to turn on friendly fire with " + gangs.getService().getPlayerStats(player).getGang() + "</gradient>"));
                                    int lenganga = gang_concacted.length();
                                    int lengangb = gangs.getService().getPlayerStats(player).getGang().length();
                                    gangs.adventure().player(p).sendMessage((MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c><bold>[ACCEPT]</gradient>")).clickEvent(runCommand("/adventurecommand sendallyfriendlyinvite $%A" + gang_concacted + "$%A $%B" + gangs.getService().getPlayerStats(player).getGang() + "$%B " + player.getName() + " " + p.getName() + " true")).hoverEvent(HoverEvent.showText(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>click me to accept"))));
                                    count.addAndGet(1);
                                } else {
                                    if (args[args.length - 1].equals("false")) {
                                        gangs.adventure().player(p.getPlayer()).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>your gang is requested to turn off friendly fire with " + gangs.getService().getPlayerStats(player).getGang() + "</gradient>"));
                                        int lenganga = gang_concacted.length();
                                        int lengangb = gangs.getService().getPlayerStats(player).getGang().length();
                                        gangs.adventure().player(p).sendMessage((MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c><bold>[ACCEPT]</gradient>")).clickEvent(runCommand("/adventurecommand sendallyfriendlyinvite $%A" + gang_concacted + "$%A $%B" + gangs.getService().getPlayerStats(player).getGang() + "$%B " + player.getName() + " " + p.getName() + " false")).hoverEvent(HoverEvent.showText(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>click me to accept"))));
                                        count.addAndGet(1);
                                    }
                                }
                            }
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    if (count.get() >= 1) {
                        gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>successfully invited " + gang_concacted + "</gradient>"));
                    } else {
                        gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>No current online players to accept invite</gradient>"));
                    }
                }else {
                        gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>you are not allies with yourself</gradient>"));
                    }
                    }
            }
                else {
                    gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>your rank doesn't allow you to set friendly fire</gradient>"));

                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        //END OF FRIENDLY FIRE ALLIES SUBCOMMAND

        //START OF DEPOSIT SUBCOMMAND
        if (args[0].equals("deposit") && args.length == 2) {

            List<String> ranks = (List<String>) gangs.getConfig().getList("gangs.ranks-with-vault-perms");
            try {
                if (ranks.contains(gangs.getService().getPlayerStats(player).getRank())) {

                int amount = Math.abs(Math.round(Integer.valueOf(args[1])));
                if(amount<=gangs.getEconomy().getBalance(player)){
                    gangs.getEconomy().withdrawPlayer(player.getName(),amount);
                    int newBalance = Integer.parseInt(gangs.getService().getServerStats(gangs.getService().getPlayerStats(player).getGang()).getBalance()) +amount;
                    gangs.getService().setBalance(gangs.getService().getPlayerStats(player).getGang(),Integer.toString(newBalance));
                    gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>deposited "+ amount+"$</gradient>"));
                    gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>new balance "+ (gangs.getService().getServerStats(gangs.getService().getPlayerStats(player).getGang()).getBalance()) +"$</gradient>"));
                }else {
                    gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>you cannot deposit more than your personal balance</gradient>"));
                }
                }else {
                    gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>your rank doesn't allow you to deposit</gradient>"));

                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        //END OF DEPOSIT SUBCOMMAND

        //START OF WITHDRAW SUBCOMMAND
        if (args[0].equals("withdraw") && args.length == 2) {

            List<String> ranks = (List<String>) gangs.getConfig().getList("gangs.ranks-with-vault-perms");
            try {
                if (ranks.contains(gangs.getService().getPlayerStats(player).getRank())) {

                    int amount = Math.abs(Math.round(Integer.valueOf(args[1])));
                    if(amount<=Integer.valueOf(gangs.getService().getServerStats(gangs.getService().getPlayerStats(player).getGang()).getBalance())){
                        gangs.getEconomy().depositPlayer(player.getName(),amount);
                        int newBalance = Integer.parseInt(gangs.getService().getServerStats(gangs.getService().getPlayerStats(player).getGang()).getBalance()) - amount;
                        gangs.getService().setBalance(gangs.getService().getPlayerStats(player).getGang(),Integer.toString(newBalance));
                        gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>withdrawn "+ amount+"$</gradient>"));
                        gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>new balance "+ (gangs.getService().getServerStats(gangs.getService().getPlayerStats(player).getGang()).getBalance()) +"$</gradient>"));
                    }else {
                        gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>you cannot withdraw more than your gang balance</gradient>"));
                    }
                }else {
                    gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>your rank doesn't allow you to withdraw</gradient>"));

                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        if (args[0].equals("balance") && args.length == 1) {

            List<String> ranks = (List<String>) gangs.getConfig().getList("gangs.ranks-with-vault-perms");
            try {
                if (ranks.contains(gangs.getService().getPlayerStats(player).getRank())) {


                    int amount = Integer.parseInt(gangs.getService().getServerStats(gangs.getService().getPlayerStats(player).getGang()).getBalance());
                    gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>Balance: "+amount+"</gradient>"));
                }else {
                    gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>your rank doesn't allow you to see balance</gradient>"));

                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        //END OF WITHDRAW SUBCOMMAND

        //START OF SET-BASE
        if (args[0].equals("set-base") && args.length == 2) {

            List<String> ranks = (List<String>) gangs.getConfig().getList("gangs.ranks-with-base-perms");
            try {
                if (ranks.contains(gangs.getService().getPlayerStats(player).getRank())) {
                    if(!gangs.getService().getBases(gangs.getService().getPlayerStats(player).getGang()).contains(args[1])){
                        gangs.getService().setBases(gangs.getService().getPlayerStats(player).getGang(),args[1],player.getWorld().getName(),String.valueOf(player.getLocation().getX()),String.valueOf(player.getLocation().getY()),String.valueOf(player.getLocation().getZ()));
                        gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>successfully set base</gradient>"));
                    }else {
                        gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>There is already a base with that name</gradient>"));
                    }
                }else {
                    gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>your rank doesn't allow you to set base</gradient>"));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        //END OF SET-BASE

        //START OF REMOVE BASE
        if (args[0].equals("remove-base") && args.length == 2) {

            List<String> ranks = (List<String>) gangs.getConfig().getList("gangs.ranks-with-base-perms");
            try {
                if (ranks.contains(gangs.getService().getPlayerStats(player).getRank())) {
                    if(gangs.getService().getBases(gangs.getService().getPlayerStats(player).getGang()).contains(args[1])){
                        gangs.getService().removeBases(gangs.getService().getPlayerStats(player).getGang(),args[1]);
                        gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>successfully removed base</gradient>"));
                    }else {
                        gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>There is no base with that name</gradient>"));
                    }
                }else {
                    gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>your rank doesn't allow you to remove base</gradient>"));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        //END OF REMOVE BASE

        //START OF TP BASE
        if (args[0].equals("tp-base") && args.length == 2) {

            try {

                    if(gangs.getService().getBases(gangs.getService().getPlayerStats(player).getGang()).contains(args[1])) {

                        long cooldownTime = gangs.getConfig().getLong("gangs.base-tp-cooldown"); // Get number of seconds from wherever you want
                        if (!tpCooldowns.containsKey(sender.getName())) {
                                tpCooldowns.put(sender.getName(), System.currentTimeMillis());

                        }
                            long secondsLeft = ((tpCooldowns.get(sender.getName()) / 1000) + cooldownTime) - (System.currentTimeMillis() / 1000);
                            if (secondsLeft > 0) {
                                // Still cooling down

                                gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>please wait " + secondsLeft + " seconds before using that command</gradient>"));
                            } else {

                                // No cooldown found or cooldown has expired, save new cooldown
                                tpCooldowns.put(sender.getName(), System.currentTimeMillis());
                                String coords = gangs.getService().getBaseCoords(gangs.getService().getPlayerStats(player).getGang(), args[1]);
                                gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>telporting in 5 seconds, don't move</gradient>"));
                                String worldName = StringUtils.substringBetween(coords, "$W", "$W");
                                Double locX = Double.valueOf(StringUtils.substringBetween(coords, "$X", "$X"));
                                Double locY = Double.valueOf(StringUtils.substringBetween(coords, "$Y", "$Y"));
                                Double locZ = Double.valueOf(StringUtils.substringBetween(coords, "$Z", "$Z"));
                                World locWorld = Bukkit.getWorld(worldName);

                                PlayerMoveEventListener.appendTeleport(player);
                                gangs.getServer().getScheduler().scheduleSyncDelayedTask(gangs, new Runnable() {
                                    public void run() {
                                        if (PlayerMoveEventListener.teleport.contains(player)) {
                                            System.out.println("WAited");
                                            Location loc = new Location(locWorld, locX, locY, locZ);
                                            player.teleport(loc);
                                            PlayerMoveEventListener.popTeleport(player);
                                        }

                                    }
                                }, 20 * 5); // 20 (one second in ticks) * 5 (seconds to wait)
                            }


                    }else {
                        gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>There is no base with that name</gradient>"));
                    }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        //END OF TP BASE

        //START OF SUMMON ALL
        if(args[0].equals("summon-all") && args.length == 1){
            List<String> ranks = (List<String>) gangs.getConfig().getList("gangs.ranks-with-summon-perms");
            try {
                if (ranks.contains(gangs.getService().getPlayerStats(player).getRank())) {

                    Bukkit.getOnlinePlayers().forEach( p ->{

                        if(p.getName()!=player.getName()) {

                            gangs.adventure().player(p).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>the gang members have been summoned! </gradient>"));

                            gangs.adventure().player(p).sendMessage((MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c><bold>[ACCEPT]</gradient>")).clickEvent(runCommand("/adventurecommand summon " + p.getName() + " " + player.getName())).hoverEvent(HoverEvent.showText(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>[click me to tp]"))));

                            gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>successfully summoned gang members</gradient>"));
                        }
                    });

                }else {
                    gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>your rank doesn't allow you to summon</gradient>"));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        //END OF SUMMON ALL

        //START OF CHALLENGE
        if(args[0].equals("challenge") && args.length>=2){


            List<String> ranks = (List<String>) gangs.getConfig().getList("gangs.ranks-with-challenge-perms");
            try {
                if (ranks.contains(gangs.getService().getPlayerStats(player).getRank())) {
                    long cooldownTime = gangs.getConfig().getLong("gangs.challenge-cooldown"); // Get number of seconds from wherever you want
                    if (!challengeCooldowns.containsKey(sender.getName())) {
                        challengeCooldowns.put(sender.getName(), System.currentTimeMillis());

                    }
                    long secondsLeft = ((challengeCooldowns.get(sender.getName()) / 1000) + cooldownTime) - (System.currentTimeMillis() / 1000);
                    if (secondsLeft > 0) {
                        // Still cooling down

                        gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>please wait " + secondsLeft + " seconds before using that command</gradient>"));
                    }

                    else {

                        challengeCooldowns.put(sender.getName(), System.currentTimeMillis());

                        StringBuilder concat_gang = new StringBuilder();

                    for (int i = 1; i <= args.length - 1; i++) {
                        concat_gang.append(args[i] + " ");
                    }

                    String gang = concat_gang.toString().trim();

                    System.out.println(gang);

                    if (gangs.getService().getGangsList().contains(gang)) {

                        if(!gang.equals(gangs.getService().getPlayerStats(player).getGang())){

                        AtomicInteger flag = new AtomicInteger();
                        Bukkit.getOnlinePlayers().forEach(p -> {

                            try {
                                if ( gangs.getService().getPlayerStats(p).getGang().equals(gang) && ranks.contains( gangs.getService().getPlayerStats(p).getRank())) {


                                    gangs.adventure().player(p).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>Your gang is challenged by "+ gangs.getService().getPlayerStats(player).getGang()+" accept to summon your members to warzone</gradient>"));

                                    gangs.adventure().player(p).sendMessage((MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c><bold>[ACCEPT]</gradient>")).clickEvent(runCommand("/adventurecommand challenge $%A" + gang + "$%A $%B" + gangs.getService().getPlayerStats(player).getGang() +"$%B $%C"+ player.getName()+"$%C" )).hoverEvent(HoverEvent.showText(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>[click me to tp]"))));

                                    gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>challenge sent successfully, both gang members will receive tp invite when challenge accepted</gradient>"));

                                    flag.set(1);
                                }


                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        });

                        if(flag.get() == 0){
                            gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>no high ranked gang member to accept challenge</gradient>"));
                        }


                    }else {
                            gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>you cannot challenge your own gang</gradient>"));
                        }
                    }else {

                        System.out.println(gangs.getService().getGangsList());
                        gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>such gang does not exist</gradient>"));
                    }
                }
                }
                else {
                    gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>your rank doesn't allow you to summon</gradient>"));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        //END OF CHALLENGE

        if(args[0].equals("baltop")){
            try {

                ArrayList<String> gangsArray = new ArrayList<>();

                Gangs gangs = Gangs.getPlugin();
                QueryBuilder<ServerStats, String> qb = gangs.getService().getServerStatsDao().queryBuilder();
                // select 2 aggregate functions as the return
                qb.orderBy("balance",false);
                // the results will contain 2 string values for the min and max
                GenericRawResults<String[]> rawResults = gangs.getService().getServerStatsDao().queryRaw(qb.prepareStatementString());
                // page through the results
                List<String[]> results = rawResults.getResults();

                int d = results.size();
                if(results.size()>10){
                    d = 10;
                }


                gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>Top 10 Gangs [Balance]</gradient>"));
                for (int i = 0;i<d;i++){
                    gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>"+(i+1)+") "+results.get(i)[0]+": "+results.get(i)[results.get(i).length-1]+"</gradient>"));

                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }


        if(args[0].equals("memtop")){
            try {

                ArrayList<String> gangsArray = new ArrayList<>();

                Gangs gangs = Gangs.getPlugin();
                QueryBuilder<PlayerStats, String> qb = gangs.getService().getPlayerStatsDao().queryBuilder();
                // select 2 aggregate functions as the return
                qb.groupBy("uuid");
                // the results will contain 2 string values for the min and max
                GenericRawResults<String[]> rawResults = gangs.getService().getServerStatsDao().queryRaw(qb.prepareStatementString());
                // page through the results
                List<String[]> results = rawResults.getResults();
                System.out.print("[");
                results.forEach(r->{
                    System.out.print("[");
                    for (int i=0;i<r.length-1;i++){
                        System.out.print(r[i]+",");
                    }
                    System.out.print(r[r.length-1]+"],");

                });
                System.out.println("]");

                ArrayList<ArrayList> gm = new ArrayList<ArrayList>();

                gangs.getService().getGangsList().forEach(g->{

                    AtomicInteger count = new AtomicInteger();

                    results.forEach(r->{

                        if(r[2].equals(g)){
                            count.getAndIncrement();
                        }

                    });

                    System.out.println(g+": "+count.get()+" members");
                    ArrayList l = new ArrayList<>();
                    l.add(g);
                    l.add(count.get());
                    gm.add(l);
                });

                //sort list and print
                gm.sort(Comparator.comparing(x -> x.get(1), Collections.reverseOrder()));

                int d = gm.size();
                if(gm.size()>10){
                    d = 10;
                }


                gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>Top 10 Gangs [Member Count]</gradient>"));
                for (int i = 0;i<d;i++){
                    gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>"+(i+1)+") "+ gm.get(i).get(0) +": "+ gm.get(i).get(gm.get(i).size()-1) +"</gradient>"));
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return true;
    }
}
