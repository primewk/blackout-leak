package bodevelopment.client.blackout.command.commands;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.command.Command;

public class HClipCommand extends Command {
   public HClipCommand() {
      super("hclip", "hclip dist");
   }

   public String execute(String[] args) {
      if (args.length > 0) {
         try {
            double value = Double.parseDouble(args[0].replace(",", "."));
            double yaw = Math.toRadians((double)(BlackOut.mc.field_1724.method_36454() + 90.0F));
            BlackOut.mc.field_1724.method_5814(BlackOut.mc.field_1724.method_23317() + Math.cos(yaw) * value, BlackOut.mc.field_1724.method_23318(), BlackOut.mc.field_1724.method_23321() + Math.sin(yaw) * value);
         } catch (Exception var6) {
            return "invalid amount";
         }
      }

      return this.format;
   }
}
