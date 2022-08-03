package heckerpowered.surrender.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import heckerpowered.surrender.common.content.block.SurrenderBlocks;
import heckerpowered.surrender.common.content.entity.NuclearTnt;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.TntMinecartRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;

public final class NuclearTntRenderer extends EntityRenderer<NuclearTnt> {
    private final BlockRenderDispatcher dispatcher;

    public NuclearTntRenderer(Context context) {
        super(context);
        dispatcher = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(NuclearTnt entity, float entityYaw, float partialTick, PoseStack poseStack,
            MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.5D, 0.0D);

        final var fuse = entity.getFuse();
        final var partialScale = fuse - partialTick + 1.0F;
        if (partialScale < 10.0F) {
            final var scale = 1.0F + (float) Math.pow(Mth.clamp(1.0F - partialScale / 10F, 0.0F, 1.0F), 3.0F) * 0.3F;
            poseStack.scale(scale, scale, scale);
        }

        poseStack.mulPose(Vector3f.YP.rotationDegrees(-90.0F));
        poseStack.translate(-0.5D, -0.5D, 0.5D);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(90.0F));

        TntMinecartRenderer.renderWhiteSolidBlock(dispatcher, SurrenderBlocks.NECULEAR_TNT.get().defaultBlockState(),
                poseStack, buffer, packedLight, fuse / 5 % 2 == 0);

        poseStack.popPose();

        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(NuclearTnt entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }

}
