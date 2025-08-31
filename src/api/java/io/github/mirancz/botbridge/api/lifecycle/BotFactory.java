package io.github.mirancz.botbridge.api.lifecycle;

import io.github.mirancz.botbridge.api.AbstractBot;
import io.github.mirancz.botbridge.api.control.ChatCommandListener;
import io.github.mirancz.botbridge.api.control.command.CommandRegister;

public interface BotFactory {


    default void onCreated(AbstractBot player) {
    }

    default ChatCommandListener registerChatCommands(){
        return null;
    }

    default CommandRegister registerBrigadierCommands() {
        return null;
    }


}
