package bodevelopment.client.blackout.module.modules.combat.defensive;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.RotationType;
import bodevelopment.client.blackout.enums.SwingHand;
import bodevelopment.client.blackout.enums.SwitchMode;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.modules.combat.misc.Suicide;
import bodevelopment.client.blackout.module.modules.combat.offensive.AutoCrystal;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.FindResult;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.class_1268;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2338;
import net.minecraft.class_742;

public class AutoMend extends Module {
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final SettingGroup sgPause = this.addGroup("Pause");
   private final SettingGroup sgRender = this.addGroup("Render");
   private final Setting<Boolean> antiCharity;
   private final Setting<Double> throwSpeed;
   private final Setting<Integer> bottles;
   private final Setting<SwitchMode> switchMode;
   private final Setting<Integer> minDur;
   private final Setting<Integer> antiWaste;
   private final Setting<Integer> forceMend;
   private final Setting<Boolean> instantRotate;
   private final Setting<Integer> autoCrystalPause;
   private final Setting<Integer> surroundPause;
   private final Setting<Integer> movePause;
   private final Setting<Integer> airPause;
   private final Setting<Boolean> renderSwing;
   private final Setting<SwingHand> swingHand;
   private double throwsLeft;
   private class_2338 lastPos;
   private boolean throwing;
   private int acTimer;
   private int surroundTimer;
   private int selfTrapTimer;
   private int moveTimer;
   private int offGroundTimer;

   public AutoMend() {
      super("Auto Mend", "Automatically mends your armor with experience bottles.", SubCategory.DEFENSIVE, true);
      this.antiCharity = this.sgGeneral.b("Anti Charity", true, "Doesn't mend if any enemy is at same position.");
      this.throwSpeed = this.sgGeneral.d("Throw Speed", 20.0D, 0.0D, 20.0D, 0.2D, "How many timer to throw every second. 20 is recommended.");
      this.bottles = this.sgGeneral.i("Bottles", 1, 1, 10, 1, "Amount of bottles to throw every time.");
      this.switchMode = this.sgGeneral.e("Switch Mode", SwitchMode.Silent, "Method of switching. Silent is the most reliable.");
      this.minDur = this.sgGeneral.i("Min Durability", 75, 0, 100, 1, "Mends if any armor piece is under this durability.");
      this.antiWaste = this.sgGeneral.i("Anti Waste", 90, 0, 100, 1, "Doesn't use experience if any armor piece is above this durability.");
      this.forceMend = this.sgGeneral.i("Force Mend", 30, 0, 100, 1, "Ignores anti waste if any armor piece if under this durability.");
      this.instantRotate = this.sgGeneral.b("Instant Rotate", true, "Ignores rotation speed limit.");
      this.autoCrystalPause = this.sgPause.i("Auto Crystal Pause", 0, 0, 100, 1, "Pauses for x ticks if auto crystal places.");
      this.surroundPause = this.sgPause.i("Surround Pause", 0, 0, 100, 1, "Pauses for x ticks if surround places.");
      this.movePause = this.sgPause.i("Move Pause", 0, 0, 100, 1, "Pauses for x ticks if moved.");
      this.airPause = this.sgPause.i("Air Pause", 0, 0, 100, 1, "Pauses for x ticks if off ground.");
      this.renderSwing = this.sgRender.b("Render Swing", true, "Renders swing animation when throwing an exp bottle.");
      this.swingHand = this.sgRender.e("Swing Hand", SwingHand.RealHand, "Which hand should be swung.");
      this.throwsLeft = 0.0D;
      this.lastPos = null;
      this.throwing = false;
      this.acTimer = 0;
      this.surroundTimer = 0;
      this.selfTrapTimer = 0;
      this.moveTimer = 0;
      this.offGroundTimer = 0;
   }

   @Event
   public void onRender(RenderEvent.World.Post event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         if (AutoCrystal.getInstance().placing) {
            this.acTimer = (Integer)this.autoCrystalPause.get();
         }

         if (Surround.getInstance().placing) {
            this.surroundTimer = (Integer)this.surroundPause.get();
         }

         if (!BlackOut.mc.field_1724.method_24515().equals(this.lastPos)) {
            this.lastPos = BlackOut.mc.field_1724.method_24515();
            this.moveTimer = (Integer)this.movePause.get();
         }

         if (!BlackOut.mc.field_1724.method_24828()) {
            this.offGroundTimer = (Integer)this.airPause.get();
         }

      }
   }

   @Event
   public void onTick(TickEvent.Pre event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null && !Suicide.getInstance().enabled) {
         this.throwsLeft += (Double)this.throwSpeed.get() / 20.0D;
         this.updateTimers();
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
      if (this.shouldThrow() && bottlesLeft >= 1) {
         this.throwing = true;
         if (this.rotate(Managers.ROTATION.nextYaw, 90.0F, RotationType.Other.withInstant((Boolean)this.instantRotate.get()), "throwing")) {
            if (hand == null) {
               switched = ((SwitchMode)this.switchMode.get()).swap(result.slot());
            }

            if (hand != null || switched) {
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
         this.throwing = false;
         this.end("throwing");
      }
   }

   private boolean shouldThrow() {
      return this.shouldMend() && this.acTimer <= 0 && this.surroundTimer <= 0 && this.selfTrapTimer <= 0 && this.moveTimer <= 0 && this.offGroundTimer <= 0;
   }

   private void updateTimers() {
      --this.acTimer;
      --this.surroundTimer;
      --this.selfTrapTimer;
      --this.moveTimer;
      --this.offGroundTimer;
   }

   private boolean shouldMend() {
      List<class_1799> armors = new ArrayList();

      for(int i = 0; i < 4; ++i) {
         armors.add(BlackOut.mc.field_1724.method_31548().method_7372(i));
      }

      float max = -1.0F;
      float lowest = 500.0F;
      Iterator var5 = armors.iterator();

      while(var5.hasNext()) {
         class_1799 stack = (class_1799)var5.next();
         float dur = (float)(stack.method_7936() - stack.method_7919()) / (float)stack.method_7936() * 100.0F;
         if (dur > max) {
            max = dur;
         }

         if (dur < lowest) {
            lowest = dur;
         }
      }

      if (lowest <= (float)(Integer)this.forceMend.get()) {
         return true;
      } else if ((Boolean)this.antiCharity.get() && this.playerAtPos()) {
         return false;
      } else if (max >= (float)(Integer)this.antiWaste.get()) {
         return false;
      } else {
         return lowest <= (float)(Integer)this.minDur.get() || this.throwing;
      }
   }

   private boolean playerAtPos() {
      Iterator var1 = BlackOut.mc.field_1687.method_18456().iterator();

      class_742 player;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         player = (class_742)var1.next();
      } while(player == BlackOut.mc.field_1724 || Managers.FRIENDS.isFriend(player) || !player.method_24515().equals(BlackOut.mc.field_1724.method_24515()));

      return true;
   }

   private void throwBottle(class_1268 hand) {
      this.useItem(hand);
      if ((Boolean)this.renderSwing.get()) {
         this.clientSwing((SwingHand)this.swingHand.get(), hand);
      }

   }
}
