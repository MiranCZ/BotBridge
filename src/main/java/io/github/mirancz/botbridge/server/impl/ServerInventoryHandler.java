package io.github.mirancz.botbridge.server.impl;

import io.github.mirancz.botbridge.api.inventory.InventoryHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerInventoryHandler extends InventoryHandler {

    private final ServerPlayerEntity player;

    public ServerInventoryHandler(ServerPlayerEntity player) {
        this.player = player;
    }

    @Override
    protected void sendContainerClick(int slotId, int button, SlotActionType actionType) {
        getScreenHandler().onSlotClick(slotId, button, actionType, player);
    }

    @Override
    protected ScreenHandler getScreenHandler() {
        return player.currentScreenHandler;
    }
}
