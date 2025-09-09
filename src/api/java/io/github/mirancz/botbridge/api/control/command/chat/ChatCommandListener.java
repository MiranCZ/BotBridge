package io.github.mirancz.botbridge.api.control.command.chat;

import io.github.mirancz.botbridge.api.Bot;

@FunctionalInterface
public interface ChatCommandListener {

    /**
     * Runs custom parsing and execution of chat command for a bot
     * @param message String of the command that was executed
     * @param bot The bot this should be executed for
     * @return If running the command was successful
     */
    boolean onCommand(String message, Bot bot);


}
