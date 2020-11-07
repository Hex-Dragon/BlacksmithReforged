package com.hexdragon.enchrebirth.mixin;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RepairItemRecipe;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.List;
import java.util.Map;

// 用于测试的 Mixin 函数
@Mixin(RepairItemRecipe.class)
public abstract class MixinTest extends SpecialRecipe {
    public MixinTest(ResourceLocation idIn) {
        super(idIn);
    }

    @Overwrite
    public ItemStack getCraftingResult(CraftingInventory inv) {
        List<ItemStack> list = Lists.newArrayList();

        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);
            if (!itemstack.isEmpty()) {
                list.add(itemstack);
                if (list.size() > 1) {
                    ItemStack itemstack1 = list.get(0);
                    if (itemstack.getItem() != itemstack1.getItem() || itemstack1.getCount() != 1 || itemstack.getCount() != 1 || !itemstack1.isRepairable()) {
                        return ItemStack.EMPTY;
                    }
                }
            }
        }

        if (list.size() == 2) {
            ItemStack itemstack3 = list.get(0);
            ItemStack itemstack4 = list.get(1);
            if (itemstack3.getItem() == itemstack4.getItem() && itemstack3.getCount() == 1 && itemstack4.getCount() == 1 && itemstack3.isRepairable()) {
                Item item = itemstack3.getItem();
                int j = itemstack3.getMaxDamage() - itemstack3.getDamage();
                int k = itemstack3.getMaxDamage() - itemstack4.getDamage();
                int i1 = itemstack3.getMaxDamage() - j - k;
                if (i1 < 0) {
                    i1 = 0;
                }

                ItemStack itemstack2 = new ItemStack(itemstack3.getItem());
                itemstack2.setDamage(i1);
                Map<Enchantment, Integer> map = Maps.newHashMap();
                Map<Enchantment, Integer> map1 = EnchantmentHelper.getEnchantments(itemstack3);
                Map<Enchantment, Integer> map2 = EnchantmentHelper.getEnchantments(itemstack4);
                Registry.ENCHANTMENT.stream().filter(Enchantment::isCurse).forEach((curse) -> {
                    int j1 = Math.max(map1.getOrDefault(curse, 0), map2.getOrDefault(curse, 0));
                    if (j1 > 0) {
                        map.put(curse, j1);
                    }

                });
                if (!map.isEmpty()) {
                    EnchantmentHelper.setEnchantments(map, itemstack2);
                }

                return itemstack2;
            }
        }

        return ItemStack.EMPTY;
    }

}
