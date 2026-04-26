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
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        Map<Integer, MobCheckPlugin.AttackState> npcTickMap = plugin.getNpcNextAttackTickMap();

        // Set larger font
        Font originalFont = graphics.getFont();
        graphics.setFont(new Font("Arial", Font.BOLD, 16));

        for (NPC npc : client.getNpcs())
        {
            if (npc == null || !npcTickMap.containsKey(npc.getIndex()))
            {
                continue;
            }

            MobCheckPlugin.AttackState state = npcTickMap.get(npc.getIndex());
            int ticks = state.ticks;
            if (ticks <= 0)
            {
                continue;
            }

            String text = ticks + " ticks (" + state.style + ")";

            Point canvasTextLocation = npc.getCanvasTextLocation(graphics, text, 0);
            if (canvasTextLocation != null)
            {
                Color color = ticks <= 2 ? Color.RED : Color.WHITE;
                OverlayUtil.renderTextLocation(graphics, canvasTextLocation, text, color);
            }
        }

        // Restore original font
        graphics.setFont(originalFont);

        return null;
    }
}