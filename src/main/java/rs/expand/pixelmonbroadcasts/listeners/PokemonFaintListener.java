// Listens for fainted Pokémon in parties. For the Nuzlocke crowd.
package rs.expand.pixelmonbroadcasts.listeners;

import com.pixelmonmod.pixelmon.api.events.PixelmonFaintEvent;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rs.expand.pixelmonbroadcasts.enums.EventData;

import static rs.expand.pixelmonbroadcasts.utilities.PlaceholderMethods.iterateAndBroadcast;
import static rs.expand.pixelmonbroadcasts.utilities.PrintingMethods.logEvent;

public class PokemonFaintListener
{
    @SubscribeEvent
    public void onPartyPokemonFaintEvent(PixelmonFaintEvent event)
    {
        // Make sure our Pokémon has an owner!
        if (event.pokemon.hasOwner())
        {
            // Create shorthand variables for convenience.
            final String baseName = event.pokemon.getPokemonName();
            final String localizedName = event.pokemon.getLocalizedName();

            // If we're in a localized setup, format a string for logging both names.
            final String nameString = baseName.equals(localizedName) ? baseName : baseName + " (" + localizedName + ")";
            EntityPlayerMP playerMP;
            if (event.player != null) {
                playerMP = event.player;
            } else {
                playerMP = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(event.pokemon.getOwnerId());
            }

            // Send a log message if we're set up to do logging for this event.
            logEvent(EventData.Others.FAINT, playerMP.getEntityWorld().getWorldInfo().getWorldName(),
                    playerMP.getPosition(), playerMP.getName(), nameString);

            // Send enabled broadcasts to people who should receive them.
            iterateAndBroadcast(EventData.Others.FAINT,
                    event.pokemon, null, playerMP, null);
        }
    }
}
