package bodevelopment.client.blackout.module.modules.legit;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.RandomMode;
import bodevelopment.client.blackout.enums.SwingHand;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_239;
import net.minecraft.class_2879;
import net.minecraft.class_3532;
import net.minecraft.class_3966;
import net.minecraft.class_239.class_240;

public class Clicker extends Module {
   private final SettingGroup sgGeneral = this.addGroup("General");
   private long prev = 0L;
   private final Setting<RandomMode> randomise;
   private final Setting<Double> cps;
   private final Setting<Double> minCps;

   public Clicker() {
      super("Clicker", "Automatically clicks", SubCategory.LEGIT, true);
      this.randomise = this.sgGeneral.e("Randomise", RandomMode.Random, "Randomises CPS.");
      this.cps = this.sgGeneral.d("CPS", 14.0D, 0.0D, 20.0D, 0.1D, ".");
      this.minCps = this.sgGeneral.d("Min CPS", 10.0D, 0.0D, 20.0D, 0.1D, ".", () -> {
         return this.randomise.get() != RandomMode.Disabled;
      });
   }

   @Event
   public void onRender(TickEvent.Pre event) {
      if (BlackOut.mc.field_1724 != null) {
         if (BlackOut.mc.field_1690.field_1886.method_1434()) {
            if (this.delayCheck()) {
               this.sendPacket(new class_2879(class_1268.field_5808));
               this.clientSwing(SwingHand.MainHand, class_1268.field_5808);
               class_239 result = BlackOut.mc.field_1765;
               if (result == null || result.method_17783() != class_240.field_1331) {
                  return;
               }

               class_1297 entity = ((class_3966)result).method_17782();
               if (entity instanceof class_1309) {
                  class_1309 livingEntity = (class_1309)entity;
                  if (livingEntity.method_29504()) {
                     return;
                  }
               }

               BlackOut.mc.field_1761.method_2918(BlackOut.mc.field_1724, entity);
               this.prev = System.currentTimeMillis();
            }

         }
      }
   }

   private boolean delayCheck() {
      double d = this.randomise.get() == RandomMode.Disabled ? (Double)this.cps.get() : class_3532.method_16436(((RandomMode)this.randomise.get()).get(), (Double)this.cps.get(), (Double)this.minCps.get());
      return (double)(System.currentTimeMillis() - this.prev) > 1000.0D / d;
   }
}
