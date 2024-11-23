package bodevelopment.client.blackout.util;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.manager.Managers;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import javax.imageio.ImageIO;

public class FileUtils {
   public static File dir = null;

   public static void init() {
      (dir = new File(BlackOut.mc.field_1697, "blackout")).mkdir();
      addFolder("fonts");
      addFile("friends.json");
      Managers.FRIENDS.read();
   }

   public static boolean exists(String... path) {
      return getFile(path).exists();
   }

   public static File getFile(String... path) {
      File file = dir;
      String[] var2 = path;
      int var3 = path.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String string = var2[var4];
         file = new File(file, string);
      }

      return file;
   }

   public static void addFile(String... path) {
      addFile(getFile(path));
   }

   public static void addFile(File file) {
      if (!file.exists()) {
         try {
            file.createNewFile();
         } catch (IOException var2) {
            throw new RuntimeException(var2);
         }
      }

   }

   public static void addFolder(String... path) {
      File folder = getFile(path);
      if (!folder.exists()) {
         folder.mkdir();
      }

   }

   public static void write(File file, JsonObject object) {
      write(file, object.toString());
   }

   public static void write(File file, String content) {
      try {
         FileWriter writer = new FileWriter(file);
         writer.write(content);
         writer.close();
      } catch (IOException var3) {
         throw new RuntimeException(var3);
      }
   }

   public static JsonObject read(String... path) {
      return read(getFile(path));
   }

   public static JsonObject read(File file) {
      return (JsonObject)readElement(file);
   }

   public static JsonElement readElement(String... path) {
      return readElement(getFile(path));
   }

   public static JsonElement readElement(File file) {
      return (new JsonParser()).parse(readString(file));
   }

   public static String readString(File file) {
      try {
         return new String(Files.readAllBytes(file.toPath()));
      } catch (IOException var2) {
         throw new RuntimeException(var2);
      }
   }

   public static URL getResource(String... path) {
      return FileUtils.class.getResource(getPathToResource(path));
   }

   public static InputStream getResourceStream(String... path) {
      return FileUtils.class.getResourceAsStream(getPathToResource(path));
   }

   private static String getPathToResource(String... path) {
      StringBuilder builder = new StringBuilder();
      String separator = "/";
      if (!path[0].equals("assets")) {
         builder.append(separator).append("assets");
         builder.append(separator).append("blackout");
      }

      String[] var3 = path;
      int var4 = path.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String d = var3[var5];
         builder.append(separator).append(d);
      }

      return builder.toString();
   }

   public static BufferedImage readResourceImage(String... path) {
      try {
         return ImageIO.read(getResourceStream(path));
      } catch (IOException var2) {
         throw new RuntimeException(var2);
      }
   }
}
