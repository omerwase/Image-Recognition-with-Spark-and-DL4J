package sdc.local.etl;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class ImageCropDataset1v2 {
    public static void main (String[] args) {


        File csvFile = new File(System.getProperty("user.home") + "/dl4j/datav2/dataset1/labels.csv");
        BufferedReader br = null;
        String line = "";
        String csvSplitBy = ",";
        Boolean filter = false;
        String filterLabel = "1478019965181415731.jpg";
        int count, xmin, ymin, xmax, ymax;
        BufferedImage img = null, newImg = null;
        File imgFile = null, outFile = null;
        int errorCount = 0;
        java.util.List<String[]>  metaData = new ArrayList<String[]>();

        try {
            br = new BufferedReader(new FileReader(csvFile));

            // Read CSV into metaData
            while ((line = br.readLine()) != null) {
                String[] inputs = line.split(csvSplitBy);
                if (!metaData.add(inputs)) {
                    System.out.printf("Error: " + inputs[4]);
                    errorCount++;
                }

                // Print during creation
                /*for (String input : inputs) {
                    System.out.printf(input + " ");
                }
                System.out.printf("\n");*/
            }

            Collections.sort(metaData, new Comparator<String[]>() {
                @Override
                public int compare(String[] o1, String[] o2) {
                    return o1[4].compareTo(o2[4]);
                }
            });

            // Sort list in inverse order
            //Collections.reverse(metaData);

            count = 1;
            for (String[] imageData : metaData) {
                count++;
                if (!filter || imageData[4].contains(filterLabel)) // filter = false to disable
                {
                    // Read file and create croppped files
                    try {
                        imgFile = new File (System.getProperty("user.home") + "/dl4j/datav2/dataset1/input/" + imageData[4].trim());
                        img = ImageIO.read(imgFile);
                        if (img != null) {

                            for (String data : imageData) {
                                System.out.printf(count + ": " + data + " ");
                            }
                            System.out.printf("\n");

                            xmin = Integer.parseInt(imageData[0]);
                            ymin = Integer.parseInt(imageData[1]);
                            xmax = Integer.parseInt(imageData[2]);
                            ymax = Integer.parseInt(imageData[3]);
                            newImg = croppedImage(img, xmin, ymin, xmax, ymax);

                            int len = imageData[4].length();
                            String imageType = imageData[5];



                            if (newImg != null) {
                                outFile = new File(System.getProperty("user.home") + "/dl4j/datav2/dataset1/output/"
                                    + getLabel(imageType) + "/" + imageData[4].substring(0, len-4).toString()
                                    + "_1_" + count + ".jpg");

                                ImageIO.write (newImg, "jpg", outFile);
                                //count++;
                            } else {
                                errorCount++;
                            }

                        } else {
                            System.out.println("Null img for " + imageData[4]);
                            errorCount++;
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("Count: " + count);
            System.out.println("Errors: " + errorCount);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    // Given image and coordinates, returns new cropped image
    protected static BufferedImage croppedImage (BufferedImage image, int xmin, int ymin, int xmax, int ymax) {
        //System.out.println("Creating image xmin: " + xmin + ", ymin: " + ymin + ", xmax: "+xmax+", ymax: "+ ymax);
        try {
            BufferedImage img = image.getSubimage (xmin, ymin, xmax - xmin, ymax - ymin);
            BufferedImage copy = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics g = copy.createGraphics();
            g.drawImage (img, 0, 0, null);
            return copy;
        } catch (RasterFormatException e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }

    }

    protected static String getLabel (String input) {
        String label;
        if (input.contains("Car")) {
            label = "car";
        } else if (input.contains("Truck")) {
            label = "truck";
        } else if (input.contains("Pedestrian")) {
            label = "pedestrian";
        } else {
            label = "NA";
        }
        return label;
    }
}
