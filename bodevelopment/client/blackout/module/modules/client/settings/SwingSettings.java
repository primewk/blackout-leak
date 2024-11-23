package bodevelopment.client.blackout.module.modules.client.settings;

import bodevelopment.client.blackout.enums.SwingState;
import bodevelopment.client.blackout.enums.SwingType;
import bodevelopment.client.blackout.module.SettingsModule;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import java.util.Objects;
import net.minecraft.class_1268;
import net.minecraft.class_2879;

public class SwingSettings extends SettingsModule {
   private static SwingSettings INSTANCE;
   private final SettingGroup sgInteract = this.addGroup("Interact");
   private final SettingGroup sgBlockPlace = this.addGroup("Block Place");
   private final SettingGroup sgMining = this.addGroup("Mining");
   private final SettingGroup sgAttack = this.addGroup("Attack");
   private final SettingGroup sgUse = this.addGroup("Use");
   public final Setting<Boolean> interact;
   public final Setting<SwingState> interactState;
   public final Setting<Boolean> blockPlace;
   public final Setting<SwingState> blockPlaceState;
   public final Setting<SwingSettings.MiningSwingState> mining;
   public final Setting<Boolean> attack;
   public final Setting<SwingState> attackState;
   public final Setting<Boolean> use;
   public final Setting<SwingState> useState;

   public SwingSettings() {
      super("Swing", false, true);
      this.interact = this.sgInteract.b("Interact Swing", true, "Swings your hand when you interact with a block.");
      SettingGroup var10001 = this.sgInteract;
      SwingState var10003 = SwingState.Post;
      Setting var10005 = this.interact;
      Objects.requireNonNull(var10005);
      this.interactState = var10001.e("Interact State", var10003, "Should we swing our hand before or after the action.", var10005::get);
      this.blockPlace = this.sgBlockPlace.b("Block Place Swing", true, "Swings your hand when placing a block.");
      var10001 = this.sgBlockPlace;
      var10003 = SwingState.Post;
      var10005 = this.blockPlace;
      Objects.requireNonNull(var10005);
      this.blockPlaceState = var10001.e("Block Place State", var10003, "Should we swing our hand before or after the action.", var10005::get);
      this.mining = this.sgMining.e("Block Place State", SwingSettings.MiningSwingState.Double, "Swings your hand when you place a crystal.");
      this.attack = this.sgAttack.b("Attack Swing", true, "Swings your hand when you attack any entity.");
      var10001 = this.sgAttack;
      var10003 = SwingState.Post;
      var10005 = this.attack;
      Objects.requireNonNull(var10005);
      this.attackState = var10001.e("Attack State", var10003, "Should we swing our hand before or after the action.", var10005::get);
      this.use = this.sgUse.b("Use Swing", false, "Swings your hand when using an item. NCP doesn't check this.");
      var10001 = this.sgUse;
      var10003 = SwingState.Post;
      var10005 = this.use;
      Objects.requireNonNull(var10005);
      this.useState = var10001.e("Use State", var10003, "Should we swing our hand before or after the action.", var10005::get);
      INSTANCE = this;
   }

   public static SwingSettings getInstance() {
      return INSTANCE;
   }

   public void swing(SwingState state, SwingType type, class_1268 hand) {
      if (state == this.getState(type)) {
         switch(type) {
         case Interact:
            this.swing((Boolean)this.interact.get(), hand);
            break;
         case Placing:
            this.swing((Boolean)this.blockPlace.get(), hand);
            break;
         case Attacking:
            this.swing((Boolean)this.attack.get(), hand);
            break;
         case Using:
            this.swing((Boolean)this.use.get(), hand);
         }

      }
   }

   public void mineSwing(SwingSettings.MiningSwingState state) {
      switch(state) {
      case Start:
         if (this.mining.get() != SwingSettings.MiningSwingState.Start) {
            return;
         }
         break;
      case End:
         if (this.mining.get() != SwingSettings.MiningSwingState.End) {
            return;
         }
         break;
      default:
         return;
      }

      this.swing(true, class_1268.field_5808);
   }

   private SwingState getState(SwingType type) {
      SwingState var10000;
      switch(type) {
      case Interact:
         var10000 = (SwingState)this.interactState.get();
         break;
      case Placing:
         var10000 = (SwingState)this.blockPlaceState.get();
         break;
      case Attacking:
         var10000 = (SwingState)this.attackState.get();
         break;
      case Using:
         var10000 = (SwingState)this.useState.get();
         break;
      case Mining:
         var10000 = SwingState.Post;
         break;
      default:
         throw new IncompatibleClassChangeError();
      }

      return var10000;
   }

   private void swing(boolean shouldSwing, class_1268 hand) {
      if (shouldSwing) {
         this.sendPacket(new class_2879(hand));
      }

   }

   public static enum MiningSwingState {
      Disabled,
      Start,
      End,
      Double;

      // $FF: synthetic method
      private static SwingSettings.MiningSwingState[] $values() {
         return new SwingSettings.MiningSwingState[]{Disabled, Start, End, Double};
      }
   }
}
