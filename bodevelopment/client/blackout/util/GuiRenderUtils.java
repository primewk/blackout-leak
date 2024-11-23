package bodevelopment.client.blackout.util;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.module.modules.client.GuiSettings;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.randomstuff.ShaderSetup;
import bodevelopment.client.blackout.rendering.shader.Shaders;
import java.awt.Color;
import net.minecraft.class_4587;

public class GuiRenderUtils {
   private static final long initTime = System.currentTimeMillis();

   public static void renderWaveText(class_4587 stack, String text, float textScale, float x, float y, boolean xCenter, boolean yCenter, boolean bold) {
      GuiSettings guiSettings = GuiSettings.getInstance();
      guiSettings.textColor.render(stack, text, textScale, x, y, xCenter, yCenter, bold);
   }

   public static void renderWaveText(class_4587 stack, String text, float textScale, float x, float y, boolean xCenter, boolean yCenter, int clr1, int clr2) {
      BlackOut.FONT.text(stack, text, textScale, x, y, Color.WHITE, xCenter, yCenter, Shaders.fontwave, new ShaderSetup((setup) -> {
         setup.set("frequency", 10.0F);
         setup.set("speed", 2.0F);
         setup.color("clr1", clr1);
         setup.color("clr2", clr2);
         setup.time(initTime);
      }));
   }

   public static Color getGuiColors(double darkness) {
      return getGuiColors((float)darkness);
   }

   public static Color getGuiColors(float darkness) {
      GuiSettings guiSettings = GuiSettings.getInstance();
      if (guiSettings.textColor.isWave()) {
         BlackOutColor bc1 = guiSettings.textColor.getTextColor();
         BlackOutColor bc2 = guiSettings.textColor.getWaveColor();
         Color c1 = new Color((float)bc1.red / 255.0F * darkness, (float)bc1.green / 255.0F * darkness, (float)bc1.blue / 255.0F * darkness, (float)bc1.alpha / 255.0F);
         Color c2 = new Color((float)bc2.red / 255.0F * darkness, (float)bc2.green / 255.0F * darkness, (float)bc2.blue / 255.0F * darkness, (float)bc2.alpha / 255.0F);
         return ColorUtils.getWave(c1, c2, 2.0D, 1.0D, 1);
      } else {
         return guiSettings.textColor.isRainbow() ? new Color(ColorUtils.getRainbow(10.0F, guiSettings.textColor.saturation(), 1.0F)) : guiSettings.textColor.getTextColor().getColor();
      }
   }

   public static int withBrightness(int color, double brightness) {
      return (color >> 24 & 255 & 255) << 24 | ((int)Math.round((double)(color >> 16 & 255) * brightness) & 255) << 16 | (int)Math.round((double)(color >> 8 & 255) * brightness) << 8 | (int)Math.round((double)(color & 255) * brightness) & 255;
   }
}
