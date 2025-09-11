package io.github.mirancz.botbridge.server.impl.player;

import com.mojang.authlib.GameProfile;
import io.github.mirancz.botbridge.api.input.BotInput;
import io.github.mirancz.botbridge.api.Bot;
import io.github.mirancz.botbridge.api.inventory.InventoryHandler;
import io.github.mirancz.botbridge.api.lifecycle.BotManager;
import io.github.mirancz.botbridge.server.commands.sidebar.SidebarManager;
import io.github.mirancz.botbridge.server.impl.ServerInventoryHandler;
import io.github.mirancz.botbridge.server.impl.input.ServerBotInput;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;


/**
 * Implementation of a server-side controllable player
 */
public class ServerBot extends Bot {


    private final CustomServerPlayerEntity mcPlayer;
    private final ServerBotInput input;
    private final SidebarManager sidebarManager;
    private final InventoryHandler inventoryHandler;

    public ServerBot(BotManager manager, MinecraftServer server, ServerWorld world, Vec3d pos, String name) {
        super(manager);
        GameProfile profile = new GameProfile(UUID.randomUUID(), name);
        mcPlayer = new CustomServerPlayerEntity(server, world, profile, this);
        mcPlayer.setPosition(pos);

        this.inventoryHandler = new ServerInventoryHandler(mcPlayer);
        this.input = new ServerBotInput(mcPlayer);
        this.sidebarManager = new SidebarManager(mcPlayer);

        // add empty lines
        for (int i = 0; i < 5; i++) {
            sidebarManager.addLine(Text.empty());
        }
    }

    // here to allow package-level access
    protected void tick() {
        super.tick();
    }

    @Override
    public BotInput getInput() {
        return input;
    }

    @Override
    public PlayerEntity getPlayer() {
        return mcPlayer;
    }

    @Override
    public InventoryHandler getInventoryHandler() {
        return inventoryHandler;
    }

    public SidebarManager getSidebar() {
        return sidebarManager;
    }

}
