package com.openlearning.ilearn.chat.queries;

import android.app.Application;
import android.os.Environment;


import java.io.File;



public class FirebaseGlobals {

    private static final String CHAT_REFERENCE = "CHAT_REFERENCE/";

    public static boolean makeDocumentDirectory() {

        if (new File(Directory.LOCAL_DOCUMENT_DIRECTORY).exists()) {
            return true;
        }

        File file = new File(Environment.getExternalStorageDirectory().toString() + "/" + Directory.APP_FOLDER_NAME + "/" + Directory.DOCUMENT_FOLDER);
        return file.mkdirs();

    }

    public static boolean makeImageDirectory() {

        if (new File(Directory.LOCAL_IMAGE_DIRECTORY).exists()) {
            return true;
        }

        File file = new File(Environment.getExternalStorageDirectory().toString() + "/" + Directory.APP_FOLDER_NAME + "/" + Directory.IMAGE_FOLDER);
        return file.mkdirs();

    }

    private static boolean makeAppFolder() {

        return new File(Environment.getExternalStorageDirectory().toString() + "/" + Directory.APP_FOLDER_NAME).mkdir();

    }

    public static int getMyLocalID(Application application) {

//        ZCApplication zc = (ZCApplication) application;
//        DbConnection dbC = zc.conn;
//
//        HashMap<String, String> map = new HashMap<>();
//        map.put(Globals.DB_COL_TYPE, Globals.DB_RECORD_TYPE_USER_CREDENTIALS);
//        ArrayList<HashMap<String, String>> records;
//
//        if (dbC != null) {
//
//            if (dbC.isOpen()) {
//
//                dbC.isAvailale();
//
//                records = dbC.retrieveRecords(map);
//
//                if (records.size() > 0) {
//
//                    String strID = records.get(0).get(Globals.DB_COL_SRV_ID);
//
//                    if (strID == null || "".equals(strID)) {
//
//                        return 0;
//                    } else {
//
//                        return Integer.parseInt(strID);
//
//                    }
//                }
//            }
//        }

        return 0;


    }

    public static class Database {

        public static final String QUERY_CHAT_REFERENCE = CHAT_REFERENCE + "MESSAGE_CHAT_REFERENCE";

    }

    public static class Storage {

        public static final String IMAGE_STORAGE_REFERENCE = CHAT_REFERENCE + "IMAGE_CHAT_STORAGE_REFERENCE";
        public static final String DOCUMENT_STORAGE_REFERENCE = CHAT_REFERENCE + "DOCUMENT_CHAT_STORAGE_REFERENCE";

    }

    public static class Directory {

        public static final String APP_FOLDER_NAME = "OMEGE ePathshala";
        public static final String IMAGE_FOLDER = "Images";
        public static final String DOCUMENT_FOLDER = "Documents";

        public static final String LOCAL_IMAGE_DIRECTORY = Environment.getExternalStorageDirectory().toString() + "/" + APP_FOLDER_NAME + "/" + IMAGE_FOLDER;
        public static final String LOCAL_DOCUMENT_DIRECTORY = Environment.getExternalStorageDirectory().toString() + "/" + APP_FOLDER_NAME + "/" + DOCUMENT_FOLDER;

    }

}
