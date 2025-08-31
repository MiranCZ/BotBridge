package io.github.mirancz.botbridge.api.control.command.brigadier;

import io.github.mirancz.botbridge.api.AbstractBot;
import net.minecraft.command.CommandSource;

public interface BotBridgeCommandSource extends CommandSource {


    AbstractBot getBotPlayer();

}
