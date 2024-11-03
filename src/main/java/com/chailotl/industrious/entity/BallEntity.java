package com.chailotl.industrious.entity;

import com.chailotl.industrious.Main;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class BallEntity extends Entity
{
	private static final TrackedData<ItemStack> ITEM = DataTracker.registerData(BallEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);

	public BallEntity(EntityType<BallEntity> entityType, World world)
	{
		super(entityType, world);
	}

	public BallEntity(World world, LivingEntity owner)
	{
		super(Main.BALL_ENTITY, world);
		setPosition(owner.getX(), owner.getEyeY() - 0.1F, owner.getZ());
	}

	public BallEntity(World world, double x, double y, double z)
	{
		super(Main.BALL_ENTITY, world);
		setPosition(x, y, z);
	}

	protected Item getDefaultItem()
	{
		return Main.WHITE_BALL;
	}

	public ItemStack getStack()
	{
		return getDataTracker().get(ITEM);
	}

	public void setStack(ItemStack stack)
	{
		getDataTracker().set(ITEM, stack.copyWithCount(1));
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder)
	{
		builder.add(ITEM, new ItemStack(getDefaultItem()));
	}

	public static DefaultAttributeContainer.Builder createBallAttributes()
	{
		return MobEntity.createMobAttributes()
			.add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.5);
	}

	@Override
	public boolean canHit()
	{
		return !isRemoved();
	}

	@Override
	public boolean isPushable()
	{
		return true;
	}

	@Override
	protected double getGravity()
	{
		return 0.08;
	}

	@Override
	public SoundCategory getSoundCategory()
	{
		return SoundCategory.AMBIENT;
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt)
	{
		nbt.put("Item", getStack().encode(getRegistryManager()));
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt)
	{
		if (nbt.contains("Item", NbtElement.COMPOUND_TYPE))
		{
			NbtCompound nbtCompound = nbt.getCompound("Item");
			setStack(ItemStack.fromNbt(getRegistryManager(), nbtCompound).orElse(ItemStack.EMPTY));
		}
	}

	public void setVelocity(double x, double y, double z, float power, float uncertainty)
	{
		Vec3d vec3d = calculateVelocity(x, y, z, power, uncertainty);
		setVelocity(vec3d);
		velocityDirty = true;
		double d = vec3d.horizontalLength();
		setYaw((float)(MathHelper.atan2(vec3d.x, vec3d.z) * 180.0F / (float)Math.PI));
		setPitch((float)(MathHelper.atan2(vec3d.y, d) * 180.0F / (float)Math.PI));
		prevYaw = getYaw();
		prevPitch = getPitch();
	}

	public void setVelocity(Entity shooter, float pitch, float yaw, float roll, float speed, float divergence)
	{
		float f = -MathHelper.sin(yaw * (float) (Math.PI / 180.0)) * MathHelper.cos(pitch * (float) (Math.PI / 180.0));
		float g = -MathHelper.sin((pitch + roll) * (float) (Math.PI / 180.0));
		float h = MathHelper.cos(yaw * (float) (Math.PI / 180.0)) * MathHelper.cos(pitch * (float) (Math.PI / 180.0));
		setVelocity(f, g, h, speed, divergence);
		Vec3d vec3d = shooter.getMovement();
		setVelocity(getVelocity().add(vec3d.x, shooter.isOnGround() ? 0.0 : vec3d.y, vec3d.z));
	}

	public Vec3d calculateVelocity(double x, double y, double z, float power, float uncertainty)
	{
		return new Vec3d(x, y, z)
			.normalize()
			.add(
				random.nextTriangular(0.0, 0.0172275 * (double)uncertainty),
				random.nextTriangular(0.0, 0.0172275 * (double)uncertainty),
				random.nextTriangular(0.0, 0.0172275 * (double)uncertainty)
			)
			.multiply(power);
	}

	@Override
	public void tick()
	{
		super.tick();

		prevX = getX();
		prevY = getY();
		prevZ = getZ();
		Vec3d vec3d = getVelocity();
		if (isTouchingWater() && getFluidHeight(FluidTags.WATER) > 0.1F)
		{
			applyWaterBuoyancy();
		}
		else
		{
			applyGravity();
		}

		if (getWorld().isClient)
		{
			noClip = false;
		}
		else
		{
			noClip = !getWorld().isSpaceEmpty(this, getBoundingBox().contract(1.0E-7));
			if (noClip)
			{
				pushOutOfBlocks(getX(), (getBoundingBox().minY + getBoundingBox().maxY) / 2.0, getZ());
			}
		}

		if (getVelocity().lengthSquared() > 1.0E-5F)
		{
			move(MovementType.SELF, getVelocity());
			float f = 0.98F;
			if (isOnGround())
			{
				f *= getWorld().getBlockState(getVelocityAffectingPos()).getBlock().getSlipperiness();
			}

			setVelocity(getVelocity().multiply(f, 0.98, f));
		}

		velocityDirty = velocityDirty | updateWaterState();
		if (!getWorld().isClient)
		{
			double d = getVelocity().subtract(vec3d).lengthSquared();
			if (d > 0.01)
			{
				velocityDirty = true;
			}
		}
	}

	@Override
	public void move(MovementType movementType, Vec3d movement)
	{
		if (this.noClip) {
			this.setPosition(this.getX() + movement.x, this.getY() + movement.y, this.getZ() + movement.z);
		} else {
			this.wasOnFire = this.isOnFire();
			if (movementType == MovementType.PISTON) {
				movement = this.adjustMovementForPiston(movement);
				if (movement.equals(Vec3d.ZERO)) {
					return;
				}
			}

			this.getWorld().getProfiler().push("move");
			if (this.movementMultiplier.lengthSquared() > 1.0E-7) {
				movement = movement.multiply(this.movementMultiplier);
				this.movementMultiplier = Vec3d.ZERO;
				this.setVelocity(Vec3d.ZERO);
			}

			movement = this.adjustMovementForSneaking(movement, movementType);
			Vec3d vec3d = this.adjustMovementForCollisions(movement);
			double d = vec3d.lengthSquared();
			if (d > 1.0E-7) {
				if (this.fallDistance != 0.0F && d >= 1.0) {
					BlockHitResult blockHitResult = this.getWorld()
						.raycast(
							new RaycastContext(this.getPos(), this.getPos().add(vec3d), RaycastContext.ShapeType.FALLDAMAGE_RESETTING, RaycastContext.FluidHandling.WATER, this)
						);
					if (blockHitResult.getType() != HitResult.Type.MISS) {
						this.onLanding();
					}
				}

				this.setPosition(this.getX() + vec3d.x, this.getY() + vec3d.y, this.getZ() + vec3d.z);
			}

			this.getWorld().getProfiler().pop();
			this.getWorld().getProfiler().push("rest");
			boolean bl = !MathHelper.approximatelyEquals(movement.x, vec3d.x);
			boolean bl2 = !MathHelper.approximatelyEquals(movement.z, vec3d.z);
			this.horizontalCollision = bl || bl2;
			this.verticalCollision = movement.y != vec3d.y;
			this.groundCollision = this.verticalCollision && movement.y < 0.0;
			if (this.horizontalCollision) {
				this.collidedSoftly = this.hasCollidedSoftly(vec3d);
			} else {
				this.collidedSoftly = false;
			}

			this.setOnGround(this.groundCollision, vec3d);
			BlockPos blockPos = this.getLandingPos();
			BlockState blockState = this.getWorld().getBlockState(blockPos);
			this.fall(vec3d.y, this.isOnGround(), blockState, blockPos);
			if (this.isRemoved()) {
				this.getWorld().getProfiler().pop();
			} else {
				if (this.horizontalCollision) {
					Vec3d vec3d2 = this.getVelocity();
					this.setVelocity(vec3d2.x * (bl ? -1 : 1), vec3d2.y, vec3d2.z * (bl2 ? -1 : 1));
				}

				Block block = blockState.getBlock();
				if (movement.y != vec3d.y) {
					//block.onEntityLand(this.getWorld(), this);
					Vec3d vec3d2 = this.getVelocity();
					if (vec3d2.y < 0.0) {
						setVelocity(vec3d2.x, -vec3d2.y * 0.75, vec3d2.z);
					}
				}

				if (this.isOnGround()) {
					block.onSteppedOn(this.getWorld(), blockPos, blockState, this);
				}

				Entity.MoveEffect moveEffect = this.getMoveEffect();
				if (moveEffect.hasAny() && !this.hasVehicle()) {
					double e = vec3d.x;
					double f = vec3d.y;
					double g = vec3d.z;
					this.speed = this.speed + (float)(vec3d.length() * 0.6);
					BlockPos blockPos2 = this.getSteppingPos();
					BlockState blockState2 = this.getWorld().getBlockState(blockPos2);
					boolean bl3 = this.canClimb(blockState2);
					if (!bl3) {
						f = 0.0;
					}

					this.horizontalSpeed = this.horizontalSpeed + (float)vec3d.horizontalLength() * 0.6F;
					this.distanceTraveled = this.distanceTraveled + (float)Math.sqrt(e * e + f * f + g * g) * 0.6F;
					if (this.distanceTraveled > this.nextStepSoundDistance && !blockState2.isAir()) {
						boolean bl4 = blockPos2.equals(blockPos);
						boolean bl5 = this.stepOnBlock(blockPos, blockState, moveEffect.playsSounds(), bl4, movement);
						if (!bl4) {
							bl5 |= this.stepOnBlock(blockPos2, blockState2, false, moveEffect.emitsGameEvents(), movement);
						}

						if (bl5) {
							this.nextStepSoundDistance = this.calculateNextStepSoundDistance();
						} else if (this.isTouchingWater()) {
							this.nextStepSoundDistance = this.calculateNextStepSoundDistance();
							if (moveEffect.playsSounds()) {
								this.playSwimSound();
							}

							if (moveEffect.emitsGameEvents()) {
								this.emitGameEvent(GameEvent.SWIM);
							}
						}
					} else if (blockState2.isAir()) {
						this.addAirTravelEffects();
					}
				}

				this.tryCheckBlockCollision();
				float h = this.getVelocityMultiplier();
				this.setVelocity(this.getVelocity().multiply(h, 1.0, h));
				if (this.getWorld()
					.getStatesInBoxIfLoaded(this.getBoundingBox().contract(1.0E-6))
					.noneMatch(state -> state.isIn(BlockTags.FIRE) || state.isOf(Blocks.LAVA))) {
					if (this.getFireTicks() <= 0) {
						this.setFireTicks(-this.getBurningDuration());
					}

					if (this.wasOnFire && (this.inPowderSnow || this.isWet())) {
						this.playExtinguishSound();
					}
				}

				if (this.isOnFire() && (this.inPowderSnow || this.isWet())) {
					this.setFireTicks(-this.getBurningDuration());
				}

				this.getWorld().getProfiler().pop();
			}
		}
	}

	@Override
	public BlockPos getVelocityAffectingPos()
	{
		return this.getPosWithYOffset(0.999999F);
	}

	private void applyWaterBuoyancy()
	{
		Vec3d vec3d = getVelocity();
		setVelocity(vec3d.x * 0.99F, vec3d.y + (double)(vec3d.y < 0.06F ? 5.0E-4F : 0.0F), vec3d.z * 0.99F);
	}

	@Override
	public boolean damage(DamageSource source, float amount)
	{
		if (isInvulnerableTo(source) || getWorld().isClient)
		{
			return false;
		}
		else if (source.getAttacker() instanceof PlayerEntity player && player.isSneaking())
		{
			kill();
			if (getWorld().getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS))
			{
				ItemStack itemStack = getStack().copy();
				itemStack.set(DataComponentTypes.CUSTOM_NAME, getCustomName());
				dropStack(itemStack);
			}

			return true;
		}
		else
		{
			if (!source.isIn((DamageTypeTags.NO_KNOCKBACK)))
			{
				double strength = 0.4;
				double vertical = 0.4;
				double x = 0.0;
				double z = 0.0;
				if (source.getPosition() != null)
				{
					x = source.getPosition().getX() - getX();
					z = source.getPosition().getZ() - getZ();
				}

				if (source.getAttacker() instanceof PlayerEntity player && player.isSprinting())
				{
					strength = 0.6;
					vertical = 0.6;
				}

				takeKnockback(strength, vertical, x, z);
			}

			return false;
		}
	}

	public void takeKnockback(double strength, double vertical, double x, double z)
	{
		if (strength > 0)
		{
			velocityDirty = true;
			Vec3d vec3d = getVelocity();

			while (x * x + z * z < 1.0E-5F)
			{
				x = (Math.random() - Math.random()) * 0.01;
				z = (Math.random() - Math.random()) * 0.01;
			}

			Vec3d vec3d2 = new Vec3d(x, 0.0, z).normalize().multiply(strength);
			setVelocity(
				vec3d.x / 2.0 - vec3d2.x,
				isOnGround() ? Math.min(vertical, vec3d.y / 2.0 + vertical) : vec3d.y,
				vec3d.z / 2.0 - vec3d2.z
			);
		}
	}
}