package me.towdium.jecalculation.command;

import java.util.Collections;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

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

    default String getKey(String key) {
        return "jecalculation.command." + getName() + '.' + key;
    }
}
