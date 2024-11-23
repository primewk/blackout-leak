package bodevelopment.client.blackout;

import bodevelopment.client.blackout.util.FileUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Installer {
   public static void main(String[] args) throws IOException {
      String name = getVersion();
      String url = String.format("https://github.com/KassuK1/Blackout-Client/releases/download/latest/%s.jar", name);
      InputStream inputStream = (new URL(url)).openStream();
      String path = Installer.class.getProtectionDomain().getCodeSource().getLocation().getFile();
      File parent = (new File(path)).getParentFile();
      File file = new File(parent, name + ".jar");
      FileUtils.addFile(file);
      Files.copy(inputStream, Path.of(file.getPath(), new String[0]), new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
   }

   private static String getVersion() throws IOException {
      URL url = new URL("https://raw.githubusercontent.com/KassuK1/Blackout-Client/main/README.md");
      InputStream readMeStream = url.openStream();
      BufferedReader read = new BufferedReader(new InputStreamReader(readMeStream));
      CompletableFuture<String> version = new CompletableFuture();
      read.lines().forEach((line) -> {
         if (!version.isDone()) {
            readLine(line, version);
         }

      });
      if (!version.isDone()) {
         throw new IOException();
      } else {
         try {
            return (String)version.get();
         } catch (ExecutionException | InterruptedException var5) {
            throw new RuntimeException(var5);
         }
      }
   }

   private static void readLine(String line, CompletableFuture<String> version) {
      if (line.contains("Latest build: ") && line.contains(".jar")) {
         String string = line.split("\\.jar")[0];
         version.complete(string.substring(string.lastIndexOf("/") + 1));
      }
   }
}
