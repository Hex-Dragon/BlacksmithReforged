package com.hexdragon.enchrebirth.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;

// 腐朽诅咒：拥有这个附魔的工具在 修补、合并、从经验修补获得耐久度 时增加的耐久度下降 70%
// TODO : 让腐朽诅咒可以作用于经验修补附魔，和物品合并的合成配方（Mixin 大概不够用了，这可能需要写一个新的 RepairItemRecipe）
// TODO : 削弱经验修补附魔：每点经验值恢复 1 点耐久而不是 2 点
// TODO : 修改耐久附魔：每次使用时消耗耐久度的概率将降至 30+(70/(0.5+等级))% （即耐久度每次都有 23%/42%/50% 的概率不会减少），这让盔甲上的耐久附魔变得更强，但让普通物品上的耐久附魔变得更弱了
public class DecayCurseEnchantment extends Enchantment {
    public DecayCurseEnchantment() {super(Rarity.VERY_RARE, EnchantmentType.BREAKABLE, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND});}

    public int getMinEnchantability(int enchantmentLevel) {return 20;}
    public int getMaxEnchantability(int enchantmentLevel) {return 50;}
    public int getMaxLevel() {return 1;}
    public boolean isTreasureEnchantment() {return true;}
    public boolean isCurse() {return true;}

}
