package nl.imine.model;

import java.util.Optional;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

public class ItemRequirement {

	private ItemType itemType;
	private Short itemData;
	private String itemName;

	public ItemRequirement() {
	}

	public ItemRequirement(ItemType itemType, Short itemData, String itemName) {
		this.itemType = itemType;
		this.itemData = itemData;
		this.itemName = itemName;
	}

	public boolean isMet(ItemStack item) {
		Short durability = null;
		String name = null;
		Optional<Integer> oDurability = item.get(Keys.ITEM_DURABILITY);
		if (oDurability.isPresent()) {
			durability = oDurability.get().shortValue();
		}
		Optional<Text> oName = item.get(Keys.DISPLAY_NAME);
		if (oName.isPresent()) {
			name = oName.get().toPlain();
		}
		return hasCorrectType(item.getItem()) && hasCorrectDataValue(durability) && hasCorrectName(name);
	}

	public boolean hasCorrectType(ItemType compare) {
		return itemType == null || this.itemType.equals(compare);
	}

	public boolean hasCorrectDataValue(Short compare) {
		return itemData == null || this.itemData.equals(compare);
	}

	public boolean hasCorrectName(String name) {
		return this.itemName == null || this.itemName.equals(name);
	}

	public ItemType getItemType() {
		return itemType;
	}

	public Short getItemData() {
		return itemData;
	}

	public String getItemName() {
		return itemName;
	}
}
