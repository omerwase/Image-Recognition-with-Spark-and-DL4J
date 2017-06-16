package sdc.local.etl;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class ImageCropDataset2v2 {
    public static void main (String[] args) {

        System.out.println(System.getProperty("user.dir") + "\n");

        File csvFile = new File(System.getProperty("user.home") + "/dl4j/datav2/dataset2/labels.csv");
        BufferedReader br = null;
        String line = "";
        String csvSplitBy = " ";
        Boolean filter = false;
        String label = "1478019965181415731.jpg";
        int count, fileNo, xmin, ymin, xmax, ymax;
        BufferedImage img = null, newImg = null;
        File imgFile = null, outFile = null;

        java.util.List<String[]>  metaData = new ArrayList<String[]>();

        try {
            br = new BufferedReader(new FileReader(csvFile));

            // Read CSV into metaData
            while ((line = br.readLine()) != null) {
                String[] inputs = line.split(csvSplitBy);
                if (!metaData.add(inputs)) {
                    System.out.printf("Error: " + inputs[0]);
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
                    return o1[0].compareTo(o2[0]);
                }
            });

            // Sort list in inverse order
            //Collections.reverse(metaData);

            count = 0;
            fileNo = 0;
            for (String[] imageData : metaData) {
                count++;
                //if (!filter || imageData[0].contains(label)) // filter = false to disable
                if (Integer.parseInt(imageData[5].trim())==0)
                {
                    // Read file and create croppped files
                    try {
                        //System.out.println(System.getProperty("user.home") + "/dl4j/datav2/dataset2/input/" + imageData[0].trim());
                        imgFile = new File (System.getProperty("user.home") + "/dl4j/datav2/dataset2/input/" + imageData[0].trim());
                        img = ImageIO.read(imgFile);
                        if (img != null) {

                            //count++;
                            for (String data : imageData) {
                                System.out.printf(count + ": " + data + " ");
                            }
                            System.out.printf("\n");

                            xmin = Integer.parseInt(imageData[1]);
                            ymin = Integer.parseInt(imageData[2]);
                            xmax = Integer.parseInt(imageData[3]);
                            ymax = Integer.parseInt(imageData[4]);
                            newImg = croppedImage(img, xmin, ymin, xmax, ymax);

                            int len = imageData[0].length();
                            String type1 = imageData[6];
                            String type2 = "";
                            if (imageData.length > 7){
                                type2 = imageData[7];
                            }
                            outFile = new File(System.getProperty("user.home") + "/dl4j/datav2/dataset2/output/"
                                + getLabel(type1, type2) + "/" + imageData[0].substring(0, len-4).toString()
                                + "_2_" + count + ".jpg");

                            if (newImg != null) {
                                ImageIO.write (newImg, "jpg", outFile);
                            }

                        } else {
                            System.out.println("Null img for " + imageData[0]);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("Count of " + label + ": " + count);

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
        System.out.println("Creating image xmin: " + xmin + ", ymin: " + ymin + ", xmax: "+xmax+", ymax: "+ ymax);
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

    protected static String getLabel (String input, String input2) {
        String label;
        if (input.contains("car")) {
            label = "car";
        } else if (input.contains("truck")) {
            label = "truck";
        } else if (input.contains("biker")) {
            label = "biker";
        } else if (input.contains("pedestrian")) {
            label = "pedestrian";
        } else if (input.contains("trafficLight")) {
            if (input2.contains("GreenLeft")) {
                label = "trafficLightGreenLeft";
            } else if (input2.contains("RedLeft")) {
                label = "trafficLightRedLeft";
            } else if (input2.contains("YellowLeft")) {
                label = "trafficLightYellowLeft";
            } else if (input2.contains("Green")) {
                label = "trafficLightGreen";
            } else if (input2.contains("Red")) {
                label = "trafficLightRed";
            } else if (input2.contains("Yellow")) {
                label = "trafficLightYellow";
            } else {
                label = "trafficLight";
            }
        } else {
            label = "NA";
        }
        return label;
    }
}
