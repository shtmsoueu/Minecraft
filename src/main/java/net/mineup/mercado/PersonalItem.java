package net.mineup.mercado;

import org.bukkit.inventory.*;

import java.text.*;
import java.util.*;
import org.bukkit.inventory.meta.*;

import net.mineup.mercado.utils.geral.*;
import net.mineup.mercado.utils.itemhandler.*;

public class PersonalItem
{
    private ItemStack item;
    private String uuid;
    private String owner;
    private double price;
    private String buyer;
    
    public PersonalItem(ItemStack item, String owner, String buyer, double price, String uuid) {
        this.item = item;
        this.uuid = uuid;
        this.price = price;
        this.owner = owner;
        this.buyer = buyer;
    }
    
    public PersonalItem(ItemStack item, String owner, String buyer, double price) {
        this(item, owner, buyer, price, createUUID(item, owner, buyer));
    }
    
    @SuppressWarnings("deprecation")
	public static String createUUID(ItemStack item, String owner, String buyer) {
        return String.valueOf(item.getTypeId() + item.getData().getData()) + owner + buyer + UPMarket.getInstance().getSQLManager().getAmountPersonalItems(buyer);
    }
    
    public ItemStack putLore() {
        ItemStack is = this.item.clone();
        ItemMeta meta = is.getItemMeta();
        List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<String>();
        if (lore.isEmpty() || !lore.get(lore.size() - 1).startsWith(Text.colorize("&ePreço:"))) {
            lore.add("");
            lore.add(Text.colorize("&eVendedor: &7" + this.owner));
            lore.add(Text.colorize("&ePreço: &2$" + NumberFormat.getInstance().format(this.price)));
            meta.setLore(lore);
            is.setItemMeta(meta);
        }
        return is;
    }
    
    public ItemStack putLore2() {
        ItemStack is = this.item.clone();
        ItemMeta meta = is.getItemMeta();
        List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<String>();
        if (lore.isEmpty() || !lore.get(lore.size() - 1).startsWith(Text.colorize("&ePreço:"))) {
            lore.add("");
            lore.add(Text.colorize("&eVendedor: &7" + this.owner));
            lore.add(Text.colorize("&eComprador: &7" + this.buyer));
            lore.add(Text.colorize("&ePreço: &2$" + NumberFormat.getInstance().format(this.price)));
            lore.add("");
            lore.add(Text.colorize("&aClique para coletar."));
            meta.setLore(lore);
            is.setItemMeta(meta);
        }
        return is;
    }
    
    public ItemStack getItem() {
        return this.item;
    }
    
    public String getUUID() {
        return this.uuid;
    }
    
    public String getUUID(ItemStack item) {
        NBTItemStack packet = new NBTItemStack(item);
        return packet.getString("uuid");
    }
    
    public String getOwner() {
        return this.owner;
    }
    
    public double getPrice() {
        return this.price;
    }
    
    public String getBuyer() {
        return this.buyer;
    }
    
    public void setItem(ItemStack item) {
        this.item = item;
    }
    
    public void setUUID(String uuid) {
        this.uuid = uuid;
    }
    
    public void setOwner(String owner) {
        this.owner = owner;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }
}
