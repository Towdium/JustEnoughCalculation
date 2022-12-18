package me.towdium.jecalculation.command;

import java.util.HashMap;
import javax.annotation.ParametersAreNonnullByDefault;
import me.towdium.jecalculation.command.commands.CCraft;
import me.towdium.jecalculation.command.commands.CHelp;
import me.towdium.jecalculation.command.commands.CMath;
import me.towdium.jecalculation.command.commands.CState;

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
        add(new CMath());
        add(new CCraft());
    }

    static void add(ISubCommand c) {
        commands.put(c.getName(), c);
    }
}
