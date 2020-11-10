package com.hexdragon.util.item;

import com.sun.istack.internal.NotNull;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;

import java.util.Map;
import java.util.stream.Collectors;

public class EnchantmentHelperRe {

    // 获取物品的所有诅咒附魔
    public static Map<Enchantment, Integer> getCurseEnchantments(@NotNull ItemStack stack) {
        return EnchantmentHelper.getEnchantments(stack).entrySet().stream().filter((pair) -> pair.getKey().isCurse()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    // 获取物品的所有非诅咒附魔
    public static Map<Enchantment, Integer> getNonCurseEnchantments(@NotNull ItemStack stack) {
        return EnchantmentHelper.getEnchantments(stack).entrySet().stream().filter((pair) -> !pair.getKey().isCurse()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
