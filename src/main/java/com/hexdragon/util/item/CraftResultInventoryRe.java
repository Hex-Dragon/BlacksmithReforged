package com.hexdragon.util.item;

import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

// 支持多个物品栏的 CraftResultInventory
public class CraftResultInventoryRe extends CraftResultInventory {

    // 构造函数
    private final int slotsCount;
    private final NonNullList<ItemStack> inventoryContents;
    public CraftResultInventoryRe(int numSlots) {
        this.slotsCount = numSlots;
        this.inventoryContents = NonNullList.withSize(numSlots, ItemStack.EMPTY);
    }
    public CraftResultInventoryRe(ItemStack... stacksIn) {
        this.slotsCount = stacksIn.length;
        this.inventoryContents = NonNullList.from(ItemStack.EMPTY, stacksIn);
    }

    // 对物品格子的各种操作
    public boolean isEmpty() {
        for (ItemStack itemstack : this.inventoryContents) {
            if (!itemstack.isEmpty()) return false;
        }
        return true;
    }
    public int getSizeInventory() {return slotsCount;}
    public void clear() {this.inventoryContents.clear();}
    public ItemStack getStackInSlot(int index) {return this.inventoryContents.get(index);}
    public void setInventorySlotContents(int index, ItemStack stack) {this.inventoryContents.set(index, stack);}
    public ItemStack removeStackFromSlot(int index) {return ItemStackHelper.getAndRemove(this.inventoryContents, index);}
    public ItemStack decrStackSize(int index, int count) {return ItemStackHelper.getAndRemove(this.inventoryContents, index);}

}
