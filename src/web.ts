/* eslint-disable @typescript-eslint/no-unused-vars */
import { WebPlugin } from '@capacitor/core';

import type { ExternalFilesPlugin, ExtFileEntry } from './definitions';

export class ExternalFilesWeb extends WebPlugin implements ExternalFilesPlugin {
  async dirChooser(): Promise<{
    rootDir: string;
  }> {
    throw this.unimplemented('Not implemented on web.');
  }

  async readDir(options: {
    rootDir: string;
    path: string;
  }): Promise<{ files: ExtFileEntry[] }> {
    throw this.unimplemented('Not implemented on web.');
  }

  readFile(options: {
    rootDir: string;
    path: string;
  }): Promise<{ data: string }> {
    throw this.unimplemented('Not implemented on web.');
  }

  readFileBinary(options: {
    rootDir: string;
    path: string;
  }): Promise<{ data: number[] }> {
    throw this.unimplemented('Not implemented on web.');
  }

  delete(options: { rootDir: string; path: string }): Promise<void> {
    throw this.unimplemented('Not implemented on web.');
  }

  createDir(options: { rootDir: string; path: string }): Promise<void> {
    throw this.unimplemented('Not implemented on web.');
  }

  writeFile(options: {
    rootDir: string;
    path: string;
    data: string;
  }): Promise<void> {
    throw this.unimplemented('Not implemented on web.');
  }

  copyAssetDir(options: {
    assetPath: string;
    rootDir: string;
    path: string;
  }): Promise<void> {
    throw this.unimplemented('Not implemented on web.');
  }
}
