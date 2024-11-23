package bodevelopment.client.blackout.module.modules.combat.offensive;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.RandomMode;
import bodevelopment.client.blackout.enums.RotationType;
import bodevelopment.client.blackout.enums.SwingHand;
import bodevelopment.client.blackout.enums.SwingState;
import bodevelopment.client.blackout.enums.SwingType;
import bodevelopment.client.blackout.enums.SwitchMode;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.GameJoinEvent;
import bodevelopment.client.blackout.event.events.PacketEvent;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.interfaces.mixin.IRaycastContext;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.MoveUpdateModule;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.modules.client.Notifications;
import bodevelopment.client.blackout.module.modules.combat.misc.AntiBot;
import bodevelopment.client.blackout.module.modules.combat.misc.Teams;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.module.setting.multisettings.BoxMultiSetting;
import bodevelopment.client.blackout.randomstuff.ExtrapolationMap;
import bodevelopment.client.blackout.randomstuff.timers.RenderList;
import bodevelopment.client.blackout.util.DamageUtils;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import bodevelopment.client.blackout.util.RotationUtils;
import bodevelopment.client.blackout.util.SettingUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_1511;
import net.minecraft.class_1657;
import net.minecraft.class_1743;
import net.minecraft.class_1799;
import net.minecraft.class_1829;
import net.minecraft.class_1831;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_2398;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2824;
import net.minecraft.class_2846;
import net.minecraft.class_2848;
import net.minecraft.class_2886;
import net.minecraft.class_3532;
import net.minecraft.class_418;
import net.minecraft.class_4587;
import net.minecraft.class_5134;
import net.minecraft.class_742;
import net.minecraft.class_7833;
import net.minecraft.class_239.class_240;
import net.minecraft.class_2828.class_2829;
import net.minecraft.class_2846.class_2847;
import net.minecraft.class_2848.class_2849;

public class Aura extends MoveUpdateModule {
   private static Aura INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final SettingGroup sgTeleport = this.addGroup("Teleport");
   private final SettingGroup sgBlocking = this.addGroup("Blocking");
   private final SettingGroup sgDelay = this.addGroup("Delay");
   private final SettingGroup sgRender = this.addGroup("Render");
   private final Setting<Aura.TargetMode> targetMode;
   private final Setting<Boolean> checkMaxHP;
   private final Setting<Integer> maxHp;
   private final Setting<SwitchMode> switchMode;
   private final Setting<Boolean> onlyWeapon;
   private final Setting<Boolean> ignoreNaked;
   private final Setting<Boolean> tpDisable;
   private final Setting<Aura.RotationMode> rotationMode;
   private final Setting<List<class_1299<?>>> entities;
   private final Setting<Double> hitChance;
   private final Setting<Double> expand;
   private final Setting<Integer> extrapolation;
   private final Setting<Boolean> disableDead;
   private final Setting<Boolean> ignoreRanges;
   private final Setting<Double> hitHeight;
   private final Setting<Double> dynamicHeight;
   private final Setting<Boolean> critSprint;
   private final Setting<Double> scanRange;
   private final Setting<Double> wallScanRange;
   private final Setting<Boolean> teleport;
   private final Setting<Integer> maxPackets;
   private final Setting<Double> maxDistance;
   private final Setting<Boolean> tpBack;
   private final Setting<Boolean> blocking;
   private final Setting<Aura.BlockMode> block;
   private final Setting<Aura.BlockRenderMode> blockRender;
   private final Setting<Double> speed;
   private final Setting<Aura.DelayMode> delayMode;
   private final Setting<Integer> packets;
   private final Setting<RandomMode> randomise;
   private final Setting<Double> maxCps;
   private final Setting<Double> minCps;
   private final Setting<Double> cpsSetting;
   private final Setting<Boolean> fatigueSim;
   private final Setting<Integer> maxFatigue;
   private final Setting<Integer> fatigueRaise;
   private final Setting<Integer> fatigueDecrease;
   private final Setting<Double> charge;
   private final Setting<Double> minDelay;
   private final Setting<Double> randomNegative;
   private final Setting<Double> randomPositive;
   private final Setting<Boolean> critSync;
   private final Setting<Double> critVelocity;
   private final Setting<Boolean> hitParticles;
   private final Setting<Boolean> swing;
   private final Setting<SwingHand> swingHand;
   private final Setting<Aura.RenderMode> renderMode;
   private final Setting<Double> renderTime;
   private final BoxMultiSetting rendering;
   private boolean shouldRender;
   private long prevAttack;
   private long nextBlock;
   public boolean isBlocking;
   private double alwaysRenderTime;
   private float f;
   private int fatigue;
   private int timeOG;
   private double random;
   private final RenderList<class_238> renderBoxes;
   private final ExtrapolationMap extrapolationMap;
   private volatile class_1297 target;
   private class_238 renderBox;
   public static class_742 targetedPlayer = null;

   public Aura() {
      super("Aura", "Pokes people automatically.", SubCategory.OFFENSIVE);
      this.targetMode = this.sgGeneral.e("Mode", Aura.TargetMode.Health, "How to pick the target", () -> {
         return true;
      });
      this.checkMaxHP = this.sgGeneral.b("Check Max HP", false, "Checks if target has too much hp.");
      SettingGroup var10001 = this.sgGeneral;
      Setting var10008 = this.checkMaxHP;
      Objects.requireNonNull(var10008);
      this.maxHp = var10001.i("Max HP", 36, 0, 100, 1, "Target's health must be under this value.", var10008::get);
      this.switchMode = this.sgGeneral.e("Switch mode", SwitchMode.Disabled, "How to switch to the sword", () -> {
         return true;
      });
      this.onlyWeapon = this.sgGeneral.b("Only Weapon", true, "Only attacks with weapons");
      this.ignoreNaked = this.sgGeneral.b("Ignore naked", false, "Doesn't hit naked players");
      this.tpDisable = this.sgGeneral.b("Disable on TP", false, "Should we disable when teleporting to another world");
      this.rotationMode = this.sgGeneral.e("Rotation mode", Aura.RotationMode.OnHit, "When should we rotate. Only active if attack rotations are enabled in rotation settings.", () -> {
         return true;
      });
      this.entities = this.sgGeneral.el("Entities", ".", class_1299.field_6097);
      this.hitChance = this.sgGeneral.d("Hit Chance", 1.0D, 0.0D, 1.0D, 0.01D, ".");
      this.expand = this.sgGeneral.d("Expand", 0.0D, 0.0D, 1.0D, 0.01D, ".");
      this.extrapolation = this.sgGeneral.i("Extrapolation", 1, 0, 3, 1, ".");
      this.disableDead = this.sgGeneral.b("Disable Dead", false, "Disables the module if you die");
      this.ignoreRanges = this.sgGeneral.b("Ignore Ranges", false, "Might be useful in cpvp.");
      this.hitHeight = this.sgGeneral.d("Hit Height", 0.8D, 0.0D, 1.0D, 0.01D, ".");
      this.dynamicHeight = this.sgGeneral.d("Dynamic Height", 0.5D, 0.0D, 1.0D, 0.01D, ".");
      this.critSprint = this.sgGeneral.b("Crit Sprint", true, "Sends stop sprint packet before hitting to make sure you crit.");
      this.scanRange = this.sgGeneral.d("Scan Range", 0.0D, 0.0D, 10.0D, 0.1D, ".");
      this.wallScanRange = this.sgGeneral.d("Wall Scan Range", 0.0D, 0.0D, 10.0D, 0.1D, ".");
      this.teleport = this.sgTeleport.b("Teleport", false, ".");
      this.maxPackets = this.sgTeleport.i("Max Packets", 1, 1, 10, 1, "Maximum amount of tp packets to send (each direction).");
      this.maxDistance = this.sgTeleport.d("Max Distance", 5.0D, 1.0D, 50.0D, 0.5D, ".");
      this.tpBack = this.sgTeleport.b("TP Back", false, ".");
      this.blocking = this.sgBlocking.b("Blocking", false, ".");
      var10001 = this.sgBlocking;
      Aura.BlockMode var10003 = Aura.BlockMode.Hold;
      Setting var10005 = this.blocking;
      Objects.requireNonNull(var10005);
      this.block = var10001.e("Block Mode", var10003, "Blocks with a sword.", var10005::get);
      var10001 = this.sgBlocking;
      Aura.BlockRenderMode var1 = Aura.BlockRenderMode.Disabled;
      var10005 = this.blocking;
      Objects.requireNonNull(var10005);
      this.blockRender = var10001.e("Block Render", var1, ".", var10005::get);
      this.speed = this.sgBlocking.d("Anim Speed", 0.5D, 0.0D, 1.0D, 0.01D, ".", () -> {
         return (Boolean)this.blocking.get() && this.blockRender.get() == Aura.BlockRenderMode.Fan || this.blockRender.get() == Aura.BlockRenderMode.Float || this.blockRender.get() == Aura.BlockRenderMode.Slap;
      });
      this.delayMode = this.sgDelay.e("Delay Mode", Aura.DelayMode.Smart, ".");
      this.packets = this.sgDelay.i("Packets", 1, 1, 10, 1, ".");
      this.randomise = this.sgDelay.e("Randomise", RandomMode.Random, "Randomises CPS.", () -> {
         return this.delayMode.get() == Aura.DelayMode.Basic;
      });
      this.maxCps = this.sgDelay.d("Max CPS", 12.0D, 0.0D, 20.0D, 0.1D, ".", () -> {
         return this.delayMode.get() == Aura.DelayMode.Basic && this.randomise.get() != RandomMode.Disabled;
      });
      this.minCps = this.sgDelay.d("Min CPS", 8.0D, 0.0D, 20.0D, 0.1D, ".", () -> {
         return this.delayMode.get() == Aura.DelayMode.Basic && this.randomise.get() != RandomMode.Disabled;
      });
      this.cpsSetting = this.sgDelay.d("CPS", 15.0D, 0.0D, 20.0D, 0.1D, ".", () -> {
         return this.delayMode.get() == Aura.DelayMode.Basic && this.randomise.get() == RandomMode.Disabled;
      });
      this.fatigueSim = this.sgDelay.b("Simulate Fatigue", false, ".", () -> {
         return this.delayMode.get() == Aura.DelayMode.Basic;
      });
      this.maxFatigue = this.sgDelay.i("Max Fatigue", 50, 0, 1000, 1, "Max added delay to clicks (milliseconds)", () -> {
         return this.delayMode.get() == Aura.DelayMode.Basic && (Boolean)this.fatigueSim.get();
      });
      this.fatigueRaise = this.sgDelay.i("Fatigue Raise", 5, 0, 1000, 1, ".", () -> {
         return this.delayMode.get() == Aura.DelayMode.Basic && (Boolean)this.fatigueSim.get();
      });
      this.fatigueDecrease = this.sgDelay.i("Fatigue Decrease", 2, 0, 1000, 1, ".", () -> {
         return this.delayMode.get() == Aura.DelayMode.Basic && (Boolean)this.fatigueSim.get();
      });
      this.charge = this.sgDelay.d("Charge", 1.0D, 0.0D, 1.0D, 0.01D, ".", () -> {
         return this.delayMode.get() == Aura.DelayMode.Vanilla;
      });
      this.minDelay = this.sgDelay.d("Min Delay", 0.5D, 0.0D, 1.0D, 0.01D, ".", () -> {
         return this.delayMode.get() == Aura.DelayMode.Smart || this.delayMode.get() == Aura.DelayMode.Vanilla;
      });
      this.randomNegative = this.sgDelay.d("Negative Random", 0.0D, 0.0D, 1.0D, 0.01D, ".", () -> {
         return this.delayMode.get() == Aura.DelayMode.Smart || this.delayMode.get() == Aura.DelayMode.Vanilla;
      });
      this.randomPositive = this.sgDelay.d("Positive Random", 0.0D, 0.0D, 1.0D, 0.01D, ".", () -> {
         return this.delayMode.get() == Aura.DelayMode.Smart || this.delayMode.get() == Aura.DelayMode.Vanilla;
      });
      this.critSync = this.sgDelay.b("Crit Sync", true, "Delays attacks if you would fall down soon.");
      var10001 = this.sgDelay;
      var10008 = this.critSync;
      Objects.requireNonNull(var10008);
      this.critVelocity = var10001.d("Crit Velocity", 0.1D, 0.0D, 1.0D, 0.01D, "Attacks when you have reached -x y velocity.", var10008::get);
      this.hitParticles = this.sgRender.b("Hit Particles", false, "Spawn particles when hitting enemy.");
      this.swing = this.sgRender.b("Swing", true, "Renders swing animation when attacking an entity.");
      var10001 = this.sgRender;
      SwingHand var2 = SwingHand.RealHand;
      var10005 = this.swing;
      Objects.requireNonNull(var10005);
      this.swingHand = var10001.e("Swing Hand", var2, "Which hand should be swung.", var10005::get);
      this.renderMode = this.sgRender.e("Render Mode", Aura.RenderMode.Hit, ".");
      this.renderTime = this.sgRender.d("Render Time", 1.0D, 0.0D, 10.0D, 0.1D, ".");
      this.rendering = BoxMultiSetting.of(this.sgRender, "Box");
      this.shouldRender = false;
      this.prevAttack = 0L;
      this.nextBlock = 0L;
      this.isBlocking = false;
      this.alwaysRenderTime = 0.0D;
      this.f = 0.0F;
      this.fatigue = 0;
      this.timeOG = 0;
      this.renderBoxes = RenderList.getList(false);
      this.extrapolationMap = new ExtrapolationMap();
      this.target = null;
      this.renderBox = new class_238(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
      INSTANCE = this;
   }

   public static Aura getInstance() {
      return INSTANCE;
   }

   public void onEnable() {
      this.end("attacking");
   }

   public void onDisable() {
      if (this.isBlocking) {
         this.stopBlocking();
      }

   }

   public String getInfo() {
      return this.target == null ? null : (this.target.method_5477().getString().length() > 16 ? "Attacking" : this.target.method_5477().getString());
   }

   public boolean shouldSkipListeners() {
      return false;
   }

   @Event
   public void onGameJoin(GameJoinEvent event) {
      if ((Boolean)this.tpDisable.get()) {
         this.disable(this.getDisplayName() + " was disabled due to server change/teleport", 5, Notifications.Type.Info);
      }

   }

   public void onTickPre(TickEvent.Pre event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         if (BlackOut.mc.field_1724.method_24828()) {
            ++this.timeOG;
         } else {
            this.timeOG = 0;
         }

         if (!this.holdingSword() && this.isBlocking) {
            this.stopBlocking();
         }

         if (this.enabled) {
            super.onTickPre(event);
            if ((Boolean)this.disableDead.get() && BlackOut.mc.field_1755 instanceof class_418) {
               this.disable(this.getDisplayName() + " was disabled due to dying");
            } else if (this.holdingSword()) {
               if (this.target == null) {
                  if (this.isBlocking) {
                     this.stopBlocking();
                  }

               } else if (this.block.get() == Aura.BlockMode.Hold) {
                  if (this.isBlocking && System.currentTimeMillis() > this.nextBlock) {
                     this.nextBlock = System.currentTimeMillis() + 500L;
                     this.stopBlocking();
                     if (SettingUtils.grimPackets() && (Boolean)this.blocking.get()) {
                        this.startBlocking();
                     }
                  } else if (!this.isBlocking && (Boolean)this.blocking.get()) {
                     this.startBlocking();
                  }

               }
            }
         }
      } else {
         this.isBlocking = false;
      }
   }

   @Event
   public void onSend(PacketEvent.Sent event) {
      if (!(Boolean)this.blocking.get()) {
         this.isBlocking = false;
      } else {
         if (event.packet instanceof class_2886 && this.holdingSword()) {
            this.isBlocking = true;
         }

         class_2596 var3 = event.packet;
         if (var3 instanceof class_2846) {
            class_2846 packet = (class_2846)var3;
            if (packet.method_12363() == class_2847.field_12974) {
               this.isBlocking = false;
            }
         }

      }
   }

   @Event
   public void onRender(RenderEvent.World.Post event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         this.renderBoxes.update((box, delta, time) -> {
            this.rendering.render(box, (float)(1.0D - delta), 1.0F);
         });
         if (!this.enabled) {
            this.renderSingle(false, event.frameTime);
         } else {
            this.updateTarget();
            if (this.target != null && this.shouldRender) {
               this.renderBox = this.getBox(this.target);
               class_243 offset = this.target.method_19538().method_1023(this.target.field_6014, this.target.field_6036, this.target.field_5969).method_1021((double)BlackOut.mc.method_1488());
               this.renderBox = this.renderBox.method_997(offset);
               class_1297 var4 = this.target;
               if (var4 instanceof class_742) {
                  class_742 player = (class_742)var4;
                  targetedPlayer = player;
               } else {
                  targetedPlayer = null;
               }
            } else {
               targetedPlayer = null;
            }

            this.renderSingle(this.shouldRender, event.frameTime);
         }
      }
   }

   private void renderSingle(boolean refresh, double frameTime) {
      if (this.renderMode.get() == Aura.RenderMode.Always && this.renderBox != null) {
         if (refresh) {
            this.alwaysRenderTime = (Double)this.renderTime.get();
         } else {
            this.alwaysRenderTime -= frameTime;
         }

         double progress = class_3532.method_15350(this.alwaysRenderTime / (Double)this.renderTime.get(), 0.0D, 1.0D);
         this.rendering.render(this.renderBox, (float)progress, (float)progress);
      }
   }

   protected void update(boolean allowAction, boolean fakePos) {
      if (this.target == null) {
         this.fatigue = Math.max(this.fatigue - (Integer)this.fatigueDecrease.get(), 0);
      }

      this.shouldRender = false;
      if (this.target != null && BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null && this.enabled) {
         int slot = this.bestSlot(((SwitchMode)this.switchMode.get()).inventory);
         boolean holding = !(Boolean)this.onlyWeapon.get() || BlackOut.mc.field_1724.method_6047().method_7909() instanceof class_1831;
         if (slot >= 0) {
            if (!(Boolean)this.onlyWeapon.get() || BlackOut.mc.field_1724.method_31548().method_5438(slot).method_7909() instanceof class_1831) {
               if (holding || this.switchMode.get() != SwitchMode.Disabled) {
                  this.shouldRender = true;
                  boolean rotated = this.rotationMode.get() != Aura.RotationMode.Constant || !SettingUtils.shouldRotate(RotationType.Attacking) || this.attackRotate(this.getBox(this.target), this.getRotationVec(), "attacking");
                  if (rotated && this.delayCheck()) {
                     if (this.rotationMode.get() != Aura.RotationMode.OnHit || !SettingUtils.shouldRotate(RotationType.Attacking) || this.attackRotate(this.getBox(this.target), this.getRotationVec(), "attacking")) {
                        if (this.inRange(this.target)) {
                           if (allowAction) {
                              boolean switched = false;
                              if (holding || (switched = ((SwitchMode)this.switchMode.get()).swap(slot))) {
                                 this.fatigue += (Integer)this.fatigueRaise.get();
                                 this.attackTarget();
                                 if (switched) {
                                    ((SwitchMode)this.switchMode.get()).swapBack();
                                 }

                                 if (this.rotationMode.get() == Aura.RotationMode.OnHit) {
                                    this.end("attacking");
                                 }

                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private class_243 getRotationVec() {
      double x = class_3532.method_15350(this.target.method_18798().field_1352 + BlackOut.mc.field_1724.method_18798().field_1352, -0.3D, 0.3D);
      double y = class_3532.method_15350(this.getHitHeight(), this.target.method_5829().field_1322, this.target.method_5829().field_1325);
      double z = class_3532.method_15350(this.target.method_18798().field_1350 + BlackOut.mc.field_1724.method_18798().field_1350, -0.3D, 0.3D);
      return new class_243(this.target.method_23317() + x, y, this.target.method_23321() + z);
   }

   private double getHitHeight() {
      double targetY = class_3532.method_16436((Double)this.hitHeight.get(), this.target.method_5829().field_1322, this.target.method_5829().field_1325);
      return class_3532.method_16436((Double)this.dynamicHeight.get(), targetY, BlackOut.mc.field_1724.method_23320());
   }

   private boolean delayCheck() {
      if ((Boolean)this.critSync.get() && this.timeOG < 5 && this.shouldWaitCrit()) {
         return false;
      } else {
         double timeSince = (double)(System.currentTimeMillis() - this.prevAttack) / 1000.0D;
         switch((Aura.DelayMode)this.delayMode.get()) {
         case Basic:
            return timeSince > this.getDelay();
         case Smart:
            double delay = Math.max(1.0D / BlackOut.mc.field_1724.method_26825(class_5134.field_23723), (Double)this.minDelay.get());
            return timeSince > this.getRandom(delay - (Double)this.randomNegative.get(), delay + (Double)this.randomPositive.get());
         case Vanilla:
            return timeSince >= (Double)this.minDelay.get() && (double)BlackOut.mc.field_1724.field_6273 >= 20.0D / BlackOut.mc.field_1724.method_26825(class_5134.field_23723) * (Double)this.charge.get();
         default:
            return true;
         }
      }
   }

   private double getRandom(double start, double end) {
      return class_3532.method_16436(this.random, start, end);
   }

   private boolean shouldWaitCrit() {
      if (!(Boolean)this.critSprint.get() && BlackOut.mc.field_1724.method_5624()) {
         return false;
      } else {
         return BlackOut.mc.field_1724.method_24828() || BlackOut.mc.field_1724.field_6017 <= 0.0F || BlackOut.mc.field_1724.method_18798().field_1351 >= -(Double)this.critVelocity.get();
      }
   }

   private double getDelay() {
      double cps = this.randomise.get() == RandomMode.Disabled ? (Double)this.cpsSetting.get() : this.getRandom((Double)this.minCps.get(), (Double)this.maxCps.get());
      return 1.0D / cps + this.getFatigue() / 1000.0D;
   }

   private double getFatigue() {
      if (!(Boolean)this.fatigueSim.get()) {
         return 0.0D;
      } else {
         long f = Math.min((long)this.fatigue, ((Integer)this.maxFatigue.get()).longValue());
         return (Boolean)this.fatigueSim.get() ? (double)f : 0.0D;
      }
   }

   private void attackTarget() {
      if (this.holdingSword() && this.block.get() == Aura.BlockMode.Spam && (Boolean)this.blocking.get()) {
         this.stopBlocking();
      }

      this.prevAttack = System.currentTimeMillis();
      this.random = ((RandomMode)this.randomise.get()).get();
      SettingUtils.swing(SwingState.Pre, SwingType.Attacking, class_1268.field_5808);
      List<class_243> positions = !SettingUtils.inAttackRange(this.target.method_5829()) && (Boolean)this.teleport.get() ? this.getPath(this.target) : null;
      if (positions != null) {
         positions.forEach((posx) -> {
            this.sendInstantly(new class_2829(posx.method_10216(), posx.method_10214(), posx.method_10215(), false));
         });
         if ((Boolean)this.tpBack.get()) {
            BlackOut.mc.field_1724.method_33574((class_243)positions.get(positions.size() - 1));
         }
      }

      if (this.chanceCheck()) {
         boolean shouldCritSprint = (Boolean)this.critSprint.get() && !BlackOut.mc.field_1724.method_24828() && BlackOut.mc.field_1724.field_6017 > 0.0F && BlackOut.mc.field_1724.method_5624();
         if (shouldCritSprint) {
            this.sendPacket(new class_2848(BlackOut.mc.field_1724, class_2849.field_12985));
         }

         for(int i = 0; i < (Integer)this.packets.get(); ++i) {
            this.sendPacket(class_2824.method_34206(this.target, BlackOut.mc.field_1724.method_5715()));
         }

         if (shouldCritSprint) {
            this.sendPacket(new class_2848(BlackOut.mc.field_1724, class_2849.field_12981));
         }

         if (this.target instanceof class_1511) {
            Managers.ENTITY.setSemiDead(this.target.method_5628());
         }

         this.spawnParticles();
      }

      BlackOut.mc.field_1724.field_6273 = 0;
      SettingUtils.swing(SwingState.Post, SwingType.Attacking, class_1268.field_5808);
      if (positions != null && (Boolean)this.tpBack.get()) {
         for(int i = positions.size() - 2; i >= 0; --i) {
            class_243 pos = (class_243)positions.get(i);
            this.sendInstantly(new class_2829(pos.method_10216(), pos.method_10214(), pos.method_10215(), false));
         }

         this.sendInstantly(new class_2829(BlackOut.mc.field_1724.method_23317(), BlackOut.mc.field_1724.method_23318(), BlackOut.mc.field_1724.method_23321(), false));
      }

      if (this.holdingSword() && this.block.get() == Aura.BlockMode.Spam && (Boolean)this.blocking.get()) {
         this.startBlocking();
      }

      if (this.renderMode.get() == Aura.RenderMode.Hit) {
         this.renderBoxes.add(this.renderBox, (Double)this.renderTime.get());
      }

      if ((Boolean)this.swing.get()) {
         this.clientSwing((SwingHand)this.swingHand.get(), class_1268.field_5808);
      }

   }

   private boolean holdingSword() {
      return Managers.PACKET.getStack().method_7909() instanceof class_1829;
   }

   private boolean chanceCheck() {
      return Math.random() <= (Double)this.hitChance.get();
   }

   private void spawnParticles() {
      if ((Boolean)this.hitParticles.get()) {
         BlackOut.mc.field_1713.method_3061(this.target, class_2398.field_11205);
      }

   }

   private int bestSlot(boolean inventory) {
      int slot = -1;
      double hDmg = -1.0D;

      for(int i = 0; i < (inventory ? BlackOut.mc.field_1724.method_31548().method_5439() + 1 : 9); ++i) {
         class_1799 stack = BlackOut.mc.field_1724.method_31548().method_5438(i);
         if (!(Boolean)this.onlyWeapon.get() || stack.method_7909() instanceof class_1829 || stack.method_7909() instanceof class_1743) {
            double dmg = DamageUtils.itemDamage(stack);
            if (dmg > hDmg) {
               slot = i;
               hDmg = dmg;
            }
         }
      }

      return slot;
   }

   private void updateTarget() {
      AtomicReference<Double> value = new AtomicReference(0.0D);
      this.target = null;
      this.extrapolationMap.update((entity) -> {
         return (Integer)this.extrapolation.get();
      });
      BlackOut.mc.field_1687.method_18112().forEach((entity) -> {
         if (((List)this.entities.get()).contains(entity.method_5864()) && entity != BlackOut.mc.field_1724) {
            double distance = (double)BlackOut.mc.field_1724.method_5739(entity);
            if ((Boolean)this.teleport.get()) {
               if (distance > (double)(Integer)this.maxPackets.get() * (Double)this.maxDistance.get()) {
                  return;
               }
            } else if (distance > 10.0D) {
               return;
            }

            double var10000;
            class_1309 livingEntity;
            switch((Aura.TargetMode)this.targetMode.get()) {
            case Health:
               if (entity instanceof class_1309) {
                  livingEntity = (class_1309)entity;
                  var10000 = (double)(10000.0F - livingEntity.method_6032() - livingEntity.method_6067());
               } else {
                  var10000 = 50.0D;
               }
               break;
            case Angle:
               var10000 = 10000.0D - Math.abs(RotationUtils.yawAngle((double)BlackOut.mc.field_1724.method_36454(), RotationUtils.getYaw(entity)));
               break;
            case Distance:
               var10000 = 10000.0D - BlackOut.mc.field_1724.method_19538().method_1022(entity.method_19538());
               break;
            default:
               throw new IncompatibleClassChangeError();
            }

            double val = var10000;
            if (!(val <= (Double)value.get())) {
               if (entity instanceof class_1309) {
                  livingEntity = (class_1309)entity;
                  if (livingEntity.method_6032() <= 0.0F) {
                     return;
                  }

                  if (livingEntity.method_7325()) {
                     return;
                  }

                  if (!this.inScanRange(entity) && !this.inRange(entity)) {
                     return;
                  }
               }

               if (entity instanceof class_742) {
                  class_742 player = (class_742)entity;
                  AntiBot antiBot = AntiBot.getInstance();
                  Teams teams = Teams.getInstance();
                  if (antiBot.enabled && antiBot.mode.get() == AntiBot.HandlingMode.Ignore && antiBot.getBots().contains(player)) {
                     return;
                  }

                  if (teams.enabled && teams.isTeammate(player)) {
                     return;
                  }

                  if ((Boolean)this.ignoreNaked.get() && !this.getArmor(player)) {
                     return;
                  }

                  if ((Boolean)this.checkMaxHP.get() && player.method_6032() + player.method_6067() > (float)(Integer)this.maxHp.get()) {
                     return;
                  }

                  if (Managers.FRIENDS.isFriend(player)) {
                     return;
                  }
               }

               if (val > (Double)value.get()) {
                  this.target = entity;
                  value.set(val);
               }

            }
         }
      });
   }

   private boolean inScanRange(class_1297 entity) {
      return SettingUtils.attackRangeTo(entity.method_5829(), (class_243)null) <= SettingUtils.attackTrace(entity.method_5829()) ? (Double)this.scanRange.get() : (Double)this.wallScanRange.get();
   }

   private boolean inRange(class_1297 entity) {
      if ((Boolean)this.ignoreRanges.get()) {
         if (BlackOut.mc.field_1724.method_5739(entity) < 8.0F) {
            return true;
         }
      } else if (SettingUtils.inAttackRange(entity.method_5829())) {
         return true;
      }

      return (Boolean)this.teleport.get() && this.canTeleport(entity);
   }

   private void startBlocking() {
      this.sendSequenced((s) -> {
         return new class_2886(class_1268.field_5808, s);
      });
   }

   private void stopBlocking() {
      this.sendPacket(new class_2846(class_2847.field_12974, class_2338.field_10980, class_2350.field_11033, 0));
   }

   private class_238 getBox(class_1297 entity) {
      class_238 box = this.extrapolationMap.get(entity);
      return (Double)this.expand.get() > 0.0D ? this.expandHitbox(box, entity) : box;
   }

   private class_238 expandHitbox(class_238 box, class_1297 entity) {
      for(int i = 0; i <= 20; ++i) {
         box = this.expand(entity, box, 0.05D, 0.0D, 0.0D);
         box = this.expand(entity, box, 0.0D, 0.0D, 0.05D);
         box = this.expand(entity, box, 0.0D, 0.05D, 0.0D);
         box = this.expand(entity, box, -0.05D, 0.0D, 0.0D);
         box = this.expand(entity, box, 0.0D, 0.0D, -0.05D);
         box = this.expand(entity, box, 0.0D, -0.05D, 0.0D);
      }

      return box;
   }

   private class_238 expand(class_1297 entity, class_238 box, double x, double y, double z) {
      class_238 newBox = box.method_1012(x * (Double)this.expand.get(), y * (Double)this.expand.get(), z * (Double)this.expand.get());
      return OLEPOSSUtils.inside(entity, newBox) ? box : newBox;
   }

   public boolean blockTransform(class_4587 stack) {
      if (this.enabled && this.target != null && BlackOut.mc.field_1724.method_6047().method_7909() instanceof class_1829) {
         if (this.blockRender.get() != Aura.BlockRenderMode.Disabled && (Boolean)this.blocking.get()) {
            stack.method_22903();
            this.f += BlackOut.mc.method_1534() / 20.0F * ((Double)this.speed.get()).floatValue() * 5.0F;
            this.f -= (float)((int)this.f);
            float swingProgress = BlackOut.mc.field_1724.method_6055(BlackOut.mc.method_1488());
            class_1297 var5 = this.target;
            float d;
            if (var5 instanceof class_1309) {
               class_1309 livingEntity = (class_1309)var5;
               float hurt = (float)livingEntity.field_6235 - BlackOut.mc.method_1488();
               d = 1.0F - this.boAnimate(1.0F - Math.max(hurt, 0.0F) / 10.0F);
            } else {
               d = 0.0F;
            }

            float k;
            switch((Aura.BlockRenderMode)this.blockRender.get()) {
            case BlackOut:
               stack.method_22904(0.5D, -0.5D, -1.25D);
               stack.method_22907(class_7833.field_40714.rotationDegrees(65.0F));
               stack.method_22907(class_7833.field_40716.rotationDegrees(-60.0F + d * 75.0F));
               stack.method_22907(class_7833.field_40718.rotationDegrees(90.0F));
               break;
            case KassuK:
               stack.method_22904(0.5D, -0.5D, -1.25D);
               stack.method_22907(class_7833.field_40714.rotationDegrees(65.0F));
               stack.method_22907(class_7833.field_40716.rotationDegrees(-60.0F));
               stack.method_22907(class_7833.field_40718.rotationDegrees(90.0F + d * 50.0F));
               break;
            case Retarded:
               k = (float)Math.sin((double)System.currentTimeMillis() / 150.0D);
               stack.method_22904(0.3D, -0.55D - 0.2D * (double)k, -1.15D);
               stack.method_22907(class_7833.field_40714.rotationDegrees(65.0F));
               stack.method_22907(class_7833.field_40716.rotationDegrees(-45.0F));
               stack.method_22907(class_7833.field_40718.rotationDegrees(100.0F + k * 50.0F));
               break;
            case KassuK2:
               k = (float)Math.sin((double)System.currentTimeMillis() / 65.0D);
               stack.method_22904(0.3D, -0.55D - 0.1D * (double)k, -1.15D);
               stack.method_22907(class_7833.field_40714.rotationDegrees(65.0F - k * 10.0F));
               stack.method_22907(class_7833.field_40716.rotationDegrees(-45.0F - k * 10.0F));
               stack.method_22907(class_7833.field_40718.rotationDegrees(90.0F + k * 10.0F));
               break;
            case KassuK3:
               k = (float)Math.sin((double)System.currentTimeMillis() / 65.0D);
               stack.method_22904(0.3D, -0.55D - 0.1D * (double)k, -1.15D);
               stack.method_22907(class_7833.field_40714.rotationDegrees(65.0F));
               stack.method_22907(class_7833.field_40716.rotationDegrees(-45.0F + k * 10.0F));
               stack.method_22907(class_7833.field_40718.rotationDegrees(90.0F));
               break;
            case Fan:
               stack.method_22904(0.5D, -0.5D, -1.25D);
               stack.method_22907(class_7833.field_40714.rotationDegrees(65.0F));
               stack.method_22907(class_7833.field_40716.rotationDegrees(-360.0F * this.f));
               stack.method_22907(class_7833.field_40718.rotationDegrees(90.0F));
               break;
            case Float:
               this.transformItem(stack, -0.1F, this.f);
               stack.method_22904(0.5D, -0.4D, -0.2D);
               stack.method_22907(class_7833.field_40714.rotationDegrees(-70.0F));
               stack.method_22907(class_7833.field_40716.rotationDegrees(32.0F));
               stack.method_22907(class_7833.field_40718.rotationDegrees(40.0F));
               break;
            case Slap:
               this.transformItem(stack, 0.0F, this.f);
               stack.method_22904(0.5D, -0.2D, -0.2D);
               stack.method_22907(class_7833.field_40714.rotationDegrees(-80.0F));
               stack.method_22907(class_7833.field_40716.rotationDegrees(60.0F));
               stack.method_22907(class_7833.field_40718.rotationDegrees(30.0F));
               break;
            case GPT:
               stack.method_22904(0.5D, -0.3D, -1.2D);
               stack.method_22907(class_7833.field_40714.rotationDegrees(-45.0F));
               stack.method_22907(class_7833.field_40716.rotationDegrees(90.0F));
               stack.method_22907(class_7833.field_40718.rotationDegrees(0.0F));
               stack.method_22904(0.0D, -0.5D * (double)this.f, 0.0D);
               stack.method_22907(class_7833.field_40714.rotationDegrees(-20.0F * this.f));
               stack.method_22907(class_7833.field_40716.rotationDegrees(5.0F * this.f));
               stack.method_22907(class_7833.field_40718.rotationDegrees(10.0F * this.f));
               stack.method_22905(1.0F + 0.1F * this.f, 1.0F + 0.1F * this.f, 1.0F + 0.1F * this.f);
            }

            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private void transformItem(class_4587 stack, float equipProgress, float swingProgress) {
      stack.method_22904(0.0D, 0.0D, -0.72D);
      stack.method_22904(0.0D, (double)equipProgress * -0.6D, 0.0D);
      stack.method_22907(class_7833.field_40716.rotationDegrees(45.0F));
      float f = class_3532.method_15374(swingProgress * swingProgress * 3.1415927F);
      float f1 = class_3532.method_15374(class_3532.method_15355(swingProgress) * 3.1415927F);
      stack.method_22907(class_7833.field_40714.rotationDegrees(f1 * -40.0F));
      stack.method_22907(class_7833.field_40716.rotationDegrees(f * -10.0F));
      stack.method_22907(class_7833.field_40718.rotationDegrees(f1 * -10.0F));
   }

   private float boAnimate(float d) {
      if ((double)d < 0.1D) {
         return 1.0F - d * 10.0F;
      } else {
         float d2 = 1.0F - (d - 0.1F) / 0.9F;
         return 1.0F - d2 * d2 * d2;
      }
   }

   private boolean getArmor(class_1657 entity) {
      for(int i = 0; i < 4; ++i) {
         if (!entity.method_31548().method_7372(i).method_7960()) {
            return true;
         }
      }

      return false;
   }

   private boolean canTeleport(class_1297 entity) {
      double distance = (double)BlackOut.mc.field_1724.method_5739(entity);
      return distance > (double)(Integer)this.maxPackets.get() * (Double)this.maxDistance.get() ? false : this.raycastCheck(entity.method_5829());
   }

   private List<class_243> getPath(class_1297 entity) {
      class_243 diff = entity.method_19538().method_1020(BlackOut.mc.field_1724.method_19538());
      List<class_243> path = new ArrayList();

      for(int i = 1; i <= (Integer)this.maxPackets.get(); ++i) {
         double delta = (double)((float)i / ((Integer)this.maxPackets.get()).floatValue());
         path.add(BlackOut.mc.field_1724.method_19538().method_1019(diff.method_1021(delta)));
      }

      return path;
   }

   private boolean raycastCheck(class_238 f) {
      f = f.method_1011(0.06D);
      class_238 b = BlackOut.mc.field_1724.method_5829().method_1011(0.06D);
      return this.raycast(new class_243(b.field_1323, b.field_1322, b.field_1321), new class_243(f.field_1323, f.field_1322, f.field_1321)) && this.raycast(new class_243(b.field_1320, b.field_1322, b.field_1321), new class_243(f.field_1320, f.field_1322, f.field_1321)) && this.raycast(new class_243(b.field_1323, b.field_1322, b.field_1324), new class_243(f.field_1323, f.field_1322, f.field_1324)) && this.raycast(new class_243(b.field_1320, b.field_1322, b.field_1324), new class_243(f.field_1320, f.field_1322, f.field_1324)) && this.raycast(new class_243(b.field_1323, b.field_1325, b.field_1321), new class_243(f.field_1323, f.field_1325, f.field_1321)) && this.raycast(new class_243(b.field_1320, b.field_1325, b.field_1321), new class_243(f.field_1320, f.field_1325, f.field_1321)) && this.raycast(new class_243(b.field_1323, b.field_1325, b.field_1324), new class_243(f.field_1323, f.field_1325, f.field_1324)) && this.raycast(new class_243(b.field_1320, b.field_1325, b.field_1324), new class_243(f.field_1320, f.field_1325, f.field_1324));
   }

   private boolean raycast(class_243 from, class_243 to) {
      ((IRaycastContext)DamageUtils.raycastContext).blackout_Client$set(from, to);
      return DamageUtils.raycast(DamageUtils.raycastContext, false).method_17783() == class_240.field_1333;
   }

   public static enum TargetMode {
      Health,
      Angle,
      Distance;

      // $FF: synthetic method
      private static Aura.TargetMode[] $values() {
         return new Aura.TargetMode[]{Health, Angle, Distance};
      }
   }

   public static enum RotationMode {
      OnHit,
      Constant;

      // $FF: synthetic method
      private static Aura.RotationMode[] $values() {
         return new Aura.RotationMode[]{OnHit, Constant};
      }
   }

   public static enum BlockMode {
      Spam,
      Hold,
      Fake;

      // $FF: synthetic method
      private static Aura.BlockMode[] $values() {
         return new Aura.BlockMode[]{Spam, Hold, Fake};
      }
   }

   public static enum BlockRenderMode {
      BlackOut,
      KassuK,
      KassuK2,
      KassuK3,
      Disabled,
      Fan,
      Retarded,
      Float,
      Slap,
      GPT;

      // $FF: synthetic method
      private static Aura.BlockRenderMode[] $values() {
         return new Aura.BlockRenderMode[]{BlackOut, KassuK, KassuK2, KassuK3, Disabled, Fan, Retarded, Float, Slap, GPT};
      }
   }

   public static enum DelayMode {
      Basic,
      Smart,
      Vanilla;

      // $FF: synthetic method
      private static Aura.DelayMode[] $values() {
         return new Aura.DelayMode[]{Basic, Smart, Vanilla};
      }
   }

   public static enum RenderMode {
      None,
      Hit,
      Always;

      // $FF: synthetic method
      private static Aura.RenderMode[] $values() {
         return new Aura.RenderMode[]{None, Hit, Always};
      }
   }
}
