package bodevelopment.client.blackout.randomstuff.mainmenu;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.gui.menu.MainMenu;
import bodevelopment.client.blackout.module.modules.client.MainMenuSettings;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.util.render.RenderUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import net.minecraft.class_2960;
import net.minecraft.class_4587;
import net.minecraft.class_751;
import net.minecraft.class_766;

public class ColorMainMenu implements MainMenuRenderer {
   private final class_751 PANORAMA_CUBE_MAP = new class_751(new class_2960("textures/gui/title/background/panorama"));
   private final class_766 backgroundRenderer;

   public ColorMainMenu() {
      this.backgroundRenderer = new class_766(this.PANORAMA_CUBE_MAP);
   }

   public void render(class_4587 stack, float height, float mx, float my, String splashText) {
      this.renderTitle(stack, splashText);
      this.renderButtons(stack);
   }

   public void renderBackground(class_4587 stack, float width, float height, float mx, float my) {
      MainMenuSettings mainMenuSettings = MainMenuSettings.getInstance();
      BlackOutColor color = (BlackOutColor)mainMenuSettings.shitfuckingmenucolor.get();
      RenderSystem.setShaderColor((float)color.red / 255.0F, (float)color.green / 255.0F, (float)color.blue / 255.0F, (float)color.alpha / 255.0F);
      this.backgroundRenderer.method_3317(BlackOut.mc.method_1488(), 1.0F);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      if ((Integer)mainMenuSettings.blur.get() > 0) {
         RenderUtils.loadBlur("title", (Integer)mainMenuSettings.blur.get());
         RenderUtils.drawLoadedBlur("title", stack, (renderer) -> {
            renderer.quadShape(0.0F, 0.0F, width, height, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F);
         });
      }
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

   private void renderButton(class_4587 stack, String name) {
      RenderUtils.drawLoadedBlur("title", stack, (renderer) -> {
         renderer.rounded(0.0F, 0.0F, 360.0F, 10.0F, 25.0F, 10, 1.0F, 1.0F, 1.0F, 1.0F);
      });
      RenderUtils.rounded(stack, 0.0F, 0.0F, 360.0F, 10.0F, 25.0F, 15.0F, (new Color(255, 255, 255, 0)).getRGB(), (new Color(0, 0, 0, 180)).getRGB());
      BlackOut.FONT.text(stack, name, 3.0F, 180.0F, 5.0F, Color.WHITE, true, true);
      stack.method_46416(0.0F, 85.0F, 0.0F);
   }

   private void renderTitle(class_4587 stack, String splashText) {
      BlackOut.BOLD_FONT.text(stack, "BlackOut", 8.5F, 0.0F, -250.0F, Color.WHITE, true, true);
      BlackOut.FONT.text(stack, splashText, 2.5F, 0.0F, -200.0F, Color.WHITE, true, true);
   }
}
