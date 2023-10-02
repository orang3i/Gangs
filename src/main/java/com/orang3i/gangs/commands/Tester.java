package com.orang3i.gangs.commands;

import com.orang3i.gangs.Gangs;
import com.orang3i.gangs.database.GangsService;
import com.orang3i.gangs.database.entities.PlayerStats;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class Tester implements CommandExecutor {

    private final Gangs gangs;

    public Tester(Gangs gangs){
        this.gangs = gangs;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        //Component comp1 = MiniMessage.miniMessage().deserialize( "<gradient:#9281fb:#eb93fc>Welcome Tester!</gradient>");
        gangs.adventure().player((Player) sender).sendMessage(MiniMessage.miniMessage().deserialize( "<gradient:#8e28ed:#f52c2c>Welcome Tester! get testing ;)</gradient>"));



        return true;
    }
}
