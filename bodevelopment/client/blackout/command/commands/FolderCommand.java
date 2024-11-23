package bodevelopment.client.blackout.command.commands;

import bodevelopment.client.blackout.command.Command;
import bodevelopment.client.blackout.util.FileUtils;
import java.io.IOException;

public class FolderCommand extends Command {
   public int fakePlayerID = 0;

   public FolderCommand() {
      super("folder", "fakeplayer");
   }

   public String execute(String[] args) {
      try {
         Runtime var10000 = Runtime.getRuntime();
         String[] var10001 = new String[]{"configs"};
         var10000.exec("explorer.exe /select," + FileUtils.getFile(var10001).getAbsolutePath());
         return "Opened folder";
      } catch (IOException var3) {
         throw new RuntimeException(var3);
      }
   }
}
