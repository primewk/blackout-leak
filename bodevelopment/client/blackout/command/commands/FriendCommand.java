package bodevelopment.client.blackout.command.commands;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.command.Command;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.manager.managers.FriendsManager;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.minecraft.class_640;

public class FriendCommand extends Command {
   public FriendCommand() {
      super("friends", "friends [add, remove, list] [name]");
   }

   public String execute(String[] args) {
      if (args.length > 0) {
         String var2 = args[0];
         byte var3 = -1;
         switch(var2.hashCode()) {
         case -934610812:
            if (var2.equals("remove")) {
               var3 = 1;
            }
            break;
         case 96417:
            if (var2.equals("add")) {
               var3 = 0;
            }
            break;
         case 3322014:
            if (var2.equals("list")) {
               var3 = 2;
            }
         }

         switch(var3) {
         case 0:
            if (args.length < 2) {
               return "Who should be added?";
            }

            class_640 entry = this.getEntry(args[1]);
            if (entry != null) {
               return Managers.FRIENDS.add(entry.method_2966().getName(), entry.method_2966().getId());
            }

            return Managers.FRIENDS.add(args[1], (UUID)null);
         case 1:
            if (args.length == 1) {
               return "Who should be removed?";
            }

            return Managers.FRIENDS.remove(args[1]);
         case 2:
            StringBuilder builder = new StringBuilder();
            List<FriendsManager.Friend> friends = Managers.FRIENDS.getFriends();
            int i = 0;

            for(int length = friends.size(); i < length; ++i) {
               builder.append(((FriendsManager.Friend)friends.get(i)).getName());
               if (i < length - 1) {
                  builder.append("\n");
               }
            }

            return builder.toString();
         }
      }

      return this.format;
   }

   private class_640 getEntry(String name) {
      Iterator var2 = BlackOut.mc.method_1562().method_45732().iterator();

      class_640 entry;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         entry = (class_640)var2.next();
      } while(!entry.method_2966().getName().equalsIgnoreCase(name));

      return entry;
   }
}
