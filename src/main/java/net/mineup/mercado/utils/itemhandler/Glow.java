package net.mineup.mercado.utils.itemhandler;

import org.bukkit.inventory.*;
import org.bukkit.enchantments.*;
import java.lang.reflect.*;

public class Glow extends EnchantmentWrapper
{
    private static Enchantment glow;
    
    public Glow(final int id) {
        super(id);
    }
    
    public boolean canEnchantItem(final ItemStack item) {
        return true;
    }
    
    public boolean conflictsWith(final Enchantment other) {
        return false;
    }
    
    public EnchantmentTarget getItemTarget() {
        return null;
    }
    
    public int getMaxLevel() {
        return 10;
    }
    
    public String getName() {
        return "Glow";
    }
    
    public int getStartLevel() {
        return 1;
    }
    
    public static Enchantment getGlow() {
        if (Glow.glow != null) {
            return Glow.glow;
        }
        if (!Enchantment.isAcceptingRegistrations()) {
            try {
                final Field f = Enchantment.class.getDeclaredField("acceptingNew");
                f.setAccessible(true);
                f.set(null, true);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        Glow.glow = (Enchantment)new Glow(100);
        if (Enchantment.getByName("glll") == null) {
            Enchantment.registerEnchantment(Glow.glow);
        }
        return Glow.glow;
    }
    
    public static void addGlow(final ItemStack item) {
        final Enchantment glow = getGlow();
        item.addEnchantment(glow, 1);
    }
}
