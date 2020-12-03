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
import net.minecraft.state.DirectionProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public class NetheriteAnvilBlock extends AnvilBlock {

    // 设置方块属性
    public NetheriteAnvilBlock() {
        super(AbstractBlock.Properties.create(Material.ANVIL, MaterialColor.IRON).setRequiresTool().hardnessAndResistance(50F, 1200.0F).harvestTool(ToolType.PICKAXE).harvestLevel(3).sound(SoundType.ANVIL));
        this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(RegMain.blockStateMaterial, 1));
    }

    // 设置其对应的物品
    public static class NetheriteAnvilItem extends BlockItem {
        public NetheriteAnvilItem(Block blockIn) {super(blockIn, new Item.Properties().group(ItemGroup.DECORATIONS));}
    }

    // 设置碰撞箱
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    private static final VoxelShape PART_BASE = Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 3.0D, 13.0D);
    private static final VoxelShape PART_LOWER = Block.makeCuboidShape(4.0D, 3.0D, 4.0D, 12.0D, 5.0D, 12.0D);
    private static final VoxelShape PART_MID_X = Block.makeCuboidShape(5.0D, 5.0D, 6.0D, 11.0D, 9.0D, 10.0D);
    private static final VoxelShape PART_UPPER_X = Block.makeCuboidShape(3.0D, 9.0D, 5.0D, 13.0D, 10.0D, 11.0D);
    private static final VoxelShape PART_TOP_X = Block.makeCuboidShape(0.0D, 10.0D, 3.0D, 16.0D, 15.0D, 13.0D);
    private static final VoxelShape PART_MID_Z = Block.makeCuboidShape(6.0D, 5.0D, 5.0D, 10.0D, 9.0D, 11.0D);
    private static final VoxelShape PART_UPPER_Z = Block.makeCuboidShape(5.0D, 9.0D, 3.0D, 11.0D, 10.0D, 13.0D);
    private static final VoxelShape PART_TOP_Z = Block.makeCuboidShape(3.0D, 10.0D, 0.0D, 13.0D, 15.0D, 16.0D);
    private static final VoxelShape X_AXIS_AABB = VoxelShapes.or(PART_BASE, PART_LOWER, PART_MID_X, PART_UPPER_X, PART_TOP_X);
    private static final VoxelShape Z_AXIS_AABB = VoxelShapes.or(PART_BASE, PART_LOWER, PART_MID_Z, PART_UPPER_Z, PART_TOP_Z);
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        Direction direction = state.get(FACING);
        return direction.getAxis() == Direction.Axis.X ? X_AXIS_AABB : Z_AXIS_AABB;
    }

    // 设置红石比较器输出
    public boolean hasComparatorInputOverride(BlockState state) {return true;}
    public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {return Container.calcRedstone(worldIn.getTileEntity(pos));}

}