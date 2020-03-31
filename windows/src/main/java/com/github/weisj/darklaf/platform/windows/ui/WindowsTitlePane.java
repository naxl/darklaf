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

package com.github.weisj.darklaf.platform.windows.ui;

import com.github.weisj.darklaf.platform.decorations.CustomTitlePane;
import com.github.weisj.darklaf.icons.ScaledIcon;
import com.github.weisj.darklaf.icons.ToggleIcon;
import com.github.weisj.darklaf.platform.PointerUtil;
import com.github.weisj.darklaf.platform.windows.JNIDecorationsWindows;
import com.github.weisj.darklaf.util.PropertyKey;
import com.github.weisj.darklaf.util.Scale;
import sun.awt.SunToolkit;

import javax.accessibility.AccessibleContext;
import javax.swing.*;
import javax.swing.plaf.UIResource;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * @author Konstantin Bulenkov
 * @author Jannis Weis
 */
public class WindowsTitlePane extends CustomTitlePane {
    public static final String KEY_RESIZABLE = "resizable";
    public static final String KEY_STATE = "state";
    public static final String KEY_ICON_IMAGE = "iconImage";
    private static final int PAD = 5;
    private static final int BAR_HEIGHT = 28;
    private static final int BUTTON_WIDTH = 46;
    private static final int ICON_WIDTH = 32;
    private static final int ICON_SIZE = ICON_WIDTH - 3 * PAD;
    private final JRootPane rootPane;
    private final ContainerListener layeredPaneContainerListener = new ContainerListener() {
        @Override
        public void componentAdded(final ContainerEvent e) {
            if (e.getChild() instanceof JMenuBar) {
                addMenuBar(getRootPane().getJMenuBar());
            }
            if (getRootPane().getJMenuBar() == null && menuBar != null) {
                removeMenuBar();
            }
        }

        @Override
        public void componentRemoved(final ContainerEvent e) {
        }
    };
    private boolean oldResizable;
    private PropertyChangeListener propertyChangeListener;
    private WindowListener windowListener;
    private ToggleIcon closeIcon;
    private ToggleIcon maximizeIcon;
    private ToggleIcon restoreIcon;
    private ToggleIcon minimizeIcon;
    private JButton windowIconButton;
    private JButton closeButton;
    private JButton maximizeToggleButton;
    private JButton minimizeButton;
    private Action closeAction;
    private Action restoreAction;
    private Action maximizeAction;
    private Action minimizeAction;
    private JLabel titleLabel;
    private final Window window;
    private long windowHandle;
    private JMenuBar menuBar;
    private final ContainerListener rootPaneContainerListener = new ContainerListener() {
        @Override
        public void componentAdded(final ContainerEvent e) {
            if (e.getChild() instanceof JLayeredPane) {
                ((JLayeredPane) e.getChild()).addContainerListener(layeredPaneContainerListener);
            }
        }

        @Override
        public void componentRemoved(final ContainerEvent e) {
            if (e.getChild() instanceof JLayeredPane) {
                ((JLayeredPane) e.getChild()).removeContainerListener(layeredPaneContainerListener);
            }
        }
    };
    private int state;

    private Color inactiveBackground;
    private Color inactiveForeground;
    private Color inactiveHover;
    private Color inactiveClick;
    private Color activeBackground;
    private Color activeForeground;
    private Color border;

    public WindowsTitlePane(final JRootPane root, final int decorationStyle, final Window window) {
        this.rootPane = root;
        this.window = window;
        rootPane.addContainerListener(rootPaneContainerListener);
        rootPane.getLayeredPane().addContainerListener(layeredPaneContainerListener);
        state = -1;
        oldResizable = true;
        installSubcomponents(decorationStyle);
        installDefaults();
        setLayout(createLayout());
    }


    private static JButton createButton(final String accessibleName, final Icon icon, final Action action) {
        return createButton(accessibleName, icon, action, false);
    }


    private static JButton createButton(final String accessibleName, final Icon icon, final Action action,
                                        final boolean close) {
        JButton button = new JButton() {
            @Override
            public boolean isRolloverEnabled() {
                return true;
            }
        };
        if (close) {
            button.putClientProperty("JButton.borderless.hover",
                                     UIManager.getColor("Windows.TitlePane.close.rollOverColor"));
            button.putClientProperty("JButton.borderless.click",
                                     UIManager.getColor("Windows.TitlePane.close.clickColor"));
        }
        button.putClientProperty("JButton.noBorderlessOverwrite", true);
        button.setFocusable(false);
        button.setOpaque(false);
        button.setRolloverEnabled(true);
        button.putClientProperty("JButton.variant", "borderlessRectangular");
        button.putClientProperty("paintActive", Boolean.TRUE);
        button.putClientProperty(AccessibleContext.ACCESSIBLE_NAME_PROPERTY, accessibleName);
        button.setAction(action);
        button.setIcon(icon);
        button.setText(null);
        return button;
    }

    public void uninstall() {
        uninstallListeners();
        uninstallDecorations();
        removeAll();
        if (menuBar != null) {
            getRootPane().setJMenuBar(menuBar);
        }
    }

    @Override
    public Insets getWindowSizeAdjustment() {
        //Compensate for the insets of the native window peer that include the decorations.
        Insets insets = window != null && windowHandle != 0
                        ? window.getInsets()
                        : new Insets(0, 0, 0, 0);
        insets.set(-insets.top, -insets.left, -insets.bottom, -insets.right);
        return insets;
    }

    private void uninstallListeners() {
        if (window != null) {
            window.removeWindowListener(windowListener);
            window.removePropertyChangeListener(propertyChangeListener);
        }
        if (rootPane != null) {
            rootPane.removeContainerListener(rootPaneContainerListener);
            rootPane.getLayeredPane().removeContainerListener(layeredPaneContainerListener);
        }
    }

    protected void uninstallDecorations() {
        if (windowHandle != 0) {
            JNIDecorationsWindows.uninstallDecorations(windowHandle);
            windowHandle = 0;
        }
        rootPane.removeContainerListener(rootPaneContainerListener);
        rootPane.getLayeredPane().removeContainerListener(layeredPaneContainerListener);
        if (menuBar != null) {
            menuBar.setPreferredSize(null);
            rootPane.setJMenuBar(menuBar);
        }
    }

    public void install() {
        if (window != null) {
            if (!installDecorations()) return;
            if (window instanceof Frame) {
                titleLabel.setText(((Frame) window).getTitle());
                setState(((Frame) window).getExtendedState());
            } else {
                setState(0);
            }
            if (window instanceof Dialog) {
                titleLabel.setText(((Dialog) window).getTitle());
            }
            determineColors();
            setActive(window.isActive());
            installListeners();
            updateSystemIcon();
        }
    }

    private boolean installDecorations() {
        if (window instanceof Dialog || window instanceof Frame) {
            windowHandle = PointerUtil.getHWND(window);
            if (windowHandle != 0) {
                JNIDecorationsWindows.installDecorations(windowHandle);
                updateResizeBehaviour();
                Color color = window.getBackground();
                JNIDecorationsWindows.setBackground(windowHandle, color.getRed(), color.getGreen(), color.getBlue());
                forceNativeResize();
            } else {
                uninstall();
                return false;
            }
        }
        return true;
    }

    private void forceNativeResize() {
        Rectangle bounds = window.getBounds();
        Dimension size = bounds.getSize();
        Point p = bounds.getLocation();
        if (window.isPreferredSizeSet()) {
            size = window.getPreferredSize();
        } else {
            p.x += size.width / 2;
            p.y += size.height / 2;
        }
        //Resizing triggers #reshapeNativePeer
        window.setSize(size.width, size.height + 1);
        window.setSize(size.width, size.height);
        window.setLocation(p.x - size.width / 2,
                           p.y - size.height / 2);
    }

    private void installListeners() {
        if (window != null) {
            windowListener = createWindowListener();
            window.addWindowListener(windowListener);
            propertyChangeListener = createWindowPropertyChangeListener();
            window.addPropertyChangeListener(propertyChangeListener);
        }
    }

    private WindowListener createWindowListener() {
        return new WindowsTitlePane.WindowHandler();
    }

    private PropertyChangeListener createWindowPropertyChangeListener() {
        return new PropertyChangeHandler();
    }

    private void installSubcomponents(final int decorationStyle) {
        titleLabel = new JLabel();
        titleLabel.setHorizontalAlignment(JLabel.LEFT);
        add(titleLabel);

        createIcons();
        createActions();
        createButtons();
        if (decorationStyle == JRootPane.FRAME) {
            windowIconButton = createWindowIcon();
            add(windowIconButton);
            add(minimizeButton);
            add(maximizeToggleButton);
            add(closeButton);
        } else if (decorationStyle == JRootPane.PLAIN_DIALOG ||
                   decorationStyle == JRootPane.INFORMATION_DIALOG ||
                   decorationStyle == JRootPane.ERROR_DIALOG ||
                   decorationStyle == JRootPane.COLOR_CHOOSER_DIALOG ||
                   decorationStyle == JRootPane.FILE_CHOOSER_DIALOG ||
                   decorationStyle == JRootPane.QUESTION_DIALOG ||
                   decorationStyle == JRootPane.WARNING_DIALOG) {
            add(closeButton);
        }
        addMenuBar(getRootPane().getJMenuBar());
    }

    protected void addMenuBar(final JMenuBar menuBar) {
        if (menuBar != null) {
            this.menuBar = menuBar;
            //Otherwise, a white bar will appear where the menuBar used to be.
            menuBar.setPreferredSize(new Dimension(0, 0));
            menuBar.setOpaque(false);
            add(menuBar);
        }
    }

    protected void removeMenuBar() {
        remove(menuBar);
        menuBar.setOpaque(true);
        menuBar = null;
    }

    private void determineColors() {
        switch (getWindowDecorationStyle()) {
            case JRootPane.ERROR_DIALOG:
                activeBackground = UIManager.getColor("Windows.OptionPane.errorDialog.titlePane.background");
                activeForeground = UIManager.getColor("Windows.OptionPane.errorDialog.titlePane.foreground");
                break;
            case JRootPane.QUESTION_DIALOG:
            case JRootPane.COLOR_CHOOSER_DIALOG:
            case JRootPane.FILE_CHOOSER_DIALOG:
                activeBackground = UIManager.getColor("Windows.OptionPane.questionDialog.titlePane.background");
                activeForeground = UIManager.getColor("Windows.OptionPane.questionDialog.titlePane.foreground");
                break;
            case JRootPane.WARNING_DIALOG:
                activeBackground = UIManager.getColor("Windows.OptionPane.warningDialog.titlePane.background");
                activeForeground = UIManager.getColor("Windows.OptionPane.warningDialog.titlePane.foreground");
                break;
            default: //JRootPane.Frame
                activeBackground = UIManager.getColor("Windows.TitlePane.background");
                activeForeground = UIManager.getColor("Windows.TitlePane.foreground");
                break;
        }
        inactiveBackground = UIManager.getColor("Windows.TitlePane.inactiveBackground");
        inactiveForeground = UIManager.getColor("Windows.TitlePane.inactiveForeground");
        inactiveHover = UIManager.getColor("Windows.TitlePane.inactiveBackgroundHover");
        inactiveClick = UIManager.getColor("Windows.TitlePane.inactiveBackgroundClick");
        border = UIManager.getColor("Windows.TitlePane.borderColor");
    }

    private void installDefaults() {
        setFont(UIManager.getFont("InternalFrame.titleFont", getLocale()));
    }

    protected JButton createWindowIcon() {
        windowIconButton = new JButton();
        windowIconButton.putClientProperty("JButton.noShadowOverwrite", true);
        windowIconButton.setComponentPopupMenu(createMenu());
        windowIconButton.putClientProperty("JButton.variant", "onlyLabel");
        windowIconButton.addActionListener(e -> windowIconButton
            .getComponentPopupMenu()
            .show(windowIconButton,
                  windowIconButton.getWidth() / 2,
                  windowIconButton.getHeight() / 2));
        windowIconButton.setFocusable(false);
        windowIconButton.setBorderPainted(false);
        return windowIconButton;
    }


    private JPopupMenu createMenu() {
        JPopupMenu menu = new JPopupMenu();
        if (getWindowDecorationStyle() == JRootPane.FRAME) {
            addMenuItems(menu);
        }
        return menu;
    }

    private void addMenuItems(final JPopupMenu menu) {
        menu.add(new JMenuItem(restoreAction) {{
            setDisabledIcon(restoreIcon);
        }});
        menu.add(new JMenuItem(minimizeAction) {{
            setDisabledIcon(minimizeIcon);
        }});
        if (Toolkit.getDefaultToolkit().isFrameStateSupported(Frame.MAXIMIZED_BOTH)) {
            menu.add(new JMenuItem(maximizeAction) {{
                setDisabledIcon(maximizeIcon);
            }});
        }
        menu.addSeparator();
        menu.add(new JMenuItem(closeAction) {{
            setDisabledIcon(closeIcon);
        }});
    }

    private void close() {
        Window window = getWindow();
        if (window != null) {
            window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
        }
    }


    private Window getWindow() {
        return window;
    }

    private void minimize() {
        JNIDecorationsWindows.minimize(windowHandle);
    }

    private void maximize() {
        JNIDecorationsWindows.maximize(windowHandle);
    }

    private void restore() {
        JNIDecorationsWindows.restore(windowHandle);
    }

    private void createActions() {
        closeAction = new CloseAction();
        if (getWindowDecorationStyle() == JRootPane.FRAME) {
            minimizeAction = new MinimizeAction();
            restoreAction = new RestoreAction();
            maximizeAction = new MaximizeAction();
        }
    }

    private void createIcons() {
        minimizeIcon = new ToggleIcon(UIManager.getIcon("Windows.TitlePane.minimize.icon"),
                                      UIManager.getIcon("Windows.TitlePane.minimizeInactive.icon"));
        maximizeIcon = new ToggleIcon(UIManager.getIcon("Windows.TitlePane.maximize.icon"),
                                      UIManager.getIcon("Windows.TitlePane.maximizeInactive.icon"));
        restoreIcon = new ToggleIcon(UIManager.getIcon("Windows.TitlePane.restore.icon"),
                                     UIManager.getIcon("Windows.TitlePane.restoreInactive.icon"));
        closeIcon = new ToggleIcon(UIManager.getIcon("Windows.TitlePane.close.icon"),
                                   UIManager.getIcon("Windows.TitlePane.closeInactive.icon"));
    }

    private void createButtons() {
        closeButton = createButton("Close", closeIcon, closeAction, true);
        Icon closePressed = UIManager.getIcon("Windows.TitlePane.closeHover.icon");
        closeButton.setRolloverIcon(closePressed);
        closeButton.setPressedIcon(closePressed);

        if (getWindowDecorationStyle() == JRootPane.FRAME) {
            minimizeButton = createButton("Iconify", minimizeIcon, minimizeAction);
            maximizeToggleButton = createButton("Maximize", maximizeIcon, restoreAction);
        }
    }


    private LayoutManager createLayout() {
        return new TitlePaneLayout();
    }

    private void setActive(final boolean active) {
        closeIcon.setActive(active);
        titleLabel.setForeground(active ? activeForeground : inactiveForeground);
        if (getWindowDecorationStyle() == JRootPane.FRAME) {
            minimizeIcon.setActive(active);
            maximizeIcon.setActive(active);
            closeIcon.setActive(active);
            restoreIcon.setActive(active);
            setButtonActive(minimizeButton, active);
            setButtonActive(maximizeToggleButton, active);
        }
        getRootPane().repaint();
    }

    protected void setButtonActive(final JButton button, final boolean active) {
        if (active) {
            button.putClientProperty("JButton.borderless.hover", null);
            button.putClientProperty("JButton.borderless.click", null);
        } else {
            button.putClientProperty("JButton.borderless.hover", inactiveHover);
            button.putClientProperty("JButton.borderless.click", inactiveClick);
        }
    }

    public void paintComponent(final Graphics g) {
        if (getFrame() != null) {
            setState(getFrame().getExtendedState());
        }
        updateResizeBehaviour();
        Window window = getWindow();
        boolean active = window == null || window.isActive();
        int width = getWidth();
        int height = getHeight();

        Color background = active ? activeBackground : inactiveBackground;

        g.setColor(background);
        g.fillRect(0, 0, width, height);

        if (getWindowDecorationStyle() != JRootPane.NONE && menuBar != null) {
            g.setColor(border);
            g.fillRect(0, height - 1, width, 1);
        }
    }

    public JRootPane getRootPane() {
        return rootPane;
    }

    private Frame getFrame() {
        if (window instanceof Frame) {
            return (Frame) window;
        }
        return null;
    }

    private void setState(final int state) {
        setState(state, false);
    }

    protected void updateResizeBehaviour() {
        boolean res = isResizable(window, rootPane);
        if (oldResizable != res) {
            oldResizable = res;
            JNIDecorationsWindows.setResizable(windowHandle, res);
        }
    }

    private void setState(final int state, final boolean updateRegardless) {
        Window wnd = getWindow();
        if (wnd != null && getWindowDecorationStyle() == JRootPane.FRAME) {
            if (this.state == state && !updateRegardless) {
                return;
            }
            Frame frame = getFrame();

            if (frame != null) {
                JRootPane rootPane = getRootPane();

                if (((state & Frame.MAXIMIZED_BOTH) != 0)
                    && (rootPane.getBorder() == null
                        || (rootPane.getBorder() instanceof UIResource))
                    && frame.isShowing()) {
                    rootPane.setBorder(null);
                }

                if (frame.isResizable()) {
                    if ((state & Frame.MAXIMIZED_BOTH) != 0) {
                        updateToggleButton(restoreAction, restoreIcon);
                        maximizeAction.setEnabled(false);
                        restoreAction.setEnabled(true);
                    } else {
                        updateToggleButton(maximizeAction, maximizeIcon);
                        maximizeAction.setEnabled(true);
                        restoreAction.setEnabled(false);
                    }
                    if (maximizeToggleButton.getParent() == null || minimizeButton.getParent() == null) {
                        add(maximizeToggleButton);
                        add(minimizeButton);
                        revalidate();
                        repaint();
                    }
                    maximizeToggleButton.setText(null);
                } else {
                    maximizeAction.setEnabled(false);
                    restoreAction.setEnabled(false);
                    if (maximizeToggleButton.getParent() != null) {
                        remove(maximizeToggleButton);
                        revalidate();
                        repaint();
                    }
                }
            } else {
                // Not contained in a Frame
                maximizeAction.setEnabled(false);
                restoreAction.setEnabled(false);
                minimizeAction.setEnabled(false);
                remove(maximizeToggleButton);
                remove(minimizeButton);
                revalidate();
                repaint();
            }
            closeAction.setEnabled(true);
            this.state = state;
        }
    }

    protected boolean isResizable(final Window window, final JRootPane rootPane) {
        if (JRootPane.NONE == rootPane.getWindowDecorationStyle()) {
            return false;
        } else {
            if (window instanceof Dialog) {
                return ((Dialog) window).isResizable();
            } else if (window instanceof Frame) {
                return ((Frame) window).isResizable();
            }
        }
        return false;
    }

    private int getWindowDecorationStyle() {
        return getRootPane().getWindowDecorationStyle();
    }

    private void updateToggleButton(final Action action, final Icon icon) {
        maximizeToggleButton.setAction(action);
        maximizeToggleButton.setIcon(icon);
        maximizeToggleButton.setText(null);
    }

    protected boolean isLeftToRight(final Window window) {
        return (window == null) ?
               getRootPane().getComponentOrientation().isLeftToRight() :
               window.getComponentOrientation().isLeftToRight();
    }

    private void updateSystemIcon() {
        Window window = getWindow();
        if (window == null) {
            windowIconButton.setIcon(null);
            return;
        }

        List<Image> icons = window.getIconImages();
        assert icons != null;
        Icon systemIcon;
        if (icons.size() == 0) {
            systemIcon = UIManager.getIcon("Windows.TitlePane.icon");
        } else if (icons.size() == 1) {
            systemIcon = new ScaledIcon(icons.get(0).getScaledInstance(Scale.scaleWidth(ICON_SIZE),
                                                                       Scale.scaleHeight(ICON_SIZE),
                                                                       Image.SCALE_AREA_AVERAGING));
        } else {
            systemIcon = new ScaledIcon(SunToolkit.getScaledIconImage(icons, Scale.scaleWidth(ICON_SIZE),
                                                                      Scale.scaleHeight(ICON_SIZE))
            );
        }
        if (windowIconButton != null) {
            windowIconButton.setIcon(systemIcon);
            SwingUtilities.invokeLater(this::repaint);
        }
    }

    private class CloseAction extends AbstractAction {
        public CloseAction() {
            super("Close", closeIcon);
        }

        public void actionPerformed(final ActionEvent e) {
            close();
        }
    }

    private class MinimizeAction extends AbstractAction {
        public MinimizeAction() {
            //UIManager.getString("Minimize", getLocale())
            super("Minimize", minimizeIcon);
        }

        public void actionPerformed(final ActionEvent e) {
            minimize();
        }
    }

    private class MaximizeAction extends AbstractAction {
        public MaximizeAction() {
            super("Maximize", maximizeIcon);
        }

        public void actionPerformed(final ActionEvent e) {
            maximize();
        }
    }

    private class RestoreAction extends AbstractAction {
        public RestoreAction() {
            super("Restore", restoreIcon);
        }

        public void actionPerformed(final ActionEvent e) {
            restore();
        }
    }

    private boolean hideTitleBar() {
        String title = titleLabel.getText();
        if (title == null) title = "";
        return windowHandle == 0
               || (getWindowDecorationStyle() == JRootPane.NONE
                   && menuBar == null
                   && title.length() == 0);
    }

    private class TitlePaneLayout implements LayoutManager {
        public void addLayoutComponent(final String name, final Component c) {
        }

        public void removeLayoutComponent(final Component c) {
        }

        public Dimension preferredLayoutSize(final Container c) {
            if (hideTitleBar()) return new Dimension(0, 0);
            int size = computeHeight();
            return new Dimension(size + 1, size + 1);
        }

        public Dimension minimumLayoutSize(final Container c) {
            return preferredLayoutSize(c);
        }

        private int computeHeight() {
            if (hideTitleBar()) return 0;
            FontMetrics fm = rootPane.getFontMetrics(getFont());
            int height = fm.getHeight() + 7;
            if (menuBar != null) {
                height = Math.max(height, menuBar.getMinimumSize().height);
            }
            return Math.max(BAR_HEIGHT, height);
        }

        public void layoutContainer(final Container c) {
            if (hideTitleBar()) {
                if (windowIconButton != null) windowIconButton.setBounds(0, 0, 0, 0);
                if (closeButton != null) closeButton.setBounds(0, 0, 0, 0);
                if (minimizeButton != null) minimizeButton.setBounds(0, 0, 0, 0);
                if (maximizeToggleButton != null) maximizeToggleButton.setBounds(0, 0, 0, 0);
                if (titleLabel != null) titleLabel.setBounds(0, 0, 0, 0);
                return;
            }
            boolean leftToRight = isLeftToRight(window);

            int w = getWidth();
            int x;
            int start = 0;
            int y = 0;
            int height = computeHeight();
            int left = 0;
            int right = 0;

            if (windowIconButton != null) {
                int windowButtonWidth = windowIconButton.getIcon() != null ?
                                        Math.max(windowIconButton.getIcon().getIconHeight(),
                                                 windowIconButton.getIcon().getIconWidth()) : ICON_WIDTH;
                windowButtonWidth = Math.min(ICON_WIDTH, windowButtonWidth);
                windowIconButton.setBounds(start + PAD / 2, y, windowButtonWidth, height);
                start += windowButtonWidth + PAD;
                left = start;
            }
            if (menuBar != null) {
                int menuWidth = getPreferredMenuSize().width;
                Insets menuInsets = menuBar.getInsets();
                menuBar.setBounds(start, y, menuWidth, height + menuInsets.bottom);
                start += menuWidth + PAD;
                left += menuWidth;
            }
            x = w;
            if (closeButton != null) {
                x -= BUTTON_WIDTH;
                right += BUTTON_WIDTH;
                closeButton.setBounds(x, y, BUTTON_WIDTH, height);
            }
            if (getWindowDecorationStyle() == JRootPane.FRAME) {
                if (Toolkit.getDefaultToolkit().isFrameStateSupported(Frame.MAXIMIZED_BOTH)) {
                    if (maximizeToggleButton != null && maximizeToggleButton.getParent() != null) {
                        x -= BUTTON_WIDTH;
                        right += BUTTON_WIDTH;
                        maximizeToggleButton.setBounds(x, y, BUTTON_WIDTH, height);
                    }
                    if (minimizeButton != null && minimizeButton.getParent() != null) {
                        x -= BUTTON_WIDTH;
                        right += BUTTON_WIDTH;
                        minimizeButton.setBounds(x, y, BUTTON_WIDTH, height);
                    }
                }
            }
            start = Math.max(start, PAD);
            titleLabel.setBounds(start, 0, x - start - PAD, height);

            if (!leftToRight) {
                mirror(windowIconButton, w);
                mirror(menuBar, w);
                mirror(closeButton, w);
                mirror(minimizeButton, w);
                mirror(maximizeToggleButton, w);
                mirror(titleLabel, w);
                int tmp = left;
                left = right;
                right = tmp;

            }
            JNIDecorationsWindows.updateValues(windowHandle,
                                               Scale.scaleWidth(left),
                                               Scale.scaleWidth(right),
                                               Scale.scaleHeight(height));

        }

        private void mirror(final JComponent component, final int w) {
            if (component != null) {
                component.setLocation(w - component.getX() - component.getWidth(),
                                      component.getY());
            }
        }

        private Dimension getPreferredMenuSize() {
            LayoutManager menuBarLayout = menuBar.getLayout();
            Dimension size = null;
            if (menuBarLayout != null) {
                size = menuBarLayout.preferredLayoutSize(menuBar);
            }
            return (size != null) ? size : menuBar.getPreferredSize();
        }
    }

    protected class PropertyChangeHandler implements PropertyChangeListener {
        public void propertyChange(final PropertyChangeEvent pce) {
            String name = pce.getPropertyName();
            if (KEY_RESIZABLE.equals(name) || KEY_STATE.equals(name)) {
                Frame frame = getFrame();
                if (frame != null) {
                    setState(frame.getExtendedState(), true);
                }
                if (KEY_RESIZABLE.equals(name)) {
                    JNIDecorationsWindows.setResizable(windowHandle, Boolean.TRUE.equals(pce.getNewValue()));
                    getRootPane().repaint();
                }
            } else if (PropertyKey.TITLE.equals(name)) {
                titleLabel.setText(pce.getNewValue() == null ? "" : pce.getNewValue().toString());
                repaint();
            } else if (PropertyKey.COMPONENT_ORIENTATION.equals(name)) {
                revalidate();
                repaint();
            } else if (KEY_ICON_IMAGE.equals(name)) {
                updateSystemIcon();
                revalidate();
                repaint();
            } else if (PropertyKey.BACKGROUND.equals(name) && pce.getNewValue() instanceof Color) {
                Color color = (Color) pce.getNewValue();
                if (color == null) return;
                JNIDecorationsWindows.setBackground(windowHandle, color.getRed(), color.getGreen(), color.getBlue());
            }
        }
    }

    protected class WindowHandler extends WindowAdapter {

        public void windowActivated(final WindowEvent ev) {
            setActive(true);
        }

        public void windowDeactivated(final WindowEvent ev) {
            setActive(false);
        }
    }

}
