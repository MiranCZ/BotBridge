package io.github.mirancz.botbridge.server.impl;

import io.github.mirancz.botbridge.api.AbstractBot;
import io.github.mirancz.botbridge.api.control.command.BotBridgeCommandSource;
import net.minecraft.command.ReturnValueConsumer;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.network.message.SignedCommandArguments;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.thread.FutureQueue;
import org.jetbrains.annotations.Nullable;

public class ServerBotCommandSource extends ServerCommandSource implements BotBridgeCommandSource {

    private final AbstractBot bot;

    public ServerBotCommandSource(AbstractBot bot, CommandOutput output, Vec3d pos, Vec2f rot, ServerWorld world, int level, String name, Text displayName, MinecraftServer server, @Nullable Entity entity, boolean silent, ReturnValueConsumer resultStorer, EntityAnchorArgumentType.EntityAnchor entityAnchor, SignedCommandArguments signedArguments, FutureQueue messageChainTaskQueue) {
        super(output, pos, rot, world, level, name, displayName, server, entity, silent, resultStorer, entityAnchor, signedArguments, messageChainTaskQueue);
        this.bot = bot;
    }


    @Override
    public AbstractBot getBotPlayer() {
        return bot;
    }
}
