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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Queue;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import net.picklecraft.picklexpbank.Accounts.Account;
import org.bukkit.Bukkit;

/**
 * This code borrows a lot from https://github.com/LogBlock/LogBlock/blob/master/src/main/java/de/diddiz/LogBlock/Consumer.java
 * Credit to Diddiz and the other logblock authors.
 * @author Pickle <curtisdhi@gmail.com>
 */
public class Consumer extends TimerTask {
    
    private final long TIME_PER_RUN = 1000; 
    private final PickleXPBank plugin;
    
    private final Queue<PreparedStatementRow> queue = new LinkedBlockingQueue<>();
    
    public Consumer(PickleXPBank plugin) {
        this.plugin = plugin;
    }
    
    public void queueAccount(Account account) {
        queue.offer(new AccountRow(account));
    }

    public void queueXPSign(XPSign xpSign) {
        queue.offer(new XPSignRow(xpSign));
    }
    
    @Override
    public void run() {
        if (queue.isEmpty()) { return; }
        
        try {
            final Connection conn = plugin.getConnection();
            if (conn == null) {
                return;
            }
            conn.setAutoCommit(false);

            final long start = System.currentTimeMillis();
            
            while (!queue.isEmpty() && (System.currentTimeMillis() - start) < TIME_PER_RUN) {
                final PreparedStatementRow row = queue.poll();
                if (row == null) { continue; }

                try {
                    row.setConnection(conn);
                    row.executeStatements();
                }
                catch (final SQLException ex) {
                    Bukkit.getLogger().log(Level.SEVERE, "SQL exception on insertion: ", ex);
                }
            }
            conn.commit();
        }
        catch (final SQLException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "SQL exception on insertion: ", ex);
        }
        
        
    }
    
    private interface PreparedStatementRow
    {

        abstract void setConnection(Connection connection);
        abstract void executeStatements() throws SQLException;

    }
    
    private class AccountRow implements PreparedStatementRow {
        private Connection connection;
        private final Account account;
        
        public AccountRow(Account account) {
            this.account = account;
        }
        
        @Override
        public void setConnection(Connection connection) {
            this.connection = connection;
        }

        @Override
        public void executeStatements() throws SQLException {
            String sql = "INSERT INTO `pxpb_accounts` (id, name, balance) values(?,?,?) on duplicate key update balance=?";
            PreparedStatement ps = null;
            try {
                ps = connection.prepareStatement(sql);
                ps.setString(1, account.getUuid().toString());
                ps.setString(2, account.getPlayerName());
                ps.setLong(3, account.getBalance());
                ps.setLong(4, account.getBalance());
                
                ps.executeUpdate();
            }
            //Don't bother catching and just let the caller handle it.
            finally {
            // individual try/catch here, though ugly, prevents resource leaks
                if(ps != null) {
                    try {
                        ps.close();
                    }
                    catch(SQLException ex) {
                        Bukkit.getLogger().severe(ex.getMessage());
                    }
                }
            }
        }
    
    }
    
    private class XPSignRow implements PreparedStatementRow {
        private Connection connection;
        private final XPSign xpSign;
        
        public XPSignRow(XPSign xpSign) {
            this.xpSign = xpSign;
        }
        
        @Override
        public void setConnection(Connection connection) {
            this.connection = connection;
        }

        @Override
        public void executeStatements() throws SQLException {
            PreparedStatement ps = null;
            try {
                if (xpSign.isRemoved()) {
                    ps = getDeleteStatement(ps);
                    
                    xpSign.setIsRemoved(true);
                }
                else {
                    ps = getInsertStatement(ps);
                }
                
                if (ps != null) {
                    ps.executeUpdate();
                }
                
            }
            //Don't bother catching and just let the caller handle it.
            finally {
            // individual try/catch here, though ugly, prevents resource leaks
                if(ps != null) {
                    try {
                        ps.close();
                    }
                    catch(SQLException ex) {
                        Bukkit.getLogger().severe(ex.getMessage());
                    }
                }
            }
            
        }
        
        private PreparedStatement getInsertStatement(PreparedStatement ps) throws SQLException {
            
            String sql = "INSERT INTO `pxpb_xpsigns` (account_id, world, x, y, z) values(?,?,?,?,?)";
            ps = connection.prepareStatement(sql);
            ps.setString(1, xpSign.getAccount().getUuid().toString());
            ps.setString(2, xpSign.getLocation().getWorld().getName());
            ps.setInt(3, xpSign.getLocation().getBlockX());
            ps.setInt(4, xpSign.getLocation().getBlockY());
            ps.setInt(5, xpSign.getLocation().getBlockZ());

            return ps;
        }
        
        private PreparedStatement getDeleteStatement(PreparedStatement ps) throws SQLException {
            
            String sql = "DELETE FROM `pxpb_xpsigns` WHERE account_id=? AND world=? AND x=? AND y=? AND z=?";
            ps = connection.prepareStatement(sql);
            ps.setString(1, xpSign.getAccount().getUuid().toString());
            ps.setString(2, xpSign.getLocation().getWorld().getName());
            ps.setInt(3, xpSign.getLocation().getBlockX());
            ps.setInt(4, xpSign.getLocation().getBlockY());
            ps.setInt(5, xpSign.getLocation().getBlockZ());

            return ps;
        }

    }
    
    
}

