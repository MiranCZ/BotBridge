package io.github.mirancz.botbridge.api;

import io.github.mirancz.botbridge.api.input.AbstractInput;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public abstract class AbstractPlayer {

    protected boolean runningTask = false;

    public double getX() {
        return getMcPlayer().getX();
    }
    public double getY() {
        return getMcPlayer().getY();
    }
    public double getZ() {
        return getMcPlayer().getZ();
    }
    public BlockPos getBlockPos() {
        return getMcPlayer().getBlockPos();
    }
    public float getPitch() {
        return getMcPlayer().getPitch();
    }
    public float getYaw() {
        return getMcPlayer().getYaw();
    }
    public String getProfileName() {
        return getMcPlayer().getGameProfile().getName();
    }

    public void taskStart() {
        runningTask = true;
    }

    public void taskEnd() {
        runningTask = false;
    }

    public boolean isOf(PlayerEntity player) {
        return getMcPlayer() == player;
    }

    protected void tick() {
        if (!runningTask) return;


        PlayerEntity player = getMcPlayer();
        if (player == null) {
            return; //FIXME is this fine?
        }

        AbstractInput input = getInput();
        double movementSpeed = player.getAttributeValue(EntityAttributes.PLAYER_SNEAKING_SPEED);
        input.tick(player.isCrawling() || player.isInSneakingPose(), (float) movementSpeed);
        input.reset();
    }

    public abstract AbstractWorld getWorld();

    public abstract AbstractInput getInput();

    protected abstract PlayerEntity getMcPlayer();
}
