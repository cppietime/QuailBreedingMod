package com.funguscow.crossbreed.client.model;

import com.funguscow.crossbreed.BreedMod;
import com.funguscow.crossbreed.entity.QuailEntity;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class QuailModel extends AgeableListModel<QuailEntity> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(BreedMod.MODID, QuailEntity.ID), "main");

    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart wingLeft;
    private final ModelPart wingRight;
    private final ModelPart legLeft;
    private final ModelPart legRight;

    public QuailModel(ModelPart root) {
        body = root.getChild("body");
        head = root.getChild("head");
        wingLeft = root.getChild("wingLeft");
        wingRight = root.getChild("wingRight");
        legLeft = root.getChild("legLeft");
        legRight = root.getChild("legRight");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition parts = mesh.getRoot();

        parts.addOrReplaceChild("body",
                CubeListBuilder.create()
                        .texOffs(0, 20)
                        .addBox(-3, -6, -3, 6, 6, 6, new CubeDeformation(0))
                        .texOffs(0, 16)
                        .addBox(-2, -3, 3, 4, 2, 2, new CubeDeformation(0)),
                PartPose.offset(0, 21, 0));

		/*body = new ModelPart(this);
		body.setRotationPoint(0.0F, 21.0F, 0.0F);
		body.setTextureOffset(0, 20).addBox(-3.0F, -6.0F, -3.0F, 6.0F, 6.0F, 6.0F, 0.0F, false);
		body.setTextureOffset(0, 16).addBox(-2.0F, -3.0F, 3.0F, 4.0F, 2.0F, 2.0F, 0.0F, false);*/

        parts.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-2, -4, -3, 4, 4, 3, new CubeDeformation(0))
                        .texOffs(18, 0)
                        .addBox(-2, -2, -5, 4, 1, 2, new CubeDeformation(0))
                        .texOffs(14, 0)
                        .addBox(-1, -6, -2, 1, 2, 1, new CubeDeformation(0)),
                PartPose.offset(0, 16, -3)
        );

		/*head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, 16.0F, -3.0F);
		head.setTextureOffset(0, 0).addBox(-2.0F, -4.0F, -3.0F, 4.0F, 4.0F, 3.0F, 0.0F, false);
		head.setTextureOffset(18, 0).addBox(-2.0F, -2.0F, -5.0F, 4.0F, 1.0F, 2.0F, 0.0F, false);
		head.setTextureOffset(14, 0).addBox(-1.0F, -6.0F, -2.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);*/

        parts.addOrReplaceChild("wingLeft",
                CubeListBuilder.create()
                        .texOffs(14, 3)
                        .addBox(-1, 0, -1, 1, 4, 6, new CubeDeformation(0)),
                PartPose.offset(-3, 16, -1));

		/*wingLeft = new ModelRenderer(this);
		wingLeft.setRotationPoint(-3.0F, 16.0F, -1.0F);
		wingLeft.setTextureOffset(14, 3).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 4.0F, 6.0F, 0.0F, false);*/

        parts.addOrReplaceChild("wingRight",
                CubeListBuilder.create()
                        .texOffs(14, 3)
                        .addBox(0, 0, -1, 1, 4, 6, new CubeDeformation(0)),
                PartPose.offset(3, 16, -1));

		/*wingRight = new ModelRenderer(this);
		wingRight.setRotationPoint(3.0F, 16.0F, -1.0F);
		wingRight.setTextureOffset(14, 3).addBox(0.0F, 0.0F, -1.0F, 1.0F, 4.0F, 6.0F, 0.0F, false);*/

        parts.addOrReplaceChild("legLeft",
                CubeListBuilder.create()
                        .texOffs(0, 7)
                        .addBox(-1, 0, -1, 1, 3, 1, new CubeDeformation(0)),
                PartPose.offset(-1, 21, 2));

		/*legLeft = new ModelRenderer(this);
		legLeft.setRotationPoint(-1.0F, 21.0F, 2.0F);
		legLeft.setTextureOffset(0, 7).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);*/

        parts.addOrReplaceChild("legRight",
                CubeListBuilder.create()
                        .texOffs(0, 7)
                        .addBox(0, 0, -1, 1, 3, 1, new CubeDeformation(0)),
                PartPose.offset(1, 21, 2));

		/*legRight = new ModelRenderer(this);
		legRight.setRotationPoint(1.0F, 21.0F, 2.0F);
		legRight.setTextureOffset(0, 7).addBox(0.0F, 0.0F, -1.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);*/

        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public void setupAnim(@NotNull QuailEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.xRot = headPitch * ((float) Math.PI / 180F);
        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        this.body.xRot = 0;//((float)Math.PI / 2F);
        this.legRight.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.legLeft.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        this.wingRight.zRot = -ageInTicks;
        this.wingLeft.zRot = ageInTicks;
    }

    @Override
    protected @NotNull Iterable<ModelPart> headParts() {
        return ImmutableList.of(head);
    }

    @Override
    protected @NotNull Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(body, legLeft, legRight, wingLeft, wingRight);
    }
}