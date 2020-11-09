package com.hexdragon.enchrebirth.mixin;

import com.hexdragon.enchrebirth.Main;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.minecraft.HttpMinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(YggdrasilMinecraftSessionService.class)
public abstract class YggdrasilMixin extends HttpMinecraftSessionService {
    protected YggdrasilMixin(HttpAuthenticationService authenticationService) { super(authenticationService); }

    // 强制结束 MC 的玩家档案获取，以避免进入存档时卡死
    @Inject(method = "fillGameProfile", at = @At(value = "HEAD"), cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void fillGameProfile(GameProfile profile, boolean requireSecure, CallbackInfoReturnable<GameProfile> cir) {
        Main.LOGGER.warn("Canceled game profile filling.");
        cir.setReturnValue(profile);
        cir.cancel();
    }

}

