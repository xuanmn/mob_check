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
 * Overlay that displays a tick countdown above tracked NPCs, showing
 * how many game ticks remain until their next attack.
 */
public class MobCheckOverlay extends Overlay
{
    private final Client client;
    private final MobCheckPlugin plugin;

    @Inject
    public MobCheckOverlay(Client client, MobCheckPlugin plugin)
    {
        this.client = client;
        this.plugin = plugin;

        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE); // Draw above 3D game scene
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        Map<Integer, Integer> npcTickMap = plugin.getNpcNextAttackTickMap();

        // Use a bold font for better visibility
        Font originalFont = graphics.getFont();
        graphics.setFont(new Font("Arial", Font.BOLD, 18));

        for (NPC npc : client.getNpcs())
        {
            if (npc == null)
                continue;

            int index = npc.getIndex();
            Integer ticksRemaining = npcTickMap.get(index);

            if (ticksRemaining == null || ticksRemaining <= 0)
                continue;

            String text = ticksRemaining + " ticks";

            // Try to get the canvas position above the NPC's head
            Point canvasTextLocation = npc.getCanvasTextLocation(graphics, text, 0);
            if (canvasTextLocation != null)
            {
                Color textColor = ticksRemaining <= 2 ? Color.RED : Color.WHITE;
                OverlayUtil.renderTextLocation(graphics, canvasTextLocation, text, textColor);
            }
        }

        graphics.setFont(originalFont);
        return null;
    }
}