package com.hexdragon.enchrebirth.registry;

import com.hexdragon.enchrebirth.Main;
import com.hexdragon.enchrebirth.block.AnvilContainerRe;
import com.hexdragon.enchrebirth.block.AnvilTileEntity;
import com.hexdragon.enchrebirth.block.GrindstoneContainerRe;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RegMain {

    // 容器
    public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, Main.MODID);
    public static final RegistryObject<ContainerType<GrindstoneContainerRe>> containerGrindstone = CONTAINERS.register("grindstone", () -> IForgeContainerType.create((int windowId, PlayerInventory inv, PacketBuffer data) -> new GrindstoneContainerRe(windowId, inv)));
    public static final RegistryObject<ContainerType<AnvilContainerRe>> containerAnvil = CONTAINERS.register("anvil", () -> IForgeContainerType.create((int windowId, PlayerInventory inv, PacketBuffer data) -> new AnvilContainerRe(windowId, inv)));

    // TileEntity
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Main.MODID);
    public static RegistryObject<TileEntityType<AnvilTileEntity>> tileEntityAnvil = TILE_ENTITIES.register("obsidian_ter_tileentity", () -> TileEntityType.Builder.create(AnvilTileEntity::new, Blocks.ANVIL).build(null));

}
