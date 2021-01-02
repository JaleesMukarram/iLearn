package com.openlearning.ilearn.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;

import com.openlearning.ilearn.R;
import com.openlearning.ilearn.databinding.ViewFileAttachmentsDialogueBinding;
import com.openlearning.ilearn.databinding.ViewSelectSingleFileBinding;
import com.openlearning.ilearn.databinding.ViewSelectSingleImageBinding;
import com.openlearning.ilearn.databinding.ViewSelectSinglePdfBinding;
import com.openlearning.ilearn.interfaces.ActivityHooks;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FilePickerUtils implements ActivityHooks {

    public static final int MODE_IMAGES_ONLY = 1;
    public static final int MODE_ALL = 4;

    public static final int INDEX_FILE_IMAGES = 0;
    public static final int INDEX_FILE_PDF = 1;
    public static final int INDEX_FILE_DOCUMENTS = 2;

    private static final String TAG = "FilePickerTAG";


    private AlertDialog.Builder builder;
    private Dialog mDialogue;


    private ViewFileAttachmentsDialogueBinding mBinding;

    private boolean filesLoaded;
    private boolean shouldStartItself;

    private List<List<File>> filesList;

    private final Activity homeScreen;
    private final int currentMode;

    private final FilePickerCallback filePickerInterface;


    public FilePickerUtils(Activity homeScreen, int currentMode, FilePickerCallback filePickerInterface) {

        this.homeScreen = homeScreen;
        this.currentMode = currentMode;
        this.filePickerInterface = filePickerInterface;
        Log.d(TAG, "FilePickerObject Created");

    }

    @Override
    public void callHooks() {

        filesList = new ArrayList<>();
        new BackgroundFilesLoading().start();

    }

    @Override
    public void handleIntent() {

    }

    @Override
    public void init() {

        mBinding = DataBindingUtil.inflate(homeScreen.getLayoutInflater(), R.layout.view_file_attachments_dialogue, null, false);

        View mDialogueView = mBinding.getRoot();

        mBinding.IVDialogueClose.setOnClickListener(view -> {

            if (mDialogue != null) {
                mDialogue.cancel();
            }
        });

        builder = new AlertDialog.Builder(homeScreen);
        builder.setView(mDialogueView);

    }

    @Override
    public void process() {


    }

    @Override
    public void loaded() {

    }

    public void showNow() {

        if (filesLoaded) {

            init();
            setGridView();

            mDialogue = builder.create();
            mDialogue.show();

            Log.d(TAG, "showNow: images size: "+filesList.get(INDEX_FILE_IMAGES).size());

        } else {

            shouldStartItself = true;
        }

    }

    private String getSize(long length) {

        int kb = 1024;
        int mb = kb * 1000;

        if (length > mb) {

            return new DecimalFormat("##.##").format(1f * length / mb) + "MB";
        } else if (length > kb) {

            return new DecimalFormat("##.##").format(1f * length / kb) + "KB";
        } else {

            return length + "B";
        }

    }

    private void setGridView() {

        mBinding.PBRWorking.setVisibility(View.GONE);

        String[] options;

        if (currentMode == MODE_ALL) {

            options = new String[]{"Images", "Pdf", "Documents and others"};


        } else if (currentMode == MODE_IMAGES_ONLY) {

            options = new String[]{"Images"};

        } else {

            options = new String[]{"Images", "Pdf", "Documents and others"};

        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(homeScreen, android.R.layout.simple_spinner_dropdown_item, options);
        mBinding.SPRSelectType.setAdapter(arrayAdapter);

        mBinding.SPRSelectType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i == 0) {

                    mBinding.GVDialogueImageSelection.setAdapter(new GridViewAdapter(GridViewAdapter.MODE_IMAGE));
                    mBinding.GVDialogueImageSelection.setNumColumns(4);

                } else if (i == 1) {
                    mBinding.GVDialogueImageSelection.setAdapter(new GridViewAdapter(GridViewAdapter.MODE_PDF));
                    mBinding.GVDialogueImageSelection.setNumColumns(1);

                } else {
                    mBinding.GVDialogueImageSelection.setAdapter(new GridViewAdapter(GridViewAdapter.MODE_DOCUMENT));
                    mBinding.GVDialogueImageSelection.setNumColumns(1);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mBinding.GVDialogueImageSelection.setAdapter(new GridViewAdapter(GridViewAdapter.MODE_IMAGE));

    }

    private class GridViewAdapter extends BaseAdapter {

        public static final int MODE_PDF = 4;
        public static final int MODE_DOCUMENT = 8;
        private static final int MODE_IMAGE = 2;

        private final int currentMode;

        public GridViewAdapter(int currentMode) {
            this.currentMode = currentMode;
        }

        @Override
        public int getCount() {

            if (currentMode == MODE_IMAGE) {
                return filesList.get(0).size();
            } else if (currentMode == MODE_PDF) {
                return filesList.get(1).size();
            } else if (currentMode == MODE_DOCUMENT) {
                return filesList.get(2).size();
            }

            return 0;
        }

        @Override
        public Object getItem(int position) {

            if (currentMode == MODE_IMAGE) {

                return filesList.get(0).get(position);

            } else if (currentMode == MODE_PDF) {

                return filesList.get(1).get(position);

            } else if (currentMode == MODE_DOCUMENT) {

                return filesList.get(2).get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View view = null;

            if (currentMode == MODE_IMAGE) {

                ViewSelectSingleImageBinding binding = DataBindingUtil.inflate(homeScreen.getLayoutInflater(), R.layout.view_select_single_image, null, false);

                view = binding.getRoot();
                setImageViewMode(position, binding);


            } else if (currentMode == MODE_PDF) {

                ViewSelectSinglePdfBinding binding = DataBindingUtil.inflate(homeScreen.getLayoutInflater(), R.layout.view_select_single_pdf, null, false);

                view = binding.getRoot();
                setPDFViewMode(position, binding);

            } else if (currentMode == MODE_DOCUMENT) {

                ViewSelectSingleFileBinding binding = DataBindingUtil.inflate(homeScreen.getLayoutInflater(), R.layout.view_select_single_file, null, false);
                view = binding.getRoot();
                setDocumentViewMode(position, binding);
            }

            return view;
        }

        private void setImageViewMode(final int position, ViewSelectSingleImageBinding mBinding) {


            mBinding.IVGridRowSingleImage.setOnClickListener(view -> {

                filePickerInterface.onFileSelected(filesList.get(INDEX_FILE_IMAGES).get(position), INDEX_FILE_IMAGES);
                mDialogue.cancel();
            });

            mBinding.setImageFile(filesList.get(INDEX_FILE_IMAGES).get(position));

        }

        private void setPDFViewMode(final int position, ViewSelectSinglePdfBinding binding) {

            binding.TVPDFSize.setText(getSize(filesList.get(INDEX_FILE_PDF).get(position).length()));

            mBinding.getRoot().setOnClickListener(view -> {

                filePickerInterface.onFileSelected(filesList.get(INDEX_FILE_PDF).get(position), INDEX_FILE_PDF);
                mDialogue.cancel();

            });

            binding.TVSelectDocument.setText(filesList.get(INDEX_FILE_PDF).get(position).getName());


        }

        private void setDocumentViewMode(final int position, ViewSelectSingleFileBinding binding) {

            binding.TVPDFSize.setText(getSize(filesList.get(INDEX_FILE_DOCUMENTS).get(position).length()));

            binding.getRoot().setOnClickListener(view -> {

                filePickerInterface.onFileSelected(filesList.get(INDEX_FILE_DOCUMENTS).get(position), INDEX_FILE_DOCUMENTS);
                mDialogue.cancel();

            });

            binding.TVSelectDocument.setText(filesList.get(INDEX_FILE_DOCUMENTS).get(position).getName());

        }
    }

    private class BackgroundFilesLoading extends Thread {

        private final List<File> imageFiles;
        private final List<File> pdfFiles;
        private final List<File> documentsFiles;

        private BackgroundFilesLoading() {

            imageFiles = new ArrayList<>();
            pdfFiles = new ArrayList<>();
            documentsFiles = new ArrayList<>();

        }

        @Override
        public void run() {

            getAllFilesForSelecting(Environment.getExternalStorageDirectory());
            sortFiles();
            onFilesLoaded();

        }

        private void getAllFilesForSelecting(File externalStorageDirectory) {

            File[] files = externalStorageDirectory.listFiles();

            if (files != null) {

                for (File file : files) {

                    if (file.isDirectory()) {

                        if (file.getName().contains(".")) {
                            continue;
                        }

                        getAllFilesForSelecting(file);

                    } else {


                        if (file.getName().endsWith(".jpg") || file.getName().endsWith(".png") || file.getName().endsWith(".jpeg")) {

                            imageFiles.add(file);

                        } else if (currentMode == MODE_ALL) {


                            if (file.getName().endsWith(".pdf")) {

                                pdfFiles.add(file);

                            } else {

                                if (file.getName().endsWith(".doc") ||
                                        file.getName().endsWith(".docx") ||
                                        file.getName().endsWith(".ppt") ||
                                        file.getName().endsWith(".pptx") ||
                                        file.getName().endsWith(".xls") ||
                                        file.getName().endsWith(".xlss") ||
                                        file.getName().endsWith(".zip") ||
                                        file.getName().endsWith(".rar") ||
                                        file.getName().endsWith(".wav") ||
                                        file.getName().endsWith(".mp3") ||
                                        file.getName().endsWith(".txt") ||
                                        file.getName().endsWith(".3gp") ||
                                        file.getName().endsWith(".mpg") ||
                                        file.getName().endsWith(".mp4") ||
                                        file.getName().endsWith(".avi") ||
                                        file.getName().endsWith(".mpeg")) {

                                    documentsFiles.add(file);

                                }

                            }

                        }
                    }
                }
            }
        }

        private void sortFiles() {

            Log.d(TAG, "Total Image files: " + imageFiles.size());

            Collections.sort(imageFiles, (o1, o2) -> Long.compare(o2.lastModified(), o1.lastModified()));
            Collections.sort(pdfFiles, (o1, o2) -> Long.compare(o2.lastModified(), o1.lastModified()));
            Collections.sort(documentsFiles, (o1, o2) -> Long.compare(o2.lastModified(), o1.lastModified()));
        }

        protected void onFilesLoaded() {

            Log.d(TAG, "Files loaded successfully");

            filesList.add(imageFiles);

            if (currentMode == MODE_ALL) {

                filesList.add(pdfFiles);
                filesList.add(documentsFiles);

            }

            filesLoaded = true;

            if (shouldStartItself) {

                Log.d(TAG, "Self Starting");

                if (homeScreen != null) {

                    homeScreen.runOnUiThread(FilePickerUtils.this::showNow);
                }
            }
        }
    }

    public interface FilePickerCallback {

        void onFileSelected(File file, int type);
    }


}
