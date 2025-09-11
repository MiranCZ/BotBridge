package io.github.mirancz.botbridge.api.inventory.slot;

import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class Slot {

    private final SlotType type;
    private final int index;

    public static Slot none() {
        return new Slot(SlotType.GLOBAL, -999);
    }

    public static Slot global(int globalIndex) {
        return new Slot(SlotType.GLOBAL, globalIndex);
    }

    public static Slot hotbar(int hotbarIndex) {
        return new Slot(SlotType.HOTBAR, hotbarIndex);
    }

    public static Slot playerInv(int x, int y) {
        return playerInv(x + 9*y);
    }

    public static Slot playerInv(int inventoryIndex) {
        return new Slot(SlotType.PLAYER_INVENTORY, inventoryIndex);
    }

    public static Slot armor(int armorIndex) {
        return new Slot(SlotType.ARMOR, armorIndex);
    }

    public static Slot offhand() {
        return new Slot(SlotType.OFFHAND, 0);
    }

    private Slot(SlotType type, int index) {
        this.type = type;
        this.index = index;
    }

    /**
     * @return Index of the slot for the given ScreenHandler
     */
    public int getIndex(ScreenHandler handler) {
        int globalIndex = type.indexConvertor.apply(handler, this.index);

        if (globalIndex == -1) {
            // FIXME this is invalid, what now?
        }

        return globalIndex;
    }

    private enum SlotType {

        GLOBAL((handler, index) -> index),
        HOTBAR((screenHandler, index) -> {
            List<Integer> ids = getSortedSlotIds(screenHandler);

            if (screenHandler instanceof PlayerScreenHandler) {
                ids.removeLast(); //offhand
            }

            if (index < 0 || index >= 9) return -1;

            return ids.get(ids.size() - 9 + index);
        }),
        OFFHAND((screenHandler, integer) -> {
            if (screenHandler instanceof PlayerScreenHandler) {
                return getSortedSlotIds(screenHandler).getLast();
            }
            return -1;
        }),
        PLAYER_INVENTORY((handler, index) -> {
            if (index < 0 || index >= 27) return -1;

            List<Integer> ids = getSortedSlotIds(handler);

            if (handler instanceof PlayerScreenHandler) {
                ids.removeLast(); //offhand
            }

            for (int i = 0; i < 9; i++) {
                ids.removeLast(); //hotbar
            }
            return ids.get(ids.size()-27+index);
        }),
        ARMOR((handler, index) -> {
            if (index < 0 || index >= 4) return -1;
            if (handler instanceof PlayerScreenHandler) {
                return index+5; //hardcoding *should* be fine here
            }
            return -1;
        });


        private final BiFunction<ScreenHandler, Integer, Integer> indexConvertor;

        SlotType(BiFunction<ScreenHandler, Integer, Integer> indexConvertor) {
            this.indexConvertor = indexConvertor;
        }
    }

    private static List<Integer> getSortedSlotIds(ScreenHandler handler) {
        return new ArrayList<>(handler.slots.stream().map(slot -> slot.id).sorted().toList());
    }

}
