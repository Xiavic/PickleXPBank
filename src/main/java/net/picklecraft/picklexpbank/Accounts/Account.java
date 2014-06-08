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

import org.bukkit.entity.Player;

/**
 *
 * @author Pickle <curtisdhi@gmail.com>
 */
public class Account {
    
    private final Player player;
    private long balance;
    
    public Account(Player player, long balance) {
        this.player = player;
        this.balance = balance;
    }
    public Account(Player player) {
        this(player, 0);
    }
    
    public void setBalance(long balance) {
        this.balance = balance;
    }
    
    public long getBalance() {
        return balance;
    }
    
    public void addBalance(long amount) {
        balance += amount;
        player.setTotalExperience(player.getTotalExperience() - (int)amount);
    }
    public void subBalance(long amount) {
        balance -= amount;
        player.setTotalExperience(player.getTotalExperience() + (int)amount);
    }

    public Player getPlayer() {
        return player;
    }
    
    public boolean canPlaceXPSign() {
        
        if (player.hasPermission("PickleXPBank.placeSign")) {
            
            final AccountManager acManager = AccountManager.getInstance();
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
