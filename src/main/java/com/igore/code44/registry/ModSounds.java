package com.igore.code44.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, "code44");

    public static final RegistryObject<SoundEvent> AMBIENT_WHISPER =
            SOUND_EVENTS.register("ambient_whisper",
                    () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("code44", "ambient_whisper")));

    public static final RegistryObject<SoundEvent> LULLABY =
            SOUND_EVENTS.register("lullaby",
                    () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("code44", "lullaby")));

    public static final RegistryObject<SoundEvent> EFBUI_SPAWN =
            SOUND_EVENTS.register("efbuispawn",
                    () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("code44", "efbuispawn")));

    public static final RegistryObject<SoundEvent> EFBUI_JOIN =
            SOUND_EVENTS.register("efbuijoin",
                    () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("code44", "efbuijoin")));

    public static final RegistryObject<SoundEvent> ZER000_KILLING_YOU =
            SOUND_EVENTS.register("zer000killingyou",
                    () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("code44", "zer000killingyou")));

    public static final RegistryObject<SoundEvent> HEART =
            SOUND_EVENTS.register("heart",
                    () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("code44", "heart")));

    public static final RegistryObject<SoundEvent> EFBUI_MINESHAFT =
            SOUND_EVENTS.register("efbuimineshaft",
                    () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("code44", "efbuimineshaft")));

    public static final RegistryObject<SoundEvent> SOS =
            SOUND_EVENTS.register("sos",
                    () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("code44", "sos")));

    public static final RegistryObject<SoundEvent> EFBUI_DIMENSION =
            SOUND_EVENTS.register("efbuimir",
                    () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("code44", "efbuimir")));

    public static final RegistryObject<SoundEvent> DOOR =
            SOUND_EVENTS.register("door",
                    () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("code44", "door")));

    public static final RegistryObject<SoundEvent> FAKE =
            SOUND_EVENTS.register("fake",
                    () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("code44", "fake")));

    public static final RegistryObject<SoundEvent> DOORVOID =
            SOUND_EVENTS.register("doorvoid",
                    () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("code44", "doorvoid")));

    public static final RegistryObject<SoundEvent> DOOREFBUI =
            SOUND_EVENTS.register("doorefbui",
                    () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("code44", "doorefbui")));

    public static final RegistryObject<SoundEvent> GLICHDEADBLACK =
            SOUND_EVENTS.register("glichdeadblack",
                    () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("code44", "glichdeadblack")));

    public static final RegistryObject<SoundEvent> GODLOVEU =
            SOUND_EVENTS.register("godloveu",
                    () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("code44", "godloveu")));

    public static final RegistryObject<SoundEvent> IMWATCH =
            SOUND_EVENTS.register("imwatch",
                    () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("code44", "imwatch")));

    public static final RegistryObject<SoundEvent> SLEEPISSHORT =
            SOUND_EVENTS.register("sleepisshort",
                    () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("code44", "sleepisshort")));

    public static final RegistryObject<SoundEvent> URLIFE =
            SOUND_EVENTS.register("urlife",
                    () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("code44", "urlife")));

    public static final RegistryObject<SoundEvent> YOUREND =
            SOUND_EVENTS.register("yourend",
                    () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("code44", "yourend")));

    public static final RegistryObject<SoundEvent> CRYING =
            SOUND_EVENTS.register("crying",
                    () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("code44", "crying")));

    public static final RegistryObject<SoundEvent> BROKENGLASS =
            SOUND_EVENTS.register("brokenglass",
                    () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("code44", "brokenglass")));

    public static final RegistryObject<SoundEvent> SKINWALKER =
            SOUND_EVENTS.register("skinwalker",
                    () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("code44", "skinwalker")));

    public static final RegistryObject<SoundEvent> TUNNELOBSERVERSPAWN =
            SOUND_EVENTS.register("tunnelobserverspawn",
                    () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("code44", "tunnelobserverspawn")));

    public static final RegistryObject<SoundEvent> ERR44R =
            SOUND_EVENTS.register("err44r",
                    () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("code44", "err44r")));

    public static final RegistryObject<SoundEvent> MAZE =
            SOUND_EVENTS.register("maze",
                    () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("code44", "maze")));

    public static final RegistryObject<SoundEvent> GRETEMINOS =
            SOUND_EVENTS.register("greteminos",
                    () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("code44", "greteminos")));

    public static final RegistryObject<SoundEvent> WHITEDOOR =
            SOUND_EVENTS.register("whitedoor",
                    () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("code44", "whitedoor")));

    public static final RegistryObject<SoundEvent> MAZEGUARDIANUNSPAWNED =
            SOUND_EVENTS.register("mazeguardianunspawned",
                    () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("code44", "mazeguardianunspawned")));

    public static final RegistryObject<SoundEvent> FAKEPLAYERINCHATHELLO =
            SOUND_EVENTS.register("fakeplayerinchathello",
                    () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("code44", "fakeplayerinchathello")));

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
