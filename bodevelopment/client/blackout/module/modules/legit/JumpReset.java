package bodevelopment.client.blackout.module.modules.legit;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;

public class JumpReset extends Module {
   public JumpReset() {
      super("Jump Reset", "Resets knockback by jumping", SubCategory.LEGIT, true);
   }

   @Event
   public void onTick(TickEvent.Pre event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null && BlackOut.mc.field_1724.field_6235 > 1 && BlackOut.mc.field_1724.method_24828()) {
         BlackOut.mc.field_1724.method_6043();
      }

   }
}
