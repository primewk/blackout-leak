package bodevelopment.client.blackout.module.modules.combat.defensive;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.RenderShape;
import bodevelopment.client.blackout.enums.RotationType;
import bodevelopment.client.blackout.enums.SwingHand;
import bodevelopment.client.blackout.enums.SwitchMode;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.BlockStateEvent;
import bodevelopment.client.blackout.event.events.PacketEvent;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.interfaces.functional.DoublePredicate;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.randomstuff.FindResult;
import bodevelopment.client.blackout.randomstuff.Pair;
import bodevelopment.client.blackout.randomstuff.PlaceData;
import bodevelopment.client.blackout.randomstuff.timers.TimerList;
import bodevelopment.client.blackout.util.BoxUtils;
import bodevelopment.client.blackout.util.DamageUtils;
import bodevelopment.client.blackout.util.EntityUtils;
import bodevelopment.client.blackout.util.HoleUtils;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import bodevelopment.client.blackout.util.SettingUtils;
import bodevelopment.client.blackout.util.render.Render3DUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1303;
import net.minecraft.class_1511;
import net.minecraft.class_1542;
import net.minecraft.class_1683;
import net.minecraft.class_1747;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2189;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2620;
import net.minecraft.class_3417;
import net.minecraft.class_3419;

public class Blocker extends Module {
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final SettingGroup sgProtection = this.addGroup("Protection");
   private final SettingGroup sgSpeed = this.addGroup("Speed");
   private final SettingGroup sgBlocks = this.addGroup("Blocks");
   private final SettingGroup sgAttack = this.addGroup("Attack");
   private final SettingGroup sgDamage = this.addGroup("Damage");
   private final SettingGroup sgRender = this.addGroup("Render");
   private final Setting<Boolean> pauseEat;
   private final Setting<SwitchMode> switchMode;
   private final Setting<Double> mineTime;
   private final Setting<Double> maxMineTime;
   private final Setting<Boolean> packet;
   private final Setting<Boolean> onlyHole;
   private final Setting<Boolean> surroundFloor;
   private final Setting<Boolean> surroundFloorBottom;
   private final Setting<Boolean> surroundSides;
   private final Setting<Boolean> surroundTop;
   private final Setting<Boolean> surroundBottom;
   private final Setting<Boolean> trapCev;
   private final Setting<Boolean> cev;
   private final Setting<Blocker.PlaceDelayMode> placeDelayMode;
   private final Setting<Integer> placeDelayT;
   private final Setting<Double> placeDelayS;
   private final Setting<Integer> places;
   private final Setting<Double> cooldown;
   private final Setting<List<class_2248>> blocks;
   private final Setting<Boolean> attack;
   private final Setting<Double> attackSpeed;
   private final Setting<Boolean> always;
   private final Setting<Double> minDmg;
   private final Setting<Boolean> placeSwing;
   private final Setting<SwingHand> placeHand;
   private final Setting<Boolean> attackSwing;
   private final Setting<SwingHand> attackHand;
   private final Setting<RenderShape> renderShape;
   private final Setting<BlackOutColor> lineColor;
   private final Setting<BlackOutColor> sideColor;
   private final TimerList<Pair<class_2338, Integer>> mining;
   private final List<Blocker.ProtectBlock> toProtect;
   private final List<class_2338> placePositions;
   private final List<Blocker.Render> render;
   private final TimerList<class_2338> placed;
   private int blocksLeft;
   private int placesLeft;
   private FindResult result;
   private boolean switched;
   private class_1268 hand;
   private int tickTimer;
   private double timer;
   private long lastTime;
   private long lastAttack;

   public Blocker() {
      super("Blocker", "Covers your surround blocks if any enemy tries to mine them.", SubCategory.DEFENSIVE, true);
      this.pauseEat = this.sgGeneral.b("Pause Eat", false, "Pauses when eating.");
      this.switchMode = this.sgGeneral.e("Switch Mode", SwitchMode.Silent, "Method of switching. Silent is the most reliable.");
      this.mineTime = this.sgGeneral.d("Mine Time", 1.0D, 0.0D, 10.0D, 0.1D, "How long do we let enemies mine our surround for before protecting it.");
      this.maxMineTime = this.sgGeneral.d("Max Mine Time", 5.0D, 0.0D, 10.0D, 0.1D, "Ignores mining after x seconds.");
      this.packet = this.sgGeneral.b("Packet", false, ".");
      this.onlyHole = this.sgGeneral.b("Only Hole", false, "Only protects when you are in a hole..");
      this.surroundFloor = this.sgProtection.b("Surround Floor", true, "Places blocks around surround floor blocks");
      this.surroundFloorBottom = this.sgProtection.b("Surround Floor Bottom", true, "Places blocks under surround floor blocks");
      this.surroundSides = this.sgProtection.b("Surround Sides", true, "Places blocks next to surround blocks");
      this.surroundTop = this.sgProtection.b("Surround Side Top", true, "Places blocks on top of surround blocks");
      this.surroundBottom = this.sgProtection.b("Surround Side Bottom", true, "Places blocks under surround blocks");
      this.trapCev = this.sgProtection.b("Trap Cev", true, "Places on top of trap side block");
      this.cev = this.sgProtection.b("Cev", true, "Places on top of trap top blocks");
      this.placeDelayMode = this.sgSpeed.e("Place Delay Mode", Blocker.PlaceDelayMode.Ticks, ".");
      this.placeDelayT = this.sgSpeed.i("Place Delay Ticks", 1, 0, 20, 1, "Tick delay between places.", () -> {
         return this.placeDelayMode.get() == Blocker.PlaceDelayMode.Ticks;
      });
      this.placeDelayS = this.sgSpeed.d("Place Delay", 0.1D, 0.0D, 1.0D, 1.0D, "Delay between places.", () -> {
         return this.placeDelayMode.get() == Blocker.PlaceDelayMode.Seconds;
      });
      this.places = this.sgSpeed.i("Places", 1, 0, 20, 1, "How many blocks to place each time.");
      this.cooldown = this.sgSpeed.d("Cooldown", 0.5D, 0.0D, 1.0D, 1.0D, "Waits x seconds before trying to place at the same position.");
      this.blocks = this.sgBlocks.bl("Blocks", "Blocks to use.", class_2246.field_10540);
      this.attack = this.sgAttack.b("Attack", true, "Attacks crystals blocking.");
      this.attackSpeed = this.sgAttack.d("Attack Speed", 4.0D, 0.0D, 20.0D, 0.1D, "How many times to attack every second.");
      this.always = this.sgDamage.b("Always", true, "Doesn't check for min damage when placing.");
      this.minDmg = this.sgDamage.d("Min Damage", 6.0D, 0.0D, 20.0D, 0.1D, "Doesn't place if you would take less damage than this.", () -> {
         return !(Boolean)this.always.get();
      });
      this.placeSwing = this.sgRender.b("Place Swing", true, "Renders swing animation when placing a block.");
      SettingGroup var10001 = this.sgRender;
      SwingHand var10003 = SwingHand.RealHand;
      Setting var10005 = this.placeSwing;
      Objects.requireNonNull(var10005);
      this.placeHand = var10001.e("Place Hand", var10003, "Which hand should be swung.", var10005::get);
      this.attackSwing = this.sgRender.b("Attack Swing", true, "Renders swing animation when attacking a crystal.");
      var10001 = this.sgRender;
      var10003 = SwingHand.RealHand;
      var10005 = this.attackSwing;
      Objects.requireNonNull(var10005);
      this.attackHand = var10001.e("Attack Hand", var10003, "Which hand should be swung.", var10005::get);
      this.renderShape = this.sgRender.e("Render Shape", RenderShape.Full, "Which parts should be rendered.");
      this.lineColor = this.sgRender.c("Line Color", new BlackOutColor(255, 0, 0, 255), ".");
      this.sideColor = this.sgRender.c("Side Color", new BlackOutColor(255, 0, 0, 50), ".");
      this.mining = new TimerList(true);
      this.toProtect = new ArrayList();
      this.placePositions = new ArrayList();
      this.render = Collections.synchronizedList(new ArrayList());
      this.placed = new TimerList(false);
      this.blocksLeft = 0;
      this.placesLeft = 0;
      this.result = null;
      this.switched = false;
      this.hand = null;
      this.tickTimer = 0;
      this.timer = 0.0D;
      this.lastTime = 0L;
      this.lastAttack = 0L;
   }

   @Event
   public void onBlock(BlockStateEvent event) {
      if (event.previousState.method_26204() != event.state.method_26204() && !OLEPOSSUtils.replaceable(event.pos) && this.placePositions.contains(event.pos)) {
         this.render.add(new Blocker.Render(event.pos, System.currentTimeMillis()));
      }

   }

   @Event
   public void onTickPre(TickEvent.Pre event) {
      ++this.tickTimer;
   }

   @Event
   public void onRender(RenderEvent.World.Post event) {
      this.placed.update();
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         this.timer += (double)(System.currentTimeMillis() - this.lastTime) / 1000.0D;
         this.lastTime = System.currentTimeMillis();
         this.updateBlocks();
         this.updatePlacing();
         synchronized(this.render) {
            this.render.removeIf((r) -> {
               if (System.currentTimeMillis() - r.time > 1000L) {
                  return true;
               } else {
                  double progress = 1.0D - (double)Math.min(System.currentTimeMillis() - r.time, 500L) / 500.0D;
                  Render3DUtils.box(BoxUtils.get(r.pos), ((BlackOutColor)this.sideColor.get()).alphaMulti(progress), ((BlackOutColor)this.lineColor.get()).alphaMulti(progress), (RenderShape)this.renderShape.get());
                  return false;
               }
            });
         }
      }
   }

   @Event
   public void onReceive(PacketEvent.Receive.Pre event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_2620) {
         class_2620 p = (class_2620)var3;
         this.mining.remove((timer) -> {
            return (Integer)((Pair)timer.value).method_15441() == p.method_11280();
         });
         this.mining.add(new Pair(p.method_11277(), p.method_11280()), (Double)this.maxMineTime.get());
      }
   }

   private void updatePlacing() {
      if (!(Boolean)this.pauseEat.get() || !BlackOut.mc.field_1724.method_6115()) {
         this.updateResult();
         this.updatePlaces();
         this.blocksLeft = Math.min(this.placesLeft, this.result.amount());
         this.hand = OLEPOSSUtils.getHand(this::valid);
         this.switched = false;
         this.placePositions.clear();
         this.toProtect.stream().filter(this::shouldProtect).forEach(this::addPlacePositions);
         this.updateAttack();
         this.placePositions.stream().filter((pos) -> {
            return !EntityUtils.intersects(BoxUtils.get(pos), (entity) -> {
               return entity instanceof class_1511 && System.currentTimeMillis() - this.lastAttack > 100L;
            });
         }).forEach(this::place);
         if (this.switched) {
            ((SwitchMode)this.switchMode.get()).swapBack();
         }

      }
   }

   private void addPlacePositions(Blocker.ProtectBlock p) {
      switch(p.type) {
      case 0:
      case 1:
         class_2350[] var8 = class_2350.values();
         int var9 = var8.length;

         for(int var4 = 0; var4 < var9; ++var4) {
            class_2350 dir = var8[var4];
            if (p.type == 1) {
               if (!(Boolean)this.surroundSides.get() && dir.method_10166().method_10179() || !(Boolean)this.surroundTop.get() && dir == class_2350.field_11036 || !(Boolean)this.surroundBottom.get() && dir == class_2350.field_11033) {
                  continue;
               }
            } else if (dir == class_2350.field_11036 || !(Boolean)this.surroundFloor.get() && dir.method_10166().method_10179() || !(Boolean)this.surroundFloorBottom.get() && dir == class_2350.field_11033) {
               continue;
            }

            class_2338 pos = p.pos.method_10093(dir);
            if (OLEPOSSUtils.replaceable(pos)) {
               PlaceData data = SettingUtils.getPlaceData(pos);
               if (data.valid() && SettingUtils.inPlaceRange(data.pos()) && !EntityUtils.intersects(BoxUtils.get(pos), this::validForIntersects)) {
                  this.placePositions.add(pos);
               }
            }
         }

         return;
      case 2:
      case 3:
         class_2338 pos = p.pos.method_10084();
         if (!OLEPOSSUtils.replaceable(pos)) {
            return;
         }

         PlaceData data = SettingUtils.getPlaceData(pos);
         if (!data.valid()) {
            return;
         }

         if (!SettingUtils.inPlaceRange(data.pos())) {
            return;
         }

         if (EntityUtils.intersects(BoxUtils.get(pos), this::validForIntersects)) {
            return;
         }

         this.placePositions.add(pos);
      }

   }

   private void updateAttack() {
      if ((Boolean)this.attack.get()) {
         if (!((double)(System.currentTimeMillis() - this.lastAttack) < 1000.0D / (Double)this.attackSpeed.get())) {
            class_1297 blocking = this.getBlocking();
            if (blocking != null) {
               if (!SettingUtils.shouldRotate(RotationType.Attacking) || this.attackRotate(blocking.method_5829(), 0.1D, "attacking")) {
                  this.attackEntity(blocking);
                  if (SettingUtils.shouldRotate(RotationType.Attacking)) {
                     this.end("attacking");
                  }

                  if ((Boolean)this.attackSwing.get()) {
                     this.clientSwing((SwingHand)this.attackHand.get(), class_1268.field_5808);
                  }

                  this.lastAttack = System.currentTimeMillis();
               }
            }
         }
      }
   }

   private class_1297 getBlocking() {
      class_1297 crystal = null;
      double lowest = 1000.0D;
      Iterator var4 = BlackOut.mc.field_1687.method_18112().iterator();

      while(true) {
         class_1297 entity;
         do {
            do {
               do {
                  if (!var4.hasNext()) {
                     return crystal;
                  }

                  entity = (class_1297)var4.next();
               } while(!(entity instanceof class_1511));
            } while(BlackOut.mc.field_1724.method_5739(entity) > 5.0F);
         } while(!SettingUtils.inAttackRange(entity.method_5829()));

         Iterator var6 = this.placePositions.iterator();

         while(var6.hasNext()) {
            class_2338 pos = (class_2338)var6.next();
            if (BoxUtils.get(pos).method_994(entity.method_5829())) {
               double dmg = DamageUtils.crystalDamage(BlackOut.mc.field_1724, BlackOut.mc.field_1724.method_5829(), entity.method_19538());
               if (dmg < lowest) {
                  crystal = entity;
                  lowest = dmg;
               }
            }
         }
      }
   }

   private void updateResult() {
      this.result = ((SwitchMode)this.switchMode.get()).find(this::valid);
   }

   private boolean valid(class_1799 stack) {
      class_1792 var3 = stack.method_7909();
      boolean var10000;
      if (var3 instanceof class_1747) {
         class_1747 block = (class_1747)var3;
         if (((List)this.blocks.get()).contains(block.method_7711())) {
            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
   }

   private void updatePlaces() {
      switch((Blocker.PlaceDelayMode)this.placeDelayMode.get()) {
      case Ticks:
         if (this.placesLeft >= (Integer)this.places.get() || this.tickTimer >= (Integer)this.placeDelayT.get()) {
            this.placesLeft = (Integer)this.places.get();
            this.tickTimer = 0;
         }
         break;
      case Seconds:
         if (this.placesLeft >= (Integer)this.places.get() || this.timer >= (Double)this.placeDelayS.get()) {
            this.placesLeft = (Integer)this.places.get();
            this.timer = 0.0D;
         }
      }

   }

   private void place(class_2338 pos) {
      if (this.blocksLeft > 0) {
         PlaceData data = SettingUtils.getPlaceData(pos, (p, d) -> {
            return this.placed.contains((Object)p);
         }, (DoublePredicate)null);
         if (data != null && data.valid()) {
            if (!SettingUtils.shouldRotate(RotationType.BlockPlace) || this.rotateBlock(data, RotationType.BlockPlace, "placing")) {
               if (!this.switched && this.hand == null) {
                  this.switched = ((SwitchMode)this.switchMode.get()).swap(this.result.slot());
               }

               if (this.switched || this.hand != null) {
                  this.placeBlock(this.hand, data.pos().method_46558(), data.dir(), data.pos());
                  if ((Boolean)this.placeSwing.get()) {
                     this.clientSwing((SwingHand)this.placeHand.get(), this.hand);
                  }

                  if (!(Boolean)this.packet.get()) {
                     this.setBlock(pos);
                  }

                  this.placed.add(pos, (Double)this.cooldown.get());
                  --this.blocksLeft;
                  --this.placesLeft;
                  if (SettingUtils.shouldRotate(RotationType.BlockPlace)) {
                     this.end("placing");
                  }

               }
            }
         }
      }
   }

   private void setBlock(class_2338 pos) {
      class_1792 item = BlackOut.mc.field_1724.method_31548().method_5438(this.result.slot()).method_7909();
      if (item instanceof class_1747) {
         class_1747 block = (class_1747)item;
         Managers.PACKET.addToQueue((handler) -> {
            BlackOut.mc.field_1687.method_8501(pos, block.method_7711().method_9564());
            BlackOut.mc.field_1687.method_8486((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), class_3417.field_14574, class_3419.field_15245, 1.0F, 1.0F, false);
         });
      }
   }

   private boolean shouldProtect(Blocker.ProtectBlock p) {
      class_2338 pos = p.pos;
      switch(p.type) {
      case 1:
         if (OLEPOSSUtils.solid2(pos) && BlackOut.mc.field_1687.method_8320(pos).method_26204() != class_2246.field_9987) {
            break;
         }

         return false;
      case 2:
      case 3:
         if (BlackOut.mc.field_1687.method_8320(p.pos).method_26204() != class_2246.field_10540) {
            return false;
         }

         if (!(BlackOut.mc.field_1687.method_8320(p.pos.method_10084()).method_26204() instanceof class_2189)) {
            return false;
         }

         if (SettingUtils.oldCrystals() && !(BlackOut.mc.field_1687.method_8320(p.pos.method_10086(2)).method_26204() instanceof class_2189)) {
            return false;
         }
      }

      return !this.mining.contains((timer) -> {
         return ((class_2338)((Pair)timer.value).method_15442()).equals(p.pos) && (double)(System.currentTimeMillis() - timer.startTime) >= (Double)this.mineTime.get() * 1000.0D;
      }) ? false : this.damageCheck(pos, p.type);
   }

   private void updateBlocks() {
      this.toProtect.clear();
      if (!(Boolean)this.onlyHole.get() || HoleUtils.inHole(BlackOut.mc.field_1724.method_24515())) {
         class_2338 e = class_2338.method_49637(BlackOut.mc.field_1724.method_23317(), BlackOut.mc.field_1724.method_5829().field_1325, BlackOut.mc.field_1724.method_23321());
         class_2338 pos = new class_2338(BlackOut.mc.field_1724.method_31477(), (int)Math.round(BlackOut.mc.field_1724.method_23318()), BlackOut.mc.field_1724.method_31479());
         int[] size = new int[4];
         double xOffset = BlackOut.mc.field_1724.method_23317() - (double)BlackOut.mc.field_1724.method_31477();
         double zOffset = BlackOut.mc.field_1724.method_23321() - (double)BlackOut.mc.field_1724.method_31479();
         if (xOffset < 0.3D) {
            size[0] = -1;
         }

         if (xOffset > 0.7D) {
            size[1] = 1;
         }

         if (zOffset < 0.3D) {
            size[2] = -1;
         }

         if (zOffset > 0.7D) {
            size[3] = 1;
         }

         this.updateSurround(pos, size);
         if ((Boolean)this.trapCev.get()) {
            this.updateEyes(e, size);
         }

         if ((Boolean)this.cev.get()) {
            this.updateTop(e.method_10084());
         }

      }
   }

   private void updateTop(class_2338 pos) {
      this.toProtect.add(new Blocker.ProtectBlock(pos, 3));
   }

   private void updateEyes(class_2338 pos, int[] size) {
      for(int x = size[0] - 1; x <= size[1] + 1; ++x) {
         for(int z = size[2] - 1; z <= size[3] + 1; ++z) {
            if (x != size[0] - 1 && x != size[1] + 1 || z != size[2] - 1 && z != size[3] + 1) {
               this.toProtect.add(new Blocker.ProtectBlock(pos.method_10069(x, 0, z), 2));
            }
         }
      }

   }

   private void updateSurround(class_2338 pos, int[] size) {
      for(int y = -1; y <= 0; ++y) {
         for(int x = size[0] - 1; x <= size[1] + 1; ++x) {
            for(int z = size[2] - 1; z <= size[3] + 1; ++z) {
               boolean bx = x == size[0] - 1 || x == size[1] + 1;
               boolean by = y == -1;
               boolean bz = z == size[2] - 1 || z == size[3] + 1;
               if (by) {
                  if (!bx && !bz) {
                     this.toProtect.add(new Blocker.ProtectBlock(pos.method_10069(x, y, z), 0));
                  }
               } else if (!bx || !bz) {
                  this.toProtect.add(new Blocker.ProtectBlock(pos.method_10069(x, y, z), 1));
               }
            }
         }
      }

   }

   private boolean validForIntersects(class_1297 entity) {
      return !(entity instanceof class_1542) && !(entity instanceof class_1511);
   }

   private boolean damageCheck(class_2338 blockPos, int type) {
      if ((Boolean)this.always.get()) {
         return true;
      } else {
         switch(type) {
         case 1:
            for(int x = -2; x <= 2; ++x) {
               for(int y = -2; y <= 2; ++y) {
                  for(int z = -2; z <= 2; ++z) {
                     class_2338 pos = blockPos.method_10069(x, y, z);
                     if (BlackOut.mc.field_1687.method_8320(pos).method_26204() instanceof class_2189 && (!SettingUtils.oldCrystals() || BlackOut.mc.field_1687.method_8320(pos.method_10084()).method_26204() instanceof class_2189) && !EntityUtils.intersects(BoxUtils.crystalSpawnBox(pos), (entity) -> {
                        return !(entity instanceof class_1303) && !(entity instanceof class_1683);
                     }) && DamageUtils.crystalDamage(BlackOut.mc.field_1724, BlackOut.mc.field_1724.method_5829(), this.feet(pos), blockPos) >= (Double)this.minDmg.get()) {
                        return true;
                     }
                  }
               }
            }

            return false;
         case 2:
         case 3:
            if (DamageUtils.crystalDamage(BlackOut.mc.field_1724, BlackOut.mc.field_1724.method_5829(), this.feet(blockPos.method_10084()), blockPos) >= (Double)this.minDmg.get()) {
               return true;
            }
         }

         return false;
      }
   }

   private class_243 feet(class_2338 pos) {
      return new class_243((double)pos.method_10263() + 0.5D, (double)pos.method_10264(), (double)pos.method_10260() + 0.5D);
   }

   public static enum PlaceDelayMode {
      Ticks,
      Seconds;

      // $FF: synthetic method
      private static Blocker.PlaceDelayMode[] $values() {
         return new Blocker.PlaceDelayMode[]{Ticks, Seconds};
      }
   }

   public static record Render(class_2338 pos, long time) {
      public Render(class_2338 pos, long time) {
         this.pos = pos;
         this.time = time;
      }

      public class_2338 pos() {
         return this.pos;
      }

      public long time() {
         return this.time;
      }
   }

   private static record ProtectBlock(class_2338 pos, int type) {
      private ProtectBlock(class_2338 pos, int type) {
         this.pos = pos;
         this.type = type;
      }

      public class_2338 pos() {
         return this.pos;
      }

      public int type() {
         return this.type;
      }
   }
}
