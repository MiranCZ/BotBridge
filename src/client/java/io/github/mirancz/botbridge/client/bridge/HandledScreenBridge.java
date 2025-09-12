package io.github.mirancz.botbridge.client.bridge;

public interface HandledScreenBridge {

    void botBridge$startFakeDrag(int draggingFrom, int button);

    void botBridge$addDragSlot(int slot);

    void botBridge$stopFakeDrag();
}
