package io.github.mirancz.botbridge.api.lifecycle;

import io.github.mirancz.botbridge.api.AbstractBot;
import io.github.mirancz.botbridge.api.control.command.chat.ChatCommandListener;
import io.github.mirancz.botbridge.api.control.Task;

import java.util.ArrayList;
import java.util.List;

public class ChatCommandHandler {

    private final List<ChatCommandListener> consumers = new ArrayList<>();
    private final BotManager botManager;

    public ChatCommandHandler(BotManager botManager) {
        this.botManager = botManager;
    }

    public void register(ChatCommandListener chatConsumer) {
        consumers.add(chatConsumer);
    }

    public boolean onCommand(String message, AbstractBot player) {
        boolean anySuccess = false;
        for (ChatCommandListener consumer : consumers) {
            Task task = botManager.getPlayerTask(player);
            boolean success = consumer.onCommand(message, player);

            // simple guard-rail check
            if (!success && (task != botManager.getPlayerTask(player))) {
                throw new IllegalStateException("Changed task without returning 'true'!");
            }

            if (success && anySuccess) {
                // TODO better checking
                System.out.println("Multiple command consumers register the same command!");
            }
            anySuccess |= success;
        }

        return anySuccess;
    }

}
