package com.ldtteam.structurize.client.render;

import com.ldtteam.structurize.Structurize;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * The optifine compat layer.
 * Allows shaders to work somewhat.
 */
public class OptifineCompat
{
    private Method isShadersEnabledMethod;
    private Method preRenderChunkLayerMethod;
    private Method postRenderChunkLayerMethod;
    private Method setupArrayPointersVboMethod;
    private Method calcNormalForLayerMethod;
    private Method beginUpdateChunksMethod;
    private Method endUpdateChunksMethod;
    private Field isShadowPassField;
    private boolean currentShadowPassFieldValue = false;

    private boolean enableOptifine;

    /**
     * Creates new instance.
     */
    public OptifineCompat()
    {
        enableOptifine = false;
    }

    /**
     * Initializes the compat layer.
     * Makes sure that all relevant classes are available as well as all required methods.
     * Will disable compat if either a class is missing, or a method is missing.
     * This ensures that if, optifines structure changes we do not crash and just disable the compat.
     */
    public void intialize()
    {
        try
        {
            setupReflectedMethodReferences();

            Structurize.getLogger().info("Optifine found. Enabling compat.");
            enableOptifine = true;
        }
        catch (final ClassNotFoundException e)
        {
            Structurize.getLogger().info("Optifine not found. Disabling compat.");
            enableOptifine = false;
        }
        catch (final NoSuchMethodException e)
        {
            Structurize.getLogger().error("Optifine found. But could not access related methods.", e);
            enableOptifine = false;
        }
        catch (final NoSuchFieldException e)
        {
            Structurize.getLogger().error("Optifine found. But could not access related fields", e);
            enableOptifine = false;
        }
    }

    /**
     * Performs the reflective access to the Optifine related methods.
     *
     * @throws ClassNotFoundException Thrown when a optifine class is missing.
     * @throws NoSuchMethodException  Thrown when a optifine method is missing.
     * @throws NoSuchFieldException   Thrown when a optifine field is missing.
     */
    private void setupReflectedMethodReferences() throws ClassNotFoundException, NoSuchMethodException, NoSuchFieldException
    {
        final Class<?> configClass = Class.forName("net.optifine.Config");
        final Class<?> shaderRenderClass = Class.forName("net.optifine.shaders.ShadersRender");
        final Class<?> sVertexBuilderClass = Class.forName("net.optifine.shaders.SVertexBuilder");
        final Class<?> shadersClass = Class.forName("net.optifine.shaders.Shaders");

        isShadersEnabledMethod = configClass.getMethod("isShaders");
        isShadersEnabledMethod.setAccessible(true);

        preRenderChunkLayerMethod = shaderRenderClass.getMethod("preRenderChunkLayer", BlockRenderLayer.class);
        preRenderChunkLayerMethod.setAccessible(true);

        postRenderChunkLayerMethod = shaderRenderClass.getMethod("postRenderChunkLayer", BlockRenderLayer.class);
        postRenderChunkLayerMethod.setAccessible(true);

        setupArrayPointersVboMethod = shaderRenderClass.getMethod("setupArrayPointersVbo");
        setupArrayPointersVboMethod.setAccessible(true);

        calcNormalForLayerMethod = sVertexBuilderClass.getMethod("calcNormalChunkLayer", BufferBuilder.class);
        calcNormalForLayerMethod.setAccessible(true);

        beginUpdateChunksMethod = shadersClass.getMethod("beginUpdateChunks");
        beginUpdateChunksMethod.setAccessible(true);

        endUpdateChunksMethod = shadersClass.getMethod("endUpdateChunks");
        endUpdateChunksMethod.setAccessible(true);

        isShadowPassField = shadersClass.getField("isShadowPass");
        isShadowPassField.setAccessible(true);
    }

    /**
     * Call to setup the shader in Optifine.
     * Checks if the compat is enabled or not.
     */
    @OnlyIn(Dist.CLIENT)
    public void preBlueprintDraw()
    {
        if (!enableOptifine)
        {
            return;
        }

        try
        {
            if ((Boolean) isShadersEnabledMethod.invoke(null))
            {
                currentShadowPassFieldValue = (boolean) isShadowPassField.get(null);
                isShadowPassField.set(null, false);

                beginUpdateChunksMethod.invoke(null);
                preRenderChunkLayerMethod.invoke(null, BlockRenderLayer.TRANSLUCENT);
            }
        }
        catch (final IllegalAccessException e)
        {
            shutdown("access", e);
        }
        catch (final InvocationTargetException e)
        {
            shutdown("invoke", e);
        }
    }

    /**
     * Call to disable the shader
     * Checks if the compat is enabled or not.
     */
    @OnlyIn(Dist.CLIENT)
    public void postBlueprintDraw()
    {
        if (!enableOptifine)
        {
            return;
        }

        try
        {
            if ((Boolean) isShadersEnabledMethod.invoke(null))
            {
                postRenderChunkLayerMethod.invoke(null, BlockRenderLayer.TRANSLUCENT);
                endUpdateChunksMethod.invoke(null);

                isShadowPassField.set(null, currentShadowPassFieldValue);
            }
        }
        catch (final IllegalAccessException e)
        {
            shutdown("access", e);
        }
        catch (final InvocationTargetException e)
        {
            shutdown("invoke", e);
        }
    }

    /**
     * Called to setup the pointers in the arrays.
     *
     * @return True when optifine is enabled and setup completed, false when not.
     */
    @OnlyIn(Dist.CLIENT)
    public boolean setupArrayPointers()
    {
        if (!enableOptifine)
        {
            return false;
        }

        try
        {
            if ((Boolean) isShadersEnabledMethod.invoke(null))
            {
                setupArrayPointersVboMethod.invoke(null);
                return true;
            }
        }
        catch (final IllegalAccessException e)
        {
            shutdown("access", e);
        }
        catch (final InvocationTargetException e)
        {
            shutdown("invoke", e);
        }

        return false;
    }

    /**
     * Called to handle the buffer information for optifine.
     * Calculates the normals of the faces.
     *
     * @param tessellator The tessellator that is about to be uploaded to the GPU.
     */
    @OnlyIn(Dist.CLIENT)
    public void beforeBuilderUpload(final StructureTessellator tessellator)
    {
        if (!enableOptifine)
        {
            return;
        }

        try
        {
            if ((Boolean) isShadersEnabledMethod.invoke(null))
            {
                Structurize.getLogger().info("Recalculating normals in Optifine mode.");
                calcNormalForLayerMethod.invoke(null, tessellator.getBuilder());
            }
        }
        catch (final IllegalAccessException e)
        {
            shutdown("access", e);
        }
        catch (final InvocationTargetException e)
        {
            shutdown("invoke", e);
        }
    }

    /**
     * Fires errors into logger. Prevents all future executions of every method.
     *
     * @param invokeOrAccess invoke or access string
     * @param e              expection reference
     */
    private void shutdown(final String invokeOrAccess, final Exception e)
    {
        Structurize.getLogger().error("Failed to {} Optifine related rendering methods. Disabling Optifine Compat.", invokeOrAccess);
        Structurize.getLogger().error("", e);
        enableOptifine = false;
    }
}
