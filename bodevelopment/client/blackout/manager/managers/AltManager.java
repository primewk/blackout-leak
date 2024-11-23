package bodevelopment.client.blackout.manager.managers;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.gui.menu.Account;
import bodevelopment.client.blackout.manager.Manager;
import bodevelopment.client.blackout.util.FileUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.class_320.class_321;

public class AltManager extends Manager {
   private final List<Account> accounts = new ArrayList();
   private long lastSave = 0L;
   private boolean shouldSave = false;
   public Account selected;
   public Account currentSession;

   public void init() {
      this.selected = new Account(BlackOut.mc.method_1548());
      this.currentSession = this.selected;
      BlackOut.EVENT_BUS.subscribe(this, () -> {
         return false;
      });
      String path = "accounts.json";
      if (FileUtils.exists(path)) {
         JsonElement jsonElement = FileUtils.readElement(FileUtils.getFile(path));
         JsonObject jsonObject = jsonElement instanceof JsonNull ? new JsonObject() : (JsonObject)jsonElement;
         jsonObject.entrySet().forEach((entry) -> {
            JsonElement element = (JsonElement)entry.getValue();
            if (element instanceof JsonObject) {
               JsonObject object = (JsonObject)element;
               this.readData(object);
            } else {
               this.getAccounts().add(new Account((String)entry.getKey(), (String)null, (UUID)null, "", Optional.empty(), Optional.empty(), class_321.field_1988));
            }
         });
      } else {
         FileUtils.addFile(path);
      }

      this.save();
   }

   private void readData(JsonObject jsonObject) {
      this.add(new Account(jsonObject));
   }

   @Event
   public void onTick(TickEvent.Pre event) {
      if (!BlackOut.mc.method_1548().method_1676().equals(this.selected.getName())) {
         this.selected.setSession();
      }

      if (this.shouldSave && System.currentTimeMillis() - this.lastSave > 5000L) {
         this.shouldSave = false;
         JsonObject object = new JsonObject();
         this.getAccounts().forEach((account) -> {
            JsonObject accountObject = account.asJson();
            if (accountObject != null) {
               object.add(account.getScript(), accountObject);
            }

         });
         FileUtils.write(FileUtils.getFile("accounts.json"), object);
         this.lastSave = System.currentTimeMillis();
      }

   }

   public void add(Account account) {
      this.getAccounts().add(account);
      this.save();
   }

   public void remove(Account account) {
      this.getAccounts().remove(account);
      this.save();
   }

   public void set(Account account) {
      this.selected = account;
      this.save();
   }

   public void save() {
      this.shouldSave = true;
   }

   public List<Account> getAccounts() {
      return this.accounts;
   }
}
