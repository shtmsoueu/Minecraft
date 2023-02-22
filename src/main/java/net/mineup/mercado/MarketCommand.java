package net.mineup.mercado;

import java.lang.reflect.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.*;

import java.text.*;
import net.md_5.bungee.api.chat.*;
import net.mineup.mercado.utils.geral.*;

import java.util.concurrent.*;
import org.bukkit.inventory.*;
import java.util.*;
import org.bukkit.permissions.*;

public class MarketCommand extends Command
{
    private UPMarket plugin;
    private Map<String, Long> delay;
    
    public MarketCommand(UPMarket plugin) {
        super("mercado");
        this.plugin = plugin;
        this.register();
        this.delay = new HashMap<String, Long>();
    }
    
    private void register() {
        try {
            Field cmap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            cmap.setAccessible(true);
            CommandMap map = (CommandMap)cmap.get(Bukkit.getServer());
            map.register(this.getName(), (Command)this);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public boolean execute(CommandSender sender, String arg1, String[] args) {
        Player player = null;
        boolean isPlayer = false;
        if (sender instanceof Player) {
            player = (Player)sender;
            isPlayer = true;
        }
        if (args.length == 0) {
            for (String string : this.plugin.getLangManager().getStringList("Messages.Help")) {
                sender.sendMessage(string);
            }
        }
        else if (args[0].equalsIgnoreCase("ver")) {
            if (!isPlayer) {
                return false;
            }
            if (args.length == 1) {
                player.openInventory(Menu.inventory(player));
                player.playSound(player.getLocation(), Sound.NOTE_BASS_DRUM, 10, 1);
            }
            else if (args.length == 2) {
                String c = args[1];
                Category category = this.plugin.getCategoryManager().getCategory(c);
                if (category == null) {
                    player.sendMessage(Text.colorize("&cEssa categoria não existe!"));
                    return false;
                }
                category.loadPages();
                category.open(player, 1);
            }
        }
        else if (args[0].equalsIgnoreCase("coletar")) {
            if (!isPlayer) {
                return false;
            }
            player.openInventory(Menu.expireds(player));
        }
        else if (args[0].equalsIgnoreCase("vender")) {
            if (!isPlayer) {
                return false;
            }
            if (args.length == 2) {
                if (!player.hasPermission("mercado.use")) {
                    player.sendMessage(this.plugin.getLangManager().getString("Errors.No_Perm"));
                    return false;
                }
                if (player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR) {
                    player.sendMessage(this.plugin.getLangManager().getString("Errors.Hold_Item"));
                    return false;
                }
                ItemStack item = player.getItemInHand();
                if (!Util.isNum(args[1])) {
                    player.sendMessage(this.plugin.getLangManager().getString("Errors.Only_Numbers"));
                    return false;
                }
                double price = Double.parseDouble(args[1]);
                if (price <= 0.0) {
                    player.sendMessage(this.plugin.getLangManager().getString("Errors.Only_Numbers"));
                    return false;
                }
                if (price > this.plugin.getConfig().getDouble("Config.Preco_Max")) {
                    player.sendMessage(this.plugin.getLangManager().getString("Errors.Expansive").replace("@preco", NumberFormat.getInstance().format(this.plugin.getConfig().getDouble("Config.Preco_Max"))));
                    return false;
                }
                boolean hasCategory = false;
                Category category2 = null;
                for (Category c2 : this.plugin.getCategoryManager().getCategories().values()) {
                    if (c2.getMaterials().contains(item.getType())) {
                        hasCategory = true;
                        category2 = c2;
                    }
                }
                if (!hasCategory) {
                    player.sendMessage(this.plugin.getLangManager().getString("Errors.Not_Registered"));
                    return false;
                }
                if (!this.hasPermission(player, this.plugin.getSQLManager().getAmountPlayerItems(player.getName()))) {
                    player.sendMessage(this.plugin.getLangManager().getString("Errors.Much_Itens").replace("@qntd", Integer.toString(this.getPermission(player))));
                    return false;
                }
                if (!this.delay.containsKey(player.getName().toLowerCase())) {
                    this.delay.put(player.getName().toLowerCase(), System.currentTimeMillis());
                    if (player.hasPermission("mercado.anunciar")) {
                        TextComponent text = new TextComponent(this.plugin.getLangManager().getString("Messages.Broadcast").replace("@prefix", Util.getColorPrefix(player.getName())).replace("@player", player.getName()).replace("@preco", NumberFormat.getInstance().format(price)));
                        BaseComponent[] json = new ComponentBuilder(this.plugin.getLangManager().getString("Messages.Broadcast_JSON").replace("|", "\n")).create();
                        text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mercado ver " + category2.getName()));
                        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, json));
                        Bukkit.getServer().spigot().broadcast((BaseComponent)text);
                    }
                }
                else if (TimeUnit.MILLISECONDS.toSeconds(Util.calculate(this.delay.get(player.getName().toLowerCase()))) >= this.plugin.getConfig().getInt("Config.Delay_Broadcast")) {
                    this.delay.remove(player.getName().toLowerCase());
                    if (player.hasPermission("mercado.anunciar")) {
                        TextComponent text = new TextComponent(this.plugin.getLangManager().getString("Messages.Broadcast").replace("@prefix", Util.getColorPrefix(player.getName())).replace("@player", player.getName()).replace("@preco", NumberFormat.getInstance().format(price)));
                        BaseComponent[] json = new ComponentBuilder(this.plugin.getLangManager().getString("Messages.Broadcast_JSON").replace("|", "\n")).create();
                        text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mercado ver " + category2.getName()));
                        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, json));
                        Bukkit.getServer().spigot().broadcast((BaseComponent)text);
                    }
                }
                this.plugin.getItensManager().addItem(new MarketItem(player.getItemInHand(), category2, player.getName(), price));
                player.sendMessage(this.plugin.getLangManager().getString("Messages.Item_Placed"));
                player.setItemInHand(new ItemStack(Material.AIR));
                player.updateInventory();
                category2.updateInventory(category2.getPages().get(category2.getPages().size()));
            }
            else if (args.length == 3) {
                if (!player.hasPermission("mercado.use")) {
                    player.sendMessage(this.plugin.getLangManager().getString("Errors.No_Perm"));
                    return false;
                }
                if (player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR) {
                    player.sendMessage(this.plugin.getLangManager().getString("Errors.Hold_Item"));
                    return false;
                }
                ItemStack item = player.getItemInHand();
                if (!Util.isNum(args[1])) {
                    player.sendMessage(this.plugin.getLangManager().getString("Errors.Only_Numbers"));
                    return false;
                }
                double price = Double.parseDouble(args[1]);
                if (price <= 0.0) {
                    player.sendMessage(this.plugin.getLangManager().getString("Errors.Only_Numbers"));
                    return false;
                }
                if (price > this.plugin.getConfig().getDouble("Config.Preco_Max")) {
                    player.sendMessage(this.plugin.getLangManager().getString("Errors.Expansive").replace("@preco", NumberFormat.getInstance().format(this.plugin.getConfig().getDouble("Config.Preco_Max"))));
                    return false;
                }
                String buyer = args[2];
                int i = 0;
                for (PersonalItem m : this.plugin.getItensManager().getPersonal().values()) {
                    if (m.getBuyer().equalsIgnoreCase(buyer)) {
                        ++i;
                    }
                }
                if (i > 34) {
                    player.sendMessage(this.plugin.getLangManager().getString("Errors.Personal_Full").replace("@player", buyer));
                    return false;
                }
                if (player.getName().equalsIgnoreCase(buyer)) {
                    player.sendMessage(this.plugin.getLangManager().getString("Errors.You_Cant_Sent"));
                    return false;
                }
                if (Bukkit.getPlayer(buyer) == null) {
                    player.sendMessage(this.plugin.getLangManager().getString("Errors.Player_Offline"));
                    return false;
                }
                this.plugin.getItensManager().addPersonal(new PersonalItem(item, player.getName(), buyer, price));
                player.sendMessage(this.plugin.getLangManager().getString("Messages.Item_Sent").replace("@player", buyer).replace("@preco", NumberFormat.getInstance().format(price)));
                player.setItemInHand(new ItemStack(Material.AIR));
                player.updateInventory();
            }
            else {
                player.sendMessage(this.plugin.getLangManager().getString("Errors.Vender_Format"));
            }
        }
        else {
            for (String string : this.plugin.getLangManager().getStringList("Messages.Help")) {
                player.sendMessage(string);
            }
        }
        return false;
    }
    
    private boolean hasPermission(Player p, int valor) {
        return valor <= this.getPermission(p);
    }
    
    private int getPermission(Player p) {
        if (p.isOp() || p.hasPermission("*")) {
            return Integer.MAX_VALUE;
        }
        for (PermissionAttachmentInfo perm : p.getEffectivePermissions()) {
            if (perm.getPermission().toLowerCase().matches("mercado.\\d+")) {
                return Integer.parseInt(perm.getPermission().split("\\.")[1]);
            }
        }
        return 0;
    }
}
