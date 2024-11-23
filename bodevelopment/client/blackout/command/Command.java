package bodevelopment.client.blackout.command;

public class Command {
   public final String name;
   public final String usage;
   public final String format;

   public Command(String name, String format) {
      this.name = name;
      this.usage = name.toLowerCase();
      this.format = format;
   }

   public String execute(String[] args) {
      return null;
   }
}
