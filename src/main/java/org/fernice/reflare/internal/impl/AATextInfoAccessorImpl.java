package org.fernice.reflare.internal.impl;

import java.util.Locale;
import java.util.Map;
import org.fernice.reflare.internal.AATextInfoHelper.AATextInfoAccessor;
import sun.swing.SwingUtilities2;

public class AATextInfoAccessorImpl implements AATextInfoAccessor {

    @Override
    public void aaTextInfo(Map<Object, Object> defaults) {
        SwingUtilities2.putAATextInfo(getAATextInfoCondition(), defaults);
    }

    private static boolean getAATextInfoCondition() {
        final String language = Locale.getDefault().getLanguage();
        final String desktop = System.getProperty("sun.desktop");

        final boolean isCjkLocale = (Locale.CHINESE.getLanguage().equals(language) || Locale.JAPANESE.getLanguage().equals(language) ||
                Locale.KOREAN.getLanguage().equals(language));
        final boolean isGnome = "gnome".equals(desktop);
        final boolean isLocal = SwingUtilities2.isLocalDisplay();

        return isLocal && (!isGnome || !isCjkLocale);
    }
}
