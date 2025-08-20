package io.github.mirancz.botbridge.api;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;

/**
 * This is here just to prevent instanceof checks and casts for now
 */
public abstract class AbstractWorld {
    
    public @Nullable Chunk getChunk(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create) {
        return getMcWorld().getChunk(chunkX, chunkZ, leastStatus, create);
    }

    
    public boolean isChunkLoaded(int chunkX, int chunkZ) {
        return getMcWorld().isChunkLoaded(chunkX, chunkZ);
    }

    
    public int getTopY(Heightmap.Type heightmap, int x, int z) {
        return getMcWorld().getTopY(heightmap, x, z);
    }

    
    public int getAmbientDarkness() {
        return getMcWorld().getAmbientDarkness();
    }

    
    public BiomeAccess getBiomeAccess() {
        return getMcWorld().getBiomeAccess();
    }

    
    public RegistryEntry<Biome> getGeneratorStoredBiome(int biomeX, int biomeY, int biomeZ) {
        return getMcWorld().getGeneratorStoredBiome(biomeX, biomeY, biomeZ);
    }

    
    public int getSeaLevel() {
        return getMcWorld().getSeaLevel();
    }

    
    public DimensionType getDimension() {
        return getMcWorld().getDimension();
    }



    
    public float getBrightness(Direction direction, boolean shaded) {
        return getMcWorld().getBrightness(direction, shaded);
    }

    
    public WorldBorder getWorldBorder() {
        return getMcWorld().getWorldBorder();
    }


    
    public @Nullable BlockEntity getBlockEntity(BlockPos pos) {
        return getMcWorld().getBlockEntity(pos);
    }

    
    public BlockState getBlockState(BlockPos pos) {
        return getMcWorld().getBlockState(pos);
    }

    
    public FluidState getFluidState(BlockPos pos) {
        return getMcWorld().getFluidState(pos);
    }


    protected abstract World getMcWorld();

}
