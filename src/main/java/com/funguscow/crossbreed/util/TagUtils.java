package com.funguscow.crossbreed.util;

import net.minecraft.nbt.CompoundTag;

public class TagUtils {

    public static int getOrDefault(CompoundTag tag, String key, int defaultValue) {
        if (tag.contains(key)) {
            return tag.getInt(key);
        }
        return defaultValue;
    }

    public static float getOrDefault(CompoundTag tag, String key, float defaultValue) {
        if (tag.contains(key)) {
            return tag.getFloat(key);
        }
        return defaultValue;
    }

    public static double getOrDefault(CompoundTag tag, String key, double defaultValue) {
        if (tag.contains(key)) {
            return tag.getDouble(key);
        }
        return defaultValue;
    }

    public static String getOrDefault(CompoundTag tag, String key, String defaultValue) {
        if (tag.contains(key)) {
            return tag.getString(key);
        }
        return defaultValue;
    }

}
