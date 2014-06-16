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
public class AdminBankExecutor implements CommandExecutor {
    private final PickleXPBank plugin = PickleXPBank.getInstance();
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("PickleXPBank.admin.modifyAccounts")) {
            if (args.length >= 2) {
                final Account account = plugin.getAccountManager().getAccount(args[0]);      
                if (account == null) {
                    sender.sendMessage("Either too many matches for "+ args[0] +" or isn't online.");
                    return false;
                }

                int amount = 0;
                try {
                    amount = Integer.parseInt(args[1]);
                }
                catch (NumberFormatException ex) {
                    sender.sendMessage("The value must be an integer");
                    return false;
                }

                if (command.getName().equalsIgnoreCase("xpadd")) {
                    account.addBalance(amount);
                    sender.sendMessage("Added "+ amount +" to "+ account.getPlayerName());
                    return true;
                }
                else if (command.getName().equalsIgnoreCase("xpsub")) {
                    account.subBalance(amount);
                    sender.sendMessage("Subtracted "+ amount +" from "+ account.getPlayerName());
                    return true;
                }
                else if (command.getName().equalsIgnoreCase("xpset")) {
                    account.setBalance(amount);
                    sender.sendMessage("Set "+ account.getPlayerName() +" to "+ amount);
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
