package bodevelopment.client.blackout.enums;

public enum RenderShape {
   Outlines(true, false),
   Sides(false, true),
   Full(true, true);

   public final boolean outlines;
   public final boolean sides;

   private RenderShape(boolean outlines, boolean sides) {
      this.outlines = outlines;
      this.sides = sides;
   }

   // $FF: synthetic method
   private static RenderShape[] $values() {
      return new RenderShape[]{Outlines, Sides, Full};
   }
}
