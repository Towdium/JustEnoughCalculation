package me.towdium.jecalculation.command;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.command.commands.CommandHelp;
import me.towdium.jecalculation.command.commands.CommandState;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;

/**
 * Author: towdium
 * Date:   8/10/17.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class Commands {
    public static final HashMap<String, SubCommand> commands;

    public static final CommandHelp commandHelp = new CommandHelp();
    public static final CommandState commandState = new CommandState();

    static {
        commands = new HashMap<>();

        add(commandHelp);
        add(commandState);
    }

    static void add(SubCommand c) {
        commands.put(c.getName(), c);
    }
}
