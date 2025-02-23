package io.redstudioragnarok.fbp.keys;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Arrays;

import static io.redstudioragnarok.fbp.FBP.mc;

/**
 * A class that holds and initialize all the keybinds for the mod.
 * <p>
 * The reasons it does not use ClientRegistry.registerKeyBinding is that this is around 5 to 7 time faster.
 * It does not matter, I just spend 30 minutes benchmarking and researching what is the fastest way to do this, so I will use it.
 *
 * @author Desoroxxx
 */
public class KeyBindings {

	public static final String categoryName = I18n.format("name");;

	public static final KeyBinding menu = new KeyBinding(I18n.format("keybind.menu"), Keyboard.KEY_P, categoryName);
	public static final KeyBinding toggle = new KeyBinding(I18n.format("keybind.toggle"), Keyboard.KEY_NONE, categoryName);
	public static final KeyBinding freeze = new KeyBinding(I18n.format("keybind.freeze"), Keyboard.KEY_NONE, categoryName);
	public static final KeyBinding killParticles = new KeyBinding(I18n.format("keybind.kill"), Keyboard.KEY_NONE, categoryName);
	public static final KeyBinding blacklistGUI = new KeyBinding(I18n.format("keybind.blacklistGUI"), Keyboard.KEY_B, categoryName);

	public static void init() {
		final ArrayList<KeyBinding> keybinds = new ArrayList<>(Arrays.asList(menu, toggle, freeze, killParticles, blacklistGUI));
		final ArrayList<KeyBinding> allKeyBindings = new ArrayList<>(Arrays.asList(mc.gameSettings.keyBindings));

		allKeyBindings.addAll(keybinds);

		mc.gameSettings.keyBindings = allKeyBindings.toArray(new KeyBinding[0]);
	}
}
