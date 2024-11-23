package bodevelopment.client.blackout.module.modules.misc;

import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.PacketEvent;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import net.minecraft.class_2793;

public class PortalGodMode extends Module {
   public PortalGodMode() {
      super("Portal God Mode", "Prevents taking damage while in portals", SubCategory.MISC, true);
   }

   @Event
   public void onSend(PacketEvent.Send event) {
      if (event.packet instanceof class_2793) {
         event.setCancelled(true);
      }

   }
}
