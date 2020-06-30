package com.ldtteam.blockout.controls;

import com.ldtteam.blockout.PaneParams;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Formatted larger textContent area.
 */
public class Text extends AbstractTextElement
{
    /**
     * String content of the text area.
     */
    protected String textContent;

    /**
     * List of string elements.
     */
    protected List<ITextProperties> formattedText;

    /**
     * The height of the text.
     */
    protected int textHeight;

    /**
     * The linespace of the text.
     */
    protected int linespace = 0;

    /**
     * Standard constructor which instantiates the textField.
     */
    public Text()
    {
        super();
        // Required default constructor.
    }

    /**
     * Create text from xml.
     *
     * @param params xml parameters.
     */
    public Text(final PaneParams params)
    {
        super(params);

        textContent = params.getLocalizedText();
        linespace = params.getIntAttribute("linespace", linespace);
    }

    @Override
    public void setScale(final float s)
    {
        super.setScale(s);
        formattedText = null;
    }

    /**
     * Getter of the textContent.
     *
     * @return the string content.
     */
    public String getTextContent()
    {
        return textContent;
    }

    public void setTextContent(final String s)
    {
        textContent = s;
        formattedText = null;
    }

    /**
     * Getter of the lineSpace.
     *
     * @return the lineSpace.
     */
    public int getLineSpace()
    {
        return linespace;
    }

    /**
     * Setter of the lineSpace.
     *
     * @param l the new lineSpace.
     */
    public void setLineSpace(final int l)
    {
        linespace = l;
    }

    /**
     * Getter of the lineHeight.
     *
     * @return the line height.
     */
    public int getLineHeight()
    {
        return (int) (mc.fontRenderer.FONT_HEIGHT * scale);
    }

    /**
     * Getter of the textheight.
     *
     * @return the text height.
     */
    public int getTextHeight()
    {
        // Force computation of textHeight, if necessary
        getFormattedText();
        return textHeight;
    }

    /**
     * Find the width of the string.
     *
     * @param s string to calculated width of.
     * @return the width of the string, in pixels.
     */
    public int getStringWidth(final ITextProperties s)
    {
        return (int) (mc.fontRenderer.func_238414_a_(s) * scale);
    }

    /**
     * Getter for the formattedText, instantiates it if not already.
     *
     * @return the list of strings.
     */
    public List<ITextProperties> getFormattedText()
    {
        if (formattedText == null)
        {
            if (textContent == null || textContent.length() == 0)
            {
                formattedText = Collections.unmodifiableList(new ArrayList<ITextProperties>());
            }
            else
            {
                formattedText = Collections.unmodifiableList(
                    mc.fontRenderer.func_238425_b_(new StringTextComponent(textContent), (int) (getWidth() / scale))
                        .stream()
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()));
            }

            final int numLines = formattedText.size();
            if (numLines > 0)
            {
                final int scaledLinespace = (int) (linespace * scale);
                textHeight = (numLines * (getLineHeight() + scaledLinespace)) - scaledLinespace;
            }
            else
            {
                textHeight = 0;
            }
        }

        return formattedText;
    }

    @Override
    public void drawSelf(final MatrixStack ms, final int mx, final int my)
    {
        final int scaledLinespace = (int) (linespace * scale);
        int offsetY = 0;

        if (textAlignment.isBottomAligned() || textAlignment.isVerticalCentered())
        {
            final int maxVisibleLines = (getHeight() + scaledLinespace) / (getLineHeight() + scaledLinespace);
            int maxVisibleSize = (maxVisibleLines * (getLineHeight() + scaledLinespace)) - scaledLinespace;

            if (getTextHeight() < maxVisibleSize)
            {
                maxVisibleSize = getTextHeight();
            }

            offsetY = Math.max(0, getHeight() - maxVisibleSize);

            if (textAlignment.isVerticalCentered())
            {
                offsetY = offsetY / 2;
            }
        }

        for (final ITextProperties s : getFormattedText())
        {
            int offsetX = 0;
            if (textAlignment.isRightAligned() || textAlignment.isHorizontalCentered())
            {
                offsetX = getWidth() - getStringWidth(s);

                if (textAlignment.isHorizontalCentered())
                {
                    offsetX /= 2;
                }
            }

            ms.push();
            ms.translate(getX() + offsetX, getY() + offsetY, 0);
            ms.scale((float) scale, (float) scale, (float) scale);
            mc.getTextureManager().bindTexture(TEXTURE);
            drawString(ms, s, 0, 0, textColor, shadow);
            ms.pop();

            offsetY += getLineHeight() + scaledLinespace;

            if ((offsetY + getLineHeight()) > getHeight())
            {
                break;
            }
        }
    }
}
