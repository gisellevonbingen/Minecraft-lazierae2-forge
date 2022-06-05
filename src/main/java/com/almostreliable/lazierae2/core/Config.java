package com.almostreliable.lazierae2.core;

import com.almostreliable.lazierae2.core.TypeEnums.TRANSLATE_TYPE;
import com.almostreliable.lazierae2.util.TextUtil;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

import static com.almostreliable.lazierae2.core.Constants.Blocks.*;
import static com.almostreliable.lazierae2.core.Constants.Config.*;
import static com.almostreliable.lazierae2.util.TextUtil.f;

public final class Config {

    public static final ForgeConfigSpec COMMON_SPEC;
    public static final CommonConfig COMMON;

    static {
        var commonPair = new Builder().configure(CommonConfig::new);
        COMMON_SPEC = commonPair.getRight();
        COMMON = commonPair.getLeft();
    }

    private Config() {}

    public static final class ProcessorConfig {

        public final IntValue upgradeSlots;
        public final IntValue baseEnergyBuffer;
        public final IntValue energyBufferAdd;
        public final IntValue baseEnergyUsage;
        public final DoubleValue energyUsageMulti;
        public final IntValue baseProcessTime;
        public final DoubleValue processTimeMulti;
        private final String id;

        private ProcessorConfig(
            Builder builder, String id
        ) {
            this.id = id;

            builder.push(id);
            upgradeSlots = builder.comment(
                f(" The number of upgrades the {} can hold.", id),
                " By default, upgrades will speed up the processor and increase the energy buffer at the cost of a higher energy consumption."
            ).translation(processorTranslation(UPGRADE_SLOTS)).defineInRange(UPGRADE_SLOTS, 8, 0, 64);
            baseEnergyBuffer = builder.comment(
                    f(" The amount of energy the {} can hold.", id),
                    " The energy buffer is used to store energy before it is used."
                )
                .translation(processorTranslation(ENERGY_BUFFER))
                .defineInRange(ENERGY_BUFFER, 100_000, 0, Integer.MAX_VALUE);
            energyBufferAdd = builder.comment(
                    f(" The additional amount of energy the {} can hold per upgrade.", id),
                    " Each upgrade increases the energy buffer by this amount.",
                    " Setting this to 0 will disable the energy buffer upgrade.",
                    " Calculation: energyBuffer = baseEnergyBuffer + (installedUpgrades * energyBufferAdd)"
                )
                .translation(processorTranslation(ENERGY_BUFFER_UPGRADE))
                .defineInRange(ENERGY_BUFFER_UPGRADE, 50_000, 0, Integer.MAX_VALUE);
            baseEnergyUsage = builder.comment(
                f(" The base amount of energy the {} uses per tick.", id),
                " Each recipe can overwrite this value. This is just a fallback value if a recipe does not specify an energy usage.",
                " All recipes from the mod itself have an own energy usage value and this setting won't have any effect on them.",
                " This is only useful for custom recipes e.g. for modpack makers."
            ).translation(processorTranslation(ENERGY_USAGE)).defineInRange(ENERGY_USAGE, 300, 0, Integer.MAX_VALUE);
            energyUsageMulti = builder.comment(
                    f(" The energy multiplier the {} uses per tick when upgraded.", id),
                    " Each upgrade multiplies the energy usage by this value.",
                    " Setting this to 1 will disable the energy usage multiplier.",
                    " Lower values than 1 will decrease the energy usage, higher values will increase it.",
                    " Calculation: energyUsage = baseEnergyUsage * energyUsageMulti ^ installedUpgrades"
                )
                .translation(processorTranslation(ENERGY_USAGE_UPGRADE))
                .defineInRange(ENERGY_USAGE_UPGRADE, 1.3, 0, Double.MAX_VALUE);
            baseProcessTime = builder.comment(
                f(" The base processing time the {} needs for a recipe.", id),
                " Each recipe can overwrite this value. This is just a fallback value if a recipe does not specify a processing time.",
                " All recipes from the mod itself have an own processing time value and this setting won't have any effect on them.",
                " This is only useful for custom recipes e.g. for modpack makers."
            ).translation(processorTranslation(PROCESS_TIME)).defineInRange(PROCESS_TIME, 200, 0, Integer.MAX_VALUE);
            processTimeMulti = builder.comment(
                    f(" The processing time multiplier the {} uses when upgraded.", id),
                    " Each upgrade multiplies the processing time for a recipe by this value.",
                    " Setting this to 1 will disable the processing time multiplier.",
                    " Lower values than 1 will decrease the processing time, higher values will increase it.",
                    " Calculation: processTime = baseProcessTime * processTimeMulti ^ installedUpgrades"
                )
                .translation(processorTranslation(PROCESS_TIME_UPGRADE))
                .defineInRange(PROCESS_TIME_UPGRADE, 0.8, 0, Double.MAX_VALUE);
            builder.pop();
        }

        private String processorTranslation(String translationKey) {
            return TextUtil.translateAsString(TRANSLATE_TYPE.CONFIG, id + "." + translationKey);
        }
    }

    public static final class CommonConfig {

        public final ProcessorConfig aggregator;
        public final ProcessorConfig etcher;
        public final ProcessorConfig grinder;
        public final ProcessorConfig infuser;
        public final DoubleValue requesterIdleEnergy;
        public final BooleanValue inWorldResonating;

        private CommonConfig(Builder builder) {
            builder.push(PROCESSOR_ID);
            builder.comment(
                "##################################################################################",
                " This section lets you configure the various values of the mod processors.       #",
                " If there is an option mentioning 'upgrades', AE2 Acceleration Cards are meant.  #",
                "##################################################################################"
            );
            aggregator = new ProcessorConfig(builder, AGGREGATOR_ID);
            grinder = new ProcessorConfig(builder, GRINDER_ID);
            infuser = new ProcessorConfig(builder, INFUSER_ID);
            etcher = new ProcessorConfig(builder, ETCHER_ID);
            builder.pop();

            builder.push(REQUESTER_ID);
            requesterIdleEnergy = builder.comment(f(
                    " The energy the {} drains from the ME network when idle.",
                    REQUESTER_ID
                ))
                .translation(TextUtil.translateAsString(TRANSLATE_TYPE.CONFIG, f("{}.{}", REQUESTER_ID, IDLE_ENERGY)))
                .defineInRange("idleEnergy", 5.0, 0.0, Double.MAX_VALUE);
            builder.pop();

            builder.push("misc");
            inWorldResonating = builder.comment(" Whether the Resonating Dust should be craft-able in the world.")
                .translation(TextUtil.translateAsString(TRANSLATE_TYPE.CONFIG, "misc.inWorldResonating"))
                .define("inWorldResonating", true);
            builder.pop();
        }
    }
}
