package bodevelopment.client.blackout.module.modules.misc;

import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.PlaySoundEvent;
import bodevelopment.client.blackout.mixin.accessors.AccessorAbstractSoundInstance;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.util.SoundUtils;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.class_2960;
import net.minecraft.class_3414;
import net.minecraft.class_3417;

public class SoundModifier extends Module {
   private final Map<class_2960[], SoundModifier.SoundSettingGroup> soundSettings = new HashMap();

   public SoundModifier() {
      super("Sound Modifier", "Modifies sounds.", SubCategory.MISC, true);
      this.put("Explosion", class_3417.field_15152);
      this.put("Hit", class_3417.field_14625, class_3417.field_15016, class_3417.field_14999, class_3417.field_14914, class_3417.field_14840, class_3417.field_14706);
      this.put("Damage", class_3417.field_15115, class_3417.field_15205, class_3417.field_27853, class_3417.field_14623, class_3417.field_17614);
      this.put("Totem", class_3417.field_14931);
   }

   private void put(String name, class_3414... events) {
      this.soundSettings.put(this.getIdentifiers(events), this.addSSGroup(name));
   }

   private class_2960[] getIdentifiers(class_3414[] events) {
      class_2960[] identifiers = new class_2960[events.length];

      for(int i = 0; i < events.length; ++i) {
         identifiers[i] = events[i].method_14833();
      }

      return identifiers;
   }

   private SoundModifier.SoundSettingGroup addSSGroup(String name) {
      SettingGroup group = this.addGroup(name);
      Setting<Boolean> cancel = group.b("Cancel " + name, false, "Doesn't play" + name + " sounds.");
      Setting<Double> volume = group.d(name + " Volume", 1.0D, 0.0D, 10.0D, 0.1D, "Volume of " + name + " sounds.", () -> {
         return !(Boolean)cancel.get();
      });
      Setting<Double> pitch = group.d(name + " Pitch", 1.0D, 0.0D, 10.0D, 0.1D, "Pitch of " + name + " sounds.", () -> {
         return !(Boolean)cancel.get();
      });
      Setting<SoundModifier.SoundMode> soundMode = group.e(name + " Mode", SoundModifier.SoundMode.Default, ".", () -> {
         return !(Boolean)cancel.get();
      });
      return new SoundModifier.SoundSettingGroup(cancel, volume, pitch, soundMode);
   }

   @Event
   public void onSound(PlaySoundEvent event) {
      SoundModifier.SoundSettingGroup group = this.getGroup(event.sound.method_4775());
      if (group != null) {
         if ((Boolean)group.cancel.get()) {
            event.setCancelled(true);
         } else {
            float volume = ((Double)group.volume.get()).floatValue();
            float pitch = ((Double)group.pitch.get()).floatValue();
            SoundModifier.SoundMode soundMode = (SoundModifier.SoundMode)group.soundMode.get();
            if (soundMode == SoundModifier.SoundMode.Default) {
               ((AccessorAbstractSoundInstance)event.sound).setVolume(volume);
               ((AccessorAbstractSoundInstance)event.sound).setPitch(pitch);
            } else {
               event.setCancelled(true);
               SoundUtils.play(pitch, volume, event.sound.method_4784(), event.sound.method_4779(), event.sound.method_4778(), event.sound.method_4787(), soundMode.name);
            }
         }
      }
   }

   private SoundModifier.SoundSettingGroup getGroup(class_2960 identifier) {
      Iterator var2 = this.soundSettings.entrySet().iterator();

      Entry entry;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         entry = (Entry)var2.next();
      } while(!this.contains((class_2960[])entry.getKey(), identifier));

      return (SoundModifier.SoundSettingGroup)entry.getValue();
   }

   private boolean contains(class_2960[] identifiers, class_2960 identifier) {
      class_2960[] var3 = identifiers;
      int var4 = identifiers.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         class_2960 id = var3[var5];
         if (id.equals(identifier)) {
            return true;
         }
      }

      return false;
   }

   private static record SoundSettingGroup(Setting<Boolean> cancel, Setting<Double> volume, Setting<Double> pitch, Setting<SoundModifier.SoundMode> soundMode) {
      private SoundSettingGroup(Setting<Boolean> cancel, Setting<Double> volume, Setting<Double> pitch, Setting<SoundModifier.SoundMode> soundMode) {
         this.cancel = cancel;
         this.volume = volume;
         this.pitch = pitch;
         this.soundMode = soundMode;
      }

      public Setting<Boolean> cancel() {
         return this.cancel;
      }

      public Setting<Double> volume() {
         return this.volume;
      }

      public Setting<Double> pitch() {
         return this.pitch;
      }

      public Setting<SoundModifier.SoundMode> soundMode() {
         return this.soundMode;
      }
   }

   public static enum SoundMode {
      Default,
      Power_Down,
      Disable,
      Enable,
      Explode,
      Hit,
      Hit2,
      Totem,
      Dig;

      private final String name = this.name().toLowerCase();

      // $FF: synthetic method
      private static SoundModifier.SoundMode[] $values() {
         return new SoundModifier.SoundMode[]{Default, Power_Down, Disable, Enable, Explode, Hit, Hit2, Totem, Dig};
      }
   }
}
