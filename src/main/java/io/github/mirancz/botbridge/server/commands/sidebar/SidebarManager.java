package io.github.mirancz.botbridge.server.commands.sidebar;

import com.google.common.collect.Lists;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import eu.pb4.sidebars.api.Sidebar;
import io.github.mirancz.botbridge.api.control.command.brigadier.BotBridgeCommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.number.BlankNumberFormat;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SidebarManager {


    private static final Style ERROR_STYLE = Style.EMPTY.withColor(Formatting.RED);
    private static final Style INFO_STYLE = Style.EMPTY.withColor(Formatting.GRAY);
    private static final List<Style> HIGHLIGHT_STYLES;
    private static final Map<ServerPlayerEntity, Sidebar> sidebars = new HashMap<>();

    private final Sidebar sidebar;
    private int lineCount = 0;

    static {
        HIGHLIGHT_STYLES = new ArrayList<>();
        for (Formatting formatting : List.of(Formatting.AQUA, Formatting.YELLOW, Formatting.GREEN, Formatting.LIGHT_PURPLE, Formatting.GOLD)) {
            HIGHLIGHT_STYLES.add(Style.EMPTY.withColor(formatting));
        }
    }

    public SidebarManager(PlayerEntity player) {
        sidebar = new Sidebar(Text.literal(player.getNameForScoreboard()).setStyle(Style.EMPTY.withBold(true)).append(Text.literal("s chat").setStyle(Style.EMPTY.withBold(false))), Sidebar.Priority.LOW);
        sidebar.setDefaultNumberFormat(BlankNumberFormat.INSTANCE);
        sidebar.show();
    }

    public void add(ServerPlayerEntity player) {
        if (sidebars.containsKey(player)) {
            remove(player);
        }

        sidebar.addPlayer(player);
        sidebars.put(player, sidebar);
    }

    public void remove(ServerPlayerEntity player) {
        sidebar.removePlayer(player);
        sidebars.remove(player);
    }

    public void onBrigadierCommand(ParseResults<BotBridgeCommandSource> parse, String command) {
        addUserLine(orderedTextToMutableText(highlight(parse, command)));
    }

    private static OrderedText highlight(ParseResults<BotBridgeCommandSource> parse, String original) {
        List<OrderedText> list = Lists.newArrayList();
        int i = 0;
        int j = -1;
        CommandContextBuilder<BotBridgeCommandSource> commandContextBuilder = parse.getContext().getLastChild();

        for (ParsedArgument<BotBridgeCommandSource, ?> botBridgeCommandSourceParsedArgument : commandContextBuilder.getArguments().values()) {
            ++j;
            if (j >= HIGHLIGHT_STYLES.size()) {
                j = 0;
            }

            int k = Math.max(botBridgeCommandSourceParsedArgument.getRange().getStart() + 1, 0);
            if (k >= original.length()) {
                break;
            }

            int l = Math.min(botBridgeCommandSourceParsedArgument.getRange().getEnd() + 1, original.length());
            if (l > 0) {
                list.add(OrderedText.styledForwardsVisitedString(original.substring(i, k), INFO_STYLE));
                list.add(OrderedText.styledForwardsVisitedString(original.substring(k, l), HIGHLIGHT_STYLES.get(j)));
                i = l;
            }
        }

        if (parse.getReader().canRead()) {
            int m = Math.max(parse.getReader().getCursor() + 1, 0);

            if (m < original.length()) {
                int n = Math.min(m + parse.getReader().getRemainingLength(), original.length());
                list.add(OrderedText.styledForwardsVisitedString(original.substring(i, m), INFO_STYLE));
                list.add(OrderedText.styledForwardsVisitedString(original.substring(m, n), ERROR_STYLE));
                i = n;
            }
        }

        list.add(OrderedText.styledForwardsVisitedString(original.substring(i), INFO_STYLE));
        return OrderedText.concat(list);
    }

    public static MutableText orderedTextToMutableText(OrderedText orderedText) {
        MutableText mutableText = Text.empty();

        orderedText.accept((index, style, codePoint) -> {
            String character = new String(Character.toChars(codePoint));

            mutableText.append(Text.literal(character).setStyle(style));
            return true;
        });

        return mutableText;
    }

    public void onChatCommand(String command, boolean valid) {
        if (!valid) {
            addUserLine(Text.literal(command).setStyle(ERROR_STYLE));
            return;
        }

        MutableText text = Text.empty();

        int i = -1;
        String[] split = command.split(" ");
        for (int j = 0, splitLength = split.length; j < splitLength; j++) {
            String s = split[j];

            if (j + 1 < splitLength) {
                s += " ";
            }
            Style style;
            if (i == -1) {
                style = INFO_STYLE;
            } else {
                style = HIGHLIGHT_STYLES.get(i);
            }
            i++;
            i = i % HIGHLIGHT_STYLES.size();


            text.append(Text.literal(s).setStyle(style));
        }

        addUserLine(text);
    }

    public void addUserLine(Text text) {
        addLineInternal(Text.literal("> ").withColor(Colors.GRAY).append(text));
    }

    public void addLine(Text text) {
        addLineInternal(Text.literal("  ").append(text));
    }

    private void addLineInternal(Text text) {
        sidebar.addLines(text);

        lineCount++;

        if (lineCount > 13) {
            // ughhh
            sidebar.removeLine(sidebar.getLinesFor(null).getFirst());

            lineCount--;
        }
    }

}
