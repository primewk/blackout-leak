package bodevelopment.client.blackout.event;

public class Cancellable {
   private boolean cancelled;

   public void cancel() {
      this.setCancelled(true);
   }

   public void setCancelled(boolean c) {
      this.cancelled = c;
   }

   public boolean isCancelled() {
      return this.cancelled;
   }
}
