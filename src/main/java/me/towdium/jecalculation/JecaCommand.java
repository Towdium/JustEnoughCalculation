package me.towdium.jecalculation;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.towdium.jecalculation.gui.JecaGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggedOutEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import static me.towdium.jecalculation.utils.Utilities.getPlayer;
import static net.minecraft.ChatFormatting.*;
import static net.minecraft.network.chat.ClickEvent.Action.SUGGEST_COMMAND;

@EventBusSubscriber({Dist.CLIENT})
public class JecaCommand {
    static CommandDispatcher<SharedSuggestionProvider> dispatcher;
    static boolean active;

    @SubscribeEvent
    public static void onOpenGui(ScreenEvent.InitScreenEvent event) {
        if (event.getScreen() instanceof ChatScreen && !active) {
            LiteralArgumentBuilder<SharedSuggestionProvider> lab = LiteralArgumentBuilder.<SharedSuggestionProvider>literal("jeca")
                    .executes((c) -> {
                        String key = "jecalculation.chat.help";
                        getPlayer().displayClientMessage(new TranslatableComponent(key), false);
                        return 0;
                    })
                    .then(LiteralArgumentBuilder.<SharedSuggestionProvider>literal("craft")
                            .executes((c) -> JecaGui.openGuiCraft(null, 0)))
                    .then(LiteralArgumentBuilder.<SharedSuggestionProvider>literal("math")
                            .executes((c) -> JecaGui.openGuiMath(null, 0)));
            dispatcher = new CommandDispatcher<>();
            dispatcher.register(lab);
            getPlayer().connection.getCommands().register(lab);
            active = true;
        }
    }

    @SubscribeEvent
    public static void onLogOut(LoggedOutEvent event) {
        active = false;
        dispatcher = null;
    }

    @SubscribeEvent
    public static void onCommand(ClientChatEvent event) {
        CommandSourceStack cs = getPlayer().createCommandSourceStack();
        String msg = event.getMessage();
        if (msg.startsWith("/jeca ") || msg.equals("/jeca")) {
            event.setCanceled(true);
            Minecraft.getInstance().gui.getChat().addRecentChat(msg);

            try {
                StringReader stringreader = new StringReader(msg);
                if (stringreader.canRead() && stringreader.peek() == '/') stringreader.skip();
                ParseResults<SharedSuggestionProvider> parse = dispatcher.parse(stringreader, cs);
                dispatcher.execute(parse);
            } catch (CommandSyntaxException var7) {
                // copied from net.minecraft.command.Commands
                cs.sendFailure(ComponentUtils.fromMessage(var7.getRawMessage()));
                if (var7.getInput() != null && var7.getCursor() >= 0) {
                    int k = Math.min(var7.getInput().length(), var7.getCursor());
                    MutableComponent tc1 = new TextComponent("").withStyle(GRAY).withStyle(i ->
                            i.withClickEvent(new ClickEvent(SUGGEST_COMMAND, event.getMessage())));
                    if (k > 10) tc1.append("...");
                    tc1.append(var7.getInput().substring(Math.max(0, k - 10), k));
                    if (k < var7.getInput().length()) {
                        MutableComponent tc2 = (new TextComponent(var7.getInput().substring(k)))
                                .withStyle(RED, UNDERLINE);
                        tc1.append(tc2);
                    }
                    tc1.append((new TranslatableComponent("command.context.here"))
                            .withStyle(RED, ITALIC));
                    cs.sendFailure(tc1);
                }
            }
        }
    }
}
