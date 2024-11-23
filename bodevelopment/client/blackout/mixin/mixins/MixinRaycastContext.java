package bodevelopment.client.blackout.mixin.mixins;

import bodevelopment.client.blackout.interfaces.mixin.IRaycastContext;
import net.minecraft.class_1297;
import net.minecraft.class_243;
import net.minecraft.class_3726;
import net.minecraft.class_3959;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({class_3959.class})
public class MixinRaycastContext implements IRaycastContext {
   @Mutable
   @Shadow
   @Final
   private class_3960 field_17555;
   @Mutable
   @Shadow
   @Final
   private class_242 field_17556;
   @Mutable
   @Shadow
   @Final
   private class_3726 field_17557;
   @Mutable
   @Shadow
   @Final
   private class_243 field_17553;
   @Mutable
   @Shadow
   @Final
   private class_243 field_17554;

   public void blackout_Client$set(class_243 start, class_243 end, class_3960 shapeType, class_242 fluidHandling, class_1297 entity) {
      this.field_17555 = shapeType;
      this.field_17556 = fluidHandling;
      this.field_17557 = class_3726.method_16195(entity);
      this.field_17553 = start;
      this.field_17554 = end;
   }

   public void blackout_Client$set(class_243 start, class_243 end) {
      this.field_17553 = start;
      this.field_17554 = end;
   }

   public void blackout_Client$set(class_3960 shapeType, class_242 fluidHandling, class_1297 entity) {
      this.field_17555 = shapeType;
      this.field_17556 = fluidHandling;
      this.field_17557 = class_3726.method_16195(entity);
   }

   public void blackout_Client$setStart(class_243 start) {
      this.field_17553 = start;
   }

   public void blackout_Client$setEnd(class_243 end) {
      this.field_17554 = end;
   }
}
