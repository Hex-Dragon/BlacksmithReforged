package com.hexdragon.util.network;

import com.hexdragon.enchrebirth.Main;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class StringPacket {
    private byte hand;
    private String message;

    public StringPacket(PacketBuffer buffer) {
        hand = buffer.readByte();
        message = buffer.readString(Short.MAX_VALUE);
    }

    public StringPacket(String message, byte hand) {
        this.message = message;
        this.hand = hand;
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeByte(this.hand);
        buf.writeString(this.message);
    }

    public void handler(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            ItemStack item = player.getHeldItem(this.hand == 0 ? Hand.MAIN_HAND : Hand.OFF_HAND);
            if (item.getItem() == Items.NAME_TAG) {
                item.setDisplayName(new StringTextComponent(this.message));
            } else {
                Main.LOGGER.warn("Got a wrong name tag packet: " + this.message);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
