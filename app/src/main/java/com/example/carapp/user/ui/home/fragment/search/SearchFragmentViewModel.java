package com.example.carapp.user.ui.home.fragment.search;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.carapp.db.MainDataBase;
import com.example.carapp.db.ProductDao;
import com.example.carapp.db.ProductEntity;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorOperator;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SearchFragmentViewModel extends ViewModel {
    private Context context;
    private ProductDao productDao;
    public MutableLiveData<List<ProductEntity>> searchLiveData;
    protected Interpreter tflite;
    private MappedByteBuffer tfliteModel;
    private TensorImage inputImageBuffer;
    private int imageSizeX;
    private int imageSizeY;
    private TensorBuffer outputProbabilityBuffer;
    private TensorProcessor probabilityProcessor;
    private static final float IMAGE_MEAN = 0.0f;
    private static final float IMAGE_STD = 1.0f;
    private static final float PROBABILITY_MEAN = 0.0f;
    private static final float PROBABILITY_STD = 255.0f;
    private List<String> labels;

    public void init(Activity activity) {
        this.context = activity;
        productDao = MainDataBase.getInstance(context).productDao();
        searchLiveData = new MutableLiveData<>();
        try {
            tflite = new Interpreter(loadModelFile(activity));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void getSearchData(String query) {
        Log.d("Tag", "carShop search  query:  " + query);

        productDao.searchProduct(query).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ProductEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d("TAG", "carShop ShopProductList onSubscribe: ");

                    }

                    @Override
                    public void onNext(List<ProductEntity> productEntityList) {
                        Log.d("TAG", "carShop search onNext: " + productEntityList.size());

                        searchLiveData.setValue(productEntityList);


                    }

                    @Override
                    public void onError(Throwable e) {

                        Log.d("TAG", "carShop search onError: " + e.getMessage());

                    }

                    @Override
                    public void onComplete() {
                        Log.d("Tag", "carShop search : onComplete get research data ");

                    }
                });


    }


    private MappedByteBuffer loadModelFile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd("model.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startoffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startoffset, declaredLength);
    }

    private TensorOperator getPreprocessNormalizeOp() {
        return new NormalizeOp(IMAGE_MEAN, IMAGE_STD);
    }

    private TensorOperator getPostProcessNormalizeOp() {
        return new NormalizeOp(PROBABILITY_MEAN, PROBABILITY_STD);
    }

    private TensorImage loadImage(final Bitmap bitmap) {
        // Loads bitmap into a TensorImage.
        inputImageBuffer.load(bitmap);

        // Creates processor for the TensorImage.
        int cropSize = Math.min(bitmap.getWidth(), bitmap.getHeight());
        // TODO(b/143564309): Fuse ops inside ImageProcessor.
        ImageProcessor imageProcessor =
                new ImageProcessor.Builder()
                        .add(new ResizeWithCropOrPadOp(cropSize, cropSize))
                        .add(new ResizeOp(imageSizeX, imageSizeY, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                        .add(getPreprocessNormalizeOp())
                        .build();
        return imageProcessor.process(inputImageBuffer);
    }

    public void getCameraImageResult(Bitmap bitmap) {
        int imageTensorIndex = 0;
        int[] imageShape = tflite.getInputTensor(imageTensorIndex).shape(); // {1, height, width, 3}
        imageSizeY = imageShape[1];
        imageSizeX = imageShape[2];
        DataType imageDataType = tflite.getInputTensor(imageTensorIndex).dataType();

        int probabilityTensorIndex = 0;
        int[] probabilityShape = tflite.getOutputTensor(probabilityTensorIndex).shape(); // {1, NUM_CLASSES}
        DataType probabilityDataType = tflite.getOutputTensor(probabilityTensorIndex).dataType();

        inputImageBuffer = new TensorImage(imageDataType);
        outputProbabilityBuffer = TensorBuffer.createFixedSize(probabilityShape, probabilityDataType);
        probabilityProcessor = new TensorProcessor.Builder().add(getPostProcessNormalizeOp()).build();

        inputImageBuffer = loadImage(bitmap);

        tflite.run(inputImageBuffer.getBuffer(), outputProbabilityBuffer.getBuffer().rewind());
        try {
            labels = FileUtil.loadLabels(context, "dict.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, Float> labeledProbability =
                new TensorLabel(labels, probabilityProcessor.process(outputProbabilityBuffer))
                        .getMapWithFloatValue();
        float maxValueInMap = (Collections.max(labeledProbability.values()));

        for (Map.Entry<String, Float> entry : labeledProbability.entrySet()) {
            if (entry.getValue() == maxValueInMap) {
                Log.d("TAG", "getCameraImageResult: "+String.valueOf(entry.getKey()));
                if (entry.getKey().equals("steeringWheel")){
                    getCategoryProducts("Steering Wheel");
                }
                if (entry.getKey().equals("engineControl")){
                    getCategoryProducts("Engine Control");
                }
                if (entry.getKey().equals("tailLight")){
                    getCategoryProducts("Tail Light");
                }
            }
        }
    }

    private void getCategoryProducts(String key) {
        productDao.getProductDataFromCamera(key).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ProductEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d("TAG", "carShop ShopProductList onSubscribe: ");

                    }

                    @Override
                    public void onNext(List<ProductEntity> productEntityList) {
                        Log.d("TAG", "carShop search onNext: " + productEntityList.size());

                        searchLiveData.setValue(productEntityList);
                    }

                    @Override
                    public void onError(Throwable e) {

                        Log.d("TAG", "carShop search onError: " + e.getMessage());

                    }
                    @Override
                    public void onComplete() {
                        Log.d("Tag", "carShop search : onComplete get research data ");

                    }
                });





    }
}
