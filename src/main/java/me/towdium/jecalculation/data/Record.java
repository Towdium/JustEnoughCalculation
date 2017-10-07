package me.towdium.jecalculation.data;

import me.towdium.jecalculation.data.label.ILabel;
import me.towdium.jecalculation.utils.Utilities;
import me.towdium.jecalculation.utils.Utilities.Recent;
import me.towdium.jecalculation.utils.wrappers.Pair;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Author: towdium
 * Date:   17-10-6.
 */
public abstract class Record {

    public static class RecordSP {
        public static final String KEY_RECIPE = "recipe";
        public static final String KEY_RECENT = "recent";

        public RecordRecipe recipes;
        public RecordRecent recents;

        public RecordSP() {
            recipes = new RecordRecipe();
            recents = new RecordRecent();
        }

        public RecordSP(NBTTagCompound nbt) {
            recipes = new RecordRecipe(nbt.getTagList(KEY_RECIPE, 10));
            recents = new RecordRecent(nbt.getTagList(KEY_RECENT, 10));
        }

        public NBTTagCompound seialize() {
            NBTTagCompound ret = new NBTTagCompound();
            ret.setTag(KEY_RECIPE, recipes.serialize());
            ret.setTag(KEY_RECENT, recents.setialize());
            return ret;
        }
    }

    public static class RecordMP {
        HashMap<UUID, RecordSP> records;

        public RecordSP get(UUID id) {
            return records.get(id);
        }
    }

    public static class RecordRecipe {
        public static final String KEY_NAME = "name";
        public static final String KEY_CONTENT = "content";

        ArrayList<Pair<String, ArrayList<Recipe>>> record = new ArrayList<>();
        HashMap<String, Integer> index = new HashMap<>();

        public RecordRecipe() {
        }

        public RecordRecipe(NBTTagList nbt) {
            StreamSupport.stream(nbt.spliterator(), false).filter(g -> g instanceof NBTTagCompound).forEach(g -> {
                NBTTagCompound group = (NBTTagCompound) g;
                String name = group.getString(KEY_NAME);
                NBTTagList recipes = group.getTagList(KEY_CONTENT, 10);
                StreamSupport.stream(recipes.spliterator(), false).filter(r -> r instanceof NBTTagCompound)
                        .map(r -> (NBTTagCompound) r).forEach(r -> add(name, new Recipe(r)));
            });
        }

        public void add(String group, Recipe recipe) {
            Integer i = index.get(group);
            if (i == null) {
                record.add(new Pair<>(group, new ArrayList<>()));
                i = record.size() - 1;
            }
            record.get(i).two.add(recipe);
        }

        public void set(String group, int index, Recipe recipe) {
            Integer i = this.index.get(group);
            if (i == null) throw new RuntimeException("Group not found: " + group + ".");
            record.get(i).two.set(index, recipe);
        }

        public void remove(String group, int index) {
            Integer i = this.index.get(group);
            if (i == null) throw new RuntimeException("Group not found: " + group + ".");
            record.get(i).two.remove(index);
        }

        public void foreach(BiConsumer<String, List<Recipe>> consumer) {
            record.forEach(p -> consumer.accept(p.one, p.two));
        }

        public NBTTagList serialize() {
            NBTTagList ret = new NBTTagList();
            foreach((n, rs) -> {
                NBTTagCompound nbt = new NBTTagCompound();
                nbt.setString(KEY_NAME, n);
                NBTTagList l = new NBTTagList();
                rs.forEach(r -> l.appendTag(r.serialize()));
                nbt.setTag(KEY_CONTENT, l);
                ret.appendTag(nbt);
            });
            return ret;
        }
    }

    public static class RecordRecent {
        Recent<ILabel> record = new Recent<>(9);

        public RecordRecent(NBTTagList nbt) {
            List<ILabel> ls = StreamSupport.stream(nbt.spliterator(), false)
                    .filter(n -> n instanceof NBTTagCompound)
                    .map(n -> ILabel.DESERIALIZER.deserialize((NBTTagCompound) n))
                    .collect(Collectors.toList());
            new Utilities.ReversedIterator<>(ls).forEachRemaining(l -> record.push(l));
        }

        public RecordRecent() {
        }

        public void push(ILabel label) {
            record.push(label);
        }

        public ILabel getLatest() {
            return record.toList().get(0);
        }

        public List<ILabel> getRecords() {
            List<ILabel> labels = record.toList();
            return labels.subList(1, labels.size());
        }

        public NBTTagList setialize() {
            NBTTagList ret = new NBTTagList();
            record.toList().forEach(l -> ret.appendTag(ILabel.DESERIALIZER.serialize(l)));
            return ret;
        }
    }
}
