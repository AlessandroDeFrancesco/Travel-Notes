package com.capraraedefrancescosoft.progettomobidev.utilities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.capraraedefrancescosoft.progettomobidev.R;
import com.capraraedefrancescosoft.progettomobidev.models.Element;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Locale;

/**
 * Created by Ianfire on 01/07/2016.
 */
public class ContentTypesUtility {

    private final static int DEFAULT_IMAGE_HEIGHT = 720;

    public interface CacheLoading {
        void onCachingComplete(Uri uri);
        void onCacheUpdate(int percentuale);
        void onCacheFailed();
    }

    public static boolean checkIsVideo(Uri uri, Context context) {
        String mimeType = getMimeType(uri, context);
        Log.d("Type", mimeType);
        return mimeType != null && mimeType.toLowerCase(Locale.ENGLISH).indexOf("video") == 0;
    }

    public static boolean checkIsImage(Uri uri, Context context) {
        String mimeType = getMimeType(uri, context);
        Log.d("Type", mimeType);
        return mimeType != null && mimeType.toLowerCase(Locale.ENGLISH).indexOf("image") == 0;
    }

    public static String getMimeType(Uri uri, Context context) {
        return context.getContentResolver().getType(uri);
    }

    /* Converte il video/immagine di un Element (che e' memorizzato in Uri) nella sua rappresentazione binaria e poi in Stringa
    * cosi da poterla inviare tramite json*/
    public static Element convertElementContentToBase64(Element element, ContentResolver contentResolver) {
        Element newElement = new Element(element);

        try {
            switch (newElement.getType()) {
                case IMAGE:
                    InputStream inputStream = contentResolver.openInputStream(Uri.parse(newElement.getContent()));
                    newElement.setContent(imageToByte(inputStream));
                    break;
                case VIDEO:
                    InputStream inputStream2 = contentResolver.openInputStream(Uri.parse(newElement.getContent()));
                    newElement.setContent(videoToByte(inputStream2));
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Errore convertElementContentToBase64");
        }

        return newElement;
    }

    private static String imageToByte(InputStream inputStream) {
        Bitmap bm = BitmapFactory.decodeStream(inputStream);
        System.out.println(bm.getHeight() + " " + bm.getWidth());
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        // scaling in png e compressione dell'immagine
        float aspectRatio = (float) bm.getHeight() / (float) bm.getWidth();
        int width = Math.round((float) DEFAULT_IMAGE_HEIGHT / aspectRatio);
        bm = Bitmap.createScaledBitmap(bm, width, DEFAULT_IMAGE_HEIGHT, false);
        bm.compress(Bitmap.CompressFormat.PNG, 100, bs);
        System.out.println(bm.getHeight() + " " + bm.getWidth());

        byte[] b = bs.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    private static String videoToByte(InputStream inputStream) {
        try {
            byte[] b = IOUtils.toByteArray(inputStream);
            return Base64.encodeToString(b, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Errore video to byte");
        }
        return "";
    }

    public static void compressAndCacheImage(final Uri data, final Context context, final CacheLoading cacheLoading) {
        Thread thread = new Thread() {
            public void run() {
                try {
                    Bitmap bm = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(data));
                    ByteArrayOutputStream bs = new ByteArrayOutputStream();
                    // scaling e compressione in jpeg dell'immagine
                    float aspectRatio = (float) bm.getHeight() / (float) bm.getWidth();
                    int width = Math.round((float) DEFAULT_IMAGE_HEIGHT / aspectRatio);
                    bm = Bitmap.createScaledBitmap(bm, width, DEFAULT_IMAGE_HEIGHT, false);
                    bm.compress(Bitmap.CompressFormat.PNG, 100, bs);
                    // salvo in memoria la nuova immagine e restituisco l'uri
                    cacheLoading.onCachingComplete(MemoryCacheUtility.cacheImage(bs.toByteArray()));
                } catch (Exception e){
                    e.printStackTrace();
                    cacheLoading.onCacheFailed();
                    System.out.println("Errore compressione immagine");
                }
            }
        };
        thread.start();
    }

    public static void compressAndCacheVideo(Uri videoUri, final Context context, final CacheLoading cacheLoading) {
        try {
            final String inputVideo = copyVideo(videoUri, context.getContentResolver());
            final String outputVideo = MemoryCacheUtility.getCacheFolder() + File.separator + "temp2.mp4";

            // durata del video in secondi per il calcolo del progresso
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(inputVideo);
            final int durationInSec = (int) (Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION))/1000);
            System.out.println("Durata Video: " + durationInSec);
            // scalo il video a 360 di larghezza, mantenendo il ratio
            String[] cmd = ("-y -i " + inputVideo + " -filter:v scale=360:-1 -crf 30 " + outputVideo).split(" ");
            FFmpeg.getInstance(context).execute(cmd, new FFmpegExecuteResponseHandler() {
                @Override
                public void onSuccess(String message) {
                    try {
                        // salvo in cache il nuovo video e restituisco l'uri
                        File videoTemp = new File(outputVideo);
                        byte[] bytes = getBytes(new FileInputStream(videoTemp));
                        cacheLoading.onCachingComplete(MemoryCacheUtility.cacheVideo(bytes));
                    } catch (IOException e) {
                        e.printStackTrace();
                        cacheLoading.onCacheFailed();
                    }
                }

                @Override
                public void onProgress(String message) {
                    if(message.contains("frame") && message.contains("time") && message.contains("bitrate")) {
                        int seconds = getTimeInSecFromUpdateMessage(message);
                        cacheLoading.onCacheUpdate((seconds * 100 / durationInSec));
                    }
                }

                @Override
                public void onFailure(String message) {
                    cacheLoading.onCacheFailed();
                }

                @Override
                public void onStart() {

                }

                @Override
                public void onFinish() {
                    // elimino file temporanei
                    new File(inputVideo).delete();
                    new File(outputVideo).delete();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Errore compressione immagine");
        }
    }

    private static int getTimeInSecFromUpdateMessage(String message){
        String time = message.substring(message.indexOf("time") + 5, message.indexOf("bitrate") - 1);
        String[] hms = time.split(":");
        float ore = Float.parseFloat(hms[0]);
        float minuti = Float.parseFloat(hms[1]);
        float secondi = Float.parseFloat(hms[2]);

        return (int) (secondi + minuti*60 + ore*60*60);
    }

    private static String copyVideo(Uri videoUri, ContentResolver contentResolver) {
        String destinationFilename = MemoryCacheUtility.getCacheFolder() + File.separator + "temp.mp4";
        InputStream in = null;
        OutputStream out = null;
        try {
            in = contentResolver.openInputStream(videoUri);
            out = new FileOutputStream(destinationFilename);

            IOUtils.copy(in, out);
            System.out.println("Copia finita");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }

        return destinationFilename;
    }

    public static int getHashFromUri(Uri data, ContentResolver contentResolver) {
        try {
            InputStream iStream = contentResolver.openInputStream(data);
            byte[] bytes = getBytes(iStream);
            return Arrays.hashCode(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

}
