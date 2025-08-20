package io.github.mirancz.botbridge.api.control.command;

import com.mojang.brigadier.CommandDispatcher;

public interface CommandRegister {

    void register(CommandDispatcher<BotBridgeCommandSource> dispatcher);

    char getPrefix();

}
