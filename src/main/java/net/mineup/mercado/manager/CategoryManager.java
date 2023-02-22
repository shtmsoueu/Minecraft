package net.mineup.mercado.manager;

import java.io.*;
import org.bukkit.configuration.file.*;

import net.mineup.mercado.*;
import net.mineup.mercado.utils.itemhandler.*;

import org.bukkit.*;

import java.util.*;

public class CategoryManager
{
    private UPMarket plugin;
    private HashMap<String, Category> categories;
    
    public CategoryManager(UPMarket plugin) {
        this.plugin = plugin;
        this.categories = new HashMap<String, Category>();
    }
    
    public void createCategory(Category category) {
        this.categories.put(category.getName().toLowerCase(), category);
        File c = new File(this.plugin.getDataFolder(), "categorias" + File.separator + category.getName().toLowerCase() + ".yml");
        if (!c.exists()) {
            try {
                c.createNewFile();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
            YamlConfiguration config = YamlConfiguration.loadConfiguration(c);
            config.set("nome", category.getName());
            config.set("icone", category.getStringIcon());
            config.set("slot", category.getSlot());
            config.set("items", category.getMaterialsString());
            config.set("glow", category.isGlow());
            try {
                config.save(c);
            }
            catch (IOException ex2) {
                ex2.printStackTrace();
            }
        }
    }
    
    public Category getCategory(int slot) {
        for (Category c : this.categories.values()) {
            if (c.getSlot() == slot) {
                return c;
            }
        }
        return null;
    }
    
    public Category getCategory(String name) {
        return this.categories.get(name.toLowerCase());
    }
    
    public void loadCategories() {
        this.createDataFolder();
        File[] listFiles;
        for (int length = (listFiles = new File(this.plugin.getDataFolder(), "categorias").listFiles()).length, i = 0; i < length; ++i) {
            File file = listFiles[i];
            if (file.getName().toLowerCase().endsWith(".yml")) {
                this.loadCategory(file);
            }
        }
    }
    
    public void createDataFolder() {
        File datafolder = new File(this.plugin.getDataFolder(), "categorias");
        if (datafolder.exists()) {
            return;
        }
        datafolder.mkdirs();
        this.createDefaultCategories();
    }
    
    public void createDefaultCategories() {
        File ex1 = null;
        File ex2 = null;
        try {
            ex1 = new File(this.plugin.getDataFolder(), "categorias" + File.separator + "blocos.yml");
            if (!ex1.exists()) {
                ex1.createNewFile();
            }
            ex2 = new File(this.plugin.getDataFolder(), "categorias" + File.separator + "combate.yml");
            if (!ex2.exists()) {
                ex2.createNewFile();
            }
        }
        catch (IOException ex3) {
            ex3.printStackTrace();
        }
        YamlConfiguration cf1 = YamlConfiguration.loadConfiguration(ex1);
        cf1.set("nome", "Blocos");
        cf1.set("icone", "1:0;&7Blocos;&7Veja aqui todos os@&7blocos a venda!");
        cf1.set("slot", 11);
        cf1.set("items", Arrays.asList("1", "2", "3"));
        cf1.set("glow", true);
        try {
            cf1.save(ex1);
        }
        catch (Exception ex4) {
            ex4.printStackTrace();
        }
        YamlConfiguration cf2 = YamlConfiguration.loadConfiguration(ex2);
        cf2.set("nome", "Combate");
        cf2.set("icone", "276:0;&bCombate;&7Veja aqui todos os@&7itens de combate a venda!");
        cf2.set("slot", 13);
        cf2.set("items", Arrays.asList("267", "268", "272", "276", "283"));
        cf2.set("glow", false);
        try {
            cf2.save(ex2);
        }
        catch (Exception ex5) {
            ex5.printStackTrace();
        }
    }
    
    public void loadCategory(File category) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(category);
        List<Material> materials = new ArrayList<Material>();
        for (String input : config.getStringList("items")) {
            materials.add(ItemHandler.materialFromString(input));
        }
        this.createCategory(new Category(config.getString("nome"), ItemHandler.iconItemStack(config.getString("icone")), config.getBoolean("glow"), config.getInt("slot"), materials));
    }
    
    public boolean hasCategory(String name) {
        return this.categories.containsKey(name.toLowerCase());
    }
    
    public HashMap<String, Category> getCategories() {
        return this.categories;
    }
}
