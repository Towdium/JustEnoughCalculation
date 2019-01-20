package me.towdium.jecalculation.command;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.command.commands.CCraft;
import me.towdium.jecalculation.command.commands.CHelp;
import me.towdium.jecalculation.command.commands.CMath;
import me.towdium.jecalculation.command.commands.CState;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;

/**
 * Author: towdium
 * Date:   8/10/17.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class Commands {
    public static final HashMap<String, ISubCommand> commands = new HashMap<>();

    static {
        add(new CHelp());
        add(new CState());
        add(new CCraft());
        add(new CMath());
    }

    static void add(ISubCommand c) {
        commands.put(c.getName(), c);
    }
}
