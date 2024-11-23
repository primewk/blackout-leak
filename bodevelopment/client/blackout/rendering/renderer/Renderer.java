package bodevelopment.client.blackout.rendering.renderer;

import bodevelopment.client.blackout.interfaces.functional.QuadConsumer;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.function.Consumer;
import net.minecraft.class_287;
import net.minecraft.class_4587;
import org.joml.Matrix4f;

public class Renderer {
   protected class_287 renderBuffer;
   protected Matrix4f renderMatrix;
   private static long prevHUDBlur = 0L;
   private static long prev3DBlur = 0L;
   protected float renderRed;
   protected float renderGreen;
   protected float renderBlue;
   protected float renderAlpha;
   private static float alpha = 1.0F;
   private static class_4587 matrices;
   public static final Matrix4f emptyMatrix = (new class_4587()).method_23760().method_23761();

   public static void onHUDBlur() {
      prevHUDBlur = System.currentTimeMillis();
   }

   public static void on3DBlur() {
      prev3DBlur = System.currentTimeMillis();
   }

   public static boolean shouldLoadHUDBlur() {
      return System.currentTimeMillis() - prevHUDBlur < 5000L;
   }

   public static boolean shouldLoad3DBlur() {
      return System.currentTimeMillis() - prev3DBlur < 5000L;
   }

   public static void setAlpha(float alpha) {
      Renderer.alpha = alpha;
   }

   public static float getAlpha() {
      return alpha;
   }

   public static void setMatrices(class_4587 matrices) {
      Renderer.matrices = matrices;
   }

   public static class_4587 getMatrices() {
      return matrices;
   }

   public static void setTexture(int id, int slot) {
      int prevActive = GlStateManager.activeTexture;
      GlStateManager._activeTexture('蓀' | slot);
      GlStateManager._bindTexture(id);
      GlStateManager._activeTexture('蓀' | prevActive);
   }

   public void multiVertex(float... floats) {
      for(int i = 0; i < floats.length / 2; ++i) {
         this.vertex(floats[i], floats[i + 1]);
      }

   }

   public void vertex(double x, double y) {
      this.vertex((float)x, (float)y);
   }

   public void vertex(float x, float y) {
      this.renderBuffer.method_22918(this.renderMatrix, x, y, 0.0F).method_1344();
   }

   public void vertex(double x, double y, double z) {
      this.vertex((float)x, (float)y, (float)z);
   }

   public void vertex(float x, float y, float z) {
      this.renderBuffer.method_22918(this.renderMatrix, x, y, z).method_1344();
   }

   public void vertex(double x, double y, double z, double r, double g, double b, double a) {
      this.vertex((float)x, (float)y, (float)z, (float)r, (float)g, (float)b, (float)a);
   }

   public void vertex(float x, float y, float z, float r, float g, float b, float a) {
      this.renderBuffer.method_22918(this.renderMatrix, x, y, z).method_22915(r, g, b, a).method_1344();
   }

   public void colorNormalVertex(double x, double y, double z, float r, float g, float b, float a, float nx, float ny, float nz) {
      this.colorNormalVertex((float)x, (float)y, (float)z, r, g, b, a, nx, ny, nz);
   }

   public void colorNormalVertex(float x, float y, float z, float r, float g, float b, float a, float nx, float ny, float nz) {
      this.renderBuffer.method_22918(this.renderMatrix, x, y, z).method_22915(r, g, b, a).method_22914(nx, ny, nz).method_1344();
   }

   public void vertex2D(float x, float y) {
      this.renderBuffer.method_22918(emptyMatrix, x, y, 0.0F).method_1344();
   }

   public void vertex2D(float x, float y, float r, float g, float b, float a) {
      this.renderBuffer.method_22918(emptyMatrix, x, y, 0.0F).method_22915(r, g, b, a).method_1344();
   }

   public void smooth(float x, float y, float rad, int steps, float angle1, float angle2) {
      this.forAngles((angle) -> {
         this.vertex((double)x + Math.cos((double)angle) * (double)rad, (double)y + Math.sin((double)angle) * (double)rad);
      }, angle1, angle2, steps);
   }

   public void smooth(float x, float y, float z, float rad, int steps, float angle1, float angle2) {
      this.forAngles((angle) -> {
         this.vertex((double)x + Math.cos((double)angle) * (double)rad, (double)y + Math.sin((double)angle) * (double)rad, (double)z);
      }, angle1, angle2, steps);
   }

   public void smooth(float x, float y, float z, float r, float g, float b, float a, float rad, int steps, float angle1, float angle2) {
      this.forAngles((angle) -> {
         this.vertex((double)x + Math.cos((double)angle) * (double)rad, (double)y + Math.sin((double)angle) * (double)rad, (double)z, (double)r, (double)g, (double)b, (double)a);
      }, angle1, angle2, steps);
   }

   private void forAngles(Consumer<Float> consumer, float angle1, float angle2, int steps) {
      float minAngle = Math.min(angle1, angle2);
      float maxAngle = Math.max(angle1, angle2);

      for(int i = steps; i >= 0; --i) {
         float angle = minAngle + (maxAngle - minAngle) * ((float)i / (float)steps);
         consumer.accept((float)Math.toRadians((double)angle));
      }

   }

   public void circle(float x, float y, float rad, int steps) {
      this.smooth(x, y, rad, steps, 0.0F, 360.0F);
   }

   public void circle(float x, float y, float z, float rad, int steps) {
      this.smooth(x, y, z, rad, steps, 0.0F, 360.0F);
   }

   public void circle(float x, float y, float z, float r, float g, float b, float a, float rad, int steps) {
      this.smooth(x, y, z, r, g, b, a, rad, steps, 0.0F, 360.0F);
   }

   public void rounded(float x, float y, float width, float height, float rad, int steps) {
      this.rounded((x2, y2, a1, a2) -> {
         this.smooth(x2, y2, rad, steps, a1, a2);
      }, x, y, width, height);
   }

   public void rounded(float x, float y, float width, float height, float rad, int steps, float r, float g, float b, float a) {
      this.rounded((x2, y2, a1, a2) -> {
         this.smooth(x2, y2, 0.0F, r, g, b, a, rad, steps, a1, a2);
      }, x, y, width, height);
   }

   public void rounded(float x, float y, float z, float width, float height, float rad, int steps, float r, float g, float b, float a) {
      this.rounded((x2, y2, a1, a2) -> {
         this.smooth(x2, y2, z, r, g, b, a, rad, steps, a1, a2);
      }, x, y, width, height);
   }

   private void rounded(QuadConsumer<Float, Float, Float, Float> consumer, float x, float y, float width, float height) {
      this.consumerRounded(consumer, x, x + width, y, y + height);
   }

   public void fitRounded(float x, float y, float width, float height, float rad, int steps) {
      this.fitRounded((x2, y2, a1, a2) -> {
         this.smooth(x2, y2, rad, steps, a1, a2);
      }, x, y, width, height, rad);
   }

   public void fitRounded(float x, float y, float width, float height, float rad, int steps, float r, float g, float b, float a) {
      this.fitRounded((x2, y2, a1, a2) -> {
         this.smooth(x2, y2, 0.0F, r, g, b, a, rad, steps, a1, a2);
      }, x, y, width, height, rad);
   }

   public void fitRounded(float x, float y, float z, float width, float height, float rad, int steps, float r, float g, float b, float a) {
      this.fitRounded((x2, y2, a1, a2) -> {
         this.smooth(x2, y2, z, r, g, b, a, rad, steps, a1, a2);
      }, x, y, width, height, rad);
   }

   private void fitRounded(QuadConsumer<Float, Float, Float, Float> consumer, float x, float y, float width, float height, float rad) {
      float x1 = x + rad;
      float x2 = x + width - rad;
      float y1 = y + rad;
      float y2 = y + height - rad;
      this.consumerRounded(consumer, x1, x2, y1, y2);
   }

   private void consumerRounded(QuadConsumer<Float, Float, Float, Float> consumer, float x1, float x2, float y1, float y2) {
      consumer.accept(x2, y2, 0.0F, 90.0F);
      consumer.accept(x2, y1, 270.0F, 360.0F);
      consumer.accept(x1, y1, 180.0F, 270.0F);
      consumer.accept(x1, y2, 90.0F, 180.0F);
   }

   public void quadShape(float x, float y, float w, float h) {
      this.consumerQuad(x, y, w, h, (posX, posY, f1, f2) -> {
         this.vertex(posX, posY);
      });
   }

   public void quadShape(float x, float y, float w, float h, float z) {
      this.consumerQuad(x, y, w, h, (posX, posY, f1, f2) -> {
         this.vertex(posX, posY, z);
      });
   }

   public void quadShape(float x, float y, float w, float h, float z, float r, float g, float b, float a) {
      this.consumerQuad(x, y, w, h, (posX, posY, f1, f2) -> {
         this.vertex(posX, posY, z, r, g, b, a);
      });
   }

   private void consumerQuad(float x, float y, float w, float h, QuadConsumer<Float, Float, Float, Float> consumer) {
      consumer.accept(x, y, 0.0F, 0.0F);
      consumer.accept(x, y + h, 0.0F, 1.0F);
      consumer.accept(x + w, y + h, 1.0F, 1.0F);
      consumer.accept(x + w, y, 1.0F, 0.0F);
   }
}
