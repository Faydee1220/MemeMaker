package com.teamtreehouse.mememaker.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.teamtreehouse.mememaker.MemeMakerApplicationSettings;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtilities {

    public static void saveAssetImage(Context context, String assetName) {
        // get the internal storage folder
        File fileDirectory = getFileDirectory(context);

        // get file in the internal storage folder with name = assetName
        File fileToWrite = new File(fileDirectory, assetName);

        AssetManager assetManager = context.getAssets();

        try {
            // open input stream to read from the assets file
            InputStream inputStream = assetManager.open(assetName);

            // open output stream to write to the internal storage file
            FileOutputStream fos = new FileOutputStream(fileToWrite);

            // copy function from inputStream to outputStream
            copyFile(inputStream, fos);

            // close the streams after finish copying
            inputStream.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File getFileDirectory(Context context) {
        MemeMakerApplicationSettings settings = new MemeMakerApplicationSettings(context);
        String storageType = settings.getStoragePreference();
//        String storageType = StorageType.INTERNAL;
//        String storageType = StorageType.PRIVATE_EXTERNAL;
//        String storageType = StorageType.PUBLIC_EXTERNAL;

        if (storageType.equals(StorageType.INTERNAL)) {
            return context.getFilesDir();
        }
        else {
            if (isExternalStorageAvailable()) {
                if (storageType.equals(StorageType.PRIVATE_EXTERNAL)) {
                    return context.getExternalFilesDir(null);
                }
                else {
                    return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                }
            }
            else {
                return context.getFilesDir();
            }
        }

    }

    public static boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    public static File[] listFiles(Context context) {
        File fileDirectory = getFileDirectory(context);
        File[] filteredFiles = fileDirectory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.getAbsolutePath().contains(".jpg")) {
                    return true;
                }
                else {
                    return false;
                }
            }
        });
        return filteredFiles;
    }

    public static Uri saveImageForSharing(Context context, Bitmap bitmap,  String assetName) {
        File fileToWrite = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), assetName);

        try {
            FileOutputStream outputStream = new FileOutputStream(fileToWrite);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return Uri.fromFile(fileToWrite);
        }
    }


    public static void saveImage(Context context, Bitmap bitmap, String name) {
        File fileDirectory = getFileDirectory(context);
        File fileToWrite = new File(fileDirectory, name);

        try {
            FileOutputStream outputStream = new FileOutputStream(fileToWrite);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
