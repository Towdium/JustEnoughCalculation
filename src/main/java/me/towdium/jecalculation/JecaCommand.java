package me.towdium.jecalculation;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.towdium.jecalculation.gui.JecaGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggedOutEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import static net.minecraft.util.text.TextFormatting.*;
import static net.minecraft.util.text.event.ClickEvent.Action.SUGGEST_COMMAND;

@EventBusSubscriber({Dist.CLIENT})
public class JecaCommand {
    static CommandDispatcher<ISuggestionProvider> dispatcher;
    static boolean active;

    @SubscribeEvent
    public static void onOpenGui(InitGuiEvent event) {
        if (event.getGui() instanceof ChatScreen && !active) {
            LiteralArgumentBuilder<ISuggestionProvider> lab = LiteralArgumentBuilder.<ISuggestionProvider>literal("jeca")
                    .executes((c) -> {
                        String key = "jecalculation.chat.help";
                        Minecraft.getInstance().player.sendMessage(new TranslationTextComponent(key));
                        return 0;
                    })
                    .then(LiteralArgumentBuilder.<ISuggestionProvider>literal("craft")
                            .executes((c) -> JecaGui.openGuiCraft(null)))
                    .then(LiteralArgumentBuilder.<ISuggestionProvider>literal("math")
                            .executes((c) -> JecaGui.openGuiMath(null)));
            dispatcher = new CommandDispatcher<>();
            dispatcher.register(lab);
            Minecraft.getInstance().player.connection.func_195515_i().register(lab);
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
        CommandSource cs = Minecraft.getInstance().player.getCommandSource();
        String msg = event.getMessage();
        if (msg.startsWith("/jeca ") || msg.equals("/jeca")) {
            event.setCanceled(true);
            Minecraft.getInstance().ingameGUI.getChatGUI().addToSentMessages(msg);

            try {
                StringReader stringreader = new StringReader(msg);
                if (stringreader.canRead() && stringreader.peek() == '/') stringreader.skip();
                ParseResults<ISuggestionProvider> parse = dispatcher.parse(stringreader, cs);
                dispatcher.execute(parse);
            } catch (CommandSyntaxException var7) {
                // copied from net.minecraft.command.Commands
                cs.sendErrorMessage(TextComponentUtils.toTextComponent(var7.getRawMessage()));
                if (var7.getInput() != null && var7.getCursor() >= 0) {
                    int k = Math.min(var7.getInput().length(), var7.getCursor());
                    ITextComponent tc1 = new StringTextComponent("").applyTextStyle(GRAY).applyTextStyle(i ->
                            i.setClickEvent(new ClickEvent(SUGGEST_COMMAND, event.getMessage())));
                    if (k > 10) tc1.appendText("...");
                    tc1.appendText(var7.getInput().substring(Math.max(0, k - 10), k));
                    if (k < var7.getInput().length()) {
                        ITextComponent tc2 = (new StringTextComponent(var7.getInput().substring(k)))
                                .applyTextStyles(RED, UNDERLINE);
                        tc1.appendSibling(tc2);
                    }
                    tc1.appendSibling((new TranslationTextComponent("command.context.here"))
                            .applyTextStyles(RED, ITALIC));
                    cs.sendErrorMessage(tc1);
                }
            }
        }
    }
}
