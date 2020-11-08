package com.hexdragon.enchrebirth.block;

import com.hexdragon.core.item.EnchantmentHelperRe;
import com.hexdragon.core.item.ItemHelperRe;
import com.hexdragon.enchrebirth.reg.Registry;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.IWorldPosCallable;

import java.util.Map;

public class GrindstoneContainerRe extends Container {

    // 输入与输出物品槽
    private final IInventory inputInventory = new Inventory(1) {
        public void markDirty() {
            super.markDirty();
            GrindstoneContainerRe.this.onCraftMatrixChanged(this);
        }
    };
    private final IInventory outputInventory = new Inventory(2);

    // 构造页面槽位
    private void Constuct(int windowIdIn, PlayerInventory playerInventoryIn, final IWorldPosCallable worldPosCallableIn) {
        this.addSlot(new Slot(this.inputInventory, 0, 62, 28) {
            // 检查物品是否能输入：没有没拿走的输出，且接受输入
            public boolean isItemValid(ItemStack stack) {
                return !(inputInventory.isEmpty() && !outputInventory.isEmpty()) && stack.getCount() == 1 && stack.isDamageable();
            }
        });
        this.addSlot(new Slot(this.outputInventory, 0, 120, 28) {
            // 禁止将物品放在输出格
            public boolean isItemValid(ItemStack stack) {
                return false;
            }

            // 从输出格拿走物品时清空输入格
            public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack) {
                GrindstoneContainerRe.this.inputInventory.setInventorySlotContents(0, ItemStack.EMPTY);
                return stack;
            }
        });
        this.addSlot(new Slot(this.outputInventory, 1, 120 + 18, 28) {
            // 禁止将物品放在输出格
            public boolean isItemValid(ItemStack stack) {
                return false;
            }

            // 从输出格拿走物品时清空输入格
            public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack) {
                GrindstoneContainerRe.this.inputInventory.setInventorySlotContents(0, ItemStack.EMPTY);
                return stack;
            }
        });

        // 增加物品栏与快捷栏槽位
        int yPositionStart = 68;
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventoryIn, j + i * 9 + 9, 8 + j * 18, yPositionStart + i * 18));
            }
        }
        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventoryIn, k, 8 + k * 18, 58 + yPositionStart));
        }
    }

    // 当输入物品改变时更新输出
    private void updateRecipeOutput() {
        // 获取输入
        ItemStack itemInput = this.inputInventory.getStackInSlot(0);
        ItemStack itemOutput1 = ItemStack.EMPTY;
        ItemStack itemOutput2 = ItemStack.EMPTY;
        // 判断输出
        if (inputInventory.isEmpty() && outputInventory.isEmpty()) {
            // 输入输出均为空：保持清空
        } else if (inputInventory.isEmpty() && !outputInventory.isEmpty()) {
            // 输入为空，输出不为空
            if (outputInventory.getStackInSlot(0).isEmpty() || outputInventory.getStackInSlot(1).isEmpty()) {
                // 刚拿走一个输出物品：输出保持不变
                itemOutput1 = outputInventory.getStackInSlot(0);
                itemOutput2 = outputInventory.getStackInSlot(1);
            } else {
                // 刚拿走输入物品：清空输出
            }
        } else {
            // 输入不为空，输出为空：更换了一个新的输入物品，更新输出
            // 输入不为空，输出为空：更新输出
            Map<Enchantment, Integer> DeletedEnchantments = EnchantmentHelperRe.getNonCurseEnchantments(itemInput);
            if (DeletedEnchantments.size() >= 1) {
                // 要求输入为有非诅咒附魔的物品
                itemOutput1 = this.prepareNewItem(itemInput);
                itemOutput2 = new ItemStack(Items.LAPIS_LAZULI, DeletedEnchantments.size()); // 每删除一个附魔给予一个青金石
            }
        }
        // 设置物品并提交更改
        this.outputInventory.setInventorySlotContents(0, itemOutput1);
        this.outputInventory.setInventorySlotContents(1, itemOutput2);
        this.detectAndSendChanges();
    }

    // TODO : 打开砂轮物品栏时关闭游戏，会在重新上线后掉落包括输入和输出预览的全部物品
    // TODO : 将砂轮 GUI 左侧的砂轮图片换成渲染的 3D 方块模型，这样可以做到材质包兼容
    // 对砂轮的单个物品进行预处理：例如移除非诅咒附魔、重置修复消耗等
    private ItemStack prepareNewItem(ItemStack stack) {
        ItemStack itemstack = stack.copy();
        itemstack.setCount(1);
        // 设置损伤值：消耗 0.4 个物品原料的耐久
        int newDamage = Math.min(itemstack.getMaxDamage(),
                itemstack.getDamage() + (int) (itemstack.getMaxDamage() / ItemHelperRe.getDamageableItemMaterialCost(itemstack) * 0.4F));
        itemstack.setDamage(newDamage);
        // 移除非诅咒附魔
        itemstack.removeChildTag("Enchantments");
        Map<Enchantment, Integer> map = EnchantmentHelperRe.getCurseEnchantments(stack);
        EnchantmentHelper.setEnchantments(map, itemstack);
        // 重置修复消耗
        itemstack.removeChildTag("RepairCost");
        return itemstack;
    }

    // 接口: 当页面关闭时触发，尝试返还物品
    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        // 如果输入为空则返还输出，如果输出为空则返还输入
        this.worldPosCallable.consume((p_217009_2_, p_217009_3_) -> this.clearContainer(playerIn, p_217009_2_,
                this.inputInventory.isEmpty() ? this.outputInventory : this.inputInventory));
    }

    // 接口: 当玩家使用 Shift+左键 快速转移物品时触发，需要尝试转移物品
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        // 事实上就大概差不多改了下，完全没看懂这是在干啥
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index == 1 || index == 2) {
                if (!this.mergeItemStack(itemstack1, 3, 39, true)) return ItemStack.EMPTY;
                slot.onSlotChange(itemstack1, itemstack);
            } else if (index != 0) {
                if (!this.mergeItemStack(itemstack1, 0, 2, false)) return ItemStack.EMPTY;
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



    /*
     * --------------------------------------------------------------
     *  以下方法直接使用原本的代码，不需要也未曾进行过修改
     * --------------------------------------------------------------
     */

    // 构造函数
    private final IWorldPosCallable worldPosCallable;
    public GrindstoneContainerRe(int windowIdIn, PlayerInventory playerInventoryIn) {
        this(windowIdIn, playerInventoryIn, IWorldPosCallable.DUMMY);
    }
    public GrindstoneContainerRe(int windowIdIn, PlayerInventory playerInventoryIn, final IWorldPosCallable worldPosCallableIn) {
        super(Registry.containerGrindstone.get(), windowIdIn);
        this.worldPosCallable = worldPosCallableIn;
        Constuct(windowIdIn, playerInventoryIn, worldPosCallableIn);
    }

    // 接口: 决定玩家是否可以使用该方块
    public boolean canInteractWith(PlayerEntity playerIn) {
        return isWithinUsableDistance(this.worldPosCallable, playerIn, Blocks.GRINDSTONE);
    }

    // 接口: 当输入物品改变时触发更新
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        super.onCraftMatrixChanged(inventoryIn);
        if (inventoryIn == this.inputInventory) {
            this.updateRecipeOutput();
        }
    }


}
