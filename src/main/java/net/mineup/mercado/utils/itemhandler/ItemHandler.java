package net.mineup.mercado.utils.itemhandler;

import org.bukkit.inventory.*;
import java.math.*;
import java.io.*;

import org.bukkit.*;
import java.util.*;
import org.bukkit.inventory.meta.*;

import net.mineup.mercado.utils.geral.*;

public final class ItemHandler
{
    public static String toString(final ItemStack itemStack) {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final DataOutputStream dataOutput = new DataOutputStream(outputStream);
        try {
            final Object nbtTagListItems = Reflection.getNMSClass("NBTTagList").newInstance();
            final Object nbtTagCompoundItem = Reflection.getNMSClass("NBTTagCompound").newInstance();
            final Object nms = Reflection.getMethod(Reflection.getOBCClass("inventory.CraftItemStack"), "asNMSCopy", ItemStack.class).invoke(null, itemStack);
            Reflection.getMethod(Reflection.getNMSClass("ItemStack"), "save", Reflection.getNMSClass("NBTTagCompound")).invoke(nms, nbtTagCompoundItem);
            Reflection.getMethod(Reflection.getNMSClass("NBTTagList"), "add", Reflection.getNMSClass("NBTBase")).invoke(nbtTagListItems, nbtTagCompoundItem);
            Reflection.getMethod(Reflection.getNMSClass("NBTCompressedStreamTools"), "a", Reflection.getNMSClass("NBTTagCompound"), DataOutput.class).invoke(null, nbtTagCompoundItem, dataOutput);
        }
        catch (Throwable ex) {
            ex.printStackTrace();
        }
        return new BigInteger(1, outputStream.toByteArray()).toString(32);
    }
    
    public static ItemStack fromString(final String data) {
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(new BigInteger(data, 32).toByteArray());
        Object nmsItem = null;
        Object toReturn = null;
        try {
            final Object nbtTagCompoundRoot = Reflection.getMethod(Reflection.getNMSClass("NBTCompressedStreamTools"), "a", DataInputStream.class).invoke(null, new DataInputStream(inputStream));
            if (nbtTagCompoundRoot != null) {
                nmsItem = Reflection.getMethod(Reflection.getNMSClass("ItemStack"), "createStack", Reflection.getNMSClass("NBTTagCompound")).invoke(null, nbtTagCompoundRoot);
            }
            toReturn = Reflection.getMethod(Reflection.getOBCClass("inventory.CraftItemStack"), "asBukkitCopy", Reflection.getNMSClass("ItemStack")).invoke(null, nmsItem);
        }
        catch (Throwable ex) {
            ex.printStackTrace();
        }
        return (ItemStack)toReturn;
    }
    
    @SuppressWarnings("deprecation")
	public static String iconString(final ItemStack input) {
        final String id = Integer.toString(input.getTypeId());
        final String data = Byte.toString(input.getData().getData());
        final String name = input.getItemMeta().getDisplayName();
        String lore = "";
        if (input.getItemMeta().hasLore()) {
            for (final String s : input.getItemMeta().getLore()) {
                lore = String.valueOf(lore) + s + "@";
            }
        }
        return String.valueOf(id) + ":" + data + "|" + name + "|" + lore;
    }
    
    @SuppressWarnings("deprecation")
	public static ItemStack iconItemStack(final String input) {
        final String[] split = input.split(";");
        final int material = Integer.parseInt(split[0].split(":")[0]);
        final byte data = Byte.parseByte(split[0].split(":")[1]);
        final String name = Text.colorize(split[1]);
        final ItemStack item = new ItemStack(Material.getMaterial(material), 1, (short)data);
        if (item.getType() == Material.SKULL_ITEM) {
            final SkullMeta meta = (SkullMeta)item.getItemMeta();
            if (new Random().nextInt(1) == 0) {
                meta.setOwner("yiatzz");
            }
            else {
                meta.setOwner("T3rrors");
            }
            meta.setDisplayName(name);
            if (split[2] != null) {
                final List<String> lore = new ArrayList<String>();
                for (int i = 0; i < split[2].split("@").length; ++i) {
                    lore.add(Text.colorize(split[2].split("@")[i]));
                }
                meta.setLore(lore);
            }
            item.setItemMeta((ItemMeta)meta);
        }
        else {
            final ItemMeta meta2 = item.getItemMeta();
            meta2.setDisplayName(name);
            if (split[2] != null) {
                final List<String> lore = new ArrayList<String>();
                for (int i = 0; i < split[2].split("@").length; ++i) {
                    lore.add(Text.colorize(split[2].split("@")[i]));
                }
                meta2.setLore(lore);
            }
            item.setItemMeta(meta2);
        }
        return item;
    }
    
    @SuppressWarnings("deprecation")
	public static Material materialFromString(final String input) {
        if (input.contains(":")) {
            return Material.getMaterial(Integer.parseInt(input.split(":")[0]));
        }
        return Material.getMaterial(Integer.parseInt(input));
    }
}
