package me.towdium.jecalculation.command.commands;

import me.towdium.jecalculation.command.ISubCommand;
import me.towdium.jecalculation.data.Controller;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.util.ChatComponentText;

import javax.annotation.ParametersAreNonnullByDefault;

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
        Controller.openGuiCraft();
    }
}
