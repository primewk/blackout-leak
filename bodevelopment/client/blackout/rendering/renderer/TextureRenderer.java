package bodevelopment.client.blackout.rendering.renderer;

import bodevelopment.client.blackout.randomstuff.ShaderSetup;
import bodevelopment.client.blackout.rendering.shader.Shader;
import bodevelopment.client.blackout.rendering.shader.Shaders;
import bodevelopment.client.blackout.rendering.texture.BOTextures;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_4587;
import net.minecraft.class_293.class_5596;

public class TextureRenderer extends Renderer {
   private static final TextureRenderer INSTANCE = new TextureRenderer("rur");
   private class_4587 renderMatrix = new class_4587();
   private int id;
   private int width = 0;
   private int height = 0;
   private final String name;
   private float renderX;
   private float renderY;
   private float renderW;
   private float renderH;
   private float renderBlur;
   private float renderRed;
   private float renderGreen;
   private float renderBlue;
   private float renderAlpha;
   private float u1;
   private float v1;
   private float u2;
   private float v2;

   public static TextureRenderer getInstance() {
      return INSTANCE;
   }

   public static void renderRounded(class_4587 stack, float x, float y, float width, float height, float u1, float v1, float u2, float v2, float rad, int steps, BOTextures.Texture texture) {
      INSTANCE.setTexture(texture.getId());
      INSTANCE.startRender(stack, x - rad, y - rad, width + rad * 2.0F, height + rad * 2.0F, u1, v1, u2, v2, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, class_5596.field_27381);
      INSTANCE.rounded(x, y, width, height, rad, steps);
      INSTANCE.endRender();
   }

   public static void renderFitRounded(class_4587 stack, float x, float y, float width, float height, float u1, float v1, float u2, float v2, float rad, int steps, BOTextures.Texture texture) {
      renderFitRounded(stack, x, y, width, height, u1, v1, u2, v2, rad, steps, texture.getId());
   }

   public static void renderFitRounded(class_4587 stack, float x, float y, float width, float height, float u1, float v1, float u2, float v2, float rad, int steps, int id) {
      INSTANCE.setTexture(id);
      INSTANCE.startRender(stack, x, y, width, height, u1, v1, u2, v2, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, class_5596.field_27381);
      INSTANCE.fitRounded(x, y, width, height, rad, steps);
      INSTANCE.endRender();
   }

   public static void renderCircle(class_4587 stack, float x, float y, float u1, float v1, float u2, float v2, float rad, int steps, BOTextures.Texture texture) {
      renderCircle(stack, x, y, u1, v1, u2, v2, rad, steps, texture.getId());
   }

   public static void renderCircle(class_4587 stack, float x, float y, float u1, float v1, float u2, float v2, float rad, int steps, int id) {
      INSTANCE.setTexture(id);
      INSTANCE.startRender(stack, x - rad, y - rad, rad * 2.0F, rad * 2.0F, u1, v1, u2, v2, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, class_5596.field_27381);
      INSTANCE.circle(x, y, rad, steps);
      INSTANCE.endRender();
   }

   public static void renderQuad(class_4587 stack, float x, float y, float width, float height, float u1, float v1, float u2, float v2, int id) {
      INSTANCE.setTexture(id);
      INSTANCE.quadUV(stack, x, y, width, height, u1, v1, u2, v2, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F);
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public String getName() {
      return this.name;
   }

   public TextureRenderer(String name) {
      this.name = name;
   }

   public void load(BOTextures.UploadData data) {
      this.id = data.id();
      this.width = data.width();
      this.height = data.height();
   }

   public void quad(class_4587 stack, float x, float y, float w, float h) {
      this.quad(stack, x, y, w, h, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F);
   }

   public void quad(class_4587 stack, float x, float y, float w, float h, int color) {
      this.quad(stack, x, y, w, h, 0.0F, color);
   }

   public void quad(class_4587 stack, float x, float y, float w, float h, float blur, int color) {
      this.quad(stack, x, y, w, h, blur, (float)(color >> 16 & 255) / 255.0F, (float)(color >> 8 & 255) / 255.0F, (float)(color & 255) / 255.0F, (float)(color >>> 24) / 255.0F);
   }

   public void quad(class_4587 stack, float x, float y, float w, float h, float blur, float r, float g, float b, float a) {
      this.quadUV(stack, x, y, w, h, 0.0F, 0.0F, 1.0F, 1.0F, blur, r, g, b, a);
   }

   public void quadUV(class_4587 stack, float x, float y, float w, float h, float u1, float v1, float u2, float v2) {
      this.quadUV(stack, x, y, w, h, u1, v1, u2, v2, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F);
   }

   public void quadUV(class_4587 stack, float x, float y, float w, float h, float u1, float v1, float u2, float v2, int color) {
      this.quadUV(stack, x, y, w, h, u1, v1, u2, v2, 0.0F, color);
   }

   public void quadUV(class_4587 stack, float x, float y, float w, float h, float u1, float v1, float u2, float v2, float blur, int color) {
      this.quadUV(stack, x, y, w, h, u1, v1, u2, v2, blur, (float)(color >> 16 & 255) / 255.0F, (float)(color >> 8 & 255) / 255.0F, (float)(color & 255) / 255.0F, (float)(color >>> 24) / 255.0F);
   }

   public void quadUV(class_4587 stack, float x, float y, float w, float h, float u1, float v1, float u2, float v2, float blur, float r, float g, float b, float a) {
      this.startRender(stack, x, y, w, h, u1, v1, u2, v2, blur, r, g, b, a, class_5596.field_27382);
      this.vertex(x, y);
      this.vertex(x, y + h);
      this.vertex(x + w, y + h);
      this.vertex(x + w, y);
      this.endRender();
   }

   public void quadUV(class_4587 stack, float x, float y, float w, float h, float u1, float v1, float u2, float v2, float blur, float r, float g, float b, float a, Shader shader, ShaderSetup setup) {
      this.startRender(stack, x, y, w, h, u1, v1, u2, v2, blur, r, g, b, a, class_5596.field_27382);
      this.vertex(x, y);
      this.vertex(x, y + h);
      this.vertex(x + w, y + h);
      this.vertex(x + w, y);
      this.endRender(shader, setup);
   }

   public void setTexture(int id) {
      this.id = id;
   }

   public void verticalFlip() {
      float v = this.v1;
      this.v1 = this.v2;
      this.v2 = v;
   }

   public void horizontalFlip() {
      float u = this.u1;
      this.u1 = this.u2;
      this.u2 = u;
   }

   public void startRender(class_4587 stack, float x, float y, float w, float h, float blur, float r, float g, float b, float a, class_5596 drawMode) {
      this.startRender(stack, x, y, w, h, 0.0F, 0.0F, 1.0F, 1.0F, blur, r, g, b, a, drawMode);
   }

   public void startRender(class_4587 stack, float x, float y, float w, float h, float u1, float v1, float u2, float v2, float blur, float r, float g, float b, float a, class_5596 drawMode) {
      this.renderMatrix = stack;
      this.renderX = x;
      this.renderY = y;
      this.renderW = w;
      this.renderH = h;
      this.renderBlur = blur;
      this.renderRed = r;
      this.renderGreen = g;
      this.renderBlue = b;
      this.renderAlpha = a;
      this.u1 = u1;
      this.u2 = u2;
      this.v1 = v1;
      this.v2 = v2;
      RenderSystem.enableBlend();
      setTexture(this.id, 0);
      (this.renderBuffer = class_289.method_1348().method_1349()).method_1328(drawMode, class_290.field_1592);
   }

   public void vertex(float x, float y) {
      this.renderBuffer.method_22912((double)x, (double)y, 0.0D).method_1344();
   }

   public void vertex(float x, float y, float z) {
      this.renderBuffer.method_22912((double)x, (double)y, (double)z).method_1344();
   }

   public void vertex(float x, float y, float z, float r, float g, float b, float a) {
      this.renderBuffer.method_22912((double)x, (double)y, (double)z).method_22915(r, g, b, a).method_1344();
   }

   public void endRender() {
      Renderer.setMatrices(this.renderMatrix);
      this.endRender(this.renderBlur > 0.0F ? Shaders.blurUV : Shaders.textureUV, new ShaderSetup((setup) -> {
         if (this.renderBlur > 0.0F) {
            setup.set("blur", this.renderBlur);
         }

         setup.set("pos", this.renderX, this.renderY, this.renderX + this.renderW, this.renderY + this.renderH);
         setup.set("uv", this.u1, this.v1, this.u2, this.v2);
      }));
   }

   public void endRender(Shader shader, ShaderSetup setup) {
      shader.set("clr", this.renderRed, this.renderGreen, this.renderBlue, this.renderAlpha);
      shader.set("uTexture", 0);
      shader.render(this.renderBuffer, setup);
      RenderSystem.disableBlend();
   }
}
