package com.hexdragon.enchrebirth.block.anvil;

import com.hexdragon.core.item.CraftInputInventory;
import com.hexdragon.enchrebirth.registry.RegMain;
import com.sun.istack.internal.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

// 铁砧 TileEntity，用于引导自定义模型与存储物品 NBT
public class AnvilTileEntity extends TileEntity implements INamedContainerProvider {
    public AnvilTileEntity() {
        super(RegMain.tileEntityAnvil.get());
    }
    @Override public ITextComponent getDisplayName() {
        return new TranslationTextComponent("block.minecraft.anvil");
    }

    // 内建物品栏
    public final CraftInputInventory inventory = new CraftInputInventory(2);
    @Nullable @Override public Container createMenu(int sycID, PlayerInventory inventory, PlayerEntity player) {
        // 与 Container 同步物品栏
        AnvilContainerRe container = new AnvilContainerRe(sycID, inventory, IWorldPosCallable.of(this.world, this.pos));
        this.inventory.container = container;
        return container;
    }

    // 将物品栏 NBT 化
    @Override public void read(BlockState state, CompoundNBT compound) {
        inventory.setInventorySlotContents(0, ItemStack.read(compound.getCompound("ItemLeft")));
        inventory.setInventorySlotContents(1, ItemStack.read(compound.getCompound("ItemRight")));
        super.read(null, compound);
    }
    @Override public CompoundNBT write(CompoundNBT compound) {
        compound.put("ItemLeft", inventory.getStackInSlot(0).serializeNBT());
        compound.put("ItemRight", inventory.getStackInSlot(1).serializeNBT());
        return super.write(compound);
    }

}
