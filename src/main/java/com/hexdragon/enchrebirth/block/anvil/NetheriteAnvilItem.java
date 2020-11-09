package com.hexdragon.enchrebirth.block.anvil;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

public abstract class NetheriteAnvilItem extends BlockItem {
    public NetheriteAnvilItem(Block block) {
        super(block, new Item.Properties().group(ItemGroup.DECORATIONS));
    }


    public static class PerfectNetheriteAnvilItem extends NetheriteAnvilItem {
        public PerfectNetheriteAnvilItem() {
            super(Blocks.ANVIL);
        }
    }
    public static class ChippedNetheriteAnvilItem extends NetheriteAnvilItem {
        public ChippedNetheriteAnvilItem() {
            super(Blocks.CHIPPED_ANVIL);
        }
    }
    public static class DamagedNetheriteAnvilItem extends NetheriteAnvilItem {
        public DamagedNetheriteAnvilItem() {
            super(Blocks.DAMAGED_ANVIL);
        }
    }
}