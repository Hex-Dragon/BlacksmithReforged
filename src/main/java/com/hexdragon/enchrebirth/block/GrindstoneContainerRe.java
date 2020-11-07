package com.hexdragon.enchrebirth.block;

import com.hexdragon.enchrebirth.reg.Registry;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.RepairContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.world.World;

import java.util.Map;
import java.util.stream.Collectors;

public class GrindstoneContainerRe extends Container {

    // 输入与输出物品槽
    private final IInventory outputInventory = new CraftResultInventory();
    private final IInventory inputInventory = new Inventory(2) {
        /**
         * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think
         * it hasn't changed and skip it.
         */
        public void markDirty() {
            super.markDirty();
            GrindstoneContainerRe.this.onCraftMatrixChanged(this);
        }
    };

    // 构造页面槽位
    private void Constuct(int windowIdIn, PlayerInventory playerInventoryIn, final IWorldPosCallable worldPosCallableIn) {
        this.addSlot(new Slot(this.inputInventory, 0, 49, 19) {
            /**
             * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
             */
            public boolean isItemValid(ItemStack stack) {
                return stack.isDamageable() || stack.getItem() == Items.ENCHANTED_BOOK || stack.isEnchanted();
            }
        });
        this.addSlot(new Slot(this.inputInventory, 1, 49, 40) {
            /**
             * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
             */
            public boolean isItemValid(ItemStack stack) {
                return stack.isDamageable() || stack.getItem() == Items.ENCHANTED_BOOK || stack.isEnchanted();
            }
        });
        this.addSlot(new Slot(this.outputInventory, 2, 129, 34) {
            /**
             * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
             */
            public boolean isItemValid(ItemStack stack) {
                return false;
            }

            public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack) {
                worldPosCallableIn.consume((p_216944_1_, p_216944_2_) -> {
                    int l = this.getEnchantmentXpFromInputs(p_216944_1_);

                    while (l > 0) {
                        int i1 = ExperienceOrbEntity.getXPSplit(l);
                        l -= i1;
                        p_216944_1_.addEntity(new ExperienceOrbEntity(p_216944_1_, p_216944_2_.getX(), (double) p_216944_2_.getY() + 0.5D, (double) p_216944_2_.getZ() + 0.5D, i1));
                    }

                    p_216944_1_.playEvent(1042, p_216944_2_, 0);
                });
                GrindstoneContainerRe.this.inputInventory.setInventorySlotContents(0, ItemStack.EMPTY);
                GrindstoneContainerRe.this.inputInventory.setInventorySlotContents(1, ItemStack.EMPTY);
                return stack;
            }

            /**
             * Returns the total amount of XP stored in all of the input slots of this container. The return value is
             * randomized, so that it returns between 50% and 100% of the total XP.
             */
            private int getEnchantmentXpFromInputs(World worldIn) {
                int l = 0;
                l = l + this.getEnchantmentXp(GrindstoneContainerRe.this.inputInventory.getStackInSlot(0));
                l = l + this.getEnchantmentXp(GrindstoneContainerRe.this.inputInventory.getStackInSlot(1));
                if (l > 0) {
                    int i1 = (int) Math.ceil((double) l / 2.0D);
                    return i1 + worldIn.rand.nextInt(i1);
                } else {
                    return 0;
                }
            }

            /**
             * Returns the total amount of XP stored in the enchantments of this stack.
             */
            private int getEnchantmentXp(ItemStack stack) {
                int l = 0;
                Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(stack);

                for (Map.Entry<Enchantment, Integer> entry : map.entrySet()) {
                    Enchantment enchantment = entry.getKey();
                    Integer integer = entry.getValue();
                    if (!enchantment.isCurse()) {
                        l += enchantment.getMinEnchantability(integer);
                    }
                }

                return l;
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
        ItemStack itemstack = this.inputInventory.getStackInSlot(0);
        ItemStack itemstack1 = this.inputInventory.getStackInSlot(1);
        boolean flag = !itemstack.isEmpty() || !itemstack1.isEmpty();
        boolean flag1 = !itemstack.isEmpty() && !itemstack1.isEmpty();
        if (!flag) {
            this.outputInventory.setInventorySlotContents(0, ItemStack.EMPTY);
        } else {
            boolean flag2 = !itemstack.isEmpty() && itemstack.getItem() != Items.ENCHANTED_BOOK && !itemstack.isEnchanted() || !itemstack1.isEmpty() && itemstack1.getItem() != Items.ENCHANTED_BOOK && !itemstack1.isEnchanted();
            if (itemstack.getCount() > 1 || itemstack1.getCount() > 1 || !flag1 && flag2) {
                this.outputInventory.setInventorySlotContents(0, ItemStack.EMPTY);
                this.detectAndSendChanges();
                return;
            }

            int j = 1;
            int i;
            ItemStack itemstack2;
            if (flag1) {
                if (itemstack.getItem() != itemstack1.getItem()) {
                    this.outputInventory.setInventorySlotContents(0, ItemStack.EMPTY);
                    this.detectAndSendChanges();
                    return;
                }

                Item item = itemstack.getItem();
                int k = itemstack.getMaxDamage() - itemstack.getDamage();
                int l = itemstack.getMaxDamage() - itemstack1.getDamage();
                int i1 = k + l + itemstack.getMaxDamage() * 5 / 100;
                i = Math.max(itemstack.getMaxDamage() - i1, 0);
                itemstack2 = this.copyEnchantments(itemstack, itemstack1);
                if (!itemstack2.isRepairable()) i = itemstack.getDamage();
                if (!itemstack2.isDamageable() || !itemstack2.isRepairable()) {
                    if (!ItemStack.areItemStacksEqual(itemstack, itemstack1)) {
                        this.outputInventory.setInventorySlotContents(0, ItemStack.EMPTY);
                        this.detectAndSendChanges();
                        return;
                    }

                    j = 2;
                }
            } else {
                boolean flag3 = !itemstack.isEmpty();
                i = flag3 ? itemstack.getDamage() : itemstack1.getDamage();
                itemstack2 = flag3 ? itemstack : itemstack1;
            }

            this.outputInventory.setInventorySlotContents(0, this.prepareNewItem(itemstack2, i, j));
        }

        this.detectAndSendChanges();
    }

    // 将物品 B 的附魔拷贝到 A，并作为 C 返回
    private ItemStack copyEnchantments(ItemStack copyTo, ItemStack copyFrom) {
        ItemStack itemstack = copyTo.copy();
        Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(copyFrom);

        for (Map.Entry<Enchantment, Integer> entry : map.entrySet()) {
            Enchantment enchantment = entry.getKey();
            if (!enchantment.isCurse() || EnchantmentHelper.getEnchantmentLevel(enchantment, itemstack) == 0) {
                itemstack.addEnchantment(enchantment, entry.getValue());
            }
        }

        return itemstack;
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

    // 接口: 当页面关闭时触发，需要尝试返还物品
    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        this.worldPosCallable.consume((p_217009_2_, p_217009_3_) -> this.clearContainer(playerIn, p_217009_2_, this.inputInventory));
    }

    // 接口: 当玩家使用 Shift+点击 点击某个物品以快速转移时触发，需要尝试转移物品
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            ItemStack itemstack2 = this.inputInventory.getStackInSlot(0);
            ItemStack itemstack3 = this.inputInventory.getStackInSlot(1);
            if (index == 2) {
                if (!this.mergeItemStack(itemstack1, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemstack1, itemstack);
            } else if (index != 0 && index != 1) {
                if (!itemstack2.isEmpty() && !itemstack3.isEmpty()) {
                    if (index < 30) {
                        if (!this.mergeItemStack(itemstack1, 30, 39, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (index < 39 && !this.mergeItemStack(itemstack1, 3, 30, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.mergeItemStack(itemstack1, 0, 2, false)) {
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

}
