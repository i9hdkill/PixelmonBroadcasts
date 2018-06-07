package rs.expand.pixelmonbroadcasts.utilities;

// Remote imports.
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.*;

import static rs.expand.pixelmonbroadcasts.PixelmonBroadcasts.broadcastConfig;
import static rs.expand.pixelmonbroadcasts.PixelmonBroadcasts.messageConfig;

// A collection of methods that are commonly used. One changed word or color here, and half the mod changes. Sweet.
public class PrintingMethods
{
    // Remove the ugly prefix from console commands, so we can roll our own. Thanks for the examples, NickImpact!
    private static Optional<ConsoleSource> getConsole()
    {
        if (Sponge.isServerAvailable())
            return Optional.of(Sponge.getServer().getConsole());
        else
            return Optional.empty();
    }

    // If we need to print something without any major formatting, do it here. Good for console lists.
    public static void printBasicMessage(final String inputString)
    {
        getConsole().ifPresent(console ->
                console.sendMessage(Text.of("§f" + inputString)));
    }

    // If we need to show a basic error, do it here.
    public static void printBasicError(final String inputString)
    {
        getConsole().ifPresent(console ->
                console.sendMessage(Text.of("§4PBR §f// §4Error: §c" + inputString)));
    }

    // If we can't read a main config options bundle (really just a String), format and throw this error.
    static void printOptionsNodeError(final List<String> nodes)
    {
        for (final String node : nodes)
            printBasicMessage("§cCould not read options node \"§4" + node + "§c\".");

        printBasicMessage("§cThe main config contains invalid options. Disabling these.");
        printBasicMessage("§cCheck the config, and when fixed use §4/pixelmonbroadcasts reload§c.");
    }

    // If we can't read a main config message, format and throw this error.
    static void printMessageNodeError(final List<String> nodes)
    {
        for (final String node : nodes)
            printBasicMessage("§cCould not read message node \"§4" + node + "§c\".");

        printBasicMessage("§cThe main config contains invalid messages. Hiding these.");
        printBasicMessage("§cCheck the config, and when fixed use §4/pixelmonbroadcasts reload§c.");
    }

    // Check if the given player has the given flag (or flags) set, and if so, return a clean status that we can use.
    public static boolean checkToggleStatus(final EntityPlayerMP recipient, final String... flags)
    {
        // Did we get provided just one flag? This will generally be the most common situation.
        if (flags.length == 1)
        {
            // Does the player have a flag set for this notification?
            if (recipient.getEntityData().getCompoundTag("pbToggles").hasKey(flags[0]))
            {
                // Return the flag's status.
                return recipient.getEntityData().getCompoundTag("pbToggles").getBoolean(flags[0]);
            }

            // Player does not have the flag, so return the default state ("true").
            return true;
        }
        /*                                                                                                    *\
           vvv Currently unused. Was used for a little bit, but then disabled in favor of specific flags. vvv

        // We do not, look through all the flags and see if any are disabled.
        else if (flags.length > 1)
        {
            // Set up a Set of the state of all provided flags.
            Set<Boolean> flagSet = new HashSet<>();

            // Loop through all provided flags, and add them to the Set.
            for (String flag : flags)
            {
                // Does the player have a flag set for this notification?
                if (recipient.getEntityData().getCompoundTag("pbToggles").hasKey(flag))
                {
                    // Return the flag's status.
                    flagSet.add(recipient.getEntityData().getCompoundTag("pbToggles").getBoolean(flag));
                }
                else
                    flagSet.add(true);
            }

            // Return "true" if all flags are either true or unset (default state is true). Return "false" otherwise.
            return !flagSet.contains(false);
        }
        \*                                                                                                    */
        else
        {
            printBasicError("Could not find any player toggles to check. Swallowing message.");
            printBasicError("This is a bug, please report this. Include the specific event.");
            return false;
        }
    }

    // Checks whether a broadcast's message is present. Prints an error if the actual config could not be read.
    public static boolean checkBroadcastStatus(String key)
    {
        if (broadcastConfig != null)
            return broadcastConfig.getNode(key).getString() != null;
        else
        {
            printBasicError("The broadcasts config could not be read! Broadcasts will not work!");
            return false;
        }
    }

    // Gets a key from broadcasts.conf, formats it (ampersands to section characters), and then returns it.
    // Also swaps any provided placeholders with String representations of the Objects given, if present.
    public static String sendBroadcast(String key, final Object... params)
    {
        if (broadcastConfig != null)
        {
            // Get the broadcast from the broadcast config, if it's there.
            String broadcast = broadcastConfig.getNode(key).getString();

            // Did we get a broadcast?
            if (broadcast != null)
            {
                // If any parameters are available, find all placeholders in the broadcast and replace them.
                for (int i = 0; i < params.length; i++)
                    broadcast = broadcast.replace("{" + i+1 + "}", params[i].toString());

                return TextSerializers.FORMATTING_CODE.deserialize(broadcast).toString();
            }
            // We did not get a broadcast, return the provided key and make sure it's unformatted.
            else
                return "§r" + key;
        }
        // We could not read the config, return the provided key and make sure it's unformatted.
        else
            return "§r" + key;
    }

    // Gets a key from messages.conf, formats it (ampersands to section characters), and then returns it.
    // Also swaps any provided placeholders with String representations of the Objects given, if present.
    public static String getTranslation(String key, final Object... params)
    {
        if (messageConfig != null)
        {
            // Get the message from the message config, if it's there.
            String message = messageConfig.getNode("legendarySpawnMessage").getString();

            // Did we get a message?
            if (message != null)
            {
                // If any parameters are available, find all placeholders in the message and replace them.
                for (int i = 0; i < params.length; i++)
                    message = message.replace("{" + i+1 + "}", params[i].toString());

                return TextSerializers.FORMATTING_CODE.deserialize(message).toString();
            }
            // We did not get a message, return the provided key and make sure it's unformatted.
            else
                return "§r" + key;
        }
        // We could not read the config, return the provided key and make sure it's unformatted.
        else
            return "§r" + key;
    }

    // Gets a key from messages.conf, formats it (ampersands to section characters), and then sends it.
    // Also swaps any provided placeholders with String representations of the Objects given, if present.
    public static void sendTranslation(CommandSource recipient, String key, Object... params)
    {
        if (messageConfig != null)
        {
            // Get the message from the message config, if it's there.
            String message = messageConfig.getNode("legendarySpawnMessage").getString();

            // Did we get a message?
            if (message != null)
            {
                // If any parameters are available, find all placeholders in the message and replace them.
                for (int i = 0; i < params.length; i++)
                    message = message.replace("{" + i+1 + "}", params[i].toString());

                recipient.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(message).toText());
            }
            // We did not get a message, return the provided key and make sure it's unformatted.
            else
                recipient.sendMessage(Text.of("§r" + key));
        }
        // We could not read the config, return the provided key and make sure it's unformatted.
        else
            recipient.sendMessage(Text.of("§r" + key));
    }
}
