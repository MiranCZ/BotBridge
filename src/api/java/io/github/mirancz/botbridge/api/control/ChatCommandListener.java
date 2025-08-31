package io.github.mirancz.botbridge.api.control;

import io.github.mirancz.botbridge.api.AbstractBot;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ChatCommandListener {


    @Nullable
    Task onCommand(String message, AbstractBot player);



}
