package com.ldtteam.structurize.block;

import java.util.concurrent.atomic.AtomicBoolean;
import com.ldtteam.structurize.client.render.EventRenderer;
import com.ldtteam.structurize.util.constants.MathConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.PushReaction;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

/**
 * Anyblock substitution block class
 */
public class AnyblockSubstitution extends Block implements IAnchorBlock
{
    /**
     * Whether block instances should render texture or not.
     */
    private static final AtomicBoolean SHOULD_RENDER_BLOCK_TEXTURE = new AtomicBoolean(false);

    /**
     * Creates default anyblock substitution block.
     */
    public AnyblockSubstitution()
    {
        this(
            Properties.create(new Material(MaterialColor.WOOD, false, true, true, true, true, false, false, PushReaction.BLOCK))
                .doesNotBlockMovement()
                .hardnessAndResistance(1.0F));
    }

    /**
     * MC constructor.
     *
     * @param properties properties
     */
    public AnyblockSubstitution(final Properties properties)
    {
        super(properties);
        setRegistryName("anyblocksubstitution");
    }

    @Override
    public boolean propagatesSkylightDown(final BlockState state, final IBlockReader reader, final BlockPos pos)
    {
        return true; // !SHOULD_RENDER_BLOCK_TEXTURE.get(); does not work since it's only checked when resource reloading
    }

    @Override
    public BlockRenderType getRenderType(final BlockState state)
    {
        return SHOULD_RENDER_BLOCK_TEXTURE.get() ? BlockRenderType.MODEL : BlockRenderType.INVISIBLE;
    }

    /**
     * Creates special blockitem.
     *
     * @param itemGroup creative tab
     * @return new BlockItem
     */
    public BlockItem createSpecialBI(final ItemGroup itemGroup)
    {
        return (BlockItem) new BlockItem(this, new Item.Properties().group(itemGroup)){
            @Override
            public ActionResult<ItemStack> onItemRightClick(final World worldIn, final PlayerEntity playerIn, final Hand handIn)
            {
                if (worldIn.isRemote())
                {
                    SHOULD_RENDER_BLOCK_TEXTURE.set(!SHOULD_RENDER_BLOCK_TEXTURE.get());
                    EventRenderer.recompileTesselators();
                    final BlockPos center = playerIn.getPosition();
                    ((ClientWorld) worldIn).markSurroundingsForRerender(
                        center.getX() / MathConstants.CHUNK_BLOCK_SIZE,
                        center.getY() / MathConstants.CHUNK_BLOCK_SIZE,
                        center.getZ() / MathConstants.CHUNK_BLOCK_SIZE);
                }
                return new ActionResult<>(ActionResultType.SUCCESS, playerIn.getHeldItem(handIn));
            }
        }.setRegistryName(this.getRegistryName());
    }
}
