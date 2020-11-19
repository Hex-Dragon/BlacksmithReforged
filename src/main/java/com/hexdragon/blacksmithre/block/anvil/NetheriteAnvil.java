package com.hexdragon.blacksmithre.block.anvil;

import com.hexdragon.blacksmithre.registry.RegMain;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Direction;
import net.minecraftforge.common.ToolType;

public class NetheriteAnvil extends AnvilBlock {

    public NetheriteAnvil() {
        super(AbstractBlock.Properties.create(Material.ANVIL, MaterialColor.IRON).setRequiresTool().hardnessAndResistance(50F, 1200.0F).harvestTool(ToolType.PICKAXE).harvestLevel(3).sound(SoundType.ANVIL));
        this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(RegMain.blockStateMaterial, 1));
    }

    public static class NetheriteAnvilItem extends BlockItem {
        public NetheriteAnvilItem(Block blockIn) {
            super(blockIn, new Item.Properties().group(ItemGroup.DECORATIONS));
        }
    }

}