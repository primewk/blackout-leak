package bodevelopment.client.blackout.module.modules.combat.offensive;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.RenderShape;
import bodevelopment.client.blackout.enums.RotationType;
import bodevelopment.client.blackout.enums.SwingHand;
import bodevelopment.client.blackout.enums.SwitchMode;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.randomstuff.ExtrapolationMap;
import bodevelopment.client.blackout.randomstuff.FindResult;
import bodevelopment.client.blackout.randomstuff.PlaceData;
import bodevelopment.client.blackout.util.BoxUtils;
import bodevelopment.client.blackout.util.DamageUtils;
import bodevelopment.client.blackout.util.EntityUtils;
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
import java.util.function.Predicate;
import net.minecraft.class_1268;
import net.minecraft.class_1309;
import net.minecraft.class_1542;
import net.minecraft.class_1657;
import net.minecraft.class_1747;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_2741;
import net.minecraft.class_4969;

public class AnchorAura extends Module {
   public final SettingGroup sgGeneral = this.addGroup("General");
   public final SettingGroup sgDamage = this.addGroup("Damage");
   public final SettingGroup sgExtrapolation = this.addGroup("Extrapolation");
   public final SettingGroup sgRender = this.addGroup("Render");
   private final Setting<Double> enemyDistance;
   private final Setting<Double> placeSpeed;
   private final Setting<Double> interactSpeed;
   private final Setting<Double> explodeSpeed;
   private final Setting<SwitchMode> switchMode;
   private final Setting<Double> minPlace;
   private final Setting<Boolean> checkSelfPlacing;
   private final Setting<Double> maxSelfPlace;
   private final Setting<Double> minSelfRatio;
   private final Setting<Boolean> checkFriendPlacing;
   private final Setting<Double> maxFriendPlace;
   private final Setting<Double> minFriendRatio;
   private final Setting<Boolean> checkEnemyExplode;
   private final Setting<Double> minExplode;
   private final Setting<Boolean> checkSelfExplode;
   private final Setting<Double> maxSelfExplode;
   private final Setting<Double> minSelfExplodeRatio;
   private final Setting<Boolean> checkFriendExplode;
   private final Setting<Double> maxFriendExplode;
   private final Setting<Double> minFriendExplodeRatio;
   private final Setting<Double> forcePop;
   private final Setting<Double> selfPop;
   private final Setting<Double> friendPop;
   private final Setting<Integer> extrapolation;
   private final Setting<Integer> selfExtrapolation;
   private final Setting<Integer> hitboxExtrapolation;
   private final Setting<Boolean> placeSwing;
   private final Setting<SwingHand> placeHand;
   private final Setting<RenderShape> renderShape;
   private final Setting<BlackOutColor> lineColor;
   private final Setting<BlackOutColor> sideColor;
   private class_2338 placePos;
   private class_2338 explodePos;
   private class_1309 target;
   private double selfHealth;
   private double enemyHealth;
   private double friendHealth;
   private double selfDamage;
   private double enemyDamage;
   private double friendDamage;
   private final ExtrapolationMap extMap;
   private final ExtrapolationMap hitboxMap;
   private final List<class_1657> enemies;
   private FindResult result;
   private long lastPlace;
   private long lastInteract;
   private long lastExplode;
   private int progress;
   private int targetProgress;
   private class_2338 calcBest;
   private double calcValue;
   private int calcR;
   private int targetCalcR;
   private class_2338 calcMiddle;
   private class_2338 targetCalcBest;
   private double targetCalcValue;
   private boolean bestIsLoaded;

   public AnchorAura() {
      super("Anchor Aura", "Places and blows up anchors.", SubCategory.OFFENSIVE, true);
      this.enemyDistance = this.sgGeneral.d("Enemy Distance", 10.0D, 0.0D, 100.0D, 1.0D, ".");
      this.placeSpeed = this.sgGeneral.d("Place Speed", 4.0D, 0.0D, 20.0D, 0.1D, ".");
      this.interactSpeed = this.sgGeneral.d("Load Speed", 2.0D, 0.0D, 20.0D, 0.1D, ".");
      this.explodeSpeed = this.sgGeneral.d("Explode Speed", 2.0D, 0.0D, 20.0D, 0.1D, ".");
      this.switchMode = this.sgGeneral.e("Switch Mode", SwitchMode.Silent, "Method of switching. Silent is the most reliable but delays crystals on some servers.");
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
      this.checkEnemyExplode = this.sgDamage.b("Enemy Explode", true, "Checks enemy damage when attacking.");
      var10001 = this.sgDamage;
      var10008 = this.checkEnemyExplode;
      Objects.requireNonNull(var10008);
      this.minExplode = var10001.d("Min Explode", 5.0D, 0.0D, 20.0D, 0.1D, "Minimum damage to attack.", var10008::get);
      this.checkSelfExplode = this.sgDamage.b("Self Explode", true, "Checks self damage when attacking.");
      var10001 = this.sgDamage;
      var10008 = this.checkSelfExplode;
      Objects.requireNonNull(var10008);
      this.maxSelfExplode = var10001.d("Max Explode", 10.0D, 0.0D, 20.0D, 0.1D, "Max self damage for attacking.", var10008::get);
      var10001 = this.sgDamage;
      var10008 = this.checkSelfExplode;
      Objects.requireNonNull(var10008);
      this.minSelfExplodeRatio = var10001.d("Min Explode Ratio", 2.0D, 0.0D, 20.0D, 0.1D, "Min self damage ratio for attacking (enemy / self).", var10008::get);
      this.checkFriendExplode = this.sgDamage.b("Friend Explode", true, "Checks friend damage when attacking.");
      var10001 = this.sgDamage;
      var10008 = this.checkFriendExplode;
      Objects.requireNonNull(var10008);
      this.maxFriendExplode = var10001.d("Max Friend Explode", 12.0D, 0.0D, 20.0D, 0.1D, "Max friend damage for attacking.", var10008::get);
      var10001 = this.sgDamage;
      var10008 = this.checkFriendExplode;
      Objects.requireNonNull(var10008);
      this.minFriendExplodeRatio = var10001.d("Min Friend Explode Ratio", 1.0D, 0.0D, 20.0D, 0.1D, "Min friend damage ratio for attacking (enemy / friend).", var10008::get);
      this.forcePop = this.sgDamage.d("Force Pop", 0.0D, 0.0D, 5.0D, 0.25D, "Ignores damage checks if any enemy will be popped in x hits.");
      this.selfPop = this.sgDamage.d("Anti Pop", 1.0D, 0.0D, 5.0D, 0.25D, "Ignores damage checks if any enemy will be popped in x hits.");
      this.friendPop = this.sgDamage.d("Anti Friend Pop", 0.0D, 0.0D, 5.0D, 0.25D, "Ignores damage checks if any enemy will be popped in x hits.");
      this.extrapolation = this.sgExtrapolation.i("Extrapolation", 0, 0, 20, 1, ".");
      this.selfExtrapolation = this.sgExtrapolation.i("Self Extrapolation", 0, 0, 20, 1, ".");
      this.hitboxExtrapolation = this.sgExtrapolation.i("Hitbox Extrapolation", 0, 0, 20, 1, ".");
      this.placeSwing = this.sgRender.b("Place Swing", false, "Renders swing animation when placing a crystal.");
      this.placeHand = this.sgRender.e("Place Hand", SwingHand.RealHand, "Which hand should be swung.");
      this.renderShape = this.sgRender.e("Render Shape", RenderShape.Full, "Which parts of render should be rendered.");
      this.lineColor = this.sgRender.c("Line Color", new BlackOutColor(255, 0, 0, 255), "Line color of rendered boxes.");
      this.sideColor = this.sgRender.c("Side Color", new BlackOutColor(255, 0, 0, 50), "Side color of rendered boxes.");
      this.placePos = null;
      this.explodePos = null;
      this.target = null;
      this.selfHealth = 0.0D;
      this.enemyHealth = 0.0D;
      this.friendHealth = 0.0D;
      this.selfDamage = 0.0D;
      this.enemyDamage = 0.0D;
      this.friendDamage = 0.0D;
      this.extMap = new ExtrapolationMap();
      this.hitboxMap = new ExtrapolationMap();
      this.enemies = new ArrayList();
      this.result = null;
      this.lastPlace = 0L;
      this.lastInteract = 0L;
      this.lastExplode = 0L;
      this.progress = 0;
      this.targetProgress = 0;
      this.calcBest = null;
      this.calcValue = 0.0D;
      this.calcR = 0;
      this.targetCalcR = 0;
      this.calcMiddle = null;
      this.targetCalcBest = null;
      this.targetCalcValue = 0.0D;
      this.bestIsLoaded = false;
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
         this.calc(event.tickDelta);
         if (this.explodePos != null) {
            this.updateInteract();
            this.updateExplode();
         }

         if (this.placePos != null) {
            this.updatePlace();
            Render3DUtils.box(BoxUtils.get(this.placePos), (BlackOutColor)this.sideColor.get(), (BlackOutColor)this.lineColor.get(), (RenderShape)this.renderShape.get());
         }
      }
   }

   private void calc(float tickDelta) {
      if (this.calcMiddle != null) {
         int d = this.calcR * 2 + 1;
         int target = d * d * d;

         int i;
         int x;
         int y;
         int z;
         class_2338 pos;
         for(i = this.progress; (float)i < (float)target * tickDelta; ++i) {
            this.progress = i;
            x = i % d - this.calcR;
            y = i / d % d - this.calcR;
            z = i / d / d % d - this.calcR;
            pos = this.calcMiddle.method_10069(x, y, z);
            this.calcPos(pos);
         }

         d = this.targetCalcR * 2 + 1;
         target = d * d * d;

         for(i = this.targetProgress; (float)i < (float)target * tickDelta; ++i) {
            this.targetProgress = i;
            x = i % d - this.targetCalcR;
            y = i / d % d - this.targetCalcR;
            z = i / d / d % d - this.targetCalcR;
            pos = this.calcMiddle.method_10069(x, y, z);
            this.calcTarget(pos);
         }

      }
   }

   private void calcTarget(class_2338 pos) {
      if (BlackOut.mc.field_1687.method_8320(pos).method_26204() == class_2246.field_23152) {
         if (SettingUtils.getPlaceOnDirection(pos) != null) {
            if (SettingUtils.inInteractRange(pos)) {
               this.calcDamage(pos);
               if (this.explodeDamageCheck()) {
                  double value = this.getExplodeValue(pos);
                  boolean isLoaded = (Integer)BlackOut.mc.field_1687.method_8320(pos).method_11654(class_2741.field_23187) > 0;
                  if (!(value <= this.targetCalcValue) && (!this.bestIsLoaded || isLoaded)) {
                     this.targetCalcBest = pos;
                     this.targetCalcValue = value;
                     this.bestIsLoaded = isLoaded;
                  }
               }
            }
         }
      }
   }

   private void calcPos(class_2338 pos) {
      if (OLEPOSSUtils.replaceable(pos) || BlackOut.mc.field_1687.method_8320(pos).method_26204() == class_2246.field_23152) {
         PlaceData data = SettingUtils.getPlaceData(pos);
         if (this.inRangeToEnemies(pos)) {
            if (data.valid()) {
               if (SettingUtils.getPlaceOnDirection(pos) != null) {
                  if (SettingUtils.inInteractRange(pos)) {
                     if (SettingUtils.inPlaceRange(data.pos())) {
                        this.calcDamage(pos);
                        if (this.placeDamageCheck()) {
                           double value = this.getValue(pos);
                           if (!(value <= this.calcValue)) {
                              if (!EntityUtils.intersects(BoxUtils.get(pos), (entity) -> {
                                 return !(entity instanceof class_1542);
                              }, this.hitboxMap.getMap())) {
                                 this.calcBest = pos;
                                 this.calcValue = value;
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

   private boolean inRangeToEnemies(class_2338 pos) {
      class_243 vec = pos.method_46558();
      Iterator var3 = this.enemies.iterator();

      class_1657 player;
      do {
         if (!var3.hasNext()) {
            return false;
         }

         player = (class_1657)var3.next();
      } while(!(BoxUtils.middle(player.method_5829()).method_1022(vec) < 4.0D));

      return true;
   }

   private void updatePlace() {
      if (OLEPOSSUtils.replaceable(this.placePos)) {
         if (!((double)(System.currentTimeMillis() - this.lastPlace) < 1000.0D / (Double)this.placeSpeed.get())) {
            this.place();
         }
      }
   }

   private void updateInteract() {
      class_2680 state = BlackOut.mc.field_1687.method_8320(this.explodePos);
      if (state.method_26204() == class_2246.field_23152 && (Integer)state.method_11654(class_2741.field_23187) <= 0) {
         if (!((double)(System.currentTimeMillis() - this.lastInteract) < 1000.0D / (Double)this.interactSpeed.get())) {
            this.interact(this.explodePos);
         }
      }
   }

   private void updateExplode() {
      class_2680 state = BlackOut.mc.field_1687.method_8320(this.explodePos);
      if (state.method_26204() == class_2246.field_23152 && (Integer)state.method_11654(class_2741.field_23187) > 0) {
         if (!((double)(System.currentTimeMillis() - this.lastExplode) < 1000.0D / (Double)this.explodeSpeed.get())) {
            this.explode(this.explodePos);
         }
      }
   }

   private void explode(class_2338 pos) {
      class_2350 dir = SettingUtils.getPlaceOnDirection(pos);
      if (dir != null) {
         Predicate<class_1799> predicate = (stack) -> {
            return stack.method_7909() != class_1802.field_8801;
         };
         class_1268 hand = OLEPOSSUtils.getHand(predicate);
         this.result = ((SwitchMode)this.switchMode.get()).find(predicate);
         if (hand != null || this.result.wasFound()) {
            PlaceData data = SettingUtils.getPlaceData(pos);
            class_243 placeVec = data != null && data.valid() ? data.pos().method_46558().method_43206(data.dir(), 0.5D) : null;
            if (!SettingUtils.shouldRotate(RotationType.Interact) || this.rotateBlock(pos, dir, placeVec, RotationType.Interact, 0.1D, "explode")) {
               boolean switched = false;
               if (hand != null || (switched = ((SwitchMode)this.switchMode.get()).swap(this.result.slot()))) {
                  this.interactBlock(hand, pos.method_46558(), dir, pos);
                  BlackOut.mc.field_1687.method_8501(pos, class_2246.field_10124.method_9564());
                  this.lastExplode = System.currentTimeMillis();
                  this.explodePos = null;
                  if ((Boolean)this.placeSwing.get()) {
                     this.clientSwing((SwingHand)this.placeHand.get(), hand);
                  }

                  this.end("explode");
                  if (switched) {
                     ((SwitchMode)this.switchMode.get()).swapBack();
                  }

               }
            }
         }
      }
   }

   private void interact(class_2338 pos) {
      class_2350 dir = SettingUtils.getPlaceOnDirection(pos);
      if (dir != null) {
         class_1268 hand = OLEPOSSUtils.getHand(class_1802.field_8801);
         this.result = ((SwitchMode)this.switchMode.get()).find(class_1802.field_8801);
         if (hand != null || this.result.wasFound()) {
            PlaceData data = SettingUtils.getPlaceData(pos);
            class_243 placeVec = data != null && data.valid() ? data.pos().method_46558().method_43206(data.dir(), 0.5D) : null;
            if (!SettingUtils.shouldRotate(RotationType.Interact) || this.rotateBlock(pos, dir, placeVec, RotationType.Interact, 0.05D, "interact")) {
               boolean switched = false;
               if (hand != null || (switched = ((SwitchMode)this.switchMode.get()).swap(this.result.slot()))) {
                  this.interactBlock(hand, pos.method_46558(), dir, pos);
                  class_4969.method_26382(BlackOut.mc.field_1724, BlackOut.mc.field_1687, pos, BlackOut.mc.field_1687.method_8320(pos));
                  this.blockPlaceSound(pos, this.result.stack());
                  this.lastInteract = System.currentTimeMillis();
                  if ((Boolean)this.placeSwing.get()) {
                     this.clientSwing((SwingHand)this.placeHand.get(), hand);
                  }

                  this.end("interact");
                  if (switched) {
                     ((SwitchMode)this.switchMode.get()).swapBack();
                  }

               }
            }
         }
      }
   }

   private void place() {
      PlaceData data = SettingUtils.getPlaceData(this.placePos);
      if (data.valid()) {
         class_1268 hand = OLEPOSSUtils.getHand(class_1802.field_23141);
         this.result = ((SwitchMode)this.switchMode.get()).find(class_1802.field_23141);
         if (hand != null || this.result.wasFound()) {
            if (!SettingUtils.shouldRotate(RotationType.BlockPlace) || this.rotateBlock(data, data.pos().method_46558().method_43206(data.dir(), 0.5D), RotationType.BlockPlace, "placing")) {
               boolean switched = false;
               if (hand != null || (switched = ((SwitchMode)this.switchMode.get()).swap(this.result.slot()))) {
                  this.placeBlock(hand, data.pos().method_46558(), data.dir(), data.pos());
                  this.setBlock(hand, this.placePos);
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

   private void setBlock(class_1268 hand, class_2338 pos) {
      class_1792 item;
      if (hand == null) {
         item = BlackOut.mc.field_1724.method_31548().method_5438(this.result.slot()).method_7909();
      } else {
         item = OLEPOSSUtils.getItem(hand).method_7909();
      }

      if (item instanceof class_1747) {
         class_1747 block = (class_1747)item;
         Managers.PACKET.addToQueue((handler) -> {
            BlackOut.mc.field_1687.method_8501(pos, block.method_7711().method_9564());
            this.blockPlaceSound(this.placePos, this.result.stack());
         });
      }
   }

   private void updatePos() {
      this.findTargets();
      this.extMap.update((player) -> {
         return player == BlackOut.mc.field_1724 ? (Integer)this.selfExtrapolation.get() : (Integer)this.extrapolation.get();
      });
      this.hitboxMap.update((player) -> {
         return player == BlackOut.mc.field_1724 ? 0 : (Integer)this.hitboxExtrapolation.get();
      });
      this.placePos = this.calcBest;
      this.explodePos = this.targetCalcBest;
      this.startCalc();
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
                        this.enemies.clear();
                        map.forEach((playerx, d) -> {
                           this.enemies.add(playerx);
                        });
                        return;
                     }

                     player = (class_1657)var2.next();
                  } while(player == BlackOut.mc.field_1724);
               } while(player.method_6032() <= 0.0F);

               distance = (double)BlackOut.mc.field_1724.method_5739(player);
            } while(distance > (Double)this.enemyDistance.get());

            if (map.size() < 3) {
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

   private void startCalc() {
      this.selfHealth = this.getHealth(BlackOut.mc.field_1724);
      this.calcBest = null;
      this.calcValue = -42069.0D;
      this.progress = 0;
      this.calcR = (int)Math.ceil(SettingUtils.maxPlaceRange());
      this.calcMiddle = class_2338.method_49638(BlackOut.mc.field_1724.method_33571());
      this.targetCalcBest = null;
      this.targetCalcValue = -42069.0D;
      this.bestIsLoaded = false;
      this.targetCalcR = (int)Math.ceil(SettingUtils.maxInteractRange());
      this.targetProgress = 0;
   }

   private double getExplodeValue(class_2338 pos) {
      double value = 0.0D;
      if (SettingUtils.shouldRotate(RotationType.Interact)) {
         double yaw = Math.abs(RotationUtils.yawAngle((double)Managers.ROTATION.prevYaw, RotationUtils.getYaw(pos)));
         double per = Math.max(SettingUtils.yawStep(RotationType.Interact), 45.0D);
         int steps = (int)Math.ceil(yaw / per);
         value += 180.0D / per - (double)steps;
      }

      value += this.enemyDamage - this.selfDamage;
      return value;
   }

   private double getValue(class_2338 pos) {
      double value = 0.0D;
      if (SettingUtils.shouldRotate(RotationType.BlockPlace)) {
         double yaw = Math.abs(RotationUtils.yawAngle((double)Managers.ROTATION.prevYaw, RotationUtils.getYaw(pos)));
         double per = Math.max(SettingUtils.yawStep(RotationType.BlockPlace), 45.0D);
         int steps = (int)Math.ceil(yaw / per);
         value += 180.0D / per - (double)steps;
      }

      value += this.enemyDamage - this.selfDamage;
      return value;
   }

   private boolean explodeDamageCheck() {
      if (this.selfDamage * (Double)this.selfPop.get() > this.selfHealth) {
         return false;
      } else if (this.friendDamage * (Double)this.friendPop.get() > this.friendHealth) {
         return false;
      } else if (this.enemyDamage * (Double)this.forcePop.get() > this.enemyHealth) {
         return true;
      } else if ((Boolean)this.checkEnemyExplode.get() && this.enemyDamage < (Double)this.minExplode.get()) {
         return false;
      } else {
         if ((Boolean)this.checkSelfExplode.get()) {
            if (this.selfDamage > (Double)this.maxSelfExplode.get()) {
               return false;
            }

            if ((Boolean)this.checkEnemyExplode.get() && this.enemyDamage / this.selfDamage < (Double)this.minSelfExplodeRatio.get()) {
               return false;
            }
         }

         if ((Boolean)this.checkFriendExplode.get()) {
            if (this.friendDamage > (Double)this.maxFriendExplode.get()) {
               return false;
            } else {
               return !(this.enemyDamage / this.friendDamage < (Double)this.minFriendExplodeRatio.get());
            }
         } else {
            return true;
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
      } else if (this.enemyDamage < (Double)this.minPlace.get()) {
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

   private void calcDamage(class_2338 pos) {
      class_243 vec = pos.method_46558();
      this.target = null;
      this.selfDamage = DamageUtils.anchorDamage(BlackOut.mc.field_1724, this.extMap.get(BlackOut.mc.field_1724), vec, pos);
      this.enemyDamage = 0.0D;
      this.friendDamage = 0.0D;
      this.enemyHealth = 20.0D;
      this.friendHealth = 20.0D;
      this.enemies.forEach((player) -> {
         class_238 box = this.extMap.get(player);
         if (!(player.method_6032() <= 0.0F) && player != BlackOut.mc.field_1724) {
            double dmg = DamageUtils.anchorDamage(player, box, vec, pos);
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

   private double getHealth(class_1309 entity) {
      return (double)(entity.method_6032() + entity.method_6067());
   }
}
