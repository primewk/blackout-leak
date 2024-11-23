package bodevelopment.client.blackout.randomstuff;

public record Rotation(float yaw, float pitch) {
   public Rotation(float yaw, float pitch) {
      this.yaw = yaw;
      this.pitch = pitch;
   }

   public float yaw() {
      return this.yaw;
   }

   public float pitch() {
      return this.pitch;
   }
}
