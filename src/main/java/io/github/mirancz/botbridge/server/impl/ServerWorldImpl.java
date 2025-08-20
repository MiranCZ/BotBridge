package io.github.mirancz.botbridge.server.impl;

import io.github.mirancz.botbridge.api.AbstractWorld;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class ServerWorldImpl extends AbstractWorld {

    private final ServerWorld world;

    public ServerWorldImpl(ServerWorld world) {
        this.world = world;
    }

    @Override
    protected World getMcWorld() {
        return world;
    }
}
