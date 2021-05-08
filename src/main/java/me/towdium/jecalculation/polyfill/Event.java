package me.towdium.jecalculation.polyfill;

import cpw.mods.fml.relauncher.Side;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Deprecated
public @interface Event {

    /**
     * A class which will be subscribed to {@link net.minecraftforge.common.MinecraftForge.EVENT_BUS} at mod construction time.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Deprecated
    @interface EventBusSubscriber {
        Side[] value() default {Side.CLIENT, Side.SERVER};

        /**
         * Optional value, only nessasary if tis annotation is not on the same class that has a @Mod annotation.
         * Needed to prevent early classloading of classes not owned by your mod.
         *
         * @return mod id
         */
        String modid() default "";
    }
}
