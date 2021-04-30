package me.towdium.jecalculation.command.commands;

import me.towdium.jecalculation.command.ISubCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;

/**
 * Author: towdium
 * Date:   17-9-11.
 */
@ParametersAreNonnullByDefault
public class CommandOreDict implements ISubCommand {
    @Override
    public String getName() {
        return "ore";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/jec ore";
    }

    @Override
    public void execute(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            Arrays.stream(OreDictionary.getOreNames()).forEach(s -> sender.addChatMessage(new ChatComponentText(s)));
        } // TODO error handling
    }
}
