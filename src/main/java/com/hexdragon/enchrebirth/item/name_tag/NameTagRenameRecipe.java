package com.hexdragon.enchrebirth.item.name_tag;

import com.hexdragon.enchrebirth.registry.RegMain;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

// 将命名牌与任意其他物品合成，可以消耗一个命名牌将其重命名
// TODO : <重要> <难以完成> 似乎命名牌的这个特殊配方注册失败了，虽然有注册代码但是连新建实例都没触发过
public class NameTagRenameRecipe extends SpecialRecipe {
    public NameTagRenameRecipe(ResourceLocation idIn) { super(idIn); }
    public IRecipeSerializer<?> getSerializer() {return RegMain.recipeNameTagRename.get();}

    // 检查物品是否符合该配方
    public boolean matches(CraftingInventory inv, World worldIn) {
        int nameTagFound = 0, totalFound = 0;
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);
            if (itemstack.isEmpty()) continue;
            totalFound += 1;
            if (itemstack.getItem() == Items.NAME_TAG) nameTagFound += 1;
        }
        // 有两个物品，其中一个为命名牌
        return nameTagFound == 1 && totalFound == 2;
    }

    // 获取配方结果
    public ItemStack getCraftingResult(CraftingInventory inv) {
        // 获取目标物品与新名称
        ItemStack target = null;
        ITextComponent newName = null;
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);
            if (itemstack.isEmpty()) continue;
            if (itemstack.getItem() == Items.NAME_TAG) {
                newName = itemstack.getDisplayName();
            } else {
                target = itemstack.copy();
            }
        }
        // 设置名称并返回
        if (target == null || newName == null) return ItemStack.EMPTY;
        target.setDisplayName(newName);
        return target;
    }

    // 配方占用的格子数
    public boolean canFit(int width, int height) {
        return width * height >= 2;
    }

}
