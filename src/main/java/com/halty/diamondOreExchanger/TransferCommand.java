package com.halty.diamondOreExchanger;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TransferCommand implements CommandExecutor {

    DiamondOreExchanger plugin;

    public TransferCommand(DiamondOreExchanger plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage("Эта команда может быть использована только игроками.");
            return true;
        }

        String receiver = strings[0];
        double amount = Double.parseDouble(strings[1]);

        if (player.getName().equalsIgnoreCase(receiver)) {
            player.sendMessage("Нельзя переводить деньги самому себе");
            return true;
        }

        if (plugin.getDatabase().getBalance(Bukkit.getPlayer(receiver).getUniqueId()) < amount || amount <= 0) {
            player.sendMessage("Nuh uh");
            return true;
        }

        try {
            plugin.getDatabase().updatePlayerBalance(Bukkit.getPlayer(receiver).getUniqueId(), amount);
            plugin.getDatabase().updatePlayerBalance(player.getUniqueId(), -amount);
            player.sendMessage("Ваш баланс: " + plugin.getDatabase().getBalance(player.getUniqueId()));
            player.sendMessage("Вы отправили: " + amount + " монет " + receiver);
        } catch (NullPointerException e) {
            player.sendMessage("Такого игрока не существует либо он не в сети");
        }

        return true;
    }
}
