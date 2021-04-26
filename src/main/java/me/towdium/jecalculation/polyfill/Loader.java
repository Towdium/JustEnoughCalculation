package me.towdium.jecalculation.utils.polyfill;


import cpw.mods.fml.common.eventhandler.EventBus;
import me.towdium.jecalculation.JustEnoughCalculation;
import net.minecraftforge.common.MinecraftForge;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Loader {

    public static void subscribeToEventBus() {
        Set<Class<?>> classes = getAllClasses("me.towdium.jecalculation");
        classes.stream().filter((cls) -> cls.isAnnotationPresent(Event.EventBusSubscriber.class)).forEach((cls) -> {
            JustEnoughCalculation.logger.info("loading class" + cls.getName());
            try {
                MinecraftForge.EVENT_BUS.register(cls.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

    private static Set<Class<?>> getAllClasses(String packageName) {
        List<ClassLoader> classLoadersList = new LinkedList<ClassLoader>();
        classLoadersList.add(ClasspathHelper.contextClassLoader());
        classLoadersList.add(ClasspathHelper.staticClassLoader());

        Reflections reflections = new Reflections(new ConfigurationBuilder().setScanners(
                new SubTypesScanner(false /* don't exclude Object.class */), new ResourcesScanner()).setUrls(
                ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0]))).filterInputsBy(
                new FilterBuilder().include(FilterBuilder.prefix(packageName))));
        return reflections.getSubTypesOf(Object.class);
    }
}
