package bodevelopment.client.blackout.event.events;

import net.minecraft.class_332;
import net.minecraft.class_4587;

public class RenderEvent {
   public double frameTime = 0.0D;
   public float tickDelta = 0.0F;
   private long prevEvent = 0L;

   protected void setFrameTime() {
      if (this.prevEvent > 0L) {
         this.frameTime = (double)(System.currentTimeMillis() - this.prevEvent) / 1000.0D;
      }

      this.prevEvent = System.currentTimeMillis();
   }

   public static class Hud extends RenderEvent {
      public class_332 context;

      public static class Post extends RenderEvent.Hud {
         private static final RenderEvent.Hud.Post INSTANCE = new RenderEvent.Hud.Post();

         public static RenderEvent.Hud.Post get(class_332 context, float tickDelta) {
            INSTANCE.context = context;
            INSTANCE.tickDelta = tickDelta;
            INSTANCE.setFrameTime();
            return INSTANCE;
         }
      }

      public static class Pre extends RenderEvent.Hud {
         private static final RenderEvent.Hud.Pre INSTANCE = new RenderEvent.Hud.Pre();

         public static RenderEvent.Hud.Pre get(class_332 context, float tickDelta) {
            INSTANCE.context = context;
            INSTANCE.tickDelta = tickDelta;
            INSTANCE.setFrameTime();
            return INSTANCE;
         }
      }
   }

   public static class World extends RenderEvent {
      public class_4587 stack = null;

      public static class Post extends RenderEvent.World {
         private static final RenderEvent.World.Post INSTANCE = new RenderEvent.World.Post();

         public static RenderEvent.World.Post get(class_4587 stack, float tickDelta) {
            INSTANCE.stack = stack;
            INSTANCE.tickDelta = tickDelta;
            INSTANCE.setFrameTime();
            return INSTANCE;
         }
      }

      public static class Pre extends RenderEvent.World {
         private static final RenderEvent.World.Pre INSTANCE = new RenderEvent.World.Pre();

         public static RenderEvent.World.Pre get(class_4587 stack, float tickDelta) {
            INSTANCE.stack = stack;
            INSTANCE.tickDelta = tickDelta;
            INSTANCE.setFrameTime();
            return INSTANCE;
         }
      }
   }
}
