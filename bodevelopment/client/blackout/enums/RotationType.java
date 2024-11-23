package bodevelopment.client.blackout.enums;

import java.util.Objects;

public enum RotationType {
   Interact((RotationType)null, false),
   InstantInteract(Interact, true),
   BlockPlace((RotationType)null, false),
   InstantBlockPlace(BlockPlace, true),
   Attacking((RotationType)null, false),
   InstantAttacking(Attacking, true),
   Mining((RotationType)null, false),
   InstantMining(Mining, true),
   Use((RotationType)null, false),
   InstantUse(Use, true),
   Other((RotationType)null, false),
   InstantOther(Other, true);

   public final RotationType checkType;
   public final boolean instant;

   private RotationType(RotationType checkType, boolean instant) {
      this.checkType = (RotationType)Objects.requireNonNullElse(checkType, this);
      this.instant = instant;
   }

   public RotationType asInstant() {
      RotationType var10000;
      switch(this) {
      case Interact:
         var10000 = InstantInteract;
         break;
      case BlockPlace:
         var10000 = InstantBlockPlace;
         break;
      case Attacking:
         var10000 = InstantAttacking;
         break;
      case Mining:
         var10000 = InstantMining;
         break;
      case Use:
         var10000 = InstantUse;
         break;
      case Other:
         var10000 = InstantOther;
         break;
      default:
         var10000 = this;
      }

      return var10000;
   }

   public RotationType asNonInstant() {
      RotationType var10000;
      switch(this) {
      case InstantInteract:
         var10000 = Interact;
         break;
      case InstantBlockPlace:
         var10000 = BlockPlace;
         break;
      case InstantAttacking:
         var10000 = Attacking;
         break;
      case InstantMining:
         var10000 = Mining;
         break;
      case InstantUse:
         var10000 = Use;
         break;
      case InstantOther:
         var10000 = Other;
         break;
      default:
         var10000 = this;
      }

      return var10000;
   }

   public RotationType withInstant(boolean instant) {
      if (this.instant == instant) {
         return this;
      } else {
         return instant ? this.asInstant() : this.asNonInstant();
      }
   }

   // $FF: synthetic method
   private static RotationType[] $values() {
      return new RotationType[]{Interact, InstantInteract, BlockPlace, InstantBlockPlace, Attacking, InstantAttacking, Mining, InstantMining, Use, InstantUse, Other, InstantOther};
   }
}
