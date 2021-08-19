package devx.app.licensee.common.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Debug;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Stack;

import devx.app.licensee.R;

import static android.os.storage.OnObbStateChangeListener.MOUNTED;

public class ImageLoader {
    private HashMap<String, Bitmap> cache=new HashMap<String, Bitmap>();
    private File cacheDir;
    private boolean doScale = true;
    public void checkMemory()
    {
        Runtime rt = Runtime.getRuntime();
        long vmAlloc = rt.totalMemory() - rt.freeMemory();
        long nativeAlloc = Debug.getNativeHeapAllocatedSize();
        Log.i("Storistic", "vmHeap "+formatMemoeryText(rt.totalMemory()));
        Log.i("Storistic", "vmAllocated "+formatMemoeryText(vmAlloc));
        Log.i("Storistic", "nativeAllocated "+formatMemoeryText(nativeAlloc));
        Log.i("Storistic", "totalAllocated "+formatMemoeryText(nativeAlloc+vmAlloc));
        Log.i("Storistic", "nativeHeap "+formatMemoeryText(Debug.getNativeHeapSize()));
    }
    private String formatMemoeryText(long memory) {
        float memoryInMB = memory * 1f / 1024 / 1024;
        return String.format("%.1f MB", memoryInMB);
    }
    public ImageLoader(Context context, String name){
        //Make the background thead low priority. This way it will not affect the UI performance
        photoLoaderThread.setPriority(Thread.NORM_PRIORITY);
        //Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        cacheDir=new File(android.os.Environment.getExternalStorageDirectory()+"/"+context.getString(R.string.app_name),name);
    else
        cacheDir=context.getCacheDir();
        if(!cacheDir.exists())
            cacheDir.mkdirs();
        doScale = true;
    }
    // this constructor is use for actual size of image, we pass doScale value false.
// if do scale value is false  then we are not scaling image
// if do scale value is true then it bahves like a upper constructor i.i do scaling
    public ImageLoader(Context context, String name, boolean doScale){
        this.doScale = doScale;
        //Make the background thead low priority. This way it will not affect the UI performance
        photoLoaderThread.setPriority(Thread.NORM_PRIORITY);
        //Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
        cacheDir=new File(android.os.Environment.getExternalStorageDirectory()+"/"+context.getString(R.string.app_name),name);
    else
        cacheDir=context.getCacheDir();
        if(!cacheDir.exists())
            cacheDir.mkdirs();
    }
    final int stubid=R.color.grey;
    public void DisplayImage(String url, Activity activity, ImageView imageView)
    {
        if(cache.containsKey(url))
            imageView.setImageBitmap(cache.get(url));
        else
        {
            queuePhoto(url, activity, imageView);
            imageView.setImageResource(stubid);
        }
    }
    private void queuePhoto(String url, Activity activity, ImageView imageView)
    {
        //This ImageView may be used for other images before. So there may be some old tasks in the queue. We need to discard them.
        photosQueue.Clean(imageView);
        PhotoToLoad p=new PhotoToLoad(url, imageView);
        synchronized(photosQueue.photosToLoad){
            photosQueue.photosToLoad.push(p);
            photosQueue.photosToLoad.notifyAll();
        }
        //start thread if it's not started yet
        if(photoLoaderThread.getState()==Thread.State.NEW)
            photoLoaderThread.start();
    }
    private Bitmap getBitmap(String url)
    {
        //I identify images by hashcode. Not a perfect solution, good for the demo.
        String filename=String.valueOf(url.hashCode());
        File f=new File(cacheDir, filename);
        //from SD cache
        Bitmap b = decodeFile(f);
        if(b!=null)
            return b;
        //from web
        try {
            Bitmap bitmap=null;
            InputStream is= null;
            try
            {
                is=new URL(url).openStream();
            }catch(FileNotFoundException ex)
            {
                ex.printStackTrace();
                // this code is only for storistic
                String prefixUrl=url.substring(0,url.lastIndexOf('/')+1);
                Log.i("Storistic", "Prefix url: "+prefixUrl);
                String suffixUrl=url.substring(url.lastIndexOf('/')+1);
                Log.i("Storistic", "suffixUrl url: "+suffixUrl);
                Log.i("Storistic", "modified url"+(prefixUrl+ URLEncoder.encode(suffixUrl,"UTF-8")));
                is = new URL(prefixUrl+URLEncoder.encode(suffixUrl,"UTF-8")).openStream();
                // ends
            }
            OutputStream os = new FileOutputStream(f);
            //Utils.CopyStream(is, os);
            os.close();
            bitmap = decodeFile(f);
            return bitmap;
        } catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }
    //decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f){
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);
            //Find the correct scale value. It should be the power of 2.
            final int REQUIREDSIZE=125;
            int width=o.outWidth, height=o.outHeight;
            int scale=1;
            // if doScale value is true then we scale the image
            // otherwise not
            if(doScale)
            {
                while(true){
                    if(width/2<REQUIREDSIZE || height/2<REQUIREDSIZE)
                    break;
                    width/=2;
                    height/=2;
                    scale++;
                }
            }
        /*else
        {
            scale = 2;
        }*/
            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        catch (OutOfMemoryError e){
            clearMemoryCache();
            // call garbage collector
            System.gc();
        }
        return null;
    }
    //Task for the queue
    private class PhotoToLoad
    {
        public String url;
        public ImageView imageView;
        public PhotoToLoad(String u, ImageView i){
            url=u;
            imageView=i;
        }
    }
    PhotosQueue photosQueue=new PhotosQueue();
    public void stopThread()
    {
        photoLoaderThread.interrupt();
    }
    //stores list of photos to download
    class PhotosQueue
    {
        private Stack<PhotoToLoad> photosToLoad=new Stack<PhotoToLoad>();
        //removes all instances of this ImageView
        public void Clean(ImageView image)
        {
            for(int j=0 ;j<photosToLoad.size();){
                if(photosToLoad.get(j).imageView==image)
                    photosToLoad.remove(j);
                else
                    ++j;
            }
        }
    }
    class PhotosLoader extends Thread {
        public void run() {
            try {
                while(true)
                {
                    //thread waits until there are any images to load in the queue
                    if(photosQueue.photosToLoad.size()==0)
                        synchronized(photosQueue.photosToLoad){
                            photosQueue.photosToLoad.wait();
                        }
                    if(photosQueue.photosToLoad.size()!=0)
                    {
                        PhotoToLoad photoToLoad;
                        synchronized(photosQueue.photosToLoad){
                            photoToLoad=photosQueue.photosToLoad.pop();
                        }
                        Bitmap bmp=getBitmap(photoToLoad.url);
                        cache.put(photoToLoad.url, bmp);
                        checkMemory();
                        cache.put(photoToLoad.url, bmp);
                        checkMemory();
                        if(((String)photoToLoad.imageView.getTag()).equals(photoToLoad.url)){
                            BitmapDisplayer bd=new BitmapDisplayer(bmp, photoToLoad.imageView);
                            Activity a=(Activity)photoToLoad.imageView.getContext();
                            a.runOnUiThread(bd);
                        }
                        bmp = null;
                    }
                    if(Thread.interrupted())
                        break;
                }
            } catch (InterruptedException e) {
                //allow thread to exit
            }
        }
    }
    PhotosLoader photoLoaderThread=new PhotosLoader();
    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable
    {
        Bitmap bitmap;
        ImageView imageView;
        public BitmapDisplayer(Bitmap b, ImageView i){bitmap=b;imageView=i;}
        public void run()
        {
            if(bitmap!=null)
            {
                imageView.setImageBitmap(bitmap);
                bitmap = null;
            }
            else
                imageView.setImageResource(stubid);
        }
    }
    public void clearCache() {
        //clear memory cache
        cache.clear();
        //clear SD cache
        File[] files=cacheDir.listFiles();
        for(File f:files)
            f.delete();
    }
    public void clearMemoryCache()
    {
        cache.clear();
    }
    // not using this method
    public void recyleCacheBitmap()
    {
        for(int i = 0; i<cache.size();i++)
        {
            try
            {
                cache.get(i).recycle();
            }
            catch(NullPointerException ex)
            {
            }
        }
        clearMemoryCache();
    }
}
