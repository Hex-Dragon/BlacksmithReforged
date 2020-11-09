package com.hexdragon.enchrebirth.block.anvil;

import com.hexdragon.enchrebirth.registry.RegMain;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

// 铁砧 TileEntity，用于引导自定义模型与存储物品 NBT
public class AnvilTileEntity extends LockableLootTileEntity {
    public AnvilTileEntity() {
        super(RegMain.tileEntityAnvil.get());
    }

    private Container container = null;
    public void markDirty() {
        super.markDirty();
        if (container != null) container.onCraftMatrixChanged(this);
        this.getWorld().notifyBlockUpdate(this.getPos(), this.getBlockState(), this.getBlockState(), 3);
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

    public NonNullList<ItemStack> getItems() {
        return this.contents;
    }

    public void setItems(NonNullList<ItemStack> itemsIn) {
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

    /**
     * Retrieves packet to send to the client whenever this Tile Entity is resynced via World.notifyBlockUpdate. For
     * modded TE's, this packet comes back to you clientside in {@link #onDataPacket}
     */
    @Nullable
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, 0, this.getUpdateTag());
    }

    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    // TODO : 检查红石信号输出

}
