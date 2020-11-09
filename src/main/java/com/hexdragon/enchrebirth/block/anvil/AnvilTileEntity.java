package com.hexdragon.enchrebirth.block.anvil;

import com.hexdragon.enchrebirth.registry.RegMain;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

// 铁砧 TileEntity，用于引导自定义模型与存储物品 NBT
public class AnvilTileEntity extends LockableLootTileEntity {
    public static class PerfectAnvilTileEntity extends AnvilTileEntity {
        public PerfectAnvilTileEntity() { super(RegMain.tileEntityPerfectAnvil.get()); }
    }

    public static class ChippedAnvilTileEntity extends AnvilTileEntity {
        public ChippedAnvilTileEntity() { super(RegMain.tileEntityChippedAnvil.get()); }
    }

    public static class DamagedAnvilTileEntity extends AnvilTileEntity {
        public DamagedAnvilTileEntity() { super(RegMain.tileEntityDamagedAnvil.get()); }
    }
    public AnvilTileEntity(TileEntityType<?> typeIn) { super(typeIn); }

    private Container container = null;
    public void markDirty() {
        super.markDirty();
        if (container != null) container.onCraftMatrixChanged(this);
        world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
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
    }

    /**
     * Retrieves packet to send to the client whenever this Tile Entity is resynced via World.notifyBlockUpdate. For
     * modded TE's, this packet comes back to you clientside in {@link #onDataPacket}
     */
    @Nullable
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(pos, 0, getUpdateTag());
    }
    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        handleUpdateTag(null, pkt.getNbtCompound());
    }
    public CompoundNBT getUpdateTag() {
        return write(new CompoundNBT());
    }

    // TODO : 检查红石信号输出
    // TODO : 需要重构

}
