package com.sales.utils;



import com.sales.exceptions.MyException;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;





public class UploadImageValidator {
        public static boolean isValidImage(MultipartFile imageFile, int minWidth, int minHeight, int maxWidth, int maxHeight, double[] allowedAspectRatios, String[] allowedFormats) {
            try {

                BufferedImage image =ImageIO.read(imageFile.getInputStream());
                if(image == null){
                    throw new MyException("Your file is corrupted or modified with another extension. We accept only png,jpg,jpeg image due to some security reasons.");
                }
                int width = image.getWidth();
                int height = image.getHeight();

                System.out.println("image width : "+width + " : "+height);
                // Check dimensions
                if (width < minWidth || width > maxWidth || height < minHeight || height > maxHeight) {
                    return false;
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
    }


