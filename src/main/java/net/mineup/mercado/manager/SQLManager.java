package net.mineup.mercado.manager;

import org.bukkit.scheduler.*;

import net.mineup.mercado.*;
import net.mineup.mercado.database.*;
import net.mineup.mercado.utils.itemhandler.*;

import java.sql.*;
import org.bukkit.plugin.*;
import java.util.*;

public class SQLManager
{
    private UPMarket plugin;
    
    public SQLManager(final UPMarket plugin) {
        this.plugin = plugin;
    }
    
    public void addItem(final MarketItem item) {
        try {
            if (this.plugin.getStorage().getConnection().isClosed()) {
                this.plugin.getStorage().connect();
            }
            final Database database = this.plugin.getStorage();
            final PreparedStatement stmt = database.prepareStatement("INSERT INTO km_items (" + Columns.CATEGORY.getColumn() + ", " + Columns.UUID.getColumn() + ", " + Columns.ITEM.getColumn() + ", " + Columns.PRICE.getColumn() + ", " + Columns.OWNER.getColumn() + ", " + Columns.TIME.getColumn() + ") VALUES (?, ?, ?, ?, ?, ?)");
            stmt.setString(1, item.getCategory().getName());
            stmt.setString(2, item.getUUID());
            stmt.setString(3, ItemHandler.toString(item.getItem()));
            stmt.setDouble(4, item.getPrice());
            stmt.setString(5, item.getOwner());
            stmt.setLong(6, System.currentTimeMillis());
            stmt.execute();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void refreshItem(final String player) {
        if (player == null) {
            return;
        }
        new BukkitRunnable() {
            public void run() {
                try {
                    if (SQLManager.this.plugin.getStorage().getConnection().isClosed()) {
                        SQLManager.this.plugin.getStorage().connect();
                    }
                    final Database database = SQLManager.this.plugin.getStorage();
                    final PreparedStatement stmt = database.prepareStatement("SELECT * FROM km_items WHERE " + Columns.OWNER.getColumn() + "= ?");
                    stmt.setString(1, player);
                    final ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        final long expire = rs.getLong(Columns.TIME.getColumn());
                        final long after = SQLManager.this.plugin.getConfig().getInt("Timers.Tempo_Loja") * 60 * 60 * 1000;
                        final long atual = System.currentTimeMillis();
                        if (expire + after <= atual) {
                            SQLManager.this.plugin.getItensManager().addExpired(new MarketItem(ItemHandler.fromString(rs.getString(Columns.ITEM.getColumn())), null, rs.getString(Columns.OWNER.getColumn()), 0.0, rs.getString(Columns.UUID.getColumn())));
                            SQLManager.this.plugin.getItensManager().removeItem(rs.getString(Columns.UUID.getColumn()));
                        }
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.runTask((Plugin)UPMarket.getInstance());
    }
    
    public void deleteItem(final String uuid) {
        try {
            if (this.plugin.getStorage().getConnection().isClosed()) {
                this.plugin.getStorage().connect();
            }
            final Database database = this.plugin.getStorage();
            final PreparedStatement stmt = database.prepareStatement("DELETE FROM km_items WHERE " + Columns.UUID.getColumn() + "= ?");
            stmt.setString(1, uuid);
            stmt.executeUpdate();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public List<MarketItem> getEveryItems() {
        List<MarketItem> result = null;
        try {
            if (this.plugin.getStorage().getConnection().isClosed()) {
                this.plugin.getStorage().connect();
            }
            final Database database = this.plugin.getStorage();
            final PreparedStatement stmt = database.prepareStatement("SELECT * FROM km_items");
            result = new ArrayList<MarketItem>();
            final ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(new MarketItem(ItemHandler.fromString(rs.getString(Columns.ITEM.getColumn())), this.plugin.getCategoryManager().getCategory(rs.getString(Columns.CATEGORY.getColumn())), rs.getString(Columns.OWNER.getColumn()), rs.getDouble(Columns.PRICE.getColumn()), rs.getString(Columns.UUID.getColumn())));
            }
            return result;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return result;
        }
    }
    
    public int getAmountPlayerItems(final String player) {
        int i = 0;
        try {
            if (this.plugin.getStorage().getConnection().isClosed()) {
                this.plugin.getStorage().connect();
            }
            final Database database = this.plugin.getStorage();
            final PreparedStatement stmt = database.prepareStatement("SELECT " + Columns.ITEM.getColumn() + " FROM km_items WHERE " + Columns.OWNER.getColumn() + "= ?");
            stmt.setString(1, player);
            final ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ++i;
            }
            return i;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return i;
        }
    }
    
    public void addExpired(final MarketItem item) {
        try {
            if (this.plugin.getStorage().getConnection().isClosed()) {
                this.plugin.getStorage().connect();
            }
            final Database database = this.plugin.getStorage();
            final PreparedStatement stmt = database.prepareStatement("INSERT INTO km_expired (" + Columns.UUID.getColumn() + ", " + Columns.ITEM.getColumn() + ", " + Columns.OWNER.getColumn() + ", " + Columns.TIME.getColumn() + ") VALUES (?, ?, ?, ?)");
            stmt.setString(1, item.getUUID());
            stmt.setString(2, ItemHandler.toString(item.getItem()));
            stmt.setString(3, item.getOwner());
            stmt.setLong(4, System.currentTimeMillis());
            stmt.execute();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void deletePersonal(final String uuid) {
        try {
            if (this.plugin.getStorage().getConnection().isClosed()) {
                this.plugin.getStorage().connect();
            }
            final Database database = this.plugin.getStorage();
            final PreparedStatement stmt = database.prepareStatement("DELETE FROM km_personal WHERE " + Columns.UUID.getColumn() + "= ?");
            stmt.setString(1, uuid);
            stmt.executeUpdate();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void refreshExpired(final String player) {
        if (player == null) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (SQLManager.this.plugin.getStorage().getConnection().isClosed()) {
                        SQLManager.this.plugin.getStorage().connect();
                    }
                    final Database database = SQLManager.this.plugin.getStorage();
                    final PreparedStatement stmt = database.prepareStatement("SELECT * FROM km_expired WHERE " + Columns.OWNER.getColumn() + "= ?");
                    stmt.setString(1, player);
                    final ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        final long expire = rs.getLong(Columns.TIME.getColumn());
                        final long after = SQLManager.this.plugin.getConfig().getInt("Timers.Tempo_Expirado") * 60 * 60 * 1000;
                        final long atual = System.currentTimeMillis();
                        if (expire + after <= atual) {
                            SQLManager.this.plugin.getItensManager().removeExpired(rs.getString(Columns.UUID.getColumn()));
                        }
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }
    
    public List<MarketItem> getEveryExpired() {
        List<MarketItem> result = null;
        try {
            if (this.plugin.getStorage().getConnection().isClosed()) {
                this.plugin.getStorage().connect();
            }
            final Database database = this.plugin.getStorage();
            final PreparedStatement stmt = database.prepareStatement("SELECT * FROM km_expired");
            result = new ArrayList<MarketItem>();
            final ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(new MarketItem(ItemHandler.fromString(rs.getString(Columns.ITEM.getColumn())), null, rs.getString(Columns.OWNER.getColumn()), 0.0, rs.getString(Columns.UUID.getColumn())));
            }
            return result;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return result;
        }
    }
    
    public void deleteExpired(final String uuid) {
        try {
            if (this.plugin.getStorage().getConnection().isClosed()) {
                this.plugin.getStorage().connect();
            }
            final Database database = this.plugin.getStorage();
            final PreparedStatement stmt = database.prepareStatement("DELETE FROM km_expired WHERE " + Columns.UUID.getColumn() + "= ?");
            stmt.setString(1, uuid);
            stmt.executeUpdate();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void addPersonal(final PersonalItem item) {
        try {
            if (this.plugin.getStorage().getConnection().isClosed()) {
                this.plugin.getStorage().connect();
            }
            final Database database = this.plugin.getStorage();
            final PreparedStatement stmt = database.prepareStatement("INSERT INTO km_personal (" + Columns.UUID.getColumn() + ", " + Columns.ITEM.getColumn() + ", " + Columns.PRICE.getColumn() + ", " + Columns.OWNER.getColumn() + ", " + Columns.BUYER.getColumn() + ") VALUES (?, ?, ?, ?, ?)");
            stmt.setString(1, item.getUUID());
            stmt.setString(2, ItemHandler.toString(item.getItem()));
            stmt.setDouble(3, item.getPrice());
            stmt.setString(4, item.getOwner());
            stmt.setString(5, item.getBuyer());
            stmt.execute();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public List<PersonalItem> getEveryPersonal() {
        List<PersonalItem> result = null;
        try {
            if (this.plugin.getStorage().getConnection().isClosed()) {
                this.plugin.getStorage().connect();
            }
            final Database database = this.plugin.getStorage();
            final PreparedStatement stmt = database.prepareStatement("SELECT * FROM km_personal");
            result = new ArrayList<PersonalItem>();
            final ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(new PersonalItem(ItemHandler.fromString(rs.getString(Columns.ITEM.getColumn())), rs.getString(Columns.OWNER.getColumn()), rs.getString(Columns.BUYER.getColumn()), rs.getDouble(Columns.PRICE.getColumn()), rs.getString(Columns.UUID.getColumn())));
            }
            return result;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return result;
        }
    }
    
    public int getAmountPersonalItems(final String player) {
        int i = 0;
        try {
            if (this.plugin.getStorage().getConnection().isClosed()) {
                this.plugin.getStorage().connect();
            }
            final Database database = this.plugin.getStorage();
            final PreparedStatement stmt = database.prepareStatement("SELECT " + Columns.ITEM.getColumn() + " FROM km_personal WHERE " + Columns.BUYER.getColumn() + "= ?");
            stmt.setString(1, player);
            final ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ++i;
            }
            return i;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return i;
        }
    }
}
