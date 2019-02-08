/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.internal.impl;

import java.awt.Image;
import java.awt.image.BaseMultiResolutionImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.fernice.reflare.internal.ImageHelper.ImageAccessor;

public class ImageAccessorImpl implements ImageAccessor {

    @Override
    public Image getMultiResolutionImageResource(String resource) throws IOException {
        var image = ImageIO.read(ImageAccessorImpl.class.getResourceAsStream(resource));

        var resource2x = resource.substring(0, resource.lastIndexOf('.')) + "@2x" + resource.substring(resource.lastIndexOf("."));

        var input2x = ImageAccessorImpl.class.getResourceAsStream(resource2x);

        if (input2x != null) {
            var image2x = ImageIO.read(input2x);

            return new BaseMultiResolutionImage(image, image2x);
        } else {
            return image;
        }
    }
}
