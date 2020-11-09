package com.hexdragon.core.item;

import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;

// 在触发 markDirty 时触发 onCraftMatrixChanged 的 Inventory
public class CraftInputInventory extends Inventory {

    public CraftInputInventory(int numSlots) {
        super(numSlots);
    }
    public CraftInputInventory(ItemStack... stacksIn) {
        super(stacksIn);
    }

    public Container container = null;
    public void markDirty() {
        super.markDirty();
        if (container != null) container.onCraftMatrixChanged(this);
    }

}
