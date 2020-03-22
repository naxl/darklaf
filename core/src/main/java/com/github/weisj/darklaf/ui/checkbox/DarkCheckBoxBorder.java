/*
 * MIT License
 *
 * Copyright (c) 2020 Jannis Weis
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.weisj.darklaf.ui.checkbox;


import com.github.weisj.darklaf.ui.button.DarkToggleButtonUI;
import com.github.weisj.darklaf.util.DarkUIUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.UIResource;
import java.awt.*;

/**
 * @author Jannis Weis
 */
public class DarkCheckBoxBorder extends EmptyBorder implements UIResource {

    @Override
    public void paintBorder(final Component c, final Graphics g, final int x, final int y,
                            final int width, final int height) {
    }

    public DarkCheckBoxBorder() {
        super(UIManager.getInsets("CheckBox.borderInsets"));
    }

    @Override
    public Insets getBorderInsets(final Component c) {
        if (isInCell(c)) {
            return new InsetsUIResource(0, 0, 0, 0);
        }
        return new InsetsUIResource(top, left, bottom, right);
    }

    protected static boolean isInCell(final Component c) {
        return isTreeCellEditor(c) || isTableCellEditor(c) || DarkUIUtil.isInCell(c);
    }

    protected static boolean isTreeCellEditor(final Component c) {
        return c instanceof JComponent
            && Boolean.TRUE.equals(((JComponent) c).getClientProperty(DarkToggleButtonUI.KEY_IS_TREE_EDITOR));
    }

    protected static boolean isTableCellEditor(final Component c) {
        return c instanceof JComponent
            && Boolean.TRUE.equals(((JComponent) c).getClientProperty(DarkToggleButtonUI.KEY_IS_TREE_EDITOR));
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}
