package bodevelopment.client.blackout.module.modules.legit;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.SwingHand;
import bodevelopment.client.blackout.enums.SwitchMode;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.InteractBlockEvent;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.FindResult;
import java.util.Objects;
import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2338;
import net.minecraft.class_239;
import net.minecraft.class_2879;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import net.minecraft.class_239.class_240;

public class HitCrystal extends Module {
   private static HitCrystal INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<SwitchMode> switchMode;
   private final Setting<Integer> postPlace;
   private final Setting<Integer> preCrystal;
   private final Setting<Boolean> multiCrystal;
   private final Setting<Double> speed;
   private final Setting<Double> attackSpeed;
   private final Setting<Boolean> attack;
   private int timer;
   private class_2338 pos;
   private boolean placed;
   private boolean attacked;
   private double places;
   private double attacks;

   public HitCrystal() {
      super("Hit Crystal", "Places a crystal on top of obsidian.", SubCategory.LEGIT, true);
      this.switchMode = this.sgGeneral.e("Switch Mode", SwitchMode.Normal, "Method of switching.");
      this.postPlace = this.sgGeneral.i("Post Place Ticks", 1, 0, 20, 1, ".");
      this.preCrystal = this.sgGeneral.i("Pre Crystal Ticks", 1, 0, 20, 1, ".");
      this.multiCrystal = this.sgGeneral.b("Multi Crystal", true, ".");
      SettingGroup var10001 = this.sgGeneral;
      Setting var10008 = this.multiCrystal;
      Objects.requireNonNull(var10008);
      this.speed = var10001.d("Speed", 10.0D, 0.0D, 20.0D, 1.0D, ".", var10008::get);
      this.attackSpeed = this.sgGeneral.d("Attack Speed", 20.0D, 0.0D, 20.0D, 1.0D, ".");
      this.attack = this.sgGeneral.b("Attack", true, ".");
      this.timer = -1;
      this.pos = null;
      this.placed = false;
      this.attacked = false;
      this.places = 0.0D;
      this.attacks = 0.0D;
      INSTANCE = this;
   }

   public static HitCrystal getInstance() {
      return INSTANCE;
   }

   @Event
   public void onPlace(InteractBlockEvent event) {
      class_1799 stack = event.hand == class_1268.field_5808 ? Managers.PACKET.getStack() : BlackOut.mc.field_1724.method_6079();
      if (stack.method_31574(class_1802.field_8281)) {
         this.timer = 0;
         this.placed = false;
         this.attacked = false;
         this.pos = event.hitResult.method_17777().method_10093(event.hitResult.method_17780());
      }

   }

   public void onTick() {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         this.places += (Double)this.speed.get() / 20.0D;
         this.attacks += (Double)this.attackSpeed.get() / 20.0D;
         if (this.timer >= 0) {
            if (++this.timer <= (Integer)this.postPlace.get() + (Integer)this.preCrystal.get() + 10) {
               this.updateAttacking();
               this.updatePlacing();
            } else {
               this.timer = -1;
            }
         }

         this.places = Math.min(this.places, 1.0D);
         this.attacks = Math.min(this.attacks, 1.0D);
      }
   }

   private void updatePlacing() {
      if (this.pos != null) {
         class_239 var2 = BlackOut.mc.field_1765;
         if (var2 instanceof class_3965) {
            class_3965 hitResult = (class_3965)var2;
            if ((Boolean)this.multiCrystal.get() || !this.placed) {
               if (hitResult.method_17783() != class_240.field_1333) {
                  if (hitResult.method_17777().equals(this.pos)) {
                     while(this.places > 0.0D) {
                        this.placeCrystal(hitResult);
                        --this.places;
                     }

                  }
               }
            }
         }
      }
   }

   private void updateAttacking() {
      if ((Boolean)this.attack.get()) {
         if (this.pos != null) {
            class_239 var2 = BlackOut.mc.field_1765;
            if (var2 instanceof class_3966) {
               class_3966 entityHitResult = (class_3966)var2;
               if (!(Boolean)this.multiCrystal.get() || !this.attacked) {
                  if (entityHitResult.method_17783() != class_240.field_1333) {
                     if (entityHitResult.method_17782().method_24515().equals(this.pos.method_10084())) {
                        if (this.timer > (Integer)this.postPlace.get() + (Integer)this.preCrystal.get()) {
                           BlackOut.mc.field_1761.method_2918(BlackOut.mc.field_1724, entityHitResult.method_17782());
                           BlackOut.mc.method_1562().method_52787(new class_2879(class_1268.field_5808));
                           this.clientSwing(SwingHand.RealHand, class_1268.field_5808);
                           this.attacked = true;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void placeCrystal(class_3965 hitResult) {
      class_1268 hand = null;
      if (Managers.PACKET.getStack().method_7909() == class_1802.field_8301) {
         hand = class_1268.field_5808;
      }

      FindResult result = ((SwitchMode)this.switchMode.get()).find(class_1802.field_8301);
      boolean switched = false;
      if (this.timer >= (Integer)this.postPlace.get()) {
         if (hand != null || result.wasFound() && (switched = ((SwitchMode)this.switchMode.get()).swap(result.slot()))) {
            if (this.timer >= (Integer)this.postPlace.get() + (Integer)this.preCrystal.get()) {
               hand = hand == null ? class_1268.field_5808 : hand;
               class_1269 actionResult = BlackOut.mc.field_1761.method_2896(BlackOut.mc.field_1724, hand, hitResult);
               if (actionResult.method_23666()) {
                  BlackOut.mc.method_1562().method_52787(new class_2879(hand));
               }

               this.clientSwing(SwingHand.RealHand, hand);
               this.placed = true;
               if (switched) {
                  ((SwitchMode)this.switchMode.get()).swapBack();
               }

            }
         }
      }
   }
}
