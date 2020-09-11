/*
 * MIT License
 *
 * Copyright (c) 2020 Jannis Weis
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package com.github.weisj.darklaf.ui.colorchooser;

import java.awt.*;

import javax.swing.*;

/**
 * @author Jannis Weis
 */
class MainSwatchPanel extends SwatchPanel {

    protected void initValues() {
        swatchSize = UIManager.getDimension("ColorChooser.swatchesSwatchSize", getLocale());
        numSwatches = new Dimension(30, 15);
        gap = new Dimension(1, 1);
    }

    protected void initColors() {
        int[] rawValues = initRawValues();
        int numColors = rawValues.length / 3;

        colors = new Color[numColors];
        for (int i = 0; i < numColors; i++) {
            colors[i] = new Color(rawValues[(i * 3)], rawValues[(i * 3) + 1], rawValues[(i * 3) + 2]);
        }
    }

    private int[] initRawValues() {
        return new int[] {0, 0, 0, 31, 0, 0, 31, 6, 0, 31, 12, 0, 31, 18, 0, 31, 24, 0, 31, 31, 0, 24, 31, 0, 18, 31, 0,
            12, 31, 0, 6, 31, 0, 0, 31, 0, 0, 31, 6, 0, 31, 12, 0, 31, 18, 0, 31, 24, 0, 31, 31, 0, 24, 31, 0, 18, 31,
            0, 12, 31, 0, 6, 31, 0, 0, 31, 6, 0, 31, 12, 0, 31, 18, 0, 31, 24, 0, 31, 31, 0, 31, 31, 0, 24, 31, 0, 18,
            31, 0, 12, 18, 18, 18, 61, 0, 0, 61, 12, 0, 61, 24, 0, 61, 37, 0, 61, 49, 0, 61, 61, 0, 49, 61, 0, 37, 61,
            0, 24, 61, 0, 12, 61, 0, 0, 61, 0, 0, 61, 12, 0, 61, 24, 0, 61, 37, 0, 61, 49, 0, 61, 61, 0, 49, 61, 0, 37,
            61, 0, 24, 61, 0, 12, 61, 0, 0, 61, 11, 0, 61, 24, 0, 61, 37, 0, 61, 49, 0, 61, 61, 0, 61, 61, 0, 49, 61, 0,
            37, 61, 0, 24, 36, 36, 36, 92, 0, 0, 92, 18, 0, 92, 37, 0, 92, 55, 0, 92, 73, 0, 92, 92, 0, 73, 92, 0, 55,
            92, 0, 37, 92, 0, 18, 92, 0, 0, 92, 0, 0, 92, 18, 0, 92, 37, 0, 92, 55, 0, 92, 73, 0, 92, 92, 0, 73, 92, 0,
            55, 92, 0, 37, 92, 0, 18, 92, 0, 0, 92, 17, 0, 92, 37, 0, 92, 55, 0, 92, 73, 0, 92, 92, 0, 92, 92, 0, 73,
            92, 0, 55, 92, 0, 37, 54, 54, 54, 128, 0, 0, 128, 26, 0, 128, 51, 0, 128, 77, 0, 128, 102, 0, 128, 128, 0,
            102, 128, 0, 77, 128, 0, 51, 128, 0, 26, 128, 0, 0, 128, 0, 0, 128, 25, 0, 128, 51, 0, 128, 77, 0, 128, 102,
            0, 128, 128, 0, 102, 128, 0, 77, 128, 0, 51, 128, 0, 26, 128, 0, 0, 128, 23, 0, 128, 51, 0, 128, 77, 0, 128,
            102, 0, 128, 128, 0, 128, 128, 0, 102, 128, 0, 76, 128, 0, 51, 72, 72, 72, 158, 0, 0, 158, 32, 0, 158, 63,
            0, 158, 95, 0, 158, 126, 0, 158, 158, 0, 126, 158, 0, 95, 158, 0, 63, 158, 0, 32, 158, 0, 0, 158, 0, 0, 158,
            32, 0, 158, 63, 0, 158, 95, 0, 158, 126, 0, 158, 158, 0, 126, 158, 0, 95, 158, 0, 63, 158, 0, 32, 158, 0, 0,
            158, 29, 0, 158, 63, 0, 158, 95, 0, 158, 126, 0, 158, 158, 0, 158, 158, 0, 126, 158, 0, 95, 158, 0, 63, 91,
            91, 91, 189, 0, 0, 189, 38, 0, 189, 75, 0, 189, 113, 0, 189, 151, 0, 189, 189, 0, 151, 189, 0, 113, 189, 0,
            75, 189, 0, 38, 189, 0, 0, 189, 0, 0, 189, 38, 0, 189, 75, 0, 189, 113, 0, 189, 151, 0, 189, 189, 0, 151,
            189, 0, 113, 189, 0, 75, 189, 0, 38, 189, 0, 0, 189, 35, 0, 189, 75, 0, 189, 113, 0, 189, 151, 0, 189, 189,
            0, 189, 189, 0, 151, 189, 0, 113, 189, 0, 75, 109, 109, 109, 219, 0, 0, 219, 44, 0, 219, 88, 0, 219, 132, 0,
            219, 175, 0, 219, 219, 0, 175, 219, 0, 132, 219, 0, 88, 219, 0, 44, 219, 0, 0, 219, 0, 0, 219, 44, 0, 219,
            88, 0, 219, 132, 0, 219, 175, 0, 219, 219, 0, 175, 219, 0, 132, 219, 0, 88, 219, 0, 44, 219, 0, 0, 219, 40,
            0, 219, 88, 0, 219, 132, 0, 219, 175, 0, 219, 219, 0, 219, 219, 0, 175, 219, 0, 132, 219, 0, 88, 127, 127,
            127, 255, 0, 0, 255, 51, 0, 255, 102, 0, 255, 153, 0, 255, 204, 0, 255, 255, 0, 204, 255, 0, 153, 255, 0,
            102, 255, 0, 51, 255, 0, 0, 255, 0, 0, 255, 51, 0, 255, 102, 0, 255, 153, 0, 255, 204, 0, 255, 255, 0, 204,
            255, 0, 153, 255, 0, 102, 255, 0, 51, 255, 0, 0, 255, 47, 0, 255, 102, 0, 255, 153, 0, 255, 204, 0, 255,
            255, 0, 255, 255, 0, 204, 255, 0, 153, 255, 0, 102, 145, 145, 145, 255, 31, 31, 255, 75, 31, 255, 120, 31,
            255, 165, 31, 255, 210, 31, 255, 255, 31, 210, 255, 31, 165, 255, 31, 120, 255, 31, 75, 255, 31, 31, 255,
            31, 31, 255, 75, 31, 255, 120, 31, 255, 165, 31, 255, 210, 31, 255, 255, 31, 210, 255, 31, 165, 255, 31,
            120, 255, 31, 75, 255, 31, 31, 255, 72, 31, 255, 120, 31, 255, 165, 31, 255, 210, 31, 255, 255, 31, 255,
            255, 31, 210, 255, 31, 165, 255, 31, 120, 163, 163, 163, 255, 61, 61, 255, 100, 61, 255, 139, 61, 255, 177,
            61, 255, 216, 61, 255, 255, 61, 216, 255, 61, 177, 255, 61, 139, 255, 61, 100, 255, 61, 61, 255, 61, 61,
            255, 100, 61, 255, 139, 61, 255, 177, 61, 255, 216, 61, 255, 255, 61, 216, 255, 61, 177, 255, 61, 139, 255,
            61, 100, 255, 61, 61, 255, 97, 61, 255, 139, 61, 255, 177, 61, 255, 216, 61, 255, 255, 61, 255, 255, 61,
            216, 255, 61, 177, 255, 61, 139, 182, 182, 182, 255, 92, 92, 255, 124, 92, 255, 157, 92, 255, 190, 92, 255,
            222, 92, 255, 255, 92, 222, 255, 92, 190, 255, 92, 157, 255, 92, 124, 255, 92, 92, 255, 92, 92, 255, 124,
            92, 255, 157, 92, 255, 190, 92, 255, 222, 92, 255, 255, 92, 222, 255, 92, 190, 255, 92, 157, 255, 92, 124,
            255, 92, 92, 255, 122, 92, 255, 157, 92, 255, 190, 92, 255, 222, 92, 255, 255, 92, 255, 255, 92, 222, 255,
            92, 190, 255, 92, 157, 200, 200, 200, 255, 128, 128, 255, 153, 128, 255, 179, 128, 255, 204, 128, 255, 230,
            128, 255, 255, 128, 229, 255, 128, 204, 255, 128, 179, 255, 128, 153, 255, 128, 128, 255, 128, 128, 255,
            153, 128, 255, 179, 128, 255, 204, 128, 255, 229, 128, 255, 255, 128, 229, 255, 128, 204, 255, 128, 179,
            255, 128, 153, 255, 128, 128, 255, 151, 128, 255, 178, 128, 255, 204, 128, 255, 230, 128, 255, 255, 128,
            255, 255, 128, 229, 255, 128, 204, 255, 128, 179, 218, 218, 218, 255, 158, 158, 255, 177, 158, 255, 197,
            158, 255, 216, 158, 255, 236, 158, 255, 255, 158, 236, 255, 158, 216, 255, 158, 197, 255, 158, 177, 255,
            158, 158, 255, 158, 158, 255, 177, 158, 255, 197, 158, 255, 216, 158, 255, 236, 158, 255, 255, 158, 236,
            255, 158, 216, 255, 158, 197, 255, 158, 177, 255, 158, 158, 255, 176, 158, 255, 197, 158, 255, 216, 158,
            255, 236, 158, 255, 255, 158, 255, 255, 158, 236, 255, 158, 216, 255, 158, 197, 236, 236, 236, 255, 189,
            189, 255, 202, 189, 255, 215, 189, 255, 228, 189, 255, 242, 189, 255, 255, 189, 242, 255, 189, 228, 255,
            189, 215, 255, 189, 202, 255, 189, 189, 255, 189, 189, 255, 202, 189, 255, 215, 189, 255, 228, 189, 255,
            242, 189, 255, 255, 189, 242, 255, 189, 228, 255, 189, 215, 255, 189, 202, 255, 189, 189, 255, 201, 189,
            255, 215, 189, 255, 228, 189, 255, 242, 189, 255, 255, 189, 255, 255, 189, 242, 255, 189, 228, 255, 189,
            215, 255, 255, 255, 255, 219, 219, 255, 226, 219, 255, 234, 219, 255, 241, 219, 255, 248, 219, 255, 255,
            219, 248, 255, 219, 241, 255, 219, 234, 255, 219, 226, 255, 219, 219, 255, 219, 219, 255, 226, 219, 255,
            234, 219, 255, 241, 219, 255, 248, 219, 255, 255, 219, 248, 255, 219, 241, 255, 219, 234, 255, 219, 226,
            255, 219, 219, 255, 226, 219, 255, 234, 219, 255, 241, 219, 255, 248, 219, 255, 255, 219, 255, 255, 219,
            248, 255, 219, 241, 255, 219, 234,};
    }
}
