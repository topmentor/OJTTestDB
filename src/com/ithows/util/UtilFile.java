package com.ithows.util;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.zip.*;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.FileSystemUtils;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;

/**
 *
 * @author ksyuser
 */
public class UtilFile {

//         System.out.println("1 <<<<<< "  + FilenameUtils.getExtension(fileName));   // 확장자
//         System.out.println("2 <<<<<< "  + FilenameUtils.getBaseName(fileName));   // 파일명만(확장자 제외)
//         System.out.println("6 <<<<<< "  + FilenameUtils.getName(fileName));     // 파일명만(확장자 포함)
//         System.out.println("3 <<<<<< "  + FilenameUtils.getFullPath(fileName));   // 파열경로
//         System.out.println("4 <<<<<< "  + FilenameUtils.getPrefix(fileName));     // 드라이브 경로
//         System.out.println("4 <<<<<< "  + FilenameUtils.getPath(fileName));     // 드라이브 뺀 나머지

    public static long getFreeSpace() {
        long freeSpace = 0 ;
        try {
            freeSpace = FileSystemUtils.freeSpaceKb("/home/pi");
            System.out.println(freeSpace + "kb");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return freeSpace;

    }

    public static ArrayList<String> getFileNamesInZip(String zipFilePath) throws Exception {
        ZipFile zip = new ZipFile(zipFilePath);

        ArrayList<String> fileNames = new ArrayList<String>();

        for (Enumeration e = zip.entries(); e.hasMoreElements();) {
            ZipEntry entry = (ZipEntry) e.nextElement();
            if (!entry.isDirectory()) {
                fileNames.add(entry.getName());

            }
        }

        return fileNames;
    }

    public static boolean checkShapeFile(String zipFilePath) throws Exception {

        ArrayList<String> fileNames = getFileNamesInZip(zipFilePath);
        Map<String, Integer> map = new HashMap<String, Integer>();
        boolean result = true;
        int num = 0;

        for (int i = 0; i < fileNames.size(); i++) {

            String orgName = FilenameUtils.getBaseName(fileNames.get(i));

            if (FilenameUtils.getExtension(fileNames.get(i)).equals("shp")
                    || FilenameUtils.getExtension(fileNames.get(i)).equals("shx")
                    || FilenameUtils.getExtension(fileNames.get(i)).equals("dbf")) {
                if (map.get(orgName) == null) {
                    map.put(orgName, 1);

                } else {
                    num = map.get(orgName) + 1;
                    map.put(orgName, num);
                }

            }
        }

        for (Map.Entry<String, Integer> elem : map.entrySet()) {
//            System.out.println( String.format("키 : %s, 값 : %s", elem.getKey(), elem.getValue()) );
            if (elem.getValue() < 3) {
                result = false;
                break;
            }
        }

        return result;
    }


    public static void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                if (!Files.isSymbolicLink(f.toPath())) {
                    deleteDir(f);
                }
            }
        }
        file.delete();
    }

    public static boolean deleteFile(String fileName) {
        boolean flag = false;
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }


    //압축파일 작성
    // 디렉토리의 경우 하위 폴더 압축은 안 됨
    public static boolean zip(String sourcePath, String zipFileName ) throws IOException{
        File file = new File(sourcePath);
        String files[] = null;


        //파일이 디렉토리 일경우 리스트를 읽어오고
        //파일이 디렉토리가 아니면 첫번째 배열에 파일이름을 넣는다.
        if( file.isDirectory() ){
            files = file.list();
        }else{
            files = new String[1];
            files[0] = FilenameUtils.getName(sourcePath);
        }

        sourcePath = FilenameUtils.getFullPath(sourcePath);

        //buffer size
        int size = 1024;
        byte[] buf = new byte[size];

        FileInputStream fis = null;
        ZipArchiveOutputStream zos = null;
        BufferedInputStream bis = null;

        try {
            // Zip 파일생성
            zos = new ZipArchiveOutputStream(new BufferedOutputStream(new FileOutputStream(zipFileName)));
            for( int i=0; i < files.length; i++ ){
                //해당 폴더안에 다른 폴더가 있다면 지나간다.
                if( new File(sourcePath + files[i]).isDirectory() ){
                    continue;
                }
                //encoding 설정
                zos.setEncoding("UTF-8");

//                System.out.println(sourcePath +  files[i]);

                //buffer에 해당파일의 stream을 입력한다.
                fis = new FileInputStream(sourcePath +  files[i]);
                bis = new BufferedInputStream(fis,size);

                //zip에 넣을 다음 entry 를 가져온다.
                zos.putArchiveEntry(new ZipArchiveEntry(files[i]));


                //준비된 버퍼에서 집출력스트림으로 write 한다.
                int len;
                while((len = bis.read(buf,0,size)) != -1){
                    zos.write(buf,0,len);
                }

                bis.close();
                fis.close();
                zos.closeArchiveEntry();

            }
            zos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }finally{
            if( zos != null ){
                zos.close();
            }
            if( fis != null ){
                fis.close();
            }
            if( bis != null ){
                bis.close();
            }
        }

        return true;
    }


    // 특정 이름으로 시작하는 파일중 최근 파일 찾기
    // 이름을 지정하지 않으면 그냥 최근 파일만 찾음
    public static String getLastModified(String directoryFilePath, String startName)
    {
        File directory = new File(directoryFilePath);
        File[] files = directory.listFiles(File::isFile);
        long lastModifiedTime = Long.MIN_VALUE;
        File chosenFile = null;

        if (files != null)
        {
            for (File file : files)
            {
                if(startName != null && !startName.equals("")){
                    if (file.lastModified() > lastModifiedTime && file.getName().startsWith(startName))
                    {
                        chosenFile = file;
                        lastModifiedTime = file.lastModified();
                    }
                }else{
                    if (file.lastModified() > lastModifiedTime)
                    {
                        chosenFile = file;
                        lastModifiedTime = file.lastModified();
                    }

                }
            }
        }

        return chosenFile.getAbsolutePath() ;
    }

    public static boolean checkExist(String fileName) {
        boolean flag = false;

        if(fileName.equals("")){
            return flag;
        }

        File file = new File(fileName);
        if (file.exists()) {
            flag = true;
        }
        return flag;
    }

    public static long getFileSize(String fileName){
        long fSize = -1;

        if(!checkExist(fileName)){
            return fSize;
        }

        File file = new File(fileName);
        fSize = file.length();
//        long kilobyte = bytes / 1024;
//        long megabyte = kilobyte / 1024;

        return fSize;
    }

    public static JSONArray readTextToJSonArray(String fileName) {

        JSONArray array = null;
        StringBuffer readBuffer = new StringBuffer();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line = "";
            while ((line = reader.readLine()) != null) {
                readBuffer.append(line);
            }

            array = new JSONArray(readBuffer.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return array;
    }

    // CSV 파일을 읽어서 스트링배열 리스트로 담아 줌
    // |를 분리자로 지정시 "\\|" 로 지정해 주어야 함
    public static List<String[]> readFromCsvFile(String separator, String fileName) {
        if(separator.equals("")){
            separator = ",";
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            List<String[]> list = new ArrayList<>();
            String line = "";
            while ((line = reader.readLine()) != null) {
                String[] array = line.split(separator);
                list.add(array);
            }
            return list;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 스트링배열 리스트를 csv로 저장
    public static void writeToCsvFile(List<String[]> thingsToWrite, String separator, String fileName) {
        if(separator.equals("")){
            separator = ",";
        }
        try (FileWriter writer = new FileWriter(fileName)) {
            for (String[] strings : thingsToWrite) {
                for (int i = 0; i < strings.length; i++) {
                    writer.append(strings[i]);
                    if (i < (strings.length - 1))
                        writer.append(separator);
                }
                writer.append(System.lineSeparator());
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static ArrayList<String> getFilenameListInZipfile(String zipFileName){
        ArrayList<String> list = new ArrayList<String>();

        if(!checkExist(zipFileName)){
            return list;
        }

        // ZipFile 인스턴스 생성
        try (ZipFile zipFile = new ZipFile(zipFileName)) {

            // 압축 파일 내의 엔트리들을 나열
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            // 모든 엔트리들을 반복 처리
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                list.add(entry.getName());
            }

        } catch (IOException e) {
            // 오류 처리
            System.err.println("Error opening zip file: " + e.getMessage());
        }

        return list;
    }
}
