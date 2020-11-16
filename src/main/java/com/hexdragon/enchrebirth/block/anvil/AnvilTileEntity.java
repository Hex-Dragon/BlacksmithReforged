package com.hexdragon.enchrebirth.block.anvil;

import com.hexdragon.enchrebirth.Main;
import com.hexdragon.enchrebirth.registry.RegMain;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

// 铁砧 TileEntity，用于引导自定义模型与存储物品 NBT
public class AnvilTileEntity extends LockableLootTileEntity implements ISidedInventory {
    protected ITextComponent getDefaultName() {return new TranslationTextComponent("gui.anvil.title");}

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
        public PerfectNetheriteAnvilTileEntity() { super(RegMain.tileEntityPerfectNetheriteAnvil.get()); }
    }
    public static class ChippedNetheriteAnvilTileEntity extends AnvilTileEntity {
        public ChippedNetheriteAnvilTileEntity() { super(RegMain.tileEntityChippedNetheriteAnvil.get()); }
    }
    public static class DamagedNetheriteAnvilTileEntity extends AnvilTileEntity {
        public DamagedNetheriteAnvilTileEntity() { super(RegMain.tileEntityDamagedNetheriteAnvil.get()); }
    }
    public AnvilTileEntity(TileEntityType<?> typeIn) { super(typeIn); }

    // 物品栏基础
    public NonNullList<ItemStack> inventory = NonNullList.withSize(2, ItemStack.EMPTY);
    public int getSizeInventory() {return 2;}
    public NonNullList<ItemStack> getItems() {
        return this.inventory;
    }
    public void setItems(NonNullList<ItemStack> itemsIn) {
        this.inventory = itemsIn;
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
        AnvilContainerRe container = new AnvilContainerRe(id, player, IWorldPosCallable.of(this.world, this.pos), this);
        Main.LastTileEntity = this;
        this.container = container;
        return container;
    }

    // 控制漏斗的流入和流出
    @Override public int[] getSlotsForFace(Direction direction) {
        if (direction == Direction.UP) return new int[]{0, 1}; // 顶部可以向全部两格输入
        Direction blockDirection = this.getBlockState().get(AnvilBlock.FACING);
        if (direction == blockDirection.getOpposite()) return new int[]{0}; // 左侧只能输入到左侧
        if (direction == blockDirection) return new int[]{1}; // 右侧只能输入到右侧
        return new int[0]; // 其他侧拒绝访问
    }
    @Override public boolean canInsertItem(int index, ItemStack itemStackIn, @Nullable Direction direction) {
        Direction blockDirection = this.getBlockState().get(AnvilBlock.FACING);
        return direction == Direction.UP || direction == blockDirection || direction == blockDirection.getOpposite();
    }
    @Override public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
        return false;
    }
    @Nonnull @Override public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (!this.removed && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return LazyOptional.of(() -> new SidedInvWrapper(this, side)).cast();
        }
        return super.getCapability(cap, side);
    }

    // TODO : 让铁砧可以根据物品栏里的物品数量向比较器输出红石信号

}
