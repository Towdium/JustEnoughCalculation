package me.towdium.jecalculation.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;

/**
 * Author: towdium
 * Date:   8/10/17.
 */
@ParametersAreNonnullByDefault
public interface ISubCommand {
    String getName();

    String getUsage(ICommandSender sender);

    void execute(ICommandSender sender, String[] args) throws CommandException;

    @SuppressWarnings("unused")
    default List<String> getTabCompletions(ICommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
