package bodevelopment.client.blackout.command.commands;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.command.Command;

public class DebugCommand extends Command {
   public DebugCommand() {
      super("debug", "debug [send]");
   }

   public String execute(String[] args) {
      if (args.length > 0 && args[0].equals("send")) {
         String ip = !BlackOut.mc.method_1496() && BlackOut.mc.method_1562() != null && BlackOut.mc.method_1562().method_45734() != null ? BlackOut.mc.method_1562().method_45734().field_3761 : "Singleplayer";
         return "BlackOut 2.0.0 " + BlackOut.TYPE + " " + ip;
      } else {
         return this.format;
      }
   }
}
