/* eslint-disable @typescript-eslint/no-unused-vars */
import { WebPlugin } from '@capacitor/core';

import type {
  Encoding,
  ExternalFilesPlugin,
  ExtFileEntry,
} from './definitions';

export class ExternalFilesWeb extends WebPlugin implements ExternalFilesPlugin {
  async dirChooser(): Promise<{
    root: string;
  }> {
    throw this.unimplemented('Not implemented on web.');
  }

  async readDir(options: {
    root: string;
    path: string;
  }): Promise<{ files: ExtFileEntry[] }> {
    throw this.unimplemented('Not implemented on web.');
  }

  getFileEntry(options: {
    root: string;
    path: string;
  }): Promise<{ file: ExtFileEntry }> {
    throw this.unimplemented('Not implemented on web.');
  }

  readFile(options: {
    root: string;
    path: string;
    encoding?: Encoding;
  }): Promise<{ data: string }> {
    throw this.unimplemented('Not implemented on web.');
  }

  delete(options: { root: string; path: string }): Promise<void> {
    throw this.unimplemented('Not implemented on web.');
  }

  createDir(options: { root: string; path: string }): Promise<void> {
    throw this.unimplemented('Not implemented on web.');
  }

  writeFile(options: {
    root: string;
    path: string;
    data: string;
  }): Promise<void> {
    throw this.unimplemented('Not implemented on web.');
  }

  copyAssetDir(options: {
    assetPath: string;
    root: string;
    path: string;
  }): Promise<void> {
    throw this.unimplemented('Not implemented on web.');
  }
}
