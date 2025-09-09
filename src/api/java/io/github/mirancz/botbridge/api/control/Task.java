package io.github.mirancz.botbridge.api.control;

import io.github.mirancz.botbridge.api.Bot;

public abstract class Task {

    public static boolean stop(Bot player) {
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

    protected final Bot player;

    public Task(Bot player) {
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
