package io.github.mirancz.botbridge.api.control;

import io.github.mirancz.botbridge.api.AbstractPlayer;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ChatCommandListener {


    @Nullable
    Task onCommand(String message, AbstractPlayer player);



}
