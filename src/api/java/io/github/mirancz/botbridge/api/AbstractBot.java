package io.github.mirancz.botbridge.api;

import io.github.mirancz.botbridge.api.control.Task;
import io.github.mirancz.botbridge.api.input.AbstractInput;
import io.github.mirancz.botbridge.api.lifecycle.BotManager;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public abstract class AbstractBot {

    private final BotManager manager;

    protected AbstractBot(BotManager manager) {
        this.manager = manager;
    }

    public final boolean runTask(Task task) {
        manager.executeTask(task, this);
        return true;
    }

    public String getProfileName() {
        return getPlayer().getGameProfile().getName();
    }

    public void taskEnd() {
        getInput().freeControl();
    }

    public boolean isOf(PlayerEntity player) {
        return getPlayer() == player;
    }

    protected void tick() {
        if (!manager.isTaskRunningFor(this)) return;

        PlayerEntity player = getPlayer();
        if (player == null) {
            return; //FIXME is this fine?
        }

        AbstractInput input = getInput();
        double movementSpeed = player.getAttributeValue(EntityAttributes.PLAYER_SNEAKING_SPEED);
        input.tick(player.isCrawling() || player.isInSneakingPose(), (float) movementSpeed);
        input.reset();
    }

    public World getWorld() {
        PlayerEntity player = getPlayer();
        if (player == null) return null;

        return player.getWorld();
    }

    public abstract AbstractInput getInput();

    public abstract PlayerEntity getPlayer();
}
