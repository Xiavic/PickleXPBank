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
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import net.picklecraft.picklexpbank.Accounts.Account;
import net.picklecraft.picklexpbank.Accounts.AccountManager;
import net.picklecraft.picklexpbank.Factories.AccountFactory;
import net.picklecraft.picklexpbank.Factories.XPSignFactory;
import org.bukkit.Bukkit;

/**
 *
 * @author Pickle <curtisdhi@gmail.com>
 */
public class Updater {
    private final PickleXPBank plugin;

    
    public Updater(PickleXPBank plugin) {
        this.plugin = plugin;
    }
    
    public void loadFromSql(AccountManager accountManager) {
        final Connection conn = plugin.getConnection();
        final String selectAccountsStatement = "SELECT FROM `pxpb_accounts`;";
        final String selectXPSignsStatement = "SELECT FROM `pxpb_xpsigns` WHERE account_id = ?;";
        try (PreparedStatement psAccounts = conn.prepareStatement(selectAccountsStatement)) {
            final ResultSet rsAccounts = psAccounts.executeQuery();
            
            while (rsAccounts.next()) {
                final Account account = AccountFactory.createAccount(rsAccounts);
                accountManager.addAccount(account);
                
                //this server doesn't recongize this player?
                if (account == null) {
                    continue;
                }
                
                try (PreparedStatement psSigns = conn.prepareStatement(selectXPSignsStatement)) {
                    psSigns.setString(1, account.getPlayer().getUniqueId().toString());
                    final ResultSet rsSigns = psSigns.executeQuery();

                    while (rsSigns.next()) {
                        accountManager.addXPSign(XPSignFactory.createXPSign(account, rsSigns));
                    }

                }
                catch (final SQLException ex) {
                    Bukkit.getLogger().severe("[SQLException] Failed to get xpsigns from Sql.");
                }
                
            }
        
        }
        catch (final SQLException ex) {
            Bukkit.getLogger().severe("[SQLException] Failed to get accounts from Sql.");
        }
        
        
    }
    
    public void checkTables() throws SQLException {
        final Connection conn = plugin.getConnection();
       
        final Statement state = conn.createStatement();
        final DatabaseMetaData dbm = conn.getMetaData();
        conn.setAutoCommit(true);

        createTable(dbm, state, "pxpb_accounts", "id char(32) NOT NULL,"+
            " balance bigint(20) NOT NULL, PRIMARY KEY (id), UNIQUE KEY player_id (id))"+
            " ENGINE=InnoDB DEFAULT CHARSET=utf8;");
            
        createTable(dbm, state, "pxpb_xpsigns", "id int(11) NOT NULL AUTO_INCREMENT,"+
            " account_id int(11) NOT NULL, world varchar(20) NOT NULL, x int(11) NOT NULL,"+
            " y int(11) NOT NULL, z int(11) NOT NULL, PRIMARY KEY (id))"+
            " ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;");
    }
    
    private static void createTable(DatabaseMetaData dbm, Statement state, String table, 
            String query) throws SQLException {
        if (!dbm.getTables(null, null, table, null).next()) {
            Bukkit.getLogger().log(Level.INFO, "Creating table {0}.", table);
            state.execute("CREATE TABLE `" + table + "` " + query);
            
            if (!dbm.getTables(null, null, table, null).next()) {
                throw new SQLException("Table " + table + " not found and failed to create");
            }
        }
    }
    
    
}
