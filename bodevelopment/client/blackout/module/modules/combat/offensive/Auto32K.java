package bodevelopment.client.blackout.module.modules.combat.offensive;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.RenderShape;
import bodevelopment.client.blackout.enums.RotationType;
import bodevelopment.client.blackout.enums.SwitchMode;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.MoveEvent;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.interfaces.functional.DoublePredicate;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.ObsidianModule;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.randomstuff.FindResult;
import bodevelopment.client.blackout.randomstuff.PlaceData;
import bodevelopment.client.blackout.util.BoxUtils;
import bodevelopment.client.blackout.util.InvUtils;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import bodevelopment.client.blackout.util.RotationUtils;
import bodevelopment.client.blackout.util.SettingUtils;
import bodevelopment.client.blackout.util.render.Render3DUtils;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.class_1268;
import net.minecraft.class_1657;
import net.minecraft.class_1703;
import net.minecraft.class_1713;
import net.minecraft.class_1716;
import net.minecraft.class_1722;
import net.minecraft.class_1735;
import net.minecraft.class_1747;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1829;
import net.minecraft.class_1887;
import net.minecraft.class_1890;
import net.minecraft.class_1893;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2315;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_2480;
import net.minecraft.class_2680;
import net.minecraft.class_2350.class_2353;
import net.minecraft.class_2828.class_2831;

public class Auto32K extends Module {
   private static Auto32K INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final SettingGroup sgRender = this.addGroup("Render");
   private final Setting<Auto32K.Mode> mode;
   private final Setting<SwitchMode> switchMode;
   private final Setting<Boolean> currentSlot;
   private final Setting<Integer> swordSlot;
   private final Setting<Boolean> yCheck;
   private final Setting<Boolean> serverDir;
   private final Setting<ObsidianModule.RotationMode> rotationMode;
   private final Setting<Boolean> silent;
   private final Setting<RenderShape> renderShapeHopper;
   private final Setting<BlackOutColor> lineColorHopper;
   private final Setting<BlackOutColor> sideColorHopper;
   private final Setting<RenderShape> renderShapeShulker;
   private final Setting<BlackOutColor> lineColorShulker;
   private final Setting<BlackOutColor> sideColorShulker;
   private final Setting<RenderShape> renderShapeDispenser;
   private final Setting<BlackOutColor> lineColorDispenser;
   private final Setting<BlackOutColor> sideColorDispenser;
   private final Setting<RenderShape> renderShapeRedstone;
   private final Setting<BlackOutColor> lineColorRedstone;
   private final Setting<BlackOutColor> sideColorRedstone;
   private class_2350 dispenserDir;
   private class_2338 hopperPos;
   private class_2338 supportPos;
   private class_2338 dispenserPos;
   private class_2338 redstonePos;
   private boolean valid;
   private class_2338 boxInside;
   private class_2338 openedBox;
   private class_2338 openedHopper;
   private boolean placed;
   private boolean found;
   private class_2338 calcMiddle;
   private int progress;
   private class_2350 calcDispenserDir;
   private class_2338 calcHopperPos;
   private class_2338 calcSupportPos;
   private class_2338 calcDispenserPos;
   private class_2338 calcRedstonePos;
   private boolean calcValid;
   private double calcValue;
   private int calcR;

   public Auto32K() {
      super("Auto 32K", ".", SubCategory.OFFENSIVE, true);
      this.mode = this.sgGeneral.e("Mode", Auto32K.Mode.Hopper, ".");
      this.switchMode = this.sgGeneral.e("Switch Mode", SwitchMode.Silent, ".");
      this.currentSlot = this.sgGeneral.b("Current Slot", true, ".");
      this.swordSlot = this.sgGeneral.i("Slot", 1, 1, 9, 1, ".", () -> {
         return !(Boolean)this.currentSlot.get();
      });
      this.yCheck = this.sgGeneral.b("Y Check", true, ".", () -> {
         return this.mode.get() == Auto32K.Mode.Dispenser;
      });
      this.serverDir = this.sgGeneral.b("Server Dir", true, ".", () -> {
         return this.mode.get() == Auto32K.Mode.Dispenser;
      });
      this.rotationMode = this.sgGeneral.e("Rotation Mode", ObsidianModule.RotationMode.Instant, ".", () -> {
         return this.mode.get() == Auto32K.Mode.Dispenser;
      });
      this.silent = this.sgGeneral.b("Silent", true, ".");
      this.renderShapeHopper = this.sgRender.e("Hopper Render Shape", RenderShape.Full, "Which parts of render should be rendered.");
      this.lineColorHopper = this.sgRender.c("Hopper Line Color", new BlackOutColor(255, 255, 255, 255), "Line color of rendered boxes.");
      this.sideColorHopper = this.sgRender.c("Hopper Side Color", new BlackOutColor(255, 255, 255, 50), "Side color of rendered boxes.");
      this.renderShapeShulker = this.sgRender.e("Shulker Render Shape", RenderShape.Full, "Which parts of render should be rendered.");
      this.lineColorShulker = this.sgRender.c("Shulker Line Color", new BlackOutColor(255, 0, 0, 255), "Line color of rendered boxes.");
      this.sideColorShulker = this.sgRender.c("Shulker Side Color", new BlackOutColor(255, 0, 0, 50), "Side color of rendered boxes.");
      this.renderShapeDispenser = this.sgRender.e("Dispenser Render Shape", RenderShape.Full, "Which parts of render should be rendered.");
      this.lineColorDispenser = this.sgRender.c("Dispenser Line Color", new BlackOutColor(255, 255, 255, 255), "Line color of rendered boxes.");
      this.sideColorDispenser = this.sgRender.c("Dispenser Side Color", new BlackOutColor(255, 255, 255, 50), "Side color of rendered boxes.");
      this.renderShapeRedstone = this.sgRender.e("Redstone Render Shape", RenderShape.Full, "Which parts of render should be rendered.");
      this.lineColorRedstone = this.sgRender.c("Redstone Line Color", new BlackOutColor(255, 0, 0, 255), "Line color of rendered boxes.");
      this.sideColorRedstone = this.sgRender.c("Redstone Side Color", new BlackOutColor(255, 0, 0, 50), "Side color of rendered boxes.");
      this.dispenserDir = null;
      this.hopperPos = null;
      this.supportPos = null;
      this.dispenserPos = null;
      this.redstonePos = null;
      this.valid = false;
      this.boxInside = class_2338.field_10980;
      this.openedBox = class_2338.field_10980;
      this.openedHopper = class_2338.field_10980;
      this.placed = false;
      this.found = false;
      this.calcMiddle = class_2338.field_10980;
      this.progress = 0;
      this.calcDispenserDir = null;
      this.calcHopperPos = null;
      this.calcSupportPos = null;
      this.calcDispenserPos = null;
      this.calcRedstonePos = null;
      this.calcValid = false;
      this.calcValue = 0.0D;
      this.calcR = 0;
      INSTANCE = this;
   }

   public static Auto32K getInstance() {
      return INSTANCE;
   }

   public boolean isSilenting() {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         if (!(Boolean)this.silent.get()) {
            return false;
         } else {
            return BlackOut.mc.field_1724.field_7512 instanceof class_1716 || BlackOut.mc.field_1724.field_7512 instanceof class_1722;
         }
      } else {
         return false;
      }
   }

   public void onEnable() {
      this.resetPos();
   }

   @Event
   public void onMove(MoveEvent.Post event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         if (this.shouldCalc()) {
            this.calc(1.0F);
            this.endCalc();
         }

         if (!SettingUtils.grimPackets()) {
            this.update(false);
         }

      }
   }

   @Event
   public void onTickPre(TickEvent.Pre event) {
      this.placed = false;
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null && SettingUtils.grimPackets()) {
         this.update(false);
      }
   }

   @Event
   public void onTickPost(TickEvent.Post event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         this.update(true);
         if (this.shouldCalc()) {
            this.startCalc();
         }

      }
   }

   @Event
   public void onRender(RenderEvent.World.Pre event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         if (this.shouldCalc()) {
            this.calc(event.tickDelta);
         }

      }
   }

   @Event
   public void onRenderPost(RenderEvent.World.Post event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         if (this.hopperPos != null) {
            this.renderBox(this.hopperPos, this.renderShapeHopper, this.sideColorHopper, this.lineColorHopper);
         }

         if (this.hopperPos != null) {
            this.renderBox(this.hopperPos.method_10084(), this.renderShapeShulker, this.sideColorShulker, this.lineColorShulker);
         }

         if (this.dispenserPos != null) {
            this.renderBox(this.dispenserPos, this.renderShapeDispenser, this.sideColorDispenser, this.lineColorDispenser);
         }

         if (this.redstonePos != null) {
            this.renderBox(this.redstonePos, this.renderShapeRedstone, this.sideColorRedstone, this.lineColorRedstone);
         }

      }
   }

   private void renderBox(class_2338 pos, Setting<RenderShape> shape, Setting<BlackOutColor> sideColor, Setting<BlackOutColor> lineColor) {
      Render3DUtils.box(BoxUtils.get(pos), (BlackOutColor)sideColor.get(), (BlackOutColor)lineColor.get(), (RenderShape)shape.get());
   }

   private void resetPos() {
      this.dispenserDir = null;
      this.hopperPos = null;
      this.supportPos = null;
      this.dispenserPos = null;
      this.redstonePos = null;
      this.valid = false;
      this.boxInside = class_2338.field_10980;
      this.openedBox = class_2338.field_10980;
      this.openedHopper = class_2338.field_10980;
      this.placed = false;
      this.found = false;
      this.calcMiddle = class_2338.field_10980;
      this.progress = 0;
      this.calcDispenserDir = null;
      this.calcHopperPos = null;
      this.calcSupportPos = null;
      this.calcDispenserPos = null;
      this.calcRedstonePos = null;
      this.calcValid = false;
      this.calcValue = 0.0D;
      this.calcR = 0;
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
            class_2338 pos = this.calcMiddle.method_10069(x, y, z);
            this.updatePos(pos);
         }

      }
   }

   private boolean shouldCalc() {
      return !this.valid || !this.found;
   }

   private void startCalc() {
      this.calcDispenserPos = null;
      this.calcHopperPos = null;
      this.calcRedstonePos = null;
      this.calcSupportPos = null;
      this.calcDispenserDir = null;
      this.calcValue = -42069.0D;
      this.found = false;
      this.calcValid = false;
      this.progress = 0;
      this.calcR = (int)Math.ceil(SettingUtils.maxPlaceRange());
      this.calcMiddle = class_2338.method_49638(BlackOut.mc.field_1724.method_33571());
   }

   private void endCalc() {
      this.dispenserDir = this.calcDispenserDir;
      this.hopperPos = this.calcHopperPos;
      this.supportPos = this.calcSupportPos;
      this.dispenserPos = this.calcDispenserPos;
      this.redstonePos = this.calcRedstonePos;
      this.found = this.valid = this.calcValid;
   }

   private void update(boolean place) {
      switch((Auto32K.Mode)this.mode.get()) {
      case Hopper:
         this.moveSword();
         if (!this.valid) {
            return;
         }

         this.place(class_2246.field_10312, this.hopperPos, place);
         if (this.placeShulker(place)) {
            this.hopperUpdate();
         }
         break;
      case Dispenser:
         this.moveSword();
         if (!this.valid) {
            return;
         }

         this.place(class_2246.field_10312, this.hopperPos, place);
         this.place(class_2246.field_10540, this.supportPos, place);
         this.place(class_2246.field_10200, this.dispenserPos, place);
         if (!this.boxUpdate()) {
            return;
         }

         this.place(class_2246.field_10002, this.redstonePos, place);
         this.hopperUpdate();
      }

   }

   private boolean boxUpdate() {
      if (this.dispenserPos == null) {
         return false;
      } else if (this.get(this.dispenserPos).method_26204() != class_2246.field_10200) {
         return false;
      } else {
         class_2350 dir = SettingUtils.getPlaceOnDirection(this.dispenserPos);
         if (dir == null) {
            return false;
         } else {
            boolean isOpened = this.openedBox.equals(this.dispenserPos);
            boolean isBox = this.boxInside.equals(this.dispenserPos);
            if (!isOpened) {
               this.openedBox = this.dispenserPos;
               this.interactBlock(class_1268.field_5808, this.dispenserPos.method_46558(), dir, this.dispenserPos);
            }

            if (!isBox) {
               this.putBox();
            }

            return isBox;
         }
      }
   }

   private boolean hopperUpdate() {
      if (this.get(this.hopperPos).method_26204() != class_2246.field_10312) {
         return false;
      } else {
         class_2350 dir = SettingUtils.getPlaceOnDirection(this.hopperPos);
         if (dir == null) {
            return false;
         } else {
            if (!this.openedHopper.equals(this.hopperPos)) {
               this.openedHopper = this.hopperPos;
               this.interactBlock(class_1268.field_5808, this.hopperPos.method_46558(), dir, this.hopperPos);
            }

            return this.openedHopper.equals(this.hopperPos);
         }
      }
   }

   private int getSlot() {
      return (Boolean)this.currentSlot.get() ? BlackOut.mc.field_1724.method_31548().field_7545 : (Integer)this.swordSlot.get() - 1;
   }

   private void putBox() {
      class_1703 handler = BlackOut.mc.field_1724.field_7512;
      if (handler instanceof class_1716) {
         Iterator var2 = handler.field_7761.iterator();

         class_1735 slot;
         do {
            if (!var2.hasNext()) {
               return;
            }

            slot = (class_1735)var2.next();
         } while(!OLEPOSSUtils.isShulker(slot.method_7677()));

         this.boxInside = this.dispenserPos;
         BlackOut.mc.field_1761.method_2906(handler.field_7763, slot.field_7874, 0, class_1713.field_7794, BlackOut.mc.field_1724);
         BlackOut.mc.field_1724.method_3137();
      }
   }

   private boolean moveSword() {
      class_1703 handler = BlackOut.mc.field_1724.field_7512;
      if (!(handler instanceof class_1722)) {
         return false;
      } else {
         Iterator var2 = handler.field_7761.iterator();

         while(var2.hasNext()) {
            class_1735 slot = (class_1735)var2.next();
            class_1799 stack = slot.method_7677();
            if (stack.method_7909() instanceof class_1829) {
               Map<class_1887, Integer> enchants = class_1890.method_8222(stack);
               if (enchants.containsKey(class_1893.field_9118) && (Integer)enchants.get(class_1893.field_9118) >= 10) {
                  int s = this.getSlot();
                  if (s != BlackOut.mc.field_1724.method_31548().field_7545) {
                     InvUtils.swap(s);
                  }

                  BlackOut.mc.field_1761.method_2906(handler.field_7763, slot.field_7874, s, class_1713.field_7791, BlackOut.mc.field_1724);
                  return true;
               }
            }
         }

         return false;
      }
   }

   private boolean placeShulker(boolean place) {
      class_2338 pos = this.hopperPos.method_10084();
      if (BlackOut.mc.field_1687.method_8320(pos).method_26204() instanceof class_2480) {
         return true;
      } else {
         class_1268 hand = OLEPOSSUtils.getHand(OLEPOSSUtils::isShulker);
         FindResult findResult = null;
         if (hand == null && !(findResult = ((SwitchMode)this.switchMode.get()).find(OLEPOSSUtils::isShulker)).wasFound()) {
            return false;
         } else {
            PlaceData data = SettingUtils.getPlaceData(pos, false);
            if (!data.valid()) {
               return false;
            } else if (!SettingUtils.inInteractRange(data.pos())) {
               return false;
            } else if (SettingUtils.shouldRotate(RotationType.BlockPlace) && !this.rotateBlock(data, RotationType.BlockPlace, "placing")) {
               return false;
            } else if (place && !this.placed) {
               if (hand == null && !((SwitchMode)this.switchMode.get()).swap(findResult.slot())) {
                  return false;
               } else {
                  this.placeBlock(hand, data);
                  class_1792 var8 = (hand != null ? Managers.PACKET.handStack(hand) : findResult.stack()).method_7909();
                  class_2248 var10000;
                  if (var8 instanceof class_1747) {
                     class_1747 blockitem = (class_1747)var8;
                     var10000 = blockitem.method_7711();
                  } else {
                     var10000 = class_2246.field_10603;
                  }

                  class_2680 state = var10000.method_9564();
                  BlackOut.mc.field_1687.method_8501(pos, state);
                  this.placed = true;
                  if (hand == null) {
                     ((SwitchMode)this.switchMode.get()).swapBack();
                  }

                  return true;
               }
            } else {
               return false;
            }
         }
      }
   }

   private void place(class_2248 block, class_2338 pos, boolean place) {
      if (pos != null) {
         class_1792 var5 = block.method_8389();
         if (var5 instanceof class_1747) {
            class_1747 blockItem = (class_1747)var5;
            if (BlackOut.mc.field_1687.method_8320(pos).method_26204() != block) {
               class_1268 hand = OLEPOSSUtils.getHand((class_1792)blockItem);
               FindResult findResult = null;
               if (hand != null || (findResult = ((SwitchMode)this.switchMode.get()).find((class_1792)blockItem)).wasFound()) {
                  PlaceData data = SettingUtils.getPlaceData(pos, false);
                  if (data.valid()) {
                     if (SettingUtils.inPlaceRange(data.pos())) {
                        if (!SettingUtils.shouldRotate(RotationType.BlockPlace) || this.rotateBlock(data, RotationType.BlockPlace, "placing")) {
                           if (place && !this.placed) {
                              if (block == class_2246.field_10200) {
                                 switch((ObsidianModule.RotationMode)this.rotationMode.get()) {
                                 case Instant:
                                    if (!this.rotate(this.dispenserDir.method_10144(), 0.0F, 0.0D, 45.0D, RotationType.InstantOther, "facing")) {
                                       return;
                                    }
                                    break;
                                 case Normal:
                                    if (!this.rotate(this.dispenserDir.method_10144(), 0.0F, 0.0D, 45.0D, RotationType.Other, "facing")) {
                                       return;
                                    }
                                 }
                              }

                              if (hand != null || ((SwitchMode)this.switchMode.get()).swap(findResult.slot())) {
                                 if (this.rotationMode.get() == ObsidianModule.RotationMode.Packet) {
                                    this.sendPacket(new class_2831(this.dispenserDir.method_10144(), 0.0F, Managers.PACKET.isOnGround()));
                                 }

                                 this.placeBlock(hand, data);
                                 class_2680 state = block.method_9564();
                                 if (block == class_2246.field_10200) {
                                    state = (class_2680)state.method_11657(class_2315.field_10918, class_2350.method_10150((double)Managers.ROTATION.prevYaw).method_10153());
                                 }

                                 BlackOut.mc.field_1687.method_8501(pos, state);
                                 this.placed = true;
                                 if (hand == null) {
                                    ((SwitchMode)this.switchMode.get()).swapBack();
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

   private void updatePos(class_2338 hopper) {
      switch((Auto32K.Mode)this.mode.get()) {
      case Hopper:
         this.updateHopper(hopper);
         break;
      case Dispenser:
         this.updateDispenser(hopper);
      }

   }

   private void updateHopper(class_2338 hopper) {
      double value = this.getValue(hopper);
      if (!(value < this.calcValue)) {
         class_2680 state = this.get(hopper);
         if (state.method_26204() != class_2246.field_10312) {
            if (!OLEPOSSUtils.replaceable(hopper)) {
               return;
            }

            PlaceData data = SettingUtils.getPlaceData(hopper);
            if (!data.valid()) {
               return;
            }

            if (!SettingUtils.inPlaceRange(data.pos())) {
               return;
            }
         }

         if (SettingUtils.inInteractRange(hopper) && SettingUtils.getPlaceOnDirection(hopper) != null) {
            if (OLEPOSSUtils.replaceable(hopper.method_10084()) || this.get(hopper.method_10084()).method_26204() instanceof class_2480) {
               this.calcHopperPos = hopper;
               this.calcValid = true;
               this.calcValue = value;
            }
         }
      }
   }

   private void updateDispenser(class_2338 hopper) {
      double value = this.getValue(hopper);
      if (!(value < this.calcValue)) {
         class_2680 state = this.get(hopper);
         if (state.method_26204() != class_2246.field_10312) {
            if (!OLEPOSSUtils.replaceable(hopper)) {
               return;
            }

            PlaceData data = SettingUtils.getPlaceData(hopper);
            if (!data.valid()) {
               return;
            }

            if (!SettingUtils.inPlaceRange(data.pos())) {
               return;
            }
         }

         if (SettingUtils.inInteractRange(hopper) && SettingUtils.getPlaceOnDirection(hopper) != null) {
            if (OLEPOSSUtils.replaceable(hopper.method_10084()) || this.get(hopper.method_10084()).method_26204() instanceof class_2480) {
               Iterator var14 = class_2353.field_11062.iterator();

               while(true) {
                  class_2350 dir;
                  class_2338 pos;
                  do {
                     if (!var14.hasNext()) {
                        return;
                     }

                     dir = (class_2350)var14.next();
                     pos = hopper.method_10093(dir).method_10084();
                  } while(!this.validDispenser(pos, dir));

                  class_2350[] var8 = class_2350.values();
                  int var9 = var8.length;

                  for(int var10 = 0; var10 < var9; ++var10) {
                     class_2350 direction = var8[var10];
                     class_2338 pos2 = pos.method_10093(direction);
                     if (this.get(pos2).method_26204() == class_2246.field_10002 && this.get(pos).method_26204() != class_2246.field_10200) {
                        break;
                     }

                     if (OLEPOSSUtils.replaceable(pos2) && direction != class_2350.field_11033 && direction != dir.method_10153()) {
                        PlaceData data = SettingUtils.getPlaceData(pos2, false);
                        if (data.valid() && SettingUtils.inPlaceRange(data.pos())) {
                           this.calcDispenserDir = dir;
                           this.calcHopperPos = hopper;
                           this.calcSupportPos = hopper.method_10093(dir);
                           this.calcDispenserPos = this.calcSupportPos.method_10084();
                           this.calcRedstonePos = this.calcDispenserPos.method_10093(direction);
                           this.calcValid = true;
                           this.calcValue = value;
                           return;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private boolean directionCheck(class_2338 pos, class_2350 direction) {
      class_243 center = pos.method_46558();
      double pitch;
      if ((Boolean)this.serverDir.get()) {
         pitch = RotationUtils.getYaw(center);
         if (Math.abs(RotationUtils.yawAngle((double)direction.method_10144(), pitch)) > 40.0D) {
            return false;
         }
      }

      if ((Boolean)this.yCheck.get()) {
         pitch = RotationUtils.getPitch(center);
         return Math.abs(pitch) < 45.0D;
      } else {
         return true;
      }
   }

   private double getValue(class_2338 pos) {
      double value = 0.0D;
      Iterator var4 = BlackOut.mc.field_1687.method_18456().iterator();

      while(var4.hasNext()) {
         class_1657 player = (class_1657)var4.next();
         double distance = player.method_5707(pos.method_46558());
         if (distance < 100.0D) {
            value += distance;
         }
      }

      return value;
   }

   private boolean validDispenser(class_2338 pos, class_2350 direction) {
      class_2680 state = BlackOut.mc.field_1687.method_8320(pos);
      if (SettingUtils.getPlaceOnDirection(pos) == null) {
         return false;
      } else if (state.method_26204() == class_2246.field_10200) {
         return state.method_11654(class_2315.field_10918) == direction.method_10153();
      } else if (!OLEPOSSUtils.replaceable(pos)) {
         return false;
      } else if (!this.directionCheck(pos, direction)) {
         return false;
      } else {
         PlaceData data = SettingUtils.getPlaceData(pos, (p, d) -> {
            return d == class_2350.field_11033;
         }, (DoublePredicate)null);
         return data.valid() && SettingUtils.inPlaceRange(data.pos());
      }
   }

   private class_2680 get(class_2338 pos) {
      return BlackOut.mc.field_1687.method_8320(pos);
   }

   public static enum Mode {
      Hopper,
      Dispenser;

      // $FF: synthetic method
      private static Auto32K.Mode[] $values() {
         return new Auto32K.Mode[]{Hopper, Dispenser};
      }
   }
}
