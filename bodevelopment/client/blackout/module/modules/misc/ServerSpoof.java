package bodevelopment.client.blackout.module.modules.misc;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.PacketEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import java.util.UUID;
import net.minecraft.class_2596;
import net.minecraft.class_2720;
import net.minecraft.class_2856;
import net.minecraft.class_2856.class_2857;

public class ServerSpoof extends Module {
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<Double> delay;
   private final class_2857[] statuses;
   private UUID id;
   private long time;
   private int progress;

   public ServerSpoof() {
      super("Server Spoof", ".", SubCategory.MISC, true);
      this.delay = this.sgGeneral.d("Delay", 2.0D, 0.0D, 10.0D, 0.1D, ".");
      this.statuses = new class_2857[]{class_2857.field_13016, class_2857.field_47704, class_2857.field_13017};
      this.time = -1L;
      this.progress = 0;
   }

   @Event
   public void onTick(TickEvent.Pre event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null && BlackOut.mc.field_1724.field_6012 >= 20 && this.time >= 0L) {
         if ((double)System.currentTimeMillis() > (double)this.time + (Double)this.delay.get() * 1000.0D) {
            this.sendPacket(new class_2856(this.id, this.statuses[this.progress]));
            if (this.progress > 1) {
               this.time = -1L;
            } else {
               this.time = System.currentTimeMillis();
            }

            ++this.progress;
         }

      }
   }

   @Event
   public void onReceive(PacketEvent.Receive.Post event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_2720) {
         class_2720 packet = (class_2720)var3;
         event.setCancelled(true);
         this.id = packet.comp_2158();
         this.time = System.currentTimeMillis();
         this.progress = 0;
      }

   }
}
