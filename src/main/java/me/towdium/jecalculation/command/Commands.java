package me.towdium.jecalculation.command;

import me.towdium.jecalculation.command.commands.CHelp;
import me.towdium.jecalculation.command.commands.COreDict;
import me.towdium.jecalculation.command.commands.CState;
import me.towdium.jecalculation.command.commands.CUuid;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;

/**
 * Author: towdium
 * Date:   8/10/17.
 */
@ParametersAreNonnullByDefault
public class Commands {
    public static final HashMap<String, ISubCommand> commands = new HashMap<>();

    static {
        add(new CHelp());
        add(new CState());
        add(new COreDict());
        add(new CUuid());
    }

    static void add(ISubCommand c) {
        commands.put(c.getName(), c);
    }
}
