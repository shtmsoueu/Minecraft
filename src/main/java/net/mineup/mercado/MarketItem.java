package net.mineup.mercado;

import org.bukkit.inventory.*;

import net.mineup.mercado.utils.geral.*;
import net.mineup.mercado.utils.itemhandler.*;

import java.text.*;

public class MarketItem
{
    private ItemStack item;
    private String uuid;
    private Category category;
    private String owner;
    private double price;
    
    public MarketItem(ItemStack item, Category category, String owner, double price, String uuid) {
        this.item = item;
        this.uuid = uuid;
        this.category = category;
        this.price = price;
        this.owner = owner;
    }
    
    public MarketItem(ItemStack item, Category category, String owner, double price) {
        this(item, category, owner, price, createUUID(item, category, owner));
    }
    
    @SuppressWarnings("deprecation")
	public static String createUUID(ItemStack item, Category category, String owner) {
        return String.valueOf(category.getName().charAt(0) + item.getTypeId() + item.getData().getData()) + owner + UPMarket.getInstance().getSQLManager().getAmountPlayerItems(owner);
    }
    
    @Override
    public String toString() {
        return "MarketItem [item=" + this.item + ", uuid=" + this.uuid + ", category=" + this.category + ", owner=" + this.owner + ", price=" + this.price + "]";
    }
    
    public ItemStack insertLore() {
        return new ItemBuilder(this.item).lore("", "&7Vendedor: " + Util.getColorPrefix(this.owner) + this.owner, "&7Custo: &e" + NumberFormat.getInstance().format(this.price) + " coins", "").build();
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
    
    public Category getCategory() {
        return this.category;
    }
    
    public String getOwner() {
        return this.owner;
    }
    
    public double getPrice() {
        return this.price;
    }
    
    public void setItem(ItemStack item) {
        this.item = item;
    }
    
    public void setUUID(String uuid) {
        this.uuid = uuid;
    }
    
    public void setCategory(Category category) {
        this.category = category;
    }
    
    public void setOwner(String owner) {
        this.owner = owner;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
}
