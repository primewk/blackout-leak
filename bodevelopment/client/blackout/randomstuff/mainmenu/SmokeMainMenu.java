package bodevelopment.client.blackout.randomstuff.mainmenu;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.gui.menu.MainMenu;
import bodevelopment.client.blackout.module.modules.client.MainMenuSettings;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.randomstuff.ShaderSetup;
import bodevelopment.client.blackout.rendering.renderer.ShaderRenderer;
import bodevelopment.client.blackout.rendering.shader.Shaders;
import bodevelopment.client.blackout.util.render.RenderUtils;
import java.awt.Color;
import net.minecraft.class_290;
import net.minecraft.class_4587;

public class SmokeMainMenu implements MainMenuRenderer {
   private static final long initTime = System.currentTimeMillis();

   public void render(class_4587 stack, float height, float mx, float my, String splashText) {
      this.renderButtons(stack);
      this.renderTitle(stack, splashText);
   }

   public void renderBackground(class_4587 stack, float width, float height, float mx, float my) {
      MainMenuSettings mainMenuSettings = MainMenuSettings.getInstance();
      ShaderRenderer shaderRenderer = ShaderRenderer.getInstance();
      shaderRenderer.quad(stack, 0.0F, 0.0F, width, height, 1.0F, 1.0F, 1.0F, 1.0F, Shaders.smoke, new ShaderSetup((setup) -> {
         setup.time(initTime);
         setup.color("clr1", ((BlackOutColor)mainMenuSettings.color.get()).getRGB());
         setup.color("clr2", ((BlackOutColor)mainMenuSettings.color2.get()).getRGB());
         setup.set("speed", ((Double)mainMenuSettings.speed.get()).floatValue());
      }), class_290.field_1592);
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

   private void renderButton(class_4587 stack, String name) {
      RenderUtils.drawLoadedBlur("title", stack, (renderer) -> {
         renderer.rounded(0.0F, 0.0F, 360.0F, 10.0F, 25.0F, 10, 1.0F, 1.0F, 1.0F, 1.0F);
      });
      RenderUtils.rounded(stack, 0.0F, 0.0F, 360.0F, 10.0F, 25.0F, 10.0F, (new Color(0, 0, 0, 35)).getRGB(), (new Color(0, 0, 0, 225)).getRGB());
      BlackOut.FONT.text(stack, name, 3.0F, 180.0F, 5.0F, Color.WHITE, true, true);
      stack.method_46416(0.0F, 85.0F, 0.0F);
   }

   private void renderTitle(class_4587 stack, String splashText) {
      BlackOut.BOLD_FONT.text(stack, "BlackOut", 8.5F, 0.0F, -250.0F, Color.WHITE, true, true);
      BlackOut.FONT.text(stack, splashText, 2.5F, 0.0F, -200.0F, Color.WHITE, true, true);
   }
}
