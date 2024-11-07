package com.sales.utils;


import com.sales.exceptions.MyException;
import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;



public class UploadImageValidator {
        public static boolean isValidImage(MultipartFile imageFile, int minWidth, int minHeight, int maxWidth, int maxHeight, double[] allowedAspectRatios, String[] allowedFormats) {
            try {

                BufferedImage image =ImageIO.read(imageFile.getInputStream());
                if(image == null){
                    throw new MyException("Your image file is corrupted or modified with another extension. We accept only png,jpg,jpeg image due to some security reasons.");
                }
                int width = image.getWidth();
                int height = image.getHeight();

                System.out.println("image width : "+width + " : "+height);
                // Check dimensions
                if (width < minWidth || width > maxWidth || height < minHeight || height > maxHeight) {
                    if(width < minWidth || height < minHeight) {
                        throw new MyException("Your image file is too small.");
                    }else {
                        throw new MyException("Your image file is too large.");
                    }
                }

                // Check aspect ratio
                double aspectRatio = (double) width / height;
                System.out.println("image aspectRatio : "+aspectRatio);
                boolean validAspectRatio = false;
                for (double allowedRatio : allowedAspectRatios) {
                    if (Math.abs(aspectRatio - allowedRatio) < 0.01) {
                        validAspectRatio = true;
                        break;
                    }
                }
                if (!validAspectRatio) {
                    return false;
                }

                // Check file format
                String format = imageFile.getOriginalFilename().substring(imageFile.getOriginalFilename().lastIndexOf(".") + 1);
                boolean validFormat = false;
                for (String allowedFormat : allowedFormats) {
                    if (format.equalsIgnoreCase(allowedFormat)) {
                        validFormat = true;
                        break;
                    }
                }
                if (!validFormat) {
                    return false;
                }

                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

    public static boolean hasWhiteBackground(File file) {
        ImagePlus imp = IJ.openImage(file.getAbsolutePath());
        ImageProcessor ip = imp.getProcessor();
        boolean isWhite = false;
        int width = ip.getWidth();
        int height = ip.getHeight();
        int[] pixels = (int[]) ip.getPixels();

        int whitePixelCount = 0;
        for (int i = 0; i < pixels.length; i++) {
            int pixel = pixels[i];
            int r = (pixel >> 16) & 0xff;
            int g = (pixel >> 8) & 0xff;
            int b = pixel & 0xff;

            if (r > 230 && g > 230 && b > 230) {
                whitePixelCount++;
            }
        }

        double whitePixelPercentage = (double) whitePixelCount / (width * height) * 100;
        if (whitePixelPercentage > 30) {
            isWhite = true;
        }
        if(whitePixelPercentage > 90){
            throw new MyException("Item not visible clearly.");
        }
        return isWhite;
    }



    }


