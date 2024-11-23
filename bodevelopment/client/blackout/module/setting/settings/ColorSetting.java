package bodevelopment.client.blackout.module.setting.settings;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.gui.clickgui.screens.ColorScreen;
import bodevelopment.client.blackout.interfaces.functional.SingleOut;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.modules.client.ThemeSettings;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.util.ColorUtils;
import bodevelopment.client.blackout.util.GuiColorUtils;
import bodevelopment.client.blackout.util.render.RenderUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.awt.Color;
import net.minecraft.class_3532;
import net.minecraft.class_5253.class_5254;

public class ColorSetting extends Setting<BlackOutColor> {
   public int theme = 0;
   public float saturation = 0.0F;
   public float brightness = 0.0F;
   public int alpha = 255;
   public BlackOutColor actual;

   public ColorSetting(String name, BlackOutColor val, String description, SingleOut<Boolean> visible) {
      super(name, val, description, visible);
      this.actual = ((BlackOutColor)this.value).copy();
   }

   public float render() {
      BlackOut.FONT.text(this.stack, this.name, 2.0F, (float)(this.x + 5), (float)(this.y + 9), GuiColorUtils.getSettingText((float)this.y), false, true);
      float ry = (float)(this.y + 9);
      int color = this.get().withAlpha(255).getRGB();
      RenderUtils.rounded(this.stack, (float)this.x + this.width - 34.0F, ry - 4.0F, 25.0F, 8.0F, 3.0F, 4.0F, color, color);
      return this.getHeight();
   }

   public boolean onMouse(int key, boolean pressed) {
      if (key == 0 && pressed && this.mx > (double)this.x && this.mx < (double)((float)this.x + this.width) && this.my > (double)this.y && this.my < (double)((float)this.y + this.getHeight())) {
         Managers.CLICK_GUI.openScreen(new ColorScreen(this, this.name));
         Managers.CONFIG.saveAll();
         return true;
      } else {
         return false;
      }
   }

   public BlackOutColor get() {
      BlackOutColor var10000;
      switch(this.theme) {
      case 1:
         var10000 = this.modifyTheme(ThemeSettings.getInstance().getMain());
         break;
      case 2:
         var10000 = this.modifyTheme(ThemeSettings.getInstance().getSecond());
         break;
      default:
         var10000 = this.actual;
      }

      return var10000;
   }

   public BlackOutColor getUnmodified() {
      BlackOutColor var10000;
      switch(this.theme) {
      case 1:
         var10000 = BlackOutColor.from(ThemeSettings.getInstance().getMain());
         break;
      case 2:
         var10000 = BlackOutColor.from(ThemeSettings.getInstance().getSecond());
         break;
      default:
         var10000 = this.actual;
      }

      return var10000;
   }

   private BlackOutColor modifyTheme(int theme) {
      float[] HSB = Color.RGBtoHSB(class_5254.method_27765(theme), class_5254.method_27766(theme), class_5254.method_27767(theme), new float[3]);
      HSB[1] = class_3532.method_15363(HSB[1] + this.saturation, 0.0F, 1.0F);
      HSB[2] = class_3532.method_15363(HSB[2] + this.brightness, 0.0F, 1.0F);
      return BlackOutColor.from(ColorUtils.withAlpha(Color.HSBtoRGB(HSB[0], HSB[1], HSB[2]), this.alpha));
   }

   public float getHeight() {
      return 26.0F;
   }

   public void write(JsonObject jsonObject) {
      int var10002 = this.theme;
      jsonObject.addProperty(this.name, var10002 + "§" + this.alpha + "§" + this.saturation + "§" + this.brightness + "§" + this.actual.getRGB());
   }

   public void set(JsonElement element) {
      String[] strings = element.getAsString().split("§");
      if (strings.length != 5) {
         this.theme = 0;
         this.alpha = 255;
         this.brightness = 0.0F;
         this.saturation = 0.0F;
         this.reset();
      } else {
         this.theme = Integer.parseInt(strings[0]);
         this.alpha = Integer.parseInt(strings[1]);
         this.saturation = class_3532.method_15363(Float.parseFloat(strings[2]), -1.0F, 1.0F);
         this.brightness = class_3532.method_15363(Float.parseFloat(strings[3]), -1.0F, 1.0F);
         this.actual = BlackOutColor.from(Integer.parseInt(strings[4]));
      }

   }

   public void reset() {
      super.reset();
      this.actual = ((BlackOutColor)this.defaultValue).copy();
   }

   public void setValue(BlackOutColor color) {
      super.setValue(color);
   }
}
