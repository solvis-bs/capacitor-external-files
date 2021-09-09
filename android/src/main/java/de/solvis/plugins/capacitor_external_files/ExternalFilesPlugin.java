package de.solvis.plugins.capacitor_external_files;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import androidx.activity.result.ActivityResult;

import com.getcapacitor.JSObject;
import com.getcapacitor.Logger;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.ActivityCallback;
import com.getcapacitor.annotation.CapacitorPlugin;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.IOException;

@CapacitorPlugin(name = "ExternalFiles")
public class ExternalFilesPlugin extends Plugin {

    private ExternalFiles implementation;

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
            call.reject("NO_ROOT");
            return;
        }
        if (path == null) {
            Logger.error(getLogTag(), "No path retrieved from call", null);
            call.reject("NO_PATH");
            return;
        }

        try {
            FileExternalEntry dirFile = implementation.getExternalEntry(root, path);
            JSONArray files = implementation.listDir(dirFile);

            JSObject ret = new JSObject();
            ret.put("files", files);
            call.resolve(ret);
        } catch (FileNotFoundException | JSONException e) {
            call.reject(e.getMessage());
        }
    }

    @PluginMethod
    public void readFile(PluginCall call) {
        String root = call.getString("root");
        String path = call.getString("path");

        if (root == null) {
            Logger.error(getLogTag(), "No root retrieved from call", null);
            call.reject("NO_ROOT");
            return;
        }
        if (path == null) {
            Logger.error(getLogTag(), "No path retrieved from call", null);
            call.reject("NO_PATH");
            return;
        }

        try {
            FileExternalEntry file = implementation.getExternalEntry(root, path);
            String data = implementation.readFile(file);
            JSObject ret = new JSObject();
            ret.put("data", data);
            call.resolve(ret);
        } catch (IOException | NullPointerException e) {
            call.reject(e.getMessage());
        }
    }

    @PluginMethod
    public void readFileBinary(PluginCall call) {
        String root = call.getString("root");
        String path = call.getString("path");

        if (root == null) {
            Logger.error(getLogTag(), "No root retrieved from call", null);
            call.reject("NO_ROOT");
            return;
        }
        if (path == null) {
            Logger.error(getLogTag(), "No path retrieved from call", null);
            call.reject("NO_PATH");
            return;
        }

        try {
            FileExternalEntry file = implementation.getExternalEntry(root, path);
            byte[] data = implementation.readFileBinary(file);
            JSObject ret = new JSObject();
            ret.put("data", data);
            call.resolve(ret);
        } catch (IOException | NullPointerException e) {
            call.reject(e.getMessage());
        }
    }

    @PluginMethod
    public void delete(PluginCall call) {
        String root = call.getString("root");
        String path = call.getString("path");

        if (root == null) {
            Logger.error(getLogTag(), "No root retrieved from call", null);
            call.reject("NO_ROOT");
            return;
        }
        if (path == null) {
            Logger.error(getLogTag(), "No path retrieved from call", null);
            call.reject("NO_PATH");
            return;
        }

        try {
            FileExternalEntry file = implementation.getExternalEntry(root, path);
            implementation.delete(file);
            call.resolve();
        } catch (IOException e) {
            call.reject(e.getMessage());
        }
    }

    @PluginMethod
    public void createDir(PluginCall call) {
        String root = call.getString("root");
        String path = call.getString("path");

        if (root == null) {
            Logger.error(getLogTag(), "No root retrieved from call", null);
            call.reject("NO_ROOT");
            return;
        }
        if (path == null) {
            Logger.error(getLogTag(), "No path retrieved from call", null);
            call.reject("NO_PATH");
            return;
        }

        try {
            implementation.createDir(root, path);
            call.resolve();
        } catch (IOException e) {
            call.reject(e.getMessage());
        }
    }

    @PluginMethod
    public void writeFile(PluginCall call) {
        String root = call.getString("root");
        String path = call.getString("path");
        String data = call.getString("data");

        if (root == null) {
            Logger.error(getLogTag(), "No root retrieved from call", null);
            call.reject("NO_ROOT");
            return;
        }
        if (path == null) {
            Logger.error(getLogTag(), "No path retrieved from call", null);
            call.reject("NO_PATH");
            return;
        }
        if (data == null) {
            Logger.error(getLogTag(), "No data retrieved from call", null);
            call.reject("NO_DATA");
            return;
        }

        try {
            implementation.writeFile(root, path, data);
            call.resolve();
        } catch (IOException e) {
            call.reject(e.getMessage());
        }
    }

    @PluginMethod
    public void copyAssetDir(PluginCall call) {
        String root = call.getString("root");
        String path = call.getString("path");
        String assetPath = call.getString("assetPath");

        if (root == null) {
            Logger.error(getLogTag(), "No root retrieved from call", null);
            call.reject("NO_ROOT");
            return;
        }
        if (path == null) {
            Logger.error(getLogTag(), "No path retrieved from call", null);
            call.reject("NO_PATH");
            return;
        }
        if (assetPath == null) {
            Logger.error(getLogTag(), "No assetPath retrieved from call", null);
            call.reject("NO_ASSET_PATH");
            return;
        }

        try {
            implementation.createDir(root, path);
            FileExternalEntry targetDir = implementation.getExternalEntry(root, path);
            implementation.copyAssetDir(assetPath, targetDir);
            call.resolve();
        } catch (IOException e) {
            call.reject(e.getMessage());
        }
    }

    @ActivityCallback
    private void dirChooserResult(PluginCall call, ActivityResult result) {
        if (call == null) {
            return;
        }

        int resultCode = result.getResultCode();
        if (resultCode == Activity.RESULT_OK) {
            Intent data = result.getData();
            if (data != null) {
                Uri uri = data.getData();
                JSObject ret = new JSObject();
                ret.put("root", uri.toString());
                call.resolve(ret);
            }
        }
    }
}