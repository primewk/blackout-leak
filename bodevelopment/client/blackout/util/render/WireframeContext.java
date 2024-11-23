package bodevelopment.client.blackout.util.render;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.class_243;

public class WireframeContext {
   public final List<class_243[]> lines = new ArrayList();
   public final List<class_243[]> quads = new ArrayList();

   public static WireframeContext of(List<class_243[]> positions) {
      WireframeContext context = new WireframeContext();
      context.quads.addAll(positions);
      Iterator var2 = positions.iterator();

      while(var2.hasNext()) {
         class_243[] arr = (class_243[])var2.next();

         for(int i = 0; i < 4; ++i) {
            class_243[] line = new class_243[]{arr[i], arr[i + 1]};
            if (!contains(context.lines, line)) {
               context.lines.add(line);
            }
         }
      }

      return context;
   }

   private static boolean contains(List<class_243[]> list, class_243[] line) {
      Iterator var2 = list.iterator();

      class_243[] arr;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         arr = (class_243[])var2.next();
         if (arr[0].equals(line[0]) && arr[1].equals(line[1])) {
            return true;
         }
      } while(!arr[0].equals(line[1]) || !arr[1].equals(line[0]));

      return true;
   }
}
