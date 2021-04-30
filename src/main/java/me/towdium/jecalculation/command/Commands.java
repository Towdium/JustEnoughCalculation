package me.towdium.jecalculation.command;

import me.towdium.jecalculation.command.commands.CommandHelp;
import me.towdium.jecalculation.command.commands.CommandOreDict;
import me.towdium.jecalculation.command.commands.CommandState;
import me.towdium.jecalculation.command.commands.CommandUuid;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;

/**
 * Author: towdium
 * Date:   8/10/17.
 */
@ParametersAreNonnullByDefault
public class Commands {
    public static final HashMap<String, ISubCommand> commands;

    public static final CommandHelp commandHelp = new CommandHelp();
    public static final CommandState commandState = new CommandState();
    public static final CommandOreDict commandOreDict = new CommandOreDict();
    public static final CommandUuid commandUuid = new CommandUuid();

    static {
        commands = new HashMap<>();

        add(commandHelp);
        add(commandState);
        add(commandOreDict);
        add(commandUuid);
    }

    static void add(ISubCommand c) {
        commands.put(c.getName(), c);
    }
}
