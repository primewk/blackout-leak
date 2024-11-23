package bodevelopment.client.blackout.module;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.MoveEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.util.MovementPrediction;
import bodevelopment.client.blackout.util.SettingUtils;
import net.minecraft.class_243;

public class MoveUpdateModule extends Module {
   private MoveUpdateModule.Mode type;

   public MoveUpdateModule(String name, String description, SubCategory category) {
      super(name, description, category, true);
      this.type = MoveUpdateModule.Mode.Normal;
   }

   @Event
   public void onTickPre(TickEvent.Pre event) {
      this.type = MoveUpdateModule.Mode.values()[(SettingUtils.grimMovement() ? 2 : 0) + (SettingUtils.grimPackets() ? 1 : 0)];
      this.preTick();
   }

   @Event
   public void onMovePost(MoveEvent.Post event) {
      this.postMove();
   }

   @Event
   public void onTickPost(TickEvent.Post event) {
      this.postTick();
   }

   protected void preTick() {
      switch(this.type) {
      case Full:
         this.update(true, false);
         this.spoofedCalc();
         break;
      case Movement:
         this.spoofedCalc();
         break;
      case Packet:
         this.update(true, false);
      }

   }

   protected void postMove() {
      switch(this.type) {
      case Packet:
      case Normal:
         this.update(false, true);
      default:
      }
   }

   protected void postTick() {
      switch(this.type) {
      case Movement:
      case Normal:
         this.update(true, false);
      default:
      }
   }

   protected void update(boolean allowAction, boolean fakePos) {
   }

   private void spoofedCalc() {
      if (BlackOut.mc.field_1724 != null) {
         class_243 pos = BlackOut.mc.field_1724.method_19538();
         BlackOut.mc.field_1724.method_33574(MovementPrediction.predict(BlackOut.mc.field_1724));
         this.update(false, true);
         BlackOut.mc.field_1724.method_33574(pos);
      }
   }

   private static enum Mode {
      Normal,
      Packet,
      Movement,
      Full;

      // $FF: synthetic method
      private static MoveUpdateModule.Mode[] $values() {
         return new MoveUpdateModule.Mode[]{Normal, Packet, Movement, Full};
      }
   }
}
