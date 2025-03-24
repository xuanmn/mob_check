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
        int x = 10;
        int width = 250;

        graphics.setFont(new Font("Arial", Font.PLAIN, 14));

        // Count how many lines will be shown
        long lineCount = client.getNpcs().stream()
                .filter(npc -> {
                    Integer ticks = npcMap.get(npc.getIndex());
                    return ticks != null && ticks > 0;
                })
                .count();

        if (lineCount == 0)
        {
            return null;
        }

        // Draw semi-transparent background
        graphics.setColor(new Color(0, 0, 0, 128)); // semi-transparent black
        graphics.fillRect(x - 5, y - 15, width, (int) lineCount * 18 + 10);

        // Draw each line
        for (NPC npc : client.getNpcs())
        {
            Integer ticks = npcMap.get(npc.getIndex());
            if (ticks == null || ticks <= 0)
            {
                continue;
            }

            String line = npc.getName() + " (" + npc.getIndex() + "): " + ticks + " ticks";
            Color tickColor = ticks <= 1 ? Color.RED : ticks <= 3 ? Color.ORANGE : Color.GREEN;

            graphics.setColor(tickColor);
            graphics.drawString(line, x, y);
            y += 18;
        }

        return new Dimension(width, y);
    }
}
