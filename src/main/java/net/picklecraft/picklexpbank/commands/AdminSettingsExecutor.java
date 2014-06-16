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

package net.picklecraft.picklexpbank.commands;

import net.picklecraft.picklexpbank.Accounts.Account;
import net.picklecraft.picklexpbank.PickleXPBank;
import static net.picklecraft.picklexpbank.PickleXPBank.getPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Pickle <curtisdhi@gmail.com>
 */
public class AdminSettingsExecutor implements CommandExecutor {
    private final PickleXPBank plugin = PickleXPBank.getInstance();
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("PickleXPBank.admin.modifySettings")) {
            if (args.length >= 2) {
                int amount = 0;
                try {
                    amount = Integer.parseInt(args[1]);
                }
                catch (NumberFormatException ex) {
                    sender.sendMessage("The value must be an integer");
                    return false;
                }

                if (args[1].equalsIgnoreCase("addRate")) {
                    plugin.getConfig().set("settings.addRate", amount);
                    return true;
                }

                else if (args[1].equalsIgnoreCase("removeRate")) {
                    plugin.getConfig().set("settings.removeRate", amount);
                    return true;
                }

                else if (args[1].equalsIgnoreCase("signLimit")) {
                    plugin.getConfig().set("settings.signLimit", amount);
                    return true;
                }
            }
            return false;
            
        }
        else {
            sender.sendMessage("You do not have permission for that.");
            return true;
        }
    }
    
}
