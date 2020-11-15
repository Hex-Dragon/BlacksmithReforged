package com.hexdragon.util.network;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class PackManager {

    // 获取数据包的种类 ID
    private static int ID = 0;
    private static int nextID() {return ID++;}

    // 创建 channel
    private static SimpleChannel channel;
    public static void create(String modid) {
        PackManager.channel = NetworkRegistry.newSimpleChannel(new ResourceLocation(modid + ":packets"), () -> "1.0", (s) -> true, (s) -> true);
    }

    // 注册数据包
    public static <Packet> void registerPack(Class<Packet> messageType, BiConsumer<Packet, PacketBuffer> encoder, Function<PacketBuffer, Packet> decoder, BiConsumer<Packet, Supplier<NetworkEvent.Context>> messageConsumer) {
        channel.registerMessage(nextID(), messageType, encoder, decoder, messageConsumer);
    }

    // 将数据包发送至服务器
    public static <Packet> void sendToServer(Packet message) {
        channel.sendToServer(message);
    }

}
