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

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.UUID;
import net.picklecraft.picklexpbank.Factories.AccountFactory;
import net.picklecraft.picklexpbank.PickleXPBank;
import net.picklecraft.picklexpbank.XPSign;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
/**
 *
 * @author Pickle <curtisdhi@gmail.com>
 */
public class AccountManager extends TimerTask {
    
    private final long UPDATE_SIGN_RATE = 20; //1 second
    
    private final PickleXPBank plugin;
    
    private List<Account> accounts = new ArrayList<>();
    private List<XPSign> xpSigns = new ArrayList<>();

    public AccountManager(PickleXPBank plugin) {
        this.plugin = plugin;

    }
    
    public PickleXPBank getPlugin() {
        return plugin;
    }
    
    public void addAccount(Account account) {
        if (account == null) {
            return;
        }
        plugin.getConsumer().queueAccount(account);
        accounts.add(account);
    }
         
    public void removeAccount(Account account) {
        accounts.remove(account);
    }
    
    public Account getAccount(UUID uuid) {
        for (Account account : accounts) {
            if (account.getUuid().compareTo(uuid) == 0) {
                return account;
            }
        }
        //no account existes, so lets make one.
        Account account = AccountFactory.createAccount(uuid);
        addAccount(account);
        return account;
    }
    
    /*
     * Will return a Account if a match was found,
     * else it shall return null
     *
     * @return Account
     */
    public Account getAccount(String name) {
        Account accountMatch = null;
        name = name.toLowerCase();
        int accountMatches = 0;
        for (Account a : accounts) {
            String name2 = a.getPlayerName().toLowerCase();
            if (name2.contains(name)) {
                accountMatch = a;
                if (name2.equals(name)) {
                    break;
                } 
                //If a second match is found, set playerMatch to null and break out
                else if (++accountMatches > 1) {
                    accountMatch = null;
                    break;
                }
            }
        }
        return accountMatch;
    }
    
    public void addXPSign(XPSign xpSign) {
        if (xpSign == null) {
            return;
        }
        plugin.getConsumer().queueXPSign(xpSign);
        xpSigns.add(xpSign);
    }
    
    public void removeXPSign(XPSign xpSign) {
        plugin.getConsumer().queueXPSign(xpSign);
        xpSigns.remove(xpSign);
    }
    
    public XPSign getXPSign(Location location) {
        
        for (XPSign xpSign : xpSigns) {
            if (xpSign.getLocation().getWorld().getUID().compareTo(location.getWorld().getUID()) == 0 &&
                    xpSign.getLocation().getBlockX() == location.getBlockX() &&
                    xpSign.getLocation().getBlockY() == location.getBlockY() &&
                    xpSign.getLocation().getBlockZ() == location.getBlockZ()) {  
                return xpSign;
            }
        }
        return null;
    }
    
    public int countXPSigns(Account account) {
        int count = 0;
        
        for (XPSign xpSign : xpSigns) {
            if (xpSign.getAccount() == account) {
                count++;
            }
        }
        
        return count;
    }
    
    @Override
    public void run() {
        updateXPSigns();
    }
    
    public void updateXPSigns() {
        for (XPSign xpSign : xpSigns) {
            xpSign.update();
        }
    }
    
}
