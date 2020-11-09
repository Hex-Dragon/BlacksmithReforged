package com.hexdragon.enchrebirth.block.anvil;

import com.hexdragon.enchrebirth.registry.RegMain;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

public abstract class NetheriteAnvilItem extends BlockItem {
    public NetheriteAnvilItem(Block block) {
        super(block, new Item.Properties().group(ItemGroup.DECORATIONS));
    }

    // 使用物品的翻译而不是直接使用方块名
    @Override public String getTranslationKey() { return this.getDefaultTranslationKey(); }

    // 要求以 material = 1 的方式放置
    @Override protected BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState blockstate = this.getBlock().getStateForPlacement(context).with(RegMain.blockStateMaterial, 1);
        return blockstate != null && this.canPlace(context, blockstate) ? blockstate : null;
    }

    // 三个损坏等级的分物品
    public static class PerfectNetheriteAnvilItem extends NetheriteAnvilItem {
        public PerfectNetheriteAnvilItem() { super(Blocks.ANVIL); }
    }
    public static class ChippedNetheriteAnvilItem extends NetheriteAnvilItem {
        public ChippedNetheriteAnvilItem() { super(Blocks.CHIPPED_ANVIL); }
    }
    public static class DamagedNetheriteAnvilItem extends NetheriteAnvilItem {
        public DamagedNetheriteAnvilItem() { super(Blocks.DAMAGED_ANVIL); }
    }

}