package io.github.mirancz.botbridge.api.inventory.slot.indexes;

import io.github.mirancz.botbridge.api.inventory.slot.Slot;

public class CraftingTable {

    public static CraftingTable INSTANCE = new CraftingTable();


    private CraftingTable() {
    }

    public Slot result() {
        return Slot.global(0);
    }

    public Slot input2D(int x, int y) {
        if (x < 0 || x >= 2 || y < 0 || y >= 2) return Slot.global(-1);

        return Slot.global(x + 2*y + 1);
    }

    public Slot input3D(int x, int y) {
        if (x < 0 || x >= 3 || y < 0 || y >= 3) return Slot.global(-1);

        return Slot.global(x + 3*y + 1);
    }

}
