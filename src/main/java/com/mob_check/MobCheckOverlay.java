package com.mob_check;

import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.SpriteID;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.InfoBoxComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

public class MobCheckOverlay extends Overlay
{
	private final Client client;
	private final MobCheckPlugin plugin;
	private final SpriteManager spriteManager;
	private final PanelComponent panelComponent = new PanelComponent();

	@Inject
	public MobCheckOverlay(Client client, MobCheckPlugin plugin, SpriteManager spriteManager)
	{
		this.client = client;
		this.plugin = plugin;
		this.spriteManager = spriteManager;
		setPosition(OverlayPosition.TOP_LEFT);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		panelComponent.getChildren().clear();

		plugin.getPriorityAttack().ifPresent(priority -> {
			BufferedImage sprite = getPrayerSprite(priority.style);

			if (sprite != null)
			{
				// 1. Add to the side panel
				InfoBoxComponent infoBox = new InfoBoxComponent();
				infoBox.setImage(sprite);
				infoBox.setText(priority.ticks + "t");
				infoBox.setColor(priority.ticks <= 1 ? Color.RED : Color.WHITE);
				infoBox.setBackgroundColor(new Color(0, 0, 0, 150));
				panelComponent.getChildren().add(infoBox);

				// 2. Render above player's head
				renderAbovePlayer(graphics, sprite, priority.ticks);
			}
		});

		return panelComponent.render(graphics);
	}

	private void renderAbovePlayer(Graphics2D graphics, BufferedImage sprite, int ticks)
	{
		Player player = client.getLocalPlayer();
		if (player == null) return;

		// Offset slightly to the side of the actual overheads
		net.runelite.api.Point point = player.getCanvasImageLocation(sprite, player.getLogicalHeight() + 100);
		if (point != null)
		{
			graphics.drawImage(sprite, point.getX() - 40, point.getY(), null);

			// Draw tick countdown next to the icon
			graphics.setColor(ticks <= 1 ? Color.RED : Color.WHITE);
			graphics.setFont(new Font("Arial", Font.BOLD, 18));
			graphics.drawString(ticks + "t", point.getX() - 5, point.getY() + (sprite.getHeight() / 2) + 5);
		}
	}

	private BufferedImage getPrayerSprite(String style)
	{
		int spriteId;
		switch (style)
		{
			case "Pray Magic": spriteId = SpriteID.PRAYER_PROTECT_FROM_MAGIC; break;
			case "Pray Range": spriteId = SpriteID.PRAYER_PROTECT_FROM_MISSILES; break;
			case "Pray Melee": spriteId = SpriteID.PRAYER_PROTECT_FROM_MELEE; break;
			default: return null;
		}
		return spriteManager.getSprite(spriteId, 0);
	}
}