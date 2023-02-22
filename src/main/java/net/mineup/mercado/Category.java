package net.mineup.mercado;

import org.bukkit.*;
import org.bukkit.inventory.*;

import net.mineup.mercado.utils.itemhandler.*;

import org.bukkit.entity.*;

import java.util.*;

public class Category {
	private String name;
	private ItemStack icon;
	private boolean glow;
	private int slot;
	private List<Material> materials;
	private Map<Integer, Inventory> pages;

	public Category(String name, ItemStack icon, boolean glow, int slot,
			List<Material> materials) {
		this.glow = false;
		this.pages = new HashMap<Integer, Inventory>();
		this.name = name;
		this.icon = icon;
		this.slot = slot;
		this.materials = materials;
		this.glow = glow;
	}

	public void loadPages() {
		Inventory inv = Bukkit.createInventory((InventoryHolder) null, 54, "Mercado - " + this.name + " 1");
		inv.setItem(45, new ItemBuilder(Material.ARROW).name("&aVoltar")
				.lore("&7Clique aqui para voltar", "&7para o menu do Mercado.").build());
		int page = 1;
		this.pages.put(1, inv);
		inv.setItem(49, new ItemBuilder(this.icon).name("&6Atualizar")
				.lore("&7Clique aqui para atualizar", "&7os itens disponíveis.").removeAttributes().build());
		for (MarketItem m : UPMarket.getInstance().getItensManager().getItems().values()) {
			if (m == null) {
				continue;
			}
			if (m.getCategory() == null) {
				continue;
			}
			if (!m.getCategory().getName().equalsIgnoreCase(this.name)) {
				continue;
			}
			ItemStack item = m.insertLore();
			NBTItemStack nbt = new NBTItemStack(item);
			if (!nbt.hasKey("uuid")) {
				nbt.setString("uuid", m.getUUID());
				item = nbt.getItem();
			}
			if (inv.firstEmpty() + 1 == 54) {
				inv.setItem(53, new ItemBuilder(Material.ARROW).name("&aPágina " + (page + 1))
						.lore("&7Clique aqui para ir", "&7para a próxima página.").build());
				++page;
				inv = Bukkit.createInventory((InventoryHolder) null, 54, "Mercado - " + this.name + " " + page);
				inv.addItem(new ItemStack[] { item });
				if (page > 1) {
					inv.setItem(45, new ItemBuilder(Material.ARROW).name("&cPágina " + (page - 1))
							.lore("&7Clique aqui para ir", "&7para a página anterior.").build());
				}
				this.pages.put(page, inv);
			} else {
				inv.addItem(new ItemStack[] { item });
			}
		}
	}

	public void open(Player player, int page) {
		if (this.pages.containsKey(page)) {
			player.openInventory((Inventory) this.pages.get(page));
		} else {
			player.openInventory((Inventory) this.pages.get(1));
		}
	}

	public void updateInventory(Inventory inv) {
		this.loadPages();
		if (inv == null) {
			return;
		}
		if (inv.getViewers() == null || inv.getViewers().isEmpty()) {
			return;
		}
		for (HumanEntity he : inv.getViewers()) {
			Player p = (Player) he;
			p.updateInventory();
		}
	}

	public String getName() {
		return this.name;
	}

	public ItemStack getIcon() {
		if (this.icon == null) {
			return new ItemBuilder(Material.WOOL).durability(14).name("&cErro!")
					.lore("&7Essa categoria está", "&7com algum erro.").build();
		}
		int amount = UPMarket.getInstance().getItensManager().getItems(this);
		return new ItemBuilder(this.icon).amount(amount).lore("", "&fItens disponíveis: &7" + amount)
				.glow(this.glow).removeAttributes().build();
	}

	public String getStringIcon() {
		return ItemHandler.iconString(this.icon);
	}

	public int getSlot() {
		return this.slot;
	}

	public List<Material> getMaterials() {
		return this.materials;
	}

	@SuppressWarnings("deprecation")
	public List<String> getMaterialsString() {
		List<String> output = new ArrayList<String>();
		for (Material m : this.materials) {
			output.add(Integer.toString(m.getId()));
		}
		return output;
	}

	public Map<Integer, Inventory> getPages() {
		return this.pages;
	}

	public int getLastPage() {
		return this.pages.size();
	}

	public boolean isGlow() {
		return this.glow;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setIcon(ItemStack icon) {
		this.icon = icon;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

	public void setMaterials(List<Material> materials) {
		this.materials = materials;
	}

	public void setGlow(boolean glow) {
		this.glow = glow;
	}
}
