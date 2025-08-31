package io.github.mirancz.botbridge.server.impl.player;

import com.mojang.authlib.GameProfile;
import io.github.mirancz.botbridge.api.input.AbstractInput;
import io.github.mirancz.botbridge.api.AbstractBot;
import io.github.mirancz.botbridge.api.AbstractWorld;
import io.github.mirancz.botbridge.api.lifecycle.BotManager;
import io.github.mirancz.botbridge.server.impl.input.ServerInput;
import io.github.mirancz.botbridge.server.impl.ServerWorldImpl;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;


/**
 * Implementation of a server-side controllable player
 */
public class ServerBot extends AbstractBot {


    private final CustomServerPlayerEntity mcPlayer;
    private final ServerInput input;
    private final ServerWorldImpl world;

    public ServerBot(BotManager manager, MinecraftServer server, ServerWorld world, Vec3d pos, String name) {
        super(manager);
        GameProfile profile = new GameProfile(UUID.randomUUID(), name);
        mcPlayer = new CustomServerPlayerEntity(server, world, profile, this::tick);
        mcPlayer.setPosition(pos);

        this.input = new ServerInput(mcPlayer);
        this.world = new ServerWorldImpl(world);

    }

    @Override
    public AbstractWorld getWorld() {
        return world;
    }

    @Override
    public AbstractInput getInput() {
        return input;
    }

    @Override
    public PlayerEntity getPlayer() {
        return mcPlayer;
    }
}
