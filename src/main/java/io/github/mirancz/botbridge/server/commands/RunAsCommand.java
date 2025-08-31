package io.github.mirancz.botbridge.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.mirancz.botbridge.api.AbstractBot;
import io.github.mirancz.botbridge.api.control.command.brigadier.BotBridgeCommandSource;
import io.github.mirancz.botbridge.api.lifecycle.BotManager;
import io.github.mirancz.botbridge.api.util.Side;
import io.github.mirancz.botbridge.server.bridge.ServerCommandSourceBridge;
import io.github.mirancz.botbridge.server.impl.ServerBotCommandSource;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Colors;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

//FIXME the argument type of any player is not ideal
public class RunAsCommand implements CommandRegistrationCallback {



    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(literal("runas").then(argument("bot", EntityArgumentType.player()).then(argument("command", StringArgumentType.greedyString()).executes(s -> {

            EntitySelector argument = s.getArgument("bot", EntitySelector.class);
            ServerPlayerEntity entity = argument.getPlayer(s.getSource());
            if (!BotManager.isBot(entity)) {
                throw new SimpleCommandExceptionType(Text.of("Player '").copy().append(entity.getName()).append("' is not a bot!")).create();
            }

            AbstractBot bot = BotManager.getBot(entity);
            String command = s.getArgument("command", String.class);
            if (command.isEmpty()) {
                throw new SimpleCommandExceptionType(Text.of("Command is empty")).create();
            }

            ServerCommandSource source = s.getSource();
            ServerBotCommandSource botSource = ((ServerCommandSourceBridge) source).botBridge$from(bot);


            boolean chatSuccess = BotManager.onCommand(Side.SERVER, command, bot);
            if (chatSuccess) {
                source.sendMessage(Text.of("Chat command '"+command+"' was successfully executed").copy().withColor(Colors.GREEN));
                return 0;
            }

            char prefix = command.charAt(0);
            CommandDispatcher<BotBridgeCommandSource> customDispatcher = BotManager.getDispatcher(Side.SERVER, prefix);
            if (customDispatcher != null) {
                try {
                    int result = customDispatcher.execute(command.substring(1), botSource);

                    // FIXME maybe some simple color coding? todo figure out what exactly exit codes mean
                    source.sendMessage(Text.of("Command '"+command+"' was executed with exit code "+result).copy().withColor(Colors.GRAY));
                    return result;
                } catch (CommandSyntaxException e) {
                    // FIXME not ideal but oh well
                    source.sendError(Texts.toText(e.getRawMessage()));
                    return -1;
                }
            }
            source.sendError(Text.of("No commands with prefix of '"+prefix+"' found"));

            return -1;
        }))));
    }



}
