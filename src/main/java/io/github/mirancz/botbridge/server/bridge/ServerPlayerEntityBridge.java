package io.github.mirancz.botbridge.server.bridge;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public interface ServerPlayerEntityBridge {

    void botBridge$parentFall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition);

}
