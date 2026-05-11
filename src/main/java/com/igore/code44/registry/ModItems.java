package com.igore.code44.registry;

import com.igore.code44.Code44Mod;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Code44Mod.MODID);

    public static final RegistryObject<Item> EFBUI_VOID_BLOCK =
            ITEMS.register("efbui_void_block",
                    () -> new BlockItem(ModBlocks.EFBUI_VOID_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> EFBUI_VOID_LIGHT =
            ITEMS.register("efbui_void_light",
                    () -> new BlockItem(ModBlocks.EFBUI_VOID_LIGHT.get(), new Item.Properties()));

    public static final RegistryObject<Item> TUNNEL_STONE =
            ITEMS.register("tunnel_stone",
                    () -> new BlockItem(ModBlocks.TUNNEL_STONE.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLACK_ICON =
            ITEMS.register("black_icon", () -> new Item(new Item.Properties()));

    private ModItems() {
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
