package net.dungeonescape.common;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ReflectionHandler {
    public static void setField(Object object, String name, Object value) {
        try {
            Field field = object.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(object, value);
            field.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T getField(Class<?> clazz, Object object, String name) {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            T out = (T) field.get(object);
            field.setAccessible(false);
            return out;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T invoke(Class<?> clazz, String name, Object... args) {
        return invoke(clazz, name, Arrays.stream(args).map(Object::getClass).toArray(Class<?>[]::new), args);
    }

    public static <T> T invoke(Class<?> clazz, String name, Class<?>[] parameters, Object... args) {
        try {
            Method method = clazz.getDeclaredMethod(name, parameters);
            method.setAccessible(true);
            T out = (T) method.invoke(null, args);
            method.setAccessible(false);
            return out;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T invoke(Class<?> clazz, Object instance, String name, Class<?>[] parameters, Object... args) {
        try {
            Method method = clazz.getDeclaredMethod(name, parameters);
            method.setAccessible(true);
            T out = (T) method.invoke(instance, args);
            method.setAccessible(false);
            return out;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
