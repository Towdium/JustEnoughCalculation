package me.towdium.jecalculation.nei.adapter;

import codechicken.nei.recipe.IRecipeHandler;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("rawtypes")
@ParametersAreNonnullByDefault
public class AdapterUtil {

    static Optional<Class> getClass(String classPath) {
        try {
            return Optional.of(Class.forName(classPath));
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }

}
