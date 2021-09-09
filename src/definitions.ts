export interface ExtFileEntry {
  path: string;
  name: string;
  kind: 'file' | 'directory';
  modificationDate: number;
}
export interface ExternalFilesPlugin {
  dirChooser(): Promise<{ rootDir: string }>;
  readDir(options: {
    rootDir: string;
    path: string;
  }): Promise<{ files: ExtFileEntry[] }>;
  readFile(options: {
    rootDir: string;
    path: string;
  }): Promise<{ data: string }>;
  readFileBinary(options: {
    rootDir: string;
    path: string;
  }): Promise<{ data: number[] }>;
  delete(options: { rootDir: string; path: string }): Promise<void>;
  createDir(options: { rootDir: string; path: string }): Promise<void>;
  writeFile(options: {
    rootDir: string;
    path: string;
    data: string;
  }): Promise<void>;
  copyAssetDir(options: {
    assetPath: string;
    rootDir: string;
    path: string;
  }): Promise<void>;
}
