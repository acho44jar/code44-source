package com.igore.code44.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.horse.Horse;

public final class Zer000Manager {
    private static final String FAKE_ANIMAL_TAG = "code44FakeAnimal";

    private Zer000Manager() {
    }

    public static void tryMarkFakeAnimal(ServerLevel level, Animal animal) {
        if (!com.igore.code44.effect.FearManager.isZer000Unlocked(level)) {
            return;
        }

        if (!isEligibleAnimal(animal) || isFakeAnimal(animal)) {
            return;
        }

        if (!level.getEntitiesOfClass(Animal.class, animal.getBoundingBox().inflate(16.0D), Zer000Manager::isFakeAnimal).isEmpty()) {
            return;
        }

        if (level.random.nextInt(100) < 65) {
            animal.getPersistentData().putBoolean(FAKE_ANIMAL_TAG, true);
        }
    }

    public static void forceMarkFakeAnimal(Animal animal) {
        if (isEligibleAnimal(animal)) {
            animal.getPersistentData().putBoolean(FAKE_ANIMAL_TAG, true);
        }
    }

    public static boolean isFakeAnimal(Animal animal) {
        return animal.getPersistentData().getBoolean(FAKE_ANIMAL_TAG);
    }

    public static boolean isEligibleAnimalForTesting(Animal animal) {
        return isEligibleAnimal(animal);
    }

    private static boolean isEligibleAnimal(Animal animal) {
        return animal instanceof Chicken
                || animal instanceof Pig
                || animal instanceof Sheep
                || animal instanceof Cow
                || animal instanceof Horse;
    }
}
