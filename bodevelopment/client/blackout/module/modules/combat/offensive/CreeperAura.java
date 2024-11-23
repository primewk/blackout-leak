package bodevelopment.client.blackout.module.modules.combat.offensive;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.RenderShape;
import bodevelopment.client.blackout.enums.RotationType;
import bodevelopment.client.blackout.enums.SwingHand;
import bodevelopment.client.blackout.enums.SwitchMode;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.keys.KeyBind;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.modules.combat.misc.Suicide;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.randomstuff.ExtrapolationMap;
import bodevelopment.client.blackout.randomstuff.FindResult;
import bodevelopment.client.blackout.randomstuff.PlaceData;
import bodevelopment.client.blackout.util.BoxUtils;
import bodevelopment.client.blackout.util.DamageUtils;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import bodevelopment.client.blackout.util.RotationUtils;
import bodevelopment.client.blackout.util.SettingUtils;
import bodevelopment.client.blackout.util.render.Render3DUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import net.minecraft.class_1268;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2189;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;

public class CreeperAura extends Module {
   private static CreeperAura INSTANCE;
   private final SettingGroup sgPlace = this.addGroup("Place");
   private final SettingGroup sgSlow = this.addGroup("Slow");
   private final SettingGroup sgFacePlace = this.addGroup("Face Place");
   private final SettingGroup sgDamage = this.addGroup("Damage");
   private final SettingGroup sgExtrapolation = this.addGroup("Extrapolation");
   private final SettingGroup sgRender = this.addGroup("Render");
   private final SettingGroup sgCalculation = this.addGroup("Calculations");
   private final Setting<Boolean> place;
   private final Setting<Boolean> pauseEat;
   private final Setting<Double> placeSpeed;
   private final Setting<SwitchMode> switchMode;
   private final Setting<Double> slowDamage;
   private final Setting<Double> slowSpeed;
   private final Setting<Double> slowHealth;
   private final Setting<KeyBind> holdFacePlace;
   private final Setting<Double> facePlaceHealth;
   private final Setting<Double> armorFacePlace;
   private final Setting<Double> facePlaceDamage;
   private final Setting<Boolean> ignoreSlow;
   private final Setting<Double> minPlace;
   private final Setting<Boolean> checkSelfPlacing;
   private final Setting<Double> maxSelfPlace;
   private final Setting<Double> minSelfRatio;
   private final Setting<Boolean> checkFriendPlacing;
   private final Setting<Double> maxFriendPlace;
   private final Setting<Double> minFriendRatio;
   private final Setting<Double> forcePop;
   private final Setting<Double> selfPop;
   private final Setting<Double> friendPop;
   private final Setting<Integer> extrapolation;
   private final Setting<Integer> selfExt;
   private final Setting<Boolean> placeSwing;
   private final Setting<SwingHand> placeHand;
   private final Setting<Boolean> render;
   private final Setting<Double> renderTime;
   private final Setting<Double> fadeTime;
   private final Setting<RenderShape> renderShape;
   private final Setting<BlackOutColor> lineColor;
   private final Setting<BlackOutColor> sideColor;
   private final Setting<Double> damageValue;
   private final Setting<Double> selfDmgValue;
   private final Setting<Double> friendDmgValue;
   private final Setting<Double> rotationValue;
   private final Setting<Integer> maxTargets;
   private final Setting<Double> enemyDistance;
   private final ExtrapolationMap extMap;
   private final List<class_1657> targets;
   private class_2338 placePos;
   private double selfHealth;
   private double enemyHealth;
   private double friendHealth;
   private double selfDamage;
   private double enemyDamage;
   private double friendDamage;
   private class_1309 target;
   private class_2338 calcBest;
   private double calcValue;
   private int calcR;
   private class_2338 calcMiddle;
   private int progress;
   private long lastPlace;
   private boolean suicide;
   private boolean facePlacing;
   private class_2338 renderPos;
   private double renderProgress;

   public CreeperAura() {
      super("Creeper Aura", "Places and blows up beds.", SubCategory.OFFENSIVE, true);
      this.place = this.sgPlace.b("Place", true, "Places crystals.");
      this.pauseEat = this.sgPlace.b("Pause Eat Place", false, "Pauses placing while eating.");
      this.placeSpeed = this.sgPlace.d("Place Speed", 20.0D, 0.0D, 20.0D, 0.1D, ".");
      this.switchMode = this.sgPlace.e("Switch", SwitchMode.Silent, "Mode for switching to beds in main hand.");
      this.slowDamage = this.sgSlow.d("Slow Damage", 3.0D, 0.0D, 20.0D, 0.1D, "Switches to slow speed when the target would take under this amount of damage.");
      this.slowSpeed = this.sgSlow.d("Slow Speed", 2.0D, 0.0D, 20.0D, 0.1D, "How many times should the module place per second when damage is under slow damage.");
      this.slowHealth = this.sgSlow.d("Slow Health", 10.0D, 0.0D, 20.0D, 0.5D, "Only slow places if enemy has over x health.");
      this.holdFacePlace = this.sgFacePlace.k("Hold Face Place", "Faceplaces when holding this key.");
      this.facePlaceHealth = this.sgFacePlace.d("Face Place Health", 0.0D, 0.0D, 10.0D, 0.1D, "Automatically face places if enemy has under this much health.");
      this.armorFacePlace = this.sgFacePlace.d("Armor Face Place", 10.0D, 0.0D, 100.0D, 1.0D, "Face places if enemy's any armor piece is under this durability.");
      this.facePlaceDamage = this.sgFacePlace.d("Face Place Damage", 0.0D, 0.0D, 10.0D, 0.1D, "Sets min place and min attack to this.");
      this.ignoreSlow = this.sgFacePlace.b("Ignore Slow", true, "Doesn't slow place when faceplacing.");
      this.minPlace = this.sgDamage.d("Min Place", 5.0D, 0.0D, 20.0D, 0.1D, "Minimum damage to place.");
      this.checkSelfPlacing = this.sgDamage.b("Self Placing", true, "Checks self damage when placing.");
      SettingGroup var10001 = this.sgDamage;
      Setting var10008 = this.checkSelfPlacing;
      Objects.requireNonNull(var10008);
      this.maxSelfPlace = var10001.d("Max Place", 10.0D, 0.0D, 20.0D, 0.1D, "Max self damage for placing.", var10008::get);
      var10001 = this.sgDamage;
      var10008 = this.checkSelfPlacing;
      Objects.requireNonNull(var10008);
      this.minSelfRatio = var10001.d("Min Place Ratio", 2.0D, 0.0D, 20.0D, 0.1D, "Min self damage ratio for placing (enemy / self).", var10008::get);
      this.checkFriendPlacing = this.sgDamage.b("Friend Placing", true, "Checks friend damage when placing.");
      var10001 = this.sgDamage;
      var10008 = this.checkFriendPlacing;
      Objects.requireNonNull(var10008);
      this.maxFriendPlace = var10001.d("Max Friend Place", 12.0D, 0.0D, 20.0D, 0.1D, "Max friend damage for placing.", var10008::get);
      var10001 = this.sgDamage;
      var10008 = this.checkFriendPlacing;
      Objects.requireNonNull(var10008);
      this.minFriendRatio = var10001.d("Min Friend Place Ratio", 1.0D, 0.0D, 20.0D, 0.1D, "Min friend damage ratio for placing (enemy / friend).", var10008::get);
      this.forcePop = this.sgDamage.d("Force Pop", 0.0D, 0.0D, 5.0D, 0.25D, "Ignores damage checks if any enemy will be popped in x hits.");
      this.selfPop = this.sgDamage.d("Anti Pop", 1.0D, 0.0D, 5.0D, 0.25D, "Ignores damage checks if any enemy will be popped in x hits.");
      this.friendPop = this.sgDamage.d("Anti Friend Pop", 0.0D, 0.0D, 5.0D, 0.25D, "Ignores damage checks if any enemy will be popped in x hits.");
      this.extrapolation = this.sgExtrapolation.i("Extrapolation", 0, 0, 20, 1, "How many ticks of movement should be predicted for enemy damage checks.");
      this.selfExt = this.sgExtrapolation.i("Self Extrapolation", 0, 0, 20, 1, "How many ticks of movement should be predicted for self damage checks.");
      this.placeSwing = this.sgRender.b("Place Swing", false, "Renders swing animation when placing a crystal.");
      this.placeHand = this.sgRender.e("Place Hand", SwingHand.RealHand, "Which hand should be swung.");
      this.render = this.sgRender.b("Render Box", true, "Renders box on placement.");
      var10001 = this.sgRender;
      var10008 = this.render;
      Objects.requireNonNull(var10008);
      this.renderTime = var10001.d("Box Render Time", 0.3D, 0.0D, 10.0D, 0.1D, "How long the box should remain in full alpha value.", var10008::get);
      var10001 = this.sgRender;
      var10008 = this.render;
      Objects.requireNonNull(var10008);
      this.fadeTime = var10001.d("Box Fade Time", 1.0D, 0.0D, 10.0D, 0.1D, "How long the fading should take.", var10008::get);
      var10001 = this.sgRender;
      RenderShape var10003 = RenderShape.Full;
      Setting var10005 = this.render;
      Objects.requireNonNull(var10005);
      this.renderShape = var10001.e("Box Render Shape", var10003, "Which parts of render should be rendered.", var10005::get);
      var10001 = this.sgRender;
      BlackOutColor var1 = new BlackOutColor(255, 0, 0, 255);
      var10005 = this.render;
      Objects.requireNonNull(var10005);
      this.lineColor = var10001.c("Box Line Color", var1, "Line color of rendered boxes.", var10005::get);
      var10001 = this.sgRender;
      var1 = new BlackOutColor(255, 0, 0, 50);
      var10005 = this.render;
      Objects.requireNonNull(var10005);
      this.sideColor = var10001.c("Box Side Color", var1, "Side color of rendered boxes.", var10005::get);
      this.damageValue = this.sgCalculation.d("Damage Value", 1.0D, -2.0D, 2.0D, 0.05D, ".");
      this.selfDmgValue = this.sgCalculation.d("Self Damage Value", -1.0D, -2.0D, 2.0D, 0.05D, ".");
      this.friendDmgValue = this.sgCalculation.d("Friend Damage Value", 0.0D, -2.0D, 2.0D, 0.05D, ".");
      this.rotationValue = this.sgCalculation.d("Rotation Value", 3.0D, -5.0D, 10.0D, 0.1D, ".");
      this.maxTargets = this.sgCalculation.i("Max Targets", 3, 1, 10, 1, ".");
      this.enemyDistance = this.sgCalculation.d("Enemy Distance", 10.0D, 0.0D, 100.0D, 1.0D, ".");
      this.extMap = new ExtrapolationMap();
      this.targets = new ArrayList();
      this.placePos = null;
      this.selfHealth = 0.0D;
      this.enemyHealth = 0.0D;
      this.friendHealth = 0.0D;
      this.selfDamage = 0.0D;
      this.enemyDamage = 0.0D;
      this.friendDamage = 0.0D;
      this.target = null;
      this.calcBest = null;
      this.calcValue = 0.0D;
      this.calcR = 0;
      this.calcMiddle = null;
      this.progress = 0;
      this.lastPlace = 0L;
      this.suicide = false;
      this.facePlacing = false;
      this.renderPos = null;
      this.renderProgress = 0.0D;
      INSTANCE = this;
   }

   public static CreeperAura getInstance() {
      return INSTANCE;
   }

   @Event
   public void onTick(TickEvent.Post event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         this.calc(1.0F);
         this.updatePos();
      }

   }

   @Event
   public void onRender(RenderEvent.World.Post event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         this.updateFacePlacing();
         this.calc(event.tickDelta);
         this.updateRender();
         if (this.placePos != null && (Boolean)this.place.get() && !this.paused()) {
            this.renderPos = this.placePos;
            this.renderProgress = 0.0D;
            this.updatePlace();
         } else {
            this.renderProgress = Math.min(this.renderProgress + event.frameTime, (Double)this.renderTime.get() + (Double)this.fadeTime.get());
         }
      }
   }

   private void updateRender() {
      if ((Boolean)this.render.get() && this.renderPos != null && this.renderProgress < (Double)this.renderTime.get() + (Double)this.fadeTime.get()) {
         this.render(this.renderPos, this.lineColor, this.sideColor, this.renderShape, this.getAlpha(this.renderProgress, this.renderTime, this.fadeTime));
      }

   }

   private double getAlpha(double time, Setting<Double> rt, Setting<Double> ft) {
      return 1.0D - Math.max(time - (Double)rt.get(), 0.0D) / (Double)ft.get();
   }

   private void render(class_2338 feetPos, Setting<BlackOutColor> lines, Setting<BlackOutColor> sides, Setting<RenderShape> shape, double alpha) {
      class_238 box = this.getBoxAt(feetPos);
      Render3DUtils.box(box, ((BlackOutColor)sides.get()).alphaMulti(alpha), ((BlackOutColor)lines.get()).alphaMulti(alpha), (RenderShape)shape.get());
   }

   private class_238 getBoxAt(class_2338 pos) {
      return new class_238((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)pos.method_10264() + 0.55D, (double)(pos.method_10260() + 1));
   }

   private boolean paused() {
      return (Boolean)this.pauseEat.get() && BlackOut.mc.field_1724.method_6115();
   }

   private void updatePlace() {
      if (OLEPOSSUtils.replaceable(this.placePos)) {
         if (this.placeDelayCheck()) {
            this.place();
         }
      }
   }

   private boolean placeDelayCheck() {
      return (double)(System.currentTimeMillis() - this.lastPlace) >= 1000.0D / this.shouldSlow() ? (Double)this.slowSpeed.get() : (Double)this.placeSpeed.get();
   }

   private void updateFacePlacing() {
      this.facePlacing = ((KeyBind)this.holdFacePlace.get()).isPressed();
   }

   private boolean shouldFacePlace() {
      if (this.facePlacing) {
         return true;
      } else if (this.enemyHealth <= (Double)this.facePlaceHealth.get()) {
         return true;
      } else if (this.target == null) {
         return false;
      } else {
         Iterator var1 = this.target.method_5661().iterator();

         class_1799 stack;
         do {
            if (!var1.hasNext()) {
               return false;
            }

            stack = (class_1799)var1.next();
         } while(!stack.method_7963() || !(1.0D - (double)stack.method_7919() / (double)stack.method_7936() <= (Double)this.armorFacePlace.get() / 100.0D));

         return true;
      }
   }

   private boolean shouldSlow() {
      this.calcDamage(this.placePos);
      if ((Boolean)this.ignoreSlow.get() && this.shouldFacePlace()) {
         return false;
      } else if (this.enemyHealth < (Double)this.slowHealth.get()) {
         return false;
      } else {
         return this.enemyDamage <= (Double)this.slowDamage.get();
      }
   }

   private void place() {
      PlaceData data = SettingUtils.getPlaceData(this.placePos);
      if (data.valid()) {
         class_1268 hand = OLEPOSSUtils.getHand(class_1802.field_8503);
         FindResult result = ((SwitchMode)this.switchMode.get()).find(class_1802.field_8503);
         if (hand != null || result.wasFound()) {
            if (!SettingUtils.shouldRotate(RotationType.Interact) || this.rotateBlock(data, data.pos().method_46558().method_43206(data.dir(), 0.5D), RotationType.BlockPlace, "placing")) {
               boolean switched = false;
               if (hand != null || (switched = ((SwitchMode)this.switchMode.get()).swap(result.slot()))) {
                  this.interactBlock(hand, data.pos().method_46558(), data.dir(), data.pos());
                  this.lastPlace = System.currentTimeMillis();
                  if ((Boolean)this.placeSwing.get()) {
                     this.clientSwing((SwingHand)this.placeHand.get(), hand);
                  }

                  this.end("placing");
                  if (switched) {
                     ((SwitchMode)this.switchMode.get()).swapBack();
                  }

               }
            }
         }
      }
   }

   private void calc(float tickDelta) {
      if (this.calcMiddle != null) {
         int d = this.calcR * 2 + 1;
         int target = d * d * d;

         for(int i = this.progress; (float)i < (float)target * tickDelta; ++i) {
            this.progress = i;
            int x = i % d - this.calcR;
            int y = i / d % d - this.calcR;
            int z = i / d / d % d - this.calcR;
            this.calcPos(this.calcMiddle.method_10069(x, y, z));
         }

      }
   }

   private boolean placeDamageCheck() {
      if (this.selfDamage * (Double)this.selfPop.get() > this.selfHealth) {
         return false;
      } else if (this.friendDamage * (Double)this.friendPop.get() > this.friendHealth) {
         return false;
      } else if (this.enemyDamage * (Double)this.forcePop.get() > this.enemyHealth) {
         return true;
      } else if (this.enemyDamage < this.shouldFacePlace() ? (Double)this.facePlaceDamage.get() : (Double)this.minPlace.get()) {
         return false;
      } else {
         if ((Boolean)this.checkSelfPlacing.get()) {
            if (this.selfDamage > (Double)this.maxSelfPlace.get()) {
               return false;
            }

            if (this.enemyDamage / this.selfDamage < (Double)this.minSelfRatio.get()) {
               return false;
            }
         }

         if ((Boolean)this.checkFriendPlacing.get()) {
            if (this.friendDamage > (Double)this.maxFriendPlace.get()) {
               return false;
            } else {
               return !(this.enemyDamage / this.friendDamage < (Double)this.minFriendRatio.get());
            }
         } else {
            return true;
         }
      }
   }

   private void calcPos(class_2338 pos) {
      if (BlackOut.mc.field_1687.method_8320(pos).method_26204() instanceof class_2189) {
         if (this.inRangeToEnemies(pos)) {
            PlaceData data = SettingUtils.getPlaceData(pos);
            if (data.valid()) {
               if (SettingUtils.inInteractRange(data.pos())) {
                  this.calcDamage(pos);
                  if (this.placeDamageCheck()) {
                     double value = this.getValue(pos, true);
                     if (!(value <= this.calcValue)) {
                        this.calcBest = pos;
                        this.calcValue = value;
                     }
                  }
               }
            }
         }
      }
   }

   private void updatePos() {
      Suicide suicideModule = Suicide.getInstance();
      this.suicide = suicideModule.enabled && (Boolean)suicideModule.useCreeper.get();
      this.findTargets();
      this.extMap.update((player) -> {
         return player == BlackOut.mc.field_1724 ? (Integer)this.selfExt.get() : (Integer)this.extrapolation.get();
      });
      this.placePos = this.calcBest;
      this.startCalc();
   }

   private void startCalc() {
      this.selfHealth = this.getHealth(BlackOut.mc.field_1724);
      this.calcBest = null;
      this.calcValue = -42069.0D;
      this.progress = 0;
      this.calcR = (int)Math.ceil(SettingUtils.maxInteractRange());
      this.calcMiddle = class_2338.method_49638(BlackOut.mc.field_1724.method_33571());
   }

   private double getValue(class_2338 pos, boolean place) {
      double value = 0.0D;
      if (place && SettingUtils.shouldRotate(RotationType.BlockPlace) || !place && SettingUtils.shouldRotate(RotationType.Interact)) {
         value += this.rotationMod(pos.method_46558());
      }

      value += this.enemyMod();
      value += this.selfMod();
      value += this.friendMod();
      return value;
   }

   private double enemyMod() {
      return this.enemyDamage * (Double)this.damageValue.get();
   }

   private double selfMod() {
      return this.selfDamage * (Double)this.selfDmgValue.get();
   }

   private double friendMod() {
      return this.friendDamage * (Double)this.friendDmgValue.get();
   }

   private double rotationMod(class_243 pos) {
      double yawStep = 45.0D;
      double pitchStep = 22.0D;
      int yawSteps = (int)Math.ceil(Math.abs(RotationUtils.yawAngle((double)Managers.ROTATION.prevYaw, RotationUtils.getYaw(pos)) / yawStep));
      int pitchSteps = (int)Math.ceil(Math.abs(RotationUtils.pitchAngle((double)Managers.ROTATION.prevPitch, RotationUtils.getPitch(pos)) / pitchStep));
      int steps = Math.max(yawSteps, pitchSteps);
      return (double)(3 - Math.min(steps, 3)) * (Double)this.rotationValue.get();
   }

   private boolean inRangeToEnemies(class_2338 pos) {
      class_243 vec = pos.method_46558();
      if (this.suicide) {
         return BoxUtils.middle(BlackOut.mc.field_1724.method_5829()).method_1022(vec) < 3.0D;
      } else {
         Iterator var3 = this.targets.iterator();

         class_1657 player;
         do {
            if (!var3.hasNext()) {
               return false;
            }

            player = (class_1657)var3.next();
         } while(!(BoxUtils.middle(player.method_5829()).method_1022(vec) < 3.0D));

         return true;
      }
   }

   private void findTargets() {
      Map<class_1657, Double> map = new HashMap();
      Iterator var2 = BlackOut.mc.field_1687.method_18456().iterator();

      while(true) {
         while(true) {
            class_1657 player;
            double distance;
            do {
               do {
                  do {
                     if (!var2.hasNext()) {
                        this.targets.clear();
                        map.forEach((playerx, d) -> {
                           this.targets.add(playerx);
                        });
                        return;
                     }

                     player = (class_1657)var2.next();
                  } while(player == BlackOut.mc.field_1724);
               } while(player.method_6032() <= 0.0F);

               distance = (double)BlackOut.mc.field_1724.method_5739(player);
            } while(distance > (Double)this.enemyDistance.get());

            if (map.size() < (Integer)this.maxTargets.get()) {
               map.put(player, distance);
            } else {
               Iterator var6 = map.entrySet().iterator();

               while(var6.hasNext()) {
                  Entry<class_1657, Double> entry = (Entry)var6.next();
                  if ((Double)entry.getValue() > distance) {
                     map.remove(entry.getKey());
                     map.put(player, distance);
                     break;
                  }
               }
            }
         }
      }
   }

   private void calcDamage(class_2338 pos) {
      class_243 vec = pos.method_46558();
      this.selfDamage = DamageUtils.creeperDamage(BlackOut.mc.field_1724, this.extMap.get(BlackOut.mc.field_1724), vec, pos);
      this.enemyDamage = 0.0D;
      this.friendDamage = 0.0D;
      if (this.suicide) {
         this.enemyDamage = this.selfDamage;
         this.selfDamage = 0.0D;
         this.friendDamage = 0.0D;
         this.enemyHealth = 20.0D;
         this.friendHealth = 36.0D;
      } else {
         this.enemyHealth = 20.0D;
         this.friendHealth = 20.0D;
         this.targets.forEach((player) -> {
            class_238 box = this.extMap.get(player);
            if (!(player.method_6032() <= 0.0F) && player != BlackOut.mc.field_1724) {
               double dmg = DamageUtils.creeperDamage(player, box, vec, pos);
               double health = this.getHealth(player);
               if (Managers.FRIENDS.isFriend(player)) {
                  if (dmg > this.friendDamage) {
                     this.friendDamage = dmg;
                     this.friendHealth = health;
                  }
               } else if (dmg > this.enemyDamage) {
                  this.enemyDamage = dmg;
                  this.enemyHealth = health;
                  this.target = player;
               }

            }
         });
      }
   }

   private double getHealth(class_1309 entity) {
      return (double)(entity.method_6032() + entity.method_6067());
   }
}
