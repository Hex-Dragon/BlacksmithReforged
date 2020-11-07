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

public class Reg {

    public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, EnchRebirth.MODID);
    public static final RegistryObject<ContainerType<GrindstoneContainerRe>> containerGrindstone = CONTAINERS.register("grindstone", () -> IForgeContainerType.create((int windowId, PlayerInventory inv, PacketBuffer data) -> new GrindstoneContainerRe(windowId, inv)));

    // 物品
    // public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, EnchRebirth.MODID);
    // public static final RegistryObject<Item> itemOriginitePrime = ITEMS.register(OriginitePrime.name, OriginitePrime::new);

    // 方块
    // public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, EnchRebirth.MODID);
    // public static final RegistryObject<Block> blockOriginitePrimeOre = BLOCKS.register(OriginitePrimeOre.name, OriginitePrimeOre::new);
    // public static final RegistryObject<Item> itemOriginitePrimeOre = ITEMS.register(OriginitePrimeOre.name, OriginitePrimeOre.OriginitePrimeOreItem::new);

}
