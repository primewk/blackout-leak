package bodevelopment.client.blackout.module.modules.movement;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.PacketEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import net.minecraft.class_243;
import net.minecraft.class_2828;
import net.minecraft.class_2828.class_2830;

public class MovementSpam extends Module {
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<Integer> packets;
   private int packetsSent;

   public MovementSpam() {
      super("Movement Spam", "Sends movement packets at prev pos to do funny stuff.", SubCategory.MOVEMENT, true);
      this.packets = this.sgGeneral.i("Packets", 1, 1, 10, 1, ".");
   }

   public boolean shouldSkipListeners() {
      return false;
   }

   public String getInfo() {
      return String.valueOf(this.packetsSent);
   }

   @Event
   public void onTickPost(TickEvent.Post event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null && this.enabled) {
         class_243 pos = Managers.PACKET.pos;

         for(int i = 0; i < (Integer)this.packets.get(); ++i) {
            this.sendPacket(new class_2830(pos.field_1352, pos.field_1351, pos.field_1350, Managers.ROTATION.prevYaw, Managers.ROTATION.prevPitch, Managers.PACKET.isOnGround()));
         }

      }
   }

   @Event
   public void onSend(PacketEvent.Sent event) {
      if (event.packet instanceof class_2828) {
         ++this.packetsSent;
      }

   }
}
