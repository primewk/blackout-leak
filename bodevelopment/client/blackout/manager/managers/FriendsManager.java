package bodevelopment.client.blackout.manager.managers;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.manager.Manager;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.util.FileUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.minecraft.class_1657;
import net.minecraft.class_640;

public class FriendsManager extends Manager {
   private final List<FriendsManager.Friend> friends = new ArrayList();
   private boolean shouldSave = false;
   private long prevSave = 0L;

   public void init() {
      BlackOut.EVENT_BUS.subscribe(this, () -> {
         return BlackOut.mc.field_1724 == null || BlackOut.mc.field_1687 == null;
      });
   }

   @Event
   public void onTick(TickEvent.Pre event) {
      if (this.shouldSave && System.currentTimeMillis() - this.prevSave > 10000L) {
         this.writeFriends();
      }

      Collection<class_640> entries = BlackOut.mc.method_1562().method_45732();
      if (entries != null) {
         entries.forEach((entry) -> {
            GameProfile profile = entry.method_2966();
            String name = profile.getName();
            UUID uuid = profile.getId();
            FriendsManager.Friend friend = this.getAsFriend(name, uuid);
            if (friend != null) {
               boolean nameEquals = friend.getName().equalsIgnoreCase(name);
               if (!friend.seen() && nameEquals) {
                  friend.setUuid(uuid);
               }

               if (uuid.equals(friend.getUuid()) && !nameEquals) {
                  friend.setName(name);
               }

            }
         });
      }
   }

   public String add(String name, UUID uuid) {
      File file = FileUtils.getFile("friends.json");
      FileUtils.addFile(file);
      FriendsManager.Friend friend = new FriendsManager.Friend(name, uuid);
      this.friends.add(friend);
      this.save();
      return friend.seen() ? String.format("added %s to friends list", name) : String.format("added %s to friends list, couldnt get UUID yet but it will be updated when you see them in game", name);
   }

   public String remove(String name) {
      Iterator var2 = this.friends.iterator();

      FriendsManager.Friend friend;
      do {
         if (!var2.hasNext()) {
            return String.format("%s was not in friends list", name);
         }

         friend = (FriendsManager.Friend)var2.next();
      } while(!friend.getName().equalsIgnoreCase(name));

      this.friends.remove(friend);
      this.save();
      return String.format("removed %s from friends list", friend.getName());
   }

   private void writeFriends() {
      File file = FileUtils.getFile("friends.json");
      FileUtils.addFile(file);
      JsonObject jsonObject = new JsonObject();
      this.friends.forEach((friend) -> {
         jsonObject.addProperty(friend.getName(), friend.seen() ? friend.getUuid().toString() : "<NULL>");
      });
      this.prevSave = System.currentTimeMillis();
      FileUtils.write(file, jsonObject);
   }

   private FriendsManager.Friend getAsFriend(String name, UUID uuid) {
      Iterator var3 = this.friends.iterator();

      while(var3.hasNext()) {
         FriendsManager.Friend friend = (FriendsManager.Friend)var3.next();
         if (friend.seen()) {
            if (uuid.equals(friend.getUuid())) {
               return friend;
            }
         } else if (name.equalsIgnoreCase(friend.getName())) {
            return friend;
         }
      }

      return null;
   }

   public void read() {
      this.getFriends().clear();
      File file = FileUtils.getFile("friends.json");
      if (file.exists()) {
         JsonElement jsonElement = FileUtils.readElement(file);
         if (jsonElement instanceof JsonObject) {
            JsonObject jsonObject = (JsonObject)jsonElement;
            jsonObject.entrySet().forEach((entry) -> {
               String uuidString = ((JsonElement)entry.getValue()).getAsString();
               this.friends.add(new FriendsManager.Friend((String)entry.getKey(), uuidString.equals("<NULL>") ? null : UUID.fromString(uuidString)));
            });
         }
      }
   }

   public void save() {
      this.shouldSave = true;
   }

   public boolean isFriend(class_1657 player) {
      Iterator var2 = this.friends.iterator();

      FriendsManager.Friend friend;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         friend = (FriendsManager.Friend)var2.next();
      } while(!friend.getName().equalsIgnoreCase(player.method_7334().getName()));

      return true;
   }

   public List<FriendsManager.Friend> getFriends() {
      return this.friends;
   }

   public static class Friend {
      private String name;
      private UUID uuid;

      private Friend(String name, UUID uuid) {
         this.name = name;
         this.uuid = uuid;
         Managers.FRIENDS.save();
      }

      public String getName() {
         return this.name;
      }

      public UUID getUuid() {
         return this.uuid;
      }

      public void setName(String name) {
         this.name = name;
      }

      public void setUuid(UUID uuid) {
         this.uuid = uuid;
      }

      public boolean seen() {
         return this.uuid != null;
      }
   }
}
