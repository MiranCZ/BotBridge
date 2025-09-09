package io.github.mirancz.botbridge.api.lifecycle;

import io.github.mirancz.botbridge.api.Bot;
import io.github.mirancz.botbridge.api.control.command.chat.ChatCommandListener;
import io.github.mirancz.botbridge.api.control.command.brigadier.CommandRegister;

public interface BotFactory {


    default void onCreated(Bot player) {
    }

    default ChatCommandListener registerChatCommands(){
        return null;
    }

    default CommandRegister registerBrigadierCommands() {
        return null;
    }


}
