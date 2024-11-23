package bodevelopment.client.blackout.hud.elements;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.hud.TextElement;
import bodevelopment.client.blackout.manager.Managers;

public class TPS extends TextElement {
   public TPS() {
      super("TPS", "Shows current server TPS");
      this.setSize(10.0F, 10.0F);
   }

   public void render() {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         String tps = String.format("%.1f", Managers.TPS.tps);
         this.drawElement(this.stack, "TPS", tps);
      }
   }
}
