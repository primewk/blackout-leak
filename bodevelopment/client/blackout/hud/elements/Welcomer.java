package bodevelopment.client.blackout.hud.elements;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.hud.TextElement;
import bodevelopment.client.blackout.module.setting.Setting;
import java.time.LocalTime;

public class Welcomer extends TextElement {
   public final Setting<Welcomer.Mode> mode;

   public Welcomer() {
      super("Welcomer", "Says hello to you");
      this.mode = this.sgGeneral.e("Mode", Welcomer.Mode.Simple, ".");
      this.setSize(10.0F, 10.0F);
   }

   public void render() {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         this.stack.method_22903();
         LocalTime currentTime = LocalTime.now();
         String timetxt;
         if (currentTime.isBefore(LocalTime.NOON)) {
            timetxt = "Good Morning,";
         } else if (currentTime.isBefore(LocalTime.of(18, 0))) {
            timetxt = "Good afternoon,";
         } else if (currentTime.isBefore(LocalTime.of(22, 0))) {
            timetxt = "Good evening,";
         } else {
            timetxt = "Good night,";
         }

         String txt;
         if (this.mode.get() == Welcomer.Mode.Time) {
            txt = timetxt;
         } else {
            txt = "Welcome to Blackout Client";
         }

         this.setSize(BlackOut.FONT.getWidth(txt), BlackOut.FONT.getHeight());
         this.drawElement(this.stack, txt, BlackOut.mc.field_1724.method_5477().getString());
         this.stack.method_22909();
      }
   }

   public static enum Mode {
      Simple,
      Time;

      // $FF: synthetic method
      private static Welcomer.Mode[] $values() {
         return new Welcomer.Mode[]{Simple, Time};
      }
   }
}
