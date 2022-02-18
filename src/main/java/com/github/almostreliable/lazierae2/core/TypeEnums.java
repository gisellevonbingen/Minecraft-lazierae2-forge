package com.github.almostreliable.lazierae2.core;

import com.github.almostreliable.lazierae2.core.Config.MachineConfig;
import com.github.almostreliable.lazierae2.core.Setup.Recipes.Serializers;
import com.github.almostreliable.lazierae2.recipe.type.MachineRecipe;
import com.github.almostreliable.lazierae2.recipe.type.SingleInputRecipe;
import com.github.almostreliable.lazierae2.recipe.type.TripleInputRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Lazy;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import static com.github.almostreliable.lazierae2.core.Constants.*;

public final class TypeEnums {

    private TypeEnums() {}

    /**
     * Defines the type of the translation to
     * identify its key inside the lang file.
     */
    public enum TRANSLATE_TYPE {
        BLOCK, LABEL, TOOLTIP, BLOCK_SIDE, IO_SETTING, CONFIG
    }

    /**
     * Defines the possible IO sides of a block.
     */
    public enum BLOCK_SIDE {
        BOTTOM, TOP, FRONT, BACK, LEFT, RIGHT
    }

    /**
     * Enum to represent the different IO settings for the side configuration.
     */
    public enum IO_SETTING {
        OFF, INPUT, OUTPUT, IO
    }

    public enum MachineType implements IRecipeType<MachineRecipe> {

        AGGREGATOR(AGGREGATOR_ID,
            3,
            () -> Config.COMMON.aggregator,
            TripleInputRecipe::new,
            () -> Serializers.AGGREGATOR
        ),
        CENTRIFUGE(CENTRIFUGE_ID,
            1,
            () -> Config.COMMON.centrifuge,
            SingleInputRecipe::new,
            () -> Serializers.CENTRIFUGE
        ),
        ENERGIZER(ENERGIZER_ID, 1, () -> Config.COMMON.energizer, SingleInputRecipe::new, () -> Serializers.ENERGIZER),
        ETCHER(ETCHER_ID, 3, () -> Config.COMMON.etcher, TripleInputRecipe::new, () -> Serializers.ETCHER);

        private final String id;
        private final int inputSlots;
        private final Lazy<MachineConfig> machineConfig;
        private final BiFunction<ResourceLocation, MachineType, MachineRecipe> recipeFactory;
        private final Supplier<? extends Supplier<IRecipeSerializer<MachineRecipe>>> recipeSerializer;

        MachineType(
            String id, int inputSlots, Supplier<MachineConfig> machineConfig,
            BiFunction<ResourceLocation, MachineType, MachineRecipe> recipeFactory,
            Supplier<? extends Supplier<IRecipeSerializer<MachineRecipe>>> recipeSerializer
        ) {
            this.id = id;
            this.inputSlots = inputSlots;
            this.machineConfig = Lazy.of(machineConfig);
            this.recipeFactory = recipeFactory;
            this.recipeSerializer = recipeSerializer;
        }

        @Override
        public String toString() {
            return id;
        }

        public String getId() {
            return id;
        }

        public int getBaseProcessTime() {
            return machineConfig.get().baseProcessTime.get();
        }

        public int getBaseEnergyCost() {
            return machineConfig.get().baseEnergyUsage.get();
        }

        public int getUpgradeSlots() {
            return machineConfig.get().upgradeSlots.get();
        }

        public int getBaseEnergyBuffer() {
            return machineConfig.get().baseEnergyBuffer.get();
        }

        public int getEnergyBufferAdd() {
            return machineConfig.get().energyBufferAdd.get();
        }

        public BiFunction<ResourceLocation, MachineType, MachineRecipe> getRecipeFactory() {
            return recipeFactory;
        }

        public Supplier<IRecipeSerializer<MachineRecipe>> getRecipeSerializer() {
            return recipeSerializer.get();
        }

        public int getInputSlots() {
            return inputSlots;
        }

        public double getProcessTimeMultiplier() {
            return machineConfig.get().processTimeMulti.get();
        }

        public double getEnergyCostMultiplier() {
            return machineConfig.get().energyUsageMulti.get();
        }
    }
}
