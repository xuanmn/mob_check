package com.mob_check;

import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Point;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;
import java.util.Map;

public class MobCheckOverlay extends Overlay
{
    private final Client client;
    private final MobCheckPlugin plugin;

    // Optional font
    private static final Font FONT = new Font("Arial", Font.BOLD, 16);

    @Inject
    public MobCheckOverlay(Client client, MobCheckPlugin plugin)
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

        if (npcMap.isEmpty()) {
            return null;
        }

        graphics.setFont(FONT); // Set the custom font here

        for (Map.Entry<NPC, Integer> entry : npcMap.entrySet())
        {
            NPC npc = entry.getKey();
            int nextAttackTick = entry.getValue();

            int ticksRemaining = nextAttackTick - client.getTickCount();

            if (ticksRemaining <= 0) {
                continue; // Already attacking or outdated
            }

            LocalPoint localLocation = npc.getLocalLocation();
            if (localLocation == null) {
                continue;
            }

            Point canvasPoint = Perspective.getCanvasTextLocation(
                    client,
                    graphics,
                    localLocation,
                    String.valueOf(ticksRemaining),
                    40
            );

            if (canvasPoint != null) {
                Color tickColor = getColorForTicks(ticksRemaining);
                renderTextWithOutline(graphics, String.valueOf(ticksRemaining), canvasPoint, tickColor, Color.BLACK);
            }
        }

        return null;
    }

    /**
     * Gets a color depending on how many ticks are left.
     * Red when close, green when far.
     */
    private Color getColorForTicks(int ticksRemaining)
    {
        switch (ticksRemaining)
        {
            case 1: return Color.RED;
            case 2: return Color.ORANGE;
            case 3: return Color.YELLOW;
            default: return Color.GREEN;
        }
    }

    /**
     * Renders text with a solid outline for visibility.
     */
    private void renderTextWithOutline(Graphics2D graphics, String text, Point point, Color fillColor, Color outlineColor)
    {
        graphics.setColor(outlineColor);
        int outlineSize = 2;

        // Draw outline by drawing text slightly offset in multiple directions
        for (int x = -outlineSize; x <= outlineSize; x++) {
            for (int y = -outlineSize; y <= outlineSize; y++) {
                graphics.drawString(text, point.getX() + x, point.getY() + y);
            }
        }

        // Draw the main text on top
        graphics.setColor(fillColor);
        graphics.drawString(text, point.getX(), point.getY());
    }
}