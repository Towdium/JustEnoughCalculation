package me.towdium.jecalculation.nei.adapter;

import javax.annotation.ParametersAreNonnullByDefault;
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
