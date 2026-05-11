package com.igore.code44.registry;

import com.igore.code44.Code44Mod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(net.minecraft.core.registries.Registries.BLOCK, Code44Mod.MODID);

    public static final RegistryObject<Block> EFBUI_VOID_BLOCK =
            BLOCKS.register("efbui_void_block",
                    () -> new Block(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_BLACK)
                            .strength(-1.0F, 3_600_000.0F)
                            .sound(SoundType.STONE)
                            .noLootTable()
                            .requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> EFBUI_VOID_LIGHT =
            BLOCKS.register("efbui_void_light",
                    () -> new Block(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_BLACK)
                            .strength(-1.0F, 3_600_000.0F)
                            .sound(SoundType.STONE)
                            .lightLevel(state -> 15)
                            .noLootTable()
                            .requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> TUNNEL_STONE =
            BLOCKS.register("tunnel_stone",
                    () -> new Block(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.STONE)
                            .strength(-1.0F, 3_600_000.0F)
                            .sound(SoundType.STONE)
                            .noLootTable()
                            .requiresCorrectToolForDrops()));

    private ModBlocks() {
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
