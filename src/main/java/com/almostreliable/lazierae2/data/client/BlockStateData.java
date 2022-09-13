package com.almostreliable.lazierae2.data.client;

import com.almostreliable.lazierae2.content.GenericBlock;
import com.almostreliable.lazierae2.content.MachineBlock;
import com.almostreliable.lazierae2.content.assembler.HullBlock;
import com.almostreliable.lazierae2.content.assembler.controller.ControllerBlock;
import com.almostreliable.lazierae2.core.Setup.Blocks;
import com.almostreliable.lazierae2.core.Setup.Blocks.Assembler;
import com.almostreliable.lazierae2.util.GameUtil;
import com.almostreliable.lazierae2.util.TextUtil;
import net.minecraft.core.Direction.Axis;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.function.Function;

import static com.almostreliable.lazierae2.BuildConfig.MOD_ID;
import static com.almostreliable.lazierae2.util.TextUtil.f;

public class BlockStateData extends BlockStateProvider {

    public BlockStateData(DataGenerator gen, ExistingFileHelper fileHelper) {
        super(gen, MOD_ID, fileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        machineCustomModel(Blocks.AGGREGATOR.get());
        machineCustomModel(Blocks.ETCHER.get());
        machineCustomModel(Blocks.GRINDER.get());
        machineCustomModel(Blocks.INFUSER.get());
        machineCustomModel(Blocks.REQUESTER.get());
        registerAssembler(Assembler.CONTROLLER.get());
        registerAssembler(Assembler.ACCELERATOR.get());
        registerAssembler(Assembler.TIER_1.get());
        registerAssembler(Assembler.TIER_2.get());
        registerAssembler(Assembler.TIER_3.get());
        registerAssembler(Assembler.WALL.get());
        registerAssembler(Assembler.FRAME.get());
    }

    private void machineCustomModel(GenericBlock block) {
        var id = GameUtil.getIdFromBlock(block);
        var modelInactive = TextUtil.getRL(f("block/{}", id));
        var modelActive = TextUtil.getRL(f("block/{}", formActiveId(id)));
        orientedBlock(
            block,
            MachineBlock.FACING,
            state -> new UncheckedModelFile(
                state.getValue(GenericBlock.ACTIVE).equals(Boolean.TRUE) ? modelActive : modelInactive)
        );
    }

    private void registerAssembler(GenericBlock block) {
        var id = GameUtil.getIdFromBlock(block);
        var inactive = TextUtil.getRL(f("block/assembler/{}", id));
        var active = TextUtil.getRL(f("block/assembler/{}", formActiveId(id)));
        BlockModelBuilder modelInactive;
        BlockModelBuilder modelActive;
        if (block instanceof ControllerBlock) {
            var wall = TextUtil.getRL("block/assembler/wall");
            var wallActive = TextUtil.getRL("block/assembler/wall_active");
            modelInactive = models().orientable(id, wall, inactive, wall);
            modelActive = models().orientable(formActiveId(id), wallActive, active, wallActive);
            orientedBlock(
                block,
                ControllerBlock.FACING,
                state -> getBlockModelBuilder(modelInactive, modelActive, state)
            );
        } else {
            modelInactive = models().cubeAll(id, inactive);
            modelActive = models().cubeAll(formActiveId(id), active);
            getVariantBuilder(block).forAllStatesExcept(state -> ConfiguredModel
                .builder()
                .modelFile(getBlockModelBuilder(modelInactive, modelActive, state))
                .build(), HullBlock.HORIZONTAL, HullBlock.VERTICAL);
        }
    }

    private BlockModelBuilder getBlockModelBuilder(
        BlockModelBuilder modelInactive, BlockModelBuilder modelActive, BlockState state
    ) {
        return state.getValue(GenericBlock.ACTIVE).equals(Boolean.TRUE) ? modelActive : modelInactive;
    }

    private void orientedBlock(
        Block block, DirectionProperty facingProp, Function<? super BlockState, ? extends ModelFile> modelFunction,
        Property<?>... ignored
    ) {
        getVariantBuilder(block).forAllStatesExcept(state -> {
            var facing = state.getValue(facingProp);
            return ConfiguredModel
                .builder()
                .modelFile(modelFunction.apply(state))
                .rotationX(facing.getAxis() == Axis.Y ? facing.getAxisDirection().getStep() * -90 : 0)
                .rotationY(facing.getAxis() == Axis.Y ? 180 : ((facing.get2DDataValue() + 2) % 4) * 90)
                .build();
        }, ignored);
    }

    private String formActiveId(String id) {
        return f("{}_active", id);
    }
}
