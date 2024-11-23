package bodevelopment.client.blackout.module.modules.combat.misc;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.RotationType;
import bodevelopment.client.blackout.enums.SwingHand;
import bodevelopment.client.blackout.enums.SwitchMode;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.ObsidianModule;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.FindResult;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import bodevelopment.client.blackout.util.RotationUtils;
import bodevelopment.client.blackout.util.SettingUtils;
import net.minecraft.class_1268;
import net.minecraft.class_1747;
import net.minecraft.class_1802;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_2828.class_2831;

public class PearlPhase extends Module {
   public final SettingGroup sgGeneral = this.addGroup("General");
   public final SettingGroup sgRender = this.addGroup("Render");
   public final Setting<SwitchMode> ccSwitchMode;
   public final Setting<SwitchMode> switchMode;
   private final Setting<Boolean> ccBypass;
   public final Setting<Integer> pitch;
   private final Setting<ObsidianModule.RotationMode> rotationMode;
   private final Setting<Boolean> swing;
   private final Setting<SwingHand> swingHand;
   private boolean placed;

   public PearlPhase() {
      super("Pearl Phase", "Throws a pearl", SubCategory.MISC_COMBAT, true);
      this.ccSwitchMode = this.sgGeneral.e("CC Switch Mode", SwitchMode.Normal, "Which method of switching should be used for cc items.");
      this.switchMode = this.sgGeneral.e("Switch Mode", SwitchMode.Normal, "Which method of switching should be used.");
      this.ccBypass = this.sgRender.b("CC Bypass", false, "Does funny stuff to bypass cc's anti delay.");
      this.pitch = this.sgGeneral.i("Pitch", 85, -90, 90, 1, "How deep down to look.");
      this.rotationMode = this.sgGeneral.e("Rotation Mode", ObsidianModule.RotationMode.Normal, ".");
      this.swing = this.sgRender.b("Swing", false, "Renders swing animation when placing throwing a peal");
      this.swingHand = this.sgRender.e("Swing Hand", SwingHand.RealHand, "Which hand should be swung.");
      this.placed = false;
   }

   public void onEnable() {
      this.placed = false;
   }

   @Event
   public void onRender(RenderEvent.World.Pre event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         class_1268 hand = OLEPOSSUtils.getHand(class_1802.field_8634);
         FindResult findResult = ((SwitchMode)this.switchMode.get()).find(class_1802.field_8634);
         if (hand != null || findResult.wasFound()) {
            if (!(Boolean)this.ccBypass.get() || this.cc() || this.placed) {
               switch((ObsidianModule.RotationMode)this.rotationMode.get()) {
               case Normal:
                  if (!this.rotate((float)this.getYaw(), (float)(Integer)this.pitch.get(), RotationType.Other, "look")) {
                     return;
                  }
                  break;
               case Instant:
                  if (!this.rotate((float)this.getYaw(), (float)(Integer)this.pitch.get(), RotationType.InstantOther, "look")) {
                     return;
                  }
               }

               boolean switched = false;
               if (switched || ((SwitchMode)this.switchMode.get()).swap(findResult.slot())) {
                  if (this.rotationMode.get() == ObsidianModule.RotationMode.Packet) {
                     this.sendPacket(new class_2831((float)this.getYaw(), (float)(Integer)this.pitch.get(), Managers.PACKET.isOnGround()));
                  }

                  this.useItem(hand);
                  if ((Boolean)this.swing.get()) {
                     this.clientSwing((SwingHand)this.swingHand.get(), hand);
                  }

                  this.end("look");
                  this.disable("success");
                  if (hand == null) {
                     ((SwitchMode)this.switchMode.get()).swapBack();
                  }

               }
            }
         }
      }
   }

   private boolean cc() {
      FindResult result = null;
      if (!(result = ((SwitchMode)this.ccSwitchMode.get()).find((stack) -> {
         return stack.method_7909() instanceof class_1747;
      })).wasFound()) {
         this.disable("no CC blocks found");
         return false;
      } else {
         class_2338 pos = BlackOut.mc.field_1724.method_24515();
         if (SettingUtils.shouldRotate(RotationType.BlockPlace)) {
            switch((ObsidianModule.RotationMode)this.rotationMode.get()) {
            case Normal:
               if (!this.rotateBlock(pos.method_10074(), class_2350.field_11036, RotationType.BlockPlace, "placing")) {
                  return false;
               }
               break;
            case Instant:
               if (!this.rotateBlock(pos.method_10074(), class_2350.field_11036, RotationType.InstantBlockPlace, "placing")) {
                  return false;
               }
            }
         }

         class_1268 hand = OLEPOSSUtils.getHand((stack) -> {
            return stack.method_7909() instanceof class_1747;
         });
         if (hand == null && !((SwitchMode)this.ccSwitchMode.get()).swap(result.slot())) {
            return false;
         } else {
            if (SettingUtils.shouldRotate(RotationType.BlockPlace) && this.rotationMode.get() == ObsidianModule.RotationMode.Packet) {
               this.sendPacket(new class_2831((float)RotationUtils.getYaw(pos.method_46558()), (float)RotationUtils.getPitch(BlackOut.mc.field_1724.method_33571(), pos.method_46558()), Managers.PACKET.isOnGround()));
            }

            this.placeBlock(hand == null ? class_1268.field_5808 : hand, pos.method_10074().method_46558(), class_2350.field_11036, pos.method_10074());
            if (SettingUtils.shouldRotate(RotationType.BlockPlace)) {
               this.end("placing");
            }

            this.placed = true;
            if (hand == null) {
               ((SwitchMode)this.ccSwitchMode.get()).swapBack();
            }

            return true;
         }
      }
   }

   private int getYaw() {
      return (int)Math.round(RotationUtils.getYaw(new class_243(Math.floor(BlackOut.mc.field_1724.method_23317()) + 0.5D, 0.0D, Math.floor(BlackOut.mc.field_1724.method_23321()) + 0.5D))) + 180;
   }
}
