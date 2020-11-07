package com.hexdragon.enchrebirth.reg;

import com.hexdragon.enchrebirth.block.GrindstoneContainerRe;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.registry.Registry;

public class Reg {

    public static final ContainerType<GrindstoneContainerRe> containerGrindstone = Registry.register(Registry.MENU, "grindstone", new ContainerType<>(GrindstoneContainerRe::new));

    // 物品
    // public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, EnchRebirth.MODID);
    // public static final RegistryObject<Item> itemOriginitePrime = ITEMS.register(OriginitePrime.name, OriginitePrime::new);

    // 方块
    // public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, EnchRebirth.MODID);
    // public static final RegistryObject<Block> blockOriginitePrimeOre = BLOCKS.register(OriginitePrimeOre.name, OriginitePrimeOre::new);
    // public static final RegistryObject<Item> itemOriginitePrimeOre = ITEMS.register(OriginitePrimeOre.name, OriginitePrimeOre.OriginitePrimeOreItem::new);

}
