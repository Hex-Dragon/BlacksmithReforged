package com.hexdragon.blacksmithre.block.anvil;

import com.hexdragon.blacksmithre.registry.RegMain;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
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

    // 设置红石比较器输出
    public boolean hasComparatorInputOverride(BlockState state) {return true;}
    public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {return Container.calcRedstone(worldIn.getTileEntity(pos));}

}