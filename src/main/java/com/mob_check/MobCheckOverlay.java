package com.mob_check;

import net.runelite.api.Client;
import net.runelite.api.NPC;
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
        setPosition(OverlayPosition.TOP_LEFT);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        setPriority(OverlayPriority.MED);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        Map<Integer, Integer> npcMap = plugin.getNpcNextAttackTickMap();
        int y = 20;

        graphics.setFont(new Font("Arial", Font.PLAIN, 14));

        for (NPC npc : client.getNpcs())
        {
            Integer ticks = npcMap.get(npc.getIndex());
            if (ticks == null || ticks <= 0)
            {
                continue;
            }

            String line = npc.getName() + " (" + npc.getIndex() + "): " + ticks + " ticks";
            graphics.setColor(ticks <= 1 ? Color.RED : ticks <= 3 ? Color.ORANGE : Color.GREEN);
            graphics.drawString(line, 10, y);
            y += 16;
        }

        return new Dimension(200, y);
    }
}