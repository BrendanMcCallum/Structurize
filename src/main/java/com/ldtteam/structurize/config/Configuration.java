package com.ldtteam.structurize.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Mod root configuration.
 */
public class Configuration
{
    /*
     * Few instructions:
     * set additional states like "requires world restart":
     * - defineBoolean(builder.worldRestart(), "key", true);
     * don't forget to create lang keys:
     * - modid.config.key - name, usually same as key but formatted
     * - modid.config.key.comment - description
     */
    /**
     * Loaded everywhere, not synced
     */
    private final ModConfig common;
    private final CommonConfiguration commonConfig;
    /**
     * Loaded clientside, not synced
     */
    private final ModConfig client;
    private final ClientConfiguration clientConfig;
    /**
     * Loaded serverside, synced on connection
     */
    private final ModConfig server;
    private final ServerConfiguration serverConfig;

    /**
     * Builds configuration tree.
     *
     * @param modContainer from event
     */
    public Configuration(final ModContainer modContainer)
    {
        final Pair<CommonConfiguration, ForgeConfigSpec> com = new ForgeConfigSpec.Builder().configure(CommonConfiguration::new);
        final Pair<ClientConfiguration, ForgeConfigSpec> cli = new ForgeConfigSpec.Builder().configure(ClientConfiguration::new);
        final Pair<ServerConfiguration, ForgeConfigSpec> ser = new ForgeConfigSpec.Builder().configure(ServerConfiguration::new);
        common = new ModConfig(ModConfig.Type.COMMON, com.getRight(), modContainer);
        client = new ModConfig(ModConfig.Type.CLIENT, cli.getRight(), modContainer);
        server = new ModConfig(ModConfig.Type.SERVER, ser.getRight(), modContainer);
        commonConfig = com.getLeft();
        clientConfig = cli.getLeft();
        serverConfig = ser.getLeft();
        modContainer.addConfig(common);
        modContainer.addConfig(client);
        modContainer.addConfig(server);
    }

    public CommonConfiguration getCommon()
    {
        return commonConfig;
    }

    public ClientConfiguration getClient()
    {
        return clientConfig;
    }

    public ServerConfiguration getServer()
    {
        return serverConfig;
    }
}