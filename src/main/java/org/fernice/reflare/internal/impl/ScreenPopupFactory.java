package org.fernice.reflare.internal.impl;

import java.awt.Component;
import java.awt.Window;
import javax.swing.JRootPane;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import sun.swing.SwingAccessor;

public class ScreenPopupFactory extends PopupFactory {

    public static final String WINDOW_DRAGGABLE_BACKGROUND = "apple.awt.draggableWindowBackground";

    public static final String WINDOW_ALPHA = "Window.alpha";
    public static final String WINDOW_SHADOW = "Window.shadow";

    public static final String WINDOW_SHADOW_REVALIDATE_NOW = "apple.awt.windowShadow.revalidateNow";

    public static final String WINDOW_FADE_DELEGATE = "apple.awt._windowFadeDelegate";

    static final Float TRANSLUCENT = 248f / 255f;
    static final Float OPAQUE = 1.0f;

    boolean fIsActive = true;

    // Only popups generated with the Aqua LaF turned on will be translucent with shadows
    void setActive(final boolean b) {
        fIsActive = b;
    }

    private static Window getWindow(final Component c) {
        Component w = c;
        while (!(w instanceof Window) && (w != null)) {
            w = w.getParent();
        }
        return (Window) w;
    }

    public Popup getPopup(final Component comp, final Component invoker, final int x, final int y) {
        if (invoker == null) {
            throw new IllegalArgumentException("Popup.getPopup must be passed non-null contents");
        }

        final Popup popup;
        if (fIsActive) {
            popup = SwingAccessor.getPopupFactoryAccessor().getHeavyWeightPopup(this, comp, invoker, x, y);
        } else {
            popup = super.getPopup(comp, invoker, x, y);
        }

        // Make the popup semi-translucent if it is a heavy weight
        // see <rdar://problem/3547670> JPopupMenus have incorrect background
        final Window w = getWindow(invoker);
        if (w == null) {
            return popup;
        }

        if (!(w instanceof RootPaneContainer)) {
            return popup;
        }
        final JRootPane popupRootPane = ((RootPaneContainer) w).getRootPane();

        // we need to set every time, because PopupFactory caches the heavy weight
        // TODO: CPlatformWindow constants?
        if (fIsActive) {
            popupRootPane.putClientProperty(WINDOW_ALPHA, TRANSLUCENT);
            popupRootPane.putClientProperty(WINDOW_SHADOW, Boolean.TRUE);
            popupRootPane.putClientProperty(WINDOW_FADE_DELEGATE, invoker);

            w.setBackground(UIManager.getColor("PopupMenu.translucentBackground"));
            popupRootPane.putClientProperty(WINDOW_DRAGGABLE_BACKGROUND, Boolean.FALSE);
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    popupRootPane.putClientProperty(WINDOW_SHADOW_REVALIDATE_NOW, Math.random());
                }
            });
        } else {
            popupRootPane.putClientProperty(WINDOW_ALPHA, OPAQUE);
            popupRootPane.putClientProperty(WINDOW_SHADOW, Boolean.FALSE);
        }

        return popup;
    }
}