package bodevelopment.client.blackout.util;

import bodevelopment.client.blackout.BlackOut;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import net.minecraft.class_1113;
import net.minecraft.class_1140;
import net.minecraft.class_156;
import net.minecraft.class_243;
import net.minecraft.class_4228;
import net.minecraft.class_4225.class_4105;
import net.minecraft.class_4235.class_4236;

public class SoundUtils {
   public static void play(float pitch, float volume, String name) {
      play(pitch, volume, 0.0D, 0.0D, 0.0D, false, name);
   }

   public static void play(class_1113 instance, String name) {
      play(instance.method_4782(), instance.method_4781(), instance.method_4784(), instance.method_4779(), instance.method_4778(), instance.method_4787(), name);
   }

   public static void play(float pitch, float volume, double x, double y, double z, boolean relative, String name) {
      InputStream inputStream = FileUtils.getResourceStream("sounds", name + ".ogg");
      class_1140 engine = BlackOut.mc.method_1483().field_5590;
      class_4236 sourceManager = createSourceManager(engine, 5);
      if (sourceManager != null) {
         class_243 vec = new class_243(x, y, z);
         sourceManager.method_19735((source) -> {
            source.method_19639(pitch);
            source.method_19647(volume);
            source.method_19657();
            source.method_19645(false);
            source.method_19641(vec);
            source.method_19649(relative);
         });
         CompletableFuture.supplyAsync(() -> {
            try {
               return new class_4228(inputStream);
            } catch (IOException var2) {
               throw new CompletionException(var2);
            }
         }, class_156.method_18349()).thenAccept((stream) -> {
            sourceManager.method_19735((source) -> {
               source.method_19643(stream);
               source.method_19650();
            });
         });
      }
   }

   private static class_4236 createSourceManager(class_1140 engine, int i) {
      class_4236 sourceManager = (class_4236)engine.field_18949.method_19723(class_4105.field_18353).join();
      class_4236 var10000;
      if (sourceManager == null && i > 0) {
         --i;
         var10000 = createSourceManager(engine, i);
      } else {
         var10000 = sourceManager;
      }

      return var10000;
   }
}
