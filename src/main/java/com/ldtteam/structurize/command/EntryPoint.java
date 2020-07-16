package com.ldtteam.structurize.command;

import com.ldtteam.structurize.util.constant.Constants;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands.EnvironmentType;

/**
 * Mod entry command
 */
public class EntryPoint extends AbstractCommand
{
    /*
     * BRIGADIER manual:
     * use command tree to build complicated trees
     * use argument/command builder to create leaf commands:
     * - use #newLiteral() for subcommand or finite type selection
     * - use #newArgument() for collection selectors/number or message input etc., see net.minecraft.command.arguments
     * use builder#requires() to set e.g. permissions
     * use builder#executes() to set action on execute
     * use builder#redirect() to set alias, use aliases sparely! (they might cause confusion)
     * use builder#fork() to create fork - note: I don't know why would you fork arguments and how does this work, the only usage is
     * mc execute command
     * client sided commands are not possible now (as of 25-06-2019)
     */
    /**
     * Private constructor to hide implicit public one.
     */
    private EntryPoint()
    {
        /**
         * Intentionally left empty
         */
    }

    /**
     * Registers mod command tree to given dispatcher.
     *
     * @param dispatcher main server command dispatcher
     */
    public static void register(final CommandDispatcher<CommandSource> dispatcher, final EnvironmentType environment)
    {
        final CommandTree structurizeRoot = CommandTree.newRootNode()
            .addNode(EventMovesCommand::build, EventMovesCommand::getEnvironmentType);

        structurizeRoot.register(dispatcher, environment);
    }
}
