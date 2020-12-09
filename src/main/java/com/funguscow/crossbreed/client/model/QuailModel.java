package com.funguscow.crossbreed.client.model;

import com.funguscow.crossbreed.entity.QuailEntity;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.entity.model.AgeableModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class QuailModel<T extends QuailEntity> extends AgeableModel<QuailEntity> {
	private final ModelRenderer body;
	private final ModelRenderer head;
	private final ModelRenderer wingLeft;
	private final ModelRenderer wingRight;
	private final ModelRenderer legLeft;
	private final ModelRenderer legRight;

	public QuailModel() {
		textureWidth = 32;
		textureHeight = 32;

		body = new ModelRenderer(this);
		body.setRotationPoint(0.0F, 21.0F, 0.0F);
		body.setTextureOffset(0, 20).addBox(-3.0F, -6.0F, -3.0F, 6.0F, 6.0F, 6.0F, 0.0F, false);
		body.setTextureOffset(0, 16).addBox(-2.0F, -3.0F, 3.0F, 4.0F, 2.0F, 2.0F, 0.0F, false);

		head = new ModelRenderer(this);
		head.setRotationPoint(0.0F, 16.0F, -3.0F);
		head.setTextureOffset(0, 0).addBox(-2.0F, -4.0F, -3.0F, 4.0F, 4.0F, 3.0F, 0.0F, false);
		head.setTextureOffset(18, 0).addBox(-2.0F, -2.0F, -5.0F, 4.0F, 1.0F, 2.0F, 0.0F, false);
		head.setTextureOffset(14, 0).addBox(-1.0F, -6.0F, -2.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);

		wingLeft = new ModelRenderer(this);
		wingLeft.setRotationPoint(-3.0F, 16.0F, -1.0F);
		wingLeft.setTextureOffset(14, 3).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 4.0F, 6.0F, 0.0F, false);

		wingRight = new ModelRenderer(this);
		wingRight.setRotationPoint(3.0F, 16.0F, -1.0F);
		wingRight.setTextureOffset(14, 3).addBox(0.0F, 0.0F, -1.0F, 1.0F, 4.0F, 6.0F, 0.0F, false);

		legLeft = new ModelRenderer(this);
		legLeft.setRotationPoint(-1.0F, 21.0F, 2.0F);
		legLeft.setTextureOffset(0, 7).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);

		legRight = new ModelRenderer(this);
		legRight.setRotationPoint(1.0F, 21.0F, 2.0F);
		legRight.setTextureOffset(0, 7).addBox(0.0F, 0.0F, -1.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);
	}

	@Override
	public void setRotationAngles(QuailEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){      this.head.rotateAngleX = headPitch * ((float)Math.PI / 180F);
		this.head.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
		this.body.rotateAngleX = 0;//((float)Math.PI / 2F);
		this.legRight.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		this.legLeft.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
		this.wingRight.rotateAngleZ = -ageInTicks;
		this.wingLeft.rotateAngleZ = ageInTicks;
	}

	@Override
	protected Iterable<ModelRenderer> getHeadParts() {
		return ImmutableList.of(head);
	}

	@Override
	protected Iterable<ModelRenderer> getBodyParts() {
		return ImmutableList.of(body, legLeft, legRight, wingLeft, wingRight);
	}
}