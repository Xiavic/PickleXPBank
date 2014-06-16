/*
 * Copyright (C) 2014 Pickle <curtisdhi@gmail.com>.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */

package net.picklecraft.picklexpbank;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import net.picklecraft.Util.MySQLConnectionPool;
import net.picklecraft.picklexpbank.Accounts.Account;
import net.picklecraft.picklexpbank.Accounts.AccountManager;
import net.picklecraft.picklexpbank.Listeners.XPSignListener;
import net.picklecraft.picklexpbank.commands.AdminBankExecutor;
import net.picklecraft.picklexpbank.commands.AdminSettingsExecutor;
import net.picklecraft.picklexpbank.commands.PlayerBankExecutor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Pickle <curtisdhi@gmail.com>
 */
public class PickleXPBank extends JavaPlugin {
    
    private MySQLConnectionPool sqlPool;
    private boolean connected;
    
    private static PickleXPBank instance;
    
    private Updater updater;
    
    private Consumer consumer;
    private AccountManager accountManager;
    
    
    public static PickleXPBank getInstance() {
        if (instance == null) {
            instance = (PickleXPBank)Bukkit.getPluginManager().getPlugin("PickleXPBank");
        }
        return instance;
    }
    
    @Override
    public void onLoad() {
        getConfig().options().copyDefaults(true);
        saveConfig();
        
        instance = this;
        
        final String host = getConfig().getString("settings.mysql.host");
        final String database = getConfig().getString("settings.mysql.database");
        final int port = getConfig().getInt("settings.mysql.port");
        final String user = getConfig().getString("settings.mysql.username");
        final String password = getConfig().getString("settings.mysql.password");
        final String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useUnicode=true&characterEncoding=utf-8";
        
        try {
            getLogger().log(Level.INFO, "Connecting to {0}@{1}...", new Object[]{user, url});
            sqlPool = new MySQLConnectionPool(url, user, password);
            final Connection conn = getConnection();
            if (conn == null) {
                return;
            }
            connected = true;
            conn.close();
        } 
        catch (final Exception ex) {
            getLogger().log(Level.SEVERE, "Error while loading: {0}", ex.getMessage());
        }
        
        updater = new Updater(this);
        try {
            updater.checkTables();
        }
        catch (final SQLException ex) {
            getLogger().log(Level.SEVERE, "[SQLException] Unable to create tables.. {0}", ex.getMessage());
        }
        
        consumer = new Consumer(this);
        accountManager = new AccountManager(this); 

    }
    
    @Override 
    public void onEnable() {
        if (!connected) {
            Bukkit.getPluginManager().disablePlugin(this);
            getLogger().log(Level.SEVERE, "Not connected to a database. Disabling plugin.");
            return;
        }
        
       updater.loadFromSql(accountManager);

        getServer().getScheduler().runTaskTimerAsynchronously(this, consumer, 0, 60*20);
        getServer().getScheduler().runTaskTimer(this, accountManager, 0, 20);
        
        XPSignListener listener = new XPSignListener(this);
        Bukkit.getPluginManager().registerEvents(listener, this);
        
        AdminSettingsExecutor adminSettingsExecutor = new AdminSettingsExecutor();
        getCommand("xpsettings").setExecutor(adminSettingsExecutor);
        
        AdminBankExecutor adminBankExecutor = new AdminBankExecutor();
        getCommand("xpadd").setExecutor(adminBankExecutor);
        getCommand("xpsub").setExecutor(adminBankExecutor);
        getCommand("xpset").setExecutor(adminBankExecutor);
        
        PlayerBankExecutor playerBankExecutor = new PlayerBankExecutor();
        getCommand("xppay").setExecutor(playerBankExecutor);
    }
    
    @Override 
    public void onDisable() {
        
    }
    
    /*
    * TODO: make this easier to read!
    */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }
    
    
    public Connection getConnection() {
        try {
            final Connection conn = sqlPool.getConnection();
            if (!connected) {
                getLogger().info("MySQL connection rebuild");
                connected = true;
            }
            return conn;
        } 
        catch (final Exception ex) {
            if (connected) {
                getLogger().log(Level.SEVERE, "Error while fetching connection: ", ex);
                connected = false;
            } 
            else {
                getLogger().severe("MySQL connection lost");
            }
            return null;
        }
    }
    
    public Consumer getConsumer() {
        return consumer;
    }
    
    public AccountManager getAccountManager() {
        return accountManager;
    }
    
    
    /*
     * Will return a player name if a match was found,
     * else it shall return null
     *
     * @return Player
     */
    public static Player getPlayer(String name) {
        Player playerMatch = null;
        name = name.toLowerCase();
        int playerMatches = 0;
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            String name2 = p.getName().toLowerCase();
            if (name2.contains(name)) {
                playerMatch = p;
                if (name2.equals(name)) {
                    break;
                } 
                //If a second match is found, set playerMatch to null and break out
                else if (++playerMatches > 1) {
                    playerMatch = null;
                    break;
                }
            }
        }
        return playerMatch;
    }
   
}
