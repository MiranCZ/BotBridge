package io.github.mirancz.botbridge.client.impl.inventory;

import io.github.mirancz.botbridge.api.inventory.InventoryHandler;
import io.github.mirancz.botbridge.api.inventory.slot.Slot;
import io.github.mirancz.botbridge.client.bridge.HandledScreenBridge;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ClientInventoryHandler extends InventoryHandler {


    @Override
    public void startMouseDrag(Slot dragFrom, int button) {
        super.startMouseDrag(dragFrom, button);

        // just rendering client-side stuff
        runOnBridgedHandledScreen(
                (bridge, handler) ->
                        bridge.botBridge$startFakeDrag(dragFrom.getIndex(handler), button)
        );
    }

    @Override
    public void addSlotToDrag(Slot slot, int button) {
        super.addSlotToDrag(slot, button);

        // just rendering client-side stuff
        runOnBridgedHandledScreen(
                (bridge, handler) ->
                        bridge.botBridge$addDragSlot(slot.getIndex(handler))
        );
    }

    @Override
    public void stopMouseDrag(int button) {
        super.stopMouseDrag(button);

        // just rendering client-side stuff
        runOnBridgedHandledScreen(HandledScreenBridge::botBridge$stopFakeDrag);
    }


    private void runOnBridgedHandledScreen(BiConsumer<HandledScreenBridge, ScreenHandler> consumer) {
        runOnBridgedHandledScreen(bridge -> {
            ScreenHandler handler = getScreenHandler();
            if (handler == null) return;

            consumer.accept(bridge, handler);
        });
    }

    private void runOnBridgedHandledScreen(Consumer<HandledScreenBridge> consumer) {
        Screen screen = MinecraftClient.getInstance().currentScreen;

        if (screen instanceof HandledScreen<?> handledScreen) {
            consumer.accept((HandledScreenBridge) handledScreen);
        }
    }

    @Override
    protected void sendContainerClick(int slotId, int button, SlotActionType actionType) {
        ScreenHandler handler = getScreenHandler();
        ClientPlayerEntity player = getPlayer();
        ClientPlayerInteractionManager interactionManager = getInteractionManager();
        if (handler == null || player == null || interactionManager == null) return;

        int syncId = handler.syncId;

        interactionManager.clickSlot(syncId, slotId, button, actionType, player);
    }

    @Override
    protected ScreenHandler getScreenHandler() {
        ClientPlayerEntity player = getPlayer();
        if (player == null) return null;

        return player.currentScreenHandler;
    }

    private ClientPlayerEntity getPlayer() {
        return MinecraftClient.getInstance().player;
    }

    private ClientPlayerInteractionManager getInteractionManager() {
        return MinecraftClient.getInstance().interactionManager;
    }

}
