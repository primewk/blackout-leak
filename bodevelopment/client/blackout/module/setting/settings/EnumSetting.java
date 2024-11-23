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
import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import net.minecraft.class_3532;

public class EnumSetting<T extends Enum<?>> extends Setting<T> {
   public T[] values = null;
   private boolean choosing = false;
   private double maxWidth = 0.0D;
   private float xOffset = 0.0F;
   private float wi = 0.0F;

   public EnumSetting(String name, T val, String description, SingleOut<Boolean> visible) {
      super(name, val, description, visible);

      try {
         this.values = (Enum[])val.getClass().getMethod("values").invoke((Object)null);
      } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException var6) {
         var6.printStackTrace();
      }

   }

   public float render() {
      int var3;
      if (BlackOut.mc.field_1772 != null && this.maxWidth == 0.0D) {
         this.maxWidth = 0.0D;
         Enum[] var1 = this.values;
         int var2 = var1.length;

         for(var3 = 0; var3 < var2; ++var3) {
            T v = var1[var3];
            double w = (double)BlackOut.FONT.getWidth(v.name());
            if (w > this.maxWidth) {
               this.maxWidth = w;
            }
         }

         this.xOffset = (float)Math.max(this.maxWidth / 2.0D - 60.0D, 0.0D);
         this.wi = (float)Math.max(this.maxWidth, 100.0D);
      }

      float offset = 0.0F;
      if (this.choosing) {
         RenderUtils.rounded(this.stack, (float)this.x + this.width - this.wi - this.xOffset - 10.0F, (float)this.y, this.wi, (float)(this.values.length * 20), 4.0F, 2.0F, (new Color(25, 25, 25, 85)).getRGB(), ColorUtils.SHADOW100I);
         Enum[] var13 = this.values;
         var3 = var13.length;

         for(int var14 = 0; var14 < var3; ++var14) {
            T t = var13[var14];
            if (t != this.get()) {
               offset += 20.0F;
               double xm = this.mx - (double)((float)this.x + this.width - this.wi / 2.0F - 10.0F - this.xOffset);
               double ym = this.my - (double)((float)(this.y + 10) + offset);
               double d = 3.0D - class_3532.method_15350(Math.sqrt(xm * xm + ym * ym) / 10.0D, 1.0D, 2.0D) - 1.0D;
               BlackOut.FONT.text(this.stack, t.name(), 1.8F, (float)this.x + this.width - this.wi / 2.0F - 10.0F - this.xOffset, (float)(this.y + 10) + offset, ColorUtils.lerpColor(d, new Color(150, 150, 150, 255), new Color(200, 200, 200, 255)), true, true);
            }
         }
      }

      BlackOut.FONT.text(this.stack, ((Enum)this.get()).name(), 2.0F, (float)this.x + this.width - this.wi / 2.0F - 10.0F - this.xOffset, (float)(this.y + 9), GuiColorUtils.getSettingText((float)this.y), true, true);
      BlackOut.FONT.text(this.stack, this.name, 2.0F, (float)(this.x + 5), (float)(this.y + 9), GuiColorUtils.getSettingText((float)this.y), false, true);
      return this.getHeight();
   }

   public float getHeight() {
      return (float)(25 + (this.choosing ? (this.values.length - 1) * 20 + 4 : 0));
   }

   public boolean onMouse(int key, boolean pressed) {
      if (key == 0 && pressed && this.mx > (double)((float)this.x + this.width - this.wi - this.xOffset - 14.0F) && this.mx < (double)((float)this.x + this.width - this.xOffset + 4.0F) && this.my > (double)this.y && this.my < (double)((float)this.y + this.getHeight() + (float)(this.choosing ? 20 * this.values.length + 9 : 0))) {
         if (this.choosing) {
            this.setValue(this.getClosest());
            Managers.CONFIG.saveAll();
         }

         this.choosing = !this.choosing;
         return true;
      } else {
         return false;
      }
   }

   public T getClosest() {
      float offset = 0.0F;
      T closest = (Enum)this.get();
      double cd = this.my - (double)(this.y + 9);
      Enum[] var5 = this.values;
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         T t = var5[var7];
         if (t != this.get()) {
            offset += 20.0F;
            double d = Math.abs(this.my - (double)((float)(this.y + 10) + offset));
            if (d < cd) {
               closest = t;
               cd = d;
            }
         }
      }

      return closest;
   }

   public void write(JsonObject object) {
      object.addProperty(this.name, ((Enum)this.get()).name());
   }

   public void set(JsonElement element) {
      T newVal = null;
      Enum[] var3 = this.values;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         T val = var3[var5];
         if (element.getAsString().equals(val.name())) {
            newVal = val;
            break;
         }
      }

      if (newVal != null) {
         this.setValue(newVal);
      }

   }
}
