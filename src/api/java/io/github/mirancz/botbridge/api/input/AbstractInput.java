package io.github.mirancz.botbridge.api.input;

public abstract class AbstractInput {



    public boolean forward = false;
    public boolean back = false;
    public boolean left = false;
    public boolean right = false;
    public boolean sprint = false;
    public boolean jump = false;
    public boolean sneak = false;

    public InputKey useKey = new InputKey();
    public InputKey attackKey = new InputKey();

    private final FloatContainer pitch = new FloatContainer();
    private final FloatContainer yaw = new FloatContainer();


    private static float getMovementMultiplier(boolean positive, boolean negative) {
        if (positive == negative) {
            return 0.0F;
        } else {
            return positive ? 1.0F : -1.0F;
        }
    }


    public void tick(boolean slowDown, float slowDownFactor) {
        float forwardSpeed = getMovementMultiplier(forward, back);
        float sidewaysSpeed = getMovementMultiplier(left, right);

        if (slowDown) {
            sidewaysSpeed *= slowDownFactor;
            forwardSpeed *= slowDownFactor;
        }

        processInput(forwardSpeed, sidewaysSpeed);

        if (pitch.isSet()) {
            updatePitch(pitch.getValue());
        }
        if (yaw.isSet()) {
            updateYaw(yaw.getValue());
        }
    }

    public void setPitch(float pitch) {
        this.pitch.setValue(pitch);
    }

    public void setYaw(float yaw) {
        this.yaw.setValue(yaw);
    }

    protected abstract void processInput(float forwardSpeed, float sidewaysSpeed);

    protected abstract void updatePitch(float pitch);

    protected abstract void updateYaw(float yaw);

    public abstract void freeControl();

    public void reset() {
        forward = false;
        back = false;
        left = false;
        right = false;
        sprint = false;
        jump = false;
        sneak = false;

        useKey.reset();
        attackKey.reset();
    }

    protected static class FloatContainer {
        private float value = 0;
        private boolean set = false;

        public void setValue(float value) {
            this.value = value;
            this.set = true;
        }

        public boolean isSet() {
            return set;
        }

        public float getValue() {
            return value;
        }

        public void reset() {
            this.value = 0;
            this.set = false;
        }
    }

}
