package pers.towdium.justEnoughCalculation.gui.commom;

import java.lang.reflect.Field;
import java.util.ArrayList;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * CODE IS DONE BY Zyin055
 * A GuiScreen replacement that supports putting tooltips onto GuiButtons.
 * SEE HERE: www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/modification-development/1437428-guide-1-7-2-how-to-make-button-tooltips
 */
public abstract class GuiTooltipScreen extends GuiJustEnoughCalculation
{
    /** Show a white "?" in the top right part of any button with a tooltip assigned to it */
    public static boolean ShowTooltipButtonEffect = true;

    /** Show an aqua "?" in the top right part of any button with a tooltip assigned to it when mouseovered */
    public static boolean ShowTooltipButtonMouseoverEffect = true;

    /** Putting this string into a tooltip will cause a line break */
    public String tooltipNewlineDelimeter = "_p";

    /** The amount of time in milliseconds until a tooltip is rendered */
    public long tooltipDelay = 900;

    /** The maximum width in pixels a tooltip can occupy before word wrapping occurs */
    public int tooltipMaxWidth = 150;

    protected int tooltipXOffset = 0;
    protected int tooltipYOffset = 10;

    private final static int LINE_HEIGHT = 11;

    private long mouseoverTime = 0;
    private long prevSystemTime = -1;

    public GuiTooltipScreen(@Nonnull Container inventorySlotsIn, @Nullable GuiScreen parent) {
        super(inventorySlotsIn, parent);
    }

    public void drawScreen(int mouseX, int mouseY, float f)
    {
        super.drawScreen(mouseX, mouseY, f);
        RenderHelper.disableStandardItemLighting();
        DrawTooltipScreen(mouseX, mouseY);
        InventoryPlayer inventoryplayer = this.mc.thePlayer.inventory;
        Field field;
        Slot theSlot = null;
        try {
            field = GuiContainer.class.getDeclaredField("theSlot");
            field.setAccessible(true);
            theSlot = (Slot) field.get(this);
        } catch (Exception ignored) {}
        try {
            field = GuiContainer.class.getDeclaredField("field_147006_u");
            field.setAccessible(true);
            theSlot = (Slot) field.get(this);
        } catch (Exception ignored) {}
        if (inventoryplayer.getItemStack() == null && theSlot != null && theSlot.getHasStack())
        {
            ItemStack itemstack1 = theSlot.getStack();
            this.renderToolTip(itemstack1, mouseX, mouseY);
        }
        RenderHelper.enableStandardItemLighting();
    }

    /**
     * This method must be overriden. Gets a tooltip String for a specific button.
     * Recommended to use a switch/case statement for buttonId for easy implementation.
     * @param buttonId The ID of the button this tooltip corresponds to
     * @return The tooltip string for the specified buttonId. null if no tooltip exists for this button.
     */
    protected abstract String GetButtonTooltip(int buttonId);

    /**
     * Renders any special effects applied to tooltip buttons, and renders any tooltips for GuiButtons 
     * that are being mouseovered.
     */
    protected void DrawTooltipScreen(int mouseX, int mouseY)
    {
        if(ShowTooltipButtonEffect)
            RenderTooltipButtonEffect();

        int mousedOverButtonId = -1;

        //find out which button is being mouseovered
        for (GuiButton button : buttonList) {
            if (IsButtonMouseovered(mouseX, mouseY, button)) {
                mousedOverButtonId = button.id;

                if (ShowTooltipButtonMouseoverEffect && GetButtonTooltip(mousedOverButtonId) != null)
                    RenderTooltipButtonMouseoverEffect(button);

                break;
            }
        }

        //calculate how long this button has been mouseovered for
        if(mousedOverButtonId > -1)
        {
            long systemTime = System.currentTimeMillis();

            if(prevSystemTime > 0)
                mouseoverTime += systemTime - prevSystemTime;

            prevSystemTime = systemTime;
        }
        else
        {
            mouseoverTime = 0;
        }

        //render the button's tooltip
        if(mouseoverTime > tooltipDelay)
        {
            String tooltip = GetButtonTooltip(mousedOverButtonId);
            if(tooltip != null)
            {
                RenderTooltip(mouseX, mouseY, tooltip);
            }
        }
    }

    /**
     * Determines if a GuiButton is being mouseovered.
     * @return true if this button is mouseovered
     */
    protected boolean IsButtonMouseovered(int mouseX, int mouseY, GuiButton button)
    {
        return mouseX >= button.xPosition + button.getButtonWidth() - 10 && mouseX <= button.xPosition + button.getButtonWidth() &&
                mouseY >= button.yPosition && mouseY <= button.yPosition + 10;
    }

    /**
     * Render anything special onto all buttons that have tooltips assigned to them.
     */
    protected void RenderTooltipButtonEffect()
    {
        for (GuiButton button : buttonList) {
            if (GetButtonTooltip(button.id) != null) {
                boolean flag = mc.fontRendererObj.getUnicodeFlag();
                mc.fontRendererObj.setUnicodeFlag(true);
                mc.fontRendererObj.drawStringWithShadow("?", button.xPosition + button.getButtonWidth() - 7, button.yPosition + 1, 14737632);
                mc.fontRendererObj.setUnicodeFlag(flag);
            }
        }
    }

    /**
     * Render anything special onto buttons that have tooltips assigned to them when they are mousevered.
     */
    protected void RenderTooltipButtonMouseoverEffect(GuiButton button)
    {
        boolean flag = mc.fontRendererObj.getUnicodeFlag();
        mc.fontRendererObj.setUnicodeFlag(true);
        mc.fontRendererObj.drawStringWithShadow(FontCodes.AQUA + "?", button.xPosition+button.getButtonWidth()-7, button.yPosition+1, 14737632);
        mc.fontRendererObj.setUnicodeFlag(flag);
    }

    /**
     * Renders a tooltip at (x,y).
     */
    protected void RenderTooltip(int x, int y, String tooltip)
    {
        String[] tooltipArray = ParseTooltipArrayFromString(tooltip);

        int tooltipWidth = GetTooltipWidth(tooltipArray);
        int tooltipHeight = GetTooltipHeight(tooltipArray);

        int tooltipX = x + tooltipXOffset;
        int tooltipY = y + tooltipYOffset;

        if(tooltipX > width - tooltipWidth - 7)
            tooltipX = width - tooltipWidth - 7;
        if(tooltipY > height -  tooltipHeight - 8)
            tooltipY = height -  tooltipHeight - 8;

        //render the background inside box
        int innerAlpha = -0xFEFFFF0;  //very very dark purple
        drawGradientRect(tooltipX, tooltipY - 1, tooltipX + tooltipWidth + 6, tooltipY, innerAlpha, innerAlpha);
        drawGradientRect(tooltipX, tooltipY + tooltipHeight + 6, tooltipX + tooltipWidth + 6, tooltipY + tooltipHeight + 7, innerAlpha, innerAlpha);
        drawGradientRect(tooltipX, tooltipY, tooltipX + tooltipWidth + 6, tooltipY + tooltipHeight + 6, innerAlpha, innerAlpha);
        drawGradientRect(tooltipX - 1, tooltipY, tooltipX, tooltipY + tooltipHeight + 6, innerAlpha, innerAlpha);
        drawGradientRect(tooltipX + tooltipWidth + 6, tooltipY, tooltipX + tooltipWidth + 7, tooltipY + tooltipHeight + 6, innerAlpha, innerAlpha);

        //render the background outside box
        int outerAlpha1 = 0x505000FF;
        int outerAlpha2 = (outerAlpha1 & 0xFEFEFE) >> 1 | outerAlpha1 & -0x1000000;
        drawGradientRect(tooltipX, tooltipY + 1, tooltipX + 1, tooltipY + tooltipHeight + 6 - 1, outerAlpha1, outerAlpha2);
        drawGradientRect(tooltipX + tooltipWidth + 5, tooltipY + 1, tooltipX + tooltipWidth + 7, tooltipY + tooltipHeight + 6 - 1, outerAlpha1, outerAlpha2);
        drawGradientRect(tooltipX, tooltipY, tooltipX + tooltipWidth + 3, tooltipY + 1, outerAlpha1, outerAlpha1);
        drawGradientRect(tooltipX, tooltipY + tooltipHeight + 5, tooltipX + tooltipWidth + 7, tooltipY + tooltipHeight + 6, outerAlpha2, outerAlpha2);

        //render the foreground text
        int lineCount = 0;
        for (String s : tooltipArray)
        {
            mc.fontRendererObj.drawStringWithShadow(s, tooltipX + 2, tooltipY + 2 + lineCount * LINE_HEIGHT, 0xFFFFFF);
            lineCount++;
        }
    }

    /**
     * Converts a String representation of a tooltip into a String[], and also decodes any font codes used.
     * @param s Ex: "Hello,_nI am your _ltooltip_r and you love me."
     * @return An array of Strings such that each String width does not exceed tooltipMaxWidth
     */
    protected String[] ParseTooltipArrayFromString(String s)
    {
        s = DecodeStringCodes(s);
        String[] tooltipSections = s.split(tooltipNewlineDelimeter);
        ArrayList<String> tooltipArrayList = new ArrayList<>();

        for(String section : tooltipSections)
        {
            String tooltip = "";
            String[] tooltipWords = section.split(" ");

            for (String tooltipWord : tooltipWords) {
                int lineWidthWithNextWord = mc.fontRendererObj.getStringWidth(tooltip + tooltipWord);
                if (lineWidthWithNextWord > tooltipMaxWidth) {
                    tooltipArrayList.add(tooltip.trim());
                    tooltip = tooltipWord + " ";
                } else {
                    tooltip += tooltipWord + " ";
                }
            }

            tooltipArrayList.add(tooltip.trim());
        }

        String[] tooltipArray = new String[tooltipArrayList.size()];
        tooltipArrayList.toArray(tooltipArray);

        return tooltipArray;
    }

    /**
     * Decodes any font codes into something useable by the fontRendererObj.
     * @param s E.x.: "Hello,_nI am your _ltooltip_r and you love me."
     * @return E.x. output (html not included): <br>"Hello,<br>I am your <b>tooltip</b> and you love me."
     */
    private String DecodeStringCodes(String s)
    {
        return s.replace("_0", FontCodes.BLACK)
                .replace("_1", FontCodes.DARK_BLUE)
                .replace("_2", FontCodes.DARK_GREEN)
                .replace("_3", FontCodes.DARK_AQUA)
                .replace("_4", FontCodes.DARK_RED)
                .replace("_5", FontCodes.DARK_PURPLE)
                .replace("_6", FontCodes.GOLD)
                .replace("_7", FontCodes.GRAY)
                .replace("_8", FontCodes.DARK_GREY)
                .replace("_9", FontCodes.BLUE)
                .replace("_a", FontCodes.GREEN)
                .replace("_b", FontCodes.AQUA)
                .replace("_c", FontCodes.RED)
                .replace("_d", FontCodes.LIGHT_PURPLE)
                .replace("_e", FontCodes.YELLOW)
                .replace("_f", FontCodes.WHITE)
                .replace("_k", FontCodes.OBFUSCATED)
                .replace("_l", FontCodes.BOLD)
                .replace("_m", FontCodes.STRIKETHROUGH)
                .replace("_n", FontCodes.UNDERLINE)
                .replace("_o", FontCodes.ITALICS)
                .replace("_r", FontCodes.RESET);
    }

    /***
     * Gets the width of the tooltip in pixels.
     */
    private int GetTooltipWidth(String[] tooltipArray)
    {
        int longestWidth = 0;
        for(String s : tooltipArray)
        {
            int width = mc.fontRendererObj.getStringWidth(s);
            if(width > longestWidth)
                longestWidth = width;
        }
        return longestWidth;
    }

    /**
     * Gets the height of the tooltip in pixels.
     */
    private int GetTooltipHeight(String[] tooltipArray)
    {
        int tooltipHeight = mc.fontRendererObj.FONT_HEIGHT - 2;
        if (tooltipArray.length > 1)
        {
            tooltipHeight += (tooltipArray.length - 1) * LINE_HEIGHT;
        }
        return tooltipHeight;
    }


    public class FontCodes
    {
        //color codes for rendered strings
        public static final String BLACK = "\2470";
        public static final String DARK_BLUE = "\2471";
        public static final String DARK_GREEN = "\2472";
        public static final String DARK_AQUA = "\2473";
        public static final String DARK_RED = "\2474";
        public static final String DARK_PURPLE = "\2475";
        public static final String GOLD = "\2476";
        public static final String GRAY = "\2477";
        public static final String DARK_GREY = "\2478";
        public static final String BLUE = "\2479";
        public static final String GREEN = "\247a";
        public static final String AQUA = "\247b";
        public static final String RED = "\247c";
        public static final String LIGHT_PURPLE = "\247d";
        public static final String YELLOW = "\247e";
        public static final String WHITE = "\247f";

        //font styles
        public static final String OBFUSCATED = "\247k";
        public static final String BOLD = "\247l";
        public static final String STRIKETHROUGH = "\247m";
        public static final String UNDERLINE = "\247n";
        public static final String ITALICS = "\247o";

        public static final String RESET = "\247r";
    }
}