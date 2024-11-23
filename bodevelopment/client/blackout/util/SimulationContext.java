package bodevelopment.client.blackout.util;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.interfaces.functional.DoubleConsumer;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.class_1297;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_265;

public class SimulationContext {
   public class_238 box;
   public final class_1297 entity;
   public final int ticks;
   public final Consumer<class_238> consumer;
   public final DoubleConsumer<SimulationContext, Integer> onTick;
   public final double jumpHeight;
   public boolean onGround = false;
   public boolean prevOnGround = false;
   public boolean jump = false;
   public final class_243 originalMotion;
   public final boolean originalWater;
   public final boolean originalLava;
   public boolean inWater = false;
   public boolean inLava = false;
   public double motionX;
   public double motionY;
   public double motionZ;
   public double reverseStep = 0.0D;
   public double step = 0.0D;
   private List<class_265> collisions;

   public SimulationContext(class_1297 entity, int ticks, double jumpHeight, class_243 originalMotion, Consumer<class_238> consumer, DoubleConsumer<SimulationContext, Integer> onTick) {
      this.entity = entity;
      this.box = entity.method_5829();
      this.ticks = ticks;
      this.jumpHeight = jumpHeight;
      this.originalMotion = originalMotion;
      this.originalWater = OLEPOSSUtils.inWater(this.box);
      this.originalLava = OLEPOSSUtils.inLava(this.box);
      this.consumer = consumer;
      this.onTick = onTick;
      this.motionX = originalMotion.field_1352;
      this.motionY = originalMotion.field_1351;
      this.motionZ = originalMotion.field_1350;
   }

   public void move(class_243 movement) {
      this.box = this.box.method_997(movement);
   }

   public boolean isOnGround() {
      return Simulator.isOnGround(this.entity, this.box) && this.motionY < 0.0D;
   }

   public void setOnGround(boolean onGround) {
      this.onGround = onGround;
   }

   public void updateCollisions() {
      this.collisions = BlackOut.mc.field_1687.method_20743(this.entity, this.box.method_1012(this.motionX, this.motionY, this.motionZ));
   }

   public class_243 collide(class_243 motion, class_238 box) {
      return class_1297.method_20736(this.entity, motion, box, BlackOut.mc.field_1687, this.collisions);
   }

   public void accept() {
      if (this.consumer != null) {
         this.consumer.accept(this.box);
      }

   }

   public boolean inFluid() {
      return this.inWater || this.inLava;
   }

   public boolean inFluidOriginal() {
      return this.originalWater || this.originalLava;
   }
}
