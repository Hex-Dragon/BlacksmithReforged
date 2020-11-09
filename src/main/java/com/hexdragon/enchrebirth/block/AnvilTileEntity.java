package com.hexdragon.enchrebirth.block;

import com.hexdragon.core.item.CraftInputInventory;
import com.hexdragon.enchrebirth.Main;
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

public class AnvilTileEntity extends TileEntity implements INamedContainerProvider {
    public AnvilTileEntity() {
        super(RegMain.tileEntityAnvil.get());
    }
    @Override public ITextComponent getDisplayName() {
        return new TranslationTextComponent("block.minecraft.anvil");
    }

    public final CraftInputInventory inputInventory = new CraftInputInventory(2);

    @Nullable @Override public Container createMenu(int sycID, PlayerInventory inventory, PlayerEntity player) {
        Main.LOGGER.warn("CREATE MENU");
        AnvilContainerRe container = new AnvilContainerRe(sycID, inventory, IWorldPosCallable.of(this.world, this.pos));
        inputInventory.container = container;
        return container;
    }

    @Override public void read(BlockState state, CompoundNBT compound) {
        Main.LOGGER.warn("READ");
        inputInventory.addItem(ItemStack.read(compound.getCompound("item1")));
        inputInventory.addItem(ItemStack.read(compound.getCompound("item2")));
        super.read(null, compound);
    }

    @Override public CompoundNBT write(CompoundNBT compound) {
        Main.LOGGER.warn("WRITE");
        ItemStack itemStack1 = inputInventory.getStackInSlot(0).copy();
        compound.put("item1", itemStack1.serializeNBT());
        ItemStack itemStack2 = inputInventory.getStackInSlot(1).copy();
        compound.put("item2", itemStack2.serializeNBT());
        return super.write(compound);
    }

    public CraftInputInventory getInventory() {
        return inputInventory;
    }

}
