package com.mob_check;

import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.*;
import java.util.Map;

/**
 * This overlay displays a tick countdown above NPCs that are being tracked
 * by the MobCheckPlugin. The countdown indicates how many ticks remain until
 * the NPC performs its next attack.
 */
public class MobCheckOverlay extends Overlay
{
    private final Client client;
    private final MobCheckPlugin plugin;

    /**
     * Constructor that gets injected with the current RuneLite client instance
     * and a reference to the MobCheckPlugin (used to fetch tracked NPC data).
     */
    @Inject
    public MobCheckOverlay(Client client, MobCheckPlugin plugin)
    {
        this.client = client;
        this.plugin = plugin;

        // Set this overlay to draw dynamically above the scene
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    /**
     * This method runs every frame to render the overlay.
     * It loops through all NPCs and, if they're being tracked, displays a countdown.
     */
    @Override
    public Dimension render(Graphics2D graphics)
    {
        // Get the map of NPC index → ticks remaining until next attack
        Map<Integer, Integer> npcTickMap = plugin.getNpcNextAttackTickMap();

        // Save the original font and switch to a bold, larger one for readability
        Font originalFont = graphics.getFont();
        graphics.setFont(new Font("Arial", Font.BOLD, 18));

        // Loop through all NPCs in the game world
        for (NPC npc : client.getNpcs())
        {
            if (npc == null || !npcTickMap.containsKey(npc.getIndex()))
            {
                continue; // Skip NPCs not being tracked
            }

            int ticks = npcTickMap.get(npc.getIndex());
            if (ticks <= 0)
            {
                continue; // Skip if the countdown has expired
            }

            String text = ticks + " ticks";

            // Get the location above the NPC head to render the text
            Point canvasTextLocation = npc.getCanvasTextLocation(graphics, text, 0);
            if (canvasTextLocation != null)
            {
                // Red if ticks is 2 or smaller, white otherwise
                Color color = ticks <= 2 ? Color.RED : Color.WHITE;
                OverlayUtil.renderTextLocation(graphics, canvasTextLocation, text, color);
            }
        }

        // Restore the original font after rendering
        graphics.setFont(originalFont);
        return null;
    }
}