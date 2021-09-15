package de.solvis.plugins.capacitor_external_files;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import androidx.activity.result.ActivityResult;
import androidx.documentfile.provider.DocumentFile;

import com.getcapacitor.JSObject;
import com.getcapacitor.Logger;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.ActivityCallback;
import com.getcapacitor.annotation.CapacitorPlugin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

@CapacitorPlugin(name = "ExternalFiles")
public class ExternalFilesPlugin extends Plugin {

    private ExternalFiles implementation;

    public static String InvalidInputErrCode = "INVALID_INPUT_ERROR";
    public static String NotFoundErrCode = "NOT_FOUND_ERROR";
    public static String IoErrCode = "IO_ERROR";

    @Override
    public void load() {
        implementation = new ExternalFiles(getContext());
    }

    @PluginMethod
    public void dirChooser(PluginCall call) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(call, intent, "dirChooserResult");
    }

    @PluginMethod
    public void readDir(PluginCall call) {
        String root = call.getString("root");
        String path = call.getString("path");

        if (root == null) {
            Logger.error(getLogTag(), "No root retrieved from call", null);
            call.reject("Invalid root", InvalidInputErrCode);
            return;
        }
        if (path == null) {
            Logger.error(getLogTag(), "No path retrieved from call", null);
            call.reject("Invalid path", InvalidInputErrCode);
            return;
        }

        try {
            ExternalFilesEntry dirFile = implementation.getExternalEntry(root, path);
            JSONArray files = implementation.listDir(dirFile);

            JSObject ret = new JSObject();
            ret.put("files", files);
            call.resolve(ret);
        } catch (FileNotFoundException e) {
            call.reject(e.getMessage(), NotFoundErrCode, e);
        } catch (JSONException e) {
            call.reject(e.getMessage(), e);
        }
    }

    @PluginMethod
    public void getFileEntry(PluginCall call) {
        String root = call.getString("root");
        String path = call.getString("path");

        if (root == null) {
            Logger.error(getLogTag(), "No root retrieved from call", null);
            call.reject("Invalid root", InvalidInputErrCode);
            return;
        }
        if (path == null) {
            Logger.error(getLogTag(), "No path retrieved from call", null);
            call.reject("Invalid path", InvalidInputErrCode);
            return;
        }

        try {
            ExternalFilesEntry file = implementation.getExternalEntry(root, path);
            JSONObject jsonObject = new JSONObject();
            String kind = file.fileEntry.isFile() ? "file" : "directory";
            String name = file.fileEntry.getName();
            long modificationDate = file.fileEntry.lastModified();

            jsonObject.put("path", file.path);
            jsonObject.put("name", name);
            jsonObject.put("kind", kind);
            jsonObject.put("modificationDate", modificationDate);

            JSObject ret = new JSObject();
            ret.put("file", jsonObject);
            call.resolve(ret);
        } catch (FileNotFoundException  e) {
            call.reject(e.getMessage(), NotFoundErrCode, e);
        } catch ( JSONException e) {
            call.reject(e.getMessage(), e);
        }
    }

    @PluginMethod
    public void readFile(PluginCall call) {
        String root = call.getString("root");
        String path = call.getString("path");
        String encoding = call.getString("encoding");

        if (root == null) {
            Logger.error(getLogTag(), "No root retrieved from call", null);
            call.reject("Invalid root", InvalidInputErrCode);
            return;
        }
        if (path == null) {
            Logger.error(getLogTag(), "No path retrieved from call", null);
            call.reject("Invalid path", InvalidInputErrCode);
            return;
        }

        Charset charset = implementation.getEncoding(encoding);
        if (encoding != null && charset == null) {
            call.reject("Unsupported encoding provided: " + encoding, InvalidInputErrCode);
            return;
        }

        try {
            ExternalFilesEntry file = implementation.getExternalEntry(root, path);
            String data = implementation.readFile(file, charset);
            JSObject ret = new JSObject();
            ret.put("data", data);
            call.resolve(ret);
        } catch (FileNotFoundException e) {
            call.reject(e.getMessage(), NotFoundErrCode, e);
        } catch (IOException e) {
            call.reject(e.getMessage(), IoErrCode, e);
        }
    }

    @PluginMethod
    public void delete(PluginCall call) {
        String root = call.getString("root");
        String path = call.getString("path");

        if (root == null) {
            Logger.error(getLogTag(), "No root retrieved from call", null);
            call.reject("Invalid root", InvalidInputErrCode);
            return;
        }
        if (path == null) {
            Logger.error(getLogTag(), "No path retrieved from call", null);
            call.reject("Invalid path", InvalidInputErrCode);
            return;
        }

        try {
            ExternalFilesEntry file = implementation.getExternalEntry(root, path);
            implementation.delete(file);
            call.resolve();
        } catch (FileNotFoundException e) {
            call.reject(e.getMessage(), NotFoundErrCode, e);
        } catch (IOException e) {
            call.reject(e.getMessage(), IoErrCode, e);
        }
    }

    @PluginMethod
    public void createDir(PluginCall call) {
        String root = call.getString("root");
        String path = call.getString("path");

        if (root == null) {
            Logger.error(getLogTag(), "No root retrieved from call", null);
            call.reject("Invalid root", InvalidInputErrCode);
            return;
        }
        if (path == null) {
            Logger.error(getLogTag(), "No path retrieved from call", null);
            call.reject("Invalid path", InvalidInputErrCode);
            return;
        }

        try {
            implementation.createDir(root, path);
            call.resolve();
        } catch (FileNotFoundException e) {
            call.reject(e.getMessage(), NotFoundErrCode, e);
        } catch (IOException e) {
            call.reject(e.getMessage(), IoErrCode, e);
        }
    }

    @PluginMethod
    public void writeFile(PluginCall call) {
        String root = call.getString("root");
        String path = call.getString("path");
        String data = call.getString("data");
        String encoding = call.getString("encoding");

        Charset charset = implementation.getEncoding(encoding);
        if (encoding != null && charset == null) {
            call.reject("Unsupported encoding provided: " + encoding);
            return;
        }

        if (root == null) {
            Logger.error(getLogTag(), "No root retrieved from call", null);
            call.reject("Invalid root", InvalidInputErrCode);
            return;
        }
        if (path == null) {
            Logger.error(getLogTag(), "No path retrieved from call", null);
            call.reject("Invalid path", InvalidInputErrCode);
            return;
        }
        if (data == null) {
            Logger.error(getLogTag(), "No data retrieved from call", null);
            call.reject("NO_DATA");
            return;
        }

        try {
            implementation.writeFile(root, path, data, charset);
            call.resolve();
        } catch (FileNotFoundException e) {
            call.reject(e.getMessage(), NotFoundErrCode, e);
        } catch (IOException e) {
            call.reject(e.getMessage(), IoErrCode, e);
        }
    }

    @PluginMethod
    public void copyAssetDir(PluginCall call) {
        String root = call.getString("root");
        String path = call.getString("path");
        String assetPath = call.getString("assetPath");

        if (root == null) {
            Logger.error(getLogTag(), "No root retrieved from call", null);
            call.reject("Invalid root", InvalidInputErrCode);
            return;
        }
        if (path == null) {
            Logger.error(getLogTag(), "No path retrieved from call", null);
            call.reject("Invalid path", InvalidInputErrCode);
            return;
        }
        if (assetPath == null) {
            Logger.error(getLogTag(), "No assetPath retrieved from call", null);
            call.reject("NO_ASSET_PATH");
            return;
        }

        try {
            implementation.createDir(root, path);
            ExternalFilesEntry targetDir = implementation.getExternalEntry(root, path);
            implementation.copyAssetDir(assetPath, targetDir);
            call.resolve();
        } catch (FileNotFoundException e) {
            call.reject(e.getMessage(), NotFoundErrCode, e);
        } catch (IOException e) {
            call.reject(e.getMessage(), IoErrCode, e);
        }
    }

    @ActivityCallback
    private void dirChooserResult(PluginCall call, ActivityResult result) {
        if (call == null) {
            return;
        }

        int resultCode = result.getResultCode();
        if (resultCode == Activity.RESULT_OK) {
            Intent intent = result.getData();
            if (intent != null) {
                DocumentFile externalFile = DocumentFile.fromTreeUri(getContext(), intent.getData());
                if(externalFile == null) return;
                Uri uri = externalFile.getUri();
                JSObject ret = new JSObject();
                ret.put("root", uri.toString());
                call.resolve(ret);
            }
        }
    }
}