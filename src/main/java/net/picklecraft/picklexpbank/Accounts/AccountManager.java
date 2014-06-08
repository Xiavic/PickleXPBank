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
import net.picklecraft.picklexpbank.PickleXPBank;
import net.picklecraft.picklexpbank.XPSign;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Pickle <curtisdhi@gmail.com>
 */
public class AccountManager {
    
    private final long UPDATE_SIGN_RATE = 20; //1 second
    
    private final PickleXPBank plugin;
    
    private List<Account> accounts = new ArrayList<>();
    private List<XPSign> xpSigns = new ArrayList<>();
    
    public AccountManager(PickleXPBank plugin) {
        this.plugin = plugin;
        new BukkitRunnable() {

            @Override
            public void run() {
                updateXPSigns();
            }
        }.runTaskTimer(plugin, 0, 20);
    }
    
    public PickleXPBank getPlugin() {
        return plugin;
    }
    
    public void addAccount(Account account) {
        accounts.add(account);
    }
         
    public void removeAccount(Account account) {
        accounts.remove(account);
    }
    
    public Account getAccount(Player player) {
        for (Account account : accounts) {
            if (account.getPlayer() == player) {
                return account;
            }
        }
        //no account existes, so lets make one.
        Account account = new Account(player);
        addAccount(account);
        return account;
    }
    
    public void addXPSign(XPSign xpSign) {
        xpSigns.add(xpSign);
    }
    
    public void removeXPSign(XPSign xpSign) {
        xpSigns.remove(xpSign);
    }
    
    public XPSign getXPSign(Sign sign) {
        for (XPSign xpSign : xpSigns) {
            if (xpSign.getSign() == sign) {
                return xpSign;
            }
        }
        return null;
    }
    
    public void updateXPSigns() {
        for (XPSign xpSign : xpSigns) {
            xpSign.update();
        }
    }
    
}