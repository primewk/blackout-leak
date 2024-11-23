package bodevelopment.client.blackout.module.modules.combat.defensive;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.RotationType;
import bodevelopment.client.blackout.enums.SwingHand;
import bodevelopment.client.blackout.enums.SwitchMode;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.PacketEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.modules.client.Notifications;
import bodevelopment.client.blackout.module.modules.movement.PacketFly;
import bodevelopment.client.blackout.module.modules.movement.Scaffold;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.FindResult;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import bodevelopment.client.blackout.util.SettingUtils;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.class_1268;
import net.minecraft.class_1747;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2350;
import net.minecraft.class_2708;
import net.minecraft.class_3532;
import net.minecraft.class_2828.class_2829;
import net.minecraft.class_2828.class_2830;
import net.minecraft.class_2828.class_2831;

public class Burrow extends Module {
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final SettingGroup sgLagBack = this.addGroup("Lag Back");
   private final SettingGroup sgRender = this.addGroup("Render");
   private final Setting<SwitchMode> switchMode;
   private final Setting<List<class_2248>> blocks;
   private final Setting<Boolean> instaRot;
   private final Setting<Boolean> pFly;
   private final Setting<Boolean> enableScaffold;
   private final Setting<Double> lagBackOffset;
   private final Setting<Integer> lagBackPackets;
   private final Setting<Boolean> smooth;
   private final Setting<Boolean> syncPacket;
   private final Setting<Boolean> renderSwing;
   private final Setting<SwingHand> swingHand;
   private boolean success;
   private boolean enabledPFly;
   private boolean enabledScaffold;
   private final Predicate<class_1799> predicate;

   public Burrow() {
      super("Burrow", "Places a block inside your feet.", SubCategory.DEFENSIVE, true);
      this.switchMode = this.sgGeneral.e("Switch Mode", SwitchMode.Silent, "Method of switching.");
      this.blocks = this.sgGeneral.bl("Blocks", "Blocks to use.", class_2246.field_10540, class_2246.field_10443);
      this.instaRot = this.sgGeneral.b("Insta Rotation", false, "Instantly rotates.");
      this.pFly = this.sgGeneral.b("Packet Fly", false, "Enabled packetfly after burrowing.");
      SettingGroup var10001 = this.sgGeneral;
      Setting var10005 = this.pFly;
      Objects.requireNonNull(var10005);
      this.enableScaffold = var10001.b("Scaffold", false, "Enabled scaffold after burrowing.", var10005::get);
      this.lagBackOffset = this.sgLagBack.d("Offset", -0.1D, -10.0D, 10.0D, 0.2D, "Y offset for rubberband packet.");
      this.lagBackPackets = this.sgLagBack.i("Packets", 1, 1, 20, 1, "How many offset packets to send.");
      this.smooth = this.sgLagBack.b("Smooth", false, "Enabled scaffold after burrowing.");
      var10001 = this.sgLagBack;
      var10005 = this.smooth;
      Objects.requireNonNull(var10005);
      this.syncPacket = var10001.b("Sync Packet", false, ".", var10005::get);
      this.renderSwing = this.sgRender.b("Render Swing", true, "Renders swing animation when placing a block.");
      var10001 = this.sgRender;
      SwingHand var10003 = SwingHand.RealHand;
      var10005 = this.renderSwing;
      Objects.requireNonNull(var10005);
      this.swingHand = var10001.e("Swing Hand", var10003, "Which hand should be swung.", var10005::get);
      this.success = false;
      this.enabledPFly = false;
      this.enabledScaffold = false;
      this.predicate = (itemStack) -> {
         class_1792 patt4215$temp = itemStack.method_7909();
         if (patt4215$temp instanceof class_1747) {
            class_1747 block = (class_1747)patt4215$temp;
            return ((List)this.blocks.get()).contains(block.method_7711());
         } else {
            return false;
         }
      };
   }

   public void onEnable() {
      this.success = false;
      this.enabledPFly = false;
      this.enabledScaffold = false;
   }

   public void onDisable() {
      PacketFly pFly = PacketFly.getInstance();
      if (this.enabledPFly) {
         pFly.disable(pFly.getDisplayName() + " disabled by Burrow");
      }

      if (this.enabledScaffold) {
         pFly.disable(pFly.getDisplayName() + " disabled by Burrow");
      }

      this.enabledPFly = false;
      this.enabledScaffold = false;
   }

   @Event
   public void onTick(TickEvent.Pre event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null && !this.success) {
         class_1268 hand = OLEPOSSUtils.getHand(this.predicate);
         boolean blocksPresent = hand != null;
         FindResult result = ((SwitchMode)this.switchMode.get()).find(this.predicate);
         if (!blocksPresent) {
            blocksPresent = result.wasFound();
         }

         if (blocksPresent) {
            boolean rotated = (Boolean)this.instaRot.get() || !SettingUtils.shouldRotate(RotationType.BlockPlace) || this.rotatePitch(90.0F, RotationType.BlockPlace, "placing");
            if (rotated) {
               boolean switched = hand != null;
               if (!switched) {
                  switched = ((SwitchMode)this.switchMode.get()).swap(result.slot());
               }

               if (!switched) {
                  this.disable(this.getDisplayName() + " correct blocks not found", 2, Notifications.Type.Alert);
               } else {
                  if ((Boolean)this.instaRot.get() && SettingUtils.shouldRotate(RotationType.BlockPlace)) {
                     this.sendPacket(new class_2831(Managers.ROTATION.nextYaw, 90.0F, Managers.PACKET.isOnGround()));
                  }

                  double y = 0.0D;
                  double velocity = 0.42D;

                  while(y < 1.1D) {
                     y += velocity;
                     velocity = (velocity - 0.08D) * 0.98D;
                     this.sendPacket(new class_2829(BlackOut.mc.field_1724.method_23317(), BlackOut.mc.field_1724.method_23318() + y, BlackOut.mc.field_1724.method_23321(), false));
                  }

                  this.placeBlock(hand, BlackOut.mc.field_1724.method_24515().method_10074().method_46558(), class_2350.field_11036, BlackOut.mc.field_1724.method_24515().method_10074());
                  if ((Boolean)this.renderSwing.get()) {
                     this.clientSwing((SwingHand)this.swingHand.get(), hand);
                  }

                  if (!(Boolean)this.instaRot.get() && SettingUtils.shouldRotate(RotationType.BlockPlace)) {
                     this.end("placing");
                  }

                  this.lagBack(y);
                  this.success = true;
                  if (hand == null) {
                     ((SwitchMode)this.switchMode.get()).swapBack();
                  }

                  if (!(Boolean)this.pFly.get()) {
                     this.disable(this.getDisplayName() + " was successful");
                  }

               }
            }
         }
      }
   }

   @Event
   public void onPacket(PacketEvent.Receive.Pre event) {
      if ((Boolean)this.pFly.get() && this.success && event.packet instanceof class_2708) {
         PacketFly packetFly = PacketFly.getInstance();
         if (!packetFly.enabled) {
            this.enabledPFly = true;
            packetFly.enable("enabled by Burrow");
            Scaffold scaffold = Scaffold.getInstance();
            if ((Boolean)this.enableScaffold.get() && !scaffold.enabled) {
               scaffold.enable("enabled by burrow");
               this.enabledScaffold = true;
            }
         }
      }

   }

   private void lagBack(double y) {
      for(int i = 0; i < (Integer)this.lagBackPackets.get(); ++i) {
         this.sendPacket(new class_2829(BlackOut.mc.field_1724.method_23317(), BlackOut.mc.field_1724.method_23318() + y + (Double)this.lagBackOffset.get(), BlackOut.mc.field_1724.method_23321(), false));
      }

      if ((Boolean)this.smooth.get()) {
         this.sendPacket(Managers.PACKET.incrementedPacket(BlackOut.mc.field_1724.method_19538()));
         if ((Boolean)this.syncPacket.get()) {
            float yaw = class_3532.method_15393(Managers.ROTATION.prevYaw);
            if (yaw < 0.0F) {
               yaw += 360.0F;
            }

            Managers.PACKET.sendInstantly(new class_2830(BlackOut.mc.field_1724.method_23317(), BlackOut.mc.field_1724.method_23318(), BlackOut.mc.field_1724.method_23321(), yaw, Managers.ROTATION.prevPitch, false));
         }
      }

   }
}
