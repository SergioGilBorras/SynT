# Windows packaging (MSI) for SynT

**Location in this repository:** `packaging/windows/README.md`

This document explains how to generate a Windows **.msi** installer for SynT using `jpackage`.

> Note: This guide is for Windows. For Linux packaging (`.deb` / `.rpm`) you can adapt the `jpackage` command by changing `--type`.

---

## Requirements

1. **A JDK that includes `jpackage`**
   - `jpackage` is included in modern JDKs.
   - The examples below use **JDK 20**.

2. **WiX Toolset v3** (required to create MSI installers on Windows)
   - Download: https://github.com/wixtoolset/wix3/releases/tag/wix3141rtm
   - Install it and ensure WiX tools are available.

3. **Main JAR in `dist/`**
   - The command uses `--input ./dist` and `--main-jar SynT.jar`.
   - So you must build the project first so that `dist/SynT.jar` exists.

4. **Application icon (`.ico`)**
   - This repo provides: `packaging/windows/SynT.ico`
   - If you only have a PNG, convert it to ICO.

---

## 1) Build the JAR (`dist/SynT.jar`)

Make sure the project produces the JAR under `dist/`.

- If you use NetBeans: **Run / Build** typically creates the artifact in `dist/`.
- If you use Ant: it depends on the project `build.xml`.

Verify it exists:

- `dist/SynT.jar`

---

## 2) Recommended command (MSI)

Run this from the project root directory:

```powershell
jpackage --name SynT --input .\dist --main-jar SynT.jar --main-class GUI.Inicio --type msi --runtime-image "C:\Program Files\Java\jdk-20" --win-shortcut --icon ".\packaging\SynT.ico" --app-version 1.3.0 --verbose
```

### Parameters explained

- `--name SynT`
  - Application (and installer) name.

- `--input .\dist`
  - Folder where the JAR is located.

- `--main-jar SynT.jar`
  - Main application JAR.

- `--main-class GUI.Inicio`
  - Main class (application entry point).

- `--type msi`
  - Generates an MSI installer.

- `--runtime-image "C:\Program Files\Java\jdk-20"`
  - Runtime to bundle.
  - Important: this must match your local JDK/JRE installation path.

- `--win-shortcut`
  - Creates a start menu/desktop shortcut.

- `--icon ...\SynT.ico`
  - Installer and application icon.

- `--app-version 1.2.0`
  - Application version shown in the installer.

- `--verbose`
  - Enables detailed logs.

---

## 3) Where does the MSI go?

`jpackage` usually writes the installer to the current directory (or to `./output`, depending on version and parameters).

To control the output folder, add:

```text
--dest .\dist_installer
```

Example:

```powershell
jpackage ... --dest .\dist_installer
```

---

## 4) Common issues

### A) “WiX Toolset not found” / MSI build errors

On Windows, `--type msi` requires WiX v3.

- Install WiX 3.x.
- Restart your terminal.
- Ensure `candle.exe` and `light.exe` are available (either via PATH or proper installation).

### B) The JAR cannot be found

Check:

- `dist/SynT.jar` exists.
- You are running the command from the project root.

### C) Icon problems

`--icon` must point to a valid `.ico` file.

---

## History

- This file replaces the old Spanish instructions in `software_genera_exe/`.

