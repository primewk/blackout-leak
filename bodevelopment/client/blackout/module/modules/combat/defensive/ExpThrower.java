package bodevelopment.client.blackout.module.modules.combat.defensive;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.RotationType;
import bodevelopment.client.blackout.enums.SwingHand;
import bodevelopment.client.blackout.enums.SwitchMode;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.FindResult;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import java.util.Iterator;
import java.util.Objects;
import net.minecraft.class_1268;
import net.minecraft.class_1799;
import net.minecraft.class_1802;

public class ExpThrower extends Module {
   private static ExpThrower INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final SettingGroup sgRender = this.addGroup("Render");
   private final Setting<Double> throwSpeed;
   private final Setting<Integer> bottles;
   private final Setting<SwitchMode> switchMode;
   private final Setting<Integer> antiWaste;
   private final Setting<Integer> forceMend;
   private final Setting<Boolean> rotate;
   private final Setting<Boolean> instantRotate;
   private final Setting<Boolean> renderSwing;
   private final Setting<SwingHand> swingHand;
   private double throwsLeft;

   public ExpThrower() {
      super("Exp Thrower", "Automatically throws exp bottles.", SubCategory.DEFENSIVE, true);
      this.throwSpeed = this.sgGeneral.d("Throw Speed", 20.0D, 0.0D, 20.0D, 0.2D, "How many timer to throw every second. 20 is recommended.");
      this.bottles = this.sgGeneral.i("Bottles", 1, 1, 10, 1, "Amount of bottles to throw every time.");
      this.switchMode = this.sgGeneral.e("Switch Mode", SwitchMode.Silent, "Method of switching. Silent is the most reliable.");
      this.antiWaste = this.sgGeneral.i("Anti Waste", 90, 0, 100, 1, "Doesn't use experience if any armor piece is above this durability.");
      this.forceMend = this.sgGeneral.i("Force Mend", 30, 0, 100, 1, "Ignores anti waste if any armor piece if under this durability.");
      this.rotate = this.sgGeneral.b("Rotate", true, "Looks down.");
      SettingGroup var10001 = this.sgGeneral;
      Setting var10005 = this.rotate;
      Objects.requireNonNull(var10005);
      this.instantRotate = var10001.b("Instant Rotate", true, "Ignores rotation speed limit.", var10005::get);
      this.renderSwing = this.sgRender.b("Render Swing", true, "Renders swing animation when throwing an exp bottle.");
      this.swingHand = this.sgRender.e("Swing Hand", SwingHand.RealHand, "Which hand should be swung.");
      this.throwsLeft = 0.0D;
      INSTANCE = this;
   }

   public static ExpThrower getInstance() {
      return INSTANCE;
   }

   @Event
   public void onTick(TickEvent.Pre event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         this.throwsLeft += (Double)this.throwSpeed.get() / 20.0D;
         this.update();
         this.throwsLeft = Math.min(1.0D, this.throwsLeft);
      }
   }

   private void update() {
      class_1268 hand = OLEPOSSUtils.getHand(class_1802.field_8287);
      FindResult result = null;
      boolean switched = false;
      int bottlesLeft = 0;
      if (hand == null) {
         result = ((SwitchMode)this.switchMode.get()).find(class_1802.field_8287);
         if (result.wasFound()) {
            bottlesLeft = Math.min((int)Math.floor(this.throwsLeft), result.amount());
         }
      } else {
         int b = hand == class_1268.field_5808 ? Managers.PACKET.getStack().method_7947() : BlackOut.mc.field_1724.method_6079().method_7947();
         bottlesLeft = Math.min((int)Math.floor(this.throwsLeft), b);
      }

      bottlesLeft = Math.min(bottlesLeft, (Integer)this.bottles.get());
      if (this.shouldMend() && bottlesLeft >= 1) {
         if (!(Boolean)this.rotate.get() || this.rotate(Managers.ROTATION.nextYaw, 90.0F, RotationType.Other.withInstant((Boolean)this.instantRotate.get()), "throwing")) {
            if (hand != null || (switched = ((SwitchMode)this.switchMode.get()).swap(result.slot()))) {
               while(bottlesLeft > 0) {
                  this.throwBottle(hand);
                  --bottlesLeft;
                  --this.throwsLeft;
               }

               if (switched) {
                  ((SwitchMode)this.switchMode.get()).swapBack();
               }

            }
         }
      } else {
         this.end("throwing");
      }
   }

   private boolean shouldMend() {
      float max = -1.0F;
      float lowest = 500.0F;
      boolean found = false;
      Iterator var5 = BlackOut.mc.field_1724.method_5661().iterator();

      while(var5.hasNext()) {
         class_1799 stack = (class_1799)var5.next();
         if (!stack.method_7960() && stack.method_7963()) {
            found = true;
            float dur = (float)(stack.method_7936() - stack.method_7919()) / (float)stack.method_7936() * 100.0F;
            if (dur > max) {
               max = dur;
            }

            if (dur < lowest) {
               lowest = dur;
            }
         }
      }

      if (!found) {
         return false;
      } else if (lowest <= (float)(Integer)this.forceMend.get()) {
         return true;
      } else {
         return max < (float)(Integer)this.antiWaste.get();
      }
   }

   private void throwBottle(class_1268 hand) {
      this.useItem(hand);
      if ((Boolean)this.renderSwing.get()) {
         this.clientSwing((SwingHand)this.swingHand.get(), hand);
      }

   }
}
