package bodevelopment.client.blackout.module.modules.movement;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.MoveEvent;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import net.minecraft.class_243;
import net.minecraft.class_2828.class_2830;

public class NoFall extends Module {
   private final SettingGroup sgGeneral = this.addGroup("General");
   public final Setting<NoFall.Mode> mode;
   private float fallDist;
   private float lastFallDist;
   private boolean tg;
   private boolean grim;

   public NoFall() {
      super("NoFall", "Prevents fall damage", SubCategory.MOVEMENT, true);
      this.mode = this.sgGeneral.e("Mode", NoFall.Mode.Packet, ".", () -> {
         return true;
      });
      this.lastFallDist = 0.0F;
      this.tg = false;
      this.grim = false;
   }

   public String getInfo() {
      return ((NoFall.Mode)this.mode.get()).name();
   }

   @Event
   public void onMovePost(MoveEvent.PostSend event) {
      if (this.grim) {
         this.grim = false;
         if (!Managers.PACKET.isOnGround()) {
            class_243 vec = Managers.PACKET.pos;
            this.sendPacket(new class_2830(vec.field_1352, vec.field_1351 + 1.0E-6D, vec.field_1350, Managers.ROTATION.prevYaw, Managers.ROTATION.prevPitch, false));
            BlackOut.mc.field_1724.field_6017 = 0.0F;
         }
      }
   }

   @Event
   public void onMove(MoveEvent.Pre event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         if (BlackOut.mc.field_1724.field_6017 == 0.0F) {
            this.fallDist = 0.0F;
         }

         this.fallDist += BlackOut.mc.field_1724.field_6017 - this.lastFallDist;
         this.lastFallDist = BlackOut.mc.field_1724.field_6017;
         switch((NoFall.Mode)this.mode.get()) {
         case Packet:
            if (this.fallDist > 2.0F) {
               Managers.PACKET.spoofOG(true);
               this.fallDist = 0.0F;
            }
            break;
         case LessDMG:
            if ((double)BlackOut.mc.field_1724.field_6017 > 1.5D && this.tg) {
               Managers.PACKET.spoofOG(true);
               this.tg = false;
            }

            if (BlackOut.mc.field_1724.method_24828()) {
               this.tg = true;
            }
            break;
         case GroundSpoof:
            if (BlackOut.mc.field_1724.field_6017 > 2.0F) {
               Managers.PACKET.spoofOG(true);
               this.fallDist = 0.0F;
            }
            break;
         case NoGround:
            Managers.PACKET.spoofOG(false);
            break;
         case Grim:
            if (BlackOut.mc.field_1724.field_6017 >= 3.0F) {
               this.grim = true;
            }
         }

      }
   }

   public static enum Mode {
      Packet,
      GroundSpoof,
      NoGround,
      LessDMG,
      Grim;

      // $FF: synthetic method
      private static NoFall.Mode[] $values() {
         return new NoFall.Mode[]{Packet, GroundSpoof, NoGround, LessDMG, Grim};
      }
   }
}
