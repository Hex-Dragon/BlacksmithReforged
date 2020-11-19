package com.hexdragon.blacksmithre.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RepairItemRecipe;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RepairItemRecipe.class)
public abstract class RepairItemRecipeMixin extends SpecialRecipe {
    public RepairItemRecipeMixin(ResourceLocation idIn) {
        super(idIn);
    }

    // 将使用普通合成来合并两个工具时的 5% 耐久度奖励取消
    // 返回 20 会使得耐久度再 +1，这是因为 MC 的 0 耐久度也可以用一次……
    @Redirect(method = "getCraftingResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMaxDamage()I", ordinal = 2))
    private int getMaxDamageMixin(ItemStack item) {
        return 20;
    }

}
