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

package net.picklecraft.picklexpbank.Factories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import net.picklecraft.picklexpbank.Accounts.Account;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 * @author Pickle <curtisdhi@gmail.com>
 */
public class AccountFactory {
    
    public static Account createAccount(Player player) {
        if (player == null) {
            return null;
        }
        return new Account(player);
    }
    
    public static Account createAccount(UUID uuid) {
        Player player = Bukkit.getServer().getPlayer(uuid);
        return createAccount(player);
    }
    
    public static Account createAccount(ResultSet rs) throws SQLException {
        UUID uuid = UUID.fromString(rs.getString("id"));  
        Account account = createAccount(uuid);
        
        if (account != null) {
            account.setBalance(rs.getLong("balance"));
        }
        
        return account;
    }
} 
