package com.hexdragon.enchrebirth.block.anvil;

import com.hexdragon.enchrebirth.registry.RegMain;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

// 铁砧 TileEntity，用于引导自定义模型与存储物品 NBT
public class AnvilTileEntity extends LockableLootTileEntity {
    public AnvilTileEntity() {
        super(RegMain.tileEntityAnvil.get());
    }

    public Container container = null;
    public void markDirty() {
        super.markDirty();
        if (container != null) container.onCraftMatrixChanged(this);
    }

    public NonNullList<ItemStack> contents = NonNullList.withSize(2, ItemStack.EMPTY);

    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        if (!this.checkLootAndWrite(compound)) {
            ItemStackHelper.saveAllItems(compound, this.contents);
        }

        return compound;
    }

    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
        this.contents = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        if (!this.checkLootAndRead(nbt)) {
            ItemStackHelper.loadAllItems(nbt, this.contents);
        }

    }

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSizeInventory() {
        return 2;
    }

    protected NonNullList<ItemStack> getItems() {
        return this.contents;
    }

    protected void setItems(NonNullList<ItemStack> itemsIn) {
        this.contents = itemsIn;
    }

    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("block.minecraft.anvil");
    }

    protected Container createMenu(int id, PlayerInventory player) {
        AnvilContainerRe container = new AnvilContainerRe(id, player, IWorldPosCallable.of(this.world, this.pos));
        this.container = container;
        return container;
        // return ChestContainer.createGeneric9X3(id, player, this);
    }


    // TODO : 检查红石信号输出


//    @Override public ITextComponent getDisplayName() {
//        return new TranslationTextComponent("block.minecraft.anvil");
//    }
//
//    // 内建物品栏
//    public final CraftInputInventory inventory = new CraftInputInventory(2);
//    @Nullable @Override public Container createMenu(int sycID, PlayerInventory inventory, PlayerEntity player) {
//        // 与 Container 同步物品栏
//        AnvilContainerRe container = new AnvilContainerRe(sycID, inventory, IWorldPosCallable.of(this.world, this.pos));
//        this.inventory.container = container;
//        return container;
//    }
//
//    // 将物品栏 NBT 化
//    @Override public void read(BlockState state, CompoundNBT compound) {
//        inventory.setInventorySlotContents(0, ItemStack.read(compound.getCompound("ItemLeft")));
//        inventory.setInventorySlotContents(1, ItemStack.read(compound.getCompound("ItemRight")));
//        super.read(null, compound);
//    }
//    @Override public CompoundNBT write(CompoundNBT compound) {
//        compound.put("ItemLeft", inventory.getStackInSlot(0).serializeNBT());
//        compound.put("ItemRight", inventory.getStackInSlot(1).serializeNBT());
//        return super.write(compound);
//    }

}
