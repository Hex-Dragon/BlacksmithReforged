package com.hexdragon.blacksmithre.mixin.item;

import com.hexdragon.blacksmithre.item.name_tag.NameTagScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.NameTagItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(NameTagItem.class)
public abstract class NameTagItemMixin extends Item {
    public NameTagItemMixin(Properties properties) {super(properties);}

    // 接管命名牌的右键事件，打开 GUI
    @Override public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (worldIn.isRemote) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                ITextComponent defaultName = playerIn.getHeldItem(handIn).getDisplayName();
                Minecraft.getInstance().displayGuiScreen(new NameTagScreen(defaultName.getString(), handIn));
            });
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

}
