package com.hexdragon.enchrebirth.block;

import com.hexdragon.enchrebirth.reg.Registry;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.RepairContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.IWorldPosCallable;

import java.util.Map;
import java.util.stream.Collectors;

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
        this.addSlot(new Slot(this.inputInventory, 0, 49, 19) {
            // 检查物品是否能作为输入
            public boolean isItemValid(ItemStack stack) {
                return stack.isDamageable() || stack.getItem() == Items.ENCHANTED_BOOK || stack.isEnchanted();
            }
        });
        this.addSlot(new Slot(this.outputInventory, 0, 129, 34) {
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
        this.addSlot(new Slot(this.outputInventory, 1, 129 + 18, 34) {
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
        int yPositionStart = 84;
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
        if (!itemInput.isEmpty() && itemInput.getCount() == 1 &&
                itemInput.getItem() != Items.ENCHANTED_BOOK && !itemInput.isEnchanted()) {
            // 要求输入为 1 个非附魔书的有附魔物品
            itemOutput1 = this.prepareNewItem(itemInput, itemInput.getDamage(), itemInput.getCount());
            itemOutput2 = new ItemStack(Item.getItemById(1), 3);
        }
        // 设置物品并提交更改
        this.outputInventory.setInventorySlotContents(0, itemOutput1);
        this.outputInventory.setInventorySlotContents(1, itemOutput2);
        this.detectAndSendChanges();
    }

    // 对砂轮的单个物品进行预处理：例如移除非诅咒附魔、重置修复损耗等级等
    private ItemStack prepareNewItem(ItemStack stack, int damage, int count) {
        ItemStack itemstack = stack.copy();
        itemstack.removeChildTag("Enchantments");
        itemstack.removeChildTag("StoredEnchantments");
        if (damage > 0) {
            itemstack.setDamage(damage);
        } else {
            itemstack.removeChildTag("Damage");
        }

        itemstack.setCount(count);
        Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(stack).entrySet().stream().filter((p_217012_0_) -> p_217012_0_.getKey().isCurse()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        EnchantmentHelper.setEnchantments(map, itemstack);
        itemstack.setRepairCost(0);
        if (itemstack.getItem() == Items.ENCHANTED_BOOK && map.size() == 0) {
            itemstack = new ItemStack(Items.BOOK);
            if (stack.hasDisplayName()) {
                itemstack.setDisplayName(stack.getDisplayName());
            }
        }

        for (int i = 0; i < map.size(); ++i) {
            itemstack.setRepairCost(RepairContainer.getNewRepairCost(itemstack.getRepairCost()));
        }

        return itemstack;
    }

    // 接口: 当玩家使用 Shift+左键 快速转移物品时触发，需要尝试转移物品
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            ItemStack itemstack2 = this.inputInventory.getStackInSlot(0);
            if (index == 2) {
                if (!this.mergeItemStack(itemstack1, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemstack1, itemstack);
            } else if (index != 0 && index != 1) {
                if (!this.mergeItemStack(itemstack1, 0, 2, false)) {
                    return ItemStack.EMPTY;
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

    // 接口: 当页面关闭时触发，尝试返还物品
    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        this.worldPosCallable.consume((p_217009_2_, p_217009_3_) -> this.clearContainer(playerIn, p_217009_2_, this.inputInventory));
    }

}
