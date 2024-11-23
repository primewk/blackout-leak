package bodevelopment.client.blackout.module;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.RotationType;
import bodevelopment.client.blackout.enums.SwingHand;
import bodevelopment.client.blackout.enums.SwingState;
import bodevelopment.client.blackout.enums.SwingType;
import bodevelopment.client.blackout.enums.SwitchMode;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.BlockStateEvent;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.interfaces.functional.DoublePredicate;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.modules.combat.defensive.Surround;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.module.setting.multisettings.BoxMultiSetting;
import bodevelopment.client.blackout.randomstuff.FindResult;
import bodevelopment.client.blackout.randomstuff.PlaceData;
import bodevelopment.client.blackout.randomstuff.Rotation;
import bodevelopment.client.blackout.randomstuff.timers.RenderList;
import bodevelopment.client.blackout.randomstuff.timers.TimerList;
import bodevelopment.client.blackout.util.BoxUtils;
import bodevelopment.client.blackout.util.DamageUtils;
import bodevelopment.client.blackout.util.EntityUtils;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import bodevelopment.client.blackout.util.RotationUtils;
import bodevelopment.client.blackout.util.SettingUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1511;
import net.minecraft.class_1542;
import net.minecraft.class_1657;
import net.minecraft.class_1747;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_2824;
import net.minecraft.class_2828.class_2831;

public class ObsidianModule extends Module {
   public final SettingGroup sgGeneral = this.addGroup("General");
   public final SettingGroup sgBlocks = this.addGroup("Blocks");
   public final SettingGroup sgSpeed = this.addGroup("Speed");
   public final SettingGroup sgAttack = this.addGroup("Attack");
   public final SettingGroup sgRender = this.addGroup("Render");
   private final Setting<Boolean> pauseEat;
   private final Setting<Boolean> packet;
   private final Setting<Boolean> allowSneak;
   private final Setting<SwitchMode> switchMode;
   private final Setting<Boolean> onlyOnGround;
   private final Setting<ObsidianModule.RotationMode> rotationMode;
   public final Setting<List<class_2248>> blocks;
   public final Setting<List<class_2248>> supportBlocks;
   private final Setting<Surround.PlaceDelayMode> placeDelayMode;
   private final Setting<Integer> placeDelayT;
   private final Setting<Double> placeDelayS;
   private final Setting<Integer> places;
   protected final Setting<Double> cooldown;
   protected final Setting<Boolean> attack;
   private final Setting<Double> attackSpeed;
   private final Setting<Boolean> alwaysAttack;
   private final Setting<Boolean> placeSwing;
   private final Setting<SwingHand> placeHand;
   private final Setting<Boolean> attackSwing;
   private final Setting<SwingHand> attackHand;
   private final BoxMultiSetting normalRendering;
   private final BoxMultiSetting supportRendering;
   private int tickTimer;
   private double timer;
   protected final List<class_2338> insideBlocks;
   public final List<class_2338> blockPlacements;
   private final List<class_2338> supportPositions;
   protected final List<class_2338> valids;
   private final TimerList<class_2338> placed;
   private final RenderList<class_2338> render;
   private final RenderList<class_2338> supportRender;
   private boolean support;
   private class_1268 hand;
   private int blocksLeft;
   private int placesLeft;
   private FindResult result;
   private boolean switched;
   private long lastAttack;
   public boolean placing;
   private boolean firstCalc;

   public ObsidianModule(String name, String description, SubCategory category) {
      super(name, description, category, true);
      this.pauseEat = this.sgGeneral.b("Pause Eat", false, "Pauses when eating.");
      this.packet = this.sgGeneral.b("Packet", false, ".");
      this.allowSneak = this.sgGeneral.b("Allow Sneak", false, ".");
      this.switchMode = this.sgGeneral.e("Switch Mode", SwitchMode.Silent, "Method of switching. Silent is the most reliable but delays crystals on some servers.");
      this.onlyOnGround = this.sgGeneral.b("Only On Ground", false, ".");
      this.rotationMode = this.sgGeneral.e("Rotation Mode", ObsidianModule.RotationMode.Normal, ".");
      this.blocks = this.sgBlocks.bl("Blocks", "Blocks to use.", class_2246.field_10540);
      this.supportBlocks = this.sgBlocks.bl("Support Blocks", "Blocks to use for support.", class_2246.field_10540);
      this.placeDelayMode = this.sgSpeed.e("Place Delay Mode", Surround.PlaceDelayMode.Ticks, ".");
      this.placeDelayT = this.sgSpeed.i("Place Tick Delay", 1, 0, 20, 1, "Tick delay between places.", () -> {
         return this.placeDelayMode.get() == Surround.PlaceDelayMode.Ticks;
      });
      this.placeDelayS = this.sgSpeed.d("Place Delay", 0.1D, 0.0D, 1.0D, 0.01D, "Delay between places.", () -> {
         return this.placeDelayMode.get() == Surround.PlaceDelayMode.Seconds;
      });
      this.places = this.sgSpeed.i("Places", 1, 1, 20, 1, "How many blocks to place each time.");
      this.cooldown = this.sgSpeed.d("Cooldown", 0.3D, 0.0D, 1.0D, 0.01D, "Waits x seconds before trying to place at the same position if there is more than 1 missing block.");
      this.attack = this.sgAttack.b("Attack", true, "Attacks crystals blocking surround.");
      SettingGroup var10001 = this.sgAttack;
      Setting var10008 = this.attack;
      Objects.requireNonNull(var10008);
      this.attackSpeed = var10001.d("Attack Speed", 4.0D, 0.0D, 20.0D, 0.05D, "How many times to attack every second.", var10008::get);
      var10001 = this.sgAttack;
      Setting var10005 = this.attack;
      Objects.requireNonNull(var10005);
      this.alwaysAttack = var10001.b("Always Attack", false, "Attacks crystals even when surround block isn't broken.", var10005::get);
      this.placeSwing = this.sgRender.b("Place Swing", true, "Renders swing animation when placing a block.");
      var10001 = this.sgRender;
      SwingHand var10003 = SwingHand.RealHand;
      var10005 = this.placeSwing;
      Objects.requireNonNull(var10005);
      this.placeHand = var10001.e("Place Swing Hand", var10003, "Which hand should be swung.", var10005::get);
      this.attackSwing = this.sgRender.b("Attack Swing", true, "Renders swing animation when attacking a block.");
      var10001 = this.sgRender;
      var10003 = SwingHand.RealHand;
      var10005 = this.attackSwing;
      Objects.requireNonNull(var10005);
      this.attackHand = var10001.e("Attack Swing Hand", var10003, "Which hand should be swung.", var10005::get);
      this.normalRendering = BoxMultiSetting.of(this.sgRender);
      this.supportRendering = BoxMultiSetting.of(this.sgRender, "Support");
      this.tickTimer = 0;
      this.timer = 0.0D;
      this.insideBlocks = new ArrayList();
      this.blockPlacements = new ArrayList();
      this.supportPositions = new ArrayList();
      this.valids = new ArrayList();
      this.placed = new TimerList(false);
      this.render = RenderList.getList(true);
      this.supportRender = RenderList.getList(true);
      this.support = false;
      this.hand = null;
      this.blocksLeft = 0;
      this.placesLeft = 0;
      this.result = null;
      this.switched = false;
      this.lastAttack = 0L;
      this.placing = false;
      this.firstCalc = true;
   }

   public void onEnable() {
      this.tickTimer = (Integer)this.placeDelayT.get();
      this.timer = (Double)this.placeDelayS.get();
      this.placesLeft = (Integer)this.places.get();
      this.firstCalc = true;
   }

   public void onDisable() {
      this.blockPlacements.stream().filter(OLEPOSSUtils::replaceable).forEach((pos) -> {
         this.render.add(pos, 0.5D);
      });
      this.supportPositions.forEach((pos) -> {
         this.supportRender.add(pos, 0.5D);
      });
   }

   public boolean shouldSkipListeners() {
      return false;
   }

   @Event
   public void onBlock(BlockStateEvent event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null && this.enabled) {
         if (event.previousState.method_26204() != event.state.method_26204() && !OLEPOSSUtils.replaceable(event.pos)) {
            if (this.blockPlacements.contains(event.pos)) {
               this.render.add(event.pos, 0.5D);
            }

            if (this.supportPositions.contains(event.pos)) {
               this.supportRender.add(event.pos, 0.5D);
            }
         }

      }
   }

   @Event
   public void onTick(TickEvent.Pre event) {
      ++this.tickTimer;
   }

   @Event
   public void onRender(RenderEvent.World.Post event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         if (!this.enabled) {
            this.render.update((pos, time, delta) -> {
               this.normalRendering.render(BoxUtils.get(pos), (float)(1.0D - delta), 1.0F);
            });
         } else {
            this.placed.update();
            this.timer += event.frameTime;
            if (!this.preCalc()) {
               this.updateBlocks();
               this.updateSupport();
               this.blockPlacements.stream().filter(OLEPOSSUtils::replaceable).forEach((block) -> {
                  this.normalRendering.render(BoxUtils.get(block));
                  if (this.firstCalc) {
                     this.render.remove((Object)block);
                  }

               });
               this.supportPositions.forEach((block) -> {
                  this.supportRendering.render(BoxUtils.get(block));
                  if (this.firstCalc) {
                     this.supportRender.remove((Object)block);
                  }

               });
               this.render.update((pos, time, delta) -> {
                  this.normalRendering.render(BoxUtils.get(pos), (float)(1.0D - delta), 1.0F);
               });
               this.supportRender.update((pos, time, delta) -> {
                  this.supportRendering.render(BoxUtils.get(pos), (float)(1.0D - delta), 1.0F);
               });
               this.firstCalc = false;
               if (!(Boolean)this.pauseEat.get() || !BlackOut.mc.field_1724.method_6115()) {
                  if (!(Boolean)this.onlyOnGround.get() || BlackOut.mc.field_1724.method_24828()) {
                     this.placeBlocks();
                  }
               }
            }
         }
      }
   }

   protected double getCooldown() {
      return 0.0D;
   }

   protected boolean preCalc() {
      return false;
   }

   private void updateAttack() {
      if ((Boolean)this.attack.get()) {
         if (!((double)(System.currentTimeMillis() - this.lastAttack) < 1000.0D / (Double)this.attackSpeed.get())) {
            class_1297 blocking = this.getBlocking();
            if (blocking != null) {
               if (!SettingUtils.shouldRotate(RotationType.Attacking) || this.attackRotate(blocking.method_5829(), -0.1D, "attacking")) {
                  SettingUtils.swing(SwingState.Pre, SwingType.Attacking, class_1268.field_5808);
                  this.sendPacket(class_2824.method_34206(blocking, BlackOut.mc.field_1724.method_5715()));
                  SettingUtils.swing(SwingState.Post, SwingType.Attacking, class_1268.field_5808);
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

      while(var4.hasNext()) {
         class_1297 entity = (class_1297)var4.next();
         if (entity instanceof class_1511 && !(BlackOut.mc.field_1724.method_5739(entity) > 5.0F) && SettingUtils.inAttackRange(entity.method_5829()) && this.validForBlocking(entity)) {
            double dmg = Math.max(10.0D, DamageUtils.crystalDamage(BlackOut.mc.field_1724, BlackOut.mc.field_1724.method_5829(), entity.method_19538()));
            if (dmg < lowest) {
               lowest = dmg;
               crystal = entity;
            }
         }
      }

      return crystal;
   }

   protected boolean validForBlocking(class_1297 entity) {
      Iterator var2 = ((Boolean)this.alwaysAttack.get() ? this.blockPlacements : this.valids).iterator();

      class_2338 pos;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         pos = (class_2338)var2.next();
      } while(!BoxUtils.get(pos).method_994(entity.method_5829()));

      return true;
   }

   private void placeBlocks() {
      List<class_2338> positions = new ArrayList();
      this.setSupport();
      if (this.support) {
         positions.addAll(this.supportPositions);
      } else {
         positions.addAll(this.blockPlacements);
      }

      this.valids.clear();
      Stream var10000 = positions.stream().filter(this::validBlock);
      List var10001 = this.valids;
      Objects.requireNonNull(var10001);
      var10000.forEach(var10001::add);
      this.updateAttack();
      this.updatePlaces();
      if ((this.result = ((SwitchMode)this.switchMode.get()).find(this::valid)).wasFound()) {
         this.blocksLeft = Math.min(this.placesLeft, this.result.amount());
         this.hand = OLEPOSSUtils.getHand(this::valid);
         this.switched = false;
         this.valids.stream().filter((pos) -> {
            return !EntityUtils.intersects(BoxUtils.get(pos), this::validEntity);
         }).sorted(Comparator.comparingDouble(RotationUtils::getYaw)).forEach(this::place);
         if (this.switched && this.hand == null) {
            ((SwitchMode)this.switchMode.get()).swapBack();
         }

      }
   }

   private void updatePlaces() {
      switch((Surround.PlaceDelayMode)this.placeDelayMode.get()) {
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

   private boolean validBlock(class_2338 pos) {
      if (!OLEPOSSUtils.replaceable(pos)) {
         return false;
      } else {
         PlaceData data = SettingUtils.getPlaceData(pos, (p, d) -> {
            return this.placed.contains((Object)p);
         }, (DoublePredicate)null, !(Boolean)this.allowSneak.get());
         if (!data.valid()) {
            return false;
         } else if (!SettingUtils.inPlaceRange(data.pos())) {
            return false;
         } else {
            return !this.placed.contains((Object)pos);
         }
      }
   }

   private void place(class_2338 pos) {
      if (this.result.amount() > 0) {
         PlaceData data = SettingUtils.getPlaceData(pos, (p, d) -> {
            return this.placed.contains((Object)p);
         }, (DoublePredicate)null, !(Boolean)this.allowSneak.get());
         if (data.valid()) {
            this.placing = true;
            if (SettingUtils.shouldRotate(RotationType.BlockPlace)) {
               switch((ObsidianModule.RotationMode)this.rotationMode.get()) {
               case Normal:
                  if (!this.rotateBlock(data, RotationType.BlockPlace, "placing")) {
                     return;
                  }
                  break;
               case Instant:
                  if (!this.rotateBlock(data, RotationType.InstantBlockPlace, "placing")) {
                     return;
                  }
               }
            }

            if (this.blocksLeft > 0) {
               if (!this.switched && this.hand == null) {
                  this.switched = ((SwitchMode)this.switchMode.get()).swap(this.result.slot());
               }

               if (this.switched || this.hand != null) {
                  if (SettingUtils.shouldRotate(RotationType.BlockPlace) && this.rotationMode.get() == ObsidianModule.RotationMode.Packet) {
                     Rotation rotation = SettingUtils.getRotation(data.pos(), data.dir(), data.pos().method_46558(), RotationType.BlockPlace);
                     this.sendPacket(new class_2831(rotation.yaw(), rotation.pitch(), Managers.PACKET.isOnGround()));
                  }

                  this.placeBlock(this.hand, data.pos().method_46558(), data.dir(), data.pos());
                  if ((Boolean)this.placeSwing.get()) {
                     this.clientSwing((SwingHand)this.placeHand.get(), this.hand);
                  }

                  if (!(Boolean)this.packet.get()) {
                     this.setBlock(pos);
                  }

                  this.placed.add(pos, this.getCooldown());
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
            this.blockPlaceSound(pos, this.result.stack());
         });
      }
   }

   private void setSupport() {
      this.support = false;
      double min = 10000.0D;
      Iterator var3 = this.blockPlacements.iterator();

      class_2338 pos;
      double y;
      while(var3.hasNext()) {
         pos = (class_2338)var3.next();
         if (this.validBlock(pos)) {
            y = RotationUtils.getYaw(pos.method_46558());
            if (y < min) {
               this.support = false;
               min = y;
            }
         }
      }

      var3 = this.supportPositions.iterator();

      while(var3.hasNext()) {
         pos = (class_2338)var3.next();
         if (this.validBlock(pos)) {
            y = RotationUtils.getYaw(pos.method_46558());
            if (y < min) {
               this.support = true;
               min = y;
            }
         }
      }

   }

   private boolean valid(class_1799 stack) {
      class_1792 var3 = stack.method_7909();
      boolean var10000;
      if (var3 instanceof class_1747) {
         class_1747 block = (class_1747)var3;
         if (((List)(this.support ? this.supportBlocks : this.blocks).get()).contains(block.method_7711())) {
            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
   }

   private void updateSupport() {
      this.supportPositions.clear();
      this.blockPlacements.forEach((pos) -> {
         class_2338 support = this.findSupport(pos);
         if (support != null) {
            this.supportPositions.add(support);
         }

      });
   }

   protected class_2338 findSupport(class_2338 pos) {
      if (!OLEPOSSUtils.replaceable(pos)) {
         return null;
      } else if (this.hasSupport(pos, true)) {
         return null;
      } else {
         class_2350[] var2 = class_2350.values();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            class_2350 dir = var2[var4];
            if (dir != class_2350.field_11036) {
               class_2338 pos2 = pos.method_10093(dir);
               if (!this.blockPlacements.contains(pos2) && !this.insideBlocks.contains(pos2) && !EntityUtils.intersects(BoxUtils.get(pos2), (entity) -> {
                  return entity instanceof class_1657 && !entity.method_7325();
               }) && SettingUtils.getPlaceData(pos2, !(Boolean)this.allowSneak.get()).valid() && SettingUtils.inPlaceRange(pos2) && SettingUtils.getPlaceData(pos, (p, d) -> {
                  return d == dir;
               }, (DoublePredicate)null, !(Boolean)this.allowSneak.get()).valid()) {
                  return pos2;
               }
            }
         }

         return null;
      }
   }

   protected boolean hasSupport(class_2338 pos, boolean check) {
      PlaceData data = SettingUtils.getPlaceData(pos, !(Boolean)this.allowSneak.get());
      if (data.valid()) {
         return true;
      } else {
         class_2350[] var4 = class_2350.values();
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            class_2350 dir = var4[var6];
            PlaceData data2 = SettingUtils.getPlaceData(pos, (p, d) -> {
               return d == dir;
            }, (DoublePredicate)null, !(Boolean)this.allowSneak.get());
            if (data2.valid()) {
               class_2338 offsetPos = pos.method_10093(dir);
               if (this.supportPositions.contains(offsetPos)) {
                  return true;
               }

               if (check && this.blockPlacements.contains(offsetPos) && this.hasSupport(offsetPos, false)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   private void updateBlocks() {
      this.updateInsideBlocks();
      this.blockPlacements.clear();
      this.addPlacements();
   }

   private void updateInsideBlocks() {
      this.insideBlocks.clear();
      this.addInsideBlocks();
   }

   protected void addInsideBlocks() {
   }

   protected void addPlacements() {
   }

   protected void addBlocks(class_1297 entity, int[] size) {
   }

   protected boolean validEntity(class_1297 entity) {
      if (entity instanceof class_1511 && System.currentTimeMillis() - this.lastAttack < 100L) {
         return false;
      } else {
         return !(entity instanceof class_1542);
      }
   }

   protected boolean intersects(class_1657 player) {
      class_238 playerBox = player.method_5829().method_35580(1.0E-4D, 1.0E-4D, 1.0E-4D);
      Iterator var3 = this.blockPlacements.iterator();

      class_2338 pos;
      do {
         if (!var3.hasNext()) {
            return false;
         }

         pos = (class_2338)var3.next();
      } while(!playerBox.method_994(BoxUtils.get(pos)));

      return true;
   }

   protected int[] getSize(class_1657 player) {
      int[] size = new int[4];
      double x = player.method_23317() - (double)player.method_31477();
      double z = player.method_23321() - (double)player.method_31479();
      if (x < 0.3D) {
         size[0] = -1;
      }

      if (x > 0.7D) {
         size[1] = 1;
      }

      if (z < 0.3D) {
         size[2] = -1;
      }

      if (z > 0.7D) {
         size[3] = 1;
      }

      return size;
   }

   protected class_2338 getPos() {
      return BlackOut.mc.field_1724 == null ? class_2338.field_10980 : new class_2338(BlackOut.mc.field_1724.method_31477(), (int)Math.round(BlackOut.mc.field_1724.method_23318()), BlackOut.mc.field_1724.method_31479());
   }

   public static enum RotationMode {
      Normal,
      Instant,
      Packet;

      // $FF: synthetic method
      private static ObsidianModule.RotationMode[] $values() {
         return new ObsidianModule.RotationMode[]{Normal, Instant, Packet};
      }
   }
}
