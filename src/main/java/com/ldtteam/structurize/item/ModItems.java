package com.ldtteam.structurize.item;

import com.ldtteam.structurize.util.constants.GeneralConstants;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.IForgeRegistry;
import static com.ldtteam.structurize.block.ModBlocks.*;

/**
 * Utils for mod items init
 */
public class ModItems
{
    public static final ModItemGroup CREATIVE_TAB = new ModItemGroup();

    public static final BuildTool BUILD_TOOL = new BuildTool(CREATIVE_TAB);
    public static final ScanTool SCAN_TOOL = new ScanTool(CREATIVE_TAB);
    public static final ShapeTool SHAPE_TOOL = new ShapeTool(CREATIVE_TAB);
    public static final Caliper CALIPER = new Caliper(CREATIVE_TAB);

    static
    {
        CREATIVE_TAB.setIcon(BUILD_TOOL);
    }

    /**
     * Private constructor to hide implicit public one.
     */
    private ModItems()
    {
        /**
         * Intentionally left empty
         */
    }

    /**
     * Register mod items.
     *
     * @param registry forge item registry
     */
    public static void registerItems(final IForgeRegistry<Item> registry)
    {
        registry.registerAll(BUILD_TOOL, SCAN_TOOL, SHAPE_TOOL, CALIPER);
        registry.registerAll(ANYBLOCK_SUBSTITUTION.createSpecialBI(CREATIVE_TAB), newBI(VARIABLE_SUBSTITUTION));
    }

    /**
     * Creates blockitem from given block.
     *
     * @param  block already registered block
     * @return       new BlockItem
     */
    private static BlockItem newBI(final Block block)
    {
        return newBI(block, CREATIVE_TAB);
    }

    /**
     * Creates blockitem from given block and item group.
     *
     * @param  block     already registered block
     * @param  itemGroup creative tab
     * @return           new BlockItem
     */
    private static BlockItem newBI(final Block block, final ItemGroup itemGroup)
    {
        return (BlockItem) new BlockItem(block, new Item.Properties().group(itemGroup)).setRegistryName(block.getRegistryName());
    }

    /**
     * Creative tab
     */
    private static class ModItemGroup extends ItemGroup
    {
        private Item icon;
        private ItemStack latestItemStack;

        /**
         * Create default creative tab.
         */
        private ModItemGroup()
        {
            super(GeneralConstants.MOD_NAME);
        }

        /**
         * Sets tab icon.
         *
         * @param  iconIn tab icon
         * @return        this
         */
        public ModItemGroup setIcon(final Item iconIn)
        {
            icon = iconIn;
            latestItemStack.setCount(0);
            return this;
        }

        @Override
        public ItemStack createIcon()
        {
            latestItemStack = new ItemStack(icon);
            return latestItemStack;
        }
    }
}
