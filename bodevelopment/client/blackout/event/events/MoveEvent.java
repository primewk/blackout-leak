package bodevelopment.client.blackout.event.events;

import bodevelopment.client.blackout.event.Cancellable;
import bodevelopment.client.blackout.interfaces.mixin.IVec3d;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.modules.combat.defensive.Clip;
import bodevelopment.client.blackout.module.modules.misc.Clear;
import bodevelopment.client.blackout.module.modules.movement.BurrowTrap;
import bodevelopment.client.blackout.module.modules.movement.ElytraFly;
import bodevelopment.client.blackout.module.modules.movement.FastFall;
import bodevelopment.client.blackout.module.modules.movement.Flight;
import bodevelopment.client.blackout.module.modules.movement.HoleSnap;
import bodevelopment.client.blackout.module.modules.movement.Jesus;
import bodevelopment.client.blackout.module.modules.movement.LongJump;
import bodevelopment.client.blackout.module.modules.movement.PacketFly;
import bodevelopment.client.blackout.module.modules.movement.PhaseWalk;
import bodevelopment.client.blackout.module.modules.movement.Scaffold;
import bodevelopment.client.blackout.module.modules.movement.Speed;
import bodevelopment.client.blackout.module.modules.movement.Strafe;
import bodevelopment.client.blackout.module.modules.movement.TargetStrafe;
import net.minecraft.class_1313;
import net.minecraft.class_243;

public class MoveEvent {
   public static class PostSend {
      private static final MoveEvent.PostSend INSTANCE = new MoveEvent.PostSend();

      public static MoveEvent.PostSend get() {
         return INSTANCE;
      }
   }

   public static class Post {
      private static final MoveEvent.Post INSTANCE = new MoveEvent.Post();

      public static MoveEvent.Post get() {
         return INSTANCE;
      }
   }

   public static class Pre extends Cancellable {
      private static final MoveEvent.Pre INSTANCE = new MoveEvent.Pre();
      public class_243 originalMovement = new class_243(0.0D, 0.0D, 0.0D);
      public class_243 movement = new class_243(0.0D, 0.0D, 0.0D);
      public class_1313 movementType;
      public int xzValue;
      public int yValue;

      public Pre() {
         this.movementType = class_1313.field_6308;
         this.xzValue = 0;
         this.yValue = 0;
      }

      public static MoveEvent.Pre get(class_243 movement, class_1313 type) {
         INSTANCE.movement = movement;
         INSTANCE.movementType = type;
         INSTANCE.originalMovement = movement;
         INSTANCE.xzValue = 0;
         INSTANCE.yValue = 0;
         INSTANCE.setCancelled(false);
         return INSTANCE;
      }

      public void setXZ(Module module, double x, double z) {
         int v = this.getValue(module);
         if (this.xzValue <= v) {
            ((IVec3d)this.movement).blackout_Client$setXZ(x, z);
            this.xzValue = v;
         }
      }

      public void setY(Module module, double y) {
         int v = this.getValue(module);
         if (this.yValue <= v) {
            ((IVec3d)this.movement).blackout_Client$setY(y);
            this.yValue = v;
         }
      }

      public void set(Module module, double x, double y, double z) {
         this.setXZ(module, x, z);
         this.setY(module, y);
      }

      private int getValue(Module module) {
         if (module instanceof BurrowTrap) {
            return 13;
         } else if (module instanceof Clip) {
            return 5;
         } else if (module instanceof ElytraFly) {
            return 10;
         } else if (module instanceof FastFall) {
            return 11;
         } else if (module instanceof Flight) {
            return 9;
         } else if (module instanceof HoleSnap) {
            return 6;
         } else if (module instanceof Jesus) {
            return 4;
         } else if (module instanceof LongJump) {
            return 8;
         } else if (module instanceof PacketFly) {
            return 14;
         } else if (module instanceof PhaseWalk) {
            return 15;
         } else if (module instanceof Scaffold) {
            return 7;
         } else if (module instanceof Speed) {
            return 1;
         } else if (module instanceof Strafe) {
            return 2;
         } else if (module instanceof TargetStrafe) {
            return 3;
         } else {
            return module instanceof Clear ? 16 : 0;
         }
      }
   }
}
