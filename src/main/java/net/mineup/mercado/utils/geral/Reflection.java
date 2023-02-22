package net.mineup.mercado.utils.geral;

import org.bukkit.*;
import java.lang.reflect.*;

public class Reflection
{
    private static String versionString;
    
    public static String getVersion() {
        if (Reflection.versionString == null) {
            final String name = Bukkit.getServer().getClass().getPackage().getName();
            Reflection.versionString = String.valueOf(name.substring(name.lastIndexOf(46) + 1)) + ".";
        }
        return Reflection.versionString;
    }
    
    public static Class<?> getNMSClass(final String nmsClassName) {
        final String clazzName = "net.minecraft.server." + getVersion() + nmsClassName;
        Class<?> clazz = null;
        try {
            clazz = Class.forName(clazzName);
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return clazz;
    }
    
    public static Class<?> getOBCClass(final String obcClassName) {
        final String clazzName = "org.bukkit.craftbukkit." + getVersion() + obcClassName;
        Class<?> clazz = null;
        try {
            clazz = Class.forName(clazzName);
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return clazz;
    }
    
    public static Method getMethod(final Class<?> clazz, final String methodName, final Class<?>... params) {
        try {
            final Method method = clazz.getMethod(methodName, params);
            return method;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static Class<?> getClazz(final String clazzName) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(clazzName);
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return clazz;
    }
}
