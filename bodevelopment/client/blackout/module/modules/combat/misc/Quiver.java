package bodevelopment.client.blackout.module.modules.combat.misc;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.RotationType;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.modules.misc.Simulation;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.Pair;
import bodevelopment.client.blackout.randomstuff.timers.TickTimerList;
import bodevelopment.client.blackout.util.InvUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.class_1268;
import net.minecraft.class_1291;
import net.minecraft.class_1293;
import net.minecraft.class_1703;
import net.minecraft.class_1713;
import net.minecraft.class_1723;
import net.minecraft.class_1744;
import net.minecraft.class_1753;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1844;
import net.minecraft.class_2653;

public class Quiver extends Module {
   private final SettingGroup sgGeneral = this.addGroup("General");
   public final Setting<Integer> charge;
   public final Setting<Integer> delay;
   public final Setting<Boolean> closeInv;
   public final Setting<Boolean> instantMove;
   public final Setting<Integer> durationLeft;
   public final Setting<Double> moveSpeed;
   public final Setting<Integer> retryTime;
   private final Setting<Boolean> instantRotate;
   private final List<Pair<class_1293, Integer>> arrows;
   private final List<Integer> actions;
   private final TickTimerList<class_1291> shot;
   private class_1291 currentEffect;
   public static boolean charging = false;
   private int timer;
   private boolean charged;
   private double movesLeft;

   public Quiver() {
      super("Quiver", "Shoots yourself with a bow to apply positive effects", SubCategory.MISC, true);
      this.charge = this.sgGeneral.i("Charge", 5, 0, 20, 1, "How long to charge until releasing.");
      this.delay = this.sgGeneral.i("Delay", 0, 0, 20, 1, "Waits for this many ticks before charging again.");
      this.closeInv = this.sgGeneral.b("Close Inventory", true, "Closes inventory after moving arrows.");
      this.instantMove = this.sgGeneral.b("Instant Move", true, ".");
      this.durationLeft = this.sgGeneral.i("Duration Left", 5, 0, 60, 1, ".");
      this.moveSpeed = this.sgGeneral.d("Move Speed", 20.0D, 0.0D, 20.0D, 0.2D, ".", () -> {
         return !(Boolean)this.instantMove.get();
      });
      this.retryTime = this.sgGeneral.i("Retry Time", 50, 0, 100, 1, ".");
      this.instantRotate = this.sgGeneral.b("Instant Rotate", true, "Ignores rotation speed limit.");
      this.arrows = new ArrayList();
      this.actions = new ArrayList();
      this.shot = new TickTimerList(false);
      this.currentEffect = null;
      this.timer = 0;
      this.charged = false;
      this.movesLeft = 0.0D;
   }

   @Event
   public void onTick(TickEvent.Pre event) {
      ++this.timer;
      this.movesLeft += (Double)this.moveSpeed.get() / 20.0D;
      this.shot.update();
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null && BlackOut.mc.field_1724.method_6047().method_7909() == class_1802.field_8102) {
         this.update();
      } else {
         charging = false;
      }

      this.movesLeft = Math.min(this.movesLeft, 1.0D);
   }

   private void update() {
      this.updateArrows();
      this.updateShooting();
   }

   private void updateShooting() {
      class_1799 stack = BlackOut.mc.field_1724.method_6047();
      if (stack != null && stack.method_7909() instanceof class_1753) {
         if (!this.updateMoving()) {
            if (this.charged) {
               this.charged = false;
               BlackOut.mc.field_1761.method_2897(BlackOut.mc.field_1724);
            }

         } else if (this.rotatePitch(-90.0F, RotationType.Other.withInstant((Boolean)this.instantRotate.get()), "bow")) {
            if (!BlackOut.mc.field_1724.method_6115() && this.timer > (Integer)this.delay.get()) {
               BlackOut.mc.field_1761.method_2919(BlackOut.mc.field_1724, class_1268.field_5808);
               charging = true;
               this.charged = true;
            }

            if (BlackOut.mc.field_1724.method_6048() >= (Integer)this.charge.get()) {
               BlackOut.mc.field_1761.method_2897(BlackOut.mc.field_1724);
               this.predict(this.getSlot(true));
               this.shot.add(this.currentEffect, (Integer)this.retryTime.get());
               charging = false;
               this.timer = 0;
               this.charged = false;
            }

         }
      }
   }

   private void predict(int slot) {
      if (slot >= 0 && Simulation.getInstance().quiverShoot()) {
         class_1703 handler = BlackOut.mc.field_1724.field_7512;
         class_1799 stack = handler.method_7611(slot).method_7677().method_7972();
         if (stack.method_7947() > 1) {
            stack.method_7939(stack.method_7947() - 1);
         } else {
            stack = class_1802.field_8162.method_7854();
         }

         Managers.PACKET.preApply(new class_2653(handler.field_7763, handler.method_37421(), slot, stack));
      }
   }

   private boolean updateMoving() {
      if (BlackOut.mc.field_1724.field_7512 instanceof class_1723 && !this.actions.isEmpty()) {
         while(true) {
            if (!(this.movesLeft > 0.0D) || this.actions.isEmpty()) {
               if (this.actions.isEmpty() && (Boolean)this.closeInv.get() && BlackOut.mc.field_1724.field_7512 instanceof class_1723) {
                  this.closeInventory();
               }
               break;
            }

            this.click((Integer)this.actions.get(0));
            this.actions.remove(0);
            --this.movesLeft;
         }
      } else {
         this.actions.clear();
      }

      int toShoot = this.getSlot(false);
      int all = this.getSlot(true);
      if (toShoot < 0) {
         return false;
      } else {
         if (toShoot != all) {
            if (!this.actions.isEmpty()) {
               return false;
            }

            if (!(BlackOut.mc.field_1724.field_7512 instanceof class_1723)) {
               return false;
            }

            this.move(toShoot);
            this.move(all);
            this.move(toShoot);
            if ((Boolean)this.closeInv.get() && (Boolean)this.instantMove.get() && BlackOut.mc.field_1724.field_7512 instanceof class_1723) {
               this.closeInventory();
            }
         }

         return true;
      }
   }

   private void move(int slot) {
      if ((Boolean)this.instantMove.get()) {
         this.click(slot);
      } else {
         this.actions.add(slot);
      }

   }

   private void click(int slot) {
      InvUtils.interactSlot(0, slot, 0, class_1713.field_7790);
   }

   private int getSlot(boolean first) {
      int slot = -1;
      Iterator var3 = this.arrows.iterator();

      while(true) {
         int s;
         while(true) {
            class_1293 effect;
            do {
               if (!var3.hasNext()) {
                  return slot;
               }

               Pair<class_1293, Integer> entry = (Pair)var3.next();
               effect = (class_1293)entry.method_15442();
               s = (Integer)entry.method_15441();
            } while(s > slot && slot > -1);

            if (first) {
               break;
            }

            if ((!BlackOut.mc.field_1724.method_6059(effect.method_5579()) || ((class_1293)BlackOut.mc.field_1724.method_6088().get(effect.method_5579())).method_5584() <= (Integer)this.durationLeft.get() * 20) && !this.shot.contains((Object)effect.method_5579()) && effect.method_5579().method_5573()) {
               this.currentEffect = effect.method_5579();
               break;
            }
         }

         slot = s;
      }
   }

   private void updateArrows() {
      this.arrows.clear();
      class_1703 var2 = BlackOut.mc.field_1724.field_7512;
      if (var2 instanceof class_1723) {
         class_1723 handler = (class_1723)var2;

         for(int i = 0; i < handler.field_7761.size(); ++i) {
            class_1799 stack = handler.method_7611(i).method_7677();
            if (stack != null && stack.method_7909() instanceof class_1744) {
               List<class_1293> effects = class_1844.method_8067(stack);
               if (!effects.isEmpty()) {
                  this.arrows.add(new Pair((class_1293)effects.get(0), i));
               }
            }
         }

      }
   }
}
