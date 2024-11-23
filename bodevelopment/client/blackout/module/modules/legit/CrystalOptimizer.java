package bodevelopment.client.blackout.module.modules.legit;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.PacketEvent;
import bodevelopment.client.blackout.mixin.accessors.AccessorInteractEntityC2SPacket;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import net.minecraft.class_1297;
import net.minecraft.class_1511;
import net.minecraft.class_2596;
import net.minecraft.class_2824;
import net.minecraft.class_1297.class_5529;
import net.minecraft.class_2824.class_5907;

public class CrystalOptimizer extends Module {
   private static CrystalOptimizer INSTANCE;

   public CrystalOptimizer() {
      super("Crystal Optimizer", "Stupid name but basically means set dead.", SubCategory.LEGIT, true);
      INSTANCE = this;
   }

   public static CrystalOptimizer getInstance() {
      return INSTANCE;
   }

   @Event
   public void onSent(PacketEvent.Sent event) {
      class_2596 var4 = event.packet;
      if (var4 instanceof class_2824) {
         class_2824 packet = (class_2824)var4;
         if (((AccessorInteractEntityC2SPacket)packet).getType().method_34211() == class_5907.field_29172) {
            class_1297 var5 = BlackOut.mc.field_1687.method_8469(((AccessorInteractEntityC2SPacket)packet).getId());
            if (var5 instanceof class_1511) {
               class_1511 entity = (class_1511)var5;
               BlackOut.mc.field_1687.method_2945(entity.method_5628(), class_5529.field_26998);
            }
         }
      }

   }
}
