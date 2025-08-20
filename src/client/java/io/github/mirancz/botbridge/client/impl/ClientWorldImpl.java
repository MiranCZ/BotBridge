package io.github.mirancz.botbridge.client.impl;

import io.github.mirancz.botbridge.api.AbstractWorld;
import net.minecraft.client.MinecraftClient;
import net.minecraft.world.World;

public class ClientWorldImpl extends AbstractWorld {

    @Override
    protected World getMcWorld() {
        return MinecraftClient.getInstance().world;
    }
}
