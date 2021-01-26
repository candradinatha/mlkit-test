package com.example.absensi.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.absensi.R;
import com.example.absensi.view.MainActivity;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ch.zhaw.facerecognitionlibrary.Helpers.FileHelper;
import ch.zhaw.facerecognitionlibrary.Helpers.PreferencesHelper;
import ch.zhaw.facerecognitionlibrary.PreProcessor.PreProcessorFactory;
import ch.zhaw.facerecognitionlibrary.Recognition.Recognition;
import ch.zhaw.facerecognitionlibrary.Recognition.RecognitionFactory;

import static ch.zhaw.facerecognitionlibrary.Helpers.FileHelper.RESULTS_PATH;

public class TestActivity extends AppCompatActivity {
    private static final String TAG = "Test";
    TextView progress;
    Thread thread;

    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        progress = (TextView) findViewById(R.id.progressText);
        progress.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    public void onResume()
    {
        super.onResume();
        final Handler handler = new Handler(Looper.getMainLooper());
        thread = new Thread(new Runnable() {
            public void run() {
                if(!Thread.currentThread().isInterrupted()){
                    PreProcessorFactory ppF = new PreProcessorFactory(getApplicationContext());
                    PreferencesHelper preferencesHelper = new PreferencesHelper(getApplicationContext());
//                    String algorithm = preferencesHelper.getClassificationMethod();
                    String algorithm = getString(R.string.tensorflow);

                    FileHelper fileHelper = new FileHelper();
                    fileHelper.createDataFolderIfNotExsiting();
                    final File[] persons = fileHelper.getTestList();
                    Log.d(TAG, String.valueOf(persons.length));
                    if (persons.length > 0) {
                        Recognition rec = RecognitionFactory.getRecognitionAlgorithm(getApplicationContext(), Recognition.RECOGNITION, algorithm);
                        // total and matches are used to calculate the accuracy afterwards
                        int total = 0;
                        int total_reference = 0;
                        int total_deviation = 0;
                        int matches = 0;
                        int matches_reference = 0;
                        int matches_deviation = 0;
                        List<String> results = new ArrayList<>();
                        results.add("Set;Expected Name;Expected File;Result");
                        Date time_start = new Date();
                        for (File person : persons) {
                            Log.d(TAG, "lebihhhh");
                            if (person.isDirectory()){
                                File[] folders = person.listFiles();
                                for (File folder : folders) {
                                    if (folder.isDirectory()){
                                        File[] files = folder.listFiles();
                                        int counter = 1;
                                        for (File file : files) {
                                            if (FileHelper.isFileAnImage(file)){
                                                Date time_preprocessing_start = new Date();
                                                Mat imgRgba = Imgcodecs.imread(file.getAbsolutePath());
                                                Imgproc.cvtColor(imgRgba, imgRgba, Imgproc.COLOR_BGRA2RGBA);

                                                List<Mat> images = ppF.getProcessedImage(imgRgba, PreProcessorFactory.PreprocessingMode.RECOGNITION);
                                                if (images == null || images.size() > 1) {
                                                    // More than 1 face detected --> cannot use this file for training
                                                    Date time_preprocessing_end = new Date();
                                                    // Subtract time of preprocessing
                                                    time_start.setTime(time_start.getTime() + (time_preprocessing_end.getTime() - time_preprocessing_start.getTime()));
                                                    continue;
                                                } else {
                                                    imgRgba = images.get(0);
                                                }
                                                if (imgRgba.empty()) {
                                                    Date time_preprocessing_end = new Date();
                                                    // Subtract time of preprocessing
                                                    time_start.setTime(time_start.getTime() + (time_preprocessing_end.getTime() - time_preprocessing_start.getTime()));
                                                    continue;
                                                }
                                                // The last token is the name --> Folder name = Person name
                                                String[] tokens = file.getParentFile().getParent().split("/");
                                                final String name = tokens[tokens.length - 1];
                                                tokens = file.getParent().split("/");
                                                final String folderName = tokens[tokens.length - 1];

//                                              fileHelper.saveCroppedImage(imgRgb, ppF, file, name, total);

                                                total++;
                                                if (folderName.equals("reference")) {
                                                    total_reference++;
                                                } else if (folderName.equals("deviation")) {
                                                    total_deviation++;
                                                }

                                                String name_recognized = rec.recognize(imgRgba, name);
                                                results.add(folderName + ";" + name + ";" + file.getName() + ";" + name_recognized);

                                                if (name.equals(name_recognized)) {
                                                    matches++;
                                                    if (folderName.equals("reference")) {
                                                        matches_reference++;
                                                    } else if (folderName.equals("deviation")) {
                                                        matches_deviation++;
                                                    }
                                                }
                                                // Update screen to show the progress
                                                final int counterPost = counter;
                                                final int filesLength = files.length;
                                                progress.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        progress.append("Image " + counterPost + " of " + filesLength + " from " + name + "\n");
                                                    }
                                                });
                                                counter++;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Date time_end = new Date();
                        long duration = time_end.getTime() - time_start.getTime();
                        int durationPerImage = (int) duration / total;
//                        int durationPerImage = 0;
                        int TP = matches;
                        int TN = persons.length * 6;
                        int FP = total - matches;
                        int FN = persons.length * 6 - total;
                        Log.d("TP", String.valueOf(TP));
                        Log.d("TN", String.valueOf(TN));
                        Log.d("FP", String.valueOf(FP));
                        Log.d("FN", String.valueOf(FN));
                        Log.d("matches", String.valueOf(matches));
                        Log.d("total", String.valueOf(total));
                        double accuracy = ((double) TP + (double) TN) / ((double) TP + (double) TN + (double) FN + (double) FP);
//                        double accuracy = (double) matches / (double) total;
                        double precision = ((double) TP) / ((double) TP + (double) FP);
                        double recall = ((double) TP) / ((double) TP + (double) FN);
                        double accuracy_reference = (double) matches_reference / (double) total_reference;
                        double accuracy_deviation = (double) matches_deviation / (double) total_deviation;
                        double robustness = accuracy_deviation / accuracy_reference;
                        Log.d(TAG, "accuracy" + String.valueOf(accuracy));
                        Map<String, ?> printMap = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getAll();
                        saveResultsToFile(printMap, accuracy, accuracy_reference, accuracy_deviation, robustness, durationPerImage, results, precision, recall);
                        rec.saveTestData();

                        final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("accuracy", accuracy);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(intent);
                            }
                        });
                    }
                } else {
                    Thread.currentThread().interrupt();
                }
            }
        });
        thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        thread.interrupt();
    }

    @Override
    protected void onStop() {
        super.onStop();
        thread.interrupt();
    }

    public void saveResultsToFile(Map<String, ?> map, double accuracy, double accuracy_reference, double accuracy_deviation, double robustness, int duration, List<String> results, double precision, double recall){
        String timestamp = new SimpleDateFormat("ddMMyyyyHHmm").format(new java.util.Date());
        createFolderIfNotExisting(RESULTS_PATH);
        String filepath = RESULTS_PATH + "Accuracy_" + String.format("%.2f", accuracy * 100) + "_" + timestamp + ".txt";
        try {
            FileWriter fw = new FileWriter(filepath);
            for (Map.Entry entry : map.entrySet()){
                fw.append(entry.getKey() + ": " + entry.getValue() + "\n");
            }
            fw.append("Accuracy: " + accuracy * 100 + "%\n");
            fw.append("Precision: " +precision * 100 + "%\n");
            fw.append("Recall: " +recall * 100 + "%\n");
            fw.append("Accuracy reference: " + accuracy_reference * 100 + "%\n");
            fw.append("Accuracy deviation: " + accuracy_deviation * 100 + "%\n");
            fw.append("Robustness: " + robustness * 100 + "%\n");
            fw.append("Duration per image: " + duration + "ms\n");
            for (String result : results){
                fw.append(result + "\n");
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createFolderIfNotExisting(String path){
        File folder = new File(path);
        folder.mkdir();
    }
}
