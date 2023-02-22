package net.mineup.mercado.signgui;

import org.bukkit.entity.*;
import org.bukkit.*;
import org.bukkit.plugin.*;
import org.bukkit.metadata.*;
import org.bukkit.block.*;
import org.bukkit.scheduler.*;

import net.mineup.mercado.*;

public class SignGUI
{
    private void openSign(Player p, Block b) {
        try {
            Object world = b.getWorld().getClass().getMethod("getHandle", (Class<?>[])new Class[0]).invoke(b.getWorld(), new Object[0]);
            Object blockPos = getNMSClass("BlockPosition").getConstructor(Integer.TYPE, Integer.TYPE, Integer.TYPE).newInstance(b.getX(), b.getY(), b.getZ());
            Object sign = world.getClass().getMethod("getTileEntity", getNMSClass("BlockPosition")).invoke(world, blockPos);
            Object player = p.getClass().getMethod("getHandle", (Class<?>[])new Class[0]).invoke(p, new Object[0]);
            player.getClass().getMethod("openSign", getNMSClass("TileEntitySign")).invoke(player, sign);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    static Class<?> getNMSClass(String nmsClassString) throws ClassNotFoundException {
        if (nmsClassString.equals("ChatSerializer") && !UPMarket.NMS_VERSION.equals("v1_8_R1")) {
            nmsClassString = "IChatBaseComponent$ChatSerializer";
        }
        return Class.forName("net.minecraft.server." + UPMarket.NMS_VERSION + "." + nmsClassString);
    }
    
    @SuppressWarnings("deprecation")
	public void openSignGUI(Player p, String[] text) {
        Location l = p.getLocation();
        Location ls = new Location(p.getWorld(), l.getX(), 255.0, l.getZ());
        ls.getBlock().setType(Material.SIGN_POST);
        ls.getBlock().setData((byte)0);
        ls.getBlock().setMetadata("signgui", (MetadataValue)new FixedMetadataValue((Plugin)UPMarket.getInstance(), (Object)"a"));
        Sign s = (Sign)ls.getBlock().getState();
        s.setLine(0, text[0]);
        s.setLine(1, text[1]);
        s.setLine(2, text[2]);
        s.setLine(3, text[3]);
        s.update(true, false);
        new BukkitRunnable() {
            public void run() {
                SignGUI.this.openSign(p, ls.getBlock());
            }
        }.runTaskLater((Plugin)UPMarket.getInstance(), 10L);
    }
}
