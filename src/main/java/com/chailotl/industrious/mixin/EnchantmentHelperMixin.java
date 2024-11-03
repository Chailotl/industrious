package com.chailotl.industrious.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin
{
	@Inject(method = "modifyKnockback", at = @At("RETURN"), cancellable = true)
	private static void boxingGloveKnockback(ServerWorld world, ItemStack stack, Entity target, DamageSource damageSource, float baseKnockback, CallbackInfoReturnable<Float> cir)
	{
		/*if (stack.getItem() instanceof BoxingGloveItem)
		{
			Main.LOGGER.info("hello world");
			cir.setReturnValue(cir.getReturnValue() + 2f);
		}*/
	}
}