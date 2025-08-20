package io.github.mirancz.botbridge.api.lifecycle;

import com.mojang.brigadier.CommandDispatcher;
import io.github.mirancz.botbridge.api.AbstractPlayer;
import io.github.mirancz.botbridge.api.control.ChatCommandListener;
import io.github.mirancz.botbridge.api.control.command.BotBridgeCommandSource;
import io.github.mirancz.botbridge.api.control.command.CommandRegister;

public interface BotFactory {


    default void onCreated(AbstractPlayer player) {
    }

    default ChatCommandListener registerChatCommands(){
        return null;
    }

    default CommandRegister registerBrigadierCommands() {
        return null;
    }


}
