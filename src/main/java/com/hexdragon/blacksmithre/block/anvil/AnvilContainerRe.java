package com.hexdragon.blacksmithre.block.anvil;

import com.hexdragon.corere.item.EnchantmentHelperRe;
import com.hexdragon.corere.item.ItemHelperRe;
import com.hexdragon.blacksmithre.registry.RegMain;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;

import javax.annotation.Nonnull;
import java.util.Map;

public class AnvilContainerRe extends Container {

    // 构造函数
    public final IWorldPosCallable worldPosCallable;
    private final PlayerEntity player;
    public AnvilContainerRe(int id, PlayerInventory playerInventory) {
        // 当从 Screen 渲染线程触发，就会从这里进行调用
        this(id, playerInventory, IWorldPosCallable.DUMMY, new Inventory(2));
    }
    public AnvilContainerRe(int id, PlayerInventory playerInventory, IWorldPosCallable worldPosCallable, IInventory tileEntity) {
        super(RegMain.containerAnvil.get(), id);
        this.worldPosCallable = worldPosCallable;
        this.player = playerInventory.player;
        this.inputInventory = tileEntity; // 与 NBT 中的物品栏同步
        Constuct(playerInventory);
        onCraftMatrixChanged(inputInventory); // 初始化输出
    }

    // 输入与输出物品槽
    public IInventory inputInventory;
    public final CraftResultInventory outputInventory = new CraftResultInventory();

    // 构造页面槽位
    private void Constuct(PlayerInventory playerInventory) {
        this.addSlot(new Slot(this.inputInventory, 0, 62, 28));
        this.addSlot(new Slot(this.inputInventory, 1, 80, 28));
        this.addSlot(new Slot(this.outputInventory, 2, 138, 28) {
            // 禁止将物品放在输出格
            public boolean isItemValid(ItemStack stack) {
                return false;
            }
            // 触发玩家拿走物品的事件
            public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack) {
                return AnvilContainerRe.this.onTakeOutput(thePlayer, stack);
            }
        });

        // 增加物品栏与快捷栏槽位
        int yPositionStart = 68;
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, yPositionStart + i * 18));
            }
        }
        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 58 + yPositionStart));
        }
    }

    // 消耗的物品合成原料个数
    public int materialCost;

    // 当输入物品改变时更新输出
    public void updateRepairOutput() {

        // 获取输入物品，并检查不为空
        ItemStack leftItem = this.inputInventory.getStackInSlot(0);
        ItemStack middleItem = this.inputInventory.getStackInSlot(1);
        if (leftItem.isEmpty() || middleItem.isEmpty()) {
            this.outputInventory.setInventorySlotContents(0, ItemStack.EMPTY);
            return;
        }

        // 初始化
        this.materialCost = 0;

        // 调用钩子函数
        if (!onAnvilChange(this, leftItem, middleItem, outputInventory, leftItem.getDisplayName().getString(), 0))
            return;

        // 将可损坏的物品优先视作左侧
        if (middleItem.isDamageable() && !leftItem.isDamageable()) {
            ItemStack swapItem = leftItem.copy();
            leftItem = middleItem; middleItem = swapItem;
        }
        ItemStack outputItem = leftItem.copy();

        // 根据腐朽诅咒附魔获取修复比
        final float enchantmentRadio = MathHelper.clamp(1 - Math.max(EnchantmentHelper.getEnchantmentLevel(RegMain.enchDecay.get(), leftItem), EnchantmentHelper.getEnchantmentLevel(RegMain.enchDecay.get(), middleItem)) * 0.7f, 0f, 1f);

        // 判断配方情况
        if (outputItem.isDamageable() && outputItem.getItem().getIsRepairable(leftItem, middleItem)) {
            // 使用原材料修复物品

            // 根据难度与附魔获取实际修复比
            final float[] radio = {0};
            worldPosCallable.consume((world, pos) -> radio[0] = new float[]{1f, 1f, 0.98f, 0.95f}[world.getDifficulty().getId()] * enchantmentRadio);

            // 获取每个材料修复的耐久度
            int mendingPerMaterial = (int) (outputItem.getMaxDamage() / ItemHelperRe.getDamageableItemRepairCost(outputItem) * radio[0]);
            if (mendingPerMaterial <= 0) {
                this.outputInventory.setInventorySlotContents(0, ItemStack.EMPTY);
                return;
            }

            // 获取消耗的材料个数
            int materialCost, mending = mendingPerMaterial;
            for (materialCost = 0; mending > 0 && materialCost < middleItem.getCount(); ++materialCost) {
                outputItem.setDamage(outputItem.getDamage() - mending);
                mending = Math.min(outputItem.getDamage(), mendingPerMaterial);
            }
            this.materialCost = materialCost;

        } else if (leftItem.getItem() != middleItem.getItem() || !leftItem.isDamageable()) {
            // 配方无效，直接结束

            this.outputInventory.setInventorySlotContents(0, ItemStack.EMPTY);
            return;

        } else {
            // 使用两个相同物品进行修复

            // 根据难度获取奖励的耐久度百分比
            final int[] radio = {0};
            worldPosCallable.consume((world, pos) -> radio[0] = new int[]{25, 25, 15, 5}[world.getDifficulty().getId()]);

            // 获取修复后的 Damage
            int dur1 = leftItem.getMaxDamage() - leftItem.getDamage();
            int dur2 = middleItem.getMaxDamage() - middleItem.getDamage();
            int durNew = (int) (dur1 + (dur2 + outputItem.getMaxDamage() * radio[0] / 100 + 1) * enchantmentRadio);
            int newDamage = outputItem.getMaxDamage() - durNew;
            if (newDamage < 0) newDamage = 0;
            if (newDamage < outputItem.getDamage()) outputItem.setDamage(newDamage);

            // 将第二个物品的诅咒转移到第一个物品
            Map<Enchantment, Integer> outputEnchantments = EnchantmentHelper.getEnchantments(outputItem);
            Map<Enchantment, Integer> curses = EnchantmentHelperRe.getCurseEnchantments(middleItem);
            if (curses.size() > 0) outputEnchantments.putAll(curses);
            EnchantmentHelper.setEnchantments(outputEnchantments, outputItem);

        }

        // 设置输出
        this.outputInventory.setInventorySlotContents(0, outputItem);
        this.detectAndSendChanges();
    }

    // 在玩家从输出格拿走物品时触发：损坏铁砧、清空输入
    private ItemStack onTakeOutput(PlayerEntity player, ItemStack itemStack) {

        // 清空输入
        if (this.materialCost > 0) {
            boolean reverseInput = !this.inputInventory.getStackInSlot(0).isDamageable();
            this.inputInventory.setInventorySlotContents(reverseInput ? 1 : 0, ItemStack.EMPTY);
            ItemStack itemstack = this.inputInventory.getStackInSlot(reverseInput ? 0 : 1);
            if (!itemstack.isEmpty() && itemstack.getCount() > this.materialCost) {
                itemstack.shrink(this.materialCost);
                this.inputInventory.setInventorySlotContents(reverseInput ? 0 : 1, itemstack);
            } else {
                this.inputInventory.setInventorySlotContents(reverseInput ? 0 : 1, ItemStack.EMPTY);
            }
        } else {
            this.inputInventory.setInventorySlotContents(0, ItemStack.EMPTY);
            this.inputInventory.setInventorySlotContents(1, ItemStack.EMPTY);
        }

        // 触发铁砧的随机损坏
        // 对于原版：“平均每个铁砧能用 25 次，相当于每用一次铁砧就消耗了 1.24 个用于合成铁砧的铁锭”
        float baseBreakChance = net.minecraftforge.common.ForgeHooks.onAnvilRepair(player, itemStack, AnvilContainerRe.this.inputInventory.getStackInSlot(0), AnvilContainerRe.this.inputInventory.getStackInSlot(1));
        this.worldPosCallable.consume((world, blockPos) -> {
            BlockState blockstate = world.getBlockState(blockPos);
            // 考虑到铁砧现在没有等级消耗，略微增加损耗速率是比较平衡的
            // 下界合金砧的耐久在普通难度下为铁砧的 25 倍，平均能用 500 次
            float[] ironChance = {0.75f, 1f, 1.25f, 1.75f};
            float[] netheriteChance = {0f, 0.02f, 0.05f, 0.07f};
            float newBreakChance = baseBreakChance * ((blockstate.get(RegMain.blockStateMaterial) == 0) ? ironChance : netheriteChance)[world.getDifficulty().getId()];
            if (!player.abilities.isCreativeMode && blockstate.isIn(BlockTags.ANVIL) && player.getRNG().nextFloat() < newBreakChance) {
                BlockState newBlockState = AnvilBlock.damage(blockstate);
                if (newBlockState == null) {
                    world.removeBlock(blockPos, false);
                    world.playEvent(1029, blockPos, 0);
                } else {
                    // 保存当前物品并清空物品栏，避免在更改方块时爆出
                    AnvilTileEntity tileEntity = (AnvilTileEntity) world.getTileEntity(blockPos);
                    ItemStack itemStack0 = tileEntity.getItems().get(0).copy();
                    ItemStack itemStack1 = tileEntity.getItems().get(1).copy();
                    tileEntity.inventory.clear();
                    // 放置损坏后的方块
                    world.setBlockState(blockPos, newBlockState, 2);
                    // 把物品放回新方块
                    AnvilTileEntity newTileEntity = (AnvilTileEntity) world.getTileEntity(blockPos);
                    newTileEntity.inventory.set(0, itemStack0);
                    newTileEntity.inventory.set(1, itemStack1);
                    newTileEntity.markDirty();
                    // 让玩家再次打开 GUI
                    player.openContainer(newTileEntity.getBlockState().getContainer(world, blockPos));
                    world.playEvent(1030, blockPos, 0);
                }
            } else {
                world.playEvent(1030, blockPos, 0);
            }
        });

        return itemStack;
    }





    /*
     * --------------------------------------------------------------
     *  以下方法直接使用原本的代码，不需要进行修改
     * --------------------------------------------------------------
     */

    // 接口: 当输入物品改变时触发更新
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        super.onCraftMatrixChanged(inventoryIn);
        if (inventoryIn == this.inputInventory) {
            this.updateRepairOutput();
        }
    }

    // 接口: 决定玩家是否可以使用该方块
    public boolean canInteractWith(PlayerEntity playerIn) {
        return this.worldPosCallable.applyOrElse((world, blockPos) -> world.getBlockState(blockPos).isIn(BlockTags.ANVIL) && playerIn.getDistanceSq((double) blockPos.getX() + 0.5D, (double) blockPos.getY() + 0.5D, (double) blockPos.getZ() + 0.5D) <= 64.0D, true);
    }

    // 接口: 当玩家使用 Shift+左键 快速转移物品时触发，需要尝试转移物品
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index == 2) {
                if (!this.mergeItemStack(itemstack1, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemstack1, itemstack);
            } else if (index != 0 && index != 1) {
                if (index < 39) {
                    if (!this.mergeItemStack(itemstack1, 0, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.mergeItemStack(itemstack1, 3, 39, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }

    // 铁砧输入改变时 Forge 触发的钩子事件（从 ForgeHooks.java 中复制）
    public static boolean onAnvilChange(AnvilContainerRe container, @Nonnull ItemStack left, @Nonnull ItemStack right, IInventory outputSlot, String name, int baseCost) {
        AnvilUpdateEvent e = new AnvilUpdateEvent(left, right, name, baseCost);
        if (MinecraftForge.EVENT_BUS.post(e)) return false;
        if (e.getOutput().isEmpty()) return true;
        outputSlot.setInventorySlotContents(0, e.getOutput());
        container.materialCost = e.getMaterialCost();
        return false;
    }

}
