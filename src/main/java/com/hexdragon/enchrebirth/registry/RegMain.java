package com.hexdragon.enchrebirth.registry;

import com.hexdragon.enchrebirth.Main;
import com.hexdragon.enchrebirth.block.GrindstoneContainerRe;
import com.hexdragon.enchrebirth.block.anvil.AnvilContainerRe;
import com.hexdragon.enchrebirth.block.anvil.AnvilTileEntity;
import com.hexdragon.enchrebirth.block.anvil.NetheriteAnvil;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.network.PacketBuffer;
import net.minecraft.state.IntegerProperty;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class RegMain {

    // 方块
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Main.MODID);
    public static final RegistryObject<Block> blockPerfectNetheriteAnvil = BLOCKS.register("netherite_anvil", NetheriteAnvil::new);
    public static final RegistryObject<Block> blockChippedNetheriteAnvil = BLOCKS.register("chipped_netherite_anvil", NetheriteAnvil::new);
    public static final RegistryObject<Block> blockDamagedNetheriteAnvil = BLOCKS.register("damaged_netherite_anvil", NetheriteAnvil::new);

    // 物品
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Main.MODID);
    public static final RegistryObject<Item> itemPerfectNetheriteAnvil = ITEMS.register("netherite_anvil", () -> new NetheriteAnvil.NetheriteAnvilItem(blockPerfectNetheriteAnvil.get()));
    public static final RegistryObject<Item> itemChippedNetheriteAnvil = ITEMS.register("chipped_netherite_anvil", () -> new NetheriteAnvil.NetheriteAnvilItem(blockChippedNetheriteAnvil.get()));
    public static final RegistryObject<Item> itemDamagedNetheriteAnvil = ITEMS.register("damaged_netherite_anvil", () -> new NetheriteAnvil.NetheriteAnvilItem(blockDamagedNetheriteAnvil.get()));

    // Container
    public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, Main.MODID);
    public static final RegistryObject<ContainerType<GrindstoneContainerRe>> containerGrindstone = CONTAINERS.register("grindstone", () -> IForgeContainerType.create((int windowId, PlayerInventory inv, PacketBuffer data) -> new GrindstoneContainerRe(windowId, inv)));
    public static final RegistryObject<ContainerType<AnvilContainerRe>> containerAnvil = CONTAINERS.register("anvil", () -> IForgeContainerType.create((int windowId, PlayerInventory inv, PacketBuffer data) -> new AnvilContainerRe(windowId, inv)));

    // TileEntity
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Main.MODID);
    public static RegistryObject<TileEntityType<AnvilTileEntity.PerfectAnvilTileEntity>> tileEntityPerfectAnvil = TILE_ENTITIES.register("anvil", () -> TileEntityType.Builder.create(AnvilTileEntity.PerfectAnvilTileEntity::new, Blocks.ANVIL).build(null));
    public static RegistryObject<TileEntityType<AnvilTileEntity.ChippedAnvilTileEntity>> tileEntityChippedAnvil = TILE_ENTITIES.register("chipped_anvil", () -> TileEntityType.Builder.create(AnvilTileEntity.ChippedAnvilTileEntity::new, Blocks.CHIPPED_ANVIL).build(null));
    public static RegistryObject<TileEntityType<AnvilTileEntity.DamagedAnvilTileEntity>> tileEntityDamagedAnvil = TILE_ENTITIES.register("damaged_anvil", () -> TileEntityType.Builder.create(AnvilTileEntity.DamagedAnvilTileEntity::new, Blocks.DAMAGED_ANVIL).build(null));
    public static RegistryObject<TileEntityType<AnvilTileEntity.PerfectNetheriteAnvilTileEntity>> tileEntityNetheritePerfectAnvil = TILE_ENTITIES.register("netherite_anvil", () -> TileEntityType.Builder.create(AnvilTileEntity.PerfectNetheriteAnvilTileEntity::new, blockPerfectNetheriteAnvil.get()).build(null));
    public static RegistryObject<TileEntityType<AnvilTileEntity.ChippedNetheriteAnvilTileEntity>> tileEntityNetheriteChippedAnvil = TILE_ENTITIES.register("chipped_netherite_anvil", () -> TileEntityType.Builder.create(AnvilTileEntity.ChippedNetheriteAnvilTileEntity::new, blockChippedNetheriteAnvil.get()).build(null));
    public static RegistryObject<TileEntityType<AnvilTileEntity.DamagedNetheriteAnvilTileEntity>> tileEntityNetheriteDamagedAnvil = TILE_ENTITIES.register("damaged_netherite_anvil", () -> TileEntityType.Builder.create(AnvilTileEntity.DamagedNetheriteAnvilTileEntity::new, blockDamagedNetheriteAnvil.get()).build(null));

    // BlockState
    public static final IntegerProperty blockStateMaterial = IntegerProperty.create("material", 0, 1);

}
