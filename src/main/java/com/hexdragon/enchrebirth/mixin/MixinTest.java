package com.hexdragon.enchrebirth.mixin;

import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.FrameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

// 用于测试的 Mixin 函数
@Mixin(DisplayInfo.class)
public abstract class MixinTest {

    @Overwrite
    public FrameType getFrame() {
        return FrameType.CHALLENGE;
    }

}
