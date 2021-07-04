/*
TODO: Remove trash-code from both ClickGUI's.
 */
package space.heliodor.ui;

import com.sun.xml.internal.ws.api.config.management.policy.ManagementAssertion;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import space.heliodor.Heliodor;
import space.heliodor.module.Module;
import space.heliodor.module.impl.render.HUD;
import space.heliodor.settings.Option;
import space.heliodor.settings.OptionBool;
import space.heliodor.settings.OptionMode;
import space.heliodor.settings.OptionNumber;
import space.heliodor.utils.Render2DUtil;
import space.heliodor.utils.Slider;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class MoonDropdown extends GuiScreen {
    private static CopyOnWriteArrayList<Category> categories = new CopyOnWriteArrayList<>();
    int mouseX;
    int mouseY;
    public MoonDropdown() {
        double offset = 0;
        for(space.heliodor.module.Category category : space.heliodor.module.Category.values()) {
            Color colorCategory = null;
            Category category1 = new Category(120 + offset, 20, colorCategory, category);
            category1.hide = true;
            categories.add(category1);
            offset += 125;
        }
    }
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        for(Category category : categories) {
            category.drawCategory(mouseX, mouseY);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        for (Category category : categories) {
            category.mouse(mouseButton, mouseX, mouseY);
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        for(Category category : categories) {
            category.released();
        }
    }

    public class Category {
        // x axis positions
        private float xPos;
        private double finalX;
        private double xDrag;
        private double width;

        // y axis positions
        private float yPos;
        private double finalY;
        private double yDrag;
        private double height;

        //slider
        private boolean sliderDragged;
        //category
        private boolean isDragging;
        private space.heliodor.module.Category category;
        private Color color;
        private boolean hide;

        public Category(double width, double height, Color color, space.heliodor.module.Category category) {
            this.width = width;
            this.height = height;
            this.color = color;
            this.category = category;
        }

        public void drawCategory(int mouseX, int mouseY) {
            float count = 0;
            float count1 = 0;
            if(this.isDragging) {
                xPos = (float) (mouseX - xDrag);
                yPos = (float) (mouseY - yDrag);
            }
            finalX = xPos;
            finalY = yPos;
            CopyOnWriteArrayList<Module> modules = new CopyOnWriteArrayList<Module>(Heliodor.INSTANCE().moduleMgr.modules);
            modules.sort(Comparator.comparingDouble(m -> (double) Heliodor.INSTANCE().clickGui1.getWidth(((Module)m).name)).reversed());
            for(Module module : modules) {
                if (module.category == category) {
                    if(module.extended) {
                        double countset = 0;
                        for(Option option : module.options) {
                            countset += 15f;
                        }
                        count += 20f + countset;
                    }
                    else {
                        count += 20f;
                    }
                }
            }
            if(!this.hide) {
                Gui.drawRect((float) (finalX + width - 50.5f), (float) (finalY + height - 24.5), (float) (finalX + width + 50.5f), (float) (finalY + height + count - 0.5f), new Color(0, 0, 0,255).getRGB());
                Gui.drawRect((float) (finalX + width - 50), (float) (finalY + height), (float) (finalX + width + 50), (float) (finalY + height + count - 1f), new Color(45, 45, 45,255).getRGB());
                for(Module module : modules) {
                    if(module.category == category) {
                        if(module.isToggled()) {
                            Gui.drawRect(0,0,0,0,0);
                            Gui.drawRect((float) (finalX + width - 50), (float) (finalY + 25 - 6 + count1), (float) (finalX + width + 50), (float) (finalY + 25 + 14 + count1), new Color(252, 77, 77,255).getRGB());
                        }
                        if(!module.options.isEmpty()) {
                            if(!module.extended) {
                                Heliodor.INSTANCE().icons.drawString("f", (float) (finalX + width + 40), (float) (finalY + 26.5f + count1), -1);
                            }
                            else {
                                Heliodor.INSTANCE().icons.drawString("g", (float)(finalX + width + 40), (float)(finalY + 26.5f + count1), -1);
                            }
                        }
                        Gui.drawRect(0,0,0,0,0);
                        Heliodor.INSTANCE().clickGui1.drawString(module.name, (float)(finalX + width - 45), (float) (finalY + 25 + count1), -1);
                        module.x = (float) (finalX + width);
                        module.y = (float) (finalY + 25 + count1);
                        if(!module.extended) {
                            count1 += 20f;
                        }
                        else {
                            double countset = 0;
                            for(Option option : module.options) {
                                countset += 15;
                            }
                            count1 += 20f + countset;
                        }
                    }
                    if(module.category == category && module.extended && module.options.size() > 0) {
                        double countset = 18f;
                        for(Option option : module.options) {
                            Gui.drawRect(0,0,0,0,0);
                            Gui.drawRect(module.x - 50, (float) (module.y + countset - 4), module.x - 40 + 90, (float) (module.y + countset + 11), new Color(33, 33, 33,255).getRGB());
                            if (option instanceof OptionNumber) {
                                option.x = module.x - 45;
                                option.y = (float) (module.y + countset + 12);
                                OptionNumber optionNumber = (OptionNumber) option;
                                DecimalFormat decimalFormat = new DecimalFormat("#.#");
                                String value = decimalFormat.format(optionNumber.value);
                                Heliodor.INSTANCE().clickGui1.drawStringWithShadow(optionNumber.name.replaceAll(module.name + " ", "") + ": " + value, module.x - 45, (float) (module.y + countset - 2), -1);
                                option.slider = new Slider((int)optionNumber.getMinimum(), (int)optionNumber.getMaximum(), (int)module.x - 45, (int) (module.y + countset + 12 - 3), 75, optionNumber);
                                option.slider.draw(mouseX, mouseY);
                            }
                            if (option instanceof OptionMode) {
                                option.x = module.x - 45;
                                option.y = (float) (module.y + countset);
                                OptionMode optionMode = (OptionMode) option;
                                Heliodor.INSTANCE().clickGui1.drawStringWithShadow(optionMode.name.replaceAll(module.name + " ", "") + ": " + optionMode.name(),module.x - 45, (float) (module.y + countset), -1);
                            }
                            if (option instanceof OptionBool) {
                                option.x = module.x - 45;
                                option.y = (float) (module.y + countset);
                                OptionBool modeSetting = (OptionBool) option;
                                Heliodor.INSTANCE().clickGui1.drawStringWithShadow(modeSetting.name.replaceAll(module.name + " ", ""), module.x - 45, (float) (module.y + countset), -1);
                                Render2DUtil.drawRoundedRect(finalX + width + 35, module.y + countset, 10, 10, 10, new Color(252, 77, 77,255).getRGB());
                                if(((OptionBool) option).isEnabled()) Heliodor.INSTANCE().icons.drawStringWithShadow("i", (float)(finalX + width + 36.5f), (float) (module.y + countset + 1.25f), -1);
                            }
                            countset += 15;
                        }
                    }
                }
            }
            else {
                Gui.drawRect((float) (finalX + width - 50.5f), (float) (finalY + height - 24.5), (float) (finalX + width + 50.5f), (float) (finalY + height + 0.5f), new Color(0, 0, 0,255).getRGB());
            }
            Gui.drawRect((float) (finalX + width - 50), (float) (finalY + height), (float) (finalX + width + 50), (float) (finalY + height - 24), new Color(24, 24, 24,255).getRGB());
            Heliodor.INSTANCE().clickGui2.drawString(category.name().toUpperCase().charAt(0) + category.name().replaceFirst(String.valueOf(category.name().charAt(0)), "").toLowerCase(Locale.ROOT), (float) ((float) finalX + width + 50 - Heliodor.INSTANCE().clickGui1.getWidth(category.name())), (float) (finalY + height - 17), -1);
            switch (category.name().toUpperCase()) {
                case "COMBAT": {
                    Heliodor.INSTANCE().icons.drawCenteredString("h", (float) ((float) finalX + width - 40), (float) ((float) finalY + height - 16), -1);
                    break;
                }
                case "MOVEMENT": {
                    Heliodor.INSTANCE().icons.drawCenteredString("x", (float) ((float) finalX + width - 40), (float) ((float) finalY + height - 16), -1);
                    break;
                }
                case "RENDER": {
                    Heliodor.INSTANCE().icons.drawCenteredString("b", (float) ((float) finalX + width - 40), (float) ((float) finalY + height - 16), -1);
                    break;
                }
                case "PLAYER": {
                    Heliodor.INSTANCE().icons.drawCenteredString("m", (float) ((float) finalX + width - 40), (float) ((float) finalY + height - 16), -1);
                    break;
                }
            }
            Gui.drawRect(0,0,0,0,0);
        }
        public void mouse(int button, int mouseX, int mouseY) {
            boolean isHolding = mouseX >= finalX + width - 50 && mouseX < finalX +width + 50 && mouseY >= finalY + height - 20 && mouseY < finalY + height - 2;
            if(isHolding && button == 0){
                this.isDragging = true;
                xDrag = mouseX - finalX;
                yDrag = mouseY - finalY;
            }
            else if(isHolding && button == 1) {
                hide = !hide;
            }
            if(!hide) {
                for(Module module : Heliodor.INSTANCE().moduleMgr.getModulesByCategory(category)) {
                    if(mouseX > module.get(true, false) - 49 && mouseX < module.get(true, false) + 49) {
                        if(mouseY > module.get(false, true) - 6 && mouseY < module.get(false, true) + 6) {
                            if(button == 0) {
                                module.toggle();
                            }
                            if(button == 1) {
                                module.extended = !module.extended;
                            }
                        }
                    }
                    if(module.extended) {
                        for(Option currento : module.options) {
                            if (mouseX > currento.x && mouseX < currento.x + 100) {
                                if (mouseY > currento.y - 5  & mouseY < currento.y + 5) {
                                    if (currento instanceof OptionBool) {
                                        OptionBool modeSetting = (OptionBool) currento;
                                        modeSetting.toggle();
                                    }
                                    if (currento instanceof OptionMode && button == 0) {
                                        OptionMode optionMode = (OptionMode) currento;
                                        optionMode.changeSetting();
                                    }
                                    if (currento instanceof OptionNumber) {
                                        OptionNumber modeSetting = (OptionNumber) currento;
                                        sliderDragged = true;
                                        currento.slider.mouseClicked(mouseX,mouseY, button);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        public void released() {
            isDragging = false;
            sliderDragged = false;

            for (Module module : Heliodor.INSTANCE().moduleMgr.getModulesByCategory(category)) {
                for(Option currento : module.options) {
                    if (mouseX > currento.x && mouseX < currento.x + 100) {
                        if (mouseY > currento.y & mouseY < currento.y +  Heliodor.INSTANCE().font1.getHeight(currento.name + ": " + "")) {
                            if(currento.slider != null && currento instanceof OptionNumber) {
                                currento.slider.mouseReleased();
                            }
                        }
                    }
                }
            }
        }
    }
}

