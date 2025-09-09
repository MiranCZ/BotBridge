package io.github.mirancz.botbridge.api.control.command.brigadier;

import io.github.mirancz.botbridge.api.Bot;
import net.minecraft.command.CommandSource;

public interface BotBridgeCommandSource extends CommandSource {


    Bot getBotPlayer();

}
