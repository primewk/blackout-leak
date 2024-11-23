package bodevelopment.client.blackout.manager.managers;

import bodevelopment.client.blackout.command.Command;
import bodevelopment.client.blackout.manager.Manager;
import bodevelopment.client.blackout.util.ClassUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.class_124;

public class CommandManager extends Manager {
   private final Map<String, Command> commands = new HashMap();
   public String prefix = "-";

   public void init() {
      this.commands.clear();
      List<Command> commandObjects = new ArrayList();
      ClassUtils.forEachClass((clazz) -> {
         commandObjects.add((Command)ClassUtils.instance(clazz));
      }, Command.class.getCanonicalName().replace(Command.class.getSimpleName(), "commands"));
      commandObjects.stream().sorted(Comparator.comparing((o) -> {
         return o.name;
      })).forEach(this::c);
   }

   private void c(Command command) {
      this.commands.put(command.usage, command);
   }

   public String onCommand(String[] args) {
      if (this.commands.containsKey(args[0])) {
         Command command = (Command)this.commands.get(args[0]);
         String respond = command.execute((String[])Arrays.copyOfRange(args, 1, args.length));
         return String.format("[%s]%s %s", command.name, class_124.field_1080, respond);
      } else {
         return null;
      }
   }
}
