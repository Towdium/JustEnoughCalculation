package me.towdium.jecalculation.command.commands;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.command.ICommandSender;

import me.towdium.jecalculation.command.ISubCommand;
import me.towdium.jecalculation.gui.JecaGui;

/**
 * Author: Towdium
 * Date: 19-1-21
 */
@ParametersAreNonnullByDefault
public class CMath implements ISubCommand {

    @Override
    public String getName() {
        return "math";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/jeca math";
    }

    @Override
    public void execute(ICommandSender sender, String[] args) {
        JecaGui.openGuiMath(true);
    }
}
