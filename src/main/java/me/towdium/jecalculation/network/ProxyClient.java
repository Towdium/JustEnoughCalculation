package me.towdium.jecalculation.network;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.client.gui.JecGui;
import me.towdium.jecalculation.client.gui.guis.GuiCalculator;
import me.towdium.jecalculation.command.JecaCommand;
import me.towdium.jecalculation.event.Handlers;
import me.towdium.jecalculation.item.ItemCalculator;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.ClientCommandHandler;
import org.lwjgl.input.Keyboard;

public class ProxyClient implements IProxy {
    public static final KeyBinding keyOpenGui =
            new KeyBinding("key.open_gui", Keyboard.KEY_NONE, "key.category");


    @Override
    public void initPost() {
        GameRegistry.registerItem(ItemCalculator.INSTANCE, ItemCalculator.INSTANCE.getUnlocalizedName());
        Handlers.handlers.forEach(FMLCommonHandler.instance().bus()::register);
        ClientCommandHandler.instance.registerCommand(new JecaCommand());
        new JecGui(null, new GuiCalculator());
    }

    @Override
    public void init() {
        JustEnoughCalculation.logger.info("client proxy init");
        ClientRegistry.registerKeyBinding(keyOpenGui);
    }


    @Override
    public void displayCalculator() {
        JecGui.displayGui(new GuiCalculator());
    }
}
