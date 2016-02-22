package pers.towdium.tudicraft.network;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import pers.towdium.tudicraft.Tudicraft;
import pers.towdium.tudicraft.core.ItemStackWrapper;
import pers.towdium.tudicraft.core.Recipe;
import pers.towdium.tudicraft.gui.calculator.ContainerCalculator;
import pers.towdium.tudicraft.network.packages.PackageCalculatorUpdate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Towdium
 */
public class PlayerHandlerClient implements IPlayerHandler {
    List<Recipe> recipes = new ArrayList<>();

    @Override
    public void addRecipe(Recipe recipe) {
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
    public boolean getHasRecipeOf(ItemStack itemStack) {
        for (Recipe recipe : recipes){
            if(recipe.getHasOutput(itemStack)){
                return true;
            }
        }
        return false;
    }

    @Override
    public Recipe getRecipeOf(ItemStack itemStack) {
        int i = getRecipeIndexOf(itemStack);
        if(i == -1){
            return null;
        }else {
            return recipes.get(i);
        }
    }

    @Override
    public ImmutableList<Recipe> getAllRecipeOf(ItemStack itemStack) {
        ImmutableList.Builder<Recipe> recipeBuilder = new ImmutableList.Builder<>();
        for(Integer index : getAllRecipeIndexOf(itemStack)){
            recipeBuilder.add(recipes.get(index));
        }
        return recipeBuilder.build();
    }

    @Override
    public int getRecipeIndexOf(ItemStack itemStack) {
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
    public ImmutableList<Integer> getAllRecipeIndexOf(ItemStack itemStack) {
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
    public void removeRecipe(int index) {
        recipes.remove(index);
    }

    @Override
    public void setRecipe(Recipe recipe, int index) {
        recipes.set(index, recipe);
    }

    @Override
    public Recipe getRecipe(int index) {
        return recipes.get(index);
    }

    @Override
    public void syncItemCalculator(ItemStack itemIn, String string) {
        ItemStack itemStack = Minecraft.getMinecraft().thePlayer.getHeldItem();
        ItemStackWrapper.NBT.setItem(itemStack, "dest",itemIn );
        ItemStackWrapper.NBT.setString(itemStack, "text", string);
        Tudicraft.networkWrapper.sendToServer(new PackageCalculatorUpdate(itemStack));
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

}
