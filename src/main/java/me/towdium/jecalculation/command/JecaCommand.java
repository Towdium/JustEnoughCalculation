package me.towdium.jecalculation.command;

import me.towdium.jecalculation.command.commands.CHelp;
import me.towdium.jecalculation.utils.wrappers.Wrapper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentTranslation;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;

/**
 * Author: towdium
 * Date:   8/10/17.
 */
@ParametersAreNonnullByDefault
public class JecaCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "jeca";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/jeca [options]";
    }


    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            Commands.commands.get(CHelp.NAME).execute(sender, cut(args));
        } else {
            ISubCommand cmd = Commands.commands.get(args[0].toLowerCase());
            if (cmd != null) {
                cmd.execute(sender, cut(args));
            } else {
                sender.addChatMessage(new ChatComponentTranslation("command.common.not_found", args[0]));
            }
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    private String[] cut(String[] args) {
        if (args.length == 0)
            return new String[0];
        String[] ret = new String[args.length - 1];
        System.arraycopy(args, 1, ret, 0, args.length - 1);
        return ret;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            return Collections.emptyList();
        }

        String cmd = args[0].toLowerCase();
        if (args.length == 1) {
            return getListOfStringsFromIterableMatchingLastWord(args, Commands.commands.keySet());
        } else {
            Wrapper<ISubCommand> sub = new Wrapper<>(null);
            Commands.commands.values().stream().filter(c -> c.getName().equals(cmd))
                    .findFirst().ifPresent(sub::push);
            return sub.value != null ? sub.value.getTabCompletions(sender, cut(args))
                    : Collections.emptyList();
        }
    }


}
