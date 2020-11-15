package com.hexdragon.corere.network;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.Function;

public class PacketManagerRe {

    // 获取数据包的种类 ID
    private static int ID = 0;
    private static int nextID() {return ID++;}

    // 创建 channel
    public static SimpleChannel channel;
    public static void create(String modid) {
        PacketManagerRe.channel = NetworkRegistry.newSimpleChannel(new ResourceLocation(modid + ":packets"), () -> "1.0", (s) -> true, (s) -> true);
    }

    // 注册数据包
    public static <T extends PacketRe> void registerPacket(Class<T> messageType, Function<PacketBuffer, T> decoder) {
        channel.registerMessage(nextID(), messageType, T::encoder, decoder, T::onReceivePacket);
    }

}
