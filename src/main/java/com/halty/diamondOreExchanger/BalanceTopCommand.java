package com.halty.diamondOreExchanger;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class BalanceTopCommand implements CommandExecutor {
    DiamondOreExchanger plugin;
    public BalanceTopCommand(DiamondOreExchanger plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        // Show top 10 richest players
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage("Эта команда может быть использована только игроками");
            return true;
        }

        HashMap<UUID, Double> balances = plugin.getDatabase().getTopTenBalances();

        for (UUID uuid : balances.keySet()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p == null) {
                continue;
            }
            commandSender.sendMessage(p.getName() + ": " + balances.get(uuid) + " монет");
        }

        return true;
    }
}
