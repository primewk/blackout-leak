package bodevelopment.client.blackout.gui.clickgui.components;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.gui.clickgui.ClickGui;
import bodevelopment.client.blackout.gui.clickgui.Component;
import bodevelopment.client.blackout.keys.KeyBind;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.modules.client.GuiSettings;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.rendering.renderer.Renderer;
import bodevelopment.client.blackout.util.ColorUtils;
import bodevelopment.client.blackout.util.GuiColorUtils;
import bodevelopment.client.blackout.util.SelectedComponent;
import bodevelopment.client.blackout.util.render.AnimUtils;
import bodevelopment.client.blackout.util.render.RenderUtils;
import com.mojang.blaze3d.platform.GlStateManager;
import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import net.minecraft.class_3532;
import net.minecraft.class_4587;

public class ModuleComponent extends Component {
   public final Module module;
   public float length;
   public float l;
   public float maxLength = -1.0F;
   public boolean opened = false;
   private float openProgress = 0.0F;
   private final int id = SelectedComponent.nextId();
   private double toggleProgress = 0.0D;
   private long prevTime = 0L;
   private static final Color disabledColor = new Color(150, 150, 150, 255);

   public ModuleComponent(class_4587 stack, Module module) {
      super(stack);
      this.module = module;
   }

   public float render() {
      GuiColorUtils.set(this.module);
      this.length = this.getHeight() + getLength(this.module.settingGroups);
      this.updateAnimation();
      if (!((float)this.y > ClickGui.height + 30.0F) && !((float)this.y + this.maxLength < -30.0F)) {
         this.shadowScissor();
         float bgY = (float)Math.max(-15, this.y);
         float bgMaxY = Math.min((float)this.y + this.maxLength, ClickGui.height + 15.0F);
         this.rounded((float)this.x, bgY, this.width, bgMaxY - bgY, 5.0F, 8.0F, GuiColorUtils.bg2, ColorUtils.SHADOW100);
         this.scissor();
         double delta = (double)(System.currentTimeMillis() - this.prevTime) / 1000.0D;
         this.prevTime = System.currentTimeMillis();
         if (this.module.enabled) {
            this.toggleProgress = Math.min(this.toggleProgress + delta, 1.0D);
         } else {
            this.toggleProgress = Math.max(this.toggleProgress - delta, 0.0D);
         }

         this.renderModule(AnimUtils.easeInOutCubic(this.toggleProgress));
         this.renderSettings();
         GlStateManager._disableScissorTest();
         return Math.min(this.l, this.maxLength);
      } else {
         return Math.min(this.l, this.maxLength);
      }
   }

   private void renderSettings() {
      this.l = this.getHeight();

      for(int i = 0; i < this.module.settingGroups.size(); ++i) {
         SettingGroup settingGroup = (SettingGroup)this.module.settingGroups.get(i);
         if (this.l >= this.maxLength) {
            return;
         }

         float yPos = (float)this.y + this.l;
         if (yPos > ClickGui.height) {
            return;
         }

         float categoryHeight = 0.0F;
         float height = ((GuiSettings.SettingGroupMode)GuiSettings.getInstance().settingGroup.get()).getHeight();
         Iterator var6 = settingGroup.settings.iterator();

         while(var6.hasNext()) {
            Setting<?> setting = (Setting)var6.next();
            if (setting.isVisible()) {
               categoryHeight += setting.getHeight();
            }
         }

         if (yPos > -height - categoryHeight - 30.0F) {
            this.renderSettingGroup(settingGroup, i == this.module.settingGroups.size() - 1);
         }

         this.l += height;
         settingGroup.settings.forEach(this::renderSetting);
      }

   }

   private void renderSetting(Setting<?> setting) {
      if (setting.isVisible()) {
         int posY = (int)((float)this.y + this.l);
         boolean shouldRender = this.l < this.maxLength && (float)posY >= -setting.getHeight() && (float)posY <= ClickGui.height + setting.getHeight();
         this.l += setting.onRender(this.stack, this.frameTime, this.width - 10.0F, this.x + 5, (int)((float)this.y + this.l), this.mx, this.my, shouldRender);
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

      switch((GuiSettings.SettingGroupMode)GuiSettings.getInstance().settingGroup.get()) {
      case Line:
         this.fadeLine((float)this.x, (float)this.y + this.l + 30.0F, (float)this.x + this.width, (float)this.y + this.l + 30.0F, GuiColorUtils.getSettingCategory((float)this.y + this.l + 30.0F));
         this.text(group.name, 1.8F, (float)this.x + this.width / 2.0F, (float)((int)((float)this.y + this.l + 20.0F)), true, true, GuiColorUtils.getSettingCategory((float)this.y + this.l + 30.0F));
         break;
      case Shadow:
         float bottomY = (float)this.y + this.l + categoryLength - 10.0F;
         if (!last && bottomY < ClickGui.height + 50.0F) {
            RenderUtils.topFade(this.stack, (float)(this.x - 5), (float)this.y + this.l + categoryLength - 10.0F, this.width + 10.0F, 20.0F, ColorUtils.SHADOW80I);
         }

         RenderUtils.bottomFade(this.stack, (float)(this.x - 5), (float)this.y + this.l + 30.0F, this.width + 10.0F, 20.0F, ColorUtils.SHADOW80I);
         this.text(group.name, 1.8F, (float)this.x + this.width / 2.0F, (float)((int)((float)this.y + this.l + 15.0F)), true, true, GuiColorUtils.getSettingCategory((float)this.y + this.l + 30.0F));
         break;
      case Quad:
         RenderUtils.rounded(this.stack, (float)(this.x + 7), (float)this.y + this.l + 12.0F, this.width - 14.0F, categoryLength, 2.0F, 7.0F, GuiColorUtils.bg2.getRGB(), ColorUtils.SHADOW80I);
         this.text(group.name, 1.8F, (float)this.x + this.width / 2.0F, (float)((int)((float)this.y + this.l + 25.0F)), true, true, GuiColorUtils.getSettingCategory((float)this.y + this.l + 30.0F));
         break;
      case None:
         this.text(group.name, 1.8F, (float)this.x + this.width / 2.0F, (float)((int)((float)this.y + this.l + 20.0F)), true, true, GuiColorUtils.getSettingCategory((float)this.y + this.l + 30.0F));
      }

   }

   private void renderModule(double toggleProgress) {
      float moduleNameOffset = this.getModuleNameOffset();
      GuiSettings guiSettings = GuiSettings.getInstance();
      double nameY = (double)this.getY();
      if (nameY > -50.0D && nameY < (double)(ClickGui.height + 50.0F)) {
         float prevAlpha = Renderer.getAlpha();
         if (toggleProgress > 0.0D) {
            Renderer.setAlpha((float)toggleProgress);
            guiSettings.textColor.render(this.stack, this.module.getDisplayName(), this.getScale(), this.getX(), this.getY(), false, false);
            Renderer.setAlpha(prevAlpha);
         }

         if (1.0D - toggleProgress > 0.0D) {
            Renderer.setAlpha((float)(1.0D - toggleProgress));
            BlackOut.FONT.text(this.stack, this.module.getDisplayName(), this.getScale(), this.getX(), this.getY(), disabledColor, false, false);
            Renderer.setAlpha(prevAlpha);
         }
      }

      if (this.module.toggleable()) {
         if ((float)this.y + moduleNameOffset > -50.0F && nameY < (double)(ClickGui.height + 50.0F)) {
            ((KeyBind)this.module.bind.get()).render(this.stack, (float)this.x + this.width - 30.0F, (float)this.y + moduleNameOffset, (float)this.x + this.width, this.mx, this.my);
         }

      }
   }

   private float getModuleNameOffset() {
      return this.getHeight() / 2.0F;
   }

   public static float getLength(List<SettingGroup> settingGroups) {
      float var10000;
      switch((GuiSettings.SettingGroupMode)GuiSettings.getInstance().settingGroup.get()) {
      case Line:
      case Shadow:
      case None:
         var10000 = 0.0F;
         break;
      case Quad:
         var10000 = 7.0F;
         break;
      default:
         throw new IncompatibleClassChangeError();
      }

      float length = var10000;
      Iterator var2 = settingGroups.iterator();

      while(var2.hasNext()) {
         SettingGroup group = (SettingGroup)var2.next();
         float var10001;
         switch((GuiSettings.SettingGroupMode)GuiSettings.getInstance().settingGroup.get()) {
         case Line:
         case None:
            var10001 = 40.0F;
            break;
         case Shadow:
            var10001 = 45.0F;
            break;
         case Quad:
            var10001 = 50.0F;
            break;
         default:
            throw new IncompatibleClassChangeError();
         }

         length += var10001;
         Iterator var4 = group.settings.iterator();

         while(var4.hasNext()) {
            Setting<?> setting = (Setting)var4.next();
            if (setting.isVisible()) {
               length += setting.getHeight();
            }
         }
      }

      return length;
   }

   public void onMouse(int button, boolean pressed) {
      if (!this.module.toggleable() || !((KeyBind)this.module.bind.get()).onMouse(button, pressed)) {
         if (!(this.my < (double)this.y)) {
            if (this.module.toggleable() && button == 0 && pressed && ((KeyBind)this.module.bind.get()).onMouse(0, true)) {
               SelectedComponent.setId(this.id);
            } else if (this.mx > (double)this.x && this.mx < (double)((float)this.x + this.width) && pressed && this.my < (double)((float)this.y + this.getHeight() - 10.0F)) {
               if (button == 1) {
                  this.opened = !this.opened;
               } else if (button == 0 && this.module.toggleable()) {
                  this.module.toggle();
                  Managers.CONFIG.saveModule(this.module);
               }

            } else {
               if (this.mx > (double)this.x && this.mx < (double)((float)this.x + this.width) && this.my < (double)((float)this.y + this.maxLength) || !pressed) {
                  Iterator var3 = this.module.settingGroups.iterator();

                  while(var3.hasNext()) {
                     SettingGroup group = (SettingGroup)var3.next();
                     Iterator var5 = group.settings.iterator();

                     while(var5.hasNext()) {
                        Setting<?> setting = (Setting)var5.next();
                        if (setting.isVisible() && setting.onMouse(button, pressed) && pressed) {
                           return;
                        }
                     }
                  }
               }

            }
         }
      }
   }

   private void updateAnimation() {
      float dist = Math.abs(this.getHeight() - this.length);
      float animationTime = class_3532.method_15363(dist / 100.0F, 1.0F, 10.0F);
      this.openProgress = class_3532.method_15363(this.openProgress + (this.opened ? this.frameTime * 20.0F / animationTime : -this.frameTime * 20.0F / animationTime), 0.0F, 1.0F);
      this.maxLength = class_3532.method_16439((float)AnimUtils.easeInOutSine((double)this.openProgress), this.getHeight(), this.length);
   }

   private void shadowScissor() {
      float sx = (float)BlackOut.mc.method_22683().method_4480() / 2.0F - ClickGui.width / 2.0F * ClickGui.unscaled + ClickGui.x;
      float y1 = (float)BlackOut.mc.method_22683().method_4507() / 2.0F - (ClickGui.height / 2.0F + 10.0F) * ClickGui.unscaled - ClickGui.y;
      float y2 = (float)BlackOut.mc.method_22683().method_4507() / 2.0F + (ClickGui.height / 2.0F + 10.0F) * ClickGui.unscaled - ClickGui.y;
      GlStateManager._enableScissorTest();
      GlStateManager._scissorBox((int)sx, (int)y1, (int)(ClickGui.width * ClickGui.unscaled), (int)Math.abs(y1 - y2));
   }

   private void scissor() {
      float minY = (float)Math.max(0, this.y);
      float maxY = Math.min(ClickGui.height, (float)this.y + this.maxLength);
      float sx = (float)BlackOut.mc.method_22683().method_4480() / 2.0F - (ClickGui.width / 2.0F - (float)this.x + 5.0F) * ClickGui.unscaled + ClickGui.x;
      float y1 = (float)BlackOut.mc.method_22683().method_4507() / 2.0F - (ClickGui.height / 2.0F - (ClickGui.height - maxY) + 5.0F) * ClickGui.unscaled - ClickGui.y;
      float y2 = (float)BlackOut.mc.method_22683().method_4507() / 2.0F + (ClickGui.height / 2.0F - minY + 10.0F) * ClickGui.unscaled - ClickGui.y;
      GlStateManager._scissorBox((int)sx, (int)Math.ceil((double)y1), (int)((this.width + 10.0F) * ClickGui.unscaled), (int)Math.ceil(y1 > y2 ? 0.0D : (double)Math.abs(y1 - y2)));
   }

   private float getX() {
      float closedX = (Boolean)GuiSettings.getInstance().centerXClosed.get() ? (float)this.x + this.width / 2.0F - BlackOut.FONT.getWidth(this.module.getDisplayName()) / 2.0F * this.getScale() : class_3532.method_16439(((Double)GuiSettings.getInstance().moduleXClosed.get()).floatValue(), (float)(this.x + 8), (float)this.x + this.width - this.getScale() * BlackOut.FONT.getWidth(this.module.getDisplayName()) - 38.0F);
      float openX = (Boolean)GuiSettings.getInstance().centerX.get() ? (float)this.x + this.width / 2.0F - BlackOut.FONT.getWidth(this.module.getDisplayName()) / 2.0F * this.getScale() : class_3532.method_16439(((Double)GuiSettings.getInstance().moduleX.get()).floatValue(), (float)(this.x + 8), (float)this.x + this.width - this.getScale() * BlackOut.FONT.getWidth(this.module.getDisplayName()) - 38.0F);
      return class_3532.method_16439(this.openProgress, closedX, openX);
   }

   private float getY() {
      return class_3532.method_16439(class_3532.method_16439(this.openProgress, ((Double)GuiSettings.getInstance().moduleYClosed.get()).floatValue(), ((Double)GuiSettings.getInstance().moduleY.get()).floatValue()), (float)(this.y + 8), Math.max((float)(this.y + 8), (float)this.y + this.getHeight() - 14.0F - this.getScale() * BlackOut.FONT.getHeight()));
   }

   private float getScale() {
      return class_3532.method_16439(this.openProgress, ((Double)GuiSettings.getInstance().moduleScaleClosed.get()).floatValue(), ((Double)GuiSettings.getInstance().moduleScale.get()).floatValue());
   }

   private float getHeight() {
      return class_3532.method_16439(this.openProgress, ((Double)GuiSettings.getInstance().moduleHeightClosed.get()).floatValue(), ((Double)GuiSettings.getInstance().moduleHeight.get()).floatValue());
   }

   public void onKey(int key, boolean state) {
      ((KeyBind)this.module.bind.get()).onKey(key, state);
      this.module.settingGroups.forEach((group) -> {
         group.settings.forEach((setting) -> {
            if (setting.isVisible()) {
               setting.onKey(key, state);
            }

         });
      });
   }
}
