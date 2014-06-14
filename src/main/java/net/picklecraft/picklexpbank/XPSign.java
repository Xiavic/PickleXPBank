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

import net.picklecraft.picklexpbank.Accounts.Account;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 *
 * @author Pickle <curtisdhi@gmail.com>
 */
public class XPSign {
    private final Location location;
    
    private final Account ownerAccount;
    
    private boolean isRemoved;
    
    public XPSign(Location location, Account ownerAccount) {
        this.location = location;
        this.ownerAccount = ownerAccount;
    }
    
    public Location getLocation() {
        return location;
    }
    
    public Account getAccount() {
        return ownerAccount;     
    }
    
    public void setIsRemoved(boolean isRemoved) {
        this.isRemoved = isRemoved;
    }
    
    public boolean isRemoved() {
        return isRemoved;
    }
    
    public void update() {
        Block b = location.getBlock();
        if (b.getState() instanceof Sign) {
            Sign sign = (Sign)b.getState();
            sign.setLine(0, PickleXPBank.getInstance().getConfig().getString("settings.signCommand"));
            sign.setLine(1, ownerAccount.getPlayerName());
            sign.setLine(3, String.valueOf(ownerAccount.getBalance()));
            sign.update();
        }
        else {
            isRemoved = true;
        }
    }
    
    public boolean canPlayerRemove(Player player) {
        if (player == ownerAccount.getPlayer() || player.hasPermission("PickleXPBank.admin.destroy")) {
            return true;
        }
        return false;
    }
    
    
}
