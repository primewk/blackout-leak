package bodevelopment.client.blackout.gui.clickgui.screens;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.gui.TextField;
import bodevelopment.client.blackout.gui.clickgui.ClickGuiScreen;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.util.GuiColorUtils;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import bodevelopment.client.blackout.util.SelectedComponent;
import bodevelopment.client.blackout.util.render.RenderUtils;
import java.awt.Color;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.class_124;

public class ConsoleScreen extends ClickGuiScreen {
   private final List<ConsoleScreen.Line> lines = new ArrayList();
   private final TextField textField = new TextField();
   private boolean typing = false;
   private final int id = SelectedComponent.nextId();
   private static final int LINE_COLOR = (new Color(50, 50, 50, 255)).getRGB();
   private static final float TEXT_SCALE = 2.0F;

   public ConsoleScreen() {
      super("Console", 900.0F, 600.0F, true);
      this.addLine("you don't need to use the prefix here (which is - for anyone that doesn't know it)", Color.GREEN.getRGB());
   }

   protected float getLength() {
      float rur = 0.0F;

      ConsoleScreen.Line line;
      for(Iterator var2 = this.lines.iterator(); var2.hasNext(); rur += line.getHeight()) {
         line = (ConsoleScreen.Line)var2.next();
      }

      return rur + 10.0F;
   }

   public void render() {
      this.stack.method_22903();
      this.stack.method_46416(0.0F, -this.scroll.get() + 10.0F, 0.0F);
      this.renderLines();
      this.stack.method_22909();
      this.renderBottom();
      this.textField.setActive(this.typing);
   }

   public void onMouse(int button, boolean state) {
      if (state) {
         if (button == 0) {
            if (!this.typing && this.mx >= -10.0D && this.mx <= (double)(this.width + 10.0F) && this.my >= (double)(this.height - 100.0F) && this.my <= (double)(this.height + 10.0F)) {
               this.typing = true;
               this.textField.clear();
            }

            if (this.textField.click(0, true) && this.typing) {
               SelectedComponent.setId(this.id);
            }

         }
      }
   }

   public void onKey(int key, boolean state) {
      if (this.typing) {
         if (state && key == 257) {
            this.handle(this.textField.getContent());
            this.textField.clear();
            this.typing = false;
            SelectedComponent.reset();
         } else {
            this.textField.type(key, state);
         }
      }
   }

   private void renderBottomBG() {
      RenderUtils.roundedBottom(this.stack, 0.0F, this.height - 100.0F, this.width, 60.0F, 10.0F, 0.0F, GuiColorUtils.bg2.getRGB(), 0);
      RenderUtils.topFade(this.stack, -10.0F, this.height - 120.0F, this.width + 20.0F, 20.0F, GuiColorUtils.bg2.getRGB());
      RenderUtils.line(this.stack, -10.0F, this.height - 100.0F, this.width + 10.0F, this.height - 100.0F, LINE_COLOR);
   }

   private void renderBottom() {
      this.textField.render(this.stack, 2.0F, this.mx, this.my, 20.0F, this.height - 65.0F, this.width - 40.0F, 0.0F, 20.0F, 15.0F, Color.WHITE, GuiColorUtils.bg2);
   }

   private void handle(String input) {
      String command = Managers.COMMANDS.onCommand(input.split(" "));
      if (command == null) {
         this.addLine("unrecognized command", Color.RED.getRGB());
      } else {
         this.addLine(command, Color.WHITE.getRGB());
      }

   }

   private void renderLines() {
      for(int i = this.lines.size() - 1; i >= 0; --i) {
         this.renderTexts((ConsoleScreen.Line)this.lines.get(i));
      }

   }

   private void renderTexts(ConsoleScreen.Line line) {
      float height = BlackOut.FONT.getHeight() * 2.0F;
      int color = line.color();
      String[] var4 = line.text();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         String string = var4[var6];
         color = this.renderLine(string, color);
         this.stack.method_22904(0.0D, (double)height * 1.5D, 0.0D);
      }

      this.stack.method_22904(0.0D, (double)height * 0.5D, 0.0D);
   }

   private int renderLine(String line, int color) {
      this.stack.method_22903();
      float x = 0.0F;
      int clr = color;
      String[] var5 = line.split("(?=§)");
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         String string = var5[var7];
         String s;
         if (string.charAt(0) == 167) {
            s = string.substring(2, string.length());
            class_124 formattingColor = class_124.method_544(string.charAt(1));
            if (formattingColor == null) {
               clr = color;
            } else {
               clr = formattingColor.method_532() | -16777216;
            }
         } else {
            s = string;
            clr = color;
         }

         BlackOut.FONT.text(this.stack, s, 2.0F, x, 0.0F, clr, false, false);
         x += BlackOut.FONT.getWidth(s) * 2.0F;
      }

      this.stack.method_22909();
      return clr;
   }

   private void addLine(String string, int color) {
      String time = this.currentTime();
      this.addLine(this.split(string, time), time, color);
   }

   private void addLine(String[] strings, String time, int color) {
      ConsoleScreen.Line line = new ConsoleScreen.Line(strings, time, color);
      if (this.scroll.get() > this.getLength() - 50.0F) {
         this.scroll.offset(line.getHeight());
      }

      this.lines.add(0, line);
      OLEPOSSUtils.limitList(this.lines, 10);
   }

   private String currentTime() {
      LocalDateTime time = LocalDateTime.now();
      return String.format("[%02d:%02d:%02d] ", time.getHour(), time.getMinute(), time.getSecond());
   }

   private String[] split(String string, String time) {
      double x = 0.0D;
      List<String> list = new ArrayList();
      StringBuilder lineBuilder = new StringBuilder();
      lineBuilder.append(time);
      x += (double)(BlackOut.FONT.getWidth(time) * 2.0F);
      String[] array = string.split("\n");
      int i = array.length;

      for(int var9 = 0; var9 < i; ++var9) {
         String line = array[var9];
         String[] var11 = line.split(" ");
         int var12 = var11.length;

         for(int var13 = 0; var13 < var12; ++var13) {
            String word = var11[var13];
            double wordWidth = (double)(BlackOut.FONT.getWidth(word) * 2.0F);
            if (!(x + wordWidth > (double)this.width)) {
               lineBuilder.append(word).append(" ");
               x += (double)(BlackOut.FONT.getWidth(word + " ") * 2.0F);
            } else {
               double widthLeft = (double)this.width - x - (double)(BlackOut.FONT.getWidth("-") * 2.0F);
               String[] letters = word.split("");
               StringBuilder wordBuilder = new StringBuilder();
               boolean changedLine = false;

               for(int i = 0; i < letters.length; ++i) {
                  String letter = letters[i];
                  widthLeft -= (double)(BlackOut.FONT.getWidth(letter) * 2.0F);
                  if (changedLine) {
                     String sus = i == letters.length - 1 ? letter + " " : letter;
                     lineBuilder.append(sus);
                     x += (double)(BlackOut.FONT.getWidth(sus) * 2.0F);
                  } else if (!letter.equals("-") && !letter.equals(".") && !letter.equals(",")) {
                     if (widthLeft < 0.0D) {
                        if (i <= 2 || i >= letters.length - 3) {
                           list.add(lineBuilder.toString());
                           lineBuilder.delete(0, lineBuilder.length() + 1);
                           lineBuilder.append(word).append(" ");
                           x = (double)(BlackOut.FONT.getWidth(word + " ") * 2.0F);
                           break;
                        }

                        lineBuilder.append(wordBuilder).append("-");
                        list.add(lineBuilder.toString());
                        lineBuilder.delete(0, lineBuilder.length() + 1);
                        changedLine = true;
                        x = (double)(BlackOut.FONT.getWidth(letter) * 2.0F);
                        lineBuilder.append(letter);
                     } else {
                        wordBuilder.append(letter);
                     }
                  } else {
                     lineBuilder.append(wordBuilder);
                     lineBuilder.append(letter);
                     list.add(lineBuilder.toString());
                     lineBuilder.delete(0, lineBuilder.length() + 1);
                     changedLine = true;
                     x = (double)(BlackOut.FONT.getWidth(letter) * 2.0F);
                  }
               }
            }
         }
      }

      lineBuilder.deleteCharAt(lineBuilder.length() - 1);
      list.add(lineBuilder.toString());
      array = new String[list.size()];

      for(i = 0; i < list.size(); ++i) {
         array[i] = (String)list.get(i);
      }

      return array;
   }

   private StringBuilder removeLast(StringBuilder stringBuilder, int rur) {
      StringBuilder string = new StringBuilder();

      for(int i = 0; i < rur; ++i) {
         int index = stringBuilder.length() - 1;
         String character = String.valueOf(stringBuilder.charAt(index));
         stringBuilder.deleteCharAt(index);
         string.insert(0, character);
      }

      return string;
   }

   private static record Line(String[] text, String time, int color) {
      private Line(String[] text, String time, int color) {
         this.text = text;
         this.time = time;
         this.color = color;
      }

      private float getHeight() {
         return ((float)this.text().length * BlackOut.FONT.getHeight() * 1.5F + BlackOut.FONT.getHeight() * 0.5F) * 2.0F;
      }

      public String[] text() {
         return this.text;
      }

      public String time() {
         return this.time;
      }

      public int color() {
         return this.color;
      }
   }
}
