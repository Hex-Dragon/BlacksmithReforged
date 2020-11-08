package com.hexdragon.enchrebirth.block;

import com.hexdragon.enchrebirth.registry.RegMain;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.AbstractRepairContainer;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.Map;

public class AnvilContainerRe extends AbstractRepairContainer {
    private static final Logger LOGGER = LogManager.getLogger();
    public int materialCost;
    private final IntReferenceHolder maximumCost = IntReferenceHolder.single();

    public AnvilContainerRe(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, IWorldPosCallable.DUMMY);
    }

    public AnvilContainerRe(int id, PlayerInventory playerInventory, IWorldPosCallable worldPosCallable) {
        super(RegMain.containerAnvil.get(), id, playerInventory, worldPosCallable);
        this.trackInt(this.maximumCost);
    }

    protected boolean func_230302_a_(BlockState p_230302_1_) {
        return p_230302_1_.isIn(BlockTags.ANVIL);
    }

    protected boolean func_230303_b_(PlayerEntity p_230303_1_, boolean p_230303_2_) {
        return (p_230303_1_.abilities.isCreativeMode || p_230303_1_.experienceLevel >= this.maximumCost.get()) && this.maximumCost.get() > 0;
    }

    protected ItemStack func_230301_a_(PlayerEntity p_230301_1_, ItemStack p_230301_2_) {
        if (!p_230301_1_.abilities.isCreativeMode) {
            p_230301_1_.addExperienceLevel(-this.maximumCost.get());
        }

        float breakChance = net.minecraftforge.common.ForgeHooks.onAnvilRepair(p_230301_1_, p_230301_2_, AnvilContainerRe.this.field_234643_d_.getStackInSlot(0), AnvilContainerRe.this.field_234643_d_.getStackInSlot(1));

        this.field_234643_d_.setInventorySlotContents(0, ItemStack.EMPTY);
        if (this.materialCost > 0) {
            ItemStack itemstack = this.field_234643_d_.getStackInSlot(1);
            if (!itemstack.isEmpty() && itemstack.getCount() > this.materialCost) {
                itemstack.shrink(this.materialCost);
                this.field_234643_d_.setInventorySlotContents(1, itemstack);
            } else {
                this.field_234643_d_.setInventorySlotContents(1, ItemStack.EMPTY);
            }
        } else {
            this.field_234643_d_.setInventorySlotContents(1, ItemStack.EMPTY);
        }

        this.maximumCost.set(0);
        this.field_234644_e_.consume((p_234633_1_, p_234633_2_) -> {
            BlockState blockstate = p_234633_1_.getBlockState(p_234633_2_);
            if (!p_230301_1_.abilities.isCreativeMode && blockstate.isIn(BlockTags.ANVIL) && p_230301_1_.getRNG().nextFloat() < breakChance) {
                BlockState blockstate1 = AnvilBlock.damage(blockstate);
                if (blockstate1 == null) {
                    p_234633_1_.removeBlock(p_234633_2_, false);
                    p_234633_1_.playEvent(1029, p_234633_2_, 0);
                } else {
                    p_234633_1_.setBlockState(p_234633_2_, blockstate1, 2);
                    p_234633_1_.playEvent(1030, p_234633_2_, 0);
                }
            } else {
                p_234633_1_.playEvent(1030, p_234633_2_, 0);
            }

        });
        return p_230301_2_;
    }

    /**
     * called when the Anvil Input Slot changes, calculates the new result and puts it in the output slot
     */
    public void updateRepairOutput() {
        ItemStack itemstack = this.field_234643_d_.getStackInSlot(0);
        this.maximumCost.set(1);
        int i = 0;
        int j = 0;
        int k = 0;
        if (itemstack.isEmpty()) {
            this.field_234642_c_.setInventorySlotContents(0, ItemStack.EMPTY);
            this.maximumCost.set(0);
        } else {
            ItemStack itemstack1 = itemstack.copy();
            ItemStack itemstack2 = this.field_234643_d_.getStackInSlot(1);
            Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemstack1);
            j = j + itemstack.getRepairCost() + (itemstack2.isEmpty() ? 0 : itemstack2.getRepairCost());
            this.materialCost = 0;
            boolean flag = false;

            if (!itemstack2.isEmpty()) {
                if (!onAnvilChange(this, itemstack, itemstack2, field_234642_c_, itemstack.getDisplayName().getString(), j))
                    return;
                flag = itemstack2.getItem() == Items.ENCHANTED_BOOK && !EnchantedBookItem.getEnchantments(itemstack2).isEmpty();
                if (itemstack1.isDamageable() && itemstack1.getItem().getIsRepairable(itemstack, itemstack2)) {
                    int l2 = Math.min(itemstack1.getDamage(), itemstack1.getMaxDamage() / 4);
                    if (l2 <= 0) {
                        this.field_234642_c_.setInventorySlotContents(0, ItemStack.EMPTY);
                        this.maximumCost.set(0);
                        return;
                    }

                    int i3;
                    for (i3 = 0; l2 > 0 && i3 < itemstack2.getCount(); ++i3) {
                        int j3 = itemstack1.getDamage() - l2;
                        itemstack1.setDamage(j3);
                        ++i;
                        l2 = Math.min(itemstack1.getDamage(), itemstack1.getMaxDamage() / 4);
                    }

                    this.materialCost = i3;
                } else {
                    if (!flag && (itemstack1.getItem() != itemstack2.getItem() || !itemstack1.isDamageable())) {
                        this.field_234642_c_.setInventorySlotContents(0, ItemStack.EMPTY);
                        this.maximumCost.set(0);
                        return;
                    }

                    if (itemstack1.isDamageable() && !flag) {
                        int l = itemstack.getMaxDamage() - itemstack.getDamage();
                        int i1 = itemstack2.getMaxDamage() - itemstack2.getDamage();
                        int j1 = i1 + itemstack1.getMaxDamage() * 12 / 100;
                        int k1 = l + j1;
                        int l1 = itemstack1.getMaxDamage() - k1;
                        if (l1 < 0) {
                            l1 = 0;
                        }

                        if (l1 < itemstack1.getDamage()) {
                            itemstack1.setDamage(l1);
                            i += 2;
                        }
                    }

                    Map<Enchantment, Integer> map1 = EnchantmentHelper.getEnchantments(itemstack2);
                    boolean flag2 = false;
                    boolean flag3 = false;

                    for (Enchantment enchantment1 : map1.keySet()) {
                        if (enchantment1 != null) {
                            int i2 = map.getOrDefault(enchantment1, 0);
                            int j2 = map1.get(enchantment1);
                            j2 = i2 == j2 ? j2 + 1 : Math.max(j2, i2);
                            boolean flag1 = enchantment1.canApply(itemstack);
                            if (this.field_234645_f_.abilities.isCreativeMode || itemstack.getItem() == Items.ENCHANTED_BOOK) {
                                flag1 = true;
                            }

                            for (Enchantment enchantment : map.keySet()) {
                                if (enchantment != enchantment1 && !enchantment1.isCompatibleWith(enchantment)) {
                                    flag1 = false;
                                    ++i;
                                }
                            }

                            if (!flag1) {
                                flag3 = true;
                            } else {
                                flag2 = true;
                                if (j2 > enchantment1.getMaxLevel()) {
                                    j2 = enchantment1.getMaxLevel();
                                }

                                map.put(enchantment1, j2);
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

                                if (flag) {
                                    k3 = Math.max(1, k3 / 2);
                                }

                                i += k3 * j2;
                                if (itemstack.getCount() > 1) {
                                    i = 40;
                                }
                            }
                        }
                    }

                    if (flag3 && !flag2) {
                        this.field_234642_c_.setInventorySlotContents(0, ItemStack.EMPTY);
                        this.maximumCost.set(0);
                        return;
                    }
                }
            }

            if (flag && !itemstack1.isBookEnchantable(itemstack2)) itemstack1 = ItemStack.EMPTY;

            this.maximumCost.set(j + i);
            if (i <= 0) {
                itemstack1 = ItemStack.EMPTY;
            }

            if (k == i && k > 0 && this.maximumCost.get() >= 40) {
                this.maximumCost.set(39);
            }

            if (this.maximumCost.get() >= 40 && !this.field_234645_f_.abilities.isCreativeMode) {
                itemstack1 = ItemStack.EMPTY;
            }

            if (!itemstack1.isEmpty()) {
                int k2 = itemstack1.getRepairCost();
                if (!itemstack2.isEmpty() && k2 < itemstack2.getRepairCost()) {
                    k2 = itemstack2.getRepairCost();
                }

                if (k != i || k == 0) {
                    k2 = getNewRepairCost(k2);
                }

                itemstack1.setRepairCost(k2);
                EnchantmentHelper.setEnchantments(map, itemstack1);
            }

            this.field_234642_c_.setInventorySlotContents(0, itemstack1);
            this.detectAndSendChanges();
        }
    }

    // 从 ForgeHooks 中复制
    public static boolean onAnvilChange(AnvilContainerRe container, @Nonnull ItemStack left, @Nonnull ItemStack right, IInventory outputSlot, String name, int baseCost) {
        AnvilUpdateEvent e = new AnvilUpdateEvent(left, right, name, baseCost);
        if (MinecraftForge.EVENT_BUS.post(e)) return false;
        if (e.getOutput().isEmpty()) return true;

        outputSlot.setInventorySlotContents(0, e.getOutput());
        container.setMaximumCost(e.getCost());
        container.materialCost = e.getMaterialCost();
        return false;
    }

    public static int getNewRepairCost(int oldRepairCost) {
        return oldRepairCost * 2 + 1;
    }

    /**
     * Get's the maximum xp cost
     */
    @OnlyIn(Dist.CLIENT)
    public int getMaximumCost() {
        return this.maximumCost.get();
    }

    public void setMaximumCost(int value) {
        this.maximumCost.set(value);
    }
}
