package com.mob_check;

import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.util.Perspective;

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
                graphics.setFont(new Font("Arial", Font.BOLD, 16));
                graphics.setColor(Color.RED);
                graphics.drawString(String.valueOf(ticks), textLocation.getX(), textLocation.getY());
            }
        }

        return null;
    }
}