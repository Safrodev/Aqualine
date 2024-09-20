package safro.aqualine.client.render;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import safro.aqualine.Aqualine;
import safro.aqualine.client.model.GhostCaptainModel;
import safro.aqualine.entity.GhostCaptainEntity;

public class GhostCaptainRenderer extends HumanoidMobRenderer<GhostCaptainEntity, PlayerModel<GhostCaptainEntity>> {

    public GhostCaptainRenderer(EntityRendererProvider.Context context) {
        super(context, new GhostCaptainModel(context.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
        this.addLayer(new HumanoidArmorLayer<>(this, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), context.getModelManager()));
    }

    @Override
    public ResourceLocation getTextureLocation(GhostCaptainEntity t) {
        return Aqualine.id("textures/entity/ghost_captain.png");
    }
}
