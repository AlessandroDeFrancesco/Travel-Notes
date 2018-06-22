package com.capraraedefrancescosoft.progettomobidev.utilities;

import android.net.Uri;
import android.os.Environment;
import android.util.Base64;

import com.capraraedefrancescosoft.progettomobidev.models.Element;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Gianpaolo Caprara on 9/2/2016.
 */
public class MemoryCacheUtility {

    // ritorna l'uri dell'element se e' in cache, null altrimenti
    public static String getElementContentFromCache(Element element) {
        File folder = getCacheFolder();
        String ext = "";

        switch(element.getType()){
            case IMAGE:
                 ext = ".png";
                break;
            case VIDEO:
                ext = ".mp4";
                break;
            case NOTE:
                return element.getContent();
        }

        // controllo che ci sia in cache
        File file = new File(folder + File.separator + element.getId() + ext);

        if(file.exists())
            return Uri.fromFile(file).toString();
        else
            return null;
    }

    public static Uri cacheImage(byte[] bytes){
        // salvo le foto in una cartella temporanea
        File folder = getCacheFolder();
        String photoName = Arrays.hashCode(bytes) + ".png";
        File photo = new File(folder, photoName);

        Uri uri = null;
        try {
            FileOutputStream fos = new FileOutputStream(photo.getPath());
            fos.write(bytes);
            fos.close();

            uri = Uri.fromFile(photo);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Errore caching imamagine");
        }

        return uri;
    }

    public static Uri cacheVideo(byte[] bytes){
        // salvo i video in una cartella temporanea
        File folder = getCacheFolder();
        String videoName = Arrays.hashCode(bytes) + ".mp4";
        File video = new File(folder, videoName);

        Uri uri = null;
        try {
            FileOutputStream fos = new FileOutputStream(video.getPath());
            fos.write(bytes);
            fos.close();

            uri = Uri.fromFile(video);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Errore caching video");
        }

        return uri;
    }

    public static File getCacheFolder(){
        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "TravelNotesCache");
        if (!folder.exists()) {
            folder.mkdir();
        }

        return folder;
    }


    /* Salva in cache il content dell'element e restituisce l'uri */
    public static String cacheElementContent(Element element) {
        String content = element.getContent();

        switch (element.getType()) {
            case IMAGE:
                byte[] bytes = Base64.decode(element.getContent(), Base64.DEFAULT);
                content = cacheImage(bytes).toString();
                break;
            case VIDEO:
                byte[] bytes2 = Base64.decode(element.getContent(), Base64.DEFAULT);
                content = cacheVideo(bytes2).toString();
                break;
            case NOTE:
                return element.getContent();
        }

        return content;
    }

}
