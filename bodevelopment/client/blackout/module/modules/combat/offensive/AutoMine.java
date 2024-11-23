package bodevelopment.client.blackout.module.modules.combat.offensive;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.RenderShape;
import bodevelopment.client.blackout.enums.RotationType;
import bodevelopment.client.blackout.enums.SwingHand;
import bodevelopment.client.blackout.enums.SwitchMode;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.PacketEvent;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.interfaces.functional.EpicInterface;
import bodevelopment.client.blackout.keys.KeyBind;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.modules.client.settings.SwingSettings;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.randomstuff.FindResult;
import bodevelopment.client.blackout.randomstuff.timers.TimerList;
import bodevelopment.client.blackout.util.BlockUtils;
import bodevelopment.client.blackout.util.BoxUtils;
import bodevelopment.client.blackout.util.DamageUtils;
import bodevelopment.client.blackout.util.EntityUtils;
import bodevelopment.client.blackout.util.InvUtils;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import bodevelopment.client.blackout.util.SettingUtils;
import bodevelopment.client.blackout.util.render.Render3DUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1303;
import net.minecraft.class_1511;
import net.minecraft.class_1657;
import net.minecraft.class_1683;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1829;
import net.minecraft.class_2189;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_2846;
import net.minecraft.class_2868;
import net.minecraft.class_3532;
import net.minecraft.class_2350.class_2353;
import net.minecraft.class_2846.class_2847;

public class AutoMine extends Module {
   private static AutoMine INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final SettingGroup sgSwitch = this.addGroup("Switch");
   private final SettingGroup sgSpeed = this.addGroup("Speed");
   private final SettingGroup sgCrystals = this.addGroup("Crystals");
   private final SettingGroup sgCev = this.addGroup("Cev");
   private final SettingGroup sgTrapCev = this.addGroup("Trap Cev");
   private final SettingGroup sgSurroundCev = this.addGroup("Surround Cev");
   private final SettingGroup sgAntiSurround = this.addGroup("Anti Surround");
   private final SettingGroup sgAntiBurrow = this.addGroup("Anti Burrow");
   private final SettingGroup sgRender = this.addGroup("Render");
   private final Setting<Boolean> pauseEat;
   private final Setting<Boolean> pauseEatPlacing;
   private final Setting<Boolean> pauseSword;
   private final Setting<Boolean> packet;
   private final Setting<Boolean> autoMine;
   private final Setting<Boolean> manualMine;
   private final Setting<Boolean> manualInstant;
   private final Setting<Boolean> manualRemine;
   private final Setting<Boolean> fastRemine;
   private final Setting<Boolean> manualRangeReset;
   private final Setting<Boolean> resetOnSwitch;
   private final Setting<Boolean> ncpProgress;
   private final Setting<Boolean> damageSync;
   private final Setting<Integer> syncPredict;
   private final Setting<Integer> syncLength;
   private final Setting<Boolean> useMineBind;
   private final Setting<KeyBind> mineBind;
   private final Setting<List<class_2248>> ignore;
   private final Setting<Boolean> preSwitch;
   private final Setting<SwitchMode> pickaxeSwitch;
   private final Setting<Boolean> allowInventory;
   private final Setting<SwitchMode> crystalSwitch;
   private final Setting<Double> speed;
   private final Setting<Boolean> onGroundSpoof;
   private final Setting<Boolean> onGroundCheck;
   private final Setting<Boolean> effectCheck;
   private final Setting<Boolean> waterCheck;
   private final Setting<Double> placeSpeed;
   private final Setting<Double> attackSpeed;
   private final Setting<Double> attackTime;
   private final Setting<AutoMine.Priority> cevPriority;
   private final Setting<Boolean> cevDamageCheck;
   private final Setting<Double> minCevDamage;
   private final Setting<Double> maxCevDamage;
   private final Setting<Boolean> instantCev;
   private final Setting<Boolean> antiAntiCev;
   private final Setting<AutoMine.Priority> trapCevPriority;
   private final Setting<Boolean> trapCevDamageCheck;
   private final Setting<Double> minTrapCevDamage;
   private final Setting<Double> maxTrapCevDamage;
   private final Setting<Boolean> instantTrapCev;
   private final Setting<Boolean> antiAntiTrapCev;
   private final Setting<AutoMine.Priority> surroundCevPriority;
   private final Setting<Boolean> surroundCevDamageCheck;
   private final Setting<Double> minSurroundCevDamage;
   private final Setting<Double> maxSurroundCevDamage;
   private final Setting<Boolean> instantSurroundCev;
   private final Setting<Boolean> antiAntiSurroundCev;
   private final Setting<Boolean> acceptCollide;
   private final Setting<AutoMine.Priority> autoCityPriority;
   private final Setting<Boolean> autoCityDamageCheck;
   private final Setting<Double> minAutoCityDamage;
   private final Setting<Double> maxAutoCityDamage;
   private final Setting<Boolean> instantAutoCity;
   private final Setting<Boolean> placeCrystal;
   private final Setting<Boolean> attackCrystal;
   private final Setting<AutoMine.Priority> antiBurrowPriority;
   private final Setting<Boolean> mineStartSwing;
   private final Setting<Boolean> mineEndSwing;
   private final Setting<SwingHand> mineHand;
   private final Setting<Boolean> placeSwing;
   private final Setting<SwingHand> placeHand;
   private final Setting<Boolean> attackSwing;
   private final Setting<SwingHand> attackHand;
   private final Setting<Boolean> animationColor;
   private final Setting<AutoMine.AnimationMode> animationMode;
   private final Setting<Double> animationExponent;
   private final Setting<RenderShape> renderShape;
   private final Setting<BlackOutColor> lineStartColor;
   private final Setting<BlackOutColor> sideStartColor;
   private final Setting<BlackOutColor> lineEndColor;
   private final Setting<BlackOutColor> sideEndColor;
   private final Setting<RenderShape> instaRenderShape;
   private final Setting<BlackOutColor> instaLineColor;
   private final Setting<BlackOutColor> instaSideColor;
   public class_2338 minePos;
   public class_2338 crystalPos;
   public AutoMine.MineType mineType;
   private class_2338 prevPos;
   private class_1657 target;
   public boolean started;
   private double progress;
   private int minedFor;
   private double prevProgress;
   private double currentProgress;
   private class_2680 prevState;
   private boolean startedThisTick;
   private long lastPlace;
   private long lastAttack;
   private class_2338 prevMined;
   private boolean shouldRestart;
   private final TimerList<class_2338> crystals;
   private final List<class_1657> enemies;

   public AutoMine() {
      super("Auto Mine", "Automatically mines enemies' surround blocks to abuse them with crystals.", SubCategory.OFFENSIVE, true);
      this.pauseEat = this.sgGeneral.b("Pause Eat", false, "Pauses when eating");
      this.pauseEatPlacing = this.sgGeneral.b("Pause Eat Placing", false, "Pauses placing a crystal when eating");
      this.pauseSword = this.sgGeneral.b("Pause Sword", false, "Doesn't mine while holding sword");
      this.packet = this.sgGeneral.b("Packet", true, "Doesn't remove the block client side.");
      this.autoMine = this.sgGeneral.b("Auto Mine", true, "Automatically chooses a target block.");
      this.manualMine = this.sgGeneral.b("Manual Mine", true, "Sets target block to the block you clicked.");
      SettingGroup var10001 = this.sgGeneral;
      Setting var10005 = this.manualMine;
      Objects.requireNonNull(var10005);
      this.manualInstant = var10001.b("Manual Instant", false, "Uses instant mine when mining manually.", var10005::get);
      this.manualRemine = this.sgGeneral.b("Manual Remine", false, "Mines the manually mined block again.", () -> {
         return (Boolean)this.manualMine.get() && !(Boolean)this.manualInstant.get();
      });
      this.fastRemine = this.sgGeneral.b("Fast Remine", false, "Calculates mining progress from last block broken.", () -> {
         return (Boolean)this.manualMine.get() && !(Boolean)this.manualInstant.get() && (Boolean)this.manualRemine.get();
      });
      var10001 = this.sgGeneral;
      var10005 = this.manualMine;
      Objects.requireNonNull(var10005);
      this.manualRangeReset = var10001.b("Manual Range Reset", true, "Resets manual mining if out of range.", var10005::get);
      this.resetOnSwitch = this.sgGeneral.b("Reset On Switch", true, "Resets mining when switched held item.");
      this.ncpProgress = this.sgGeneral.b("NCP Progress", true, "Uses ncp mining progress checks.");
      this.damageSync = this.sgGeneral.b("Damage Sync", false, "Waits for enemy's damage tick to almost end before stopping mining.");
      var10001 = this.sgGeneral;
      Setting var10008 = this.damageSync;
      Objects.requireNonNull(var10008);
      this.syncPredict = var10001.i("Sync Predict", 0, 0, 10, 1, "Waits for enemy's damage tick to almost end before stopping mining.", var10008::get);
      var10001 = this.sgGeneral;
      var10008 = this.damageSync;
      Objects.requireNonNull(var10008);
      this.syncLength = var10001.i("Sync Length", 2, 0, 10, 1, "Waits for enemy's damage tick to almost end before stopping mining.", var10008::get);
      this.useMineBind = this.sgGeneral.b("Use Mine Bind", false, "Requires you to click the mine bind to break a block.");
      var10001 = this.sgGeneral;
      Setting var10004 = this.useMineBind;
      Objects.requireNonNull(var10004);
      this.mineBind = var10001.k("Mine Bind", ".", var10004::get);
      this.ignore = this.sgGeneral.bl("Ignore", ".");
      this.preSwitch = this.sgSwitch.b("Pre Switch", false, ".");
      this.pickaxeSwitch = this.sgSwitch.e("Pickaxe Switch", SwitchMode.InvSwitch, "Method of switching. InvSwitch is used in most clients.");
      this.allowInventory = this.sgSwitch.b("Allow Inventory", false, ".", () -> {
         return ((SwitchMode)this.pickaxeSwitch.get()).inventory;
      });
      this.crystalSwitch = this.sgSwitch.e("Crystal Switch", SwitchMode.InvSwitch, "Method of switching. InvSwitch is used in most clients.");
      this.speed = this.sgSpeed.d("Speed", 1.0D, 0.0D, 2.0D, 0.05D, "Vanilla speed multiplier.");
      this.onGroundSpoof = this.sgSpeed.b("On Ground Spoof", false, ".");
      this.onGroundCheck = this.sgSpeed.b("On Ground Check", true, "Mines 5x slower when not on ground.", () -> {
         return !(Boolean)this.onGroundSpoof.get();
      });
      this.effectCheck = this.sgSpeed.b("Effect Check", true, "Modifies mining speed depending on haste and mining fatigue.");
      this.waterCheck = this.sgSpeed.b("Water Check", true, "Mines 5x slower while submerged in water.");
      this.placeSpeed = this.sgCrystals.d("Place Speed", 2.0D, 0.0D, 20.0D, 0.1D, "How many times to place a crystal every second.");
      this.attackSpeed = this.sgCrystals.d("Attack Speed", 2.0D, 0.0D, 20.0D, 0.1D, "How many times to attack a crystal every second.");
      this.attackTime = this.sgCrystals.d("Attack Time", 2.0D, 0.0D, 10.0D, 0.1D, "Tries to attack a crystal for this many seconds.");
      this.cevPriority = this.sgCev.e("Cev Priority", AutoMine.Priority.Normal, "Priority of cev.");
      this.cevDamageCheck = this.sgCev.b("Cev Damage Check", true, "Checks for damage.", () -> {
         return this.cevPriority.get() != AutoMine.Priority.Disabled;
      });
      this.minCevDamage = this.sgCev.d("Min Cev Damage", 6.0D, 0.0D, 20.0D, 0.5D, ".", () -> {
         return this.cevPriority.get() != AutoMine.Priority.Disabled && (Boolean)this.cevDamageCheck.get();
      });
      this.maxCevDamage = this.sgCev.d("Max Cev Damage", 10.0D, 0.0D, 20.0D, 0.5D, ".", () -> {
         return this.cevPriority.get() != AutoMine.Priority.Disabled && (Boolean)this.cevDamageCheck.get();
      });
      this.instantCev = this.sgCev.b("Instant Cev", false, "Only sends 1 mine start packet for each block.", () -> {
         return this.cevPriority.get() != AutoMine.Priority.Disabled;
      });
      this.antiAntiCev = this.sgCev.b("Anti Anti Cev", false, "Places a crystal and mines the block at the same time", () -> {
         return this.cevPriority.get() != AutoMine.Priority.Disabled;
      });
      this.trapCevPriority = this.sgTrapCev.e("Trap Cev Priority", AutoMine.Priority.Normal, "Priority of trap cev.");
      this.trapCevDamageCheck = this.sgTrapCev.b("Trap Cev Damage Check", true, "Checks for damage.", () -> {
         return this.trapCevPriority.get() != AutoMine.Priority.Disabled;
      });
      this.minTrapCevDamage = this.sgTrapCev.d("Min Trap Cev Damage", 6.0D, 0.0D, 20.0D, 0.5D, ".", () -> {
         return this.trapCevPriority.get() != AutoMine.Priority.Disabled && (Boolean)this.trapCevDamageCheck.get();
      });
      this.maxTrapCevDamage = this.sgTrapCev.d("Max Trap Cev Damage", 10.0D, 0.0D, 20.0D, 0.5D, ".", () -> {
         return this.trapCevPriority.get() != AutoMine.Priority.Disabled && (Boolean)this.trapCevDamageCheck.get();
      });
      this.instantTrapCev = this.sgTrapCev.b("Instant Trap Cev", false, "Only sends 1 mine start packet for each block.", () -> {
         return this.trapCevPriority.get() != AutoMine.Priority.Disabled;
      });
      this.antiAntiTrapCev = this.sgTrapCev.b("Anti Anti Trap Cev", false, "Places a crystal and mines the block at the same time", () -> {
         return this.trapCevPriority.get() != AutoMine.Priority.Disabled;
      });
      this.surroundCevPriority = this.sgSurroundCev.e("Surround Cev Priority", AutoMine.Priority.Normal, "Priority of surround cev.");
      this.surroundCevDamageCheck = this.sgSurroundCev.b("Surround Cev Damage Check", true, "Checks for damage.", () -> {
         return this.surroundCevPriority.get() != AutoMine.Priority.Disabled;
      });
      this.minSurroundCevDamage = this.sgSurroundCev.d("Min Surround Cev Damage", 6.0D, 0.0D, 20.0D, 0.5D, ".", () -> {
         return this.surroundCevPriority.get() != AutoMine.Priority.Disabled && (Boolean)this.surroundCevDamageCheck.get();
      });
      this.maxSurroundCevDamage = this.sgSurroundCev.d("Max Surround Cev Damage", 10.0D, 0.0D, 20.0D, 0.5D, ".", () -> {
         return this.surroundCevPriority.get() != AutoMine.Priority.Disabled && (Boolean)this.surroundCevDamageCheck.get();
      });
      this.instantSurroundCev = this.sgSurroundCev.b("Instant Surround Cev", false, "Only sends 1 mine start packet for each block.", () -> {
         return this.surroundCevPriority.get() != AutoMine.Priority.Disabled;
      });
      this.antiAntiSurroundCev = this.sgSurroundCev.b("Anti Anti Surround Cev", false, "Places a crystal and mines the block at the same time.", () -> {
         return this.surroundCevPriority.get() != AutoMine.Priority.Disabled;
      });
      this.acceptCollide = this.sgSurroundCev.b("Accept Collide", false, "Accepts crystals that arent on top of the block but colliding with it.", () -> {
         return this.surroundCevPriority.get() != AutoMine.Priority.Disabled;
      });
      this.autoCityPriority = this.sgAntiSurround.e("Auto City Priority", AutoMine.Priority.Normal, "Priority of auto city. Places crystal next to enemy's surround block.");
      this.autoCityDamageCheck = this.sgAntiSurround.b("Auto City Damage Check", true, "Checks for damage.", () -> {
         return this.autoCityPriority.get() != AutoMine.Priority.Disabled;
      });
      this.minAutoCityDamage = this.sgAntiSurround.d("Min Auto City Damage", 6.0D, 0.0D, 20.0D, 0.5D, ".", () -> {
         return this.autoCityPriority.get() != AutoMine.Priority.Disabled && (Boolean)this.autoCityDamageCheck.get();
      });
      this.maxAutoCityDamage = this.sgAntiSurround.d("Max Auto City Damage", 10.0D, 0.0D, 20.0D, 0.5D, ".", () -> {
         return this.autoCityPriority.get() != AutoMine.Priority.Disabled && (Boolean)this.autoCityDamageCheck.get();
      });
      this.instantAutoCity = this.sgAntiSurround.b("Instant Auto City", false, "Only sends 1 mine start packet for each block.", () -> {
         return this.autoCityPriority.get() != AutoMine.Priority.Disabled;
      });
      this.placeCrystal = this.sgAntiSurround.b("Place Crystal", true, ".", () -> {
         return this.autoCityPriority.get() != AutoMine.Priority.Disabled;
      });
      this.attackCrystal = this.sgAntiSurround.b("Attack Crystal", false, "Attacks the crystal we placed.", () -> {
         return this.autoCityPriority.get() != AutoMine.Priority.Disabled;
      });
      this.antiBurrowPriority = this.sgAntiBurrow.e("Anti Burrow Priority", AutoMine.Priority.Normal, "Priority of anti burrow.");
      this.mineStartSwing = this.sgRender.b("Mine Start Swing", false, "Renders swing animation when starting to mine.");
      this.mineEndSwing = this.sgRender.b("Mine End Swing", false, "Renders swing animation when ending mining.");
      this.mineHand = this.sgRender.e("Mine Hand", SwingHand.RealHand, "Which hand should be swung.", () -> {
         return (Boolean)this.mineStartSwing.get() || (Boolean)this.mineEndSwing.get();
      });
      this.placeSwing = this.sgRender.b("Place Swing", false, "Renders swing animation when placing a crystal.");
      var10001 = this.sgRender;
      SwingHand var10003 = SwingHand.RealHand;
      var10005 = this.placeSwing;
      Objects.requireNonNull(var10005);
      this.placeHand = var10001.e("Place Hand", var10003, "Which hand should be swung.", var10005::get);
      this.attackSwing = this.sgRender.b("Attack Swing", false, "Renders swing animation when attacking a crystal.");
      var10001 = this.sgRender;
      var10003 = SwingHand.RealHand;
      var10005 = this.attackSwing;
      Objects.requireNonNull(var10005);
      this.attackHand = var10001.e("Attack Hand", var10003, "Which hand should be swung.", var10005::get);
      this.animationColor = this.sgRender.b("Animation Color", true, "Changes color smoothly.");
      this.animationMode = this.sgRender.e("Animation Mode", AutoMine.AnimationMode.Full, ".");
      this.animationExponent = this.sgRender.d("Animation Exponent", 1.0D, 0.0D, 10.0D, 0.1D, ".");
      this.renderShape = this.sgRender.e("Render Shape", RenderShape.Full, "Which parts should be rendered.");
      this.lineStartColor = this.sgRender.c("Line Start Color", new BlackOutColor(255, 0, 0, 0), ".");
      this.sideStartColor = this.sgRender.c("Side Start Color", new BlackOutColor(255, 0, 0, 0), ".");
      this.lineEndColor = this.sgRender.c("Line End Color", new BlackOutColor(255, 0, 0, 255), ".");
      this.sideEndColor = this.sgRender.c("Side End Color", new BlackOutColor(255, 0, 0, 50), ".");
      this.instaRenderShape = this.sgRender.e("Insta Render Shape", RenderShape.Full, "Which parts should be rendered.");
      this.instaLineColor = this.sgRender.c("Insta Line Color", new BlackOutColor(255, 0, 0, 255), ".");
      this.instaSideColor = this.sgRender.c("Insta Side Color", new BlackOutColor(255, 0, 0, 50), ".");
      this.minePos = null;
      this.crystalPos = null;
      this.mineType = null;
      this.prevPos = null;
      this.target = null;
      this.started = false;
      this.progress = 0.0D;
      this.minedFor = 0;
      this.prevProgress = 0.0D;
      this.currentProgress = 0.0D;
      this.prevState = class_2246.field_10124.method_9564();
      this.startedThisTick = false;
      this.lastPlace = 0L;
      this.lastAttack = 0L;
      this.prevMined = null;
      this.shouldRestart = false;
      this.crystals = new TimerList(false);
      this.enemies = new ArrayList();
      INSTANCE = this;
   }

   public static AutoMine getInstance() {
      return INSTANCE;
   }

   @Event
   public void onSent(PacketEvent.Sent event) {
      if ((Boolean)this.resetOnSwitch.get() && event.packet instanceof class_2868) {
         this.shouldRestart = true;
      }

   }

   @Event
   public void onRender(RenderEvent.World.Post event) {
      this.updateRender(event.tickDelta);
   }

   @Event
   public void onTick(TickEvent.Pre event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         this.crystals.update();
         this.enemies.clear();
         BlackOut.mc.field_1687.method_18456().forEach((player) -> {
            if (!(BlackOut.mc.field_1724.method_5739(player) > 10.0F)) {
               if (player != BlackOut.mc.field_1724) {
                  if (!Managers.FRIENDS.isFriend(player)) {
                     this.enemies.add(player);
                  }
               }
            }
         });
         this.startedThisTick = false;
         this.updatePos();
         if (this.minePos != null && this.mineType == AutoMine.MineType.Manual) {
            if ((Boolean)this.manualRangeReset.get() && !SettingUtils.inMineRange(this.minePos)) {
               this.prevMined = null;
               this.started = false;
               this.minePos = null;
            } else {
               class_2680 state = BlackOut.mc.field_1687.method_8320(this.minePos);
               if (!(state.method_26204() instanceof class_2189)) {
                  this.prevState = state;
               }
            }
         }

         this.updateStartOrAbort();
         this.updateMining();
         this.prevProgress = this.currentProgress;
         this.currentProgress = this.getProgress();
         this.updateAttacking();
      }
   }

   private void updateStartOrAbort() {
      if (this.shouldRestart) {
         if (this.minePos != null) {
            this.abort(this.minePos);
         }

         this.shouldRestart = false;
         this.started = false;
      }

      if (this.minePos == null) {
         if (this.prevPos != null) {
            this.abort(this.prevPos);
         }
      } else {
         if (!this.minePos.equals(this.prevPos)) {
            this.started = false;
         }

         if (!this.started && !this.paused(false)) {
            class_2350 dir = SettingUtils.getPlaceOnDirection(this.minePos);
            if (!SettingUtils.startMineRot() || this.rotateBlock(this.minePos, dir, this.getMineStartRotationVec(dir), RotationType.Mining, "mining")) {
               this.start(this.minePos, false);
            }
         }
      }

      this.prevPos = this.minePos;
   }

   private boolean paused(boolean placing) {
      if ((Boolean)(placing ? this.pauseEatPlacing : this.pauseEat).get() && BlackOut.mc.field_1724.method_6115()) {
         return true;
      } else {
         return (Boolean)this.pauseSword.get() && BlackOut.mc.field_1724.method_6047().method_7909() instanceof class_1829;
      }
   }

   private void updateRender(float tickDelta) {
      double p = this.currentProgress;
      if (this.prevMined != null) {
         Render3DUtils.box(BoxUtils.get(this.prevMined), (BlackOutColor)this.instaSideColor.get(), (BlackOutColor)this.instaLineColor.get(), (RenderShape)this.instaRenderShape.get());
      } else if (this.minePos != null && this.started && this.prevProgress < p && p < Double.POSITIVE_INFINITY) {
         p = class_3532.method_16436((double)tickDelta, this.prevProgress, p);
         p = class_3532.method_15350(p, 0.0D, 1.0D);
         p = 1.0D - Math.pow(1.0D - p, (Double)this.animationExponent.get());
         p = Math.min(p / 2.0D, 0.5D);
         BlackOutColor sideColor = this.getSideColor(p * 2.0D);
         BlackOutColor lineColor = this.getLineColor(p * 2.0D);
         class_238 box = this.getBox(p, (AutoMine.AnimationMode)this.animationMode.get());
         if (box != null) {
            Render3DUtils.box(box, sideColor, lineColor, (RenderShape)this.renderShape.get());
         }
      }

   }

   private class_238 getBox(double p, AutoMine.AnimationMode mode) {
      double up = 0.5D;
      double down = 0.5D;
      double sides = 0.5D;
      switch(mode) {
      case Full:
         up = p;
         down = p;
         sides = p;
         break;
      case Up:
         down = p * 2.0D - 0.5D;
         break;
      case Down:
         up = p * 2.0D - 0.5D;
         break;
      case Double:
         double p2 = p * 2.0D - 0.5D;
         up = p2;
         down = p2;
         sides = p2;
      }

      return this.getBox(sides, up, down);
   }

   private class_238 getBox(double sides, double up, double down) {
      class_2680 state = this.ncpState();
      class_265 shape = state.method_26218(BlackOut.mc.field_1687, this.minePos);
      if (shape.method_1110()) {
         return null;
      } else {
         class_238 from = shape.method_1107();
         class_243 middle = BoxUtils.middle(from);
         class_243 scale = new class_243(from.method_17939(), from.method_17940(), from.method_17941());
         return this.fromScale(middle, scale, sides, up, down).method_996(this.minePos);
      }
   }

   private class_238 fromScale(class_243 m, class_243 s, double sides, double up, double down) {
      return new class_238(m.field_1352 - sides * s.field_1352, m.field_1351 - down * s.field_1351, m.field_1350 - sides * s.field_1350, m.field_1352 + sides * s.field_1352, m.field_1351 + up * s.field_1351, m.field_1350 + sides * s.field_1350);
   }

   private BlackOutColor getSideColor(double p) {
      if ((Boolean)this.animationColor.get()) {
         return ((BlackOutColor)this.sideStartColor.get()).lerp(p, (BlackOutColor)this.sideEndColor.get());
      } else {
         return p > 0.9D ? (BlackOutColor)this.sideEndColor.get() : (BlackOutColor)this.sideStartColor.get();
      }
   }

   private BlackOutColor getLineColor(double p) {
      if ((Boolean)this.animationColor.get()) {
         return ((BlackOutColor)this.lineStartColor.get()).lerp(p, (BlackOutColor)this.lineEndColor.get());
      } else {
         return p > 0.9D ? (BlackOutColor)this.lineEndColor.get() : (BlackOutColor)this.lineStartColor.get();
      }
   }

   private double getProgress() {
      if (this.minePos == null) {
         return 0.0D;
      } else {
         class_1799 bestStack = this.findBestSlot((stack) -> {
            return BlockUtils.getBlockBreakingDelta(stack, this.ncpState(), (Boolean)this.effectCheck.get(), (Boolean)this.waterCheck.get(), (Boolean)this.onGroundCheck.get() && !(Boolean)this.onGroundSpoof.get());
         }).stack();
         return !(Boolean)this.ncpProgress.get() ? this.progress : (double)this.minedFor / (1.0D / BlockUtils.getBlockBreakingDelta(bestStack, this.ncpState(), (Boolean)this.effectCheck.get(), (Boolean)this.waterCheck.get(), (Boolean)this.onGroundCheck.get() && !(Boolean)this.onGroundSpoof.get()));
      }
   }

   private class_2680 ncpState() {
      return this.mineType == AutoMine.MineType.Manual && (Boolean)this.manualRemine.get() && (Boolean)this.fastRemine.get() && !this.shouldInstant() ? this.prevState : BlackOut.mc.field_1687.method_8320(this.minePos);
   }

   private void updatePos() {
      if (this.minePos == null || this.mineType != AutoMine.MineType.Manual) {
         AutoMine.Target target = this.getTarget();
         this.minePos = target.pos;
         this.crystalPos = target.crystal;
         this.mineType = target.type;
         this.target = target.target;
      }
   }

   private AutoMine.Target getTarget() {
      AutoMine.Target target = null;
      if ((Boolean)this.autoMine.get()) {
         target = this.targetCheck(target, this.getCev(), this.cevPriority);
         target = this.targetCheck(target, this.getTrapCev(), this.trapCevPriority);
         target = this.targetCheck(target, this.getSurroundCev(), this.surroundCevPriority);
         target = this.targetCheck(target, this.getAutoCity(), this.autoCityPriority);
         target = this.targetCheck(target, this.getAntiBurrow(), this.antiBurrowPriority);
      }

      return target == null ? new AutoMine.Target((class_2338)null, (class_2338)null, (AutoMine.MineType)null, 0, (class_1657)null) : target;
   }

   private int getPriority(AutoMine.Target target) {
      return target == null ? 0 : target.priority;
   }

   private AutoMine.Target getCev() {
      class_2338 best = null;
      class_1657 bestPlayer = null;
      double bestDist = 1000.0D;
      Iterator var5 = this.enemies.iterator();

      while(var5.hasNext()) {
         class_1657 player = (class_1657)var5.next();
         class_2338 pos = new class_2338(player.method_31477(), (int)Math.ceil(player.method_5829().field_1325), player.method_31479());
         if (!this.invalidCev(pos, player, this.minCevDamage, this.maxCevDamage, this.cevDamageCheck)) {
            if (pos.equals(this.minePos)) {
               return new AutoMine.Target(pos, pos.method_10084(), AutoMine.MineType.Cev, ((AutoMine.Priority)this.cevPriority.get()).priority, player);
            }

            if (!this.ignored(pos)) {
               double distance = this.getDist(pos);
               if (!(distance >= bestDist)) {
                  best = pos;
                  bestDist = distance;
                  bestPlayer = player;
               }
            }
         }
      }

      return best == null ? null : new AutoMine.Target(best, best.method_10084(), AutoMine.MineType.Cev, ((AutoMine.Priority)this.cevPriority.get()).priority, bestPlayer);
   }

   private AutoMine.Target getTrapCev() {
      class_2338 best = null;
      class_1657 bestPlayer = null;
      double bestDist = 1000.0D;
      Iterator var5 = this.enemies.iterator();

      while(var5.hasNext()) {
         class_1657 player = (class_1657)var5.next();
         class_2338 eyePos = new class_2338(player.method_31477(), (int)Math.ceil(player.method_5829().field_1325) - 1, player.method_31479());
         Iterator var8 = class_2353.field_11062.iterator();

         while(var8.hasNext()) {
            class_2350 dir = (class_2350)var8.next();
            class_2338 pos = eyePos.method_10093(dir);
            if (!this.invalidCev(pos, player, this.minTrapCevDamage, this.maxTrapCevDamage, this.trapCevDamageCheck)) {
               if (pos.equals(this.minePos)) {
                  return new AutoMine.Target(pos, pos.method_10084(), AutoMine.MineType.TrapCev, ((AutoMine.Priority)this.trapCevPriority.get()).priority, player);
               }

               if (!this.ignored(pos)) {
                  double distance = this.getDist(pos);
                  if (!(distance >= bestDist)) {
                     best = pos;
                     bestDist = distance;
                     bestPlayer = player;
                  }
               }
            }
         }
      }

      return best == null ? null : new AutoMine.Target(best, best.method_10084(), AutoMine.MineType.TrapCev, ((AutoMine.Priority)this.trapCevPriority.get()).priority, bestPlayer);
   }

   private AutoMine.Target getSurroundCev() {
      class_2338 best = null;
      class_1657 bestPlayer = null;
      double bestDist = 1000.0D;
      Iterator var5 = this.enemies.iterator();

      while(var5.hasNext()) {
         class_1657 player = (class_1657)var5.next();
         class_2338 feetPos = new class_2338(player.method_31477(), (int)Math.round(player.method_23318()), player.method_31479());
         Iterator var8 = class_2353.field_11062.iterator();

         while(var8.hasNext()) {
            class_2350 dir = (class_2350)var8.next();
            class_2338 pos = feetPos.method_10093(dir);
            if (!this.invalidCev(pos, player, this.minSurroundCevDamage, this.maxSurroundCevDamage, this.surroundCevDamageCheck)) {
               if (pos.equals(this.minePos)) {
                  return new AutoMine.Target(pos, pos.method_10084(), AutoMine.MineType.SurroundCev, ((AutoMine.Priority)this.surroundCevPriority.get()).priority, player);
               }

               if (!this.ignored(pos)) {
                  double distance = this.getDist(pos);
                  if (!(distance >= bestDist)) {
                     best = pos;
                     bestDist = distance;
                     bestPlayer = player;
                  }
               }
            }
         }
      }

      return best == null ? null : new AutoMine.Target(best, best.method_10084(), AutoMine.MineType.SurroundCev, ((AutoMine.Priority)this.surroundCevPriority.get()).priority, bestPlayer);
   }

   private AutoMine.Target getAutoCity() {
      class_2338 best = null;
      class_2338 bestCrystal = null;
      class_1657 bestPlayer = null;
      double bestDist = 1000.0D;
      Iterator var6 = this.enemies.iterator();

      label89:
      while(var6.hasNext()) {
         class_1657 player = (class_1657)var6.next();
         class_2338 feetPos = new class_2338(player.method_31477(), (int)Math.round(player.method_23318()), player.method_31479());
         Iterator var9 = class_2353.field_11062.iterator();

         while(true) {
            class_2338 pos;
            class_2338 crystal;
            class_243 crystalFeet;
            do {
               do {
                  do {
                     do {
                        do {
                           do {
                              do {
                                 do {
                                    do {
                                       if (!var9.hasNext()) {
                                          continue label89;
                                       }

                                       class_2350 dir = (class_2350)var9.next();
                                       pos = feetPos.method_10093(dir);
                                       crystal = pos.method_10093(dir);
                                    } while(SettingUtils.getPlaceOnDirection(pos) == null);
                                 } while(SettingUtils.getPlaceOnDirection(crystal.method_10074()) == null);
                              } while(!this.crystalBlock(crystal.method_10074(), false));
                           } while(!SettingUtils.inMineRange(pos));
                        } while(!SettingUtils.inInteractRange(crystal.method_10074()));
                     } while(!SettingUtils.inAttackRange(OLEPOSSUtils.getCrystalBox(crystal)));

                     if (this.isInstant(pos)) {
                        return new AutoMine.Target(pos, crystal, AutoMine.MineType.AutoCity, ((AutoMine.Priority)this.autoCityPriority.get()).priority, player);
                     }
                  } while(this.ignored(pos));
               } while(!BlockUtils.mineable(pos));

               if (!(Boolean)this.autoCityDamageCheck.get()) {
                  break;
               }

               crystalFeet = new class_243((double)crystal.method_10263() + 0.5D, (double)crystal.method_10264(), (double)crystal.method_10260() + 0.5D);
            } while(DamageUtils.crystalDamage(player, player.method_5829(), crystalFeet, pos) < (Double)this.minAutoCityDamage.get() || DamageUtils.crystalDamage(BlackOut.mc.field_1724, BlackOut.mc.field_1724.method_5829(), crystalFeet, pos) > (Double)this.maxAutoCityDamage.get());

            double distance = this.getDist(pos);
            if (!(distance >= bestDist)) {
               best = pos;
               bestCrystal = crystal;
               bestDist = distance;
               bestPlayer = player;
            }
         }
      }

      return best == null ? null : new AutoMine.Target(best, bestCrystal, AutoMine.MineType.AutoCity, ((AutoMine.Priority)this.autoCityPriority.get()).priority, bestPlayer);
   }

   private AutoMine.Target getAntiBurrow() {
      class_2338 best = null;
      class_1657 bestPlayer = null;
      double bestDist = 1000.0D;
      Iterator var5 = this.enemies.iterator();

      while(var5.hasNext()) {
         class_1657 player = (class_1657)var5.next();
         class_2338 pos = new class_2338(player.method_31477(), (int)Math.round(player.method_23318()), player.method_31479());
         if (SettingUtils.getPlaceOnDirection(pos) != null && SettingUtils.inMineRange(pos)) {
            if (this.isInstant(pos)) {
               return new AutoMine.Target(pos, (class_2338)null, AutoMine.MineType.AntiBurrow, ((AutoMine.Priority)this.antiBurrowPriority.get()).priority, player);
            }

            if (!this.ignored(pos) && BlockUtils.mineable(pos)) {
               double distance = this.getDist(pos);
               if (!(distance >= bestDist)) {
                  best = pos;
                  bestDist = distance;
                  bestPlayer = player;
               }
            }
         }
      }

      return best == null ? null : new AutoMine.Target(best, (class_2338)null, AutoMine.MineType.AntiBurrow, ((AutoMine.Priority)this.antiBurrowPriority.get()).priority, bestPlayer);
   }

   private boolean invalidCev(class_2338 pos, class_1657 player, Setting<Double> minDmg, Setting<Double> maxDmg, Setting<Boolean> dmgCheck) {
      if (!this.crystalBlock(pos, true)) {
         return true;
      } else if (SettingUtils.getPlaceOnDirection(pos) == null) {
         return true;
      } else if (SettingUtils.inMineRange(pos) && SettingUtils.inInteractRange(pos) && SettingUtils.inAttackRange(OLEPOSSUtils.getCrystalBox(pos.method_10084()))) {
         if (this.isInstant(pos)) {
            return false;
         } else if (EntityUtils.intersects(BoxUtils.crystalSpawnBox(pos.method_10084()), (entity) -> {
            return !(entity instanceof class_1511) && !(entity instanceof class_1303) && !(entity instanceof class_1683);
         })) {
            return true;
         } else {
            class_243 crystalFeet = new class_243((double)pos.method_10263() + 0.5D, (double)(pos.method_10264() + 1), (double)pos.method_10260() + 0.5D);
            if ((Boolean)dmgCheck.get()) {
               if (DamageUtils.crystalDamage(player, player.method_5829(), crystalFeet, pos) < (Double)minDmg.get()) {
                  return true;
               } else {
                  return DamageUtils.crystalDamage(BlackOut.mc.field_1724, BlackOut.mc.field_1724.method_5829(), crystalFeet, pos) > (Double)maxDmg.get();
               }
            } else {
               return false;
            }
         }
      } else {
         return true;
      }
   }

   private boolean ignored(class_2338 pos) {
      return ((List)this.ignore.get()).contains(BlackOut.mc.field_1687.method_8320(pos).method_26204());
   }

   private boolean isInstant(class_2338 pos) {
      return pos.equals(this.prevMined);
   }

   private double getDist(class_2338 pos) {
      return BlackOut.mc.field_1724.method_33571().method_1022(pos.method_46558());
   }

   private boolean crystalBlock(class_2338 pos, boolean cev) {
      class_2248 bottom = this.getBlock(pos);
      if (SettingUtils.oldCrystals() && !(this.getBlock(pos.method_10084()) instanceof class_2189)) {
         return false;
      } else if (cev && bottom == class_2246.field_9987) {
         return false;
      } else if (!(this.getBlock(pos.method_10084()) instanceof class_2189)) {
         return false;
      } else if (this.isInstant(pos)) {
         return true;
      } else {
         return bottom == class_2246.field_10540 || bottom == class_2246.field_9987;
      }
   }

   private class_2248 getBlock(class_2338 pos) {
      return Managers.BLOCK.blockState(pos).method_26204();
   }

   private AutoMine.Target targetCheck(AutoMine.Target target, AutoMine.Target newTarget, Setting<AutoMine.Priority> prioritySetting) {
      int priority = ((AutoMine.Priority)prioritySetting.get()).priority;
      return priority >= 0 && newTarget != null && priority >= this.getPriority(target) ? newTarget : target;
   }

   private void updateAttacking() {
      class_1511 target = this.getTargetCrystal();
      if (target != null) {
         if (this.rotateCheck(target)) {
            if (!((double)(System.currentTimeMillis() - this.lastAttack) <= 1000.0D / (Double)this.attackSpeed.get())) {
               if ((Boolean)this.attackSwing.get()) {
                  this.clientSwing((SwingHand)this.attackHand.get(), class_1268.field_5808);
               }

               this.attackEntity(target);
               this.end("attacking");
               this.lastAttack = System.currentTimeMillis();
            }
         }
      }
   }

   private boolean rotateCheck(class_1511 target) {
      if (!SettingUtils.shouldRotate(RotationType.Attacking)) {
         return true;
      } else {
         return SettingUtils.shouldIgnoreRotations(target) ? this.checkAttackLimit() : this.attackRotate(target.method_5829(), this.getAttackRotationVec(target), -0.1D, "attacking");
      }
   }

   private class_1511 getTargetCrystal() {
      class_1511 closestCrystal = null;
      double closestDistance = 69420.0D;
      Iterator var4 = BlackOut.mc.field_1687.method_18112().iterator();

      while(var4.hasNext()) {
         class_1297 entity = (class_1297)var4.next();
         if (entity instanceof class_1511) {
            class_1511 crystal = (class_1511)entity;
            if (this.crystals.contains((Object)crystal.method_24515()) && !AutoCrystal.getInstance().shouldAutoMineStop(entity) && SettingUtils.inAttackRange(entity.method_5829())) {
               double distance = (double)BlackOut.mc.field_1724.method_5739(entity);
               if (!(distance >= closestDistance)) {
                  closestCrystal = crystal;
                  closestDistance = distance;
               }
            }
         }
      }

      return closestCrystal;
   }

   private void updateMining() {
      if (this.minePos != null && !this.startedThisTick) {
         boolean holding = this.itemMinedCheck(Managers.PACKET.getStack());
         int slot = this.findBestSlot((stack) -> {
            return BlockUtils.getBlockBreakingDelta(this.minePos, stack, (Boolean)this.effectCheck.get(), (Boolean)this.waterCheck.get(), (Boolean)this.onGroundCheck.get() && !(Boolean)this.onGroundSpoof.get());
         }).slot();
         class_1799 bestStack = holding ? Managers.PACKET.getStack() : BlackOut.mc.field_1724.method_31548().method_5438(slot);
         if ((Boolean)this.ncpProgress.get()) {
            ++this.minedFor;
         } else {
            this.progress += BlockUtils.getBlockBreakingDelta(this.minePos, bestStack, (Boolean)this.effectCheck.get(), (Boolean)this.waterCheck.get(), (Boolean)this.onGroundCheck.get() && !(Boolean)this.onGroundSpoof.get());
         }

         if (this.minedCheck(bestStack) && this.damageSyncCheck()) {
            this.mineEndUpdate(holding, slot);
         } else if (this.almostMined(bestStack) && SettingUtils.endMineRot()) {
            this.preRotate();
         }

      }
   }

   private FindResult findBestSlot(EpicInterface<class_1799, Double> test) {
      return InvUtils.findBest(((SwitchMode)this.pickaxeSwitch.get()).hotbar, ((SwitchMode)this.pickaxeSwitch.get()).inventory && (Boolean)this.allowInventory.get(), test);
   }

   private boolean damageSyncCheck() {
      if ((Boolean)this.damageSync.get() && this.target != null) {
         int amogus = -this.target.field_6008 + 11 + (Integer)this.syncPredict.get();
         return amogus > 0 && amogus <= (Integer)this.syncLength.get();
      } else {
         return true;
      }
   }

   private void mineEndUpdate(boolean holding, int slot) {
      class_1511 crystalAt = this.crystalAt(this.crystalPos);
      if (!this.notPressed() && !this.ignored(this.minePos)) {
         switch(this.mineType) {
         case Cev:
            if (crystalAt == null) {
               if (EntityUtils.intersects(BoxUtils.crystalSpawnBox(this.crystalPos), (entity) -> {
                  return true;
               })) {
                  return;
               }

               if (!this.placeCrystal(this.crystalPos.method_10074())) {
                  return;
               }

               if (!(Boolean)this.antiAntiCev.get()) {
                  return;
               }
            }
            break;
         case TrapCev:
            if (crystalAt == null) {
               if (EntityUtils.intersects(BoxUtils.crystalSpawnBox(this.crystalPos), (entity) -> {
                  return true;
               })) {
                  return;
               }

               if (!this.placeCrystal(this.crystalPos.method_10074())) {
                  return;
               }

               if (!(Boolean)this.antiAntiTrapCev.get()) {
                  return;
               }
            }
            break;
         case SurroundCev:
            if (crystalAt == null && (!(Boolean)this.acceptCollide.get() || !EntityUtils.intersects(BoxUtils.get(this.crystalPos.method_10074()), (entity) -> {
               return entity instanceof class_1511;
            }))) {
               if (EntityUtils.intersects(BoxUtils.crystalSpawnBox(this.crystalPos), (entity) -> {
                  return true;
               })) {
                  return;
               }

               if (!this.placeCrystal(this.crystalPos.method_10074())) {
                  return;
               }

               if (!(Boolean)this.antiAntiSurroundCev.get()) {
                  return;
               }
            }
            break;
         case AutoCity:
            if (crystalAt == null && (Boolean)this.placeCrystal.get() && !this.placeCrystal(this.crystalPos.method_10074())) {
               return;
            }
         }

         this.endMining(holding, slot);
      }
   }

   private boolean notPressed() {
      return (Boolean)this.useMineBind.get() && !((KeyBind)this.mineBind.get()).isPressed();
   }

   private void preRotate() {
      if (!this.notPressed()) {
         if (!(this.getBlock(this.minePos) instanceof class_2189)) {
            if (SettingUtils.inMineRange(this.minePos)) {
               class_2350 dir = SettingUtils.getPlaceOnDirection(this.minePos);
               if (dir != null) {
                  this.rotateBlock(this.minePos, dir, this.getMineEndRotationVec(), RotationType.Mining, "mining");
               }
            }
         }
      }
   }

   private void endMining(boolean holding, int slot) {
      if (!(this.getBlock(this.minePos) instanceof class_2189)) {
         if (SettingUtils.inMineRange(this.minePos)) {
            class_2350 dir = SettingUtils.getPlaceOnDirection(this.minePos);
            if (dir != null) {
               if (!SettingUtils.endMineRot() || this.rotateBlock(this.minePos, dir, this.getMineEndRotationVec(), RotationType.Mining, "mining")) {
                  boolean switched = false;
                  if (holding || (switched = ((SwitchMode)this.pickaxeSwitch.get()).swap(slot))) {
                     this.sendSequenced((s) -> {
                        return new class_2846(class_2847.field_12973, this.minePos, dir, s);
                     });
                     SettingUtils.mineSwing(SwingSettings.MiningSwingState.End);
                     if ((Boolean)this.mineEndSwing.get()) {
                        this.clientSwing((SwingHand)this.mineHand.get(), class_1268.field_5808);
                     }

                     if (!(Boolean)this.packet.get()) {
                        BlackOut.mc.field_1687.method_8501(this.minePos, class_2246.field_10124.method_9564());
                     }

                     Managers.BLOCK.set(this.minePos, class_2246.field_10124, true, true);
                     Managers.ENTITY.addSpawning(this.minePos);
                     if (this.shouldInstant()) {
                        this.prevMined = this.minePos;
                     } else if ((Boolean)this.manualRemine.get() && this.mineType == AutoMine.MineType.Manual) {
                        if ((Boolean)this.fastRemine.get()) {
                           this.start(this.minePos, true);
                        } else {
                           this.started = false;
                        }
                     } else {
                        this.prevMined = null;
                        this.started = false;
                        this.minePos = null;
                     }

                     this.end("mining");
                     if (this.crystalPos != null && this.shouldAttack()) {
                        this.crystals.add(this.crystalPos, (Double)this.attackTime.get());
                     }

                     if (switched) {
                        ((SwitchMode)this.pickaxeSwitch.get()).swapBack();
                     }

                  }
               }
            }
         }
      }
   }

   private boolean isMining(class_2338 pos) {
      if (!this.started) {
         return false;
      } else if (!pos.equals(this.minePos)) {
         return false;
      } else {
         return this.getProgress() <= 1.0D;
      }
   }

   private boolean shouldInstant() {
      boolean var10000;
      switch(this.mineType) {
      case Cev:
         var10000 = (Boolean)this.instantCev.get();
         break;
      case TrapCev:
         var10000 = (Boolean)this.instantTrapCev.get();
         break;
      case SurroundCev:
         var10000 = (Boolean)this.instantSurroundCev.get();
         break;
      case AutoCity:
         var10000 = (Boolean)this.instantAutoCity.get();
         break;
      case Manual:
         var10000 = (Boolean)this.manualInstant.get();
         break;
      default:
         var10000 = false;
      }

      return var10000;
   }

   public void onStart(class_2338 pos) {
      if (this.mineType == AutoMine.MineType.Manual && pos.equals(this.minePos)) {
         if (!this.isMining(pos)) {
            this.started = false;
         }

         this.minePos = null;
      } else {
         if ((Boolean)this.manualMine.get() && this.getBlock(pos) != class_2246.field_9987) {
            this.started = false;
            this.minePos = pos;
            this.mineType = AutoMine.MineType.Manual;
            this.crystalPos = null;
         }

      }
   }

   public void onAbort(class_2338 pos) {
   }

   public void onStop(class_2338 pos) {
   }

   private boolean shouldAttack() {
      boolean var10000;
      switch(this.mineType) {
      case Cev:
      case TrapCev:
      case SurroundCev:
         var10000 = true;
         break;
      case AutoCity:
         var10000 = (Boolean)this.attackCrystal.get();
         break;
      case Manual:
      case SurroundMiner:
      case AntiBurrow:
         var10000 = false;
         break;
      default:
         throw new IncompatibleClassChangeError();
      }

      return var10000;
   }

   private class_1511 crystalAt(class_2338 pos) {
      Iterator var2 = BlackOut.mc.field_1687.method_18112().iterator();

      while(var2.hasNext()) {
         class_1297 entity = (class_1297)var2.next();
         if (entity instanceof class_1511) {
            class_1511 crystal = (class_1511)entity;
            if (entity.method_24515().equals(pos)) {
               return crystal;
            }
         }
      }

      return null;
   }

   private boolean placeCrystal(class_2338 pos) {
      if (this.paused(true)) {
         return false;
      } else if ((double)(System.currentTimeMillis() - this.lastPlace) < 1000.0D / (Double)this.placeSpeed.get()) {
         return false;
      } else {
         class_2350 placeDir = SettingUtils.getPlaceOnDirection(pos);
         if (placeDir == null) {
            return false;
         } else {
            class_1268 hand = OLEPOSSUtils.getHand(class_1802.field_8301);
            boolean switched = false;
            FindResult result = ((SwitchMode)this.crystalSwitch.get()).find(class_1802.field_8301);
            if (hand == null && !result.wasFound()) {
               return false;
            } else if (SettingUtils.shouldRotate(RotationType.Interact) && !this.rotateBlock(pos, placeDir, this.getPlaceRotationVec(pos), RotationType.Interact, 0.1D, "crystal")) {
               return false;
            } else if (hand == null && !(switched = ((SwitchMode)this.crystalSwitch.get()).swap(result.slot()))) {
               return false;
            } else {
               if ((Boolean)this.placeSwing.get()) {
                  this.clientSwing((SwingHand)this.placeHand.get(), hand);
               }

               this.interactBlock(hand, pos.method_46558(), placeDir, pos);
               this.lastPlace = System.currentTimeMillis();
               this.end("crystal");
               if (switched) {
                  ((SwitchMode)this.crystalSwitch.get()).swapBack();
               }

               return true;
            }
         }
      }
   }

   private void start(class_2338 pos, boolean isRemine) {
      class_2350 dir = SettingUtils.getPlaceOnDirection(pos);
      if (dir != null) {
         if (this.prevPos != null && !this.prevPos.equals(pos)) {
            this.abort(this.prevPos);
         }

         this.currentProgress = 0.0D;
         this.prevProgress = 0.0D;
         this.started = true;
         this.startedThisTick = true;
         this.progress = 0.0D;
         this.minedFor = 0;
         if (!pos.equals(this.prevMined)) {
            this.prevMined = null;
            if (!isRemine && (Boolean)this.preSwitch.get()) {
               int slot = this.findBestSlot((stack) -> {
                  return BlockUtils.getBlockBreakingDelta(this.minePos, stack, (Boolean)this.effectCheck.get(), (Boolean)this.waterCheck.get(), (Boolean)this.onGroundCheck.get() && !(Boolean)this.onGroundSpoof.get());
               }).slot();
               ((SwitchMode)this.pickaxeSwitch.get()).swap(slot);
            }

            this.sendSequenced((s) -> {
               return new class_2846(class_2847.field_12968, pos, dir, s);
            });
            SettingUtils.mineSwing(SwingSettings.MiningSwingState.Start);
            this.end("mining");
            if (!isRemine && (Boolean)this.preSwitch.get()) {
               ((SwitchMode)this.pickaxeSwitch.get()).swapBack();
            }

            if ((Boolean)this.mineStartSwing.get()) {
               this.clientSwing((SwingHand)this.mineHand.get(), class_1268.field_5808);
            }

         }
      }
   }

   private void abort(class_2338 pos) {
      this.sendPacket(new class_2846(class_2847.field_12971, pos, class_2350.field_11033, 0));
      this.started = false;
   }

   private boolean itemMinedCheck(class_1799 stack) {
      if (!(Boolean)this.ncpProgress.get()) {
         return this.progress * (Double)this.speed.get() >= 1.0D;
      } else {
         return (double)this.minedFor * (Double)this.speed.get() >= Math.ceil(1.0D / BlockUtils.getBlockBreakingDelta(this.minePos, stack, (Boolean)this.effectCheck.get(), (Boolean)this.waterCheck.get(), (Boolean)this.onGroundCheck.get() || (Boolean)this.onGroundSpoof.get()));
      }
   }

   private boolean minedCheck(class_1799 stack) {
      if (this.minePos.equals(this.prevMined)) {
         return true;
      } else if (this.itemMinedCheck(stack)) {
         return true;
      } else {
         if ((Boolean)this.onGroundSpoof.get()) {
            Managers.PACKET.spoofOG(true);
         }

         return false;
      }
   }

   private boolean almostMined(class_1799 stack) {
      if (this.getBlock(this.minePos) instanceof class_2189) {
         return false;
      } else if (!SettingUtils.inMineRange(this.minePos)) {
         return false;
      } else if (SettingUtils.getPlaceOnDirection(this.minePos) == null) {
         return false;
      } else if (!(Boolean)this.ncpProgress.get()) {
         return this.progress >= 0.9D;
      } else {
         return (double)(this.minedFor + 2) >= Math.ceil(1.0D / BlockUtils.getBlockBreakingDelta(this.minePos, stack, (Boolean)this.effectCheck.get(), (Boolean)this.waterCheck.get(), (Boolean)this.onGroundCheck.get() || (Boolean)this.onGroundSpoof.get()));
      }
   }

   public double getCurrentProgress() {
      return this.currentProgress;
   }

   private class_243 getMineStartRotationVec(class_2350 dir) {
      return this.minePos.method_46558();
   }

   private class_243 getMineEndRotationVec() {
      switch(this.mineType) {
      case Cev:
      case TrapCev:
         return new class_243((double)this.minePos.method_10263() + 0.5D, (double)(this.minePos.method_10264() + 1), (double)this.minePos.method_10260() + 0.5D);
      case SurroundCev:
      case SurroundMiner:
         return new class_243((double)this.minePos.method_10263() + 0.5D, (double)this.minePos.method_10264(), (double)this.minePos.method_10260() + 0.5D);
      case AutoCity:
         double x = (double)Integer.compare(this.crystalPos.method_10263(), this.minePos.method_10263()) * 0.4D;
         double z = (double)Integer.compare(this.crystalPos.method_10260(), this.minePos.method_10260()) * 0.4D;
         return new class_243((double)this.minePos.method_10263() + 0.5D + x, (double)this.minePos.method_10264() + 0.1D, (double)this.minePos.method_10260() + 0.5D + z);
      case Manual:
         if (this.crystalBlock(this.minePos, true)) {
            return new class_243((double)this.minePos.method_10263() + 0.5D, (double)(this.minePos.method_10264() + 1), (double)this.minePos.method_10260() + 0.5D);
         }

         return this.minePos.method_46558();
      default:
         return this.minePos.method_46558();
      }
   }

   private class_243 getPlaceRotationVec(class_2338 pos) {
      switch(this.mineType) {
      case Cev:
      case TrapCev:
         return new class_243((double)this.minePos.method_10263() + 0.5D, (double)(this.minePos.method_10264() + 1), (double)this.minePos.method_10260() + 0.5D);
      case SurroundCev:
      case SurroundMiner:
         return new class_243((double)this.minePos.method_10263() + 0.5D, (double)this.minePos.method_10264(), (double)this.minePos.method_10260() + 0.5D);
      case AutoCity:
         double x = (double)Integer.compare(this.minePos.method_10263(), pos.method_10263()) / 3.0D;
         double z = (double)Integer.compare(this.minePos.method_10260(), pos.method_10260()) / 3.0D;
         return new class_243((double)pos.method_10263() + 0.5D + x, (double)pos.method_10264() + 0.9D, (double)pos.method_10260() + 0.5D + z);
      case Manual:
      default:
         return new class_243((double)pos.method_10263() + 0.5D, (double)pos.method_10264(), (double)pos.method_10260() + 0.5D);
      }
   }

   private class_243 getAttackRotationVec(class_1511 entity) {
      return entity.method_19538();
   }

   public static enum Priority {
      Highest(6),
      Higher(5),
      High(4),
      Normal(3),
      Low(2),
      Lower(1),
      Lowest(0),
      Disabled(-1);

      public final int priority;

      private Priority(int priority) {
         this.priority = priority;
      }

      // $FF: synthetic method
      private static AutoMine.Priority[] $values() {
         return new AutoMine.Priority[]{Highest, Higher, High, Normal, Low, Lower, Lowest, Disabled};
      }
   }

   public static enum AnimationMode {
      Full,
      Up,
      Down,
      Double,
      None;

      // $FF: synthetic method
      private static AutoMine.AnimationMode[] $values() {
         return new AutoMine.AnimationMode[]{Full, Up, Down, Double, None};
      }
   }

   public static enum MineType {
      Cev(true),
      TrapCev(true),
      SurroundCev(true),
      SurroundMiner(false),
      AutoCity(false),
      AntiBurrow(false),
      Manual(false);

      public final boolean cev;

      private MineType(boolean cev) {
         this.cev = cev;
      }

      // $FF: synthetic method
      private static AutoMine.MineType[] $values() {
         return new AutoMine.MineType[]{Cev, TrapCev, SurroundCev, SurroundMiner, AutoCity, AntiBurrow, Manual};
      }
   }

   private static record Target(class_2338 pos, class_2338 crystal, AutoMine.MineType type, int priority, class_1657 target) {
      private Target(class_2338 pos, class_2338 crystal, AutoMine.MineType type, int priority, class_1657 target) {
         this.pos = pos;
         this.crystal = crystal;
         this.type = type;
         this.priority = priority;
         this.target = target;
      }

      public class_2338 pos() {
         return this.pos;
      }

      public class_2338 crystal() {
         return this.crystal;
      }

      public AutoMine.MineType type() {
         return this.type;
      }

      public int priority() {
         return this.priority;
      }

      public class_1657 target() {
         return this.target;
      }
   }

   public static enum SwitchState {
      Start,
      End,
      Both;

      // $FF: synthetic method
      private static AutoMine.SwitchState[] $values() {
         return new AutoMine.SwitchState[]{Start, End, Both};
      }
   }
}
