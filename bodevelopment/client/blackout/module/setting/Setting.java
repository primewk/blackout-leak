package bodevelopment.client.blackout.module.setting;

import bodevelopment.client.blackout.interfaces.functional.SingleOut;
import bodevelopment.client.blackout.manager.Managers;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.class_4587;

public class Setting<T> {
   public final String name;
   public final String description;
   protected final T defaultValue;
   protected T value;
   private final SingleOut<Boolean> visible;
   protected int x = 0;
   protected int y = 0;
   protected double mx = 0.0D;
   protected double my = 0.0D;
   protected float width = 0.0F;
   protected float frameTime = 0.0F;
   protected class_4587 stack = null;

   public Setting(String name, T val, String description, SingleOut<Boolean> visible) {
      this.name = name;
      this.description = description;
      this.visible = visible;
      this.defaultValue = val;
      this.value = val;
   }

   public T get() {
      return this.value;
   }

   protected void setValue(T value) {
      this.value = value;
   }

   public boolean isVisible() {
      return this.visible == null || (Boolean)this.visible.get();
   }

   public float onRender(class_4587 stack, float frameTime, float width, int x, int y, double mx, double my, boolean shouldRender) {
      this.stack = stack;
      this.frameTime = frameTime;
      this.x = x;
      this.y = y;
      this.mx = mx;
      this.my = my;
      this.width = width;
      return shouldRender ? this.render() : this.getHeight();
   }

   public float render() {
      return 0.0F;
   }

   public boolean onMouse(int button, boolean pressed) {
      return false;
   }

   public void onKey(int key, boolean pressed) {
   }

   public float getHeight() {
      return 0.0F;
   }

   public void write(JsonObject object) {
   }

   public void read(JsonObject object) {
      if (!object.has(this.name)) {
         this.reset();
         Managers.CONFIG.saveAll();
      } else {
         try {
            this.set(object.get(this.name));
         } catch (Exception var3) {
            this.reset();
            Managers.CONFIG.saveAll();
         }

      }
   }

   protected void set(JsonElement element) {
   }

   public void reset() {
      this.value = this.defaultValue;
   }
}
