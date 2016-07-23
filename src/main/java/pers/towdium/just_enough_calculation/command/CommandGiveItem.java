package pers.towdium.just_enough_calculation.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fluids.FluidRegistry;
import pers.towdium.just_enough_calculation.JustEnoughCalculation;
import pers.towdium.just_enough_calculation.util.ItemStackHelper;

/**
 * Author: Towdium
 * Date:   2016/7/22.
 */
@SuppressWarnings("NullableProblems")
public class CommandGiveItem extends CommandBase {

    @Override
    public String getCommandName() {
        return "je_calculation:giveItem";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "Get a testing item";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        CommandBase.getCommandSenderAsPlayer(sender).inventory.addItemStackToInventory(ItemStackHelper.NBT.setFluid(new ItemStack(JustEnoughCalculation.itemFluidContainer), FluidRegistry.getFluid("water")));
    }
}
