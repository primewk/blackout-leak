package bodevelopment.client.blackout.hud.elements;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.hud.TextElement;
import net.minecraft.class_640;

public class Ping extends TextElement {
   public Ping() {
      super("Ping", "Shows your current ping on screen");
      this.setSize(10.0F, 10.0F);
   }

   public void render() {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         String ping = this.getPing();
         this.drawElement(this.stack, "Ping:", ping);
      }
   }

   private String getPing() {
      class_640 entry = BlackOut.mc.method_1562().method_2874(BlackOut.mc.field_1724.method_7334().getName());
      return entry == null ? "-" : String.valueOf(entry.method_2959());
   }
}
