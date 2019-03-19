/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.fernice.reflare.internal.impl;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BaseMultiResolutionImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.MultiResolutionImage;
import java.io.IOException;
import java.util.List;
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

    @Override
    public Image getScaledInstance(Image image, int width, int height, int hints) {
        if (image.getWidth(null) == width && image.getHeight(null) == height) {
            return image;
        }

        if (image instanceof MultiResolutionImage) {
            MultiResolutionImage multiResolutionImage = (MultiResolutionImage) image;

            List<Image> images = multiResolutionImage.getResolutionVariants();
            Image base = images.get(0).getScaledInstance(width, height, hints);
            Image variant = images.get(1).getScaledInstance(width * 2, height * 2, hints);

            return new BaseMultiResolutionImage(base, variant);
        } else {
            return image.getScaledInstance(width, height, hints);
        }
    }

    @Override
    public Image getFilteredInstance(Image image, ImageFilter filter) {
        if (image instanceof MultiResolutionImage) {
            MultiResolutionImage multiResolutionImage = (MultiResolutionImage) image;

            List<Image> images = multiResolutionImage.getResolutionVariants();
            Image base = getFilteredInstance0(images.get(0), filter);
            Image variant = getFilteredInstance(images.get(1), filter);

            return new BaseMultiResolutionImage(base, variant);
        } else {
            return getFilteredInstance0(image, filter);
        }
    }

    private Image getFilteredInstance0(Image image, ImageFilter filter) {
        FilteredImageSource prod = new FilteredImageSource(image.getSource(), filter);

        return Toolkit.getDefaultToolkit().createImage(prod);
    }
}
