package io.github.mirancz.botbridge.api.input;


public class InputKey {

    private int timesPressed = 0;
    private boolean pressed = false;

    public void onPress() {
        timesPressed++;
        pressed = true;
    }

    public boolean isPressed() {
        return pressed;
    }

    public boolean wasPressed() {
        if (timesPressed == 0) {
            return false;
        }

        timesPressed--;
        return true;
    }


    public void reset() {
        pressed = false;
        timesPressed = 0;
    }


}
