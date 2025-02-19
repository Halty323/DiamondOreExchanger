package com.halty.diamondOreExchanger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class TelegramCodeCommand implements CommandExecutor {

    private final DiamondOreExchanger plugin;

    public TelegramCodeCommand(DiamondOreExchanger plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage("Эта команда может быть использована только игроками");
            return true;
        }

        player.sendMessage("Ваш код: " + plugin.getDatabase().getTelegramCode(player.getUniqueId()));

        return true;
    }
}
