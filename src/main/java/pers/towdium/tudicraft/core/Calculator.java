package pers.towdium.tudicraft.core;

import com.google.common.collect.ImmutableList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import pers.towdium.tudicraft.Tudicraft;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Towdium
 */
public class Calculator {
    List<OperateRecord> operateRecords;
    OperateRecord currentOperate;
    List<Calculator> calculators;
    List<ItemRecord> catalystBuffer;
    ItemRecord itemRecord;


    public Calculator (ItemStack itemStack, int amount){
        this(new ItemRecord(itemStack, amount), new LinkedList<OperateRecord>(), new LinkedList<ItemRecord>(), true);
    }

    public Calculator (ItemRecord itemRecord, List<OperateRecord> operateRecords, List<ItemRecord> catalystBuffer, boolean exceedMode){
        this.catalystBuffer = catalystBuffer;
        this.itemRecord = itemRecord;
        this.operateRecords = operateRecords;
        for (Recipe recipe : Tudicraft.proxy.getPlayerHandler().getAllRecipeOf(new ItemStack(itemRecord.item, itemRecord.meta))){
            if(checkRecipeValid(recipe)){
                long count = getOperateCount(recipe, exceedMode);
                this.currentOperate = new OperateRecord(recipe, count);
                ImmutableList.Builder<OperateRecord> builder = new ImmutableList.Builder<>();
                builder.addAll(operateRecords);
                builder.add(currentOperate);
                ImmutableList<OperateRecord> records = builder.build();
                ImmutableList.Builder<Calculator> calculatorBuilder = new ImmutableList.Builder<>();
                for(ItemStack itemStack : recipe.input){
                    if(itemStack != null){
                        calculatorBuilder.add(new Calculator(
                                new ItemRecord(itemStack, count*recipe.getInputAmount(itemStack)), records, catalystBuffer, true
                        ));
                    }
                }
                this.calculators = calculatorBuilder.build();
                return;
            }
        }
        catalystBuffer.add(itemRecord);
    }

    @Override
    public String toString() {
        return "Calculator:" + itemRecord.item.getUnlocalizedName() + " x " + itemRecord.amount;
    }

    public CostRecord getCost(){
        if(currentOperate != null){
            CostRecord record = getCurrentCost();
            for(Calculator calculator : calculators){
                if(calculator.currentOperate != null){
                    CostRecord buffer = calculator.getCost();
                    record.merge(buffer);
                }
            }
            return record;
        }else {
            return new CostRecord();
        }
    }

    public CostRecord getCurrentCost(){
        List<ItemRecord> output = new LinkedList<>();
        List<ItemRecord> input = new LinkedList<>();
        for(ItemStack itemStack : currentOperate.recipe.output){
            if(itemStack != null){
                output.add(new ItemRecord(itemStack, currentOperate.count*itemStack.stackSize));
            }
        }
        for(ItemStack itemStack : currentOperate.recipe.input){
            if(itemStack != null){
                input.add(new ItemRecord(itemStack, currentOperate.count*itemStack.stackSize));
            }
        }
        return new Calculator.CostRecord(output, input);
    }

    protected boolean checkRecipeValid(Recipe recipe){
        for (OperateRecord record : operateRecords){
            if(record.recipe.equals(recipe) && record.count == getOperateCount(recipe)){
                return false;
            }
        }
        return true;
    }

    protected long getOperateCount(Recipe recipe){
        return (int)Math.ceil((float)itemRecord.amount/itemRecord.getOutputAmount(recipe));
    }

    protected long getOperateCount(Recipe recipe, boolean exceed){
        if(exceed){
            return (int)Math.ceil((float)itemRecord.amount/itemRecord.getOutputAmount(recipe));
        } else {
            return itemRecord.amount/itemRecord.getOutputAmount(recipe);
        }


    }

    public class CostRecord{
        List<ItemRecord> output;
        List<ItemRecord> input;
        List<ItemRecord> catalyst;

        public CostRecord(){
            output = new ArrayList<>();
            input = new ArrayList<>();
        }

        public CostRecord(List<ItemRecord> output, List<ItemRecord> input) {
            this.output = output;
            this.input = input;
        }

        public void merge(CostRecord costRecord){
            LOOP1:
            for(ItemRecord record : costRecord.output){
                for(ItemRecord record1 : output){
                    if(record1.add(record))
                        continue LOOP1;
                }
                output.add(record);
            }
            LOOP2:
            for(ItemRecord record : costRecord.input){
                for(ItemRecord record1 : input){
                    if(record1.add(record))
                        continue LOOP2;
                }
                input.add(record);
            }
        }

        public void unify(){
            cancelIO();
            for (int i = 0; i < output.size(); i++) {
                ItemRecord itemRecord = output.get(i);
                if (!itemRecord.isSameType(Calculator.this.itemRecord)) {
                    CostRecord record = new Calculator(itemRecord, new LinkedList<OperateRecord>(), new LinkedList<ItemRecord>(), true).getCost();
                    record.cancelIO();
                    if (this.canCancelBy(record)) {
                        List<ItemRecord> buffer = record.input;
                        record.input = record.output;
                        record.output = buffer;
                        this.merge(record);
                        this.cancelIO();
                    } else {
                        record = new Calculator(itemRecord, new LinkedList<OperateRecord>(), new LinkedList<ItemRecord>(), false).getCost();
                        record.cancelIO();
                        if(this.canCancelBy(record)) {
                            List<ItemRecord> buffer = record.input;
                            record.input = record.output;
                            record.output = buffer;
                            this.merge(record);
                            this.cancelIO();
                        }
                    }
                }
            }
            cancelEmpty();
        }

        protected boolean canCancelBy(CostRecord record){
            LOOP1:
            for(ItemRecord itemRecIn : record.output){
                for(ItemRecord itemRec : output){
                    if(itemRec.isSameType(itemRecIn) && itemRec.amount>=itemRecIn.amount){
                        continue LOOP1;
                    }
                }
                return false;
            }
            LOOP2:
            for(ItemRecord itemRecIn : record.input){
                for(ItemRecord itemRec : input){
                    if(itemRec.isSameType(itemRecIn) && itemRec.amount>=itemRecIn.amount){
                        continue LOOP2;
                    }
                }
                return false;
            }
            return true;
        }

        protected void cancelIO(){
            ImmutableList.Builder<ItemRecord> catalystBuilder = new ImmutableList.Builder<>();
            if(catalyst != null){
                catalystBuilder.addAll(catalyst);
            }
            for (ItemRecord inputRec : input) {
                for (ItemRecord outputRec : output) {
                    if (inputRec.cancel(outputRec) && inputRec.amount == 0) {
                        LOOP:
                        for (ItemRecord itemRecord : Calculator.this.catalystBuffer){
                            if(itemRecord.isSameType(inputRec)){
                                if(catalyst != null){
                                    for (ItemRecord exist : catalyst) {
                                        if (itemRecord.isSameType(exist)) {
                                            continue LOOP;
                                        }
                                    }
                                }
                                catalystBuilder.add(inputRec.copy());
                                break;
                            }
                        }
                    }
                }
            }
            catalyst = catalystBuilder.build();
        }

        protected void cancelEmpty(){
            ImmutableList.Builder<ItemRecord> builder = new ImmutableList.Builder<>();
            for(ItemRecord record : output){
                if(record.amount != 0){
                    builder.add(record);
                }
            }
            output = builder.build();
            builder = new ImmutableList.Builder<>();
            for(ItemRecord record : input){
                if(record.amount != 0){
                    builder.add(record);
                }
            }
            input = builder.build();
        }

        public ItemStack[] getOutputStack(){
            ItemStack[] outputStack = new ItemStack[output.size()];
            int i = 0;
            for (ItemRecord record : output){
                outputStack[i] = record.toItemStack();
                i++;
            }
            return outputStack;
        }

        public ItemStack[] getInputStack(){
            ItemStack[] inputStack = new ItemStack[input.size()];
            int i = 0;
            for (ItemRecord record : input){
                inputStack[i] = record.toItemStack();
                i++;
            }
            return inputStack;
        }

        public ItemStack[] getCatalystStack(){
            ItemStack[] catalystStack = new ItemStack[catalyst.size()];
            int i = 0;
            for (ItemRecord record : catalyst){
                catalystStack[i] = record.toItemStack();
                i++;
            }
            return catalystStack;
        }
    }
}

class OperateRecord{
    Recipe recipe;
    long count;

    public OperateRecord(Recipe recipe, long count) {
        this.recipe = recipe;
        this.count = count;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof OperateRecord)){
            return false;
        } else {
            OperateRecord record = (OperateRecord) obj;
            return record.recipe.equals(recipe) && record.count == count;
        }
    }
}