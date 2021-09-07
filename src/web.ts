import { WebPlugin } from '@capacitor/core';

import type { ExternalFilesPlugin } from './definitions';

export class ExternalFilesWeb extends WebPlugin implements ExternalFilesPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
