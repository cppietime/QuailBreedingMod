package com.funguscow.crossbreed.client.renderer;

import com.funguscow.crossbreed.BreedMod;
import com.funguscow.crossbreed.client.model.QuailModel;
import com.funguscow.crossbreed.entity.QuailEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class QuailRenderer extends MobRenderer<QuailEntity, QuailModel<QuailEntity>> {

    protected static final String TEXTURE_TEMPLATE = "textures/entity/quail/%s.png";

    public QuailRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new QuailModel(renderManagerIn.bakeLayer(QuailModel.LAYER_LOCATION)), 0.3f);
    }

    @Override
    public ResourceLocation getTextureLocation(QuailEntity entity) {
        return new ResourceLocation(BreedMod.MODID, String.format(TEXTURE_TEMPLATE, entity.getBreedName()));
    }

    @Override
    protected float getBob(QuailEntity livingBase, float partialTicks) {
        float f = Mth.lerp(partialTicks, livingBase.oFlap, livingBase.wingRotation);
        float f1 = Mth.lerp(partialTicks, livingBase.oFlapSpeed, livingBase.destPos);
        return (Mth.sin(f) + 1.0F) * f1;
    }
}
