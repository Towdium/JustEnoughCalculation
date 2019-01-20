package me.towdium.jecalculation.command.commands;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.command.ISubCommand;
import me.towdium.jecalculation.data.Controller;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Author: Towdium
 * Date: 19-1-21
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
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
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        Controller.openGuiMath();
    }
}
