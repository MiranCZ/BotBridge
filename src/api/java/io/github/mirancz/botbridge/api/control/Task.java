package io.github.mirancz.botbridge.api.control;

import io.github.mirancz.botbridge.api.AbstractBot;

public abstract class Task {

    public static boolean stop(AbstractBot player) {
        player.runTask(new Task(player) {
            @Override
            public void tick() {
            }

            @Override
            public boolean running() {
                return false;
            }
        });

        return true;
    }

    protected final AbstractBot player;

    public Task(AbstractBot player) {
        this.player = player;
    }

    public abstract void tick();

    public boolean running() {
        return true;
    }

    public final void stop() {
        player.taskEnd();

        onStop();
    }

    protected void onStop() {
    }

}
