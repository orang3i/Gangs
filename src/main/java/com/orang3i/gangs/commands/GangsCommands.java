package com.orang3i.gangs.commands;

import com.j256.ormlite.dao.Dao;
import com.orang3i.gangs.Gangs;
import com.orang3i.gangs.database.entities.PlayerStats;
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
import java.util.concurrent.atomic.AtomicInteger;

import static net.kyori.adventure.text.event.ClickEvent.runCommand;

public class GangsCommands implements CommandExecutor {

    private final Gangs gangs;
    public GangsCommands(Gangs gangs){
        this.gangs = gangs;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        Dao<PlayerStats, String> dao = gangs.getService().getPlayerStatsDao();

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
                        gangs.getService().deleteGangs(gangs.getService().getPlayerStats(player).getGang());
                        gangs.getService().setPlayerRank(player,"none");
                        gangs.getService().setPlayerGang(player,"none");

                        gang_members.forEach(strings -> {
                            try {

                                System.out.println(strings[0] + "here");
                                if(!UUID.fromString(strings[0]).equals(gangs.getService().getPlayerUUID(player.getDisplayName()))){
                                Player bander = null;
                                if (Bukkit.getPlayerExact(Bukkit.getPlayer(UUID.fromString(strings[0])).getDisplayName()) != null) {
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
                if(gangs.getService().getPlayerStats(player).getGangchat().equals("false")){
                    gangs.getService().setPlayerGangChat(player,"true");
                    System.out.println("was false");
                    gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>gang only chat enabled</gradient>"));
                }else {
                if(gangs.getService().getPlayerStats(player).getGangchat().equals("true")){
                    gangs.getService().setPlayerGangChat(player,"false");
                    System.out.println("was true");
                    gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>gang only chat disabled</gradient>"));
                }
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



                    StringBuilder concat_gang = new StringBuilder();

                    for(int i = 1;i<=(args.length-1);i++){
                        concat_gang.append(args[i]+" ");
                    }
                    String gang_concacted = concat_gang.toString().trim();
                    if(gangs.getService().getAllies(gangs.getService().getPlayerStats(player).getGang()).contains(gang_concacted)){
                        gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>you are aleady allies with " +gang_concacted+ "</gradient>"));
                    }else {
                        AtomicInteger count = new AtomicInteger();
                        Bukkit.getOnlinePlayers().forEach(p -> {
                            try {

                                if ((gangs.getService().getPlayerStats(p).getGang().equals(gang_concacted)) && ranks.contains(gangs.getService().getPlayerStats(p).getRank())) {
                                    gangs.adventure().player(p).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>your gang is invited to be an ally of " + gangs.getService().getPlayerStats(player).getGang() + "</gradient>"));

                                    int lenganga = gang_concacted.length();
                                    int lengangb = gangs.getService().getPlayerStats(player).getGang().length();
                                    gangs.adventure().player(p).sendMessage((MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c><bold>[ACCEPT]</gradient>")).clickEvent(runCommand("/adventurecommand sendallyinvite " + lenganga + " " + lengangb + " " + gang_concacted + " " + gangs.getService().getPlayerStats(player).getGang() + " " + player.getDisplayName() + " " + p.getDisplayName())).hoverEvent(HoverEvent.showText(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>click me to join " + gangs.getService().getPlayerStats(player).getGang()))));
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
                } else {

                    gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>you are not allowed to send ally invites on behalf of " + gangs.getService().getPlayerStats(player).getGang() + "</gradient>"));

                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
        //END OF ALLY-REQUEST COMMAND
//:)))
        return true;
    }
}
