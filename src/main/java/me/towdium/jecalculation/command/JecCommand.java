package me.towdium.jecalculation.command;

import mcp.MethodsReturnNonnullByDefault;
import me.towdium.jecalculation.utils.wrappers.Single;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

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
public class JecCommand extends CommandBase {
    @Override
    public String getName() {
        return "jec";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/jec [help]";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0 || args[0].toLowerCase().equals("help")) {
            Commands.commandHelp.execute(server, sender, cut(args));
        } else {
            ISubCommand cmd = Commands.commands.get(args[0].toLowerCase());
            if (cmd != null) {
                cmd.execute(server, sender, cut(args));
            } else {
                sender.sendMessage(new TextComponentTranslation("command.common.not_found", args[0]));
            }
        }
    }

    private String[] cut(String[] args) {
        if (args.length == 0)
            return new String[0];
        String[] ret = new String[args.length - 1];
        System.arraycopy(args, 1, ret, 0, args.length - 1);
        return ret;
    }

    @Override
    public List<String> getTabCompletions(
            MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 0) {
            return Collections.emptyList();
        }

        String cmd = args[0].toLowerCase();
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, Commands.commands.keySet());
        } else {
            Single<ISubCommand> sub = new Single<>(null);
            Commands.commands.values().stream().filter(c -> c.getName().equals(cmd))
                    .findFirst().ifPresent(sub::push);
            return sub.value != null ? sub.value.getTabCompletions(server, sender, cut(args), targetPos)
                    : Collections.emptyList();
        }
    }
}
