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

package net.picklecraft.picklexpbank.Accounts;

import com.sun.org.apache.xml.internal.utils.Trie;
import java.util.UUID;
import net.picklecraft.picklexpbank.PickleXPBank;
import org.bukkit.entity.Player;

/**
 *
 * @author Pickle <curtisdhi@gmail.com>
 */
public class Account {
    
    private final UUID uuid;
    private String playerName;
    private long balance;
    
    public Account(UUID uuid, long balance) {
        this.uuid = uuid;
        this.balance = balance;
    }
    public Account(UUID uuid) {
        this(uuid, 0);
    }
    
    public void setBalance(long balance) {
        this.balance = balance;
        PickleXPBank.getInstance().getConsumer().queueAccount(this);
    }
    
    public long getBalance() {
        return balance;
    }
    
    public void addBalance(long amount) {
        setBalance(balance + amount);
    }
    
    public boolean subBalance(long amount) {
        long newBalance = balance - amount;
        if (newBalance >= 0) {
            setBalance(newBalance);
            return true;
        }
        return false;
    }
    
    /*
    * @return boolean
    */
    public boolean subExperience(long amount) {
        final Player player = getPlayer();
        if (player != null) {
            int newXp = player.getTotalExperience() - (int)amount;
            if (newXp >= 0) {
                player.setTotalExperience(newXp);
                return true;
            }
        }
        
        return false;
    }
    
    /*
    * @return boolean
    */
    public boolean addExperience(long amount) {
        final Player player = getPlayer();
        if (player != null) {
            player.setTotalExperience(player.getTotalExperience() + (int)amount);
            return true;
        }
        return false;
    }

    public UUID getUuid() {
        return uuid;
    }
    
    public Player getPlayer() {
        Player player = PickleXPBank.getInstance().getServer().getPlayer(uuid);

        return player;
    }
    
    public void setPlayerName(String name) {
        playerName = name;
    }
    
    public String getPlayerName() {
        return playerName;
    }
    
    public boolean canPlaceXPSign() {
        final Player player = getPlayer();
        if (player.hasPermission("PickleXPBank.placeSign")) {
            
            final AccountManager acManager = PickleXPBank.getInstance().getAccountManager();
            final int signLimit = acManager.getPlugin().getConfig().getInt("settings.signLimit");
            
            if (signLimit == 0 || acManager.countXPSigns(this) < signLimit ||
                    player.hasPermission("PickleXPBank.placeOtherPlayerSign")) {
                
                return true;
                
            }
            else {
                player.sendMessage("I'm sorry, but you reached your limit.");
            }
            
        }
        else {
            player.sendMessage("I'm sorry, but you lack permission.");        
        }
        
        return false;
    }
 
}
