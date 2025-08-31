package io.github.mirancz.botbridge.client.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.mirancz.botbridge.api.lifecycle.BotManager;
import io.github.mirancz.botbridge.api.util.Side;
import io.github.mirancz.botbridge.client.impl.ClientBotCommandSource;
import io.github.mirancz.botbridge.client.impl.ClientBot;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {


    @Inject(
            method = "sendChatMessage",
            at = @At("HEAD"),
            cancellable = true
    )
    private void sendChatMessage(String string, CallbackInfo ci) {
        boolean success = BotManager.onCommand(Side.CLIENT, string, ClientBot.INSTANCE);

        if (success) {
            ci.cancel();
            return;
        }

        runCustomCommand(string, ci);
    }

    @Inject(
            method = "sendChatCommand",
            at = @At("HEAD"),
            cancellable = true
    )
    private void sendChatCommand(String command, CallbackInfo ci) {
        runCustomCommand("/"+command, ci);
    }


    @Unique
    private static void runCustomCommand(String string, CallbackInfo ci) {
        if (string.isEmpty()) return;

        char ch = string.charAt(0);

        var dispatcher = BotManager.getDispatcher(Side.CLIENT, ch);
        if (dispatcher != null) {
            try {
                dispatcher.execute(string.substring(1), new ClientBotCommandSource(ClientBot.INSTANCE));
            } catch (CommandSyntaxException e) {
                e.printStackTrace(); //FIXME do some other way
            }
            ci.cancel();
        }
    }


}
