package net.mineup.mercado.manager;

import java.util.*;

import net.mineup.mercado.*;

public class ItensManager
{
    private UPMarket plugin;
    private Map<String, MarketItem> items;
    private Map<String, MarketItem> expireds;
    private Map<String, PersonalItem> personal;
    
    public ItensManager(UPMarket plugin) {
        this.items = new HashMap<String, MarketItem>();
        this.expireds = new HashMap<String, MarketItem>();
        this.personal = new HashMap<String, PersonalItem>();
        this.plugin = plugin;
    }
    
    public void addItem(MarketItem m) {
        this.items.put(m.getUUID(), m);
        this.plugin.getSQLManager().addItem(m);
    }
    
    public void removeItem(String uuid) {
        this.items.remove(uuid);
        this.plugin.getSQLManager().deleteItem(uuid);
    }
    
    public MarketItem getItem(String uuid) {
        return this.items.get(uuid);
    }
    
    public void loadItems() {
        for (MarketItem m : this.plugin.getSQLManager().getEveryItems()) {
            this.items.put(m.getUUID(), m);
        }
    }
    
    @SuppressWarnings("deprecation")
	public List<MarketItem> searchItem(String input) {
        List<MarketItem> output = new ArrayList<MarketItem>();
        for (MarketItem mi : this.items.values()) {
            if (input.matches("(\\w|\\s)+")) {
                if (mi.getItem().getItemMeta().hasDisplayName() && mi.getItem().getItemMeta().getDisplayName().contains(input)) {
                    output.add(mi);
                }
                if (!mi.getOwner().equalsIgnoreCase(input)) {
                    continue;
                }
                output.add(mi);
            }
            else if (input.matches("\\d+:\\d+")) {
                if (mi.getItem().getTypeId() != Integer.parseInt(input.split(":")[0])) {
                    continue;
                }
                output.add(mi);
            }
            else {
                if (!input.matches("\\d+") || mi.getItem().getTypeId() != Integer.parseInt(input)) {
                    continue;
                }
                output.add(mi);
            }
        }
        return output;
    }
    
    public Map<String, MarketItem> getItems() {
        return this.items;
    }
    
    public int getItems(Category category) {
        int result = 0;
        if (category == null) {
            return 0;
        }
        for (MarketItem item : this.items.values()) {
            if (item.getCategory() == category) {
                ++result;
            }
        }
        return result;
    }
    
    public void addExpired(MarketItem m) {
        this.expireds.put(m.getUUID(), m);
        this.plugin.getSQLManager().addExpired(m);
    }
    
    public void removeExpired(String uuid) {
        this.expireds.remove(uuid);
        this.plugin.getSQLManager().deleteExpired(uuid);
    }
    
    public MarketItem getExpired(String uuid) {
        return this.expireds.get(uuid);
    }
    
    public void loadExpireds() {
        for (MarketItem m : this.plugin.getSQLManager().getEveryExpired()) {
            this.expireds.put(m.getUUID(), m);
        }
    }
    
    public Map<String, MarketItem> getExpireds() {
        return this.expireds;
    }
    
    public void addPersonal(PersonalItem m) {
        this.personal.put(m.getUUID(), m);
        this.plugin.getSQLManager().addPersonal(m);
    }
    
    public void removePersonal(String uuid) {
        if (this.personal.containsKey(uuid)) {
            this.personal.remove(uuid);
        }
        this.plugin.getSQLManager().deletePersonal(uuid);
    }
    
    public PersonalItem getPersonal(String uuid) {
        return this.personal.get(uuid);
    }
    
    public void loadPersonals() {
        for (PersonalItem m : this.plugin.getSQLManager().getEveryPersonal()) {
            this.personal.put(m.getUUID(), m);
        }
    }
    
    public int personalAmount(String player) {
        int i = 0;
        for (PersonalItem item : this.getPersonal().values()) {
            if (item.getBuyer().equalsIgnoreCase(player)) {
                ++i;
            }
        }
        return i;
    }
    
    public Map<String, PersonalItem> getPersonal() {
        return this.personal;
    }
}
