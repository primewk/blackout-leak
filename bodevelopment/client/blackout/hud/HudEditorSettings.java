package bodevelopment.client.blackout.hud;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.gui.clickgui.components.ModuleComponent;
import bodevelopment.client.blackout.module.modules.client.GuiSettings;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.util.ColorUtils;
import bodevelopment.client.blackout.util.GuiColorUtils;
import bodevelopment.client.blackout.util.render.RenderUtils;
import java.awt.Color;
import java.util.Iterator;
import net.minecraft.class_4587;

public class HudEditorSettings {
   private final float width = 275.0F;
   private float x;
   private float y;
   private HudElement openedElement = null;
   private long openTime = 0L;
   private int openedY;
   private class_4587 stack;
   private float frameTime;
   private float mx;
   private float my;
   private float length;
   private boolean moving;

   public void render(class_4587 stack, float frameTime, int mouseX, int mouseY) {
      this.stack = stack;
      this.frameTime = frameTime;
      float prevMx = this.mx;
      float prevMy = this.my;
      this.mx = (float)(mouseX * RenderUtils.getScale());
      this.my = (float)(mouseY * RenderUtils.getScale());
      if (this.moving) {
         this.updateMoving(this.mx - prevMx, this.my - prevMy);
      }

      if (this.openedElement != null) {
         stack.method_22903();
         stack.method_46416(this.x, this.y, 0.0F);
         this.length = ModuleComponent.getLength(this.openedElement.settingGroups) + 30.0F;
         this.renderBG();
         BlackOut.FONT.text(stack, this.openedElement.name, 2.0F, 137.5F, 15.0F, GuiColorUtils.enabled, true, true);
         this.renderSettings();
         stack.method_22909();
      }
   }

   private void updateMoving(float deltaX, float deltaY) {
      this.x += deltaX;
      this.y += deltaY;
   }

   public boolean onMouse(int button, boolean pressed) {
      if (this.openedElement == null) {
         this.moving = false;
         return false;
      } else if (button != 0) {
         return button == 1 ? this.handleRightClick(pressed) : false;
      } else if (!pressed) {
         this.moving = false;
         this.openedElement.settingGroups.forEach((group) -> {
            group.settings.forEach((setting) -> {
               if (setting.isVisible()) {
                  setting.onMouse(button, false);
               }

            });
         });
         return false;
      } else if (!(this.mx < this.x) && !(this.mx > this.x + 275.0F) && !(this.my < this.y) && !(this.my > this.y + this.length)) {
         if (this.my < this.y + 30.0F) {
            this.moving = true;
            return true;
         } else {
            this.openedElement.settingGroups.forEach((group) -> {
               group.settings.forEach((setting) -> {
                  if (setting.isVisible()) {
                     setting.onMouse(button, true);
                  }

               });
            });
            return true;
         }
      } else {
         return false;
      }
   }

   public void onKey(int key, boolean pressed) {
      if (this.openedElement != null) {
         this.openedElement.settingGroups.forEach((group) -> {
            group.settings.forEach((setting) -> {
               if (setting.isVisible()) {
                  setting.onKey(key, pressed);
               }

            });
         });
      }

   }

   private boolean handleRightClick(boolean pressed) {
      if (!pressed) {
         return false;
      } else if (!(this.mx < this.x) && !(this.mx > this.x + 275.0F) && !(this.my < this.y) && !(this.my > this.y + this.length)) {
         if (this.my < this.y + 30.0F) {
            this.openedElement = null;
         }

         return true;
      } else {
         return false;
      }
   }

   public void onRemoved(HudElement element) {
      if (element.equals(this.openedElement)) {
         this.openedElement = null;
      }

   }

   public void set(HudElement hudElement) {
      this.openTime = System.currentTimeMillis();
      this.openedElement = hudElement;
      this.x = this.mx;
      this.y = this.my;
   }

   private void renderBG() {
      RenderUtils.rounded(this.stack, 0.0F, 0.0F, 275.0F, this.length - 5.0F, 5.0F, 30.0F, GuiColorUtils.bg2.getRGB(), ColorUtils.SHADOW100I);
   }

   private void renderSettings() {
      this.openedY = 30;
      GuiColorUtils.set(this.openedElement);

      for(int i = 0; i < this.openedElement.settingGroups.size(); ++i) {
         SettingGroup settingGroup = (SettingGroup)this.openedElement.settingGroups.get(i);
         this.renderSettingGroup(settingGroup, i == this.openedElement.settingGroups.size() - 1);
         settingGroup.settings.forEach(this::renderSetting);
      }

   }

   private void renderSetting(Setting<?> setting) {
      if (setting.isVisible()) {
         this.openedY += (int)setting.onRender(this.stack, this.frameTime * 20.0F, 275.0F, 0, this.openedY, (double)(this.mx - this.x), (double)(this.my - this.y), true);
      }
   }

   private void renderSettingGroup(SettingGroup group, boolean last) {
      float categoryLength = 35.0F;
      Iterator var4 = group.settings.iterator();

      while(var4.hasNext()) {
         Setting<?> setting = (Setting)var4.next();
         if (setting.isVisible()) {
            categoryLength += setting.getHeight();
         }
      }

      int shadowColor;
      switch((GuiSettings.SettingGroupMode)GuiSettings.getInstance().settingGroup.get()) {
      case Line:
         RenderUtils.fadeLine(this.stack, 0.0F, (float)(this.openedY + 30), 275.0F, (float)(this.openedY + 30), GuiColorUtils.getSettingCategory((float)(this.openedY + 30)).getRGB());
         BlackOut.FONT.text(this.stack, group.name, 1.8F, 137.5F, (float)(this.openedY + 20), GuiColorUtils.getSettingCategory((float)(this.openedY + 30)), true, true);
         this.openedY += 40;
         break;
      case Shadow:
         shadowColor = (new Color(0, 0, 0, 80)).getRGB();
         if (!last) {
            RenderUtils.topFade(this.stack, -5.0F, (float)this.openedY + categoryLength - 10.0F, 285.0F, 20.0F, shadowColor);
         }

         RenderUtils.bottomFade(this.stack, -5.0F, (float)(this.openedY + 30), 285.0F, 20.0F, shadowColor);
         BlackOut.FONT.text(this.stack, group.name, 1.8F, 137.5F, (float)(this.openedY + 15), GuiColorUtils.getSettingCategory((float)(this.openedY + 30)), true, true);
         this.openedY += 45;
         break;
      case Quad:
         shadowColor = (new Color(0, 0, 0, 80)).getRGB();
         RenderUtils.rounded(this.stack, 3.0F, (float)(this.openedY + 12), 269.0F, categoryLength, 2.0F, 7.0F, GuiColorUtils.bg2.getRGB(), shadowColor);
         BlackOut.FONT.text(this.stack, group.name, 1.8F, 137.5F, (float)(this.openedY + 25), GuiColorUtils.getSettingCategory((float)(this.openedY + 30)), true, true);
         this.openedY += 50;
         break;
      case None:
         BlackOut.FONT.text(this.stack, group.name, 1.8F, 137.5F, (float)(this.openedY + 20), GuiColorUtils.getSettingCategory((float)(this.openedY + 30)), true, true);
         this.openedY += 40;
      }

   }
}
