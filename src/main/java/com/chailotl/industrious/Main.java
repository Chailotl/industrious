package com.chailotl.industrious;

import com.chailotl.industrious.block.*;
import com.chailotl.industrious.entity.BallEntity;
import com.chailotl.industrious.item.BallItem;
import com.chailotl.industrious.item.BoxingGloveItem;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements ModInitializer
{
	public static final String MOD_ID = "industrious";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Block COTTON_PLANT = register("cotton_plant", new CottonPlantBlock(
		AbstractBlock.Settings.create()
			.mapColor(MapColor.DARK_GREEN)
			.noCollision()
			.ticksRandomly()
			.breakInstantly()
			.sounds(BlockSoundGroup.CROP)
			.pistonBehavior(PistonBehavior.DESTROY)
	), false);

	public static final Item RAW_RUBBER = register("raw_rubber", new Item(new Item.Settings()));
	public static final Item VULCANIZED_RUBBER = register("vulcanized_rubber", new Item(new Item.Settings()));
	public static final Item COTTON_BOLL = register("cotton_boll", new Item(new Item.Settings()));
	public static final Item COTTON_SEEDS = register("cotton_seeds", new AliasedBlockItem(COTTON_PLANT, new Item.Settings()));
	public static final Item SULFUR = register("sulfur", new Item(new Item.Settings()));
	public static final Item WHITE_BOXING_GLOVE = registerBoxingGlove("white_boxing_glove");
	public static final Item LIGHT_GRAY_BOXING_GLOVE = registerBoxingGlove("light_gray_boxing_glove");
	public static final Item GRAY_BOXING_GLOVE = registerBoxingGlove("gray_boxing_glove");
	public static final Item BLACK_BOXING_GLOVE = registerBoxingGlove("black_boxing_glove");
	public static final Item BROWN_BOXING_GLOVE = registerBoxingGlove("brown_boxing_glove");
	public static final Item RED_BOXING_GLOVE = registerBoxingGlove("red_boxing_glove");
	public static final Item ORANGE_BOXING_GLOVE = registerBoxingGlove("orange_boxing_glove");
	public static final Item YELLOW_BOXING_GLOVE = registerBoxingGlove("yellow_boxing_glove");
	public static final Item LIME_BOXING_GLOVE = registerBoxingGlove("lime_boxing_glove");
	public static final Item GREEN_BOXING_GLOVE = registerBoxingGlove("green_boxing_glove");
	public static final Item CYAN_BOXING_GLOVE = registerBoxingGlove("cyan_boxing_glove");
	public static final Item LIGHT_BLUE_BOXING_GLOVE = registerBoxingGlove("light_blue_boxing_glove");
	public static final Item BLUE_BOXING_GLOVE = registerBoxingGlove("blue_boxing_glove");
	public static final Item PURPLE_BOXING_GLOVE = registerBoxingGlove("purple_boxing_glove");
	public static final Item MAGENTA_BOXING_GLOVE = registerBoxingGlove("magenta_boxing_glove");
	public static final Item PINK_BOXING_GLOVE = registerBoxingGlove("pink_boxing_glove");
	public static final Item WHITE_BALL = register("white_ball", new BallItem(new Item.Settings()));
	public static final Item RED_BALL = register("red_ball", new BallItem(new Item.Settings()));
	public static final Item BLUE_BALL = register("blue_ball", new BallItem(new Item.Settings()));
	public static final Item BEACH_BALL = register("beach_ball", new BallItem(new Item.Settings()));
	public static final Item FOOT_BALL = register("foot_ball", new BallItem(new Item.Settings()));
	public static final Item SUMMER_BALL = register("summer_ball", new BallItem(new Item.Settings()));
	public static final Item SPRING_BALL = register("spring_ball", new BallItem(new Item.Settings()));
	public static final Item RAINBOW_BALL = register("rainbow_ball", new BallItem(new Item.Settings()));
	public static final Item BI_BALL = register("bi_ball", new BallItem(new Item.Settings()));
	public static final Item ACE_BALL = register("ace_ball", new BallItem(new Item.Settings()));

	public static final RubberTapBlock RUBBER_TAP = register("rubber_tap", new RubberTapBlock(AbstractBlock.Settings.create()
		.noCollision()
		.ticksRandomly()
		.sounds(BlockSoundGroup.METAL)
		.pistonBehavior(PistonBehavior.DESTROY)
	), true);
	public static final WoodenBasinBlock WOODEN_BASIN = register("wooden_basin", new WoodenBasinBlock(AbstractBlock.Settings.create()
		.mapColor(MapColor.OAK_TAN)
		.strength(0.6F)
		.sounds(BlockSoundGroup.WOOD)
		.burnable()
	), true);
	public static final Block RAW_RUBBER_BLOCK = register("raw_rubber_block", new Block(AbstractBlock.Settings.create()
		.mapColor(MapColor.LIGHT_GRAY)
		.strength(0.8F)
		.velocityMultiplier(0.7F)
		.jumpVelocityMultiplier(0.75F)
		.sounds(BlockSoundGroup.HONEY)
	), true);
	public static final Block TREATED_RUBBER_BLOCK = register("treated_rubber_block", new Block(AbstractBlock.Settings.create()
		.mapColor(MapColor.LIGHT_GRAY)
		.strength(0.8F)
		.velocityMultiplier(0.7F)
		.jumpVelocityMultiplier(0.75F)
		.sounds(BlockSoundGroup.HONEY)
	), true);
	public static final Block VULCANIZED_RUBBER_BLOCK = register("vulcanized_rubber_block", new RubberBlock(AbstractBlock.Settings.create()
		.mapColor(MapColor.LIGHT_GRAY)
		.strength(0.8F)
		.sounds(BlockSoundGroup.CORAL)
	), true);
	public static final Block COTTON = register("cotton", new Block(AbstractBlock.Settings.create()
		.mapColor(MapColor.WHITE)
		.instrument(NoteBlockInstrument.GUITAR)
		.strength(0.8F)
		.sounds(BlockSoundGroup.WOOL)
		.burnable()
	), true);
	public static final Block LEATHER_ROLL = register("leather_roll", new OrientableBlock(AbstractBlock.Settings.create()
		.mapColor(MapColor.TERRACOTTA_BROWN)
		.strength(0.8F)
		.sounds(BlockSoundGroup.WOOL)
	), true);
	public static final Block SULFUR_BLOCK = register("sulfur_block", new Block(AbstractBlock.Settings.create()
		.mapColor(MapColor.YELLOW)
		.instrument(NoteBlockInstrument.BASEDRUM)
		.strength(0.8F)
		.requiresTool()
	), true);
	public static final Block SULFUR_ORE = register("sulfur_ore", new ExperienceDroppingBlock(
		UniformIntProvider.create(0, 2),
		AbstractBlock.Settings.create()
			.mapColor(MapColor.STONE_GRAY)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.requiresTool()
			.strength(3.0F, 3.0F)
	), true);
	public static final Block DEEPSLATE_SULFUR_ORE = register("deepslate_sulfur_ore", new ExperienceDroppingBlock(
		UniformIntProvider.create(0, 2),
		AbstractBlock.Settings.copyShallow(SULFUR_ORE)
			.mapColor(MapColor.DEEPSLATE_GRAY)
			.strength(4.5F, 3.0F)
			.sounds(BlockSoundGroup.DEEPSLATE)
	), true);
	public static final Block NETHER_SULFUR_ORE = register("nether_sulfur_ore", new ExperienceDroppingBlock(
		UniformIntProvider.create(0, 2),
		AbstractBlock.Settings.copyShallow(SULFUR_ORE)
			.mapColor(MapColor.DARK_RED)
			.sounds(BlockSoundGroup.NETHER_ORE)
	), true);
	public static final Block WHITE_TARP = registerTarp("white_tarp");
	public static final Block LIGHT_GRAY_TARP = registerTarp("light_gray_tarp");
	public static final Block GRAY_TARP = registerTarp("gray_tarp");
	public static final Block BLACK_TARP = registerTarp("black_tarp");
	public static final Block BROWN_TARP = registerTarp("brown_tarp");
	public static final Block RED_TARP = registerTarp("red_tarp");
	public static final Block ORANGE_TARP = registerTarp("orange_tarp");
	public static final Block YELLOW_TARP = registerTarp("yellow_tarp");
	public static final Block LIME_TARP = registerTarp("lime_tarp");
	public static final Block GREEN_TARP = registerTarp("green_tarp");
	public static final Block CYAN_TARP = registerTarp("cyan_tarp");
	public static final Block LIGHT_BLUE_TARP = registerTarp("light_blue_tarp");
	public static final Block BLUE_TARP = registerTarp("blue_tarp");
	public static final Block PURPLE_TARP = registerTarp("purple_tarp");
	public static final Block MAGENTA_TARP = registerTarp("magenta_tarp");
	public static final Block PINK_TARP = registerTarp("pink_tarp");

	public static final EntityType<BallEntity> BALL_ENTITY = Registry.register(Registries.ENTITY_TYPE, id("ball"),
		EntityType.Builder.<BallEntity>create(BallEntity::new, SpawnGroup.MISC)
			.dimensions(12/16f, 12/16f)
			.maxTrackingRange(4)
			.trackingTickInterval(10)
			.build()
	);

	public static final ItemGroup INDUSTRIOUS_GROUP = FabricItemGroup.builder()
		.icon(() -> new ItemStack(RAW_RUBBER))
		.displayName(Text.translatable("itemGroup.industrious"))
		.entries((context, entries) -> Registries.ITEM.stream()
			.filter(item -> Registries.ITEM.getId(item).getNamespace().equals(MOD_ID))
			.forEach(entries::add))
		.build();

	public static final TagKey<Block> STRIPPED_JUNGLE_LOGS = TagKey.of(RegistryKeys.BLOCK, id("stripped_jungle_logs"));

	@Override
	public void onInitialize()
	{
		//LOGGER.info("Hello Fabric world!");
		Registry.register(Registries.ITEM_GROUP, id("industrious"), INDUSTRIOUS_GROUP);
	}

	public static Identifier id(String path)
	{
		return Identifier.of(MOD_ID, path);
	}

	public static <T extends Item> T register(String name, T item)
	{
		return Registry.register(Registries.ITEM, id(name), item);
	}

	public static <T extends Block> T register(String name, T block, boolean hasDefaultItem) {
		Registry.register(Registries.BLOCK, id(name), block);
		if (hasDefaultItem) {
			register(name, new BlockItem(block, new Item.Settings()));
		}
		return block;
	}

	public static BoxingGloveItem registerBoxingGlove(String name)
	{
		return register(name, new BoxingGloveItem(new Item.Settings().attributeModifiers(BoxingGloveItem.createAttributeModifiers()).maxCount(1)));
	}

	public static Block registerTarp(String name)
	{
		return register(name, new Block(AbstractBlock.Settings.create()
			.mapColor(MapColor.WHITE)
			.instrument(NoteBlockInstrument.GUITAR)
			.strength(0.8F)
			.sounds(BlockSoundGroup.WOOL)
			.burnable()
		), true);
	}
}