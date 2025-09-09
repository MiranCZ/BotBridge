package io.github.mirancz.botbridge.client.impl;

import io.github.mirancz.botbridge.api.input.BotInput;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.network.ClientPlayerEntity;

public class ClientBotInput extends BotInput {


    private static class ClientInputExecutor extends Input {
        protected float forwardSpeed = 0;
        protected float sidewaysSpeed = 0;

        @Override
        public void tick(boolean slowDown, float slowDownFactor) {
            this.movementForward = forwardSpeed;
            this.movementSideways = sidewaysSpeed;
        }
    }

    @Override
    protected void processInput(float forwardSpeed, float sidewaysSpeed) {
        ClientPlayerEntity player = getPlayer();
        if (!(player.input instanceof ClientInputExecutor)) {
            player.input = new ClientInputExecutor();
        }
        ClientInputExecutor input = (ClientInputExecutor) player.input;

        getPlayer().setSprinting(sprint);

        input.forwardSpeed = forwardSpeed;
        input.sidewaysSpeed = sidewaysSpeed;

        input.pressingForward = forward;
        input.pressingBack = back;
        input.pressingLeft = left;
        input.pressingRight = right;

        input.jumping = jump;
        input.sneaking = sneak;
    }

    public void freeControl() {
        ClientPlayerEntity player = getPlayer();

        if (!(player.input instanceof KeyboardInput)) {
            player.input = new KeyboardInput(MinecraftClient.getInstance().options);
        }
    }

    @Override
    protected void updatePitch(float pitch) {
        getPlayer().setPitch(pitch);
    }

    @Override
    protected void updateYaw(float yaw) {
        getPlayer().setYaw(yaw);
    }

    private ClientPlayerEntity getPlayer() {
        return MinecraftClient.getInstance().player;
    }

}
