package com.hexdragon.enchrebirth.mixin;

import com.hexdragon.enchrebirth.block.GrindstoneContainerRe;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ContainerType.class)
public abstract class ContainerTypeMixin<T extends Container> extends net.minecraftforge.registries.ForgeRegistryEntry<net.minecraft.inventory.container.ContainerType<?>> implements net.minecraftforge.common.extensions.IForgeContainerType<T> {

    @Inject(method = "register", at = @At(value = "HEAD"), cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private static <T extends Container> void register(String key, ContainerType.IFactory<T> factory, CallbackInfoReturnable<ContainerType<T>> cir) {
        if (key == "grindstone") {
            cir.setReturnValue(Registry.register(Registry.MENU, key, (ContainerType<T>) new ContainerType<>(GrindstoneContainerRe::new)));
            cir.cancel();
        }
    }

}
