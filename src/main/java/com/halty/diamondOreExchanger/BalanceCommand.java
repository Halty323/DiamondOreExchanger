package com.halty.diamondOreExchanger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BalanceCommand implements CommandExecutor {

    DiamondOreExchanger plugin;

    public BalanceCommand(DiamondOreExchanger plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage("Эта команда может быть использована только игроками.");
            return true;
        }

        plugin.getDatabase().updatePlayerBalance(player.getUniqueId(), 0.0);
        player.sendMessage("Ваш баланс: " + plugin.getDatabase().getBalance(player.getUniqueId()));
        return true;
    }
}
