package net.mineup.mercado;

import org.bukkit.event.inventory.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;

import net.mineup.mercado.signgui.*;
import net.mineup.mercado.utils.geral.*;
import net.mineup.mercado.utils.itemhandler.*;

import org.bukkit.*;
import org.bukkit.event.player.*;
import org.bukkit.event.block.*;
import org.bukkit.event.*;
import org.bukkit.block.*;

public class Listeners implements Listener
{
    private UPMarket plugin;
    
    public Listeners(UPMarket plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler
    public void clickEvent(InventoryClickEvent event) {
        if (event.getInventory().getName().equalsIgnoreCase("Mercado - Categorias")) {
            if (event.getCurrentItem() == null || event.getCurrentItem().getItemMeta() == null || event.getCurrentItem().getType() == Material.AIR) {
                return;
            }
            event.setCancelled(true);
            Player player = (Player)event.getWhoClicked();
            if (event.getSlot() == 44) {
                player.openInventory(Menu.expireds(player));
            }
            else if (event.getSlot() == 36) {
                player.openInventory(Menu.personal(player));
            }
            else {
                Category category = this.plugin.getCategoryManager().getCategory(event.getSlot());
                if (category == null) {
                    return;
                }
                category.loadPages();
                category.open(player, 1);
            }
        }
        else if (event.getInventory().getName().equalsIgnoreCase("Mercado - Expirados")) {
            if (event.getCurrentItem() == null || event.getCurrentItem().getItemMeta() == null || event.getCurrentItem().getType() == Material.AIR) {
                return;
            }
            event.setCancelled(true);
            Player player = (Player)event.getWhoClicked();
            if (event.getSlot() == 18) {
                player.openInventory(Menu.inventory(player));
                return;
            }
            ItemStack current = event.getCurrentItem();
            NBTItemStack nbt = new NBTItemStack(current);
            String uuid = nbt.getString("uuid");
            MarketItem mi = this.plugin.getItensManager().getExpired(uuid);
            if (mi == null) {
                player.sendMessage(Text.colorize("&cEsse item não está mais na loja!"));
                return;
            }
            if (!Util.invCanHoldItem((Inventory)player.getInventory(), mi.getItem())) {
                player.sendMessage(this.plugin.getLangManager().getString("Errors.Inv_Full"));
                return;
            }
            player.getInventory().addItem(new ItemStack[] { mi.getItem() });
            player.sendMessage(this.plugin.getLangManager().getString("Messages.Expired_Collected"));
            player.closeInventory();
            this.plugin.getItensManager().removeExpired(uuid);
        }
        else if (event.getInventory().getName().matches("Mercado - (\\w|\\s)+ \\d*")) {
            if (event.getCurrentItem() == null || event.getCurrentItem().getItemMeta() == null || event.getCurrentItem().getType() == Material.AIR) {
                return;
            }
            event.setCancelled(true);
            Player player = (Player)event.getWhoClicked();
            ItemStack current = event.getCurrentItem();
            String c = Util.onlyWords(event.getInventory().getName()).replace("Mercado - ", "").trim();
            Category category2 = this.plugin.getCategoryManager().getCategory(c);
            if (event.getSlot() == 45) {
                if (current.getItemMeta().getDisplayName().contains("Voltar")) {
                    player.openInventory(Menu.inventory(player));
                }
                else {
                    category2.open(player, Integer.parseInt(Util.onlyNumbers(event.getInventory().getName())) - 1);
                }
            }
            else if (event.getSlot() == 53) {
                category2.open(player, Integer.parseInt(Util.onlyNumbers(event.getInventory().getName())) + 1);
            }
            else if (event.getSlot() == 49) {
                category2.updateInventory(event.getInventory());
            }
            else {
                NBTItemStack nbt2 = new NBTItemStack(current);
                MarketItem mi2 = this.plugin.getItensManager().getItem(nbt2.getString("uuid"));
                if (mi2 == null) {
                    player.sendMessage(Text.colorize("&cEsse item não está mais na loja!"));
                    event.getInventory().removeItem(new ItemStack[] { current });
                    player.closeInventory();
                    return;
                }
                if (player.getName().equals(mi2.getOwner())) {
                    player.sendMessage(this.plugin.getLangManager().getString("Messages.Item_Collected"));
                    this.plugin.getItensManager().addExpired(mi2);
                    this.plugin.getItensManager().removeItem(mi2.getUUID());
                    event.getInventory().removeItem(new ItemStack[] { current });
                    category2.updateInventory(event.getInventory());
                    player.openInventory(Menu.expireds(player));
                }
                else {
                    player.openInventory(Menu.confirm(mi2));
                }
            }
        }
        else if (event.getInventory().getName().equalsIgnoreCase("Mercado - Compra")) {
            if (event.getCurrentItem() == null || event.getCurrentItem().getItemMeta() == null || event.getCurrentItem().getType() == Material.AIR) {
                return;
            }
            event.setCancelled(true);
            Player player = (Player)event.getWhoClicked();
            ItemStack current = event.getInventory().getItem(13);
            NBTItemStack nbt = new NBTItemStack(current);
            MarketItem mi3 = this.plugin.getItensManager().getItem(nbt.getString("uuid"));
            if (event.getSlot() == 11) {
                player.closeInventory();
                if (mi3 == null) {
                    player.sendMessage(Text.colorize("&cEsse item não está mais na loja!"));
                    return;
                }
                if (this.plugin.getEcon().getBalance(player.getName()) < mi3.getPrice()) {
                    player.sendMessage(this.plugin.getLangManager().getString("Errors.Hasnt_Money"));
                    return;
                }
                if (!Util.invCanHoldItem((Inventory)player.getInventory(), mi3.getItem())) {
                    player.sendMessage(this.plugin.getLangManager().getString("Errors.Inv_Full"));
                    return;
                }
                player.getInventory().addItem(new ItemStack[] { mi3.getItem() });
                player.updateInventory();
                player.sendMessage(this.plugin.getLangManager().getString("Messages.Bought"));
                if (Bukkit.getPlayer(mi3.getOwner()) != null) {
                    Bukkit.getPlayer(mi3.getOwner()).sendMessage(this.plugin.getLangManager().getString("Messages.Selled").replace("@player", player.getName()));
                }
                this.plugin.getEcon().depositPlayer(mi3.getOwner(), mi3.getPrice());
                this.plugin.getEcon().withdrawPlayer(player.getName(), mi3.getPrice());
                event.getInventory().removeItem(new ItemStack[] { current });
                mi3.getCategory().updateInventory(event.getInventory());
                this.plugin.getItensManager().removeItem(mi3.getUUID());
                player.updateInventory();
            }
            else if (event.getSlot() == 15) {
                player.closeInventory();
            }
        }
        else if (event.getInventory().getName().equalsIgnoreCase("Mercado Pessoal - Compra")) {
            if (event.getCurrentItem() == null || event.getCurrentItem().getItemMeta() == null || event.getCurrentItem().getType() == Material.AIR) {
                return;
            }
            event.setCancelled(true);
            Player player = (Player)event.getWhoClicked();
            ItemStack current = event.getInventory().getItem(13);
            NBTItemStack nbt = new NBTItemStack(current);
            PersonalItem mi4 = this.plugin.getItensManager().getPersonal(nbt.getString("uuid"));
            if (event.getSlot() == 11) {
                player.closeInventory();
                if (mi4 == null) {
                    player.sendMessage(Text.colorize("&cEsse item não está mais na loja!"));
                    return;
                }
                if (this.plugin.getEcon().getBalance(player.getName()) < mi4.getPrice()) {
                    player.sendMessage(this.plugin.getLangManager().getString("Errors.Hasnt_Money"));
                    return;
                }
                if (!Util.invCanHoldItem((Inventory)player.getInventory(), mi4.getItem())) {
                    player.sendMessage(this.plugin.getLangManager().getString("Errors.Inv_Full"));
                    return;
                }
                player.getInventory().addItem(new ItemStack[] { mi4.getItem() });
                player.updateInventory();
                player.sendMessage(this.plugin.getLangManager().getString("Messages.Bought"));
                if (Bukkit.getPlayer(mi4.getOwner()) != null) {
                    Bukkit.getPlayer(mi4.getOwner()).sendMessage(this.plugin.getLangManager().getString("Messages.Selled").replace("@player", player.getName()));
                }
                this.plugin.getEcon().depositPlayer(mi4.getOwner(), mi4.getPrice());
                this.plugin.getEcon().withdrawPlayer(player.getName(), mi4.getPrice());
                event.getInventory().removeItem(new ItemStack[] { current });
                this.plugin.getItensManager().removePersonal(mi4.getUUID());
                player.updateInventory();
            }
            else if (event.getSlot() == 15) {
                player.closeInventory();
            }
        }
        else if (event.getInventory().getName().equalsIgnoreCase("Mercado - Pessoal")) {
            if (event.getCurrentItem() == null || event.getCurrentItem().getItemMeta() == null || event.getCurrentItem().getType() == Material.AIR) {
                return;
            }
            event.setCancelled(true);
            Player player = (Player)event.getWhoClicked();
            if (event.getSlot() == 27) {
                player.openInventory(Menu.inventory(player));
                return;
            }
            ItemStack current = event.getCurrentItem();
            NBTItemStack nbt = new NBTItemStack(current);
            PersonalItem mi4 = this.plugin.getItensManager().getPersonal(nbt.getString("uuid"));
            if (mi4 == null) {
                player.sendMessage("&cEsse item não está mais a venda!");
                event.getInventory().removeItem(new ItemStack[] { current });
                player.closeInventory();
                return;
            }
            if (player.getName().equals(mi4.getOwner())) {
                if (!Util.invCanHoldItem((Inventory)player.getInventory(), mi4.getItem())) {
                    player.sendMessage(this.plugin.getLangManager().getString("Errors.Inv_Full"));
                    return;
                }
                this.plugin.getItensManager().removePersonal(mi4.getUUID());
                player.getInventory().addItem(new ItemStack[] { mi4.getItem() });
                event.getInventory().removeItem(new ItemStack[] { current });
                player.updateInventory();
            }
            else {
                player.openInventory(Menu.confirm(mi4));
            }
        }
        else if (event.getInventory().getName().matches(Text.colorize("Mercado - [&c](\\w|\\s)*"))) {
            if (event.getCurrentItem() == null || event.getCurrentItem().getItemMeta() == null || event.getCurrentItem().getType() == Material.AIR) {
                return;
            }
            Player player = (Player)event.getWhoClicked();
            ItemStack current = event.getCurrentItem();
            if (current.getItemMeta().hasDisplayName() && current.getItemMeta().getDisplayName().equals(Text.colorize("&cNenhum item encontrado!"))) {
                player.closeInventory();
                return;
            }
            NBTItemStack nbt = new NBTItemStack(current);
            MarketItem mi3 = this.plugin.getItensManager().getItem(nbt.getString("uuid"));
            if (mi3 == null) {
                player.sendMessage(Text.colorize("&cEsse item não está mais na loja!"));
                event.getInventory().removeItem(new ItemStack[] { current });
                player.closeInventory();
                return;
            }
            Category category3 = mi3.getCategory();
            if (player.getName().equals(mi3.getOwner())) {
                int i = 0;
                for (MarketItem m : this.plugin.getItensManager().getExpireds().values()) {
                    if (m.getOwner().equalsIgnoreCase(player.getName())) {
                        ++i;
                    }
                }
                if (i > 25) {
                    player.sendMessage(this.plugin.getLangManager().getString("Errors.Expired_Full"));
                    return;
                }
                player.sendMessage(this.plugin.getLangManager().getString("Messages.Item_Collected"));
                this.plugin.getItensManager().addExpired(mi3);
                this.plugin.getItensManager().removeItem(mi3.getUUID());
                player.openInventory(Menu.expireds(player));
                event.getInventory().removeItem(new ItemStack[] { current });
                category3.updateInventory(event.getInventory());
                player.updateInventory();
            }
            else {
                player.openInventory(Menu.confirm(mi3));
            }
        }
    }
    
    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        if (event.getPlayer() == null) {
            return;
        }
        this.plugin.getSQLManager().refreshItem(event.getPlayer().getName());
        this.plugin.getSQLManager().refreshExpired(event.getPlayer().getName());
    }
    
    @EventHandler
    public void signChange(SignChangeEvent event) {
        Block b = event.getBlock();
        if (b.hasMetadata("signgui")) {
            Bukkit.getPluginManager().callEvent((Event)new SignGUICloseEvent(event.getPlayer(), event.getLines()));
            b.setType(Material.AIR);
        }
    }
    
    @EventHandler
    public void signUpdate(SignGUICloseEvent event) {
        event.getPlayer().openInventory(Menu.search(event.getSignText()[0]));
    }
}
