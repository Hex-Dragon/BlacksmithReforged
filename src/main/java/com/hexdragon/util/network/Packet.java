package com.hexdragon.util.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

public abstract class Packet {

    // 编码方法（解码方法让子类自己写，Java 不允许 Override 一个 Static 方法）
    public abstract void encoder(PacketBuffer buf);

    // 接收到数据包的事件处理
    public void onReceivePacket(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            NetworkEvent.Context context = ctx.get();
            if (context.getDirection() == NetworkDirection.PLAY_TO_SERVER) {
                onServerReceivePacket(context);
            } else {
                onClientReceivePacket(context);
            }
        });
        ctx.get().setPacketHandled(true);
    }

    // 接收到数据包的处理
    public abstract void onServerReceivePacket(NetworkEvent.Context ctx);

    public abstract void onClientReceivePacket(NetworkEvent.Context ctx);

    // 将数据包发送至特定对象
    // 如果要扩展支持的对象范围，使用不同的 PacketDistributor 即可
    public void sendToServer() {PacketManager.channel.sendToServer(this);}
    public void sendToPlayer(ServerPlayerEntity player) {PacketManager.channel.send(PacketDistributor.PLAYER.with(() -> player), this); }

}
