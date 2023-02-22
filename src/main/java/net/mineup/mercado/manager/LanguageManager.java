package net.mineup.mercado.manager;

import java.io.*;
import org.bukkit.configuration.file.*;

import net.mineup.mercado.*;
import net.mineup.mercado.utils.geral.*;

import java.util.*;

public class LanguageManager
{
    private UPMarket plugin;
    private File langFile;
    private YamlConfiguration langConfig;
    
    public LanguageManager(final UPMarket plugin) {
        this.plugin = plugin;
        this.langFile = new File(plugin.getDataFolder(), "lang.yml");
        if (!this.langFile.exists()) {
            plugin.saveResource("lang.yml", true);
        }
        this.langConfig = YamlConfiguration.loadConfiguration(this.langFile);
    }
    
    public String getString(final String path) {
        return this.format(this.langConfig.getString(path));
    }
    
    public List<String> getStringList(final String path) {
        final List<String> output = new ArrayList<String>();
        for (final String index : this.langConfig.getStringList(path)) {
            output.add(this.format(index));
        }
        return output;
    }
    
    private String format(final String input) {
        return Text.colorize(input);
    }
    
    protected UPMarket getPlugin() {
        return this.plugin;
    }
}
