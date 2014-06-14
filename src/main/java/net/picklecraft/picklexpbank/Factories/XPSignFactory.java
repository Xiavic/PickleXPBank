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
import net.picklecraft.picklexpbank.PickleXPBank;
import net.picklecraft.picklexpbank.XPSign;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

/**
 *
 * @author Pickle <curtisdhi@gmail.com>
 */
public class XPSignFactory {
    
    
    public static XPSign createXPSign(Location location, Account account) {
        return new XPSign(location, account);
    }
    
    public static XPSign createXPSign(Location location, UUID uuid) {
        Account account = PickleXPBank.getInstance().getAccountManager().getAccount(uuid);
        return createXPSign(location, account);
    }
    
    public static XPSign createXPSign(Account account, ResultSet rs) throws SQLException {
        Server server = PickleXPBank.getInstance().getServer();
        World world = server.getWorld(rs.getString("world"));
        Location location = new Location(world,rs.getInt("x"),rs.getInt("y"), rs.getInt("z"));
        return createXPSign(location, account);
    }
    
}
