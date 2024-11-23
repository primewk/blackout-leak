package bodevelopment.client.blackout.util;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.rendering.texture.BOTextures;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import javax.imageio.ImageIO;
import net.minecraft.class_1044;
import net.minecraft.class_2960;
import net.minecraft.class_3300;
import net.minecraft.class_3545;
import net.minecraft.class_742;

public class Capes {
   private static final Map<String, class_2960> capes = new HashMap();
   private static final List<class_2960> loaded = new ArrayList();
   private static final List<class_3545<String, class_2960>> toLoad = new ArrayList();
   private static boolean loading = false;

   public static class_2960 getCape(class_742 player) {
      String UUID = player.method_5845();
      if (!capes.containsKey(UUID)) {
         return null;
      } else {
         class_2960 identifier = (class_2960)capes.get(UUID);
         if (!loaded.contains(identifier)) {
            if (!loading) {
               startLoad();
            }

            return null;
         } else {
            return (class_2960)capes.get(UUID);
         }
      }
   }

   public static void requestCapes() {
      CompletableFuture.runAsync(() -> {
         try {
            Map<String, class_2960> identifiers = new HashMap();
            InputStream stream = (new URL("https://raw.githubusercontent.com/KassuK1/BlackoutCapes/main/capes")).openStream();
            BufferedReader read = new BufferedReader(new InputStreamReader(stream));
            read.lines().forEach((line) -> {
               readLine(line, identifiers);
            });
            read.close();
         } catch (IOException var3) {
         }

      });
   }

   private static void loadCape(String name, class_2960 identifier) {
      CompletableFuture.runAsync(() -> {
         loading = true;
         new Capes.CapeTexture(name, identifier);
         loading = false;
      });
   }

   private static void startLoad() {
      if (!toLoad.isEmpty()) {
         class_3545<String, class_2960> pair = (class_3545)toLoad.get(0);
         toLoad.remove(0);
         String name = (String)pair.method_15442();
         class_2960 identifier = (class_2960)pair.method_15441();
         loadCape(name, identifier);
      }
   }

   private static void readLine(String line, Map<String, class_2960> identifiers) {
      String[] string = line.replace(" ", "").split(":");
      if (string.length >= 3) {
         String UUID = string[1];
         String capeName = string[2];
         capes.put(UUID, (class_2960)identifiers.computeIfAbsent(capeName, (name) -> {
            class_2960 identifier = new class_2960("blackout", "textures/capes/" + name + ".png");
            toLoad.add(new class_3545(name, identifier));
            return identifier;
         }));
      }
   }

   private static class CapeTexture extends class_1044 {
      public CapeTexture(String name, class_2960 identifier) {
         try {
            BufferedImage image = ImageIO.read(new URL("https://raw.githubusercontent.com/KassuK1/BlackoutCapes/main/textures/" + name + ".png"));
            if (!RenderSystem.isOnRenderThread()) {
               RenderSystem.recordRenderCall(() -> {
                  try {
                     this.setId(name, identifier, image);
                  } catch (IOException var5) {
                  }

               });
            } else {
               this.setId(name, identifier, image);
            }
         } catch (IOException var4) {
         }

      }

      private void setId(String name, class_2960 identifier, BufferedImage image) throws IOException {
         this.field_5204 = BOTextures.upload(image).id();
         BlackOut.mc.method_1531().method_4616(identifier, this);
         Capes.loaded.add(identifier);
         System.out.println("Loaded cape: " + name);
      }

      public void method_4625(class_3300 manager) {
      }
   }
}
