package bodevelopment.client.blackout.rendering.font;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.module.modules.client.GuiSettings;
import bodevelopment.client.blackout.module.modules.misc.Streamer;
import bodevelopment.client.blackout.randomstuff.ShaderSetup;
import bodevelopment.client.blackout.rendering.shader.Shader;
import bodevelopment.client.blackout.rendering.shader.Shaders;
import bodevelopment.client.blackout.util.ColorUtils;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_4587;
import net.minecraft.class_293.class_5596;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL13C;

public class CustomFontRenderer {
   public BOFont selectedFont;
   private final String name;
   private double offset = 0.0D;
   private float scale = 1.0F;
   private final long initTime = System.currentTimeMillis();

   public CustomFontRenderer(String name) {
      this.name = name;
      BlackOut.EVENT_BUS.subscribe(this, () -> {
         return false;
      });
   }

   @Event
   public void onTick(TickEvent.Pre event) {
      GuiSettings settings = GuiSettings.getInstance();
      this.scale = ((Double)settings.fontScale.get()).floatValue();
      this.offset = (double)((1.0F - this.scale) * 40.0F);
   }

   public void loadFont() {
      this.selectedFont = new BOFont(this.name, 64);
   }

   public float getScale() {
      return this.scale;
   }

   public float getWidth(String string) {
      Streamer streamer = Streamer.getInstance();
      if (streamer.enabled) {
         string = streamer.replace(string);
      }

      int sum = 0;

      for(int i = 0; i < string.length(); ++i) {
         sum += ((CharInfo)this.selectedFont.map.getOrDefault(Integer.valueOf(string.charAt(i)), new CharInfo(0, 0, 0, 0))).width;
      }

      return (float)sum / 8.0F * this.scale;
   }

   public float getHeight() {
      return 8.0F;
   }

   public void text(class_4587 stack, String string, float s, float textX, float textY, Color color, boolean xCenter, boolean yCenter) {
      this.textInternal(stack, string, s, textX, textY, color.getRGB(), xCenter, yCenter, Shaders.font, new ShaderSetup());
   }

   public void text(class_4587 stack, String string, float s, float textX, float textY, int color, boolean xCenter, boolean yCenter) {
      this.textInternal(stack, string, s, textX, textY, color, xCenter, yCenter, Shaders.font, new ShaderSetup());
   }

   public void text(class_4587 stack, String string, float s, float textX, float textY, Color color, boolean xCenter, boolean yCenter, Shader shader, ShaderSetup setup) {
      this.textInternal(stack, string, s, textX, textY, color.getRGB(), xCenter, yCenter, shader, setup);
   }

   public void text(class_4587 stack, String string, float s, float textX, float textY, int color, boolean xCenter, boolean yCenter, Shader shader, ShaderSetup setup) {
      this.textInternal(stack, string, s, textX, textY, color, xCenter, yCenter, shader, setup);
   }

   private void textInternal(class_4587 stack, String string, float s, float textX, float textY, int color, boolean xCenter, boolean yCenter, Shader shader, ShaderSetup setup) {
      stack.method_22903();
      float d = 8.0F / this.getScale();
      float ds = s / d;
      stack.method_22905(ds, ds, 1.0F);
      float x = (textX / s - (xCenter ? this.getWidth(string) / 2.0F : 0.0F)) * d;
      float y = (textY / s - (yCenter ? this.getHeight() / 2.0F : 0.0F)) * d;
      this.string(string, stack, x, y, color, shader, setup);
      stack.method_22909();
   }

   public void string(String string, class_4587 stack, float x, float y, int color) {
      this.renderString(string, stack, x, y, color, Shaders.font, new ShaderSetup());
   }

   public void string(String string, class_4587 stack, float x, float y, int color, Shader shader, ShaderSetup setup) {
      this.renderString(string, stack, x, y, color, shader, setup);
   }

   private void renderString(String string, class_4587 stack, float x, float y, int color, Shader shader, ShaderSetup setup) {
      this.innerRenderString(string, stack, x, y, ColorUtils.withAlpha(0, (int)((float)(color >>> 24) * 100.0F / 255.0F)), Shaders.fontshadow, setup);
      this.innerRenderString(string, stack, x, y, color, shader, setup);
   }

   private void innerRenderString(String string, class_4587 stack, float x, float y, int color, Shader shader, ShaderSetup setup) {
      this.renderString(string, stack, x, y, shader, setup.append((s) -> {
         if (shader == Shaders.fontshadow) {
            s.set("alphaMulti", (float)(color >>> 24) / 255.0F);
         }

         s.colorIf("clr", color);
         s.set("uTexture", 0);
         s.setIf("texRes", (float)this.selectedFont.getWidth(), (float)this.selectedFont.getHeight());
         s.timeIf(this.initTime);
      }));
   }

   private void renderString(String string, class_4587 stack, float x, float y, Shader shader, ShaderSetup setup) {
      Streamer streamer = Streamer.getInstance();
      if (streamer.enabled) {
         string = streamer.replace(string);
      }

      Matrix4f matrix4f = stack.method_23760().method_23761();
      y = (float)((double)y - ((double)this.selectedFont.getFontSize() * 0.4D - this.offset));
      RenderSystem.enableBlend();
      GL13C.glActiveTexture(33984);
      GL13C.glBindTexture(3553, this.selectedFont.getId());
      class_287 bufferBuilder = class_289.method_1348().method_1349();
      bufferBuilder.method_1328(class_5596.field_27382, class_290.field_1585);

      for(int i = 0; i < string.length(); ++i) {
         char ch = string.charAt(i);
         x += this.renderChar(bufferBuilder, matrix4f, (CharInfo)this.selectedFont.map.getOrDefault(Integer.valueOf(ch), new CharInfo(0, 0, 0, 0)), x, y);
      }

      shader.render(bufferBuilder, setup);
      GL13C.glBindTexture(3553, GlStateManager.TEXTURES[0].field_5167);
      GL13C.glActiveTexture('蓀' | GlStateManager.activeTexture);
      RenderSystem.disableBlend();
   }

   private float renderChar(class_287 bufferBuilder, Matrix4f matrix4f, CharInfo charInfo, float x, float y) {
      bufferBuilder.method_22918(matrix4f, x + (float)charInfo.width, y, 0.0F).method_22913(charInfo.tx + charInfo.tw, charInfo.ty - charInfo.th).method_1344();
      bufferBuilder.method_22918(matrix4f, x, y, 0.0F).method_22913(charInfo.tx, charInfo.ty - charInfo.th).method_1344();
      bufferBuilder.method_22918(matrix4f, x, y + (float)charInfo.height * 1.5F, 0.0F).method_22913(charInfo.tx, charInfo.ty + charInfo.th * 0.5F).method_1344();
      bufferBuilder.method_22918(matrix4f, x + (float)charInfo.width, y + (float)charInfo.height * 1.5F, 0.0F).method_22913(charInfo.tx + charInfo.tw, charInfo.ty + charInfo.th * 0.5F).method_1344();
      return (float)charInfo.width;
   }
}
