package io.github.mirancz.botbridge.client.bridge;

public interface HandledScreenBridge {

    void botBridge$startFakeDrag(int draggingFrom);

    void botBridge$addDragSlot(int slot);

    void botBridge$stopFakeDrag();
}
