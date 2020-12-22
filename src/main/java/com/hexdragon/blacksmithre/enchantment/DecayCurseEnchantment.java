package com.hexdragon.blacksmithre.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;

// 腐朽诅咒：拥有这个附魔的工具在 修补、合并、从经验修补获得耐久度 时增加的耐久度下降 70%
// 测试Push
public class DecayCurseEnchantment extends Enchantment {
    public DecayCurseEnchantment() {super(Rarity.VERY_RARE, EnchantmentType.BREAKABLE, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND});}

    public int getMinEnchantability(int enchantmentLevel) {return 20;}
    public int getMaxEnchantability(int enchantmentLevel) {return 50;}
    public int getMaxLevel() {return 1;}
    public boolean isTreasureEnchantment() {return true;}
    public boolean isCurse() {return true;}

}
