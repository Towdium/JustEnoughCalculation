package pers.towdium.justEnoughCalculation.core;

import net.minecraft.item.ItemStack;
import pers.towdium.justEnoughCalculation.JustEnoughCalculation;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Towdium
 * This is calculating core v3.0, I call it global coordinating calculation XD.
 * The procudure might be more complex, but can avoid some unexpected results.
 */

public class Calculator {
    List<CostRecord> costRecords;
    
    public Calculator(ItemStack itemStack, long amount){
        int count = 0;
        costRecords = new ArrayList<>();
        costRecords.add(new CostRecord(new ItemRecord(itemStack, amount, false)));
        List<ItemRecord> cancellableItems = costRecords.get(0).getCancellableItems();
        LOOP2:
        while(cancellableItems.size() != 0){
            if (count > 100) {
                return;
            }
            // all the items possible tp cancel
            for(ItemRecord itemRecord : cancellableItems){
                // all the recipes for one item
                LOOP1:
                for(Recipe recipe : JustEnoughCalculation.proxy.getPlayerHandler().getAllRecipeOf(itemRecord.toItemStack(), null)){
                    long times = getCount(itemRecord, recipe);
                    ItemRecord buffer = itemRecord.copy();
                    buffer.amount = recipe.getOutputAmount(buffer.toItemStack())*times;
                    CostRecord record = new CostRecord(costRecords.get(costRecords.size()-1), new CostRecord(recipe, times, itemRecord.approx, buffer));
                    for(CostRecord costRecord : costRecords){
                        if(costRecord.equals(record)){
                            continue LOOP1;
                        }
                        costRecords.add(record);
                        count++;
                        cancellableItems = record.getCancellableItems();
                        continue LOOP2;
                    }
                }
            }
            break;
        }
    }

    public CostRecord getCost(){
        return costRecords.get(costRecords.size()-1);
    }

    protected long getCount(ItemRecord itemRecord, Recipe recipe){
        long a = recipe.getOutputAmount(itemRecord.toItemStack());
        return (long)Math.ceil(itemRecord.amount/(double)a);
    }
}
