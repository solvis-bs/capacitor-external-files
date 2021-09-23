package de.solvis.plugins.capacitor_external_files;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;

import androidx.documentfile.provider.DocumentFile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

class ExternalFilesEntry {
    public DocumentFile fileEntry;
    public String root;
    public String path;

    ExternalFilesEntry(DocumentFile fileEntry, String root, String path){
        this.fileEntry = fileEntry;
        this.root =root;
        this.path = path;
    }
}

public class ExternalFiles {

    private final Context context;

    ExternalFiles(Context context) {
        this.context = context;
    }

    ExternalFilesEntry getExternalEntry(String root, String path) throws FileNotFoundException {
        DocumentFile fileEntry = DocumentFile.fromTreeUri(context, Uri.parse(root));
        ExternalFilesEntry resultEntry = new ExternalFilesEntry(fileEntry, root, path);

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

    JSONArray listDir(ExternalFilesEntry sourceDir) throws FileNotFoundException, JSONException {
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

    String readFile(ExternalFilesEntry file, Charset charset) throws IOException {
        if (!file.fileEntry.isFile()) {
            throw new FileNotFoundException("File entry is not a file");
        }

        ContentResolver contentResolver = context.getContentResolver();
        InputStream is = contentResolver.openInputStream(file.fileEntry.getUri());
        String dataString;
        if (charset != null) {
            dataString = readFileAsString(is, charset.name());
        } else {
            dataString = readFileAsBase64EncodedData(is);
        }
        return dataString;
    }

    void delete(ExternalFilesEntry file) throws IOException {
        if (!file.fileEntry.delete()) {
            throw new IOException("Failed to delete file");
        }
    }

    DocumentFile createDir(String root, String path) throws IOException {
        ExternalFilesEntry rootEntry = getExternalEntry(root, "");
        DocumentFile fileEntry = rootEntry.fileEntry;
        if (fileEntry == null) throw new FileNotFoundException("Invalid root dir");
        if (path.equals("")) return fileEntry;

        String[] parts = path.split("/");

        for (String part : parts) {
            if(part.isEmpty()) continue;
            DocumentFile existingFileEntry = fileEntry.findFile(part);

            if (existingFileEntry != null) {
                if (existingFileEntry.isFile()) {
                    throw new IOException("Cannot create dir. File with same name exists");
                }
                fileEntry = existingFileEntry;
            } else fileEntry = fileEntry.createDirectory(part);
            if (fileEntry == null) throw new IOException("Failed to create dir");
        }

        return fileEntry;
    }

    void writeFile(String root, String path, String data, Charset charset) throws IOException {
        if (path.equals("")) throw new IOException("Invalid path");
        String[] parts = path.split("/");
        String[] pathParts = Arrays.copyOfRange(parts, 0, parts.length - 1);
        String fileName = parts[parts.length - 1];

        String filePath = TextUtils.join("/", pathParts);
        DocumentFile dirFileEntry = createDir(root, filePath);

        ContentResolver contentResolver = context.getContentResolver();
        DocumentFile file = dirFileEntry.createFile("text/plain", fileName);
        if (file == null) throw new IOException("Failed to create file");
        OutputStream os = contentResolver.openOutputStream(file.getUri());

        if (charset != null) {
            InputStream is = new ByteArrayInputStream(data.getBytes(charset));
            copyInputToOutputStream(is, os);
        } else {
            //remove header from dataURL
            if (data.contains(",")) {
                data = data.split(",")[1];
            }
            os.write(Base64.decode(data, Base64.NO_WRAP));
            os.close();
        }
    }

    void copyAssetDir(String assetPath, ExternalFilesEntry target) throws IOException {
        if (!target.fileEntry.isDirectory()) {
            throw new IOException("Not a directory");
        }

        ContentResolver contentResolver = context.getContentResolver();
        AssetManager asm = context.getAssets();

        copyAssets(contentResolver, asm, assetPath, target.fileEntry);
    }

    Charset getEncoding(String encoding) {
        if (encoding == null) {
            return null;
        }

        switch (encoding) {
            case "utf8":
                return StandardCharsets.UTF_8;
            case "utf16":
                return StandardCharsets.UTF_16;
            case "ascii":
                return StandardCharsets.US_ASCII;
            case "iso8859-1":
                return StandardCharsets.ISO_8859_1;
        }
        return null;
    }

    private void copyInputToOutputStream(InputStream is, OutputStream os) throws IOException {
        byte[] buffer = new byte[1024 * 32];
        int bytesRead;

        while ((bytesRead = is.read(buffer)) != -1) {
            os.write(buffer, 0, bytesRead);
        }

        is.close();
        os.close();
    }

    private void copyAssets(ContentResolver ctx, AssetManager asm, String assetPath, DocumentFile target) throws IOException {
        String[] assets = asm.list(assetPath);

        if (assets.length == 0) {
            copyAssetFile(ctx, asm, assetPath, target);
        } else {
            String dirName = new File(assetPath).getName();

            DocumentFile subDir = target.findFile(dirName);
            if (subDir == null) {
                subDir = target.createDirectory(dirName);
            }

            for (String asset : assets) {
                copyAssets(ctx, asm, assetPath + "/" + asset, subDir);
            }
        }
    }

    private void copyAssetFile(ContentResolver ctx, AssetManager asm, String assetPath, DocumentFile target) throws IOException {
        File assetFile = new File(assetPath);
        Uri uri = Uri.fromFile(assetFile);
        ContentResolver contentResolver = context.getContentResolver();
        String mime = contentResolver.getType(uri);
        String fileName = assetFile.getName();
        InputStream is = asm.open(assetPath);

        DocumentFile targetFile = target.findFile(fileName);
        if (targetFile != null) {
            targetFile.delete();
        }

        targetFile = target.createFile(mime, fileName);
        if (targetFile == null) throw new IOException("Failed to create file");

        OutputStream os = ctx.openOutputStream(targetFile.getUri());

        copyInputToOutputStream(is, os);
    }

    private  String readFileAsString(InputStream is, String encoding) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int length;

        while ((length = is.read(buffer)) != -1) {
            outputStream.write(buffer, 0, length);
        }

        return outputStream.toString(encoding);
    }

    private String readFileAsBase64EncodedData(InputStream is) throws IOException {
        FileInputStream fileInputStreamReader = (FileInputStream) is;
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];

        int c;
        while ((c = fileInputStreamReader.read(buffer)) != -1) {
            byteStream.write(buffer, 0, c);
        }
        fileInputStreamReader.close();

        return Base64.encodeToString(byteStream.toByteArray(), Base64.NO_WRAP);
    }
}