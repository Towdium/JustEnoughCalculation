package pers.towdium.justEnoughCalculation.core;

import com.google.common.collect.ImmutableList;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Towdium
 */
public class CostRecord{
    List<ItemRecord> output;
    List<ItemRecord> input;
    List<ItemRecord> catalyst;

    public CostRecord(ItemRecord itemRecord){
        output = new ArrayList<>();
        input = new ArrayList<>();
        catalyst = new LinkedList<>();
        input.add(itemRecord);
    }

    public CostRecord(Recipe recipe, long count){
        output = new ArrayList<>();
        input = new ArrayList<>();
        catalyst = new LinkedList<>();
        for(ItemStack itemStack : recipe.output){
            if(itemStack != null){
                output.add(new ItemRecord(itemStack, count));
            }
        }
        for(ItemStack itemStack : recipe.input){
            if(itemStack != null){
                input.add(new ItemRecord(itemStack, count));
                catalyst.add(new ItemRecord(itemStack, count));
            }
        }
    }

    /**
     * Generate CostRecord merging two CostRecords
     * ItemRecord with amount 0 may be left there
     */
    public CostRecord(CostRecord costRecord1, CostRecord costRecord2){
        output = new ArrayList<>();
        output.addAll(costRecord1.output);
        input = new ArrayList<>();
        input.addAll(costRecord1.input);
        catalyst = new LinkedList<>();
        catalyst.addAll(costRecord1.catalyst);
        // merge output
        LOOP:
        for(ItemRecord itemRecordAdd : costRecord2.output){
            for(ItemRecord itemRecordExt : output){
                if(itemRecordAdd.isSameType(itemRecordExt)){
                    itemRecordExt.add(itemRecordAdd);
                    continue LOOP;
                }
            }
            output.add(itemRecordAdd);
        }
        // merge input
        LOOP:
        for(ItemRecord itemRecordAdd : costRecord2.input){
            for(ItemRecord itemRecordExt : input){
                if(itemRecordAdd.isSameType(itemRecordExt)){
                    itemRecordExt.add(itemRecordAdd);
                    continue LOOP;
                }
            }
            input.add(itemRecordAdd);
        }
        // cancel catalyst by output
        LOOP:
        for(ItemRecord itemRecordAdd : costRecord2.output){
            for (int i = 0; i < catalyst.size(); i++) {
                ItemRecord itemRecordExt = catalyst.get(i);
                if (itemRecordAdd.isSameType(itemRecordExt) && itemRecordExt.amount <= itemRecordAdd.amount) {
                    catalyst.remove(i);
                    continue LOOP;
                }else if(itemRecordAdd.isSameType(itemRecordExt)) {
                    itemRecordExt.amount -= itemRecordAdd.amount;
                    continue LOOP;
                }
            }
        }
        // merge catalyst
        LOOP:
        for(ItemRecord itemRecordAdd : costRecord2.catalyst){
            for (ItemRecord itemRecordExt : catalyst) {
                if (itemRecordAdd.isSameType(itemRecordExt)) {
                    itemRecordExt.amount += itemRecordAdd.amount;
                    continue LOOP;
                }
            }
            catalyst.add(itemRecordAdd);
        }
        // cancel input and output
        for(ItemRecord itemRecordIn : input){
            for(ItemRecord itemRecordOut : output){
                if(itemRecordIn.isSameType(itemRecordOut)){
                    long a = itemRecordIn.amount;
                    long b = itemRecordOut.amount;
                    itemRecordIn.amount -= a>b ? b : a;
                    itemRecordOut.amount -= a>b ? b : a;
                }
            }
        }
    }

    public ImmutableList<ItemRecord> getCancellableItems(){
        ImmutableList.Builder<ItemRecord> builder = new ImmutableList.Builder<>();
        for(ItemRecord itemRecord : input){
            if(!itemRecord.isLocked() && itemRecord.amount != 0){
                builder.add(itemRecord);
            }
        }
        return builder.build();
    }

    public ImmutableList<ItemStack> getOutputStack(){
        ImmutableList.Builder<ItemStack> builder = new ImmutableList.Builder<>();
        for(ItemRecord itemRecord : output){
            if(itemRecord.amount != 0){
                builder.add(itemRecord.toItemStack());
            }
        }
        return builder.build();
    }

    public ImmutableList<ItemStack> getInputStack(){
        ImmutableList.Builder<ItemStack> builder = new ImmutableList.Builder<>();
        for(ItemRecord itemRecord : input){
            if(itemRecord.amount != 0){
                builder.add(itemRecord.toItemStack());
            }
        }
        return builder.build();
    }

    public ImmutableList<ItemStack> getCatalystStack(){
        ImmutableList.Builder<ItemStack> builder = new ImmutableList.Builder<>();
        LOOP:
        for(ItemRecord itemRecord : catalyst){
            if(itemRecord.amount != 0){
                for(ItemRecord record : output){
                    if(itemRecord.isSameType(record)){
                        continue LOOP;
                    }
                }
                itemRecord.amount = 0;
                builder.add(itemRecord.toItemStack());
            }
        }
        return builder.build();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CostRecord) {
            for (ItemRecord itemRecordIn : output) {
                for (ItemRecord itemRecordExt : ((CostRecord) obj).output) {
                    if ((itemRecordExt.isSameType(itemRecordIn) && itemRecordExt.amount != itemRecordIn.amount) || !itemRecordExt.isSameType(itemRecordIn)) {
                        return false;
                    }
                }
            }
            for (ItemRecord itemRecordIn : input) {
                for (ItemRecord itemRecordExt : ((CostRecord) obj).input) {
                    if ((itemRecordExt.isSameType(itemRecordIn) && itemRecordExt.amount != itemRecordIn.amount)|| !itemRecordExt.isSameType(itemRecordIn)) {
                        return false;
                    }
                }
            }
            for (ItemRecord itemRecordIn : ((CostRecord) obj).output) {
                for (ItemRecord itemRecordExt : output) {
                    if ((itemRecordExt.isSameType(itemRecordIn) && itemRecordExt.amount != itemRecordIn.amount) || !itemRecordExt.isSameType(itemRecordIn)) {
                        return false;
                    }
                }
            }
            for (ItemRecord itemRecordIn : ((CostRecord) obj).input) {
                for (ItemRecord itemRecordExt : input) {
                    if ((itemRecordExt.isSameType(itemRecordIn) && itemRecordExt.amount != itemRecordIn.amount)|| !itemRecordExt.isSameType(itemRecordIn)) {
                        return false;
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
