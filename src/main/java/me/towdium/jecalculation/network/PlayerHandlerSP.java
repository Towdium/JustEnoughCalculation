package me.towdium.jecalculation.network;

import com.google.common.collect.ImmutableList;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.core.Recipe;
import me.towdium.jecalculation.item.ItemLabel;
import me.towdium.jecalculation.util.helpers.ItemStackHelper;
import me.towdium.jecalculation.util.wrappers.Pair;
import me.towdium.jecalculation.util.wrappers.Singleton;
import me.towdium.jecalculation.util.wrappers.Trio;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import javax.annotation.Nullable;
import java.io.*;
import java.util.*;
import java.util.function.Function;

/**
 * Author: Towdium
 * Date:   2016/6/28.
 */

public class PlayerHandlerSP implements IProxy.IPlayerHandler {
    List<ItemStack> oreDictPref = new ArrayList<>();
    LinkedHashMap<String, List<Recipe>> recipes = new LinkedHashMap<>();
    HashMap<String, Pair<Integer, ItemStack>> labelCounter = new HashMap<>();

    public void addRecipe(Recipe recipe, String group) {
        Singleton<Boolean> i = new Singleton<>(false);
        recipes.forEach((s, recipesList) -> recipesList.forEach(oneRecipe -> {
            if (oneRecipe.equals(recipe))
                i.value = true;
        }));
        if (!i.value) {
            List<Recipe> list = recipes.get(group);
            if (list == null) {
                List<Recipe> buffer = new ArrayList<>();
                buffer.add(recipe);
                recipes.put(group, buffer);
            } else {
                list.add(recipe);
            }
            updateCounter(recipe, true);
        }
    }

    @Nullable
    public List<Recipe> getRecipeInGroup(String group) {
        return recipes.get(group);
    }

    public String getGroupName(int index) {
        Iterator<Map.Entry<String, List<Recipe>>> it = recipes.entrySet().iterator();
        for (int i = 0; i < index; i++) {
            if (it.hasNext()) {
                it.next();
            } else {
                throw new IndexOutOfBoundsException(index + " out of " + recipes.size());
            }
        }
        if (it.hasNext()) {
            return it.next().getKey();
        } else {
            throw new IndexOutOfBoundsException(index + " out of " + recipes.size());
        }
    }

    public Recipe getRecipe(String group, int index) {
        List<Recipe> temp = recipes.get(group);
        if (temp == null) {
            throw new RuntimeException("Key " + group + " not found in the map.");
        } else {
            return temp.get(index);
        }
    }

    public void removeRecipe(String group, int index) {
        List<Recipe> temp = recipes.get(group);
        if (temp == null) {
            throw new RuntimeException("Key " + group + " not found in the map.");
        } else {
            Recipe r = temp.get(index);
            if (r != null) {
                updateCounter(r, false);
            }
            temp.remove(index);
            if (temp.size() == 0) {
                recipes.remove(group);
            }
        }
    }

    public void setRecipe(String group, String groupOld, int index, Recipe recipe) {
        if (group.equals(groupOld)) {
            Recipe r = recipes.get(groupOld).get(index);
            updateCounter(r, false);
            updateCounter(recipe, true);
            recipes.get(groupOld).set(index, recipe);
        } else {
            removeRecipe(groupOld, index);
            addRecipe(recipe, group);
        }
    }

    public int getSizeRecipe() {
        Singleton<Integer> i = new Singleton<>(0);
        recipes.forEach((s, recipeList) -> i.value += recipeList.size());
        return i.value;
    }

    public int getSizeGroup() {
        return recipes.size();
    }

    public Recipe getRecipeOutput(ItemStack itemStack) {
        Pair<String, Integer> p = getIndexOutput(itemStack).get(0);
        return recipes.get(p.one).get(p.two);
    }

    public List<Recipe> getAllRecipeOutput(ItemStack itemStack) {
        List<Recipe> buffer = new ArrayList<>();
        getIndexOutput(itemStack).forEach(pair -> buffer.add(getRecipe(pair.one, pair.two)));
        return buffer;
    }

    public List<Pair<String, Integer>> getIndexOutput(ItemStack itemStack) {
        return getIndex(recipe -> recipe.getIndexOutput(itemStack));
    }

    public List<Pair<String, Integer>> getIndexCatalyst(ItemStack itemStack) {
        return getIndex(recipe -> recipe.getIndexCatalyst(itemStack));
    }

    public List<Pair<String, Integer>> getIndexInput(ItemStack itemStack) {
        return getIndex(recipe -> recipe.getIndexInput(itemStack));
    }

    List<Pair<String, Integer>> getIndex(Function<Recipe, Integer> func) {
        Singleton<Integer> i = new Singleton<>(0);
        int size = getSizeRecipe();
        List<Trio<String, Integer, Integer>> bufferA = new ArrayList<>(size);
        recipes.forEach((s, recipeList) -> {
            recipeList.forEach(recipe -> {
                bufferA.add(new Trio<>(s, i.value, func.apply(recipe)));
                ++(i.value);
            });
            i.value = 0;
        });
        bufferA.forEach(element -> i.value = i.value < element.three ? element.three : i.value);
        Collections.reverse(bufferA);
        List<List<Pair<String, Integer>>> bufferB = new ArrayList<>(i.value + 1);
        for (int j = i.value; j >= 0; j--) {
            bufferB.add(new ArrayList<>());
        }
        bufferA.forEach(element -> {
            if (element.three != -1)
                bufferB.get(element.three).add(new Pair<>(element.one, element.two));
        });
        List<Pair<String, Integer>> bufferC = new ArrayList<>(size);
        bufferB.forEach(bufferC::addAll);
        return bufferC;
    }

    public int getIndexGroup(String s) {
        int i = 0;
        for (Map.Entry<String, List<Recipe>> stringListEntry : recipes.entrySet()) {
            if (stringListEntry != null && s.equals(stringListEntry.getKey())) {
                return i;
            } else {
                ++i;
            }
        }
        return -1;
    }

    @Nullable
    public ItemStack getOreDictPref(List<ItemStack> stacks) {
        Singleton<ItemStack> buffer = new Singleton<>(null);
        stacks.forEach(itemStack -> {
            if (buffer.value == null)
                oreDictPref.forEach(pref -> {
                    if (buffer.value == null && ItemStackHelper.isItemEqual(itemStack, pref))
                        buffer.value = itemStack.copy();
                });
        });
        return buffer.value;
    }

    public List<ItemStack> getOreDictPref() {
        List<ItemStack> ret = new ArrayList<>();
        oreDictPref.forEach(stack -> ret.add(stack.copy()));
        return ret;
    }

    public void addOreDictPref(ItemStack stack) {
        Singleton<Boolean> flag = new Singleton<>(false);
        oreDictPref.forEach(itemStack -> {
            if (!flag.value && ItemStackHelper.isItemEqual(stack, itemStack))
                flag.value = true;
        });
        if (!flag.value) {
            oreDictPref.add(ItemStackHelper.NBT.setAmount(stack.copy(), 0));
        }
    }

    public void removeOreDictPref(ItemStack stack) {
        oreDictPref.removeIf(aStack -> ItemStackHelper.isItemEqual(aStack, stack));
    }

    @Override
    public void handleLogin(PlayerEvent.LoadFromFile event) {
        oreDictPref = new ArrayList<>();
        recipes = new LinkedHashMap<>();
        try {
            FileInputStream stream = new FileInputStream(event.getPlayerFile("jeca"));
            NBTTagCompound tagCompound = CompressedStreamTools.readCompressed(stream);
            readFromNBT(tagCompound);
        } catch (FileNotFoundException e) {
            JustEnoughCalculation.log.info("Record not found for " + event.getEntityPlayer().getDisplayNameString());
        } catch (IOException | IllegalArgumentException e) {
            JustEnoughCalculation.log.warn("Fail to load records for " + event.getEntityPlayer().getDisplayNameString());
        }
    }

    @Override
    public void handleSave(PlayerEvent.SaveToFile event) {
        try {
            File file = event.getPlayerFile("jeca");
            NBTTagCompound compound = writeToNBT();
            FileOutputStream fileoutputstream = new FileOutputStream(file);
            CompressedStreamTools.writeCompressed(compound, fileoutputstream);
        } catch (Exception e) {
            JustEnoughCalculation.log.warn("Fail to save records for " + event.getEntityPlayer().getDisplayNameString());
        }
    }

    @Override
    public void handleJoin(EntityJoinWorldEvent event) {
    }

    public List<ItemStack> getListLabel() {
        ImmutableList.Builder<ItemStack> b = ImmutableList.builder();
        labelCounter.forEach(((string, pair) -> b.add(pair.two)));
        return b.build();
    }

    public void readFromNBT(NBTTagCompound tag) throws IllegalArgumentException {
        NBTTagList recipes = tag.getTagList("recipes", 10);
        for (int i = 0; i < recipes.tagCount(); i++) {
            NBTTagCompound group = recipes.getCompoundTagAt(i);
            String name = group.getString("name");
            NBTTagList content = group.getTagList("content", 10);
            for (int j = 0; j < content.tagCount(); j++) {
                Recipe r = new Recipe(content.getCompoundTagAt(j));
                addRecipe(r, name);
            }
        }
        NBTTagList oreDictPref = tag.getTagList("oreDictPref", 10);
        for (int i = 0; i < oreDictPref.tagCount(); i++) {
            NBTTagCompound stack = oreDictPref.getCompoundTagAt(i);
            addOreDictPref(new ItemStack(stack));
        }
    }

    void updateCounter(Recipe r, boolean add) {
        updateCounter(r.getCatalyst(), add);
        updateCounter(r.getInput(), add);
        updateCounter(r.getOutput(), add);
    }

    void updateCounter(List<ItemStack> stacks, boolean add) {
        stacks.stream().filter((itemStack -> itemStack != null && itemStack.getItem() instanceof ItemLabel)).forEach(itemStack -> {
            String name = ItemLabel.getName(itemStack);
            if (name == null) {
                JustEnoughCalculation.log.info("An invalid ItemLabel is found in records");
            } else {
                if (labelCounter.containsKey(name)) {
                    Pair<Integer, ItemStack> i = labelCounter.get(name);
                    if (add) {
                        i.one++;
                    } else {
                        if (i.one == 1) {
                            labelCounter.remove(name);
                        } else {
                            i.one--;
                        }
                    }
                } else {
                    if (add) {
                        labelCounter.put(name, new Pair<>(1, ItemLabel.createStack(name)));
                    }
                }
            }
        });
    }

    public NBTTagCompound writeToNBT() {
        NBTTagList recipes = new NBTTagList();
        for (Map.Entry<String, List<Recipe>> entry : this.recipes.entrySet()) {
            NBTTagList buffer = new NBTTagList();
            for (Recipe r : entry.getValue()) {
                buffer.appendTag(r.writeToNbt());
            }
            NBTTagCompound group = new NBTTagCompound();
            group.setString("name", entry.getKey());
            group.setTag("content", buffer);
            recipes.appendTag(group);
        }
        NBTTagList oreDictPref = new NBTTagList();
        for (ItemStack stack : this.oreDictPref) {
            oreDictPref.appendTag(stack.serializeNBT());
        }
        NBTTagCompound ret = new NBTTagCompound();
        ret.setTag("recipes", recipes);
        ret.setTag("oreDictPref", oreDictPref);
        return ret;
    }
}
