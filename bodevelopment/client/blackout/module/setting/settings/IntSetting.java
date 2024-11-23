package bodevelopment.client.blackout.module.setting.settings;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.gui.TextField;
import bodevelopment.client.blackout.interfaces.functional.SingleOut;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.util.ColorUtils;
import bodevelopment.client.blackout.util.GuiColorUtils;
import bodevelopment.client.blackout.util.SelectedComponent;
import bodevelopment.client.blackout.util.render.RenderUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.awt.Color;
import net.minecraft.class_3532;

public class IntSetting extends Setting<Integer> {
   public final int min;
   public final int max;
   public final int step;
   private float sliderPos;
   private float sliderAnim = -1.0F;
   private boolean moving = false;
   private final TextField textField = new TextField();
   private final int id = SelectedComponent.nextId();
   private static final Color CLEAR = new Color(255, 255, 255, 0);

   public IntSetting(String name, Integer val, int min, int max, int step, String description, SingleOut<Boolean> visible) {
      super(name, val, description, visible);
      this.min = min;
      this.max = max;
      this.step = step;
   }

   public float render() {
      if (this.moving) {
         this.sliderPos = (float)class_3532.method_15350(class_3532.method_15370(this.mx, (double)(this.x + 10), (double)((float)this.x + this.width - 10.0F)), 0.0D, 1.0D);
         float val = (float)class_3532.method_48781(this.sliderPos, this.min, this.max);
         this.setValue(Math.round(val / (float)this.step) * this.step);
      } else {
         this.sliderPos = class_3532.method_15363(class_3532.method_37960((float)(Integer)this.get(), (float)this.min, (float)this.max), 0.0F, 1.0F);
      }

      if (Float.isNaN(this.sliderAnim)) {
         this.sliderAnim = this.sliderPos;
      }

      this.sliderAnim = class_3532.method_15363(class_3532.method_37166(this.sliderAnim, this.sliderPos, this.frameTime * 20.0F), 0.0F, 1.0F);
      BlackOut.FONT.text(this.stack, this.name, 2.0F, (float)(this.x + 5), (float)(this.y + 9), GuiColorUtils.getSettingText((float)this.y), false, true);
      if (SelectedComponent.is(this.id)) {
         try {
            this.setValue(Integer.parseInt(this.textField.getContent()));
         } catch (NumberFormatException var4) {
            try {
               this.setValue((int)Math.round(Double.parseDouble(this.textField.getContent().replace(",", "."))));
            } catch (NumberFormatException var3) {
            }
         }
      } else {
         this.textField.setContent(String.valueOf(this.get()));
      }

      this.textField.setActive(SelectedComponent.is(this.id));
      this.textField.render(this.stack, 2.0F, this.mx, this.my, (float)this.x + this.width - 55.0F, (float)(this.y + 4), 40.0F, 10.0F, 2.0F, 5.0F, GuiColorUtils.getSettingText((float)this.y), CLEAR);
      RenderUtils.rounded(this.stack, (float)(this.x + 10), (float)(this.y + 25), this.width - 20.0F, 0.0F, 6.0F, 2.0F, (new Color(0, 0, 0, 50)).getRGB(), ColorUtils.SHADOW100I);
      RenderUtils.rounded(this.stack, (float)(this.x + 10), (float)(this.y + 25), this.sliderAnim * (this.width - 20.0F), 0.0F, 4.0F, 2.0F, GuiColorUtils.getSettingText((float)this.y).getRGB(), ColorUtils.SHADOW100I);
      return this.getHeight();
   }

   public boolean onMouse(int key, boolean pressed) {
      if (key != 0) {
         return false;
      } else if (!pressed) {
         if (this.moving) {
            Managers.CONFIG.saveAll();
         }

         this.moving = false;
         return true;
      } else if (this.textField.click(0, true)) {
         SelectedComponent.setId(this.id);
         return true;
      } else if (this.mx > (double)this.x && this.mx < (double)((float)this.x + this.width) && this.my > (double)this.y && this.my < (double)((float)this.y + this.getHeight())) {
         this.moving = true;
         Managers.CONFIG.saveAll();
         return true;
      } else {
         return false;
      }
   }

   public void onKey(int key, boolean pressed) {
      if (key == 257 && SelectedComponent.is(this.id)) {
         SelectedComponent.reset();
      }

      this.textField.type(key, pressed);
   }

   public float getHeight() {
      return 38.0F;
   }

   public void write(JsonObject jsonObject) {
      jsonObject.addProperty(this.name, (Number)this.get());
   }

   public void set(JsonElement element) {
      this.setValue(element.getAsInt());
      this.sliderPos = class_3532.method_15363(class_3532.method_37960((float)(Integer)this.get(), (float)this.min, (float)this.max), 0.0F, 1.0F);
      this.sliderAnim = this.sliderPos;
   }
}
