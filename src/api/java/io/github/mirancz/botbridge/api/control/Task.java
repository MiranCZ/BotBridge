package io.github.mirancz.botbridge.api.control;

import io.github.mirancz.botbridge.api.AbstractBot;

public abstract class Task {

    public static Task noop(AbstractBot player, ChatCommandListener owner) {
        return new Task(player, owner) {
            @Override
            public void tick() {
            }

            @Override
            public boolean running() {
                return false;
            }
        };
    }

    protected final AbstractBot player;
    public final ChatCommandListener owner;

    public Task(AbstractBot player, ChatCommandListener owner) {
        this.player = player;
        this.owner = owner;

        player.taskStart();
    }

    public abstract void tick();

    public boolean running() {
        return true;
    }

    public void stop() {
        player.taskEnd();
    }

}
