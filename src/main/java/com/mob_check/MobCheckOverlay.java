package com.mob_check;

import net.runelite.client.util.Perspective;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import java.awt.*;
import java.util.Map;

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
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPriority(OverlayPriority.HIGH);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        Map<Integer, Integer> npcMap = plugin.getNpcNextAttackTickMap();

        for (NPC npc : client.getNpcs())
        {
            if (!npcMap.containsKey(npc.getIndex()))
            {
                continue;
            }

            int ticks = npcMap.get(npc.getIndex());
            if (ticks <= 0)
            {
                continue;
            }

            LocalPoint lp = npc.getLocalLocation();
            if (lp == null)
            {
                continue;
            }

            Point textLocation = Perspective.getCanvasTextLocation(client, graphics, lp, npc.getLogicalHeight() + 40);
            if (textLocation != null)
            {
                // Determine color based on ticks
                Color tickColor;
                if (ticks <= 1)
                    tickColor = Color.RED;
                else if (ticks <= 3)
                    tickColor = Color.ORANGE;
                else
                    tickColor = Color.GREEN;

                String tickText = String.valueOf(ticks);
                graphics.setFont(new Font("Arial", Font.BOLD, 16));

                // Shadow
                graphics.setColor(Color.BLACK);
                graphics.drawString(tickText, textLocation.getX() + 1, textLocation.getY() + 1);

                // Foreground
                graphics.setColor(tickColor);
                graphics.drawString(tickText, textLocation.getX(), textLocation.getY());
            }
        }

        return null;
    }
}