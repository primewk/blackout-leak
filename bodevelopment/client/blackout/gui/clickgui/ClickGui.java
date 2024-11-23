package bodevelopment.client.blackout.gui.clickgui;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.KeyEvent;
import bodevelopment.client.blackout.event.events.MouseButtonEvent;
import bodevelopment.client.blackout.event.events.MouseScrollEvent;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.gui.clickgui.components.CategoryComponent;
import bodevelopment.client.blackout.gui.clickgui.components.ModuleComponent;
import bodevelopment.client.blackout.helpers.ScrollHelper;
import bodevelopment.client.blackout.helpers.SmoothScrollHelper;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.ParentCategory;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.modules.client.GuiSettings;
import bodevelopment.client.blackout.rendering.framebuffer.GuiAlphaFrameBuffer;
import bodevelopment.client.blackout.rendering.renderer.ColorRenderer;
import bodevelopment.client.blackout.rendering.renderer.TextureRenderer;
import bodevelopment.client.blackout.rendering.texture.BOTextures;
import bodevelopment.client.blackout.util.ColorUtils;
import bodevelopment.client.blackout.util.GuiColorUtils;
import bodevelopment.client.blackout.util.GuiRenderUtils;
import bodevelopment.client.blackout.util.render.AnimUtils;
import bodevelopment.client.blackout.util.render.RenderUtils;
import com.mojang.blaze3d.platform.GlStateManager;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_3532;
import net.minecraft.class_437;
import net.minecraft.class_4587;
import net.minecraft.class_293.class_5596;

public class ClickGui extends class_437 {
   public static float popUpDelta = 0.0F;
   public static float scale = 0.0F;
   public static float unscaled = 0.0F;
   public long toggleTime = 0L;
   private long openTime = 0L;
   public static float x = 0.0F;
   public static float y = 0.0F;
   public static float width = 1000.0F;
   public static float height = 700.0F;
   private boolean moving = false;
   private boolean scaling = false;
   private double offsetX = 0.0D;
   private double offsetY = 0.0D;
   public final List<ModuleComponent> moduleComponents = new ArrayList();
   public final List<CategoryComponent> categoryComponents = new ArrayList();
   public float moduleLength = 0.0F;
   public static SubCategory selectedCategory;
   private double mx = 0.0D;
   private double my = 0.0D;
   private int rawMouseX = 0;
   private int rawMouseY = 0;
   public ClickGuiScreen openedScreen = null;
   private final class_4587 stack = new class_4587();
   private float categoryOffset;
   private boolean open = false;
   private final ScrollHelper categoryScroll = (new SmoothScrollHelper(0.5F, 20.0F, () -> {
      return 0.0F;
   }, () -> {
      return Math.max(this.categoryOffset - height + 110.0F, 0.0F);
   })).limit(5.0F);
   private final ScrollHelper moduleScroll = (new SmoothScrollHelper(0.5F, 20.0F, () -> {
      return 0.0F;
   }, () -> {
      return Math.max(this.moduleLength - height + 40.0F, 0.0F);
   })).limit(5.0F);
   private float frameTime;
   private float scaleDelta;
   private final ClickGuiButtons buttons = new ClickGuiButtons();

   public ClickGui() {
      super(class_2561.method_30163("Click GUI"));
      BlackOut.EVENT_BUS.subscribe(this, () -> {
         return !(BlackOut.mc.field_1755 instanceof ClickGui);
      });
   }

   public void initGui() {
      this.moduleComponents.clear();
      Managers.MODULE.getModules().forEach((m) -> {
         this.moduleComponents.add(new ModuleComponent(this.stack, m));
      });
      this.categoryComponents.clear();
      SubCategory.categories.forEach((c) -> {
         this.categoryComponents.add(new CategoryComponent(this.stack, c));
      });
   }

   public void setOpen(boolean open) {
      if (!this.isOpen() && open) {
         this.openTime = System.currentTimeMillis();
      }

      this.open = open;
   }

   public boolean isOpen() {
      return this.open;
   }

   @Event
   public void onRender(RenderEvent.World.Post event) {
      this.renderBlur();
   }

   public void method_25419() {
      this.onClick(MouseButtonEvent.get(0, false));
      this.setOpen(false);
      super.method_25419();
   }

   public void method_25394(class_332 context, int mouseX, int mouseY, float delta) {
      if (this.open) {
         popUpDelta = (float)class_3532.method_53062(System.currentTimeMillis() - this.toggleTime, 0L, 500L) / 500.0F;
         scale = (float)AnimUtils.easeOutBack((double)popUpDelta);
      } else {
         popUpDelta = 1.0F - (float)class_3532.method_53062(System.currentTimeMillis() - this.toggleTime, 0L, 250L) / 250.0F;
         if (popUpDelta <= 0.0F) {
            this.method_25419();
         }

         scale = (float)AnimUtils.easeOutQuart((double)popUpDelta);
      }

      this.frameTime = delta / 20.0F;
      unscaled = scale;
      scale /= (float)RenderUtils.getScale();
      double startX = ((double)BlackOut.mc.method_22683().method_4480() / 2.0D + (double)x - (double)(width / 2.0F)) / (double)unscaled;
      double startY = ((double)BlackOut.mc.method_22683().method_4507() / 2.0D + (double)y - (double)(height / 2.0F)) / (double)unscaled;
      this.mx = (double)((float)(this.rawMouseX = mouseX) / scale) - startX;
      this.my = (double)((float)(this.rawMouseY = mouseY) / scale) - startY;
      this.updatePos();
      this.updateScroll();
      RenderUtils.startClickGui(this.stack, unscaled, scale, width, height, x, y);
      GlStateManager._disableScissorTest();
      this.renderBlur();
      GuiAlphaFrameBuffer frameBuffer = Managers.FRAME_BUFFER.getGui();
      frameBuffer.start();
      RenderUtils.roundedShadow(this.stack, 0.0F, 0.0F, width, height, 10.0F, 30.0F, (new Color(0, 0, 0, 100)).getRGB());
      RenderUtils.roundedRight(this.stack, 200.0F, 0.0F, width - 200.0F, height, 10.0F, 0.0F, GuiColorUtils.bg1.getRGB(), ColorUtils.SHADOW100I);
      RenderUtils.rightFade(this.stack, 180.0F, -10.0F, 55.0F, height + 20.0F, (new Color(0, 0, 0, 100)).getRGB());
      this.renderLogo();
      this.renderModules();
      this.renderFade();
      RenderUtils.rounded(this.stack, 0.0F, 0.0F, 200.0F, height, 10.0F, 2.0F, GuiColorUtils.bg2.getRGB(), ColorUtils.SHADOW100I);
      GuiRenderUtils.renderWaveText(this.stack, "Blackout", 4.5F, 100.0F, 50.0F, true, true, true);
      if (this.scaling) {
         this.scaleDelta = Math.min(this.scaleDelta + this.frameTime * 5.0F, 1.0F);
      } else {
         this.scaleDelta = Math.max(this.scaleDelta - this.frameTime * 2.0F, 0.0F);
      }

      if (this.scaleDelta > 0.0F) {
         int clr = ColorUtils.withAlpha(Color.WHITE.getRGB(), (int)(this.scaleDelta * 100.0F));
         RenderUtils.rounded(this.stack, width - 10.0F, height - 10.0F, 20.0F, 20.0F, 15.0F, 5.0F, clr, clr);
      }

      this.renderCategories(this.frameTime);
      frameBuffer.end(Math.min(popUpDelta * 1.5F, 1.0F));
      this.buttons.render(mouseX, mouseY, this.openTime, this.open ? 1.0F : popUpDelta);
      if (this.openedScreen != null) {
         this.openedScreen.onRender(this.frameTime, (double)mouseX, (double)mouseY);
      }

      this.stack.method_22909();
   }

   public void setScreen(ClickGuiScreen screen) {
      if (this.openedScreen != null) {
         this.openedScreen.onClose();
      }

      this.openedScreen = screen;
   }

   private void renderFade() {
      ColorRenderer renderer = ColorRenderer.getInstance();
      renderer.startRender(this.stack, class_5596.field_27381);
      renderer.vertex(235.0F, height - 10.0F, 0.0F, (float)GuiColorUtils.bg1.getRed() / 255.0F, (float)GuiColorUtils.bg1.getGreen() / 255.0F, (float)GuiColorUtils.bg1.getBlue() / 255.0F, 0.0F);
      renderer.vertex(235.0F, height + 10.0F, 0.0F, (float)GuiColorUtils.bg1.getRed() / 255.0F, (float)GuiColorUtils.bg1.getGreen() / 255.0F, (float)GuiColorUtils.bg1.getBlue() / 255.0F, (float)GuiColorUtils.bg1.getAlpha() / 255.0F);

      int i;
      float y;
      for(i = 90; i >= 0; i -= 9) {
         y = (float)(Math.sin(Math.toRadians((double)i)) * 10.0D);
         renderer.vertex(width + (float)Math.cos(Math.toRadians((double)i)) * 10.0F, height + y, 0.0F, (float)GuiColorUtils.bg1.getRed() / 255.0F, (float)GuiColorUtils.bg1.getGreen() / 255.0F, (float)GuiColorUtils.bg1.getBlue() / 255.0F, class_3532.method_37960(y, -10.0F, 10.0F));
      }

      renderer.vertex(width + 10.0F, height - 10.0F, 0.0F, (float)GuiColorUtils.bg1.getRed() / 255.0F, (float)GuiColorUtils.bg1.getGreen() / 255.0F, (float)GuiColorUtils.bg1.getBlue() / 255.0F, 0.0F);
      renderer.endRender();
      renderer.startRender(this.stack, class_5596.field_27381);
      renderer.vertex(235.0F, -10.0F, 0.0F, (float)GuiColorUtils.bg1.getRed() / 255.0F, (float)GuiColorUtils.bg1.getGreen() / 255.0F, (float)GuiColorUtils.bg1.getBlue() / 255.0F, (float)GuiColorUtils.bg1.getAlpha() / 255.0F);
      renderer.vertex(235.0F, 10.0F, 0.0F, (float)GuiColorUtils.bg1.getRed() / 255.0F, (float)GuiColorUtils.bg1.getGreen() / 255.0F, (float)GuiColorUtils.bg1.getBlue() / 255.0F, 0.0F);
      renderer.vertex(width + 10.0F, 10.0F, 0.0F, (float)GuiColorUtils.bg1.getRed() / 255.0F, (float)GuiColorUtils.bg1.getGreen() / 255.0F, (float)GuiColorUtils.bg1.getBlue() / 255.0F, 0.0F);

      for(i = 360; i >= 270; i -= 9) {
         y = (float)(Math.sin(Math.toRadians((double)i)) * 10.0D);
         renderer.vertex(width + (float)Math.cos(Math.toRadians((double)i)) * 10.0F, y, 0.0F, (float)GuiColorUtils.bg1.getRed() / 255.0F, (float)GuiColorUtils.bg1.getGreen() / 255.0F, (float)GuiColorUtils.bg1.getBlue() / 255.0F, class_3532.method_37960(y, 10.0F, -10.0F));
      }

      renderer.endRender();
   }

   private void renderLogo() {
      GuiSettings guiSettings = GuiSettings.getInstance();
      float alpha = ((Double)guiSettings.logoAlpha.get()).floatValue();
      if (!(alpha <= 0.0F)) {
         TextureRenderer t = BOTextures.getLogoRenderer();
         float ts = 1200.0F * ((Double)guiSettings.logoScale.get()).floatValue();
         float cx = width - 100.0F;
         float cy = height - 350.0F;
         t.startRender(this.stack, cx - ts / 2.0F, cy - ts / 2.0F, ts, ts, 1.0F, 1.0F, 1.0F, 1.0F, alpha, class_5596.field_27381);

         int i;
         for(i = 90; i >= 0; i -= 9) {
            t.vertex((double)width + Math.cos(Math.toRadians((double)i)) * 10.0D, (double)height + Math.sin(Math.toRadians((double)i)) * 10.0D);
         }

         for(i = 360; i >= 270; i -= 9) {
            t.vertex((double)width + Math.cos(Math.toRadians((double)i)) * 10.0D, Math.sin(Math.toRadians((double)i)) * 10.0D);
         }

         t.vertex(200.0F, -10.0F);
         t.vertex(200.0F, height + 10.0F);
         t.endRender();
      }
   }

   private void renderBlur() {
      int blur = (Integer)GuiSettings.getInstance().blur.get();
      if (blur > 0) {
         RenderUtils.blur(blur, popUpDelta);
      }

   }

   private void updatePos() {
      if (this.moving) {
         x -= (float)(this.offsetX - this.mx);
         y -= (float)(this.offsetY - this.my);
      } else if (this.scaling) {
         width = Math.max((float)(this.mx - this.offsetX), 500.0F);
         height = Math.max((float)(this.my - this.offsetY), 200.0F);
      }

   }

   private void renderModules() {
      int columns = this.getColumns();
      int current = 0;
      float[] lengths = new float[columns];
      int moduleWidth = this.getModuleWidth(columns);
      int offset = this.getColumnOffset(columns, (float)moduleWidth);
      Iterator var6 = this.moduleComponents.iterator();

      while(var6.hasNext()) {
         ModuleComponent component = (ModuleComponent)var6.next();
         if (component.module.category == selectedCategory) {
            lengths[current] += component.onRender(this.frameTime, (float)moduleWidth, 210 + offset * (current + 1) + moduleWidth * current, (int)(30.0F + lengths[current] - this.moduleScroll.get()), this.mx, this.my) + 20.0F;
            ++current;
            if (current >= lengths.length) {
               current = 0;
            }
         }
      }

      float max = 0.0F;
      float[] var12 = lengths;
      int var8 = lengths.length;

      for(int var9 = 0; var9 < var8; ++var9) {
         float f = var12[var9];
         max = Math.max(max, f);
      }

      this.moduleLength = max;
   }

   private int getColumns() {
      return class_3532.method_15340((int)((width - 200.0F) / 320.0F), 1, 4);
   }

   private int getColumnOffset(int columns, float moduleWidth) {
      return (int)((width - 200.0F - (float)columns * moduleWidth) / (float)(columns + 1));
   }

   private int getModuleWidth(int columns) {
      return ((int)width - 200) / columns - 60;
   }

   private void renderCategories(float frameTime) {
      ParentCategory prevParent = null;
      float startY = 110.0F - this.categoryScroll.get();
      GlStateManager._enableScissorTest();
      float sx = (float)BlackOut.mc.method_22683().method_4480() / 2.0F - width / 2.0F * unscaled + x;
      float y1 = (float)BlackOut.mc.method_22683().method_4507() / 2.0F - (height / 2.0F + 10.0F) * unscaled - y;
      float y2 = (float)BlackOut.mc.method_22683().method_4507() / 2.0F + (height / 2.0F - 100.0F) * unscaled - y;
      float scissorHeight = Math.abs(y1 - y2);
      GlStateManager._scissorBox((int)sx, (int)y1, (int)(210.0F * unscaled), (int)scissorHeight);
      this.categoryOffset = 0.0F;

      CategoryComponent c;
      for(Iterator var8 = this.categoryComponents.iterator(); var8.hasNext(); this.categoryOffset += c.onRender(frameTime, 170.0F, 15, (int)(startY + this.categoryOffset), this.mx, this.my)) {
         c = (CategoryComponent)var8.next();
         ParentCategory p = c.category.parent;
         if (prevParent != p) {
            this.categoryOffset += 10.0F;
            this.renderParentCategory(p, startY + this.categoryOffset);
            this.categoryOffset += 2.0F;
         }

         prevParent = p;
      }

      RenderUtils.bottomFade(this.stack, 0.0F, 100.0F, 200.0F, 20.0F, GuiColorUtils.bg2.getRGB());
      RenderUtils.topFade(this.stack, 0.0F, height - 10.0F, 200.0F, 20.0F, GuiColorUtils.bg2.getRGB());
      GlStateManager._disableScissorTest();
   }

   private void renderParentCategory(ParentCategory parent, float yPos) {
      BlackOut.FONT.text(this.stack, parent.name(), 1.5F, 5.0F, yPos, GuiColorUtils.parentCategory, false, true);
      this.categoryOffset += 25.0F;
   }

   private void updateScroll() {
      this.moduleScroll.update(this.frameTime);
      this.categoryScroll.update(this.frameTime);
   }

   @Event
   public void onClick(MouseButtonEvent event) {
      if (this.open) {
         boolean m = false;
         if ((!event.pressed || event.button != 0 || !this.buttons.onClick(this.rawMouseX, this.rawMouseY)) && (this.openedScreen == null || !this.openedScreen.handleMouse(event.button, event.pressed))) {
            if (this.mouseOnScale()) {
               if (event.button == 0 && event.pressed) {
                  this.scaling = true;
                  this.offsetX = this.mx - (double)width;
                  this.offsetY = this.my - (double)height;
               }
            } else if (this.mouseOnCategories()) {
               this.categoryComponents.forEach((c) -> {
                  c.onMouse(event.button, event.pressed);
               });
            } else if (this.mouseOnModules()) {
               this.moduleComponents.forEach((module) -> {
                  if (module.module.category == selectedCategory) {
                     module.onMouse(event.button, event.pressed);
                  }
               });
               m = true;
            } else if (this.mouseOnName() && event.button == 0 && event.pressed) {
               this.moving = true;
               this.offsetX = this.mx;
               this.offsetY = this.my;
            }
         }

         if (!event.pressed) {
            if (event.button == 0) {
               this.moving = false;
               this.scaling = false;
            }

            if (!m) {
               this.moduleComponents.forEach((module) -> {
                  if (module.module.category == selectedCategory) {
                     module.onMouse(event.button, event.pressed);
                  }

               });
            }
         }

      }
   }

   @Event
   public void onKey(KeyEvent event) {
      if (this.openedScreen == null || !this.openedScreen.handleKey(event.key, event.pressed)) {
         this.moduleComponents.forEach((module) -> {
            if (module.module.category == selectedCategory) {
               module.onKey(event.key, event.pressed);
            }
         });
      }
   }

   @Event
   public void onScroll(MouseScrollEvent event) {
      if (this.openedScreen == null || !this.openedScreen.handleScroll(event.horizontal, event.vertical)) {
         if (this.mouseOnCategories()) {
            this.categoryScroll.add(event.vertical);
         } else if (this.mouseOnModules()) {
            this.moduleScroll.add(event.vertical);
         }

      }
   }

   private boolean mouseOnCategories() {
      return this.my > 100.0D && this.my < (double)(height + 10.0F) && this.mx > 0.0D && this.mx < 190.0D;
   }

   private boolean mouseOnModules() {
      return this.my > -10.0D && this.my < (double)(height + 10.0F) && this.mx > 190.0D && this.mx < (double)width;
   }

   private boolean mouseOnName() {
      return this.mx > -10.0D && this.mx < 210.0D && this.my > -10.0D && this.my < 110.0D;
   }

   private boolean mouseOnScale() {
      return RenderUtils.insideRounded(this.mx, this.my, (double)(width - 10.0F), (double)(height - 10.0F), 20.0D, 20.0D, 15.0D);
   }

   static {
      selectedCategory = SubCategory.OFFENSIVE;
   }
}
