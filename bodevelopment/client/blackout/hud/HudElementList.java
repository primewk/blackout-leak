package bodevelopment.client.blackout.hud;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.ConfigType;
import bodevelopment.client.blackout.helpers.ScrollHelper;
import bodevelopment.client.blackout.helpers.SmoothScrollHelper;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.rendering.renderer.Renderer;
import bodevelopment.client.blackout.util.ClassUtils;
import bodevelopment.client.blackout.util.ColorUtils;
import bodevelopment.client.blackout.util.GuiColorUtils;
import bodevelopment.client.blackout.util.render.AnimUtils;
import bodevelopment.client.blackout.util.render.RenderUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.class_3532;
import net.minecraft.class_4587;

public class HudElementList {
   private static final int minWidth = 150;
   private static final int minHeight = 40;
   private static final int elementOffset = 60;
   private static final int elementHeight = 35;
   private static final int elementRadius = 5;
   private float listLength = 0.0F;
   private final ScrollHelper scroll = (new SmoothScrollHelper(0.5F, 20.0F, () -> {
      return 0.0F;
   }, () -> {
      return this.listLength - this.getHeight() + 40.0F + 10.0F;
   })).limit(5.0F);
   private class_4587 stack;
   private final List<HudElementList.HudListEntry> entries = new ArrayList();
   private float mx;
   private float my;
   private float frameTime;
   private float width;
   private float height;
   private boolean open = false;
   private float openProgress = 0.0F;
   private boolean closed = false;
   private float closeProgress = 0.0F;

   public void init() {
      Managers.HUD.getElements().forEach((pair) -> {
         this.entries.add(new HudElementList.HudListEntry((Class)pair.method_15441()));
      });
      this.listLength = (float)(this.entries.size() * 60);
   }

   public void render(class_4587 stack, float frameTime, int mouseX, int mouseY) {
      this.frameTime = frameTime;
      this.mx = (float)(mouseX * RenderUtils.getScale());
      this.my = (float)(mouseY * RenderUtils.getScale());
      this.stack = stack;
      this.scroll.update(frameTime);
      this.updateProgress();
      this.updateScale();
      this.startAlpha();
      this.renderList();
      this.endAlpha();
   }

   public boolean onMouse(int button, boolean pressed) {
      if (button != 0) {
         return false;
      } else if (!pressed) {
         this.closed = false;
         return false;
      } else {
         float middle = (float)BlackOut.mc.method_22683().method_4480() / 2.0F;
         if (!(this.mx < middle - this.width / 2.0F - 10.0F) && !(this.mx > middle + this.width / 2.0F + 10.0F)) {
            float y = (float)BlackOut.mc.method_22683().method_4507() - this.height - 10.0F;
            if (this.my < y) {
               return false;
            } else if (this.my < y + 40.0F + 10.0F) {
               this.open = !this.open;
               return true;
            } else {
               this.onClickList(this.my - y - 40.0F);
               return true;
            }
         } else {
            return false;
         }
      }
   }

   public boolean onScroll(double vertical) {
      if (!this.insideBounds(((float)BlackOut.mc.method_22683().method_4480() - this.width) / 2.0F, (float)BlackOut.mc.method_22683().method_4507() - this.height, this.width, this.height)) {
         return false;
      } else {
         this.scroll.add(vertical);
         return true;
      }
   }

   private void onClickList(float offset) {
      offset += this.scroll.get() - 20.0F;

      for(Iterator var2 = this.entries.iterator(); var2.hasNext(); offset -= 60.0F) {
         HudElementList.HudListEntry entry = (HudElementList.HudListEntry)var2.next();
         if (offset >= 5.0F && offset <= 40.0F) {
            this.clickElement((HudElement)ClassUtils.instance(entry.hudElement));
            return;
         }
      }

   }

   private void clickElement(HudElement element) {
      Managers.HUD.add(element);
      Managers.HUD.HUD_EDITOR.onListClick(element);
      Managers.CONFIG.save(ConfigType.HUD);
      Managers.CONFIG.save(ConfigType.Binds);
      this.closed = true;
   }

   private void startAlpha() {
      Renderer.setAlpha(1.0F - this.closeProgress);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F - this.closeProgress);
   }

   private void endAlpha() {
      Renderer.setAlpha(1.0F);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void updateScale() {
      this.width = this.getWidth();
      this.height = this.getHeight();
   }

   private void renderList() {
      this.stack.method_22903();
      float width = this.getWidth();
      float height = this.getHeight();
      this.stack.method_46416(((float)BlackOut.mc.method_22683().method_4480() - width) / 2.0F, (float)BlackOut.mc.method_22683().method_4507() - height, 0.0F);
      RenderUtils.rounded(this.stack, 0.0F, 0.0F, width, height, 10.0F, 30.0F, GuiColorUtils.bg1.getRGB(), ColorUtils.SHADOW100I);
      RenderUtils.roundedTop(this.stack, 0.0F, 0.0F, width, 40.0F, 10.0F, 0.0F, GuiColorUtils.bg2.getRGB(), ColorUtils.SHADOW100I);
      this.renderListContent();
      RenderUtils.bottomFade(this.stack, -10.0F, 40.0F, width + 20.0F, 10.0F, ColorUtils.SHADOW100I);
      this.stack.method_22909();
   }

   private float clampLerpProgress(float val, float start, float end) {
      return class_3532.method_15363(class_3532.method_37960(val, start, end), 0.0F, 1.0F);
   }

   private void updateProgress() {
      if (this.open) {
         this.openProgress = Math.min(this.openProgress + this.frameTime, 1.0F);
      } else {
         this.openProgress = Math.max(this.openProgress - this.frameTime, 0.0F);
      }

      if (this.closed) {
         this.closeProgress = Math.min(this.closeProgress + this.frameTime * 20.0F, 1.0F);
      } else {
         this.closeProgress = Math.max(this.closeProgress - this.frameTime * 2.0F, 0.0F);
      }

   }

   private void renderListContent() {
      float y = 60.0F - this.scroll.get();
      this.scissor();
      this.stack.method_22903();

      for(Iterator var2 = this.entries.iterator(); var2.hasNext(); y += 60.0F) {
         HudElementList.HudListEntry entry = (HudElementList.HudListEntry)var2.next();
         entry.updateProgress(this.frameTime, this.insideBounds((float)BlackOut.mc.method_22683().method_4480() - this.width / 2.0F, y - 5.0F, this.width, 20.0F));
         RenderUtils.rounded(this.stack, 7.5F, y, this.width - 15.0F, 35.0F, 5.0F, 8.0F, GuiColorUtils.bg2.getRGB(), ColorUtils.SHADOW100I);
         BlackOut.FONT.text(this.stack, entry.hudElement.getSimpleName(), 2.0F, this.width / 2.0F, y + 17.5F, this.getTextColor(entry), true, true);
      }

      this.endScissor();
      this.stack.method_22909();
   }

   private void scissor() {
      RenderUtils.scissor((float)BlackOut.mc.method_22683().method_4480() / 2.0F - this.width / 2.0F - 10.0F, 0.0F, this.width + 20.0F, this.height - 40.0F);
   }

   private void endScissor() {
      RenderUtils.endScissor();
   }

   private Color getTextColor(HudElementList.HudListEntry entry) {
      return ColorUtils.lerpColor((double)entry.progress, Color.GRAY, Color.WHITE);
   }

   private boolean insideBounds(float x, float y, float w, float h) {
      float offsetX = this.mx - x;
      float offsetY = this.my - y;
      return offsetX >= 0.0F && offsetY >= 0.0F && offsetX <= w && offsetY <= h;
   }

   private float getWidth() {
      return 150.0F + (float)AnimUtils.easeInOutCubic((double)this.clampLerpProgress(this.openProgress, 0.0F, 0.5F)) * 200.0F;
   }

   private float getHeight() {
      return class_3532.method_16439((float)AnimUtils.easeInOutCubic((double)this.clampLerpProgress(this.openProgress, 0.5F, 1.0F)), 40.0F, (float)BlackOut.mc.method_22683().method_4507() * 0.5F);
   }

   private static class HudListEntry {
      private final Class<? extends HudElement> hudElement;
      private float progress = 0.0F;

      private HudListEntry(Class<? extends HudElement> hudElement) {
         this.hudElement = hudElement;
      }

      private void updateProgress(float frameTime, boolean close) {
         if (close) {
            this.progress = Math.min(this.progress + frameTime, 1.0F);
         } else {
            this.progress = Math.max(this.progress - frameTime, 0.0F);
         }

      }
   }
}
