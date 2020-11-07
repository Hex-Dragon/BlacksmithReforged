package com.hexdragon.enchrebirth.reg;

import com.hexdragon.enchrebirth.block.GrindstoneScreenRe;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RegClient {

    static {
        ScreenManager.registerFactory(Reg.containerGrindstone, GrindstoneScreenRe::new);
    }

}
