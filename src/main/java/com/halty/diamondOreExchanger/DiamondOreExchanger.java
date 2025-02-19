package com.halty.diamondOreExchanger;

import org.bukkit.plugin.java.JavaPlugin;

public final class DiamondOreExchanger extends JavaPlugin {
    private Database database;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getCommand("exchange").setExecutor(new ExchangeCommand(this));
        getCommand("balance").setExecutor(new BalanceCommand(this));
        getCommand("transfer").setExecutor(new TransferCommand(this));
        getCommand("balancetop").setExecutor(new BalanceTopCommand(this));
        getCommand("telegramcode").setExecutor(new TelegramCodeCommand(this));

        database = new Database(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        database = null;
    }

    public Database getDatabase() {
        return database;
    }
}
