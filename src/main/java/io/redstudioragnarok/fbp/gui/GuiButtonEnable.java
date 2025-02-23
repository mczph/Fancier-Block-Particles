package io.redstudioragnarok.fbp.gui;

import io.redstudioragnarok.fbp.FBP;
import net.jafama.FastMath;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;

public class GuiButtonEnable extends GuiButton {

	FontRenderer _fr;

	public GuiButtonEnable(int buttonID, int xPos, int yPos, FontRenderer fr) {
		super(buttonID, xPos, yPos, 25, 25, "");

		_fr = fr;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		mc.getTextureManager().bindTexture(FBP.fbpIcon);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		int centerX = x + 25 / 2;
		int centerY = y + 25 / 2;

		double distance = FastMath.sqrtQuick((mouseX - centerX) * (mouseX - centerX) + (mouseY - centerY) * (mouseY - centerY));
		double radius = FastMath.sqrtQuick(2 * Math.pow(16, 2));

		boolean flag = distance <= (radius / 2);
		int i = FBP.enabled ? 0 : 50;

		if (hovered = flag)
			i += 25;

		Gui.drawModalRectWithCustomSizedTexture(this.x, this.y, 0, i, 25, 25, 25, 100);

		final String text = (FBP.enabled ? I18n.format("menu.disable") : I18n.format("menu.enable")) + " FBP";

		if (flag)
			this.drawString(_fr, text, mouseX - _fr.getStringWidth(text) - 25, mouseY - 3, _fr.getColorCode('a'));
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		if (hovered) {
			playPressSound(mc.getSoundHandler());
			return true;
		} else
			return false;
	}
}
