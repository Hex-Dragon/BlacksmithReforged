package com.hexdragon.enchrebirth.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RepairItemRecipe;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// 用于测试的 Mixin 函数
@Mixin(RepairItemRecipe.class)
public abstract class MixinTest extends SpecialRecipe {
    public MixinTest(ResourceLocation idIn) {
        super(idIn);
    }

    @Redirect(method = "getCraftingResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMaxDamage()I", ordinal = 2))
    private int getNewMaxDamage(ItemStack item) {
        return 0;
    }

}
