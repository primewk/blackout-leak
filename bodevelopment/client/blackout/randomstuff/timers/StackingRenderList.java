package bodevelopment.client.blackout.randomstuff.timers;

public class StackingRenderList<T> extends RenderList<T> {
   protected StackingRenderList() {
   }

   public void add(T value, double time) {
      this.timers.add(new RenderList.Timer(value, time));
   }
}
