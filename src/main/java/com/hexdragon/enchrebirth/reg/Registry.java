package com.hexdragon.enchrebirth.reg;

import com.hexdragon.enchrebirth.EnchRebirth;
import com.hexdragon.enchrebirth.block.GrindstoneContainerRe;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class Registry {

    // 容器
    public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, EnchRebirth.MODID);
    public static final RegistryObject<ContainerType<GrindstoneContainerRe>> containerGrindstone = CONTAINERS.register("grindstone", () -> IForgeContainerType.create((int windowId, PlayerInventory inv, PacketBuffer data) -> new GrindstoneContainerRe(windowId, inv)));

}
