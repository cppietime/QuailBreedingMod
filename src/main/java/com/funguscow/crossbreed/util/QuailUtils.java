package com.funguscow.crossbreed.util;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;

public class QuailUtils {

    public static MutableComponent translatable(String key) {
        return MutableComponent.create(new TranslatableContents(key));
    }

}
