export interface ExtFileEntry {
  path: string;
  name: string;
  kind: 'file' | 'directory';
  modificationDate: number;
}
export interface ExternalFilesPlugin {
  dirChooser(): Promise<{ root: string }>;
  readDir(options: {
    root: string;
    path: string;
  }): Promise<{ files: ExtFileEntry[] }>;
  readFile(options: { root: string; path: string }): Promise<{ data: string }>;
  readFileBinary(options: {
    root: string;
    path: string;
  }): Promise<{ data: number[] }>;
  delete(options: { root: string; path: string }): Promise<void>;
  createDir(options: { root: string; path: string }): Promise<void>;
  writeFile(options: {
    root: string;
    path: string;
    data: string;
  }): Promise<void>;
  copyAssetDir(options: {
    assetPath: string;
    root: string;
    path: string;
  }): Promise<void>;
}
