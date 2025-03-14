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

public class mobCheckOverlay extends Overlay
{
    private final Client client;
    private final mobCheckPlugin plugin;

    @Inject
    public mobCheckOverlay(Client client, mobCheckPlugin plugin)
    {
        this.client = client;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        Map<NPC, Integer> npcMap = plugin.getNpcNextAttackTickMap();

        for (Map.Entry<NPC, Integer> entry : npcMap.entrySet())
        {
            NPC npc = entry.getKey();
            int nextAttackTick = entry.getValue();
            int ticksRemaining = nextAttackTick - client.getTickCount();

            if (ticksRemaining <= 0)
            {
                continue; // Attack happening now or outdated
            }

            // Get location to draw
            Point canvasPoint = npc.getCanvasTextLocation(graphics, String.valueOf(ticksRemaining), 40);

            if (canvasPoint != null)
            {
                OverlayUtil.renderTextLocation(graphics, canvasPoint, String.valueOf(ticksRemaining), Color.RED);
            }
        }

        return null;
    }
}