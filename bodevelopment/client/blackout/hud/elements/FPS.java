package bodevelopment.client.blackout.hud.elements;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.hud.TextElement;

public class FPS extends TextElement {
   public FPS() {
      super("FPS", "Shows your current FPS on screen");
      this.setSize(10.0F, 10.0F);
   }

   public void render() {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         this.drawElement(this.stack, "FPS:", String.valueOf(BlackOut.mc.method_47599()));
      }
   }
}
