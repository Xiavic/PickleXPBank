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
import java.util.logging.Level;
import net.picklecraft.Util.MySQLConnectionPool;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Pickle <curtisdhi@gmail.com>
 */
public class PickleXPBank extends JavaPlugin {
    
    private MySQLConnectionPool sqlPool;
    private boolean connected;
    
    @Override 
    public void onLoad() {
        
        final String host = getConfig().getString("settings.mysql.host");
        final String database = getConfig().getString("settings.mysql.database");
        final int port = getConfig().getInt("settings.mysql.port");
        final String user = getConfig().getString("settings.mysql.username");
        final String password = getConfig().getString("settings.mysql.password");
        final String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useUnicode=true&characterEncoding=utf-8";
        
        try {
            getLogger().info("Connecting to " + user + "@" + url + "...");
            sqlPool = new MySQLConnectionPool(url, user, password);
            final Connection conn = getConnection();
            if (conn == null) {
                return;
            }
            conn.close();
        } 
        catch (final NullPointerException ex) {
            getLogger().log(Level.SEVERE, "Error while loading: ", ex);
        } 
        catch (final Exception ex) {
            getLogger().severe("Error while loading: " + ex.getMessage());
        }
  
    }
    
    @Override 
    public void onEnable() {
        
    }
    
    @Override 
    public void onDisable() {
        
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
    
}
