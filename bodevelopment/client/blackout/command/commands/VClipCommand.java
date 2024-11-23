package bodevelopment.client.blackout.command.commands;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.command.Command;

public class VClipCommand extends Command {
   public VClipCommand() {
      super("vclip", "vclip y");
   }

   public String execute(String[] args) {
      if (args.length > 0) {
         try {
            double value = Double.parseDouble(args[0].replace(",", "."));
            BlackOut.mc.field_1724.method_5814(BlackOut.mc.field_1724.method_23317(), BlackOut.mc.field_1724.method_23318() + value, BlackOut.mc.field_1724.method_23321());
         } catch (Exception var4) {
            return "invalid amount";
         }
      }

      return this.format;
   }
}
