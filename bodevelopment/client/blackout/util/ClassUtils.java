package bodevelopment.client.blackout.util;

import bodevelopment.client.blackout.BlackOut;
import com.google.common.reflect.ClassPath;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

public class ClassUtils {
   private static Constructor<?> constructor = null;

   public static void init() {
      Constructor[] var0 = BlackOut.class.getDeclaredConstructors();
      int var1 = var0.length;

      for(int var2 = 0; var2 < var1; ++var2) {
         Constructor<?> ctr = var0[var2];
         constructor = ctr;
         if (ctr.getGenericParameterTypes().length == 0) {
            break;
         }
      }

      constructor.setAccessible(true);
   }

   public static void forEachClass(Consumer<? super Class<?>> consumer, String packageName) {
      try {
         ClassLoader cl = BlackOut.class.getClassLoader();
         ClassPath.from(cl).getTopLevelClassesRecursive(packageName).forEach((info) -> {
            try {
               consumer.accept(Class.forName(info.getName()));
            } catch (ClassNotFoundException var3) {
               throw new RuntimeException(var3);
            }
         });
      } catch (Exception var3) {
         throw new RuntimeException(var3);
      }
   }

   public static <T> T instance(Class<T> clazz) {
      try {
         Constructor<T> constructor = clazz.getDeclaredConstructor();
         constructor.setAccessible(true);
         return constructor.newInstance();
      } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException var2) {
         throw new RuntimeException(var2);
      }
   }
}
