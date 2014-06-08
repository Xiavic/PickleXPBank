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

package net.picklecraft.picklexpbank.Listeners;

import net.picklecraft.picklexpbank.PickleXPBank;
import net.picklecraft.picklexpbank.XPSign;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author Pickle <curtisdhi@gmail.com>
 */
public class XPSignListener implements Listener {
    
    private final PickleXPBank plugin;
            
    public XPSignListener(PickleXPBank plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onSignChangeEvent(SignChangeEvent event) {
        String signCommand = plugin.getConfig().getString("settings.signCommand");
        if (event.getLine(0).equalsIgnoreCase(signCommand)) {
            if (event.getPlayer().hasPermission("PickleXPBank.placeSign")) {
                XPSign xPSign = new XPSign((Sign)event.getBlock().getState(), 
                    plugin.getAccountManager().getAccount(event.getPlayer()));
                plugin.getAccountManager().addXPSign(xPSign);
            }
            else {
                event.getPlayer().sendMessage("I'm sorry, but you lack permission.");
                event.setCancelled(true);
            }
        }
        else {
            //Incase a plugin modifies the sign
            XPSign xpSign = plugin.getAccountManager().getXPSign((Sign)event.getBlock().getState());
            if (xpSign != null) {
                plugin.getAccountManager().removeXPSign(xpSign);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getClickedBlock().getState() instanceof Sign) {
            XPSign xpSign = plugin.getAccountManager().getXPSign((Sign)event.getClickedBlock().getState());
            if (xpSign != null) {
                
                if (event.getPlayer() == xpSign.getAccount().getPlayer()) {
                    
                    //Left click removes from the balance and adds to the player.
                    if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                        long rate = plugin.getConfig().getLong("settings.removeRate");
                        xpSign.getAccount().subBalance(rate);
                    }
                    //Right click adds to the sign and removes from the player
                    else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        long rate = plugin.getConfig().getLong("settings.addRate");
                        xpSign.getAccount().addBalance(rate);
                    }
                    
                }
                else {
                    event.getPlayer().sendMessage("Hey! that's not yours!");
                }
                
            }
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockBreakEvent(BlockBreakEvent event) {
        if (event.getBlock().getState() instanceof Sign) {
            
            XPSign xpSign = plugin.getAccountManager().getXPSign((Sign)event.getBlock().getState());
            if (xpSign != null) {
                if (event.getPlayer() == xpSign.getAccount().getPlayer()) {
                    plugin.getAccountManager().removeXPSign(xpSign);
                }
                else {
                    event.getPlayer().sendMessage("Hey! that's not yours!");
                    event.setCancelled(true);
                }
            }
            
        }
    }
    
}
