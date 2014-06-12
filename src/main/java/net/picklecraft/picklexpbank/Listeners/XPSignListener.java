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

import java.util.ArrayList;
import java.util.List;
import net.picklecraft.picklexpbank.Accounts.Account;
import net.picklecraft.picklexpbank.Factories.XPSignFactory;
import net.picklecraft.picklexpbank.PickleXPBank;
import net.picklecraft.picklexpbank.XPSign;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
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
        
        final String signCommand = plugin.getConfig().getString("settings.signCommand");
        if (event.getLine(0).equalsIgnoreCase(signCommand)) {
            
            Account account = PickleXPBank.getInstance().getAccountManager().getAccount(event.getPlayer());
            
            if (account.canPlaceXPSign()) {        
                    XPSign xPSign = XPSignFactory.createXPSign((Sign)event.getBlock().getState(), account);
                    PickleXPBank.getInstance().getAccountManager().addXPSign(xPSign);
            }
            else {
                event.setCancelled(true);
            }
            
        }
        /*else {
            //Incase a plugin modifies the sign
            XPSign xpSign = PickleXPBank.getInstance().getAccountManager().getXPSign((Sign)event.getBlock().getState());
            if (xpSign != null) {
                PickleXPBank.getInstance().getAccountManager().removeXPSign(xpSign);
            }
            
        }*/
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null) {
            if (event.getClickedBlock().getState() instanceof Sign) {
                XPSign xpSign = PickleXPBank.getInstance().getAccountManager().getXPSign((Sign)event.getClickedBlock().getState());
                if (xpSign != null) {
                    event.getPlayer().sendMessage(""+xpSign.getAccount().getBalance());
                    if (event.getPlayer().getUniqueId().compareTo(xpSign.getAccount().getPlayer().getUniqueId()) == 0) {

                        //Left click removes from the balance and adds to the player.
                        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                            final long rate = plugin.getConfig().getLong("settings.removeRate");
                            xpSign.getAccount().subBalance(rate);
                            event.getPlayer().sendMessage("Subtracted "+ rate +" from your account");
                        }
                        //Right click adds to the sign and removes from the player
                        else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                            final long rate = plugin.getConfig().getLong("settings.addRate");
                            xpSign.getAccount().addBalance(rate);
                            event.getPlayer().sendMessage("Added "+ rate +" to your account");
                        }

                    }
                    else {
                        event.getPlayer().sendMessage("Hey! that's not yours!");
                    }

                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockBreakEvent(BlockBreakEvent event) {
        if (event.getBlock().getState() instanceof Sign) {
            
            XPSign xpSign = PickleXPBank.getInstance().getAccountManager().getXPSign((Sign)event.getBlock().getState());
            if (xpSign != null) {
                if (xpSign.canPlayerRemove(event.getPlayer())) {
                    PickleXPBank.getInstance().getAccountManager().removeXPSign(xpSign);
                }
                else {
                    event.getPlayer().sendMessage("Hey! that's not yours!");
                    event.setCancelled(true);
                }
            }
            
        }
        else {
            //Search for a nearby sign
            List<Sign> signs = findSignNearBlock(event.getBlock());
            for (Sign sign : signs) {
                //if an xpsign exists, we need to cancel!
                XPSign xpSign = PickleXPBank.getInstance().getAccountManager().getXPSign(sign);
                if (xpSign != null) {
                    event.getPlayer().sendMessage("You need to break the XpSign first.");
                    event.setCancelled(true);
                    break;
                }
            }
            
        }
        
    }
    
    /*
    * this method finds signs that may be attached to a nearby block
    *   wS          wS = Wall sign 
    * wSBwS    S    S  = Standing sign
    *   wS     B    B  = Block
    */
    public List<Sign> findSignNearBlock(Block block) {
        List<Sign> signs = new ArrayList(5);
        for (int i = 0; i < 5; i++) {
            int x = 0, y = 0, z = 0;
            switch(i) {
                case 0:
                    x += 1;
                    break;
                case 1:
                    x -= 1;
                    break;
                case 2:
                    z += 1;
                    break;
                case 3:
                    z -= 1;
                    break;
                case 4:
                    y += 1;
                    break;
            }
            BlockState bs = block.getRelative(x, y, z).getState();
            if (bs instanceof Sign) {
                signs.add((Sign)bs);
            }
        }
        
        return signs;
    }
    
}
