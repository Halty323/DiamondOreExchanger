package com.halty.diamondOreExchanger;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.function.Predicate;

public class ExchangeCommand implements CommandExecutor {
    private final DiamondOreExchanger plugin;

    public ExchangeCommand(DiamondOreExchanger plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Эта команда может быть использована только игроками.");
            return true;
        }

        Player player = (Player) commandSender;
        exchangeDiamonds(player);
        return true;
    }

    private void exchangeDiamonds(Player player) {
        Inventory inventory = player.getInventory();
        int diamondOreCount = 0;
        double exchangeRate = 10.0; // Например, 10 монет за 1 алмазную руду
        ItemStack[] contents = inventory.getContents();

        // Функция для проверки, является ли предмет алмазной рудой
        Predicate<ItemStack> isDiamondOre = item -> item != null &&
                (item.getType() == Material.DIAMOND_ORE || item.getType() == Material.DEEPSLATE_DIAMOND_ORE);

        // Проверяем предметы в основном инвентаре
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (isDiamondOre.test(item)) {
                diamondOreCount += item.getAmount();
                inventory.setItem(i, null);
            }
        }

        // Проверяем предмет в основной руке
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        if (isDiamondOre.test(mainHandItem)) {
            diamondOreCount += mainHandItem.getAmount();
            player.getInventory().setItemInMainHand(null);
        }

        // Проверяем предмет в дополнительной руке
        ItemStack offHandItem = player.getInventory().getItemInOffHand();
        if (isDiamondOre.test(offHandItem)) {
            diamondOreCount += offHandItem.getAmount();
            player.getInventory().setItemInOffHand(null);
        }

        if (diamondOreCount == 0) {
            player.sendMessage("У вас нет алмазной руды для обмена.");
            return;
        }

        // Расчет суммы к начислению
        double amountToAdd = diamondOreCount * exchangeRate;

        // Обновление баланса игрока
        boolean success = plugin.getDatabase().updatePlayerBalance(player.getUniqueId(), amountToAdd);

        if (success) {
            player.sendMessage("Вы обменяли " + diamondOreCount + " алмазной руды на " + amountToAdd + " монет.");
        } else {
            player.sendMessage("Произошла ошибка при обмене. Пожалуйста, попробуйте позже.");
            // Возвращаем руду игроку, так как обмен не удался
            returnOreToPlayer(player, diamondOreCount);
        }

        // Обновляем инвентарь игрока
        player.updateInventory();
    }

    // Метод для возврата руды игроку в случае ошибки
    private void returnOreToPlayer(Player player, int count) {
        ItemStack diamondOre = new ItemStack(Material.DIAMOND_ORE, count);
        HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(diamondOre);
        if (!leftover.isEmpty()) {
            // Если инвентарь полон, бросаем оставшиеся предметы на землю
            for (ItemStack item : leftover.values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), item);
            }
        }
    }
}
