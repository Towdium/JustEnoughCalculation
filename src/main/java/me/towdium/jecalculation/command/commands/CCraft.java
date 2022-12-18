package me.towdium.jecalculation.command.commands;

import javax.annotation.ParametersAreNonnullByDefault;
import me.towdium.jecalculation.command.ISubCommand;
import me.towdium.jecalculation.gui.JecaGui;
import net.minecraft.command.ICommandSender;

/**
 * Author: Towdium
 * Date: 19-1-20
 */
@ParametersAreNonnullByDefault
public class CCraft implements ISubCommand {
    @Override
    public String getName() {
        return "craft";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/jeca craft";
    }

    @Override
    public void execute(ICommandSender sender, String[] args) {
        JecaGui.openGuiCraft(true);
    }
}
