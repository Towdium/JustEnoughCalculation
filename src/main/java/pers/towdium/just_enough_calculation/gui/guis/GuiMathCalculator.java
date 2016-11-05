package pers.towdium.just_enough_calculation.gui.guis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import org.lwjgl.input.Keyboard;
import pers.towdium.just_enough_calculation.JustEnoughCalculation;
import pers.towdium.just_enough_calculation.gui.JECContainer;
import pers.towdium.just_enough_calculation.gui.JECGuiContainer;
import pers.towdium.just_enough_calculation.network.packets.PacketSyncCalculator;
import pers.towdium.just_enough_calculation.util.Utilities;
import pers.towdium.just_enough_calculation.util.exception.IllegalPositionException;
import pers.towdium.just_enough_calculation.util.wrappers.Singleton;

import javax.annotation.Nullable;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Stack;
import java.util.function.BiFunction;

/**
 * Author: Towdium
 * Date:   2016/9/24.
 */
public class GuiMathCalculator extends JECGuiContainer {
    static final String KEY_MATH = "math";
    static final String KEY_RECORD = "record";
    static final String KEY_SIGN = "sign";
    static final String KEY_CURRENT = "current";
    static final String KEY_TYPE = "type";
    NumContainer current = new NumStack();
    BigDecimal record = null;
    enumSign sign = enumSign.NONE;
    boolean initialized;

    public GuiMathCalculator(GuiScreen parent) {
        super(new JECContainer() {
            @Override
            protected void addSlots() {}

            @Override
            public EnumSlotType getSlotType(int index) {
                return EnumSlotType.DISABLED;
            }
        }, parent);
    }

    @Nullable
    @Override
    protected String getButtonTooltip(int buttonId) {
        return null;
    }

    @Override
    protected int getSizeSlot(int index) {
        return 0;
    }

    @Override
    protected void init() {
        buttonList.add(new MyButton(7, guiLeft + 7, guiTop + 67, 28, 20, "7"));
        buttonList.add(new MyButton(8, guiLeft + 39, guiTop + 67, 28, 20, "8"));
        buttonList.add(new MyButton(9, guiLeft + 71, guiTop + 67, 28, 20, "9"));
        buttonList.add(new MyButton(4, guiLeft + 7, guiTop + 91, 28, 20, "4"));
        buttonList.add(new MyButton(5, guiLeft + 39, guiTop + 91, 28, 20, "5"));
        buttonList.add(new MyButton(6, guiLeft + 71, guiTop + 91, 28, 20, "6"));
        buttonList.add(new MyButton(1, guiLeft + 7, guiTop + 115, 28, 20, "1"));
        buttonList.add(new MyButton(2, guiLeft + 39, guiTop + 115, 28, 20, "2"));
        buttonList.add(new MyButton(3, guiLeft + 71, guiTop + 115, 28, 20, "3"));
        buttonList.add(new MyButton(0, guiLeft + 7, guiTop + 139, 28, 20, "0"));
        buttonList.add(new MyButton(10, guiLeft + 39, guiTop + 139, 28, 20, "00"));
        buttonList.add(new MyButton(11, guiLeft + 71, guiTop + 139, 28, 20, "."));
        buttonList.add(new MyButton(12, guiLeft + 109, guiTop + 67, 28, 20, "◄"));
        buttonList.add(new MyButton(13, guiLeft + 141, guiTop + 67, 28, 20, "+"));
        buttonList.add(new MyButton(14, guiLeft + 109, guiTop + 91, 28, 20, "C"));
        buttonList.add(new MyButton(15, guiLeft + 141, guiTop + 91, 28, 20, "-"));
        buttonList.add(new MyButton(16, guiLeft + 109, guiTop + 115, 28, 44, "="));
        buttonList.add(new MyButton(17, guiLeft + 141, guiTop + 115, 28, 20, "*"));
        buttonList.add(new MyButton(18, guiLeft + 141, guiTop + 139, 28, 20, "/"));
        if (!initialized) {
            updateGuiFromItem();
            initialized = true;
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(new ResourceLocation(JustEnoughCalculation.Reference.MODID, "textures/gui/guiMathCalculator.png"));
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        drawDisplay();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        actionPerformed(button.id);
    }

    void actionPerformed(int i) {
        if (i == -1)
            return;
        if (i < 10) {
            current = current.appendChar((char)('0' + i));
        } else if (i == 10) {
            current = current.appendChar('0');
            current = current.appendChar('0');
        } else {
            switch (i) {
                case 11:
                    current = current.appendChar('.');
                    break;
                case 12:
                    current = current.removeChar();
                    break;
                case 13:
                    updateResult();
                    sign = enumSign.PLUS;
                    break;
                case 15:
                    updateResult();
                    sign = enumSign.MINUS;
                    break;
                case 17:
                    updateResult();
                    sign = enumSign.PRODUCT;
                    break;
                case 18:
                    updateResult();
                    sign = enumSign.DIVIDE;
                    break;
                case 16:
                    updateResult();
                    sign = enumSign.NONE;
                    break;
                case 14:
                    current = new NumStack();
                    record = null;
                    sign = enumSign.NONE;
                    break;
                default:
                    throw new IllegalPositionException();
            }
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        updateItemFromGui();
    }

    @Override
    public void handleKeyboardInput() throws IOException {
        super.handleKeyboardInput();
        actionPerformed(getButton());
    }

    int getButton() {
        if (!Keyboard.getEventKeyState())
            return -1;
        char c = Keyboard.getEventCharacter();
        int code = Keyboard.getEventKey();
        if (c >= '0' && c <= '9') {
            return c - '0';
        } else {
            switch (c) {
                case '/': return 18;
                case '*': return 17;
                case '+': return 13;
                case '-': return 15;
                case '=': return 16;
                case '.': return 11;
            }
            switch (code) {
                case 28: return 16;
                case 14: return 12;
                case 211: return 14;
                default: return -1;
            }
        }
    }

    void updateResult() {
        if(current instanceof NumStack)
            current = new NumBigDec((record == null ? enumSign.NONE : sign).getOperator().apply(record, current.toBigDec()));
        record = current.toBigDec();
    }

    void updateItemFromGui() {
        Singleton<ItemStack> calc = new Singleton<>(null);
        calc.predicate = stack -> stack.getItem() == JustEnoughCalculation.itemCalculator;
        calc.push(mc.thePlayer.inventory.getCurrentItem());
        calc.push(mc.thePlayer.inventory.offHandInventory[0]);
        if (calc.value != null) {
            NBTTagCompound tag = calc.value.getSubCompound(KEY_MATH, true);
            tag.setString(KEY_RECORD, record == null ? "" : record.toString());
            tag.setInteger(KEY_SIGN, sign.ordinal());
            tag.setString(KEY_CURRENT, current.toString());
            tag.setInteger(KEY_TYPE, current.getType().ordinal());
            JustEnoughCalculation.networkWrapper.sendToServer(new PacketSyncCalculator(calc.value));
        }

    }

    @SuppressWarnings("ConstantConditions")
    void updateGuiFromItem() {
        Singleton<ItemStack> calc = new Singleton<>(null);
        calc.predicate = stack -> stack.getItem() == JustEnoughCalculation.itemCalculator;
        calc.push(mc.thePlayer.inventory.getCurrentItem());
        calc.push(mc.thePlayer.inventory.offHandInventory[0]);
        if (calc.value != null) {
            NBTTagCompound tag = calc.value.getSubCompound(KEY_MATH, false);
            if(tag != null) {
                String str = tag.getString(KEY_RECORD);
                record = str.equals("") ? null : new BigDecimal(tag.getString(KEY_RECORD));
                sign = enumSign.values()[tag.getInteger(KEY_SIGN)];
                switch (NumContainer.enumType.values()[tag.getInteger(KEY_TYPE)]) {
                    case BIG_DEC:
                        current = new NumBigDec(tag.getString(KEY_CURRENT));
                        break;
                    case STACK:
                        current = new NumStack(tag.getString(KEY_CURRENT));
                        break;
                }
            }
        }
    }

    // GRAPHICS

    protected void drawLT(int x, int y) {
        drawTexturedModalRect(guiLeft + x, guiTop + y, 176, 0, 4, 14);
    }

    protected void drawRT(int x, int y) {
        drawTexturedModalRect(guiLeft + x + 14, guiTop + y, 180, 0, 4, 14);
    }

    protected void drawLB(int x, int y) {
        drawTexturedModalRect(guiLeft + x, guiTop + y + 14, 184, 0, 4, 14);
    }

    protected void drawRB(int x, int y) {
        drawTexturedModalRect(guiLeft + x + 14, guiTop + y + 14, 188, 0, 4, 14);
    }

    protected void drawT(int x, int y) {
        drawTexturedModalRect(guiLeft + x + 3, guiTop + y, 176, 17, 12, 4);
    }

    protected void drawM(int x, int y) {
        drawTexturedModalRect(guiLeft + x + 3, guiTop + y + 13, 176, 21, 12, 4);
    }

    protected void drawB(int x, int y) {
        drawTexturedModalRect(guiLeft + x + 3, guiTop + y + 26, 176, 25, 12, 4);
    }

    protected void drawDot(int x, int y) {
        drawTexturedModalRect(guiLeft + x + 17, guiTop + y + 26, 188, 17, 4, 4);
    }

    protected void drawCharPlus(int x, int y) {
        drawTexturedModalRect(guiLeft + x, guiTop + y, 192, 21, 7, 7);
    }

    protected void drawCharMinus(int x, int y) {
        drawTexturedModalRect(guiLeft + x, guiTop + y, 192, 14, 7, 7);
    }

    protected void drawCharProduct(int x, int y) {
        drawTexturedModalRect(guiLeft + x, guiTop + y, 192, 7, 7, 7);
    }

    protected void drawCharDivide(int x, int y) {
        drawTexturedModalRect(guiLeft + x, guiTop + y, 192, 0, 7, 7);
    }

    protected void drawChar(int x, int y, char num) {
        boolean[] sh = getShape(num);
        if(sh[0]) drawT(x, y);
        if(sh[1]) drawRT(x, y);
        if(sh[2]) drawRB(x, y);
        if(sh[3]) drawB(x, y);
        if(sh[4]) drawLB(x, y);
        if(sh[5]) drawLT(x, y);
        if(sh[6]) drawM(x, y);
    }

    protected void drawChar(int pos, char num) {
        drawChar(141 - pos*20, 21, num);
    }

    protected void drawDot(int pos) {
        drawDot(141 - pos*20, 21);
    }

    protected void drawCharPlus() {
        drawCharPlus(152, 12);
    }

    protected void drawCharMinus() {
        drawCharMinus(146, 12);
    }

    protected void drawCharProduct() {
        drawCharProduct(140, 12);
    }

    protected void drawCharDivide() {
        drawCharDivide(134, 12);
    }

    protected void drawDisplay() {
        int dot = current.getDot();
        if (dot == NumContainer.DOT_DEFAULT)
            drawDot(0);
        else if (dot != NumContainer.DOT_NONE)
            drawDot(dot);
        int index = -1;
        for (char c : current.getChars()) {
            drawChar(++index, c);
        }
        switch (sign) {
            case PLUS: drawCharPlus(); break;
            case MINUS: drawCharMinus(); break;
            case PRODUCT: drawCharProduct(); break;
            case DIVIDE: drawCharDivide(); break;
            case NONE: break;
            default: throw new IllegalPositionException();
        }
    }

    protected boolean[] getShape(char c) {
        switch (c) {
            case '0': return Shapes.ZERO;
            case '1': return Shapes.ONE;
            case '2': return Shapes.TWO;
            case '3': return Shapes.THREE;
            case '4': return Shapes.FOUR;
            case '5': return Shapes.FIVE;
            case '6': return Shapes.SIX;
            case '7': return Shapes.SEVEN;
            case '8': return Shapes.EIGHT;
            case '9': return Shapes.NINE;
            case 'E': return Shapes.CHAR_E;
            case 'r': return Shapes.CHAR_r;
            case 'o': return Shapes.CHAR_o;
            case '-': return Shapes.MINUS;
        }
        return new boolean[] {false, false, false, false, false, false, false};
    }

    enum enumSign{
        PLUS, MINUS, PRODUCT, DIVIDE, NONE;

        static final MathContext mc = new MathContext(10, RoundingMode.HALF_UP);

        public BiFunction<BigDecimal, BigDecimal, BigDecimal> getOperator() {
            switch (this) {
                case PLUS: return BigDecimal::add;
                case MINUS: return  BigDecimal::subtract;
                case PRODUCT: return BigDecimal::multiply;
                case DIVIDE: return (a, b) -> a.divide(b, mc);
                case NONE: return (a, b) -> b;
            }
            throw new IllegalPositionException();
        }
    }

    interface NumContainer {
        char[] ERROR = {'r', 'o', 'r', 'r', 'E', '\0', '\0'};
        char[] DEFAULT = {'0', '\0', '\0', '\0', '\0', '\0', '\0'};
        int DOT_DEFAULT = -1;
        int DOT_NONE = -2;

        char[] getChars();

        int getDot();

        enumType getType();

        NumContainer appendChar(char c);

        NumContainer removeChar();

        BigDecimal toBigDec();

        enum enumType {
            BIG_DEC, STACK
        }
    }

    // GUI MATH

    static class Shapes {
        static boolean[] ZERO = {true, true, true, true, true, true, false};
        static boolean[] ONE = {false, true, true, false, false, false, false};
        static boolean[] TWO = {true, true, false, true, true, false, true};
        static boolean[] THREE = {true, true, true, true, false, false, true};
        static boolean[] FOUR = {false, true, true, false, false, true, true};
        static boolean[] FIVE = {true, false, true, true, false, true, true};
        static boolean[] SIX = {true, false, true, true, true, true, true};
        static boolean[] SEVEN = {true, true, true, false, false, false, false};
        static boolean[] EIGHT = {true, true, true, true, true, true, true};
        static boolean[] NINE = {true, true, true, true, false, true, true};
        static boolean[] CHAR_E = {true, false, false, true, true, true, true};
        static boolean[] CHAR_r = {false, false, false, false, true, false, true};
        static boolean[] CHAR_o = {false, false, true, true, true, false, true};
        static boolean[] MINUS = {false, false, false, false, false, false, true};
    }

    static class NumBigDec implements NumContainer{
        static NumberFormat format = new DecimalFormat("0.0E0"){
            {setMinimumFractionDigits(0);}
        };
        BigDecimal value;
        char[] cacheChar = new char[7];
        int cacheDot = DOT_DEFAULT;

        public NumBigDec(BigDecimal decimal) {
            value = decimal;
            updateCache();
        }

        public NumBigDec(String s) {
            value = new BigDecimal(s);
            updateCache();
        }

        @Override
        public char[] getChars() {
            return cacheChar;
        }

        @Override
        public int getDot() {
            return cacheDot;
        }

        @Override
        public enumType getType() {
            return enumType.BIG_DEC;
        }

        @Override
        public NumContainer appendChar(char c) {
            return new NumStack().appendChar(c);
        }

        @Override
        public NumContainer removeChar() {
            return new NumStack();
        }

        @Override
        public BigDecimal toBigDec() {
            return value;
        }

        @Override
        public String toString() {
            return value.toString();
        }

        void updateCache() {
            System.arraycopy(DEFAULT, 0, cacheChar, 0, 7);
            String s = value.stripTrailingZeros().toPlainString();
            int i = s.indexOf('.');
            if (i != -1)
                s = s.substring(0, i) + s.substring(i + 1);
            char[] buffer = s.toCharArray();
            boolean pos = value.signum() != -1;
            int len = pos ? 7 : 6;
            if (buffer.length <= 7 || (i <= 7 && i != -1 && (buffer[0] == '-' ? buffer[1] != '0' : buffer[0] != '0')) ||
                    (buffer[0] != '-' && buffer[0] == '0' && !(buffer[1] == '0' && buffer[2] == '0')) ||
                    (buffer[0] == '-' && buffer[1] == '0' && !(buffer[2] == '0' && buffer[3] == '0'))) {
                char[] ret = new char[7];
                int index = buffer.length > 7 ? 7 : buffer.length;
                cacheDot = i == -1 ? -1 : index - i;
                for(char c : buffer) {
                    if(index > 0)
                        ret[--index] = c;
                }
                cacheChar = ret;
            } else {
                value = value.stripTrailingZeros();
                int scale = value.precision() - (value.scale() == -1 ? 0 : value.scale());
                int scaleLen = Utilities.scaleOfInt(scale);
                if(scale < 0)
                    ++scaleLen;
                if(scaleLen > len - 2) {
                    System.arraycopy(ERROR, 0, cacheChar, 0, 7);
                    cacheDot = DOT_NONE;
                } else {
                    int dec = len - scaleLen - 2;
                    format.setMaximumFractionDigits(dec);
                    format.setMinimumFractionDigits(value.precision() > dec ? dec : 0);
                    s = format.format(value);
                    i = s.indexOf('.');
                    if (i != -1)
                        s = s.substring(0, i) + s.substring(i + 1);
                    for(int j = 0; j < 7 && s.length() - j > 0; j++) {
                        cacheChar[j] = s.charAt(s.length() - 1 - j);
                    }
                    cacheDot = i == -1 ? DOT_NONE : s.length() - i;
                }
            }
        }
    }

    static class NumStack implements NumContainer{
        Stack<Character> chars = new Stack<>();
        int posDot = DOT_DEFAULT;
        char[] cacheChar = new char[7];

        public NumStack() {
            updateCache();
        }

        public NumStack(String s) {
            for(int i = 0; s.charAt(i) != ' '; i++) {
                chars.push(s.charAt(i));
            }
            posDot =  Integer.parseInt(s.substring(s.indexOf(' ') + 1));
            updateCache();
        }

        @Override
        public char[] getChars() {
            return cacheChar;
        }

        @Override
        public int getDot() {
            return posDot;
        }

        @Override
        public enumType getType() {
            return enumType.STACK;
        }

        @Override
        public NumContainer appendChar(char c) {
            if(chars.size() >= 7)
                return this;
            if (c == '.') {
                if (posDot == DOT_DEFAULT) {
                    posDot = 0;
                    if (chars.isEmpty())
                        chars.push('0');
                }

            } else if (c == '0') {
                if (chars.size() != 0) {
                    chars.push(c);
                    changeDot(true);
                }
            } else {
                chars.push(c);
                changeDot(true);
            }
            updateCache();
            return this;
        }

        @Override
        public NumContainer removeChar() {
            if (chars.isEmpty())
                return this;
            else if (posDot == 0) {
                posDot = DOT_DEFAULT;
                if (chars.size() == 1 && chars.peek() == '0')
                    chars.pop();
            } else {
                chars.pop();
                changeDot(false);
            }
            updateCache();
            return this;
        }

        @Override
        public BigDecimal toBigDec() {
            if(chars.isEmpty()) {
                return new BigDecimal(0);
            }
            StringBuilder sb = new StringBuilder();
            chars.stream().filter(character -> character != '\0').forEach(sb::append);
            if(posDot != DOT_DEFAULT) {
                sb.insert(sb.length() - posDot, '.');
            }
            return new BigDecimal(sb.toString());
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            chars.stream().filter(character -> character != '\0').forEachOrdered(sb::append);
            sb.append(' ');
            sb.append(posDot);
            return sb.toString();
        }

        void updateCache() {
            int index = chars.size() - 1;
            for(int i = 6; i > index; i--) {
                cacheChar[i] = '\0';
            }
            if (chars.isEmpty()) {
                cacheChar[0] = '0';
            }else {
                ++index;
                for(Character c : chars) {
                    cacheChar[--index] = c;
                }
            }
        }

        void changeDot(boolean up) {
            if (up) {
                if (posDot >= 0 && posDot < 7)
                    ++posDot;
            }else {
                if (posDot > 0) {
                    --posDot;
                }
            }
        }
    }

    class MyButton extends GuiButtonExt{
        public MyButton(int id, int xPos, int yPos, int width, int height, String displayString) {
            super(id, xPos, yPos, width, height, displayString);
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            if(getButton() == id)
                super.drawButton(mc, xPosition, yPosition);
            else
                super.drawButton(mc, mouseX, mouseY);
        }
    }
}
