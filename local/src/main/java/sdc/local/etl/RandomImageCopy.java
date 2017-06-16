package sdc.local.etl;

import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.Random;

public class RandomImageCopy {

    public static void main (String[] args) {
        int outNum = 1000;
        String inDir = System.getProperty("user.home") + "sdcdata//sdcdatads2/truck/";
        String outDir = System.getProperty("user.home") + "sdcdata//randomoutput/truck/";

        int count = 0;

        File folder = new File(inDir);
        File[] listOfFiles = folder.listFiles();
        final int[] ints = new Random().ints(0, listOfFiles.length).distinct().limit(outNum).toArray();
        for (int i = 0; i < ints.length; i++) {
            String name = listOfFiles[ints[i]].getName();
            if (listOfFiles[i].isFile() && FilenameUtils.isExtension(name, "jpg")){
                if(CopyFile(name, inDir, outDir) == 1) {
                    count++;
                }
            }
        }
        System.out.println(count + " images copied");
    }

    public static int CopyFile (String fileName, String inDir, String outDir) {
        File sourceFile = new File(inDir + fileName);
        File destFile = new File(outDir + fileName);
		/* verify whether file exist in source location */
        if (!sourceFile.exists()) {
            System.out.println("Error: source file not found: " + fileName);
            return 0;
        }
		/* if file not exist then create one */
        if (!destFile.exists()) {
            try {
                destFile.createNewFile();
            } catch (IOException e) {
                System.out.println("Error: could not create output file: " + fileName);
                e.printStackTrace();
            }
        } else {
            System.out.println("Skip: file already exists: " + fileName);
            return -1;
        }
        FileChannel source = null;
        FileChannel destination = null;
        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            if (destination != null && source != null) {
                destination.transferFrom(source, 0, source.size());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        finally {
            if (source != null) {
                try {
                    source.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
            if (destination != null) {
                try {
                    destination.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        }
        return 1;
    }
}
