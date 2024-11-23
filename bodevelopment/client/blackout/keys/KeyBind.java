package bodevelopment.client.blackout.keys;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.ConfigType;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.util.ColorUtils;
import bodevelopment.client.blackout.util.GuiColorUtils;
import bodevelopment.client.blackout.util.render.AnimUtils;
import bodevelopment.client.blackout.util.render.RenderUtils;
import java.awt.Color;
import net.minecraft.class_4587;

public class KeyBind {
   private float x;
   private float y;
   private double mx;
   private double my;
   private float width;
   public Pressable value;
   private int holdingKey;
   private boolean holdingMouse;
   private long prevTime;
   private double bindProgress = 0.0D;
   private double pulse = 0.0D;

   public KeyBind(Pressable value) {
      this.value = value;
   }

   public void render(class_4587 stack, float x, float y, float maxX, double mx, double my) {
      this.x = x;
      this.y = y;
      this.mx = mx;
      this.my = my;
      String name;
      if (this.value == null) {
         name = "";
         this.width = 17.0F;
      } else {
         name = this.value.getName();
         this.width = Math.max(BlackOut.FONT.getWidth(name) * 1.5F, 17.0F);
      }

      this.x -= Math.max(this.x + this.width - maxX, 0.0F);
      double frameTime = (double)(System.currentTimeMillis() - this.prevTime) / 1000.0D;
      this.binding(frameTime);
      this.prevTime = System.currentTimeMillis();
      double progress;
      float rad;
      if (this.pulse > 0.0D) {
         progress = this.pulse;
         rad = (float)(this.pulse * 5.0D);
      } else {
         progress = AnimUtils.easeOutCubic(this.bindProgress) / 2.0D;
         rad = (float)(this.bindProgress * 3.0D);
      }

      Color shadowColor = ColorUtils.lerpColor(this.pulse, ColorUtils.SHADOW100, Color.WHITE);
      Color insideColor = ColorUtils.lerpColor(progress, GuiColorUtils.bindBG, Color.WHITE);
      this.pulse = Math.max(this.pulse - frameTime, 0.0D);
      RenderUtils.rounded(stack, this.x - this.width / 2.0F, this.y - 9.0F, this.width, 17.0F, 4.0F, rad, insideColor.getRGB(), shadowColor.getRGB());
      BlackOut.FONT.text(stack, name, 1.5F, this.x, this.y, GuiColorUtils.bindText, true, true);
   }

   private void binding(double frameTime) {
      if (this.holdingKey < 0) {
         this.bindProgress = Math.max(this.bindProgress - frameTime * 3.0D, 0.0D);
      } else {
         if (this.holdingMouse) {
            if (!MouseButtons.get(this.holdingKey)) {
               this.stopBinding();
               return;
            }
         } else if (!Keys.get(this.holdingKey)) {
            this.stopBinding();
            return;
         }

         this.bindProgress = Math.min(this.bindProgress + frameTime * 2.0D, 1.0D);
         if (this.bindProgress >= 1.0D) {
            if (this.holdingMouse) {
               this.setMouse(this.holdingKey);
            } else {
               this.setKey(this.holdingKey);
            }

            Managers.CONFIG.save(ConfigType.Binds);
            this.stopBinding();
            this.pulse = 1.0D;
         }

      }
   }

   private void stopBinding() {
      this.holdingKey = -1;
   }

   public boolean onMouse(int key, boolean pressed) {
      if (!this.isInside()) {
         return false;
      } else {
         if (pressed && this.holdingKey < 0) {
            this.holdingKey = key;
            this.holdingMouse = true;
         }

         return true;
      }
   }

   public boolean onKey(int key, boolean pressed) {
      if (!this.isInside()) {
         return false;
      } else {
         if (pressed && this.holdingKey < 0) {
            this.holdingKey = key;
            this.holdingMouse = false;
         }

         return true;
      }
   }

   private boolean isInside() {
      return this.mx > (double)(this.x - this.width / 2.0F - 4.0F) && this.mx < (double)(this.x + this.width / 2.0F + 4.0F) && this.my > (double)(this.y - 13.0F) && this.my < (double)(this.y + 12.0F);
   }

   public void setMouse(int key) {
      this.value = new MouseButton(key);
   }

   public void setKey(int key) {
      this.value = key != 259 && key != 261 && key != 256 ? new Key(key) : null;
   }

   public int getKey() {
      return this.value == null ? -1 : this.value.key;
   }

   public String getName() {
      return this.value == null ? "" : this.value.getName();
   }

   public boolean isKey(int key) {
      Pressable var3 = this.value;
      boolean var10000;
      if (var3 instanceof Key) {
         Key k = (Key)var3;
         if (k.key == key) {
            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
   }

   public boolean isMouse(int key) {
      Pressable var3 = this.value;
      boolean var10000;
      if (var3 instanceof MouseButton) {
         MouseButton m = (MouseButton)var3;
         if (m.key == key) {
            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
   }

   public boolean isPressed() {
      return this.value != null && this.value.isPressed();
   }
}
