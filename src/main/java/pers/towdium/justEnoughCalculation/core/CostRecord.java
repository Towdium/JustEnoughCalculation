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

    public CostRecord(Recipe recipe, long count, boolean approx){
        output = new ArrayList<>();
        input = new ArrayList<>();
        catalyst = new LinkedList<>();
        boolean b = recipe.isApprox() || approx;
        for(ItemStack itemStack : recipe.output){
            if(itemStack != null){
                ItemRecord record = new ItemRecord(itemStack, count);
                record.approx = b;
                output.add(record);
            }
        }
        for(ItemStack itemStack : recipe.input){
            if(itemStack != null){
                ItemRecord record = new ItemRecord(itemStack, count);
                record.approx = b;
                input.add(record);
                catalyst.add(record.copy());
            }
        }
    }

    /**
     * Generate CostRecord merging two CostRecords
     * ItemRecord with amount 0 may be left there
     */
    public CostRecord(CostRecord costRecord1, CostRecord costRecord2){
        output = new ArrayList<>();
        for (ItemRecord record : costRecord1.output){
            output.add(record.copy());
        }
        input = new ArrayList<>();
        for (ItemRecord record : costRecord1.input){
            input.add(record.copy());
        }
        catalyst = new LinkedList<>();
        for (ItemRecord record : costRecord1.catalyst){
            catalyst.add(record.copy());
        }
        // merge output
        LOOP:
        for(ItemRecord itemRecordAdd : costRecord2.output){
            for(ItemRecord itemRecordExt : output){
                if(itemRecordAdd.isSameType(itemRecordExt)){
                    itemRecordExt.add(itemRecordAdd);
                    continue LOOP;
                }
            }
            output.add(itemRecordAdd.copy());
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
            input.add(itemRecordAdd.copy());
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
                    itemRecordExt.minus(itemRecordAdd);
                    continue LOOP;
                }
            }
        }
        // merge catalyst
        LOOP:
        for(ItemRecord itemRecordAdd : costRecord2.catalyst){
            for (ItemRecord itemRecordExt : catalyst) {
                if (itemRecordAdd.isSameType(itemRecordExt)) {
                    itemRecordExt.add(itemRecordAdd);
                    continue LOOP;
                }
            }
            catalyst.add(itemRecordAdd.copy());
        }
        // cancel input and output
        for(ItemRecord itemRecordIn : input){
            for(ItemRecord itemRecordOut : output){
                if(itemRecordIn.isSameType(itemRecordOut)){
                    itemRecordIn.cancel(itemRecordOut);
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
                for(ItemRecord record : input){
                    if(itemRecord.isSameType(record) && record.amount != 0){
                        continue LOOP;
                    }
                }
                ItemRecord buffer = itemRecord.copy();
                buffer.amount=0;
                builder.add(buffer.toItemStack());
            }
        }
        return builder.build();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CostRecord) {
            for (ItemRecord itemRecordIn : output) {
                for (ItemRecord itemRecordExt : ((CostRecord) obj).output) {
                    if (itemRecordExt.isSameType(itemRecordIn) && itemRecordExt.amount != itemRecordIn.amount) {
                        return false;
                    }
                }
            }
            for (ItemRecord itemRecordIn : input) {
                for (ItemRecord itemRecordExt : ((CostRecord) obj).input) {
                    if (itemRecordExt.isSameType(itemRecordIn) && itemRecordExt.amount != itemRecordIn.amount) {
                        return false;
                    }
                }
            }
            for (ItemRecord itemRecordIn : ((CostRecord) obj).output) {
                for (ItemRecord itemRecordExt : output) {
                    if (itemRecordExt.isSameType(itemRecordIn) && itemRecordExt.amount != itemRecordIn.amount) {
                        return false;
                    }
                }
            }
            for (ItemRecord itemRecordIn : ((CostRecord) obj).input) {
                for (ItemRecord itemRecordExt : input) {
                    if (itemRecordExt.isSameType(itemRecordIn) && itemRecordExt.amount != itemRecordIn.amount) {
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
