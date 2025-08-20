package io.github.mirancz.botbridge.server.impl.input;

import io.github.mirancz.botbridge.api.input.AbstractInput;
import io.github.mirancz.botbridge.server.impl.player.CustomServerPlayerEntity;

public class ServerInput extends AbstractInput {

    private final CustomServerPlayerEntity mcServerPlayer;
    private final MouseInput mouseInput;

    public ServerInput(CustomServerPlayerEntity mcServerPlayer) {
        this.mcServerPlayer = mcServerPlayer;
        this.mouseInput = new MouseInput(mcServerPlayer);
    }


    // FIXME jump not getting spammed
    @Override
    protected void processInput(float forwardSpeed, float sidewaysSpeed) {
        mcServerPlayer.forwardSpeed = forwardSpeed;
        mcServerPlayer.sidewaysSpeed = sidewaysSpeed;
        mcServerPlayer.setJumping(jump);
        mcServerPlayer.setSneaking(sneak);
//        this.mcServerPlayer.updateInput(sidewaysSpeed, forwardSpeed, jumping, sneaking); // <--- this updates boat only..?

        mcServerPlayer.setSprinting(sprint);


        mouseInput.tick(useKey, attackKey);
    }

    public void freeControl() {
        reset();
        processInput(0, 0);
    }

    @Override
    protected void updatePitch(float pitch) {
        mcServerPlayer.setPitch(pitch);
    }

    @Override
    protected void updateYaw(float yaw) {
        mcServerPlayer.setYaw(yaw);
    }
}
