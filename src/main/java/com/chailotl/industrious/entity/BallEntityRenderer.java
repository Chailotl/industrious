package com.chailotl.industrious.entity;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;

public class BallEntityRenderer extends EntityRenderer<BallEntity>
{;
	private final ItemRenderer itemRenderer;

	public BallEntityRenderer(EntityRendererFactory.Context ctx)
	{
		super(ctx);
		this.itemRenderer = ctx.getItemRenderer();
	}

	@Override
	public Identifier getTexture(BallEntity entity)
	{
		return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
	}

	@Override
	public void render(BallEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light)
	{
		if (entity.age < 2 && dispatcher.camera.getFocusedEntity().squaredDistanceTo(entity) < 12.25) { return; }

		matrices.push();
		//matrices.multiply(new Quaternionf().rotateZ((float) (Math.PI / 2f)));
		matrices.translate(0, -0.25F, 0);
		matrices.scale(4, 4, 4);
		//matrices.multiply(dispatcher.getRotation());
		itemRenderer.renderItem(
			entity.getStack(),
			ModelTransformationMode.GROUND,
			light,
			OverlayTexture.DEFAULT_UV,
			matrices,
			vertexConsumers,
			entity.getWorld(),
			entity.getId()
		);
		matrices.pop();
		super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
	}
}