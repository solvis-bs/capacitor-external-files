# capacitor-external-files

Plugin to work with external files like on an external sd-card connected over USB-OTG.

## Install

```bash
npm install capacitor-external-files
npx cap sync
```

## API

<docgen-index>

* [`dirChooser()`](#dirchooser)
* [`readDir(...)`](#readdir)
* [`readFile(...)`](#readfile)
* [`readFileBinary(...)`](#readfilebinary)
* [`delete(...)`](#delete)
* [`createDir(...)`](#createdir)
* [`writeFile(...)`](#writefile)
* [`copyAssetDir(...)`](#copyassetdir)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### dirChooser()

```typescript
dirChooser() => any
```

**Returns:** <code>any</code>

--------------------


### readDir(...)

```typescript
readDir(options: { rootDir: string; path: string; }) => any
```

| Param         | Type                                            |
| ------------- | ----------------------------------------------- |
| **`options`** | <code>{ rootDir: string; path: string; }</code> |

**Returns:** <code>any</code>

--------------------


### readFile(...)

```typescript
readFile(options: { rootDir: string; path: string; }) => any
```

| Param         | Type                                            |
| ------------- | ----------------------------------------------- |
| **`options`** | <code>{ rootDir: string; path: string; }</code> |

**Returns:** <code>any</code>

--------------------


### readFileBinary(...)

```typescript
readFileBinary(options: { rootDir: string; path: string; }) => any
```

| Param         | Type                                            |
| ------------- | ----------------------------------------------- |
| **`options`** | <code>{ rootDir: string; path: string; }</code> |

**Returns:** <code>any</code>

--------------------


### delete(...)

```typescript
delete(options: { rootDir: string; path: string; }) => any
```

| Param         | Type                                            |
| ------------- | ----------------------------------------------- |
| **`options`** | <code>{ rootDir: string; path: string; }</code> |

**Returns:** <code>any</code>

--------------------


### createDir(...)

```typescript
createDir(options: { rootDir: string; path: string; }) => any
```

| Param         | Type                                            |
| ------------- | ----------------------------------------------- |
| **`options`** | <code>{ rootDir: string; path: string; }</code> |

**Returns:** <code>any</code>

--------------------


### writeFile(...)

```typescript
writeFile(options: { rootDir: string; path: string; data: string; }) => any
```

| Param         | Type                                                          |
| ------------- | ------------------------------------------------------------- |
| **`options`** | <code>{ rootDir: string; path: string; data: string; }</code> |

**Returns:** <code>any</code>

--------------------


### copyAssetDir(...)

```typescript
copyAssetDir(options: { rootDir: string; assetPath: string; path: string; }) => any
```

| Param         | Type                                                               |
| ------------- | ------------------------------------------------------------------ |
| **`options`** | <code>{ rootDir: string; assetPath: string; path: string; }</code> |

**Returns:** <code>any</code>

--------------------


### Interfaces


#### ExtFileEntry

| Prop                   | Type                               |
| ---------------------- | ---------------------------------- |
| **`path`**             | <code>string</code>                |
| **`name`**             | <code>string</code>                |
| **`kind`**             | <code>"file" \| "directory"</code> |
| **`modificationDate`** | <code>number</code>                |

</docgen-api>
