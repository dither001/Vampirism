package de.teamlapen.vampirism.client.model;


import com.mojang.blaze3d.platform.GlStateManager;
import de.teamlapen.vampirism.entity.vampire.VampireBaronEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;

/**
 * Attire designed for the male vampire baron - RebelT
 * Created using Tabula 7.1.0
 */
public class BaronAttireModel extends EntityModel<VampireBaronEntity> {
    public RendererModel hood;
    public RendererModel cloak;

    public BaronAttireModel() {
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.hood = new RendererModel(this, 44, 0);
        this.hood.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.hood.addBox(-4.5F, -8.5F, -4.0F, 9, 9, 9, 0.0F);
        this.cloak = new RendererModel(this, 0, 0);
        this.cloak.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.cloak.addBox(-8.5F, -0.5F, -2.5F, 17, 22, 5, 0.0F);
    }

    @Override
    public void render(VampireBaronEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        GlStateManager.pushMatrix();
        float enraged = entityIn.getEnragedProgress();
        this.hood.render(scale);
        GlStateManager.scalef(1 - 0.4f * enraged, 1 - 0.7f * enraged, 1 - 0.4f * enraged);
        this.cloak.render(scale);
        GlStateManager.popMatrix();
    }


    @Override
    public void setRotationAngles(VampireBaronEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
        super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
        float bodyRotateAngleY = 0;
        if (this.swingProgress > 0.0F) {
            HandSide handside = this.getSwingingSide(entityIn);
            float f1 = this.swingProgress;
            bodyRotateAngleY = MathHelper.sin(MathHelper.sqrt(f1) * ((float) Math.PI * 2F)) * 0.2F;
            if (handside == HandSide.LEFT) {
                bodyRotateAngleY *= -1.0F;
            }
        }
        this.hood.rotateAngleY = bodyRotateAngleY;
        this.cloak.rotateAngleY = bodyRotateAngleY;
    }

    protected HandSide getSwingingSide(VampireBaronEntity entity) {
        HandSide handside = entity.getPrimaryHand();
        return entity.swingingHand == Hand.MAIN_HAND ? handside : handside.opposite();
    }
}
