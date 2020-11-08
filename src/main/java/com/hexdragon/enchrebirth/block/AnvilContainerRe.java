package com.hexdragon.enchrebirth.block;

import com.hexdragon.enchrebirth.registry.RegMain;
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
import net.minecraft.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;

import javax.annotation.Nonnull;
import java.util.Map;

public class AnvilContainerRe extends Container {

    // 输入与输出物品槽
    private final IInventory inputInventory = new Inventory(2) {
        public void markDirty() {
            super.markDirty();
            AnvilContainerRe.this.onCraftMatrixChanged(this);
        }
    };
    private final CraftResultInventory outputInventory = new CraftResultInventory();

    // 构造页面槽位
    private void Constuct(PlayerInventory playerInventory) {
        this.addSlot(new Slot(this.inputInventory, 0, 27, 47));
        this.addSlot(new Slot(this.inputInventory, 1, 76, 47));
        this.addSlot(new Slot(this.outputInventory, 2, 134, 47) {
            // 禁止将物品放在输出格
            public boolean isItemValid(ItemStack stack) {
                return false;
            }
            // 确认玩家能否拿走物品
            public boolean canTakeStack(PlayerEntity player) {
                return player.abilities.isCreativeMode || player.experienceLevel >= totalCost.get();
            }
            // 触发玩家拿走物品的事件
            public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack) {
                return AnvilContainerRe.this.onTakeOutput(thePlayer, stack);
            }
        });

        // 增加物品栏与快捷栏槽位
        int yPositionStart = 84;
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
    // 消耗的等级
    public final IntReferenceHolder totalCost = IntReferenceHolder.single();

    // 当输入物品改变时更新输出
    public void updateRepairOutput() {

        // 获取输入物品，并检查不为空
        ItemStack inputItem = this.inputInventory.getStackInSlot(0);
        ItemStack middleItem = this.inputInventory.getStackInSlot(1);
        if (inputItem.isEmpty() || middleItem.isEmpty()) {
            this.outputInventory.setInventorySlotContents(0, ItemStack.EMPTY);
            this.totalCost.set(0);
            return;
        }
        ItemStack outputItem = inputItem.copy();

        this.totalCost.set(1);
        int recipeCost = 0;
        int baseCost = inputItem.getRepairCost() + (middleItem.isEmpty() ? 0 : middleItem.getRepairCost());
        this.materialCost = 0;
        Map<Enchantment, Integer> outputEnchantments = EnchantmentHelper.getEnchantments(outputItem);

        if (!onAnvilChange(this, inputItem, middleItem, outputInventory, inputItem.getDisplayName().getString(), baseCost))
            return;
        if (outputItem.isDamageable() && outputItem.getItem().getIsRepairable(inputItem, middleItem)) {
            int l2 = Math.min(outputItem.getDamage(), outputItem.getMaxDamage() / 4);
            if (l2 <= 0) {
                this.outputInventory.setInventorySlotContents(0, ItemStack.EMPTY);
                this.totalCost.set(0);
                return;
            }

            int i3;
            for (i3 = 0; l2 > 0 && i3 < middleItem.getCount(); ++i3) {
                int j3 = outputItem.getDamage() - l2;
                outputItem.setDamage(j3);
                ++recipeCost;
                l2 = Math.min(outputItem.getDamage(), outputItem.getMaxDamage() / 4);
            }

            this.materialCost = i3;
        } else {
            if (outputItem.getItem() != middleItem.getItem() || !outputItem.isDamageable()) {
                this.outputInventory.setInventorySlotContents(0, ItemStack.EMPTY);
                this.totalCost.set(0);
                return;
            }

            if (outputItem.isDamageable()) {
                int l = inputItem.getMaxDamage() - inputItem.getDamage();
                int i1 = middleItem.getMaxDamage() - middleItem.getDamage();
                int j1 = i1 + outputItem.getMaxDamage() * 12 / 100;
                int k1 = l + j1;
                int l1 = outputItem.getMaxDamage() - k1;
                if (l1 < 0) {
                    l1 = 0;
                }

                if (l1 < outputItem.getDamage()) {
                    outputItem.setDamage(l1);
                    recipeCost += 2;
                }
            }

            Map<Enchantment, Integer> map1 = EnchantmentHelper.getEnchantments(middleItem);
            boolean flag2 = false;
            boolean flag3 = false;

            for (Enchantment enchantment1 : map1.keySet()) {
                if (enchantment1 != null) {
                    int i2 = outputEnchantments.getOrDefault(enchantment1, 0);
                    int j2 = map1.get(enchantment1);
                    j2 = i2 == j2 ? j2 + 1 : Math.max(j2, i2);
                    boolean flag1 = enchantment1.canApply(inputItem);
                    if (this.player.abilities.isCreativeMode || inputItem.getItem() == Items.ENCHANTED_BOOK) {
                        flag1 = true;
                    }

                    for (Enchantment enchantment : outputEnchantments.keySet()) {
                        if (enchantment != enchantment1 && !enchantment1.isCompatibleWith(enchantment)) {
                            flag1 = false;
                            ++recipeCost;
                        }
                    }

                    if (!flag1) {
                        flag3 = true;
                    } else {
                        flag2 = true;
                        if (j2 > enchantment1.getMaxLevel()) {
                            j2 = enchantment1.getMaxLevel();
                        }

                        outputEnchantments.put(enchantment1, j2);
                        int k3 = 0;
                        switch (enchantment1.getRarity()) {
                            case COMMON:
                                k3 = 1;
                                break;
                            case UNCOMMON:
                                k3 = 2;
                                break;
                            case RARE:
                                k3 = 4;
                                break;
                            case VERY_RARE:
                                k3 = 8;
                        }

                        recipeCost += k3 * j2;
                        if (inputItem.getCount() > 1) {
                            recipeCost = 100;
                        }
                    }
                }
            }

            if (flag3 && !flag2) {
                this.outputInventory.setInventorySlotContents(0, ItemStack.EMPTY);
                this.totalCost.set(0);
                return;
            }
        }

        // 设置花费并检查玩家等级是否足够
        this.totalCost.set(baseCost + recipeCost);
        if ((baseCost + recipeCost) >= 100 && !this.player.abilities.isCreativeMode) {
            // 玩家等级不够
            outputItem = ItemStack.EMPTY;
        } else {
            // 玩家等级足够
            int newRepairCost = outputItem.getRepairCost();
            if (!middleItem.isEmpty() && newRepairCost < middleItem.getRepairCost()) {
                newRepairCost = middleItem.getRepairCost();
            }
            newRepairCost = newRepairCost * 2 + 1; // 每次修复都翻倍并 +1
            outputItem.setRepairCost(newRepairCost);
            EnchantmentHelper.setEnchantments(outputEnchantments, outputItem);
        }

        // 设置输出
        this.outputInventory.setInventorySlotContents(0, outputItem);
        this.detectAndSendChanges();
    }

    // 在玩家从输出格拿走物品时触发：扣除等级、损坏铁砧、清空输入
    private ItemStack onTakeOutput(PlayerEntity player, ItemStack itemStack) {

        // 扣除非创造模式玩家的等级
        if (!player.abilities.isCreativeMode) player.addExperienceLevel(-this.totalCost.get());
        this.totalCost.set(0);

        // 触发铁砧的随机损坏
        float breakChance = net.minecraftforge.common.ForgeHooks.onAnvilRepair(player, itemStack, AnvilContainerRe.this.inputInventory.getStackInSlot(0), AnvilContainerRe.this.inputInventory.getStackInSlot(1));
        this.worldPosCallable.consume((world, blockPos) -> {
            BlockState blockstate = world.getBlockState(blockPos);
            if (!player.abilities.isCreativeMode && blockstate.isIn(BlockTags.ANVIL) && player.getRNG().nextFloat() < breakChance) {
                BlockState blockstate1 = AnvilBlock.damage(blockstate);
                if (blockstate1 == null) {
                    world.removeBlock(blockPos, false);
                    world.playEvent(1029, blockPos, 0);
                } else {
                    world.setBlockState(blockPos, blockstate1, 2);
                    world.playEvent(1030, blockPos, 0);
                }
            } else {
                world.playEvent(1030, blockPos, 0);
            }
        });

        // 清空输入
        this.inputInventory.setInventorySlotContents(0, ItemStack.EMPTY);
        if (this.materialCost > 0) {
            ItemStack itemstack = this.inputInventory.getStackInSlot(1);
            if (!itemstack.isEmpty() && itemstack.getCount() > this.materialCost) {
                itemstack.shrink(this.materialCost);
                this.inputInventory.setInventorySlotContents(1, itemstack);
            } else {
                this.inputInventory.setInventorySlotContents(1, ItemStack.EMPTY);
            }
        } else {
            this.inputInventory.setInventorySlotContents(1, ItemStack.EMPTY);
        }

        return itemStack;
    }




    /*
     * --------------------------------------------------------------
     *  以下方法直接使用原本的代码，不需要进行修改
     * --------------------------------------------------------------
     */

    // 构造函数
    private final IWorldPosCallable worldPosCallable;
    private final PlayerEntity player;
    public AnvilContainerRe(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, IWorldPosCallable.DUMMY);
    }
    public AnvilContainerRe(int id, PlayerInventory playerInventory, IWorldPosCallable worldPosCallable) {
        super(RegMain.containerAnvil.get(), id);
        this.worldPosCallable = worldPosCallable;
        this.player = playerInventory.player;
        Constuct(playerInventory);
        this.trackInt(this.totalCost);
    }

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

    // 接口: 当页面关闭时触发，尝试返还物品
    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        this.worldPosCallable.consume((p_234647_2_, p_234647_3_) -> this.clearContainer(playerIn, p_234647_2_, this.inputInventory));
    }

    // 铁砧输入改变时 Forge 触发的钩子事件（从 ForgeHooks.java 中复制）
    public static boolean onAnvilChange(AnvilContainerRe container, @Nonnull ItemStack left, @Nonnull ItemStack right, IInventory outputSlot, String name, int baseCost) {
        AnvilUpdateEvent e = new AnvilUpdateEvent(left, right, name, baseCost);
        if (MinecraftForge.EVENT_BUS.post(e)) return false;
        if (e.getOutput().isEmpty()) return true;
        outputSlot.setInventorySlotContents(0, e.getOutput());
        container.totalCost.set(e.getCost());
        container.materialCost = e.getMaterialCost();
        return false;
    }

}
