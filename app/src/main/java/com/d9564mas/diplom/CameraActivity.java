package com.d9564mas.diplom;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CameraActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {
    // A tag for log output.
    private static final String TAG = "CameraActivity";
    private CameraBridgeViewBase mCameraView;
    private File mCascadeFile;
    private CascadeClassifier haarCascade;
    private BaseLoaderCallback mLoaderCallback =
            new BaseLoaderCallback(this) {
                @Override
                public void onManagerConnected(final int status){
                    switch (status) {
                        case LoaderCallbackInterface.SUCCESS:
                            Log.d(TAG, "OpenCV loaded successfully");
                            mCameraView.enableView();
                            InputStream is = getResources().openRawResource(R.raw.cascade);
                            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                            mCascadeFile = new File(cascadeDir, "face_frontal.xml");
                            FileOutputStream os;
                            try {
                                os = new FileOutputStream(mCascadeFile);
                                byte[] buffer = new byte[4096];
                                int bytesRead;
                                while ((bytesRead = is.read(buffer)) != -1) {
                                    os.write(buffer, 0, bytesRead);
                                }
                                is.close();
                                os.close();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            haarCascade=new CascadeClassifier(mCascadeFile.getAbsolutePath());
                            if(haarCascade.empty()){
                                Log.i(TAG,"failed to load cascade classifier");
                                haarCascade=null;
                            }
                            break;
                        default:
                            super.onManagerConnected(status);
                            break;
                    }
                }
            };


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        final Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mCameraView = (CameraBridgeViewBase) findViewById(R.id.CameraView);
        mCameraView.setCvCameraViewListener(this);
    }


    @Override
    public void onPause() {
        if (mCameraView != null) {
            mCameraView.disableView();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_11,
                this, mLoaderCallback);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCameraView != null) {
            mCameraView.disableView();
        }
    }

    @Override
    public void onCameraViewStarted(final int width,
                                    final int height) {
    }
    @Override
    public void onCameraViewStopped() {
    }


    private List<MatOfPoint> contours=new ArrayList<>();
    @Override
    public Mat onCameraFrame(final CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        final Mat rgba = inputFrame.rgba();
        final Mat gray = inputFrame.gray();
      //  Imgproc.cvtColor(rgba, rgba, Imgproc.COLOR_RGB2);
      //  Imgproc.threshold(rgba, rgba, 40, 255, Imgproc.THRESH_BINARY);

      // Imgproc.Canny(rgba, mIntermediateMat, 80, 100);

      //  Imgproc.findContours(mIntermediateMat, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
        //imageFilter.apply(rgba,rgba);
       // hierarchy.release();
      //  Imgproc.drawContours(rgba, contours, -1, new Scalar(Math.random() * 255, Math.random() * 255, Math.random() * 255));//, 2, 8, hierarchy, 0, new Point());
        MatOfRect dots = new MatOfRect();
        if(haarCascade!=null){
            haarCascade.detectMultiScale(gray,dots,1.1,2,2,new Size(200,200),new Size());
        }

        Rect[] dotsArray=dots.toArray();
        for(int i=0;i<dotsArray.length;i++){
            Core.rectangle(rgba,dotsArray[i].tl(),dotsArray[i].br(),new Scalar(100),3);
        }

        return rgba;
    }
}

