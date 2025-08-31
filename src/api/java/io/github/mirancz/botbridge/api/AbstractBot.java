package io.github.mirancz.botbridge.api;

import io.github.mirancz.botbridge.api.input.AbstractInput;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;

public abstract class AbstractBot {

    protected boolean runningTask = false;

    public String getProfileName() {
        return getPlayer().getGameProfile().getName();
    }

    public void taskStart() {
        runningTask = true;
    }

    public void taskEnd() {
        runningTask = false;
    }

    public boolean isOf(PlayerEntity player) {
        return getPlayer() == player;
    }

    protected void tick() {
        if (!runningTask) return;


        PlayerEntity player = getPlayer();
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

    public abstract PlayerEntity getPlayer();
}
