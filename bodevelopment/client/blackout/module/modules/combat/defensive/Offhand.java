package bodevelopment.client.blackout.module.modules.combat.defensive;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.EntityAddEvent;
import bodevelopment.client.blackout.event.events.MoveEvent;
import bodevelopment.client.blackout.event.events.PacketEvent;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.modules.combat.misc.Suicide;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.Hole;
import bodevelopment.client.blackout.randomstuff.timers.TimerMap;
import bodevelopment.client.blackout.util.BlockUtils;
import bodevelopment.client.blackout.util.BoxUtils;
import bodevelopment.client.blackout.util.DamageUtils;
import bodevelopment.client.blackout.util.HoleUtils;
import bodevelopment.client.blackout.util.InvUtils;
import bodevelopment.client.blackout.util.MovementPrediction;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.class_1297;
import net.minecraft.class_1511;
import net.minecraft.class_1703;
import net.minecraft.class_1713;
import net.minecraft.class_1723;
import net.minecraft.class_1735;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1829;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2620;
import net.minecraft.class_2846;
import net.minecraft.class_490;
import net.minecraft.class_2846.class_2847;

public class Offhand extends Module {
   private final SettingGroup sgItem = this.addGroup("Item");
   private final SettingGroup sgSwitch = this.addGroup("Switch");
   private final SettingGroup sgHealth = this.addGroup("Health");
   private final SettingGroup sgThreading = this.addGroup("Threading");
   private final Setting<Offhand.TotemMode> totemMode;
   private final Setting<Offhand.ItemMode> primary;
   private final Setting<Offhand.ItemMode> secondary;
   private final Setting<Boolean> swordGapple;
   private final Setting<Boolean> safeSwordGapple;
   private final Setting<Boolean> onlyInInventory;
   private final Setting<Offhand.SwitchMode> switchMode;
   private final Setting<Double> cooldown;
   private final Setting<Integer> latency;
   private final Setting<Boolean> prediction;
   private final Setting<Integer> hp;
   private final Setting<Integer> safeHealth;
   private final Setting<Boolean> mineCheck;
   private final Setting<Double> miningTime;
   private final Setting<Integer> holeHp;
   private final Setting<Integer> holeSafeHp;
   private final Setting<Double> safetyMultiplier;
   private final Setting<Boolean> render;
   private final Setting<Boolean> tickPre;
   private final Setting<Boolean> tickPost;
   private final Setting<Boolean> move;
   private final Setting<Boolean> crystalSpawn;
   private final TimerMap<Integer, class_2338> mining;
   private final List<class_238> prevPositions;
   private final Predicate<class_1799> totemPredicate;
   private long prevSwitch;
   private final TimerMap<Integer, Long> movedFrom;

   public Offhand() {
      super("Offhand", "Automatically puts items in offhand.", SubCategory.DEFENSIVE, true);
      this.totemMode = this.sgItem.e("Totem Mode", Offhand.TotemMode.Always, ".");
      this.primary = this.sgItem.e("Primary", Offhand.ItemMode.Crystal, ".", () -> {
         return this.totemMode.get() != Offhand.TotemMode.Always;
      });
      this.secondary = this.sgItem.e("Secondary", Offhand.ItemMode.Gapple, ".", () -> {
         return this.totemMode.get() != Offhand.TotemMode.Always && this.primary.get() != Offhand.ItemMode.Nothing;
      });
      this.swordGapple = this.sgItem.b("Sword Gapple", true, ".");
      this.safeSwordGapple = this.sgItem.b("Safe Sword Gapple", true, ".", () -> {
         return (Boolean)this.swordGapple.get() && this.totemMode.get() != Offhand.TotemMode.Never;
      });
      this.onlyInInventory = this.sgSwitch.b("Only In Inventory", false, ".");
      this.switchMode = this.sgSwitch.e("Switch Mode", Offhand.SwitchMode.FClick, ".");
      this.cooldown = this.sgSwitch.d("Cooldown", 0.2D, 0.0D, 1.0D, 0.01D, ".");
      this.latency = this.sgHealth.i("Latency", 0, 0, 10, 1, "");
      this.prediction = this.sgHealth.b("Prediction", true, ".");
      this.hp = this.sgHealth.i("Totem Health", 14, 0, 36, 1, ".");
      this.safeHealth = this.sgHealth.i("Safe Health", 18, 0, 36, 1, ".");
      this.mineCheck = this.sgHealth.b("Mine Check", true, ".");
      SettingGroup var10001 = this.sgHealth;
      Setting var10008 = this.mineCheck;
      Objects.requireNonNull(var10008);
      this.miningTime = var10001.d("Mining Time", 4.0D, 0.0D, 10.0D, 0.1D, ".", var10008::get);
      this.holeHp = this.sgHealth.i("Hole Health", 10, 0, 36, 1, ".");
      this.holeSafeHp = this.sgHealth.i("Hole Safe Health", 14, 0, 36, 1, ".");
      this.safetyMultiplier = this.sgHealth.d("Safety Multiplier", 1.0D, 0.0D, 5.0D, 0.05D, ".");
      this.render = this.sgThreading.b("Render", true, ".");
      this.tickPre = this.sgThreading.b("Tick Pre", true, ".");
      this.tickPost = this.sgThreading.b("Tick Post", true, ".");
      this.move = this.sgThreading.b("Move", true, ".");
      this.crystalSpawn = this.sgThreading.b("Crystal Spawn", true, ".");
      this.mining = new TimerMap(true);
      this.prevPositions = new ArrayList();
      this.totemPredicate = (stack) -> {
         return stack.method_31574(class_1802.field_8288);
      };
      this.prevSwitch = 0L;
      this.movedFrom = new TimerMap(true);
   }

   @Event
   public void onReceive(PacketEvent.Receive.Pre event) {
      if (BlackOut.mc.field_1687 != null) {
         class_2596 var3 = event.packet;
         if (var3 instanceof class_2620) {
            class_2620 packet = (class_2620)var3;
            if (BlockUtils.mineable(packet.method_11277())) {
               this.mining.remove((id, timer) -> {
                  return id == packet.method_11280();
               });
               this.mining.add(packet.method_11280(), packet.method_11277(), (Double)this.miningTime.get());
            }
         }
      }

   }

   @Event
   public void onRender(RenderEvent.World.Pre event) {
      if ((Boolean)this.render.get()) {
         this.update();
      }

   }

   @Event
   public void onTick(TickEvent.Pre event) {
      if ((Boolean)this.tickPre.get()) {
         this.update();
      }

   }

   @Event
   public void onTickPost(TickEvent.Post event) {
      if ((Boolean)this.tickPost.get()) {
         this.update();
      }

   }

   @Event
   public void onMove(MoveEvent.Pre event) {
      this.prevPositions.add(BlackOut.mc.field_1724.method_5829());
      OLEPOSSUtils.limitList(this.prevPositions, (Integer)this.latency.get());
      if ((Boolean)this.move.get()) {
         this.update();
      }

   }

   @Event
   public void onEntity(EntityAddEvent.Post event) {
      if (event.entity instanceof class_1511 && (Boolean)this.crystalSpawn.get()) {
         this.update();
      }

   }

   private void update() {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null && BlackOut.mc.field_1724.field_7512 instanceof class_1723) {
         if (!(Boolean)this.onlyInInventory.get() || BlackOut.mc.field_1755 instanceof class_490) {
            Predicate<class_1799> predicate = this.getItem();
            if (predicate != null) {
               if (!predicate.test(BlackOut.mc.field_1724.method_6079())) {
                  if (this.available(predicate)) {
                     this.doSwitch(predicate);
                  }

               }
            }
         }
      }
   }

   private void doSwitch(Predicate<class_1799> predicate) {
      if (!((double)(System.currentTimeMillis() - this.prevSwitch) < (Double)this.cooldown.get() * 1000.0D)) {
         class_1735 empty;
         switch((Offhand.SwitchMode)this.switchMode.get()) {
         case Basic:
            if (this.isPicked(predicate)) {
               this.clickSlot(45, 0, class_1713.field_7790);
            } else {
               empty = this.find(predicate);
               if (empty != null) {
                  this.clickSlot(empty.field_7874, 0, class_1713.field_7790);
                  this.clickSlot(45, 0, class_1713.field_7790);
                  this.addMoveTime(empty);
               }
            }

            if (this.anythingPicked()) {
               empty = this.findEmpty();
               if (empty != null) {
                  this.clickSlot(empty.field_7874, 0, class_1713.field_7790);
               }
            }

            this.prevSwitch = System.currentTimeMillis();
            this.closeInventory();
            break;
         case FClick:
            empty = this.find(predicate);
            if (empty != null) {
               this.clickSlot(empty.field_7874, 40, class_1713.field_7791);
               this.prevSwitch = System.currentTimeMillis();
               this.addMoveTime(empty);
            }

            if (!this.anythingPicked()) {
               this.closeInventory();
            }
            break;
         case Pick:
            empty = this.find(predicate);
            if (empty != null) {
               int selectedSlot = BlackOut.mc.field_1724.method_31548().field_7545;
               InvUtils.pickSwap(empty.method_34266());
               this.sendPacket(new class_2846(class_2847.field_12969, class_2338.field_10980, class_2350.field_11033));
               InvUtils.swap(selectedSlot);
               this.prevSwitch = System.currentTimeMillis();
               this.addMoveTime(empty);
            }
         }

      }
   }

   private void addMoveTime(class_1735 slot) {
      this.movedFrom.removeKey(slot.field_7874);
      this.movedFrom.add(slot.field_7874, System.currentTimeMillis(), 5.0D);
   }

   private class_1735 findEmpty() {
      for(int i = 9; i < 45; ++i) {
         class_1735 slot = BlackOut.mc.field_1724.field_7512.method_7611(i);
         if (slot.method_7677().method_7960()) {
            return slot;
         }
      }

      return null;
   }

   private class_1735 find(Predicate<class_1799> predicate) {
      List<class_1735> possible = new ArrayList();
      Iterator var3 = BlackOut.mc.field_1724.field_7512.field_7761.iterator();

      while(var3.hasNext()) {
         class_1735 slot = (class_1735)var3.next();
         if (predicate.test(slot.method_7677())) {
            possible.add(slot);
         }
      }

      Optional<class_1735> optional = possible.stream().min(Comparator.comparingLong((slotx) -> {
         return this.movedFrom.containsKey(slotx.field_7874) ? (Long)this.movedFrom.get(slotx.field_7874) : 0L;
      }));
      return (class_1735)optional.orElse((Object)null);
   }

   private void clickSlot(int id, int button, class_1713 actionType) {
      class_1703 handler = BlackOut.mc.field_1724.field_7512;
      BlackOut.mc.field_1761.method_2906(handler.field_7763, id, button, actionType, BlackOut.mc.field_1724);
   }

   private boolean isPicked(Predicate<class_1799> predicate) {
      return predicate.test(BlackOut.mc.field_1724.field_7512.method_34255());
   }

   private boolean anythingPicked() {
      return !BlackOut.mc.field_1724.field_7512.method_34255().method_7960();
   }

   private Predicate<class_1799> getItem() {
      boolean shouldSG = (Boolean)this.swordGapple.get() && BlackOut.mc.field_1690.field_1904.method_1434() && BlackOut.mc.field_1724.method_6047().method_7909() instanceof class_1829;
      if (!(Boolean)this.safeSwordGapple.get() && shouldSG) {
         return Offhand.ItemMode.Gapple.predicate;
      } else {
         switch((Offhand.TotemMode)this.totemMode.get()) {
         case Always:
            if ((Boolean)this.safeSwordGapple.get() && shouldSG) {
               return Offhand.ItemMode.Gapple.predicate;
            }

            return this.totemPredicate;
         case Danger:
            if (this.available(this.totemPredicate) && this.inDanger()) {
               return this.totemPredicate;
            }
         default:
            if ((Boolean)this.safeSwordGapple.get() && shouldSG) {
               return Offhand.ItemMode.Gapple.predicate;
            } else {
               Predicate<class_1799> primaryPredicate = ((Offhand.ItemMode)this.primary.get()).predicate;
               if (primaryPredicate == null) {
                  return null;
               } else if (this.available(primaryPredicate)) {
                  return primaryPredicate;
               } else {
                  Predicate<class_1799> secondaryPredicate = ((Offhand.ItemMode)this.secondary.get()).predicate;
                  return secondaryPredicate != null && this.available(secondaryPredicate) ? secondaryPredicate : null;
               }
            }
         }
      }
   }

   private boolean available(Predicate<class_1799> predicate) {
      return this.find(predicate) != null;
   }

   private boolean inDanger() {
      if (Suicide.getInstance().enabled && (Boolean)Suicide.getInstance().offHand.get()) {
         return false;
      } else {
         double health = (double)(BlackOut.mc.field_1724.method_6032() + BlackOut.mc.field_1724.method_6067());
         if (health <= this.getHealth()) {
            return true;
         } else {
            Iterator var3 = this.prevPositions.iterator();

            while(var3.hasNext()) {
               class_238 box = (class_238)var3.next();
               if (this.inDanger(box, health)) {
                  return true;
               }
            }

            if (this.inDanger(BlackOut.mc.field_1724.method_5829(), health)) {
               return true;
            } else {
               return (Boolean)this.prediction.get() && this.inDanger(this.predictedBox(), health);
            }
         }
      }
   }

   private class_238 predictedBox() {
      class_243 pos = MovementPrediction.predict(BlackOut.mc.field_1724);
      double lx = BlackOut.mc.field_1724.method_5829().method_17939();
      double lz = BlackOut.mc.field_1724.method_5829().method_17941();
      double height = BlackOut.mc.field_1724.method_5829().method_17940();
      return new class_238(pos.method_10216() - lx / 2.0D, pos.method_10214(), pos.method_10215() - lz / 2.0D, pos.method_10216() + lx / 2.0D, pos.method_10214() + height, pos.method_10215() + lz / 2.0D);
   }

   private boolean inDanger(class_238 box, double health) {
      Iterator var4 = BlackOut.mc.field_1687.method_18112().iterator();

      class_1297 entity;
      do {
         if (!var4.hasNext()) {
            return false;
         }

         entity = (class_1297)var4.next();
      } while(!(entity instanceof class_1511) || !(DamageUtils.crystalDamage(BlackOut.mc.field_1724, box, entity.method_19538()) * (Double)this.safetyMultiplier.get() >= health));

      return true;
   }

   private double getHealth() {
      boolean holdingTot = BlackOut.mc.field_1724.method_6079().method_31574(class_1802.field_8288);
      return this.isInHole() ? (double)(Integer)(holdingTot ? this.holeSafeHp : this.holeHp).get() : (double)(Integer)(holdingTot ? this.safeHealth : this.hp).get();
   }

   private boolean isInHole() {
      Iterator var1 = this.prevPositions.iterator();

      class_238 box;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         box = (class_238)var1.next();
      } while(this.isInHole(BoxUtils.feet(box)));

      return false;
   }

   private boolean isInHole(class_243 feet) {
      Hole hole = HoleUtils.currentHole(class_2338.method_49638(feet.method_1031(0.0D, 0.5D, 0.0D)));
      if (hole == null) {
         return false;
      } else {
         if ((Boolean)this.mineCheck.get()) {
            class_2338[] var3 = hole.positions;
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               class_2338 pos = var3[var5];
               if (this.mining.containsValue(pos)) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   public static enum TotemMode {
      Always,
      Danger,
      Never;

      // $FF: synthetic method
      private static Offhand.TotemMode[] $values() {
         return new Offhand.TotemMode[]{Always, Danger, Never};
      }
   }

   public static enum ItemMode {
      Nothing((Predicate)null),
      Crystal((stack) -> {
         return stack.method_31574(class_1802.field_8301);
      }),
      Exp((stack) -> {
         return stack.method_31574(class_1802.field_8287);
      }),
      Gapple(OLEPOSSUtils::isGapple),
      Bed(OLEPOSSUtils::isBed),
      Obsidian((stack) -> {
         return stack.method_31574(class_1802.field_8281);
      });

      private final Predicate<class_1799> predicate;

      private ItemMode(Predicate<class_1799> predicate) {
         this.predicate = predicate;
      }

      // $FF: synthetic method
      private static Offhand.ItemMode[] $values() {
         return new Offhand.ItemMode[]{Nothing, Crystal, Exp, Gapple, Bed, Obsidian};
      }
   }

   public static enum SwitchMode {
      Basic,
      FClick,
      Pick;

      // $FF: synthetic method
      private static Offhand.SwitchMode[] $values() {
         return new Offhand.SwitchMode[]{Basic, FClick, Pick};
      }
   }
}
