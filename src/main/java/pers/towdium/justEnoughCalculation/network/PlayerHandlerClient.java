package pers.towdium.justEnoughCalculation.network;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.PlayerEvent;
import pers.towdium.justEnoughCalculation.JustEnoughCalculation;
import pers.towdium.justEnoughCalculation.core.ItemStackWrapper;
import pers.towdium.justEnoughCalculation.core.Recipe;
import pers.towdium.justEnoughCalculation.network.packets.PacketCalculatorUpdate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Towdium
 */
public class PlayerHandlerClient implements IPlayerHandler {
    public List<Recipe> recipes;

    @Override
    public void addRecipe(Recipe recipe, UUID uuid) {
        for (Recipe oneRecipe : recipes){
            if(oneRecipe.equals(recipe)){
                return;
            }
        }
        if(recipe != null){
            recipes.add(recipe);
        }
    }

    @Override
    public boolean getHasRecipeOf(ItemStack itemStack, UUID uuid) {
        for (Recipe recipe : recipes){
            if(recipe.getHasOutput(itemStack)){
                return true;
            }
        }
        return false;
    }

    @Override
    public Recipe getRecipeOf(ItemStack itemStack, UUID uuid) {
        int i = getRecipeIndexOf(itemStack, null);
        if(i == -1){
            return null;
        }else {
            return recipes.get(i);
        }
    }

    @Override
    public ImmutableList<Recipe> getAllRecipeOf(ItemStack itemStack, UUID uuid) {
        ImmutableList.Builder<Recipe> recipeBuilder = new ImmutableList.Builder<>();
        for(Integer index : getAllRecipeIndexOf(itemStack, null)){
            recipeBuilder.add(recipes.get(index));
        }
        return recipeBuilder.build();
    }

    @Override
    public int getRecipeIndexOf(ItemStack itemStack, UUID uuid) {
        int[] result = new int[]{-1, -1, -1, -1};
        for(int[] record : getAllUnsortedRecipeIndexOf(itemStack)){
            result[record[1]] = record[0];
        }
        for (int index : result) {
            if (index != -1) {
                return index;
            }
        }
        return -1;
    }

    @Override
    public ImmutableList<Integer> getAllRecipeIndexOf(ItemStack itemStack, UUID uuid) {
        ImmutableList.Builder<ImmutableList.Builder<Integer>> builder1 = new ImmutableList.Builder<>();
        for(int i=0; i<4; i++){
            builder1.add(new ImmutableList.Builder<Integer>());
        }
        ImmutableList<ImmutableList.Builder<Integer>> resultBuffer = builder1.build();
        for(int[] result : getAllUnsortedRecipeIndexOf(itemStack)){
            resultBuffer.get(result[1]).add(result[0]);
        }
        ImmutableList.Builder<Integer> builder2 = new ImmutableList.Builder<>();
        for(ImmutableList.Builder<Integer> priority : resultBuffer){
            builder2.addAll(priority.build().reverse());
        }
        return builder2.build();
    }

    @Override
    public void removeRecipe(int index, UUID uuid) {
        recipes.remove(index);
    }

    @Override
    public void setRecipe(Recipe recipe, int index, UUID uuid) {
        recipes.set(index, recipe);
    }

    @Override
    public Recipe getRecipe(int index, UUID uuid) {
        return recipes.get(index);
    }

    @Override
    public void syncItemCalculator(ItemStack itemIn, String string) {
        ItemStack itemStack = Minecraft.getMinecraft().thePlayer.getHeldItem();
        ItemStackWrapper.NBT.setItem(itemStack, "dest",itemIn );
        ItemStackWrapper.NBT.setString(itemStack, "text", string);
        JustEnoughCalculation.networkWrapper.sendToServer(new PacketCalculatorUpdate(itemStack));
    }

    @Override
    public void handleLogin(PlayerEvent.LoadFromFile event) {
        try {
            FileInputStream stream = new FileInputStream(event.getPlayerFile("jeca"));
            NBTTagCompound tagCompound = CompressedStreamTools.readCompressed(stream);
            readFromNBT(tagCompound);
        } catch (Exception e) {
            recipes = new ArrayList<>();
        }
    }

    @Override
    public void handleSave(PlayerEvent.SaveToFile event) {
        try{
            File file = event.getPlayerFile("jeca");
            NBTTagCompound compound = new NBTTagCompound();
            writeToNBT(compound);
            FileOutputStream fileoutputstream = new FileOutputStream(file);
            CompressedStreamTools.writeCompressed(compound, fileoutputstream);
        }catch (Exception e){
            JustEnoughCalculation.log.warn("Fail to save records for"+event.entityPlayer.getDisplayName());
        }
    }

    ImmutableList<int[]> getAllUnsortedRecipeIndexOf(ItemStack itemStack){
        ImmutableList.Builder<int[]> builder = new ImmutableList.Builder<>();
        int size = recipes.size();
        int buffer;
        for(int i = 0; i<size; i++){
            buffer = recipes.get(i).getOutputIndex(itemStack);
            if(buffer != -1){
                builder.add(new int[]{i, buffer});
            }
        }
        return builder.build();
    }

    public void readFromNBT(NBTTagCompound tagCompound){
        int amount = tagCompound.getInteger("amount");
        List<Recipe> recipes = new ArrayList<>(amount);
        for(int i=0; i<amount; i++){
            recipes.add(Recipe.NBTUtl.fromNBT((NBTTagCompound) tagCompound.getTag(String.valueOf(i))));
        }
        this.recipes = recipes;
    }

    public void writeToNBT(NBTTagCompound tagCompound){
        tagCompound.setInteger("amount", recipes.size());
        int index = 0;
        for(Recipe recipe : recipes){
            tagCompound.setTag(String.valueOf(index++), Recipe.NBTUtl.toNBT(recipe));
        }
    }
}
