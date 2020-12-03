package com.hexdragon.corere.item;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.common.ToolType;
import org.lwjgl.system.CallbackI;

public class ItemHelperRe {

    // 完全修复某个可损坏的物品大致需要的珍贵原料数，返回值并不准确，只适合用于估计
    public static float getDamageableItemRepairCost(ItemStack itemStack) {
        // 一个下界合金视作 3 个原材料
        boolean isNetherite = itemStack.getItem().getIsRepairable(itemStack, new ItemStack(Items.NETHERITE_INGOT));
        return getDamageableItemMaterialCost(itemStack) / (isNetherite ? 3f : 1f);
    }

    // 合成某个可损坏的物品大致需要的珍贵原料数（下界合金为其对应的钻石物品），返回值并不准确，只适合用于估计
    public static int getDamageableItemMaterialCost(ItemStack itemStack) {
        Item item = itemStack.getItem();
        // 根据工具种类判断
        int maxCost = 0;
        for (ToolType toolType : itemStack.getToolTypes()) {
            switch (toolType.getName()) {
                case "axe":
                    maxCost = Math.max(maxCost, 3); break;
                case "hoe":
                    maxCost = Math.max(maxCost, 2); break;
                case "pickaxe":
                    maxCost = Math.max(maxCost, 3); break;
                case "shovel":
                    maxCost = Math.max(maxCost, 1); break;
            }
        }
        if (maxCost > 0) return maxCost;
        // 根据护甲种类判断
        if (item instanceof ArmorItem) {
            switch (((ArmorItem) item).getEquipmentSlot().getName()) {
                case "feet":
                    return 4;
                case "legs":
                    return 7;
                case "chest":
                    return 8;
                case "head":
                    return 5;
            }
        }
        // 根据 ID 判断
        String id = item.getRegistryName().getPath();
        if (id.contains("sword")) return 2;
        if (id.contains("horse_armor")) return 7;
        // 根据特定种类判断
        if (item == Items.FLINT_AND_STEEL || item == Items.SHIELD) return 1;
        if (item == Items.FISHING_ROD || item.isCrossbow(itemStack)) return 2;
        // 如果本来就应该是 3 就不作判断
        // 如果未找到则视作 3
        return 3;
    }

}
