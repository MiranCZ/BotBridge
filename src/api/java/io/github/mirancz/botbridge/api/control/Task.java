package io.github.mirancz.botbridge.api.control;

import io.github.mirancz.botbridge.api.AbstractBot;
import io.github.mirancz.botbridge.api.control.command.chat.ChatCommandListener;

public abstract class Task {

    public static boolean stop(AbstractBot player, ChatCommandListener owner) {
        player.runTask(new Task(player, owner) {
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
    public final ChatCommandListener owner;

    public Task(AbstractBot player, ChatCommandListener owner) {
        this.player = player;
        this.owner = owner;
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
