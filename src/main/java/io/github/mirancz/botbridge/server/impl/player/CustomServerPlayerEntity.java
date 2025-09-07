package io.github.mirancz.botbridge.server.impl.player;

import com.mojang.authlib.GameProfile;
import io.github.mirancz.botbridge.server.bridge.ClientConnectionBridge;
import io.github.mirancz.botbridge.server.bridge.ServerPlayerEntityBridge;
import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.block.BlockState;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.message.ChatVisibility;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.math.BlockPos;

public class CustomServerPlayerEntity extends ServerPlayerEntity {

    protected static final SyncedClientOptions DUMMY_CLIENT_OPTIONS =  new SyncedClientOptions("", 5, ChatVisibility.FULL, true, 1, Arm.RIGHT, false, false);
    private final ServerBot bot;
    private final SyncedClientOptions clientOptions;

    public CustomServerPlayerEntity(MinecraftServer server, ServerWorld world, GameProfile profile, ServerBot bot) {
        this(server, world, profile, bot, DUMMY_CLIENT_OPTIONS);
    }

    public CustomServerPlayerEntity(MinecraftServer server, ServerWorld world, GameProfile profile, ServerBot bot, SyncedClientOptions clientOptions) {
        super(server, world, profile, clientOptions);
        this.bot = bot;
        this.clientOptions = clientOptions;

        this.spawn();
    }

    @Override
    public void tick() {
        super.playerTick();
        super.tick();

        this.bot.tick();
    }

    @Override
    public void sendMessage(Text message) {
        super.sendMessage(message);

        bot.getSidebar().addLine(message);
    }

    @Override
    public void sendMessage(Text message, boolean overlay) {
        super.sendMessage(message, overlay);

        bot.getSidebar().addLine(message);
    }

    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
        // FIXME super.handleFall exists
        ((ServerPlayerEntityBridge) this).botBridge$parentFall(heightDifference, onGround, state, landedPosition);
    }

    @Override
    public void takeKnockback(double strength, double x, double z) {
        super.takeKnockback(strength, x, z);
        this.velocityModified = false; // should hopefully fix cancellation of kb from players
    }

    private void spawn() {
        ClientConnection con = new ClientConnection(NetworkSide.SERVERBOUND);
        ((ClientConnectionBridge)con).botBridge$setChannel(new EmbeddedChannel());

        server.getPlayerManager().onPlayerConnect(con, this, new ConnectedClientData(getGameProfile(), 0, clientOptions, false));

    }
}
