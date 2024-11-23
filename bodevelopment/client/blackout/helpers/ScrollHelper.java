package bodevelopment.client.blackout.helpers;

import bodevelopment.client.blackout.interfaces.functional.SingleOut;
import bodevelopment.client.blackout.randomstuff.timers.TimerList;
import net.minecraft.class_3532;

public class ScrollHelper {
   private final float friction;
   private final float speedMulti;
   private final SingleOut<Float> min;
   private final SingleOut<Float> max;
   private float limit = 0.0F;
   private final TimerList<Float> scrolls = new TimerList(false);
   protected float speed = 0.0F;
   protected float scroll = 0.0F;

   public ScrollHelper(float friction, float speedMulti, SingleOut<Float> min, SingleOut<Float> max) {
      this.friction = friction;
      this.speedMulti = speedMulti;
      this.min = min;
      this.max = max;
   }

   public ScrollHelper limit(float limit) {
      this.limit = limit;
      return this;
   }

   public float get() {
      return this.scroll;
   }

   public void reset() {
      this.speed = 0.0F;
      this.scroll = 0.0F;
   }

   public void update(float frameTime) {
      this.scrolls.update();
      this.scrolls.forEach((timer) -> {
         this.speed += (Float)timer.value * frameTime * 20.0F;
      });
      this.scroll += this.speed * frameTime * 20.0F;
      this.clamp((Float)this.min.get(), (Float)this.max.get());
      this.speed = class_3532.method_37166(this.speed, 0.0F, frameTime * 20.0F * this.friction);
   }

   protected void clamp(float min, float max) {
      this.clampScroll(min, max);
      if (this.scroll == max) {
         this.speed = Math.min(this.speed, 0.0F);
      }

      if (this.scroll == min) {
         this.speed = Math.max(this.speed, 0.0F);
      }

   }

   private void clampScroll(float min, float max) {
      this.scroll = Math.min(this.scroll, max);
      this.scroll = Math.max(this.scroll, min);
      this.scroll = Math.min(this.scroll, max);
   }

   public void update(double frameTime) {
      this.update((float)frameTime);
   }

   public void add(float amount) {
      amount = -amount;
      if (this.limit > 0.0F) {
         amount = class_3532.method_15363(amount, -this.limit, this.limit);
      }

      this.scrolls.add(amount * this.speedMulti, 0.1D);
   }

   public void offset(float amount) {
      this.scroll += amount;
   }

   public void add(double amount) {
      this.add((float)amount);
   }
}
