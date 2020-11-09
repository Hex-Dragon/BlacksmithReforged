package com.hexdragon.enchrebirth.block;

import com.hexdragon.enchrebirth.registry.RegMain;
import net.minecraft.tileentity.TileEntity;

public class AnvilTileEntity extends TileEntity {
    public AnvilTileEntity() {
        super(RegMain.tileEntityAnvil.get());
    }
}
