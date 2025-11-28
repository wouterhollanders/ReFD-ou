
# ReFD: ReFactoring Diagnosis

This repository contains the code for the ReFactoring Diagnosis (ReFD) Eclipse plugin, developed by the Open University and NHL Stenden ISA Lab.

## Overview

ReFD is an Eclipse-based toolchain designed to support various refactoring diagnosis tasks. It integrates with Atlas and other libraries to provide analysis features.

## Table of Contents

- [Overview](#overview)
- [Installation Instructions Eclipse](#installation-instructions-eclipse)
  - [Prerequisites](#prerequisites)
  - [Linux Installation](#linux-installation)
    - [Note on Linux ARM64 Systems](#note-on-linux-arm64-systems)
  - [Windows Installation](#windows-installation)
  - [MacOS Installation](#macos-installation)
- [Installation Instructions Atlas](#installation-instructions-atlas)
    - [Atlas License](#atlas-license) 

## Installation Instructions Eclipse

The installation involves setting up dependencies for Eclipse and Atlas. Some steps can be automated using the provided script for Linux, while others (e.g., for Windows) need to be performed manually.

### Prerequisites

- Java 11 or later
- Active Atlas License (see section about Atlas License)
- Git for source code retrieval
- Internet access for required plugin updates and libraries

### Linux Installation

1. **Clone the Repository:**
   ```bash
   git clone <repository_url>
   cd <repository_directory>
   ```
   
2. **Run the Setup Script:**
   Execute the provided setup script to install the necessary plugins and configure the environment automatically.  
   ```bash
   ./setup_dev_environment.sh
   ```
   This script:
   - Installs required Eclipse plugins.
   - Configures the environment to use Atlas (including fallback options for Linux ARM64 environments—see below).

* Alternatively, use the Eclipse installer for Linux (https://www.eclipse.org/downloads/packages/installer) *
   
3. **Running the Eclipse Instance:**
   Once the setup is complete, launch Eclipse and import the projects. The ReFD plugin and its components should now be available.

#### Note on Linux ARM64 Systems

A known issue affects Linux ARM64 environments. Atlas uses JNI code for its database backend, providing native libraries for MacOS (x86/ARM64), Windows (Win32), and Linux (x86), but **not for Linux ARM64**. If you are running on an M1/M2 Mac with Linux virtualization, the Atlas toolchain may not start.

**Workaround:**  
Use the older Atlas updatesite provided in the `setup_dev_environment.sh` script. This older version does not rely on the JNI implementation and should allow the toolchain to run on Linux ARM64.

### Windows Installation

*Recommended to use the installer provided by Eclipse!*

1. **Download and Install Eclipse Installer for Windows:**  
   - Go to [Eclipse Downloads](https://www.eclipse.org/downloads/packages/installer) and run the Eclipse installer.
   
2. **Install Eclipse IDE for Eclipse Committers:**  
   - Once the installer is running, it asks which version to select. Scroll down to the version of Eclipse IDE for Eclipse Committers, and select this one. 
   - Keep the checkbox on whether to install the JRE checked.

3. **Running the Eclipse Instance:**
   Once the setup is complete, launch Eclipse and import the projects. The ReFD plugin and its components should now be available.

### MacOS Installation

For MacOS (both x86 and ARM64), follow the Linux installation instructions where applicable, replacing the `setup_dev_environment.sh` step with manual plugin installations or by using a similar script adapted for macOS. Atlas supports MacOS ARM64 natively, so the Linux ARM64 issue does not apply.

## Installation Instructions Atlas

4. **Manual Steps During Installation:**
   During the import of projects into Eclipse (`step 3`), you may be prompted to trust certain unsigned content. You **must** accept these prompts; otherwise, the project will not run correctly.

5. **Install Required Eclipse Plugins Manually:**  
   - In Eclipse, go to **Help > Install New Software...**  
   - Click on  **Add > Fill in Name, Fill in Location** (refer to `setup_dev_environment.sh`).
   - Add Atlas using: `https://ensoftupdate.com/download/atlas/current` (If on Linux ARM64 VM: `https://download.ensoftcorp.com/atlas/eclipse/`)"
   - Add Toolbox Commons using: `https://ensoftcorp.github.io/toolbox-repository/`
   - Select and install the necessary plugins (including the Java plugins in the selections).
   
6. **Running the Eclipse Instance:**
   Once the setup is complete, launch Eclipse and import the projects. The ReFD plugin and its components should now be available.

### Atlas License

Atlas is a third-party tool integrated with ReFD. It is distributed under its own license. By using or installing Atlas through ReFD, you implicitly agree to abide by the terms of the Atlas license.
To run ReFD using Atlas, go through the steps below to ensure you receive a valid license for Atlas. Without a valid license, ReFD and the Atlas shell will not work.

**License Information:**  
To receive an Atlas license. Follow these steps:

- When using a student or organization mail, apply for the academic license (https://www.ensoftcorp.com/atlas/complimentary-academic-license/) **Make sure to register your computer account username here. This is because Atlas matches the license to the username running the Eclipse plugin.**

- When not using a student mail, apply for the LTE version (not tested) (https://www.ensoftcorp.com/atlas/free-atlas-lite/)

- Launch Eclipse and run ReFD as a Java application once you receive the product key. Then, when inspecting the Atlas Shell, something will be stated about not having the correct license information for running Atlas. Click on this link within the shell, and a window opens where you can fill in the product key. Once registered, close Eclipse and launch it again. Now, Atlas Shell will be fully functional, and ReFD will be ready to use.  
