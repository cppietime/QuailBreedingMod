package com.funguscow.crossbreed.client.renderer;

import com.funguscow.crossbreed.BreedMod;
import com.funguscow.crossbreed.client.model.QuailModel;
import com.funguscow.crossbreed.entity.QuailEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class QuailRenderer extends MobRenderer<QuailEntity, QuailModel<QuailEntity>> {

    protected static final String TEXTURE_TEMPLATE = "textures/entity/quail/%s.png";

    public QuailRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new QuailModel<>(), 0.3f);
    }

    @Override
    public ResourceLocation getEntityTexture(QuailEntity entity) {
        return new ResourceLocation(BreedMod.MODID, String.format(TEXTURE_TEMPLATE, entity.getBreedName()));
    }

    @Override
    protected float handleRotationFloat(QuailEntity livingBase, float partialTicks) {
        float f = MathHelper.lerp(partialTicks, livingBase.oFlap, livingBase.wingRotation);
        float f1 = MathHelper.lerp(partialTicks, livingBase.oFlapSpeed, livingBase.destPos);
        return (MathHelper.sin(f) + 1.0F) * f1;
    }
}
