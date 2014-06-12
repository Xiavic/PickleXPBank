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
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 *
 * @author Pickle <curtisdhi@gmail.com>
 */
public class XPSign {
    private final Sign sign;
    private final Account ownerAccount;
    
    private boolean isRemoved;
    
    public XPSign(Sign sign, Account ownerAccount) {
        this.sign = sign;
        this.ownerAccount = ownerAccount;
    }
    
    public Sign getSign() {
        return sign;
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
        sign.setLine(0, PickleXPBank.getInstance().getConfig().getString("settings.signCommand"));
        sign.setLine(1, ownerAccount.getPlayer().getName());
        sign.setLine(3, String.valueOf(ownerAccount.getBalance()));
        sign.update();
    }
    
    public boolean canPlayerRemove(Player player) {
        if (player == ownerAccount.getPlayer() || player.hasPermission("PickleXPBank.admin.destroy")) {
            return true;
        }
        return false;
    }
    
    
}
