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
import bodevelopment.client.blackout.module.modules.combat.offensive.AutoCrystal;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.FindResult;
import bodevelopment.client.blackout.util.DamageUtils;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import java.util.Iterator;
import java.util.function.Predicate;
import net.minecraft.class_1268;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_1511;
import net.minecraft.class_1799;
import net.minecraft.class_1844;
import net.minecraft.class_2338;
import net.minecraft.class_4537;

public class AutoPot extends Module {
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final SettingGroup sgHealth = this.addGroup("Health");
   private final SettingGroup sgPause = this.addGroup("Pause");
   private final SettingGroup sgRender = this.addGroup("Render");
   private final Setting<Double> throwSpeed;
   private final Setting<Integer> bottles;
   private final Setting<SwitchMode> switchMode;
   private final Setting<Integer> throwTicks;
   private final Setting<Boolean> instantRotate;
   private final Setting<Integer> health;
   private final Setting<Boolean> safe;
   private final Setting<Double> safeHealth;
   private final Setting<Integer> maxExisted;
   private final Setting<Integer> autoCrystalPause;
   private final Setting<Integer> surroundPause;
   private final Setting<Integer> movePause;
   private final Setting<Integer> airPause;
   private final Setting<Boolean> renderSwing;
   private final Setting<SwingHand> swingHand;
   private double throwsLeft;
   private class_2338 lastPos;
   private int acTimer;
   private int surroundTimer;
   private int moveTimer;
   private int offGroundTimer;
   private int throwTimer;
   private boolean switched;
   private FindResult result;
   private final Predicate<class_1799> healthPred;

   public AutoPot() {
      super("Auto Pot", "Automatically throws potions.", SubCategory.DEFENSIVE, true);
      this.throwSpeed = this.sgGeneral.d("Throw Speed", 20.0D, 0.0D, 20.0D, 0.2D, "How many timer to throw every second. 20 is recommended.");
      this.bottles = this.sgGeneral.i("Bottles", 1, 1, 10, 1, "Amount of bottles to throw every time.");
      this.switchMode = this.sgGeneral.e("Switch Mode", SwitchMode.Silent, "Method of switching. Silent is the most reliable.");
      this.throwTicks = this.sgGeneral.i("Throw Ticks", 1, 1, 20, 1, "Doesn't use experience if any armor piece is above this durability.");
      this.instantRotate = this.sgGeneral.b("Instant Rotate", true, "Ignores rotation speed limit.");
      this.health = this.sgHealth.i("Health", 75, 0, 100, 1, "Mends if any armor piece is under this durability.");
      this.safe = this.sgHealth.b("Safe", true, "Doesn't use experience if any armor piece is above this durability.");
      this.safeHealth = this.sgHealth.d("Safe Health", 5.0D, 0.0D, 36.0D, 0.1D, ".");
      this.maxExisted = this.sgHealth.i("Max Existed", 10, 0, 100, 1, ".");
      this.autoCrystalPause = this.sgPause.i("Auto Crystal Pause", 0, 0, 100, 1, "Pauses for x ticks if auto crystal places.");
      this.surroundPause = this.sgPause.i("Surround Pause", 0, 0, 100, 1, "Pauses for x ticks if surround places.");
      this.movePause = this.sgPause.i("Move Pause", 0, 0, 100, 1, "Pauses for x ticks if moved.");
      this.airPause = this.sgPause.i("Air Pause", 0, 0, 100, 1, "Pauses for x ticks if off ground.");
      this.renderSwing = this.sgRender.b("Render Swing", true, "Renders swing animation when throwing an exp bottle.");
      this.swingHand = this.sgRender.e("Swing Hand", SwingHand.RealHand, "Which hand should be swung.");
      this.throwsLeft = 0.0D;
      this.lastPos = null;
      this.acTimer = 0;
      this.surroundTimer = 0;
      this.moveTimer = 0;
      this.offGroundTimer = 0;
      this.throwTimer = 0;
      this.switched = false;
      this.result = null;
      this.healthPred = (stack) -> {
         if (!(stack.method_7909() instanceof class_4537)) {
            return false;
         } else {
            Iterator var1 = class_1844.method_8067(stack).iterator();

            class_1293 instance;
            do {
               if (!var1.hasNext()) {
                  return false;
               }

               instance = (class_1293)var1.next();
            } while(instance.method_5579() != class_1294.field_5915);

            return true;
         }
      };
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
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         this.throwsLeft += (Double)this.throwSpeed.get() / 20.0D;
         this.updateTimers();
         this.update();
         this.throwsLeft = Math.min(1.0D, this.throwsLeft);
      }
   }

   private void update() {
      class_1268 hand = OLEPOSSUtils.getHand(this.healthPred);
      this.result = null;
      int bottlesLeft = 0;
      if (hand == null) {
         if ((this.result = ((SwitchMode)this.switchMode.get()).find(this.healthPred)).wasFound()) {
            bottlesLeft = Math.min((int)Math.floor(this.throwsLeft), this.result.amount());
         }
      } else {
         int b = hand == class_1268.field_5808 ? Managers.PACKET.getStack().method_7947() : BlackOut.mc.field_1724.method_6079().method_7947();
         bottlesLeft = Math.min((int)Math.floor(this.throwsLeft), b);
      }

      if (this.shouldThrow() && (bottlesLeft = Math.min(bottlesLeft, (Integer)this.bottles.get())) > 0) {
         this.throwTimer = (Integer)this.throwTicks.get();
      }

      if (this.throwTimer <= 0) {
         this.end("throwing");
      } else {
         this.switched = false;
         if (this.rotatePitch(90.0F, RotationType.Other.withInstant((Boolean)this.instantRotate.get()), "throwing")) {
            while(bottlesLeft > 0) {
               this.throwBottle(hand);
               --bottlesLeft;
               --this.throwsLeft;
            }

            if (this.switched) {
               ((SwitchMode)this.switchMode.get()).swapBack();
            }

         }
      }
   }

   private boolean shouldThrow() {
      return this.shouldHeal() && this.acTimer <= 0 && this.surroundTimer <= 0 && this.moveTimer <= 0 && this.offGroundTimer <= 0;
   }

   private void updateTimers() {
      --this.acTimer;
      --this.surroundTimer;
      --this.moveTimer;
      --this.offGroundTimer;
      --this.throwTimer;
   }

   private boolean shouldHeal() {
      if (BlackOut.mc.field_1724.method_6032() <= (float)(Integer)this.health.get()) {
         return true;
      } else {
         return (Boolean)this.safe.get() && this.inDanger();
      }
   }

   private boolean inDanger() {
      Iterator var1 = BlackOut.mc.field_1687.method_18112().iterator();

      while(var1.hasNext()) {
         class_1297 entity = (class_1297)var1.next();
         if (entity instanceof class_1511 && entity.field_6012 <= (Integer)this.maxExisted.get()) {
            double damage = DamageUtils.crystalDamage(BlackOut.mc.field_1724, BlackOut.mc.field_1724.method_5829(), entity.method_19538());
            if ((double)BlackOut.mc.field_1724.method_6032() - damage <= (Double)this.safeHealth.get()) {
               return true;
            }
         }
      }

      return false;
   }

   private void throwBottle(class_1268 hand) {
      if (hand != null || (this.switched = ((SwitchMode)this.switchMode.get()).swap(this.result.slot()))) {
         this.useItem(hand);
         if ((Boolean)this.renderSwing.get()) {
            this.clientSwing((SwingHand)this.swingHand.get(), hand);
         }

      }
   }
}
