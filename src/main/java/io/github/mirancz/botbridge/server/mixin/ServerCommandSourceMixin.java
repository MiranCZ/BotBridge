package io.github.mirancz.botbridge.server.mixin;

import io.github.mirancz.botbridge.api.AbstractPlayer;
import io.github.mirancz.botbridge.server.bridge.ServerCommandSourceBridge;
import io.github.mirancz.botbridge.server.impl.ServerBotCommandSource;
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
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerCommandSource.class)
public class ServerCommandSourceMixin implements ServerCommandSourceBridge {
    @Shadow
    @Final
    private CommandOutput output;

    @Shadow
    @Final
    private Vec3d position;

    @Shadow
    @Final
    private Vec2f rotation;

    @Shadow
    @Final
    private ServerWorld world;

    @Shadow
    @Final
    private int level;

    @Shadow
    @Final
    private String name;

    @Shadow
    @Final
    private Text displayName;

    @Shadow
    @Final
    private MinecraftServer server;

    @Shadow
    @Final
    private @Nullable Entity entity;

    @Shadow
    @Final
    private boolean silent;

    @Shadow
    @Final
    private ReturnValueConsumer returnValueConsumer;

    @Shadow
    @Final
    private EntityAnchorArgumentType.EntityAnchor entityAnchor;

    @Shadow
    @Final
    private SignedCommandArguments signedArguments;

    @Shadow
    @Final
    private FutureQueue messageChainTaskQueue;

    @Override
    public ServerBotCommandSource botBridge$from(AbstractPlayer bot) {
        return new ServerBotCommandSource(bot,
                output,position, rotation, world, level, name, displayName, server, entity, silent,
                returnValueConsumer, entityAnchor, signedArguments, messageChainTaskQueue
        );
    }
}
