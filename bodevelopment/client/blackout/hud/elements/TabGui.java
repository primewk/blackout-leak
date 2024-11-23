package bodevelopment.client.blackout.hud.elements;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.KeyEvent;
import bodevelopment.client.blackout.hud.HudElement;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Category;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.ParentCategory;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.module.setting.multisettings.BackgroundMultiSetting;
import bodevelopment.client.blackout.module.setting.multisettings.TextColorMultiSetting;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.rendering.renderer.Renderer;
import bodevelopment.client.blackout.util.ColorUtils;
import bodevelopment.client.blackout.util.render.RenderUtils;
import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.class_3532;
import org.apache.commons.lang3.mutable.MutableDouble;

public class TabGui extends HudElement {
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final BackgroundMultiSetting background;
   public final Setting<TabGui.ColorMode> selectorMode;
   private final Setting<Integer> bloomIntensity;
   private final Setting<BlackOutColor> selectorColor;
   private final Setting<Double> waveSpeed;
   private final Setting<BlackOutColor> waveColor;
   private final Setting<Double> saturation;
   private final TextColorMultiSetting textColor;
   public final Setting<BlackOutColor> textDisabled;
   private int selectedModule;
   private int selectedParentId;
   private int selectedChildId;
   private ParentCategory selectedParent;
   private SubCategory selectedChild;
   private int i;
   private float progress;
   private int opened;
   private final Map<Module, MutableDouble> moduleMap;

   public TabGui() {
      super("Tab GUI", ".");
      this.background = BackgroundMultiSetting.of(this.sgGeneral, (String)null);
      this.selectorMode = this.sgGeneral.e("Selector Mode", TabGui.ColorMode.Custom, ".");
      this.bloomIntensity = this.sgGeneral.i("Selector Bloom Intensity", 1, 0, 2, 1, ".");
      this.selectorColor = this.sgGeneral.c("Selector Color", new BlackOutColor(125, 125, 125, 255), "Base color for the selector", () -> {
         return this.selectorMode.get() != TabGui.ColorMode.Rainbow;
      });
      this.waveSpeed = this.sgGeneral.d("Wave Speed", 2.0D, 0.0D, 10.0D, 0.1D, "Speed for the wave effect", () -> {
         return this.selectorMode.get() == TabGui.ColorMode.Wave;
      });
      this.waveColor = this.sgGeneral.c("Wave Color", new BlackOutColor(125, 125, 125, 255), "Color For The Wave", () -> {
         return this.selectorMode.get() == TabGui.ColorMode.Wave;
      });
      this.saturation = this.sgGeneral.d("Rainbow Saturation", 0.8D, 0.0D, 1.0D, 0.1D, ".", () -> {
         return this.selectorMode.get() == TabGui.ColorMode.Rainbow;
      });
      this.textColor = TextColorMultiSetting.of(this.sgGeneral, "Enabled Text");
      this.textDisabled = this.sgGeneral.c("Disabled Text", new BlackOutColor(150, 150, 150, 255), ".");
      this.selectedModule = 0;
      this.selectedParentId = 0;
      this.selectedChildId = 0;
      this.selectedParent = null;
      this.selectedChild = null;
      this.i = 0;
      this.progress = 0.0F;
      this.opened = 0;
      this.moduleMap = new HashMap();
      Iterator var1 = Managers.MODULE.getModules().iterator();

      while(var1.hasNext()) {
         Module module = (Module)var1.next();
         this.moduleMap.put(module, new MutableDouble(module.enabled ? 1.0D : 0.0D));
      }

      this.setSize(75.0F, (BlackOut.FONT.getHeight() + 10.0F) * (float)ParentCategory.categories.size());
      BlackOut.EVENT_BUS.subscribe(this, () -> {
         return false;
      });
   }

   public void render() {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         this.stack.method_22903();
         this.selectedParent = (ParentCategory)ParentCategory.categories.get(this.selectedParentId);
         this.selectedChild = this.getChild(this.selectedChildId);
         if (this.opened > -1) {
            this.renderParents(0.0F);
         }

         if (this.opened > 0) {
            this.renderChildren(90.0F);
         }

         if (this.opened > 1) {
            this.renderModules(180.0F, (double)(this.frameTime * 2.0F));
         }

         this.stack.method_22909();
      }
   }

   private void renderParents(float x) {
      this.progress = 0.0F;
      this.i = 0;
      this.renderBG((float)ParentCategory.categories.size(), x);
      ParentCategory.categories.forEach((cat) -> {
         this.renderCategory(cat, x, this.selectedParentId);
         ++this.i;
         this.progress += BlackOut.FONT.getHeight() + 10.0F;
      });
   }

   private void renderChildren(float x) {
      this.progress = 0.0F;
      this.i = 0;
      Iterator var2 = SubCategory.categories.iterator();

      while(var2.hasNext()) {
         SubCategory cat = (SubCategory)var2.next();
         if (cat.parent == this.selectedParent) {
            ++this.i;
         }
      }

      this.renderBG((float)this.i, x);
      this.i = 0;
      SubCategory.categories.forEach((catx) -> {
         if (catx.parent == this.selectedParent) {
            this.renderCategory(catx, x, this.selectedChildId);
            ++this.i;
            this.progress += BlackOut.FONT.getHeight() + 10.0F;
         }
      });
   }

   private void renderModules(float x, double frameTime) {
      this.progress = 0.0F;
      this.i = 0;
      Iterator var4 = this.moduleMap.entrySet().iterator();

      while(var4.hasNext()) {
         Entry<Module, MutableDouble> entry = (Entry)var4.next();
         if (((Module)entry.getKey()).category == this.selectedChild) {
            ++this.i;
         }
      }

      this.renderBG((float)this.i, x);
      this.i = 0;
      this.moduleMap.forEach((module, d) -> {
         if (module.category == this.selectedChild) {
            this.renderModule(module, d.getValue(), x);
            d.setValue(class_3532.method_15350(d.getValue() + (module.enabled ? frameTime : -frameTime), 0.0D, 1.0D));
            ++this.i;
            this.progress += BlackOut.FONT.getHeight() + 10.0F;
         }
      });
   }

   private void renderCategory(Category category, float x, int sel) {
      float y = this.progress + (BlackOut.FONT.getHeight() + 10.0F) / 2.0F;
      if (sel == this.i) {
         this.renderSelector(x + 4.0F, y);
      }

      BlackOut.FONT.text(this.stack, category.name(), 1.0F, x + 9.0F, y, this.textColor.getTextColor().getColor(), false, true);
   }

   private void renderModule(Module module, double delta, float x) {
      float y = this.progress + (BlackOut.FONT.getHeight() + 10.0F) / 2.0F;
      if (this.selectedModule == this.i) {
         this.renderSelector(x + 4.0F, y);
      }

      float test = (float)(2.0D * delta);
      float prevAlpha = Renderer.getAlpha();
      Renderer.setAlpha((float)delta);
      this.textColor.render(this.stack, module.getDisplayName(), 1.0F, x + 9.0F + test, y, false, true);
      Renderer.setAlpha(prevAlpha);
      Renderer.setAlpha((float)(1.0D - delta));
      BlackOut.FONT.text(this.stack, module.getDisplayName(), 1.0F, x + 9.0F + test, y, ((BlackOutColor)this.textDisabled.get()).getColor(), false, true);
      Renderer.setAlpha(prevAlpha);
   }

   private void renderSelector(float x, float y) {
      Color color = Color.WHITE;
      switch((TabGui.ColorMode)this.selectorMode.get()) {
      case Custom:
         color = ((BlackOutColor)this.selectorColor.get()).getColor();
         break;
      case Rainbow:
         color = new Color(ColorUtils.getRainbow(4.0F, ((Double)this.saturation.get()).floatValue(), 1.0F, 150L));
         break;
      case Wave:
         color = ColorUtils.getWave(((BlackOutColor)this.selectorColor.get()).getColor(), ((BlackOutColor)this.waveColor.get()).getColor(), (Double)this.waveSpeed.get(), 1.0D, 1);
      }

      RenderUtils.rounded(this.stack, x, y - BlackOut.FONT.getHeight() / 2.0F - 1.5F, 0.3F, BlackOut.FONT.getHeight() + 2.0F, 1.0F, (float)(Integer)this.bloomIntensity.get(), color.getRGB(), color.getRGB());
   }

   private void renderBG(float height, float x) {
      float length = (BlackOut.FONT.getHeight() + 10.0F) * height;
      RenderUtils.drawLoadedBlur("hudblur", this.stack, (renderer) -> {
         renderer.rounded(x + 1.0F, 0.0F, 75.0F, length, 3.0F, 10);
      });
      Renderer.onHUDBlur();
      this.background.render(this.stack, x + 1.0F, 0.0F, 75.0F, length, 3.0F, 3.0F);
   }

   @Event
   public void onKey(KeyEvent event) {
      if (event.pressed) {
         switch(event.key) {
         case 262:
            if (this.opened == 2) {
               Module module = this.getModule(this.selectedModule);
               if (module != null) {
                  module.toggle();
               }
            }

            this.opened = Math.min(this.opened + 1, 2);
            break;
         case 263:
            switch(this.opened) {
            case 0:
               this.selectedParentId = 0;
               break;
            case 1:
               this.selectedChildId = 0;
               break;
            case 2:
               this.selectedModule = 0;
            }

            this.opened = Math.max(this.opened - 1, -1);
            break;
         case 264:
            switch(this.opened) {
            case 0:
               ++this.selectedParentId;
               if (this.selectedParentId >= ParentCategory.categories.size()) {
                  this.selectedParentId = 0;
               }

               return;
            case 1:
               ++this.selectedChildId;
               if (this.selectedChildId >= this.getChildAmount()) {
                  this.selectedChildId = 0;
               }

               return;
            case 2:
               ++this.selectedModule;
               if (this.selectedModule >= this.getModuleAmount()) {
                  this.selectedModule = 0;
               }

               return;
            default:
               return;
            }
         case 265:
            switch(this.opened) {
            case 0:
               --this.selectedParentId;
               if (this.selectedParentId < 0) {
                  this.selectedParentId = ParentCategory.categories.size() - 1;
               }
               break;
            case 1:
               --this.selectedChildId;
               if (this.selectedChildId < 0) {
                  this.selectedChildId = this.getChildAmount() - 1;
               }
               break;
            case 2:
               --this.selectedModule;
               if (this.selectedModule < 0) {
                  this.selectedModule = this.getModuleAmount() - 1;
               }
            }
         }

      }
   }

   private int getChildAmount() {
      this.i = 0;
      SubCategory.categories.forEach((cat) -> {
         if (cat.parent == this.selectedParent) {
            ++this.i;
         }

      });
      return this.i;
   }

   private int getModuleAmount() {
      this.i = 0;
      this.moduleMap.forEach((module, d) -> {
         if (module.category == this.selectedChild) {
            ++this.i;
         }

      });
      return this.i;
   }

   private Module getModule(int index) {
      this.i = 0;
      Iterator var2 = this.moduleMap.entrySet().iterator();

      Module module;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         Entry<Module, MutableDouble> entry = (Entry)var2.next();
         module = (Module)entry.getKey();
      } while(module.category != this.selectedChild || this.i++ != index);

      return module;
   }

   private SubCategory getChild(int index) {
      this.i = 0;
      Iterator var2 = SubCategory.categories.iterator();

      SubCategory cat;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         cat = (SubCategory)var2.next();
      } while(cat.parent != this.selectedParent || this.i++ != index);

      return cat;
   }

   public static enum ColorMode {
      Rainbow,
      Custom,
      Wave;

      // $FF: synthetic method
      private static TabGui.ColorMode[] $values() {
         return new TabGui.ColorMode[]{Rainbow, Custom, Wave};
      }
   }
}
