package me.towdium.jecalculation.data.capacity;

import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.data.structure.Recipes;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

@Deprecated
public class JecaCapabilityProvider implements IExtendedEntityProperties {
    public final static String PROPERTIES_NAME = JustEnoughCalculation.Reference.MODID + ".record";
    public Recipes record;
    protected Entity theEntity;
    protected World theWorld;

    public JecaCapabilityProvider(Recipes record) {
        this.record = record;
    }

    @Override
    public void saveNBTData(NBTTagCompound compound) {
        JustEnoughCalculation.logger.info("ExtendedProperties save NBTData()");
    }

    @Override
    public void loadNBTData(NBTTagCompound compound) {
        JustEnoughCalculation.logger.info("ExtendedProperties load NBTData()");
    }

    @Override
    public void init(Entity entity, World world) {
        JustEnoughCalculation.logger.info("ExtendedProperties init()");
        this.theEntity = entity;
        this.theWorld = world;
    }
}