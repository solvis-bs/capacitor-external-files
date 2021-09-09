package de.solvis.plugins.capacitor_external_files;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.text.TextUtils;

import androidx.documentfile.provider.DocumentFile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

class FileExternalEntry {
    public DocumentFile fileEntry;
    public String rootUri;
    public String path;
}

public class ExternalFiles {

    final private Context context;

    ExternalFiles(Context context) {this.context = context;}

    FileExternalEntry getExternalEntry(String rootUri, String path) throws FileNotFoundException {
        FileExternalEntry resultEntry = new FileExternalEntry();
        resultEntry.rootUri = rootUri;
        resultEntry.path = path;
        resultEntry.fileEntry = DocumentFile.fromTreeUri(context, Uri.parse(rootUri));

        if (!path.equals("")) {
            String[] parts = path.split("/");
            for (String part : parts) {
                resultEntry.fileEntry = resultEntry.fileEntry.findFile(part);
                if (resultEntry.fileEntry == null) {
                    throw new FileNotFoundException("Failed to get file entry");
                }
            }
        }

        if (resultEntry.fileEntry == null || !resultEntry.fileEntry.exists()) {
            throw new FileNotFoundException("Failed to get file entry");
        }
        return resultEntry;
    }

    JSONArray listDir(FileExternalEntry sourceDir) throws FileNotFoundException, JSONException {
        if (!sourceDir.fileEntry.isDirectory()) {
            throw new FileNotFoundException("Source path is not a directory");
        }

        JSONArray filesDataJson = new JSONArray();

        for (DocumentFile sourceFile : sourceDir.fileEntry.listFiles()) {
            long lastModified = sourceFile.lastModified();
            String sourceFileName = sourceFile.getName();
            String path = (sourceDir.path.equals("")) ? sourceFileName : sourceDir.path + "/" + sourceFileName;
            String kind = sourceFile.isFile() ? "file" : "directory";

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("path", path);
            jsonObject.put("name", sourceFileName);
            jsonObject.put("kind", kind);
            jsonObject.put("modificationDate", lastModified);
            filesDataJson.put(jsonObject);
        }

        return filesDataJson;
    }

    String readFile(FileExternalEntry file) throws IOException {
        if (!file.fileEntry.isFile()) {
            throw new FileNotFoundException("File entry is not a file");
        }

        ContentResolver contentResolver = context.getContentResolver();
        InputStream is = contentResolver.openInputStream(file.fileEntry.getUri());

        BufferedReader r = new BufferedReader(new InputStreamReader(is));
        StringBuilder content = new StringBuilder();
        int value;
        while ((value = r.read()) != -1) {
            char c = (char) value;
            content.append(c);
        }

        is.close();

        return content.toString();
    }

    byte[] readFileBinary(FileExternalEntry file) throws IOException {
        if (!file.fileEntry.isFile()) {
            throw new FileNotFoundException("File entry is not a file");
        }

        ContentResolver ctx = context.getContentResolver();
        InputStream is = ctx.openInputStream(file.fileEntry.getUri());

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 32];
        int bytesRead;

        while ((bytesRead = is.read(buffer)) != -1) {
            os.write(buffer, 0, bytesRead);
        }

        is.close();
        os.close();

        return os.toByteArray();
    }

    void delete(FileExternalEntry file) throws IOException{
        if(!file.fileEntry.delete()){
            throw new IOException("Failed to delete file");
        }
    }

    DocumentFile createDir(String rootDir, String path) throws IOException {
        FileExternalEntry rootDirEntry = getExternalEntry(rootDir, "");
        DocumentFile fileEntry = rootDirEntry.fileEntry;
        if(fileEntry == null) throw new FileNotFoundException("Invalid root dir");
        if(path.equals("")) return fileEntry;

        String[] parts = path.split("/");

        for(String part: parts) {
            DocumentFile existingFileEntry = fileEntry.findFile(part);

            if(existingFileEntry != null){
                if(existingFileEntry.isFile()) {
                    throw new IOException("Cannot create dir. File with same name exists");
                }
                fileEntry = existingFileEntry;
            }
            else fileEntry = fileEntry.createDirectory(part);
            if(fileEntry == null) throw new IOException("Failed to create dir");
        }

        return fileEntry;
    }

    void writeFile(String rootDir, String path, String data) throws IOException {
        if(path.equals("")) throw new IOException("Invalid path");
        String[] parts = path.split("/");
        String[] pathParts = Arrays.copyOfRange(parts, 0, parts.length-1);
        String fileName = parts[parts.length-1];

        String filePath = TextUtils.join("/", pathParts);
        DocumentFile dirFileEntry = createDir(rootDir, filePath);

        InputStream is = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));

        ContentResolver contentResolver = context.getContentResolver();
        DocumentFile file = dirFileEntry.createFile("text/plain", fileName);
        if(file == null) throw new IOException("Failed to create file");
        OutputStream os = contentResolver.openOutputStream(file.getUri());

        copyInputToOutputStream(is, os);
    }

    void copyAssetDir(String assetPath, FileExternalEntry target)throws IOException {
        if(!target.fileEntry.isDirectory()) {
            throw new IOException("Not a directory");
        }

        ContentResolver contentResolver = context.getContentResolver();
        AssetManager asm = context.getAssets();

        copyAssets(contentResolver, asm, assetPath, target.fileEntry);
    }

    private void copyInputToOutputStream(InputStream is, OutputStream os) throws IOException {
        byte[] buffer = new byte[1024*32];
        int bytesRead;

        while ((bytesRead = is.read(buffer)) != -1) {
            os.write(buffer, 0, bytesRead);
        }

        is.close();
        os.close();
    }

    private  void copyAssets(ContentResolver ctx, AssetManager asm, String assetPath, DocumentFile target) throws IOException {
        String[] assets = asm.list(assetPath);

        if(assets.length == 0){
            copyAssetFile(ctx, asm, assetPath, target);
        }
        else{
            String dirName = new File(assetPath).getName();

            DocumentFile subDir = target.findFile(dirName);
            if(subDir == null) {
                subDir = target.createDirectory(dirName);
            }

            for (String asset : assets) {
                copyAssets(ctx, asm, assetPath + "/" + asset, subDir);
            }
        }
    }

    private  void copyAssetFile(ContentResolver ctx, AssetManager asm,String assetPath, DocumentFile target) throws IOException {
        File assetFile = new File(assetPath);
        Uri uri = Uri.fromFile(assetFile);
        ContentResolver contentResolver = context.getContentResolver();
        String mime = contentResolver.getType(uri);
        String fileName = assetFile.getName();
        InputStream is = asm.open(assetPath);

        DocumentFile targetFile = target.findFile(fileName);
        if(targetFile != null) {
            targetFile.delete();
        }

        targetFile = target.createFile(mime, fileName);
        if(targetFile == null) throw new IOException("Failed to create file");

        OutputStream os = ctx.openOutputStream(targetFile.getUri());


        copyInputToOutputStream(is, os);
    }
}