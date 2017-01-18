package pers.towdium.just_enough_calculation;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import pers.towdium.just_enough_calculation.util.helpers.ItemStackHelper;

/**
 * Author: Towdium
 * Date:   2016/7/22.
 */
public class JECCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "jec";
    }


    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "JEC debug commands";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length != 0) {
            EntityPlayerMP player = CommandBase.getCommandSenderAsPlayer(sender);
            switch (args[0]) {
                case "container":
                    Fluid f;
                    if (args.length == 1) {
                        f = FluidRegistry.getFluid("water");
                    } else {
                        f = FluidRegistry.getFluid(args[1]);
                        if (f == null)
                            f = FluidRegistry.getFluid("water");
                    }
                    player.inventory.addItemStackToInventory(ItemStackHelper.NBT.setFluid(new ItemStack(JustEnoughCalculation.itemFluidContainer), f));
                    break;
                case "fluids":
                    FluidRegistry.getRegisteredFluids().entrySet().forEach(entry -> player.addChatMessage(new TextComponentString(entry.getKey())));
                    break;
                default:
                    player.addChatMessage(new TextComponentString("Command not found"));
            }
        }
    }
}
