package bodevelopment.client.blackout.command.commands;

import bodevelopment.client.blackout.command.Command;
import bodevelopment.client.blackout.manager.Managers;

public class StatsCommand extends Command {
   public StatsCommand() {
      super("Stats", "Stats reset");
   }

   public String execute(String[] args) {
      if (args.length > 0 && args[0].equals("reset")) {
         Managers.STATS.reset();
         return "reset stats";
      } else {
         return this.format;
      }
   }
}
