package com.chailotl.industrious;

import com.chailotl.industrious.entity.BallEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;

public class ClientMain implements ClientModInitializer
{
	@Override
	public void onInitializeClient()
	{
		BlockRenderLayerMap.INSTANCE.putBlock(Main.COTTON_PLANT, RenderLayer.getCutout());
		EntityRendererRegistry.register(Main.BALL_ENTITY, BallEntityRenderer::new);
	}
}