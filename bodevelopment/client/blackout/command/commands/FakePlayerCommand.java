package bodevelopment.client.blackout.command.commands;

import bodevelopment.client.blackout.command.Command;
import bodevelopment.client.blackout.manager.Managers;

public class FakePlayerCommand extends Command {
   public int fakePlayerID = 0;

   public FakePlayerCommand() {
      super("fakeplayer", "fakeplayer [add, record, restart, heal, clear]");
   }

   public String execute(String[] args) {
      if (args.length > 0) {
         String var2 = args[0];
         byte var3 = -1;
         switch(var2.hashCode()) {
         case -934908847:
            if (var2.equals("record")) {
               var3 = 1;
            }
            break;
         case 96417:
            if (var2.equals("add")) {
               var3 = 2;
            }
            break;
         case 3198440:
            if (var2.equals("heal")) {
               var3 = 3;
            }
            break;
         case 94746189:
            if (var2.equals("clear")) {
               var3 = 4;
            }
            break;
         case 1097506319:
            if (var2.equals("restart")) {
               var3 = 0;
            }
         }

         switch(var3) {
         case 0:
            Managers.FAKE_PLAYER.restart();
            return "Restarted moving";
         case 1:
            Managers.FAKE_PLAYER.startRecording();
            return "Started recording movement";
         case 2:
            Managers.FAKE_PLAYER.add("KassuKay" + this.fakePlayerID);
            ++this.fakePlayerID;
            return "Added a fake player";
         case 3:
            Managers.FAKE_PLAYER.fakePlayers.forEach((entity) -> {
               entity.method_6033(20.0F);
               entity.method_6073(16.0F);
               entity.field_6213 = 0;
               entity.popped = 0;
            });
            break;
         case 4:
            Managers.FAKE_PLAYER.clear();
            return "Removed all fake players";
         }
      }

      return this.format;
   }
}
