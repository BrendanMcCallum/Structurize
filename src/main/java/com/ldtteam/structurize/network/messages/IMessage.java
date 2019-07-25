package com.ldtteam.structurize.network.messages;

import org.jetbrains.annotations.Nullable;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * Interface for all network messages
 */
public interface IMessage
{
    /**
     * Writes message data to buffer.
     *
     * @param buf network data byte buffer
     */
    void toBytes(PacketBuffer buf);

    /**
     * Reads message data from buffer.
     *
     * @param buf network data byte buffer
     */
    void fromBytes(PacketBuffer buf);

    /**
     * Which sides is message able to be executed at.
     *
     * @return CLIENT or SERVER or null (for both)
     */
    @Nullable
    LogicalSide getExecutionSide();

    /**
     * Executes message action.
     *
     * @param ctxIn           network context of incoming message
     * @param isLogicalServer whether message arrived at logical server side
     */
    void onExecute(NetworkEvent.Context ctxIn, boolean isLogicalServer);
}
