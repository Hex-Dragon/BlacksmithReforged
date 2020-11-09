package com.hexdragon.enchrebirth.block;

import com.hexdragon.enchrebirth.registry.RegMain;
import com.sun.istack.internal.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class AnvilTileEntity extends TileEntity implements INamedContainerProvider {
    public AnvilTileEntity() {
        super(RegMain.tileEntityAnvil.get());
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent("First Container");
    }

    @Nullable
    @Override
    public Container createMenu(int sycID, PlayerInventory inventory, PlayerEntity player) {
        return new AnvilContainerRe(sycID, inventory, IWorldPosCallable.of(this.world, this.pos));
    }

//    @Override
//    public void read(BlockState state, CompoundNBT compound) {
//        this.inventory.addItem(ItemStack.read(compound.getCompound("item1")));
//        this.inventory.addItem(ItemStack.read(compound.getCompound("item2")));
//        super.read(null, compound);
//    }
//
//    @Override
//    public CompoundNBT write(CompoundNBT compound) {
//        ItemStack itemStack1 = this.inventory.getStackInSlot(0).copy();
//        compound.put("item1", itemStack1.serializeNBT());
//        ItemStack itemStack2 = this.inventory.getStackInSlot(1).copy();
//        compound.put("item2", itemStack2.serializeNBT());
//        return super.write(compound);
//    }
//
//    public Inventory getInventory() {
//        return inventory;
//    }

}
