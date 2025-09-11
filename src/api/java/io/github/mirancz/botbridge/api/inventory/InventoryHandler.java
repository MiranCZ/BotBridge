package io.github.mirancz.botbridge.api.inventory;

import io.github.mirancz.botbridge.api.inventory.slot.Slot;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;

public abstract class InventoryHandler {

    
    public void clickSlot(Slot slot, int button) {
        sendContainerClick(slot, button, SlotActionType.PICKUP);
    }

    
    public void shiftClickSlot(Slot slot) {
        sendContainerClick(slot, 0, SlotActionType.QUICK_MOVE);
    }

    
    public void keyboardClick(Slot selectedSlot, int key) {
        // makes more sense that key number 1 is actually equal to one
        sendContainerClick(selectedSlot, key+1, SlotActionType.SWAP);
    }

    
    public void drop(Slot slot, boolean control) {
        int button = 0;
        if (control) {
            button = 1;
        }
        sendContainerClick(slot,button, SlotActionType.THROW);
    }

    
    public void doubleClick(Slot slot) {
        sendContainerClick(slot, 0, SlotActionType.PICKUP_ALL);
    }

    
    public void startMouseDrag(Slot dragFrom, int button) {
        clickSlot(dragFrom, button);
        sendContainerClick(Slot.none(), ScreenHandler.packQuickCraftData(0, button), SlotActionType.QUICK_CRAFT);
   }

    
    public void addSlotToDrag(Slot slot, int button) {
        sendContainerClick(slot, ScreenHandler.packQuickCraftData(1, button), SlotActionType.QUICK_CRAFT);
   }

    
    public void stopMouseDrag(int button) {
        sendContainerClick(Slot.none(), ScreenHandler.packQuickCraftData(2, button), SlotActionType.QUICK_CRAFT);
    }

    private void sendContainerClick(Slot slot, int button, SlotActionType actionType) {
        ScreenHandler handler = getScreenHandler();
        if (handler == null) return;

        int slotId = slot.getIndex(handler);
        if (slotId == -1) return;

        sendContainerClick(slotId, button, actionType);
    }

    protected abstract void sendContainerClick(int slotId, int button, SlotActionType actionType);

    protected abstract ScreenHandler getScreenHandler();
}
