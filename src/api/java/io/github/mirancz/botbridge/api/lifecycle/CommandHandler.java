package io.github.mirancz.botbridge.api.lifecycle;

import io.github.mirancz.botbridge.api.AbstractBot;
import io.github.mirancz.botbridge.api.control.ChatCommandListener;
import io.github.mirancz.botbridge.api.control.Task;

import java.util.ArrayList;
import java.util.List;

public class CommandHandler {

    private final List<ChatCommandListener> consumers = new ArrayList<>();
    private final BotManager botManager;

    public CommandHandler(BotManager botManager) {
        this.botManager = botManager;
    }

    public void register(ChatCommandListener chatConsumer) {
        consumers.add(chatConsumer);
    }

    public boolean onCommand(String message, AbstractBot player) {
        Task finalTask = null;

        for (ChatCommandListener consumer : consumers) {
            Task t = consumer.onCommand(message, player);
            if (t == null) continue;


            if (finalTask == null) finalTask = t;
            else {
                // TODO better checking
                System.out.println("Multiple command consumers register the same command!");
            }
        }

        if (finalTask != null) {
            botManager.executeTask(finalTask, player);
            return true;
        }
        return false;
    }

}
