/* Copyright 2016 Michael Sladoje and Mike Schälchli. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package com.example.absensi.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.absensi.R;
import com.example.absensi.common.Constants;
import com.example.absensi.common.Utilities;
import com.example.absensi.model.attendance.today.TodayAttendanceResponse;
import com.example.absensi.presenter.AttendanceContract;
import com.example.absensi.presenter.AttendancePresenter;
import com.example.absensi.view.BaseActivity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.io.File;
import java.util.List;

import ch.zhaw.facerecognitionlibrary.Helpers.CustomCameraView;
import ch.zhaw.facerecognitionlibrary.Helpers.FileHelper;
import ch.zhaw.facerecognitionlibrary.Helpers.MatOperation;
import ch.zhaw.facerecognitionlibrary.PreProcessor.PreProcessorFactory;
import ch.zhaw.facerecognitionlibrary.Recognition.Recognition;
import ch.zhaw.facerecognitionlibrary.Recognition.RecognitionFactory;
import cn.pedant.SweetAlert.SweetAlertDialog;
import kotlin.Unit;

//public class RecognitionActivity extends BaseActivity implements CameraBridgeViewBase.CvCameraViewListener2, AttendanceContract.View {
//    private CustomCameraView mRecognitionView;
//    private static final String TAG = "Recognition";
//    private FileHelper fh;
//    private Recognition rec;
//    private PreProcessorFactory ppF;
//    private ProgressBar progressBar;
//    private boolean front_camera;
//    private boolean night_portrait;
//    private int exposure_compensation;
//
//    private AttendancePresenter presenter = new AttendancePresenter(this, this);
//    private int attendanceId;
//    private int action;
//
//    static {
//        if (!OpenCVLoader.initDebug()) {
//            // Handle initialization error
//        }
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        Log.i(TAG,"called onCreate");
//        super.onCreate(savedInstanceState);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        setContentView(R.layout.recognition_layout);
//        progressBar = (ProgressBar)findViewById(R.id.progressBar);
//
//        fh = new FileHelper();
//        File folder = new File(fh.getFolderPath());
//        if(folder.mkdir() || folder.isDirectory()){
//            Log.i(TAG,"New directory for photos created");
//        } else {
//            Log.i(TAG,"Photos directory already existing");
//        }
//        mRecognitionView = (CustomCameraView) findViewById(R.id.RecognitionView);
//        // Use camera which is selected in settings
//        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
//        front_camera = sharedPref.getBoolean("key_front_camera", true);
////        night_portrait = sharedPref.getBoolean("key_night_portrait", false);
//        night_portrait = true;
//        exposure_compensation = Integer.valueOf(sharedPref.getString("key_exposure_compensation", "20"));
//
//        if (front_camera){
//            mRecognitionView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);
//        } else {
//            mRecognitionView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_BACK);
//        }
//        mRecognitionView.setVisibility(SurfaceView.VISIBLE);
//        mRecognitionView.setCvCameraViewListener(this);
//
//        int maxCameraViewWidth = Integer.parseInt(sharedPref.getString("key_maximum_camera_view_width", "640"));
//        int maxCameraViewHeight = Integer.parseInt(sharedPref.getString("key_maximum_camera_view_height", "480"));
//        mRecognitionView.setMaxFrameSize(maxCameraViewWidth, maxCameraViewHeight);
//
//        Intent intent = getIntent();
//        attendanceId = intent.getIntExtra(Constants.INTENT_ATTENDANCE_ID, 0);
//        action = intent.getIntExtra(Constants.INTENT_ATTENDANCE_ACTION,0);
//    }
//
//    @Override
//    public void onPause()
//    {
//        super.onPause();
//        if (mRecognitionView != null)
//            mRecognitionView.disableView();
//    }
//
//    public void onDestroy() {
//        super.onDestroy();
//        if (mRecognitionView != null)
//            mRecognitionView.disableView();
//    }
//
//    public void onCameraViewStarted(int width, int height) {
//
//        if (night_portrait) {
//            mRecognitionView.setNightPortrait();
//        }
//
//        if (exposure_compensation != 50 && 0 <= exposure_compensation && exposure_compensation <= 100)
//        mRecognitionView.setExposure(exposure_compensation);
//    }
//
//    public void onCameraViewStopped() {
//    }
//
//    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
//        Mat imgRgba = inputFrame.rgba();
//        Mat img = new Mat();
//        imgRgba.copyTo(img);
//        List<Mat> images = ppF.getProcessedImage(img, PreProcessorFactory.PreprocessingMode.RECOGNITION);
//        Rect[] faces = ppF.getFacesForRecognition();
//
//        // Selfie / Mirror mode
//        if(front_camera){
//            Core.flip(imgRgba,imgRgba,1);
//        }
//        if(images == null || images.size() == 0 || faces == null || faces.length == 0 || ! (images.size() == faces.length)){
//            // skip
//            return imgRgba;
//        } else {
//            faces = MatOperation.rotateFaces(imgRgba, faces, ppF.getAngleForRecognition());
//            for(int i = 0; i<faces.length; i++){
//                String label = rec.recognize(images.get(i), "");
//                MatOperation.drawRectangleAndLabelOnPreview(imgRgba, faces[i], rec.recognize(images.get(i), ""), front_camera);
//                if (label.equals(Integer.toString(attendanceId))) {
//                    checkInOrOut(attendanceId);
//                }
//            }
//            return imgRgba;
//        }
//    }
//
//    @Override
//    public void onResume()
//    {
//        super.onResume();
//
//        ppF = new PreProcessorFactory(getApplicationContext());
//
//        final android.os.Handler handler = new android.os.Handler(Looper.getMainLooper());
//        Thread t = new Thread(new Runnable() {
//            public void run() {
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        progressBar.setVisibility(View.VISIBLE);
//                    }
//                });
//                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
////                String algorithm = sharedPref.getString("key_classification_method", getResources().getString(R.string.eigenfaces));
//                String algorithm = getString(R.string.tensorflow);
//                rec = RecognitionFactory.getRecognitionAlgorithm(getApplicationContext(), Recognition.RECOGNITION, algorithm);
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        progressBar.setVisibility(View.GONE);
//                    }
//                });
//            }
//        });
//
//        t.start();
//
//        // Wait until Eigenfaces loading thread has finished
//        try {
//            t.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        mRecognitionView.enableView();
//    }
//
//
//    //contract
//
//    @Override
//    public void checkInResponse(@NotNull TodayAttendanceResponse response) {
//        isLoading(false);
//
//    }
//
//    @Override
//    public void checkOutResponse(@NotNull TodayAttendanceResponse response) {
//        isLoading(false);
//
//    }
//
//    @Override
//    public void getTodayAttendance(@NotNull TodayAttendanceResponse response) {
//
//    }
//
//    private void isLoading(Boolean isLoading) {
//        if (isLoading) {
//            Utilities.INSTANCE.showProgressDialog(this);
//        } else {
//            Utilities.INSTANCE.hideProgressDialog();
//        }
//    }
//
//    @Override
//    public void showError(@NotNull String title, @Nullable String message) {
//        super.showError(title, message);
//        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
//    }
//
//    private void checkInOrOut(int id) {
//        isLoading(true);
//        if (action == Constants.CHECK_IN) {
//            presenter.checkIn(id);
//        } else if (action == Constants.CHECK_OUT) {
//            presenter.checkOut(id);
//        }
//    }
//
//}
