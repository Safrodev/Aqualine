package safro.aqualine.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import safro.aqualine.Aqualine;
import safro.aqualine.AqualineClient;
import safro.aqualine.client.model.AnchorModel;
import safro.aqualine.entity.projectile.AnchorEntity;

public class AnchorRenderer extends EntityRenderer<AnchorEntity> {
    public static final ResourceLocation TEXTURE = Aqualine.id("textures/entity/anchor.png");
    private final AnchorModel model;

    public AnchorRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new AnchorModel(context.bakeLayer(AqualineClient.ANCHOR_LAYER));
    }

    @Override
    public void render(AnchorEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot()) + 90.0F));
        VertexConsumer vertexconsumer = buffer.getBuffer(this.model.renderType(this.getTextureLocation(entity)));
        this.model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(AnchorEntity entity) {
        return TEXTURE;
    }
}
