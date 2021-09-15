export interface ExtFileEntry {
  path: string;
  name: string;
  kind: 'file' | 'directory';
  modificationDate: number;
}

export enum Encoding {
  UTF8 = 'utf8',
  ASCII = 'ascii',
  UTF16 = 'utf16',
  ISO_8859_1 = 'iso8859-1',
}

export enum ErrorCodes {
  InvalidInputErrCode = 'INVALID_INPUT_ERROR',
  NotFoundErrCode = 'NOT_FOUND_ERROR',
  IoErrCode = 'IO_ERROR',
}

export interface ExternalFilesPlugin {
  dirChooser(): Promise<{ root: string }>;
  readDir(options: {
    root: string;
    path: string;
  }): Promise<{ files: ExtFileEntry[] }>;
  getFileEntry(options: {
    root: string;
    path: string;
  }): Promise<{ file: ExtFileEntry }>;
  readFile(options: {
    root: string;
    path: string;
    /**
     * The encoding to write the file in. If not provided, data
     * is written as base64 encoded.
     *
     * Pass Encoding.UTF8 to write data as string
     *
     */
    encoding?: Encoding;
  }): Promise<{ data: string }>;

  delete(options: { root: string; path: string }): Promise<void>;
  createDir(options: { root: string; path: string }): Promise<void>;
  writeFile(options: {
    root: string;
    path: string;
    data: string;
    /**
     * The encoding to write the file in. If not provided, data
     * is written as base64 encoded.
     *
     * Pass Encoding.UTF8 to write data as string
     *
     */
    encoding?: Encoding;
  }): Promise<void>;
  copyAssetDir(options: {
    assetPath: string;
    root: string;
    path: string;
  }): Promise<void>;
}
