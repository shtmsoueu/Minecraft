package net.mineup.mercado;

import org.bukkit.entity.*;
import org.bukkit.*;
import org.bukkit.inventory.*;

import net.mineup.mercado.utils.geral.*;
import net.mineup.mercado.utils.itemhandler.*;

public class Menu
{
    public static Inventory inventory(Player player) {
        Inventory output = Bukkit.createInventory(null, 45, "Mercado - Categorias");
        for (Category c : UPMarket.getInstance().getCategoryManager().getCategories().values()) {
            output.setItem(c.getSlot(), c.getIcon());
        }
        
        int amount = UPMarket.getInstance().getItensManager().personalAmount(player.getName());
        output.setItem(44, new ItemBuilder(Material.CHEST).name("&eMeus Itens").lore("&7Clique aqui para ver", "&7os seus itens expirados.").build());
        output.setItem(36, new ItemBuilder(Material.SKULL_ITEM).durability(3).amount(amount).owner(player.getName()).name("&6Mercado Pessoal").lore("&7Estes itens foram colocados", "&7à venda apenas para você", "&7por outros jogadores.", "", "&fItens disponíveis: &7" + amount).build());
        return output;
    }
    
    public static Inventory expireds(Player player) {
        Inventory output = Bukkit.createInventory(null, 27, "Mercado - Expirados");
        output.setItem(18, new ItemBuilder(Material.ARROW).name("&aVoltar").lore("&7Clique aqui para fechar o Mercado.").build());
        for (MarketItem mi : UPMarket.getInstance().getItensManager().getExpireds().values()) {
            if (mi.getOwner().equals(player.getName()) && output.firstEmpty() != -1) {
                ItemStack item = mi.insertLore();
                NBTItemStack nbt = new NBTItemStack(item);
                nbt.setString("uuid", mi.getUUID());
                item = nbt.getItem();
                output.addItem(new ItemStack[] { item });
            }
        }
        return output;
    }
    
    public static Inventory personal(Player player) {
        Inventory output = Bukkit.createInventory(null, 36, "Mercado - Pessoal");
        output.setItem(27, new ItemBuilder(Material.ARROW).name("&aVoltar").lore("&7Clique aqui para fechar o Mercado.").build());
        for (PersonalItem mi : UPMarket.getInstance().getItensManager().getPersonal().values()) {
            if (mi.getBuyer().equalsIgnoreCase(player.getName())) {
                ItemStack item = mi.putLore();
                NBTItemStack nbt = new NBTItemStack(item);
                nbt.setString("uuid", mi.getUUID());
                item = nbt.getItem();
                output.addItem(new ItemStack[] { item });
            }
            else {
                if (!mi.getOwner().equalsIgnoreCase(player.getName())) {
                    continue;
                }
                ItemStack item = mi.putLore2();
                NBTItemStack nbt = new NBTItemStack(item);
                nbt.setString("uuid", mi.getUUID());
                item = nbt.getItem();
                output.addItem(new ItemStack[] { item });
            }
        }
        return output;
    }
    
    public static Inventory confirm(MarketItem item) {
        Inventory output = Bukkit.createInventory(null, 27, "Mercado - Compra");
        output.setItem(11, new ItemBuilder(Material.WOOL).durability(5).name("&aAceitar").lore("&7Clique aqui para confirmar", "&7a compra do item.").build());
        output.setItem(15, new ItemBuilder(Material.WOOL).durability(14).name("&cRecusar").build());
        ItemStack is = item.getItem();
        NBTItemStack nbt = new NBTItemStack(is);
        nbt.setString("uuid", item.getUUID());
        is = nbt.getItem();
        output.setItem(13, is);
        return output;
    }
    
    public static Inventory confirm(PersonalItem item) {
        Inventory output = Bukkit.createInventory(null, 27, "Mercado Pessoal - Compra");
        output.setItem(11, new ItemBuilder(Material.WOOL).durability(5).name("&aAceitar").lore("&7Clique aqui para confirmar", "&7a compra do item.").build());
        output.setItem(15, new ItemBuilder(Material.WOOL).durability(14).name("&cRecusar").build());
        ItemStack is = item.getItem();
        NBTItemStack nbt = new NBTItemStack(is);
        nbt.setString("uuid", item.getUUID());
        is = nbt.getItem();
        output.setItem(13, is);
        return output;
    }
    
    public static Inventory search(String search) {
        Inventory output = Bukkit.createInventory(null, 45, String.valueOf(Text.colorize("Mercado - &c")) + search);
        if (!UPMarket.getInstance().getItensManager().searchItem(search).isEmpty()) {
            int i = 0;
            for (MarketItem mi : UPMarket.getInstance().getItensManager().searchItem(search)) {
                ItemStack item = mi.insertLore();
                if (output.getItem(i) == null || output.getItem(i).getType() == Material.AIR) {
                    NBTItemStack nbt = new NBTItemStack(item);
                    if (!nbt.hasKey("uuid")) {
                        nbt.setString("uuid", mi.getUUID());
                        item = nbt.getItem();
                    }
                    if (output.firstEmpty() + 1 == 45) {
                        break;
                    }
                    output.addItem(new ItemStack[] { item });
                }
                ++i;
            }
        }
        else {
            output.setItem(22, new ItemBuilder(Material.WOOL).durability(14).name("&cNenhum item encontrado!").build());
        }
        return output;
    }
}
