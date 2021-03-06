package mekanism.client.jei;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import mekanism.client.gui.GuiMekanism;
import mekanism.client.gui.element.GuiRelativeElement;
import mekanism.client.gui.element.GuiWindow;
import mekanism.client.jei.interfaces.IJEIIngredientHelper;
import mekanism.client.jei.interfaces.IJEIRecipeArea;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.Rect2i;
import net.minecraft.util.Identifier;

public class GuiElementHandler<GUI extends GuiMekanism<?>> implements IGuiContainerHandler<GUI> {

    private static void addAreaIfOutside(List<Rect2i> areas, int parentX, int parentY, int parentWidth, int parentHeight, AbstractButtonWidget element) {
        if (element.visible) {
            int x = element.x;
            int y = element.y;
            int width = element.getWidth();
            int height = element.getHeight();
            if (x < parentX || y < parentY || x + width > parentX + parentWidth || y + height > parentY + parentHeight) {
                //If the element sticks out at all add it
                areas.add(new Rect2i(x, y, width, height));
            }
        }
    }

    public static List<Rect2i> getAreasFor(int parentX, int parentY, int parentWidth, int parentHeight, List<? extends Element> children) {
        List<Rect2i> areas = new ArrayList<>();
        for (Element child : children) {
            if (child instanceof AbstractButtonWidget) {
                addAreaIfOutside(areas, parentX, parentY, parentWidth, parentHeight, (AbstractButtonWidget) child);
            }
        }
        return areas;
    }

    @Override
    public List<Rect2i> getGuiExtraAreas(GUI gui) {
        int parentX = gui.getLeft();
        int parentY = gui.getTop();
        int parentWidth = gui.getWidth();
        int parentHeight = gui.getHeight();
        List<Rect2i> extraAreas = getAreasFor(parentX, parentY, parentWidth, parentHeight, gui.children());
        for (GuiWindow window : gui.getWindows()) {
            //Add the window itself and any areas that poke out from the main gui
            addAreaIfOutside(extraAreas, parentX, parentY, parentWidth, parentHeight, window);
            extraAreas.addAll(getAreasFor(parentX, parentY, parentWidth, parentHeight, window.children()));
        }
        return extraAreas;
    }

    @Nullable
    @Override
    public Object getIngredientUnderMouse(GUI gui, double mouseX, double mouseY) {
        GuiWindow guiWindow = gui.getWindowHovering(mouseX, mouseY);
        if (guiWindow == null) {
            //If no window is being hovered, then check the elements in general
            return getIngredientUnderMouse(gui.children(), mouseX, mouseY);
        }
        //Otherwise check the elements of the window
        return getIngredientUnderMouse(guiWindow.children(), mouseX, mouseY);
    }

    @Nullable
    private Object getIngredientUnderMouse(List<? extends Element> children, double mouseX, double mouseY) {
        for (Element child : children) {
            if (child instanceof IJEIIngredientHelper && child.isMouseOver(mouseX, mouseY)) {
                return ((IJEIIngredientHelper) child).getIngredient();
            }
        }
        return null;
    }

    @Override
    public Collection<IGuiClickableArea> getGuiClickableAreas(GUI gui, double mouseX, double mouseY) {
        //Make mouseX and mouseY not be relative
        mouseX += gui.getGuiLeft();
        mouseY += gui.getGuiTop();
        GuiWindow guiWindow = gui.getWindowHovering(mouseX, mouseY);
        if (guiWindow == null) {
            //If no window is being hovered, then check the elements in general
            return getGuiClickableArea(gui.children(), mouseX, mouseY);
        }
        //Otherwise check the elements of the window
        return getGuiClickableArea(guiWindow.children(), mouseX, mouseY);
    }

    private Collection<IGuiClickableArea> getGuiClickableArea(List<? extends Element> children, double mouseX, double mouseY) {
        for (Element child : children) {
            if (child instanceof GuiRelativeElement && child instanceof IJEIRecipeArea) {
                IJEIRecipeArea<?> recipeArea = (IJEIRecipeArea<?>) child;
                if (recipeArea.isActive()) {
                    Identifier[] categories = recipeArea.getRecipeCategories();
                    //getRecipeCategory is a cheaper call than isMouseOver so we perform it first
                    if (categories != null && recipeArea.isMouseOverJEIArea(mouseX, mouseY)) {
                        GuiRelativeElement element = (GuiRelativeElement) child;
                        //TODO: Decide if we want our own implementation to overwrite the getTooltipStrings and have it show something like "Crusher Recipes"
                        IGuiClickableArea clickableArea = IGuiClickableArea.createBasic(element.getRelativeX(), element.getRelativeY(), element.getWidth(),
                              element.getHeight(), categories);
                        return Collections.singleton(clickableArea);
                    }
                }
            }
        }
        return Collections.emptyList();
    }
}