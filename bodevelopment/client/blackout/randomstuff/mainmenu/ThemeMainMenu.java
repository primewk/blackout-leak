package bodevelopment.client.blackout.randomstuff.mainmenu;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.gui.menu.MainMenu;
import bodevelopment.client.blackout.module.modules.client.MainMenuSettings;
import bodevelopment.client.blackout.module.modules.client.ThemeSettings;
import bodevelopment.client.blackout.util.render.RenderUtils;
import java.awt.Color;
import java.util.ArrayList;
import net.minecraft.class_4587;

public class ThemeMainMenu implements MainMenuRenderer {
   private static final ArrayList<String> changelog = new ArrayList();
   private int i = 0;
   private float longest = 0.0F;

   public void render(class_4587 stack, float height, float mx, float my, String splashText) {
      this.renderButtons(stack);
      this.renderTitle(stack, splashText);
      this.renderChangelog(stack);
   }

   public void renderBackground(class_4587 stack, float width, float height, float mx, float my) {
      MainMenuSettings mainMenuSettings = MainMenuSettings.getInstance();
      ThemeSettings themeSettings = ThemeSettings.getInstance();
      RenderUtils.fadeRounded(stack, 0.0F, 0.0F, width, height, 0.0F, 0.0F, themeSettings.getMain(), themeSettings.getSecond(), 0.2F, ((Double)mainMenuSettings.speed.get()).floatValue() / 10.0F);
      RenderUtils.loadBlur("title", (Integer)mainMenuSettings.blur.get());
      RenderUtils.drawLoadedBlur("title", stack, (renderer) -> {
         renderer.quadShape(0.0F, 0.0F, width, height, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F);
      });
   }

   public int onClick(float mx, float my) {
      float y = -100.0F;

      for(int i = 0; i < 5; ++i) {
         if (RenderUtils.insideRounded((double)mx, (double)my, -180.0D, (double)y, 360.0D, 10.0D, 25.0D)) {
            return i;
         }

         y += 85.0F;
      }

      return -1;
   }

   private void renderButtons(class_4587 stack) {
      stack.method_22903();
      stack.method_46416(-180.0F, -100.0F, 0.0F);
      String[] var2 = MainMenu.getInstance().buttonNames;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String name = var2[var4];
         this.renderButton(stack, name);
      }

      stack.method_22909();
   }

   private void renderChangelog(class_4587 stack) {
      MainMenuSettings mainMenuSettings = MainMenuSettings.getInstance();
      ThemeSettings themeSettings = ThemeSettings.getInstance();
      float height = BlackOut.BOLD_FONT.getHeight() * 1.0F + (float)changelog.size() * BlackOut.FONT.getHeight() * 2.0F;
      float width = this.longest;
      RenderUtils.drawLoadedBlur("title", stack, (renderer) -> {
         renderer.rounded(4.0F, 4.0F, width, height, 25.0F, 10, 1.0F, 1.0F, 1.0F, 1.0F);
      });
      RenderUtils.tenaRounded(stack, 4.0F, 4.0F, width, height + 8.0F, 25.0F, 10.0F, themeSettings.getMain(150), themeSettings.getSecond(150), ((Double)mainMenuSettings.speed.get()).floatValue());
      RenderUtils.rounded(stack, 10.0F, 12.0F + BlackOut.BOLD_FONT.getHeight() * 3.0F, width - 12.0F, height - BlackOut.BOLD_FONT.getHeight() * 3.0F - 6.0F, 25.0F, 0.0F, (new Color(0, 0, 0, 100)).getRGB(), (new Color(0, 0, 0, 225)).getRGB());
      BlackOut.BOLD_FONT.text(stack, "Changelog", 3.0F, width / 2.0F + 2.0F, -BlackOut.BOLD_FONT.getHeight() * 2.0F - 2.0F, Color.WHITE, true, false);
      this.i = 0;
      changelog.forEach((text) -> {
         if (BlackOut.FONT.getWidth(text) * 2.0F > this.longest) {
            this.longest = BlackOut.FONT.getWidth(text) * 2.0F;
         }

         BlackOut.FONT.text(stack, text, 2.0F, width / 2.0F + 4.0F, BlackOut.BOLD_FONT.getHeight() + 16.0F + BlackOut.FONT.getHeight() * 2.0F * (float)this.i, Color.WHITE, true, false);
         ++this.i;
      });
   }

   public static void initChangelog() {
      changelog.add("Updated to 1.20.4");
      changelog.add("New config system");
      changelog.add("Friend screen (can't do anything there but shows friends)");
      changelog.add("Console screen");
      changelog.add("Fixed some texture rendering issues");
      changelog.add("Faster binding");
      changelog.add("Grim velocity stuff");
      changelog.add("Fixed blocker");
      changelog.add("Fixed surround extend");
      changelog.add("Automine pause eat separated for attacking and mining");
      changelog.add("Better automine compatibility for autocrystal");
      changelog.add("Scrollable global colors");
      changelog.add("More customization for HUD elements");
      changelog.add("SoundESP");
      changelog.add("More customization for rotation speed");
      changelog.add("More customization for attack ranges");
      changelog.add("Rewrote extrapolation");
      changelog.add("Better friends system");
      changelog.add("Better holefill checks");
      changelog.add("Updates to latest blackout when you double click the jar");
      changelog.add("Fixed range extrapolation");
      changelog.add("Fast projectile");
      changelog.add("Velocity blocks fishing rod pull");
   }

   private void renderButton(class_4587 stack, String name) {
      MainMenuSettings mainMenuSettings = MainMenuSettings.getInstance();
      ThemeSettings themeSettings = ThemeSettings.getInstance();
      RenderUtils.drawLoadedBlur("title", stack, (renderer) -> {
         renderer.rounded(0.0F, 0.0F, 360.0F, 10.0F, 25.0F, 10, 1.0F, 1.0F, 1.0F, 1.0F);
      });
      RenderUtils.tenaRounded(stack, 0.0F, 0.0F, 360.0F, 10.0F, 25.0F, 10.0F, themeSettings.getMain(150), themeSettings.getSecond(150), ((Double)mainMenuSettings.speed.get()).floatValue());
      RenderUtils.rounded(stack, 0.0F, 0.0F, 360.0F, 10.0F, 25.0F, 10.0F, (new Color(0, 0, 0, 35)).getRGB(), (new Color(0, 0, 0, 225)).getRGB());
      BlackOut.FONT.text(stack, name, 3.0F, 180.0F, 5.0F, Color.WHITE, true, true);
      stack.method_46416(0.0F, 85.0F, 0.0F);
   }

   private void renderTitle(class_4587 stack, String splashText) {
      BlackOut.BOLD_FONT.text(stack, "BlackOut", 8.5F, 0.0F, -250.0F, Color.WHITE, true, true);
      BlackOut.FONT.text(stack, splashText, 2.5F, 0.0F, -200.0F, Color.WHITE, true, true);
   }

   static {
      initChangelog();
   }
}
