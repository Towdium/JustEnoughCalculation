package me.towdium.jecalculation.command.commands;

import me.towdium.jecalculation.command.ISubCommand;
import me.towdium.jecalculation.data.Controller;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;

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
        Controller.openGuiMath();
    }
}
