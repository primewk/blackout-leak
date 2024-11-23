package bodevelopment.client.blackout.module.setting.settings;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.interfaces.functional.SingleOut;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.util.ColorUtils;
import bodevelopment.client.blackout.util.GuiColorUtils;
import bodevelopment.client.blackout.util.render.RenderUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.class_3532;

public class BoolSetting extends Setting<Boolean> {
   private float progress = -1.0F;

   public BoolSetting(String name, Boolean val, String description, SingleOut<Boolean> visible) {
      super(name, val, description, visible);
   }

   public float render() {
      float target = (Boolean)this.get() ? 1.0F : 0.0F;
      if (this.progress < 0.0F) {
         this.progress = target;
      } else {
         this.progress = class_3532.method_16439(Math.min(this.frameTime * 20.0F, 1.0F), this.progress, target);
      }

      BlackOut.FONT.text(this.stack, this.name, 2.0F, (float)(this.x + 5), (float)(this.y + 9), GuiColorUtils.getSettingText((float)this.y), false, true);
      RenderUtils.rounded(this.stack, (float)this.x + this.width - 30.0F, (float)(this.y + 9), 16.0F, 0.0F, 8.0F, 0.0F, ColorUtils.lerpColor((double)this.progress, GuiColorUtils.getDisabledBindBG((float)this.y), GuiColorUtils.getEnabledBindBG((float)this.y)).getRGB(), ColorUtils.SHADOW100I);
      RenderUtils.rounded(this.stack, (float)this.x + this.width - 30.0F + this.progress * 16.0F, (float)(this.y + 9), 0.0F, 0.0F, 8.0F, 0.0F, ColorUtils.lerpColor((double)this.progress, GuiColorUtils.getDisabledBindDot((float)this.y), GuiColorUtils.getEnabledBindDot((float)this.y)).getRGB(), ColorUtils.SHADOW100I);
      return this.getHeight();
   }

   public boolean onMouse(int key, boolean pressed) {
      if (key == 0 && pressed && this.mx > (double)this.x && this.mx < (double)((float)this.x + this.width) && this.my > (double)this.y && this.my < (double)((float)this.y + this.getHeight())) {
         this.setValue(!(Boolean)this.get());
         Managers.CONFIG.saveAll();
         return true;
      } else {
         return false;
      }
   }

   public float getHeight() {
      return 26.0F;
   }

   public void write(JsonObject object) {
      object.addProperty(this.name, (Boolean)this.get());
   }

   public void set(JsonElement element) {
      this.setValue(element.getAsBoolean());
   }
}
