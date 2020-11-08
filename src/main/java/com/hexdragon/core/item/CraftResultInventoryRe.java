package com.hexdragon.core.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IRecipeHolder;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;

import javax.annotation.Nullable;

// 支持多个物品栏的 CraftResultInventory
public class CraftResultInventoryRe implements IInventory, IRecipeHolder {

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

    // 各种简单的方法
    public int getSizeInventory() {return slotsCount;}
    public boolean isEmpty() {
        for (ItemStack itemstack : this.inventoryContents) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    public void clear() {this.inventoryContents.clear();}
    public void markDirty() {}
    public boolean isUsableByPlayer(PlayerEntity player) {return true;}

    // 对物品格子的输入输出
    public ItemStack getStackInSlot(int index) {return this.inventoryContents.get(index);}
    public void setInventorySlotContents(int index, ItemStack stack) {this.inventoryContents.set(index, stack);}
    public ItemStack removeStackFromSlot(int index) {return ItemStackHelper.getAndRemove(this.inventoryContents, index);}
    public ItemStack decrStackSize(int index, int count) {return ItemStackHelper.getAndRemove(this.inventoryContents, index);}

    // 不知道有啥用的 Recipe 支持
    @Nullable private IRecipe<?> recipeUsed;
    public void setRecipeUsed(@Nullable IRecipe<?> recipe) {this.recipeUsed = recipe;}
    @Nullable public IRecipe<?> getRecipeUsed() {return this.recipeUsed;}

}
