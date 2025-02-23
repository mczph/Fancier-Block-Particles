package io.redstudioragnarok.fbp.gui.menu;

import io.redstudioragnarok.fbp.FBP;
import io.redstudioragnarok.fbp.gui.*;
import io.redstudioragnarok.fbp.handlers.ConfigHandler;
import io.redstudioragnarok.fbp.utils.MathUtil;
import io.redstudioragnarok.fbp.utils.ModReference;
import io.redstudioragnarok.fbp.vectors.Vector2D;
import net.jafama.FastMath;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

import java.awt.Desktop;
import java.awt.Dimension;
import java.util.Arrays;

import static io.redstudioragnarok.fbp.gui.FBPGuiButton.ButtonSize.*;

public class Page0 extends GuiScreen {

	GuiButton defaults, done, reload, enable, reportBug;
	GuiButton next;

	GuiButton infiniteDuration, timeUnit;
	GuiSlider minAge, maxAge, particlesPerAxis, scaleMult, gravityMult, rotationMult;

	Vector2D lastHandle = new Vector2D();
	Vector2D lastSize = new Vector2D();

	Vector2D handle = new Vector2D();
	Vector2D size = new Vector2D();

	long time, lastTime;

	int selected = 0;

	final int GUIOffsetY = 8;

	@Override
	public void initGui() {
		int x = this.width / 2 - 100;

		minAge = new GuiSlider(x, this.height / 5 - 10 + GUIOffsetY, (float) ((FBP.minAge - 10) / 90.0));
		maxAge = new GuiSlider(x, minAge.y + minAge.height + 1, (float) ((FBP.maxAge - 10) / 90.0));

		particlesPerAxis = new GuiSlider(x, maxAge.y + 6 + maxAge.height, (float) ((FBP.particlesPerAxis - 2) / 3.0));
		scaleMult = new GuiSlider(x, particlesPerAxis.y + particlesPerAxis.height + 1, (float) ((FBP.scaleMult - 0.75) / 0.5));
		gravityMult = new GuiSlider(x, scaleMult.y + scaleMult.height + 6, (float) ((FBP.gravityMult - 0.05) / 2.95));
		rotationMult = new GuiSlider(x, gravityMult.y + gravityMult.height + 1, (float) (FBP.rotationMult / 1.5));

		infiniteDuration = new FBPGuiButton(11, x + 205, minAge.y + 10, small , (FBP.infiniteDuration ? "\u00A7a" : "\u00A7c") + "\u221e", false, false, true);
		timeUnit = new FBPGuiButton(12, x - 25, minAge.y + 10, small , "\u00A7a\u00A7L" + (FBP.showInMillis ? "ms" : "ti"), false, false, true);

		defaults = new FBPGuiButton(0, this.width / 2 + 2, rotationMult.y + rotationMult.height + 24 - GUIOffsetY, medium , I18n.format("menu.defaults"), false, false, true);
		done = new FBPGuiButton(-1, x, defaults.y, medium , I18n.format("menu.done"), false, false, true);
		reload = new FBPGuiButton(-2, x, defaults.y + defaults.height + 1, large , I18n.format("menu.reloadconfig"), false, false, true);
		enable = new GuiButtonEnable(-6, (this.width - 25 - 27) - 4, 2, this.fontRenderer);
		reportBug = new GuiButtonBugReport(-4, this.width - 27, 2, new Dimension(width, height), this.fontRenderer);

		next = new FBPGuiButton(-3, rotationMult.x + rotationMult.width + 25, rotationMult.y - 4 - GUIOffsetY, small, "\u00A76>>", false, false, true);

		this.buttonList.addAll(Arrays.asList(defaults, done, reload, enable, reportBug, next));
		this.buttonList.addAll(Arrays.asList(minAge, maxAge, particlesPerAxis, scaleMult, gravityMult, rotationMult, infiniteDuration, timeUnit));

		update();
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		switch (button.id) {
		case -6:
			FBP.setEnabled(!FBP.enabled);
			break;
		case -5:
			FBP.showInMillis = !FBP.showInMillis;
			break;
		case -4:
			try {
				Desktop.getDesktop().browse(ModReference.newIssueLink);
			} catch (Exception e) {
				// TODO: (Debug Mode) This should count to the problem counter and should output a stack trace
			}
			break;
		case -3:
			this.mc.displayGuiScreen(new Page1());
			break;
		case -2:
			ConfigHandler.init();
			break;
		case -1:
			this.mc.displayGuiScreen(null);
			break;
		case 0:
			this.mc.displayGuiScreen(new GuiYesNo(this));
			break;
		case 11:
			infiniteDuration.displayString = ((FBP.infiniteDuration = !FBP.infiniteDuration) ? "\u00A7a" : "\u00A7c") + "\u221e";
			update();
			break;
		case 12:
			timeUnit.displayString = "\u00A7a\u00A7L" + ((FBP.showInMillis = !FBP.showInMillis) ? "ms" : "ti");
			break;
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return true;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		GuiHelper.background(minAge.y - 6 - GUIOffsetY, done.y - 4, width, height);

		int sParticleCountBase = FastMath.round(2 + 3 * particlesPerAxis.value);

		int sMinAge = (int) (10 + 90 * minAge.value);
		int sMaxAge = (int) (10 + 90 * maxAge.value);

		double sScaleMult = MathUtil.round((float) (0.75 + 0.5 * scaleMult.value), 2);
		double sGravityForce = MathUtil.round((float) (0.05 + 2.95 * gravityMult.value), 2);
		double sRotSpeed = MathUtil.round((float) (1.5 * rotationMult.value), 2);

		if (FBP.maxAge < sMinAge) {
			FBP.maxAge = sMinAge;

			maxAge.value = (float) ((FBP.maxAge - 10) / 90.0);
		}

		if (FBP.minAge > sMaxAge) {
			FBP.minAge = sMaxAge;

			minAge.value = (float) ((FBP.minAge - 10) / 90.0);
		}

		FBP.minAge = sMinAge;
		FBP.maxAge = sMaxAge;

		FBP.scaleMult = (float) sScaleMult;
		FBP.gravityMult = (float) sGravityForce;
		FBP.rotationMult = (float) sRotSpeed;
		FBP.particlesPerAxis = sParticleCountBase;

		particlesPerAxis.value = (float) ((FBP.particlesPerAxis - 2) / 3.0);

		drawMouseOverSelection(mouseX, mouseY);

		GuiHelper.drawTitle(minAge.y - GUIOffsetY, width, fontRenderer);

		drawInfo();

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	private void drawMouseOverSelection(int mouseX, int mouseY) {
		final int posY = done.y - 18;

		if (minAge.isMouseOver(mouseX, mouseY) || maxAge.isMouseOver(mouseX, mouseY)) {
			handle.y = minAge.y;
			size = new Vector2D(minAge.width, 39);
			selected = 1;
		} else if (particlesPerAxis.isMouseOver(mouseX, mouseY)) {
			handle.y = particlesPerAxis.y;
			size = new Vector2D(particlesPerAxis.width, 18);
			selected = 2;
		} else if (scaleMult.isMouseOver(mouseX, mouseY)) {
			handle.y = scaleMult.y;
			size = new Vector2D(scaleMult.width, 18);
			selected = 3;
		} else if (gravityMult.isMouseOver(mouseX, mouseY)) {
			handle.y = gravityMult.y;
			size = new Vector2D(gravityMult.width, 18);
			selected = 4;
		} else if (rotationMult.isMouseOver(mouseX, mouseY)) {
			handle.y = rotationMult.y;
			size = new Vector2D(rotationMult.x - (rotationMult.x + rotationMult.width), 18);
			selected = 5;
		} else if (infiniteDuration.isMouseOver())
			selected = 6;
		else if (timeUnit.isMouseOver())
			selected = 7;

		int step = 1;
		time = System.currentTimeMillis();

		if (lastTime > 0)
			step = (int) (time - lastTime);

		lastTime = time;

		if (lastHandle != new Vector2D()) {
			if (lastHandle.y > handle.y) {
				if (lastHandle.y - handle.y <= step)
					lastHandle.y = handle.y;
				else
					lastHandle.y -= step;
			}

			if (lastHandle.y < handle.y) {
				if (handle.y - lastHandle.y <= step)
					lastHandle.y = handle.y;
				else
					lastHandle.y += step;
			}

			lastHandle.x = minAge.x;
		}

		if (lastSize != new Vector2D()) {
			if (lastSize.y > size.y)
				if (lastSize.y - size.y <= step)
					lastSize.y = size.y;
				else
					lastSize.y -= step;

			if (lastSize.y < size.y)
				if (size.y - lastSize.y <= step)
					lastSize.y = size.y;
				else
					lastSize.y += step;

			lastSize.x = gravityMult.width;
		}

		String text;

		switch (selected) {
		case 1:
			if (!FBP.infiniteDuration) {
				String _text = (FBP.minAge != FBP.maxAge ? (I18n.format("menu.particlelife.description.duration.range") + (FBP.showInMillis ? FBP.minAge * 50 : FBP.minAge) + I18n.format("menu.particlelife.description.duration.and") + (FBP.showInMillis ? FBP.maxAge * 50 : FBP.maxAge) + (FBP.showInMillis ? "ms" : " ticks")) : (I18n.format("menu.particlelife.description.duration.to") + (FBP.showInMillis ? FBP.maxAge * 50 : FBP.maxAge) + (FBP.showInMillis ? "ms" : " ticks")));

				text = I18n.format("menu.particlelife.description.duration") + _text + I18n.format("menu.period");
			} else {
				text = I18n.format("menu.particlelife.description.infinity");
			}
			break;
		case 2:
			text = I18n.format("menu.destroyparticles.description") + (int) Math.pow(FBP.particlesPerAxis, 3) + " \u00A7c[\u00A76" + FBP.particlesPerAxis + "^3\u00A7c]"+ I18n.format("menu.period");
			break;
		case 3:
			text = I18n.format("menu.particlescale.description") + FBP.scaleMult +  I18n.format("menu.period");
			break;
		case 4:
			text = I18n.format("menu.particlegravity.description")  + FBP.gravityMult + I18n.format("menu.period");
			break;
		case 5:
			text = I18n.format("menu.particlerotation.description")  + FBP.rotationMult + I18n.format("menu.period");
			break;
		case 6:
			text = (FBP.infiniteDuration ? I18n.format("menu.disable") : I18n.format("menu.enable")) + I18n.format("menu.infiniteduration.description");
			break;
		case 7:
			text = I18n.format("menu.time.description") + (!FBP.showInMillis ? I18n.format("menu.timems.description") : "ticks") + I18n.format("menu.period");
			break;
		default:
			text = I18n.format("menu.noDescriptionFound");
		}

		if (mouseX >= minAge.x - 2 && mouseX <= minAge.x + minAge.width + 2 && mouseY < rotationMult.y + rotationMult.height && mouseY >= minAge.y && (lastSize.y <= 20 || lastSize.y < 50) && lastHandle.y >= minAge.y || infiniteDuration.isMouseOver() || timeUnit.isMouseOver()) {
			if (selected <= 5)
				GuiHelper.drawRect(lastHandle.x - 2, lastHandle.y + 2, lastSize.x + 4, lastSize.y - 2, 200, 200, 200, 35);

			this.drawCenteredString(fontRenderer, text, this.width / 2, posY, fontRenderer.getColorCode('f'));
		}
	}

	private void drawInfo() {

		String s = I18n.format("menu.destroyparticles.info") + " [\u00A76" + (int) Math.pow(FBP.particlesPerAxis, 3) + "\u00A7f]";
		particlesPerAxis.displayString = s;

		if (FBP.infiniteDuration)
			s = I18n.format("menu.minduration.info") + " [\u00A76" + "\u221e" + (FBP.showInMillis ? " ms" : " ticks") + "\u00A7f]";
		else
			s = I18n.format("menu.minduration.info") + " [\u00A76" + (FBP.showInMillis ? ((FBP.minAge * 50) + "ms") : (FBP.minAge + " tick")) + "\u00A7f]";

		minAge.displayString = s;

		if (FBP.infiniteDuration)
			s = I18n.format("menu.maxduration.info") + " [\u00A76" + "\u221e" + (FBP.showInMillis ? " ms" : " ticks") + "\u00A7f]";
		else
			s = I18n.format("menu.maxduration.info") + " [\u00A76" + (FBP.showInMillis ? ((FBP.maxAge * 50) + "ms") : (FBP.maxAge + (FBP.maxAge > 1 ? " ticks" : " tick"))) + "\u00A7f]";

		maxAge.displayString = s;

		scaleMult.displayString = I18n.format("menu.scalemult.info") + " [\u00A76" + FBP.scaleMult + "\u00A7f]";

		gravityMult.displayString = I18n.format("menu.gravityscale.info") + " [\u00A76" + FBP.gravityMult + "\u00A7f]";

		rotationMult.displayString = I18n.format("menu.rotationspeed.info") + " [\u00A76" + (FBP.rotationMult != 0 ? FBP.rotationMult : I18n.format("menu.off")) + "\u00A7f]";
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (mouseButton == 0) {
			for (GuiButton guibutton : this.buttonList) {
				if (guibutton.mousePressed(this.mc, mouseX, mouseY)) {
					if (!guibutton.isMouseOver())
						return;

					this.actionPerformed(guibutton);
				}
			}
		}
	}

	private void update() {
		minAge.enabled = !FBP.infiniteDuration;
		maxAge.enabled = !FBP.infiniteDuration;
	}

	@Override
	public void onGuiClosed() {
		ConfigHandler.writeMainConfig();
	}
}
