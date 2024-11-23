package bodevelopment.client.blackout.command.commands;

import bodevelopment.client.blackout.command.Command;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import java.util.HashMap;
import java.util.Map;

public class PanicCommand extends Command {
   private final Map<Module, Boolean> states = new HashMap();
   private boolean isOn = false;

   public PanicCommand() {
      super("panic", "panic [on, off]");
   }

   public String execute(String[] args) {
      if (args.length > 0) {
         String var2 = args[0];
         byte var3 = -1;
         switch(var2.hashCode()) {
         case 3551:
            if (var2.equals("on")) {
               var3 = 0;
            }
            break;
         case 109935:
            if (var2.equals("off")) {
               var3 = 1;
            }
         }

         switch(var3) {
         case 0:
            if (this.isOn) {
               return "already panicking";
            }

            this.states.clear();
            Managers.MODULE.getModules().forEach((m) -> {
               this.states.put(m, m.enabled);
               if (m.enabled) {
                  m.disable();
               }

            });
            this.isOn = true;
            return "started panicking";
         case 1:
            if (!this.isOn) {
               return "already stopped panicking";
            }

            this.states.forEach((m, s) -> {
               if (m.enabled != s) {
                  m.toggle();
               }

            });
            this.isOn = false;
            return "stopped panicking";
         }
      }

      return this.format;
   }
}
