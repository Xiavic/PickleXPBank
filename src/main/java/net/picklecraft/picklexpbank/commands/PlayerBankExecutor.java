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
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Pickle <curtisdhi@gmail.com>
 */
public class PlayerBankExecutor implements CommandExecutor {
    private final PickleXPBank plugin = PickleXPBank.getInstance();
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
     //player commands
        if (sender instanceof Player) {
            if (args.length >= 2) {
                if (sender.hasPermission("PickleXPBank.transfer")) {
                    final Player senderPlayer = (Player)sender;
                    final Account senderAccount = plugin.getAccountManager().getAccount(senderPlayer.getUniqueId());
                    final Account receiverAccount = plugin.getAccountManager().getAccount(args[0]);

                    if (receiverAccount == null) {
                        sender.sendMessage("Either too many matches for "+ args[0] +" or doesn't exist.");
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

                    if (senderAccount.subBalance(amount)) {
                        sender.sendMessage("You have sent "+ amount +" to "+ receiverAccount.getPlayerName());
                        receiverAccount.addBalance(amount);
                        
                        final Player receiverPlayer = receiverAccount.getPlayer();
                        if (receiverPlayer != null) {
                            receiverPlayer.sendMessage("You have received "+ amount +" from "+ senderAccount.getPlayerName());
                        }
                    }
                    else {
                        sender.sendMessage("You do not have "+ amount +" to send to "+ receiverAccount.getPlayerName());
                    }

                    return true;
                }
                else {
                    sender.sendMessage("You do not have permission for that.");
                    return true;
                }
            }
            
            return false;
        }
        else {
            sender.sendMessage("You must be a player to use this command.");
            return true;
        }
        
    }
    
}
