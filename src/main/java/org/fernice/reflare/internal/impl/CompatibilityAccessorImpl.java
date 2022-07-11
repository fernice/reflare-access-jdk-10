package org.fernice.reflare.internal.impl;

import org.fernice.reflare.internal.CompatibilityHelper.CompatibilityAccessor;
import org.fernice.refract.Modules;

public class CompatibilityAccessorImpl implements CompatibilityAccessor {

    @Override
    public void ensureCompatibility() {
        Modules.addExports("java.desktop", "sun.swing", "EVERYONE");
        Modules.addExports("java.desktop", "sun.font", "EVERYONE");
    }
}
