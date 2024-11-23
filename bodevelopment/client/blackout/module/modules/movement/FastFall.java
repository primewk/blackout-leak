package bodevelopment.client.blackout.module.modules.movement;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.MoveEvent;
import bodevelopment.client.blackout.event.events.PacketEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.util.HoleUtils;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import net.minecraft.class_2338;
import net.minecraft.class_2708;
import org.joml.Vector2i;

public class FastFall extends Module {
   public final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<Boolean> onlyHole;
   private final Setting<Boolean> jumpHole;
   private final Setting<Double> fallSpeed;
   private final Setting<Boolean> rbDisable;
   private boolean jumping;
   private Vector2i jumpPos;
   private boolean rubberbanded;
   private long rbTime;

   public FastFall() {
      super("Fast Fall", "Falls faster.", SubCategory.MOVEMENT, true);
      this.onlyHole = this.sgGeneral.b("Only Hole", false, "Falls faster when above a hole.");
      this.jumpHole = this.sgGeneral.b("Jump Hole", false, "Falls into holes even when you were falling.");
      this.fallSpeed = this.sgGeneral.d("Fall Speed", 1.0D, 0.0D, 10.0D, 0.1D, "How many blocks to fall each second.");
      this.rbDisable = this.sgGeneral.b("Rubberband Disable", true, "Disables fast fall if you rubberband.");
      this.jumping = false;
      this.jumpPos = new Vector2i(0, 0);
      this.rubberbanded = false;
      this.rbTime = 0L;
   }

   public boolean shouldSkipListeners() {
      return !this.enabled && !HoleSnap.getInstance().shouldFastFall();
   }

   @Event
   public void onMove(MoveEvent.Pre event) {
      if (event.movement.field_1351 > 0.2D) {
         this.jumping = true;
         this.jumpPos.set(BlackOut.mc.field_1724.method_31477(), BlackOut.mc.field_1724.method_31479());
      } else {
         if (event.movement.field_1351 < 0.0D) {
            this.jumpPos = new Vector2i(-69420, -69420);
         }

         boolean onGround = OLEPOSSUtils.inside(BlackOut.mc.field_1724, BlackOut.mc.field_1724.method_5829().method_989(0.0D, -0.04D, 0.0D));
         if (onGround) {
            if (this.rbTime <= 0L) {
               this.rbTime = System.currentTimeMillis();
            }

            if (System.currentTimeMillis() - this.rbTime > 100L) {
               this.rubberbanded = false;
               this.rbTime = 0L;
            }
         }

         if (!this.rubberbanded) {
            if (BlackOut.mc.field_1724.method_31477() != this.jumpPos.x || BlackOut.mc.field_1724.method_31479() != this.jumpPos.y) {
               boolean holeCheck = this.aboveHole();
               if (!(Boolean)this.onlyHole.get() || holeCheck) {
                  if (!this.jumping || holeCheck && (Boolean)this.jumpHole.get()) {
                     if (!onGround) {
                        if (!OLEPOSSUtils.inside(BlackOut.mc.field_1724, BlackOut.mc.field_1724.method_5829().method_989(0.0D, -0.6D, 0.0D))) {
                           this.fall(event, holeCheck);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @Event
   public void onTickPost(TickEvent.Post event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1724.method_24828()) {
         this.jumping = false;
      }

   }

   @Event
   public void onReceive(PacketEvent.Receive.Pre event) {
      if (event.packet instanceof class_2708 && (Boolean)this.rbDisable.get()) {
         this.rubberbanded = true;
      }

   }

   private void fall(MoveEvent.Pre event, boolean holeCheck) {
      event.setY(this, -(Double)this.fallSpeed.get());
      if (holeCheck) {
         event.setXZ(this, 0.0D, 0.0D);
      }

   }

   private boolean aboveHole() {
      for(double offset = 0.0D; offset < 7.0D; offset += 0.1D) {
         if (OLEPOSSUtils.inside(BlackOut.mc.field_1724, BlackOut.mc.field_1724.method_5829().method_989(0.0D, -offset, 0.0D))) {
            return HoleUtils.inHole(class_2338.method_49638(BlackOut.mc.field_1724.method_19538().method_1031(0.0D, -offset + 0.12D, 0.0D)));
         }
      }

      return false;
   }
}
