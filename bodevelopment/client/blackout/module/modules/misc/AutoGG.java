package bodevelopment.client.blackout.module.modules.misc;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.PacketEvent;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.util.ChatUtils;
import net.minecraft.class_2596;
import net.minecraft.class_7439;

public class AutoGG extends Module {
   public AutoGG() {
      super("Auto GG", ".", SubCategory.MISC, true);
   }

   @Event
   public void onReceive(PacketEvent.Receive.Pre event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         class_2596 var3 = event.packet;
         if (var3 instanceof class_7439) {
            class_7439 packet = (class_7439)var3;
            String unformattedText = packet.comp_763().getString();
            String[] look = new String[]{"You won! Want to play again? Click here! ", "You lost! Want to play again? Click here! ", "You died! Want to play again? Click here! "};
            if (unformattedText == null) {
               return;
            }

            String[] var5 = look;
            int var6 = look.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               String s = var5[var7];
               if (unformattedText.contains(s)) {
                  ChatUtils.sendMessage("gg");
               }
            }
         }

      }
   }
}
