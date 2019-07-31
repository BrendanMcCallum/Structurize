package com.ldtteam.structurize.structure.providers;

import java.nio.file.Path;
import java.util.List;
import com.ldtteam.structurize.structure.blueprint.Blueprint;
import com.ldtteam.structurize.structure.blueprint.BlueprintUtils;
import com.ldtteam.structurize.block.IAnchorBlock;
import com.ldtteam.structurize.pipeline.PlaceEventInfoHolder;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;

/**
 * Blueprint structure wrapper for {@link PlaceEventInfoHolder}
 */
public class BlueprintStructureProvider implements IStructureDataProvider
{
    private Blueprint blueprint;
    private Path blueprintPath;
    private PlaceEventInfoHolder<BlueprintStructureProvider> event;
    private Rotation rotation = Rotation.NONE;
    private boolean mirror = false;
    private BlockPos mirrorRotationAnchor = null;

    private BlueprintStructureProvider()
    {
    }

    /**
     * Creates new structure provider.
     *
     * @return new instance
     */
    public static BlueprintStructureProvider create()
    {
        return new BlueprintStructureProvider();
    }

    /**
     * Sets event reference.
     *
     * @param eventIn actual event
     */
    public void setEvent(final PlaceEventInfoHolder<BlueprintStructureProvider> eventIn)
    {
        event = eventIn;
    }

    /**
     * Sets blueprint file system path.
     *
     * @param path valid path for blueprint
     */
    public void setStructurePath(final Path path)
    {
        blueprint = BlueprintUtils.readFromStream(path);
        blueprintPath = path;
        mirrorRotationAnchor = null;
        event.getPosition().resize(getXsize(), getYsize(), getZsize());
    }

    /**
     * Getter for blueprint file system path.
     *
     * @return path of current blueprint
     */
    public Path getStructurePath()
    {
        return blueprintPath;
    }

    @Override
    public BlockPos getZeroBasedMirrorRotationAnchor()
    {
        if (mirrorRotationAnchor == null)
        {
            short index = 0;
            for (final BlockState bs : getBlockPalette())
            {
                if (bs.getBlock() instanceof IAnchorBlock)
                {
                    break;
                }
                index++;
            }
            for (final BlockPos pos : event.getPosition().getZeroBasedPosIterator())
            {
                if (getBlocks()[pos.getY()][pos.getZ()][pos.getX()] == index)
                {
                    mirrorRotationAnchor = pos;
                    break;
                }
            }
            if (mirrorRotationAnchor == null)
            {
                mirrorRotationAnchor = new BlockPos(getXsize() / 2, getYsize() / 2, getZsize() / 2);
            }
        }
        return mirrorRotationAnchor;
    }

    @Override
    public void rotateClockwise()
    {
        rotation = rotation.add(Rotation.CLOCKWISE_90);
        event.getPosition().rotateCW(getZeroBasedMirrorRotationAnchor().add(event.getPosition().getAnchor()));
    }

    @Override
    public void rotateCounterClockwise()
    {
        rotation = rotation.add(Rotation.COUNTERCLOCKWISE_90);
        event.getPosition().rotateCCW(getZeroBasedMirrorRotationAnchor().add(event.getPosition().getAnchor()));
    }

    @Override
    public Rotation getRotation()
    {
        return rotation;
    }

    @Override
    public void mirror()
    {
        mirror = !mirror;
        event.getPosition().mirrorX(getZeroBasedMirrorRotationAnchor().add(event.getPosition().getAnchor()));
    }

    @Override
    public boolean isMirrored()
    {
        return mirror;
    }

    @Override
    public void applyMirrorRotationOnStructure()
    {
        blueprint.rotateWithMirror(rotation, mirror ? Mirror.FRONT_BACK : Mirror.NONE, event.getWorld());
        rotation = Rotation.NONE;
        mirror = false;
    }

    @Override
    public int getXsize()
    {
        return blueprint.getSizeX();
    }

    @Override
    public int getYsize()
    {
        return blueprint.getSizeY();
    }

    @Override
    public int getZsize()
    {
        return blueprint.getSizeZ();
    }

    @Override
    public List<String> getRequiredMods()
    {
        return blueprint.getRequiredMods();
    }

    @Override
    public List<BlockState> getBlockPalette()
    {
        return blueprint.getPalette();
    }

    @Override
    public short[][][] getBlocks()
    {
        return blueprint.getStructure();
    }

    @Override
    public List<CompoundNBT> getEntities()
    {
        return blueprint.getEntitiesAsList();
    }

    @Override
    public CompoundNBT[][][] getTileEntities()
    {
        return blueprint.getTileEntities();
    }
}