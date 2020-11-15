package com.hexdragon.util.network;

import com.hexdragon.enchrebirth.Main;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public abstract class Packet {

    // 编码方法（解码方法让子类自己写，Java 不允许 Override 一个 Static 方法）
    public abstract void encoder(PacketBuffer buf);

    // 接收到数据包的事件处理
    public void onReceivePacket(Supplier<NetworkEvent.Context> ctx) {
        final NetworkEvent.Context context = ctx.get();
        if (context.getDirection() == NetworkDirection.PLAY_TO_SERVER) {
            context.enqueueWork(() -> onServerReceivePacket(context.getSender()));
        } else if (context.getDirection() == NetworkDirection.PLAY_TO_SERVER) {
            context.enqueueWork(() -> onClientReceivePacket(context.getSender()));
        } else {
            Main.LOGGER.warn("出现意料外的数据包传递方向");
        }
        context.setPacketHandled(true);
    }

    // 接收到数据包的处理
    public void onServerReceivePacket(@Nullable ServerPlayerEntity senderPlayer) {Main.LOGGER.warn("服务端接收到了未经子类处理的数据包，它可能应该发送给客户端");}
    public void onClientReceivePacket(@Nullable ServerPlayerEntity senderPlayer) {Main.LOGGER.warn("客户端接收到了未经子类处理的数据包，它可能应该发送给服务端");}

    // 将数据包发送至特定对象
    // 如果要扩展支持的对象范围，使用不同的 PacketDistributor 即可
    public void sendToServer() {PacketManager.channel.sendToServer(this);}
    public void sendToPlayer(ServerPlayerEntity player) {PacketManager.channel.send(PacketDistributor.PLAYER.with(() -> player), this); }

}
