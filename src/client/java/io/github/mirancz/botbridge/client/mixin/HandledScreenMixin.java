package io.github.mirancz.botbridge.client.mixin;


import io.github.mirancz.botbridge.client.bridge.HandledScreenBridge;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> implements HandledScreenBridge {
    @Shadow
    protected boolean cursorDragging;

    @Shadow
    @Final
    protected T handler;

    @Shadow
    @Final
    protected Set<Slot> cursorDragSlots;

    @Shadow
    public abstract void endTouchDrag();

    @Shadow
    protected abstract void calculateOffset();

    @Shadow
    private int heldButtonType;

    @Shadow
    private boolean touchIsRightClickDrag;

    @Unique
    private boolean fakeDrag = false;
    @Unique
    private int dragButton = -1;


    @Inject(method = "drawSlot", at = @At("HEAD"))
    public void inj(DrawContext context, Slot slot, CallbackInfo ci) {
        if (fakeDrag) {
            this.cursorDragging = true;

            this.touchIsRightClickDrag = (dragButton != 0);
            this.heldButtonType = dragButton;
        }
    }

    @Override
    public void botBridge$startFakeDrag(int draggingFrom, int button) {
        this.fakeDrag = true;
        this.dragButton = button;
        this.cursorDragging = true;
    }

    @Override
    public void botBridge$addDragSlot(int slot) {
        this.cursorDragSlots.add(this.handler.getSlot(slot));
        calculateOffset();
    }

    @Override
    public void botBridge$stopFakeDrag() {
        this.cursorDragging = false;
        this.endTouchDrag();
        this.fakeDrag = false;
    }

}
