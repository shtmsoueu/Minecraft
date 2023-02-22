package net.mineup.mercado.utils.itemhandler;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import net.mineup.mercado.utils.geral.Text;

public class ItemBuilder
{
    private ItemStack is;
    
    public ItemBuilder(Material m) {
        this(m, 1);
    }
    
    public ItemBuilder(ItemStack is) {
        this.is = is.clone();
    }
    
    public ItemBuilder(Material m, int amount) {
        this.is = new ItemStack(m, amount);
    }
    
    public ItemBuilder clone() {
        return new ItemBuilder(this.is);
    }
    
    public ItemBuilder durability(int dur) {
        this.is.setDurability((short)dur);
        return this;
    }
    
    public ItemBuilder name(String name) {
        ItemMeta im = this.is.getItemMeta();
        im.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        this.is.setItemMeta(im);
        return this;
    }
    
    public ItemBuilder unsafeEnchantment(Enchantment ench, int level) {
        this.is.addUnsafeEnchantment(ench, level);
        return this;
    }
    
    public ItemBuilder enchant(Enchantment ench, int level) {
        ItemMeta im = this.is.getItemMeta();
        im.addEnchant(ench, level, true);
        this.is.setItemMeta(im);
        return this;
    }
    
    public ItemBuilder removeEnchantment(Enchantment ench) {
        this.is.removeEnchantment(ench);
        return this;
    }
    
    public ItemBuilder owner(String owner) {
        try {
            SkullMeta im = (SkullMeta)this.is.getItemMeta();
            im.setOwner(owner);
            this.is.setItemMeta((ItemMeta)im);
        }
        catch (ClassCastException ex) {}
        return this;
    }
    
    public ItemBuilder infinityDurabilty() {
        this.is.setDurability((short)32767);
        return this;
    }
    
    public ItemBuilder lore(String... lore) {
        ItemMeta im = this.is.getItemMeta();
        List<String> out = (im.getLore() == null) ? new ArrayList<String>() : im.getLore();
        for (String string : lore) {
            out.add(Text.colorize(string));
        }
        im.setLore(out);
        this.is.setItemMeta(im);
        return this;
    }
    
    @SuppressWarnings("deprecation")
	public ItemBuilder woolColor(DyeColor color) {
        if (!this.is.getType().equals((Object)Material.WOOL)) {
            return this;
        }
        this.is.setDurability((short)color.getData());
        return this;
    }
    
    public ItemBuilder amount(int amount) {
        if (amount > 64) {
            amount = 64;
        }
        this.is.setAmount(amount);
        return this;
    }
    
    public ItemBuilder glow(boolean glow) {
        if (glow) {
            this.is.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
            this.removeAttributes();
        }
        return this;
    }
    
    public ItemBuilder removeAttributes() {
        ItemMeta meta = this.is.getItemMeta();
        meta.addItemFlags(ItemFlag.values());
        this.is.setItemMeta(meta);
        return this;
    }
    
    public ItemStack build() {
        return this.is;
    }
}
