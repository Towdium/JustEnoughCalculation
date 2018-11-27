package me.towdium.jecalculation.command;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;

/**
 * Author: towdium
 * Date:   8/10/17.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public interface ISubCommand {
    String getName();

    String getUsage(ICommandSender sender);

    void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException;

    @SuppressWarnings("unused")
    default List<String> getTabCompletions(
            MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return Collections.emptyList();
    }

    default String getKey(String key) {
        return "jecharacters.command." + getName() + '.' + key;
    }
}
