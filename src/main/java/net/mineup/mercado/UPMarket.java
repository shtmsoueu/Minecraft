package net.mineup.mercado;

import org.bukkit.plugin.java.*;
import net.milkbowl.vault.economy.*;
import net.mineup.mercado.database.*;
import net.mineup.mercado.manager.*;

import java.sql.*;
import java.net.*;
import java.io.*;
import org.bukkit.plugin.*;

public class UPMarket extends JavaPlugin
{
    private static UPMarket instance;
    public static String NMS_VERSION;
    private Economy econ;
    private CategoryManager categoryManager;
    private ItensManager itensManager;
    private SQLManager sqlManager;
    private LanguageManager langManager;
    private Database storage;
    private boolean useMySQL;
    private File databaseFolder;
    
    public UPMarket() {
        this.useMySQL = false;
        this.databaseFolder = new File(this.getDataFolder(), "database.db");
    }
    
    public void onEnable() {
        UPMarket.instance = this;
        UPMarket.NMS_VERSION = this.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        this.saveDefaultConfig();
        this.langManager = new LanguageManager(this);
        new MarketCommand(this);
        new Listeners(this);
        this.useMySQL = this.getConfig().getBoolean("MySQL.Use");
        this.setupEconomy();
        if (this.useMySQL) {
            this.storage = new Database(this.getConfig().getString("MySQL.Host"), this.getConfig().getString("MySQL.Database"), this.getConfig().getString("MySQL.User"), this.getConfig().getString("MySQL.Password"));
        }
        else {
            try {
                if (!this.databaseFolder.exists()) {
                    this.databaseFolder.createNewFile();
                }
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
            this.storage = new Database(this.databaseFolder);
        }
        PreparedStatement stmt = this.storage.prepareStatement("CREATE TABLE IF NOT EXISTS km_items (" + Columns.CATEGORY.getColumn() + " VARCHAR(50) NOT NULL, " + Columns.UUID.getColumn() + " VARCHAR(50) NOT NULL, " + Columns.ITEM.getColumn() + " TEXT NOT NULL, " + Columns.PRICE.getColumn() + " DOUBLE(64,2) DEFAULT 0.0, " + Columns.OWNER.getColumn() + " VARCHAR(25) NOT NULL, " + Columns.TIME.getColumn() + " REAL)");
        PreparedStatement stmt2 = this.storage.prepareStatement("CREATE TABLE IF NOT EXISTS km_expired (" + Columns.UUID.getColumn() + " VARCHAR(50) NOT NULL, " + Columns.ITEM.getColumn() + " TEXT NOT NULL, " + Columns.OWNER.getColumn() + " VARCHAR(25) NOT NULL, " + Columns.TIME.getColumn() + " REAL)");
        PreparedStatement stmt3 = this.storage.prepareStatement("CREATE TABLE IF NOT EXISTS km_personal (" + Columns.UUID.getColumn() + " VARCHAR(50) NOT NULL, " + Columns.ITEM.getColumn() + " TEXT NOT NULL, " + Columns.PRICE.getColumn() + " DOUBLE(64,2) DEFAULT 0.0, " + Columns.OWNER.getColumn() + " VARCHAR(25) NOT NULL, " + Columns.BUYER.getColumn() + " VARCHAR(25) NOT NULL)");
        this.storage.update(stmt);
        this.storage.update(stmt2);
        this.storage.update(stmt3);
        this.sqlManager = new SQLManager(this);
        (this.categoryManager = new CategoryManager(this)).loadCategories();
        (this.itensManager = new ItensManager(this)).loadExpireds();
        this.itensManager.loadItems();
        this.itensManager.loadPersonals();
    }
    
    public static UPMarket getInstance() {
        return UPMarket.instance;
    }
    
    public Economy getEcon() {
        return this.econ;
    }
    
    public CategoryManager getCategoryManager() {
        return this.categoryManager;
    }
    
    public ItensManager getItensManager() {
        return this.itensManager;
    }
    
    public boolean verifyKey(String site, String code) {
        try {
            String webPage = site;
            URL url = new URL(webPage);
            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
            if (urlConnection.getResponseCode() != 200) {
                return false;
            }
            InputStream is = urlConnection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            char[] charArray = new char[1024];
            StringBuffer sb = new StringBuffer();
            int numCharsRead;
            while ((numCharsRead = isr.read(charArray)) > 0) {
                sb.append(charArray, 0, numCharsRead);
            }
            String result = sb.toString();
            urlConnection.disconnect();
            return result.toLowerCase().contains(code.toLowerCase());
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public SQLManager getSQLManager() {
        return this.sqlManager;
    }
    
    public LanguageManager getLangManager() {
        return this.langManager;
    }
    
    public Database getStorage() {
        return this.storage;
    }
    
    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = (RegisteredServiceProvider<Economy>)this.getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            this.econ = (Economy)economyProvider.getProvider();
        }
        return this.econ != null;
    }
}
