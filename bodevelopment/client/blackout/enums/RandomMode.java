package bodevelopment.client.blackout.enums;

import bodevelopment.client.blackout.interfaces.functional.SingleOut;

public enum RandomMode {
   Sin(() -> {
      return (Math.sin((double)System.currentTimeMillis() / 500.0D) + 1.0D) / 2.0D;
   }),
   Random(Math::random),
   Disabled(() -> {
      return 0.5D;
   });

   private final SingleOut<Double> random;

   private RandomMode(SingleOut<Double> random) {
      this.random = random;
   }

   public double get() {
      return (Double)this.random.get();
   }

   // $FF: synthetic method
   private static RandomMode[] $values() {
      return new RandomMode[]{Sin, Random, Disabled};
   }
}
