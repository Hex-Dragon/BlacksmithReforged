package com.hexdragon.enchrebirth.block.anvil;

import com.hexdragon.core.item.EnchantmentHelperRe;
import com.hexdragon.core.item.ItemHelperRe;
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
import net.minecraft.tags.BlockTags;
import net.minecraft.util.IWorldPosCallable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;

import javax.annotation.Nonnull;
import java.util.Map;

public class AnvilContainerRe extends Container {

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
        worldPosCallable.consume((world, blockPos) -> this.inputInventory = (AnvilTileEntity) world.getTileEntity(blockPos)); // 与 NBT 中的物品栏同步
        Constuct(playerInventory);
        onCraftMatrixChanged(inputInventory);
    }

    // 输入与输出物品槽
    public IInventory inputInventory = new Inventory(2);
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
        ItemStack inputItem = this.inputInventory.getStackInSlot(0);
        ItemStack middleItem = this.inputInventory.getStackInSlot(1);
        if (inputItem.isEmpty() || middleItem.isEmpty()) {
            this.outputInventory.setInventorySlotContents(0, ItemStack.EMPTY);
            return;
        }
        ItemStack outputItem = inputItem.copy();

        // 初始化
        this.materialCost = 0;
        Map<Enchantment, Integer> outputEnchantments = EnchantmentHelper.getEnchantments(outputItem);

        // 调用钩子函数
        if (!onAnvilChange(this, inputItem, middleItem, outputInventory, inputItem.getDisplayName().getString(), 0))
            return;

        // 判断配方情况
        if (outputItem.isDamageable() && outputItem.getItem().getIsRepairable(inputItem, middleItem)) {
            // 使用原材料修复物品

            // 获取每个材料修复的耐久度
            int decrDamagePerMaterial = Math.min(outputItem.getDamage(), (int) (outputItem.getMaxDamage() / ItemHelperRe.getDamageableItemMaterialCost(outputItem) * 0.95F));
            if (decrDamagePerMaterial <= 0) {
                this.outputInventory.setInventorySlotContents(0, ItemStack.EMPTY);
                return;
            }

            // 获取消耗的材料个数
            int materialCost;
            for (materialCost = 0; decrDamagePerMaterial > 0 && materialCost < middleItem.getCount(); ++materialCost) {
                outputItem.setDamage(outputItem.getDamage() - decrDamagePerMaterial);
                decrDamagePerMaterial = Math.min(outputItem.getDamage(), (int) (outputItem.getMaxDamage() / ItemHelperRe.getDamageableItemMaterialCost(outputItem) * 0.95F));
            }
            this.materialCost = materialCost;

        } else if (outputItem.getItem() != middleItem.getItem() || !outputItem.isDamageable()) {
            // 配方无效，直接结束

            this.outputInventory.setInventorySlotContents(0, ItemStack.EMPTY);
            return;

        } else {
            // 使用两个相同物品进行修复

            // 获取修复后的 Damage
            int dur1 = inputItem.getMaxDamage() - inputItem.getDamage();
            int dur2 = middleItem.getMaxDamage() - middleItem.getDamage();
            int durNew = dur1 + dur2 + outputItem.getMaxDamage() * 15 / 100 + 1; // 额外奖励 15% 耐久度
            int newDamage = outputItem.getMaxDamage() - durNew;
            if (newDamage < 0) newDamage = 0;
            if (newDamage < outputItem.getDamage()) outputItem.setDamage(newDamage);

            // 将第二个物品的诅咒转移到第一个物品
            Map<Enchantment, Integer> curses = EnchantmentHelperRe.getCurseEnchantments(middleItem);
            if (curses.size() > 0) outputEnchantments.putAll(curses);

        }

        // 设置新物品的附魔
        EnchantmentHelper.setEnchantments(outputEnchantments, outputItem);

        // 设置输出
        this.outputInventory.setInventorySlotContents(0, outputItem);
        this.detectAndSendChanges();
    }

    // 铁砧相对于原版，损坏概率的百分比
    // 对于原版：“平均每个铁砧能用 25 次，相当于每用一次铁砧就消耗了 1.24 个用于合成铁砧的铁锭”
    // 考虑到铁砧现在没有等级消耗，略微增加损耗速率是比较平衡的
    private final static float AnvilBreakChanceM = 1.5F;
    // 在玩家从输出格拿走物品时触发：损坏铁砧、清空输入
    private ItemStack onTakeOutput(PlayerEntity player, ItemStack itemStack) {

        // 触发铁砧的随机损坏
        float breakChance = net.minecraftforge.common.ForgeHooks.onAnvilRepair(player, itemStack, AnvilContainerRe.this.inputInventory.getStackInSlot(0), AnvilContainerRe.this.inputInventory.getStackInSlot(1));
        this.worldPosCallable.consume((world, blockPos) -> {
            BlockState blockstate = world.getBlockState(blockPos);
            if (!player.abilities.isCreativeMode && blockstate.isIn(BlockTags.ANVIL) && player.getRNG().nextFloat() < breakChance * AnvilBreakChanceM) {
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
