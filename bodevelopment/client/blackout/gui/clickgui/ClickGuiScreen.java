package bodevelopment.client.blackout.gui.clickgui;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.helpers.ScrollHelper;
import bodevelopment.client.blackout.helpers.SmoothScrollHelper;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.rendering.framebuffer.GuiAlphaFrameBuffer;
import bodevelopment.client.blackout.rendering.renderer.Renderer;
import bodevelopment.client.blackout.util.ColorUtils;
import bodevelopment.client.blackout.util.GuiColorUtils;
import bodevelopment.client.blackout.util.render.AnimUtils;
import bodevelopment.client.blackout.util.render.RenderUtils;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import net.minecraft.class_3532;
import net.minecraft.class_4587;

public class ClickGuiScreen {
   protected int x = 0;
   protected int y = 0;
   protected double mx = 0.0D;
   protected double my = 0.0D;
   protected float frameTime = 0.0F;
   protected final class_4587 stack = new class_4587();
   protected float scale = 0.0F;
   protected float unscaled = 0.0F;
   private final long openTime = System.currentTimeMillis();
   private boolean moving = false;
   private double offsetX = 0.0D;
   private double offsetY = 0.0D;
   private final String label;
   protected final float width;
   protected final float height;
   private float length;
   protected final ScrollHelper scroll;
   protected float alpha = 0.0F;

   public ClickGuiScreen(String label, float width, float height, boolean smooth) {
      this.width = width;
      this.height = height + 40.0F;
      this.scroll = smooth ? (new SmoothScrollHelper(0.5F, 20.0F, () -> {
         return 0.0F;
      }, () -> {
         return Math.max(this.length - height, 0.0F);
      })).limit(5.0F) : (new ScrollHelper(0.5F, 20.0F, () -> {
         return 0.0F;
      }, () -> {
         return Math.max(this.length - height, 0.0F);
      })).limit(5.0F);
      this.label = label;
   }

   public void onRender(float frameTime, double mouseX, double mouseY) {
      GuiAlphaFrameBuffer frameBuffer = Managers.FRAME_BUFFER.getGui();
      frameBuffer.start();
      float popUpDelta = (float)class_3532.method_53062(System.currentTimeMillis() - this.openTime, 0L, 500L) / 500.0F;
      popUpDelta = (float)AnimUtils.easeOutBack((double)popUpDelta);
      this.scale = popUpDelta;
      this.unscaled = this.scale;
      this.scale /= (float)RenderUtils.getScale();
      RenderUtils.startClickGui(this.stack, this.unscaled, this.scale, this.width, this.height, (float)this.x, (float)this.y);
      this.frameTime = frameTime;
      double startX = ((double)BlackOut.mc.method_22683().method_4480() / 2.0D + (double)this.x - (double)(this.width / 2.0F)) / (double)this.unscaled;
      double startY = ((double)BlackOut.mc.method_22683().method_4507() / 2.0D + (double)this.y - (double)(this.height / 2.0F)) / (double)this.unscaled;
      this.mx = mouseX / (double)this.scale - startX;
      this.my = mouseY / (double)this.scale - startY;
      this.length = this.getLength();
      this.updatePos();
      this.scroll.update(frameTime);
      this.rounded(0.0F, -40.0F, this.width, this.height, 10.0F, 20.0F, GuiColorUtils.bg2, ColorUtils.SHADOW100);
      this.text(this.label, 2.5F, this.width / 2.0F, -25.0F, true, true, Color.GRAY);
      GlStateManager._enableScissorTest();
      float sx = (float)BlackOut.mc.method_22683().method_4480() / 2.0F - (this.width / 2.0F + 10.0F) * this.unscaled + (float)this.x;
      float y1 = (float)BlackOut.mc.method_22683().method_4507() / 2.0F - (this.height / 2.0F - 30.0F) * this.unscaled - (float)this.y;
      float y2 = (float)BlackOut.mc.method_22683().method_4507() / 2.0F + this.height / 2.0F * this.unscaled - (float)this.y;
      float scissorHeight = Math.abs(y1 - y2);
      GlStateManager._scissorBox((int)sx, (int)y1, (int)((this.width + 20.0F) * this.unscaled), (int)scissorHeight);
      this.render();
      GlStateManager._disableScissorTest();
      RenderUtils.bottomFade(this.stack, -10.0F, 0.0F, this.width + 20.0F, 20.0F, (new Color(0, 0, 0, 100)).getRGB());
      this.stack.method_22909();
      frameBuffer.end(this.getAlpha());
   }

   private float getAlpha() {
      if (BlackOut.mc.field_1755 instanceof ClickGui) {
         return Managers.CLICK_GUI.CLICK_GUI.isOpen() ? (float)class_3532.method_53062(System.currentTimeMillis() - Managers.CLICK_GUI.CLICK_GUI.toggleTime, 0L, 200L) / 200.0F : 1.0F - (float)class_3532.method_53062(System.currentTimeMillis() - Managers.CLICK_GUI.CLICK_GUI.toggleTime, 0L, 200L) / 200.0F;
      } else {
         return 1.0F;
      }
   }

   private void endAlpha() {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      Renderer.setAlpha(1.0F);
   }

   protected float getLength() {
      return 0.0F;
   }

   private void updatePos() {
      if (this.moving) {
         this.x = (int)((float)this.x - (float)(this.offsetX - this.mx));
         this.y = (int)((float)this.y - (float)(this.offsetY - this.my));
      }
   }

   public void render() {
   }

   public void onMouse(int button, boolean state) {
   }

   public void onKey(int key, boolean state) {
   }

   public void onClose() {
   }

   public boolean handleMouse(int button, boolean state) {
      boolean m = false;
      if (!state) {
         this.onMouse(button, false);
         m = true;
         this.moving = false;
      }

      if (!this.insideBounds()) {
         return false;
      } else {
         if (this.my < 0.0D && state) {
            if (button == 0) {
               this.moving = true;
               this.offsetX = this.mx;
               this.offsetY = this.my;
            } else if (button == 1) {
               Managers.CLICK_GUI.openScreen((ClickGuiScreen)null);
            }
         }

         if (!m) {
            this.onMouse(button, true);
         }

         return true;
      }
   }

   public boolean handleScroll(double horizontal, double vertical) {
      if (!this.insideBounds()) {
         return false;
      } else {
         if (this.insideScrollBounds()) {
            this.scroll.add(vertical);
         }

         return true;
      }
   }

   public boolean handleKey(int key, boolean state) {
      if (!this.insideBounds()) {
         return false;
      } else {
         if (this.insideKeyBounds()) {
            this.onKey(key, state);
         }

         return true;
      }
   }

   protected boolean insideBounds() {
      return this.mx > -10.0D && this.mx < (double)(this.width + 10.0F) && this.my > -50.0D && this.my < (double)(this.height - 30.0F);
   }

   protected boolean insideScrollBounds() {
      return this.insideBounds();
   }

   protected boolean insideKeyBounds() {
      return this.insideBounds();
   }

   public void rounded(float x, float y, float width, float height, float radius, float shadowRad, Color color, Color shadowColor) {
      RenderUtils.rounded(this.stack, x, y, width, height, radius, shadowRad, color.getRGB(), shadowColor.getRGB());
   }

   public void text(String string, float scale, float x, float y, boolean xCenter, boolean yCenter, Color color) {
      BlackOut.FONT.text(this.stack, string, scale, x, y, color, xCenter, yCenter);
   }

   public void line(float x1, float y1, float x2, float y2, Color color) {
      RenderUtils.line(this.stack, x1, y1, x2, y2, color.getRGB());
   }

   public void quad(float x, float y, float w, float h, Color color) {
      RenderUtils.quad(this.stack, x, y, w, h, color.getRGB());
   }

   public void fadeLine(float x1, float y1, float x2, float y2, Color color) {
      RenderUtils.fadeLine(this.stack, x1, y1, x2, y2, color.getRGB());
   }
}
