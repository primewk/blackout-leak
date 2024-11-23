package bodevelopment.client.blackout.module.modules.combat.defensive;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.ObsidianModule;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import bodevelopment.client.blackout.util.RotationUtils;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import net.minecraft.class_1297;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_3532;
import net.minecraft.class_742;
import net.minecraft.class_2828.class_2829;

public class Surround extends ObsidianModule {
   private static Surround INSTANCE;
   private final SettingGroup sgToggle = this.addGroup("Toggle");
   private final Setting<Boolean> center;
   private final Setting<Boolean> smartCenter;
   private final Setting<Boolean> phaseCenter;
   private final Setting<Boolean> extend;
   private final Setting<Boolean> toggleMove;
   private final Setting<Surround.VerticalToggleMode> toggleVertical;
   private final Setting<Double> singleCooldown;
   private final Setting<Boolean> antiCev;
   private class_2338 lastPos;
   private boolean centered;
   private class_2338 currentPos;
   private final Map<class_742, Long> blockedSince;
   private final class_2350[] directions;
   public boolean placing;

   public Surround() {
      super("Surround", "Places blocks around your legs to protect from explosions.", SubCategory.DEFENSIVE);
      this.center = this.sgGeneral.b("Center", false, "Moves to block center before surrounding.");
      SettingGroup var10001 = this.sgGeneral;
      Setting var10005 = this.center;
      Objects.requireNonNull(var10005);
      this.smartCenter = var10001.b("Smart Center", true, "Only moves until whole hitbox is inside target block.", var10005::get);
      var10001 = this.sgGeneral;
      var10005 = this.center;
      Objects.requireNonNull(var10005);
      this.phaseCenter = var10001.b("Phase Center", true, "Doesn't center if clipped inside a block.", var10005::get);
      this.extend = this.sgGeneral.b("Extend", true, ".");
      this.toggleMove = this.sgToggle.b("Toggle Move", false, "Toggles if you move horizontally.");
      this.toggleVertical = this.sgToggle.e("Toggle Vertical", Surround.VerticalToggleMode.Up, "Toggles the module if you move vertically.");
      this.singleCooldown = this.sgSpeed.d("Single Cooldown", 0.05D, 0.0D, 1.0D, 0.01D, "Waits x seconds before trying to place at the same position if there is 1 missing block.");
      var10001 = this.sgAttack;
      var10005 = this.attack;
      Objects.requireNonNull(var10005);
      this.antiCev = var10001.b("Anti CEV", false, "Attacks crystals placed on surround blocks.", var10005::get);
      this.lastPos = null;
      this.centered = false;
      this.currentPos = null;
      this.blockedSince = new HashMap();
      this.directions = new class_2350[]{class_2350.field_11043, class_2350.field_11035, class_2350.field_11034, class_2350.field_11039, class_2350.field_11033};
      this.placing = false;
      INSTANCE = this;
   }

   public static Surround getInstance() {
      return INSTANCE;
   }

   public void onEnable() {
      this.centered = false;
      this.lastPos = this.getPos();
      this.currentPos = this.getPos();
      super.onEnable();
   }

   protected boolean preCalc() {
      this.lastPos = this.currentPos;
      this.currentPos = this.getPos();
      this.placing = false;
      this.setBB();
      return this.checkToggle();
   }

   protected boolean validForBlocking(class_1297 entity) {
      if ((Boolean)this.antiCev.get()) {
         Iterator var2 = this.blockPlacements.iterator();

         while(var2.hasNext()) {
            class_2338 pos = (class_2338)var2.next();
            if (entity.method_24515().equals(pos.method_10084())) {
               return true;
            }
         }
      }

      return super.validForBlocking(entity);
   }

   protected double getCooldown() {
      return this.oneMissing() ? (Double)this.singleCooldown.get() : (Double)this.cooldown.get();
   }

   protected void addInsideBlocks() {
      this.addBlocks(BlackOut.mc.field_1724, this.getSize(BlackOut.mc.field_1724));
      this.blockPlacements.clear();
      this.addPlacements();
      if ((Boolean)this.extend.get()) {
         BlackOut.mc.field_1687.method_18456().stream().filter((player) -> {
            return BlackOut.mc.field_1724.method_5739(player) < 5.0F && player != BlackOut.mc.field_1724;
         }).sorted(Comparator.comparingDouble((player) -> {
            return (double)BlackOut.mc.field_1724.method_5739(player);
         })).forEach((player) -> {
            if (this.intersects(player)) {
               if (System.currentTimeMillis() - (Long)this.blockedSince.computeIfAbsent(player, (p) -> {
                  return System.currentTimeMillis();
               }) >= 200L) {
                  this.addBlocks(player, this.getSize(player));
               }
            } else {
               this.blockedSince.remove(player);
            }
         });
      }

      this.blockedSince.entrySet().removeIf((entry) -> {
         return System.currentTimeMillis() - (Long)entry.getValue() > 60000L;
      });
   }

   protected void addPlacements() {
      this.insideBlocks.forEach((pos) -> {
         class_2350[] var2 = this.directions;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            class_2350 dir = var2[var4];
            if (!this.blockPlacements.contains(pos.method_10093(dir)) && !this.insideBlocks.contains(pos.method_10093(dir))) {
               this.blockPlacements.add(pos.method_10093(dir));
            }
         }

      });
   }

   protected void addBlocks(class_1297 entity, int[] size) {
      class_2338 pos = entity.method_24515();

      for(int x = size[0]; x <= size[1]; ++x) {
         for(int z = size[2]; z <= size[3]; ++z) {
            class_2338 p = pos.method_10069(x, 0, z);
            if ((!(BlackOut.mc.field_1687.method_8320(p).method_26204().method_9520() > 600.0F) || p.equals(this.currentPos)) && !this.insideBlocks.contains(p.method_33096(this.currentPos.method_10264()))) {
               this.insideBlocks.add(p.method_33096(this.currentPos.method_10264()));
            }
         }
      }

   }

   private void setBB() {
      if (!this.centered && (Boolean)this.center.get() && BlackOut.mc.field_1724.method_24828() && (!(Boolean)this.phaseCenter.get() || !OLEPOSSUtils.inside(BlackOut.mc.field_1724, BlackOut.mc.field_1724.method_5829().method_35580(0.01D, 0.01D, 0.01D)))) {
         double targetX;
         double targetZ;
         if ((Boolean)this.smartCenter.get()) {
            targetX = class_3532.method_15350(BlackOut.mc.field_1724.method_23317(), (double)this.currentPos.method_10263() + 0.31D, (double)this.currentPos.method_10263() + 0.69D);
            targetZ = class_3532.method_15350(BlackOut.mc.field_1724.method_23321(), (double)this.currentPos.method_10260() + 0.31D, (double)this.currentPos.method_10260() + 0.69D);
         } else {
            targetX = (double)this.currentPos.method_10263() + 0.5D;
            targetZ = (double)this.currentPos.method_10260() + 0.5D;
         }

         double dist = (new class_243(targetX, 0.0D, targetZ)).method_1022(new class_243(BlackOut.mc.field_1724.method_23317(), 0.0D, BlackOut.mc.field_1724.method_23321()));
         if (dist < 0.2873D) {
            this.sendPacket(new class_2829(targetX, BlackOut.mc.field_1724.method_23318(), targetZ, Managers.PACKET.isOnGround()));
         }

         double x = BlackOut.mc.field_1724.method_23317();
         double z = BlackOut.mc.field_1724.method_23321();

         for(int i = 0; (double)i < Math.ceil(dist / 0.2873D); ++i) {
            double yaw = RotationUtils.getYaw(BlackOut.mc.field_1724.method_33571(), new class_243(targetX, 0.0D, targetZ), 0.0D) + 90.0D;
            x += Math.cos(Math.toRadians(yaw)) * 0.2873D;
            z += Math.sin(Math.toRadians(yaw)) * 0.2873D;
            this.sendPacket(new class_2829(x, BlackOut.mc.field_1724.method_23318(), z, Managers.PACKET.isOnGround()));
         }

         BlackOut.mc.field_1724.method_23327(targetX, BlackOut.mc.field_1724.method_23318(), targetZ);
         BlackOut.mc.field_1724.method_5857(new class_238(targetX - 0.3D, BlackOut.mc.field_1724.method_23318(), targetZ - 0.3D, targetX + 0.3D, BlackOut.mc.field_1724.method_23318() + (BlackOut.mc.field_1724.method_5829().field_1325 - BlackOut.mc.field_1724.method_5829().field_1322), targetZ + 0.3D));
         this.centered = true;
      }

   }

   private boolean checkToggle() {
      if (this.lastPos != null) {
         if ((Boolean)this.toggleMove.get() && (this.currentPos.method_10263() != this.lastPos.method_10263() || this.currentPos.method_10260() != this.lastPos.method_10260())) {
            this.disable(this.getDisplayName() + " disabled moved horizontally");
            return true;
         }

         if ((this.toggleVertical.get() == Surround.VerticalToggleMode.Up || this.toggleVertical.get() == Surround.VerticalToggleMode.Any) && this.currentPos.method_10264() > this.lastPos.method_10264()) {
            this.disable(this.getDisplayName() + " disabled moved up");
            return true;
         }

         if ((this.toggleVertical.get() == Surround.VerticalToggleMode.Down || this.toggleVertical.get() == Surround.VerticalToggleMode.Any) && this.currentPos.method_10264() < this.lastPos.method_10264()) {
            this.disable(this.getDisplayName() + " disabled moved down");
            return true;
         }
      }

      return false;
   }

   private boolean oneMissing() {
      boolean alreadyFound = false;
      Iterator var2 = this.blockPlacements.iterator();

      while(var2.hasNext()) {
         class_2338 pos = (class_2338)var2.next();
         if (OLEPOSSUtils.replaceable(pos)) {
            if (alreadyFound) {
               return false;
            }

            alreadyFound = true;
         }
      }

      return true;
   }

   public static enum VerticalToggleMode {
      Disabled,
      Up,
      Down,
      Any;

      // $FF: synthetic method
      private static Surround.VerticalToggleMode[] $values() {
         return new Surround.VerticalToggleMode[]{Disabled, Up, Down, Any};
      }
   }

   public static enum PlaceDelayMode {
      Ticks,
      Seconds;

      // $FF: synthetic method
      private static Surround.PlaceDelayMode[] $values() {
         return new Surround.PlaceDelayMode[]{Ticks, Seconds};
      }
   }
}
