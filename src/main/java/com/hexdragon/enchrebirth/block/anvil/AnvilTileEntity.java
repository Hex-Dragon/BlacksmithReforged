package com.hexdragon.enchrebirth.block.anvil;

import com.hexdragon.enchrebirth.registry.RegMain;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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
    protected ITextComponent getDefaultName() {return new TranslationTextComponent("container.anvil.title");}

    // 构造函数，为了支持不同损坏度的铁砧复读了 6 次
    public static class PerfectAnvilTileEntity extends AnvilTileEntity {
        public PerfectAnvilTileEntity() { super(RegMain.tileEntityPerfectAnvil.get()); }
    }
    public static class ChippedAnvilTileEntity extends AnvilTileEntity {
        public ChippedAnvilTileEntity() { super(RegMain.tileEntityChippedAnvil.get()); }
    }
    public static class DamagedAnvilTileEntity extends AnvilTileEntity {
        public DamagedAnvilTileEntity() { super(RegMain.tileEntityDamagedAnvil.get()); }
    }
    public static class PerfectNetheriteAnvilTileEntity extends AnvilTileEntity {
        public PerfectNetheriteAnvilTileEntity() { super(RegMain.tileEntityNetheritePerfectAnvil.get()); }
    }
    public static class ChippedNetheriteAnvilTileEntity extends AnvilTileEntity {
        public ChippedNetheriteAnvilTileEntity() { super(RegMain.tileEntityNetheriteChippedAnvil.get()); }
    }
    public static class DamagedNetheriteAnvilTileEntity extends AnvilTileEntity {
        public DamagedNetheriteAnvilTileEntity() { super(RegMain.tileEntityNetheriteDamagedAnvil.get()); }
    }
    public AnvilTileEntity(TileEntityType<?> typeIn) { super(typeIn); }

    // 物品栏
    public NonNullList<ItemStack> inventory = NonNullList.withSize(2, ItemStack.EMPTY);
    public int getSizeInventory() {return 2;}
    public NonNullList<ItemStack> getItems() {
        return this.inventory;
    }
    public void setItems(NonNullList<ItemStack> itemsIn) {
        this.inventory = itemsIn;
    }
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        // 只允许将物品放入（这个方法只影响自动化，从 GUI 放入需要在 Slot 里写）
        return Block.getBlockFromItem(stack.getItem()) == Blocks.AIR;
    }

    // NBT 交互
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        if (!this.checkLootAndWrite(compound)) ItemStackHelper.saveAllItems(compound, this.inventory);
        return compound;
    }
    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
        this.inventory = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        if (!this.checkLootAndRead(nbt)) ItemStackHelper.loadAllItems(nbt, this.inventory);
    }

    // 支持从服务器接受数据，以在物品改变时同步显示
    @Nullable public SUpdateTileEntityPacket getUpdatePacket() {return new SUpdateTileEntityPacket(pos, 0, getUpdateTag());}
    @Override public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {handleUpdateTag(null, pkt.getNbtCompound());}
    public CompoundNBT getUpdateTag() {return write(new CompoundNBT());}

    // 物品更新触发的事件
    private Container container = null;
    public void markDirty() {
        super.markDirty();
        if (container != null) container.onCraftMatrixChanged(this); // 通知 Container 改变输出内容
        world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), Constants.BlockFlags.BLOCK_UPDATE); // 通知客户端更新物品模型渲染
    }
    protected Container createMenu(int id, PlayerInventory player) {
        AnvilContainerRe container = new AnvilContainerRe(id, player, IWorldPosCallable.of(this.world, this.pos));
        this.container = container;
        return container;
    }

    // TODO : 让漏斗只能从上方注入物品（需要使用 Capability）
    // TODO : 让铁砧可以根据物品栏输出红石信号

}
