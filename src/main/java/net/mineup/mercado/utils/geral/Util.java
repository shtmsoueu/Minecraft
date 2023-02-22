package net.mineup.mercado.utils.geral;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.inventory.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.entity.Player;

public class Util {

	public static String getColorPrefix(String prefix) {
		Player player = Bukkit.getPlayer(prefix);
		User user = LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId());
		prefix = user.getCachedData().getMetaData(LuckPermsProvider.get().getContextManager().getQueryOptions(player)).getPrefix();
		return prefix.isEmpty() ? "§7" : prefix.replace("&", "§");
	}

	public static String onlyNumbers(String str) {
		if (str != null) {
			return str.replaceAll("[^0123456789]", "");
		}
		return "";
	}

	public static String onlyWords(String input) {
		if (input != null) {
			return input.replaceAll("\\d+", "");
		}
		return "";
	}

	public static boolean isNum(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static boolean invCanHoldItem(Inventory inv, ItemStack is) {
		Iterator<?> iterator = inv.iterator();
		if (iterator.hasNext()) {
			ItemStack itemStack = (ItemStack) iterator.next();
			return itemStack == null || itemStack.getType() != is.getType()
					|| itemStack.getAmount() > is.getMaxStackSize() - is.getAmount() || true;
		}
		return false;
	}

	public static long calculate(long delay) {
		return System.currentTimeMillis() - delay;
	}

	public static boolean materialExists(String m) {
		return Material.getMaterial(m.toUpperCase()) != null;
	}
}
