package io.github.mirancz.botbridge.api.control.command;

import io.github.mirancz.botbridge.api.AbstractPlayer;
import net.minecraft.command.CommandSource;

public interface BotBridgeCommandSource extends CommandSource {


    AbstractPlayer getBotPlayer();

}
