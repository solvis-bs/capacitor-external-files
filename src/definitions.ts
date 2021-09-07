export interface ExternalFilesPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
