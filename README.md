# SynT (A software tool for automating the theory synthesis process)

SynT is a desktop application that helps you **define a domain theory** (constructs, universes, variables and implications) and then **generate and explore different model representations** (tables, LaTeX matrices, graphs and CSV exports).

This repository contains the NetBeans/Ant project `SynT`.

## Packaging / installer

- Windows MSI packaging instructions: `packaging/windows/README.md`

---

## Table of contents

- [1. What is SynT?](#1-what-is-synt)
- [2. Quick start](#2-quick-start)
- [3. Data model concepts](#3-data-model-concepts)
- [4. User guide (tab by tab)](#4-user-guide-tab-by-tab)
  - [4.1. Constructs tab](#41-constructs-tab)
  - [4.2. Universes tab](#42-universes-tab)
  - [4.3. Variables tab](#43-variables-tab)
  - [4.4. Implications tab](#44-implications-tab)
  - [4.5. Generation tab](#45-generation-tab)
  - [4.6. About tab](#46-about-tab)
  - [4.7. File menu (Load/Save)](#47-file-menu-loadsave)
- [5. Tips & troubleshooting](#5-tips--troubleshooting)

---

## 1. What is SynT?

SynT is a software tool developed in an academic context (UPM) to support **the automatic synthesis of theories** from formal specifications.

With SynT you can:

- Define **constructs**, **universes** and **variables**.
- Define logical relationships as **implications**.
- Generate several **model stages** (initial, reduced cycles, transitive closure, transitive reduction, expanded cycles).
- Visualize results as **tables**, **graphs**, **LaTeX matrices**, or export to **CSV**.

---

## 2. Quick start

### Run the application

This is a NetBeans/Ant Java Swing project. Typical ways to run it:

- Using NetBeans: open the project and run it.
- Using Ant from the project root (requires a JDK installed):

```powershell
ant clean jar
```

Then run the generated JAR from `dist/` (if your NetBeans build generates it there).

> Note: the exact output folder may depend on your NetBeans configuration.

---

## 3. Data model concepts

SynT works with the following core concepts:

- **Construct**: a high-level concept in your study domain.
- **Universe**: defines a type and allowed relations/values (e.g., enum scalar, enum collection, real range, boolean).
- **Variable**: a measurable/observable property, linked to a construct and a universe.
- **Implication**: a directed relation between two literals (literal 1 → literal 2). Each literal is defined by:
  - a variable (by nickname)
  - a relation operator (e.g., `=`, `>`, `>=`, `!=`, `<`, `<=`)
  - a value
  - an optional negation flag

---

## 4. User guide (tab by tab)

The main window is implemented in `src/GUI/Inicio.java` and contains several tabs.

### 4.1. Constructs tab

Use this tab to define the constructs (domain concepts).

**Fields**

- **Name**: the construct name.
- **From (Separated by ',')**: origin/sources (use `,` to separate multiple sources).
- **Scope-conditions**: textual constraints / scope conditions for the construct.

**Buttons**

- **Add / Save**
  - **Add**: creates a new construct.
  - **Save**: updates the selected construct when you are in *Edit* mode.
- **Del**: deletes the selected construct from the list.
- **Edit / Cancel**
  - **Edit**: loads the selected construct into the fields and switches to *Save* mode.
  - **Cancel**: exits edit mode and clears the fields.
- **<**: moves the selected construct up in the list.
- **>**: moves the selected construct down in the list.
- **Clear list**: removes all constructs.

**List**

- Right-side list shows all constructs.

---

### 4.2. Universes tab

Use this tab to define the universes (types + allowed relations/values).

**Fields**

- **Name**: universe name.
- **Type**: one of:
  - `Enum (Scalar)`
  - `Enum (Collection)`
  - `Real`
  - `Bool`
- **Value (Separated by ',')** (only for Enum types): list of allowed enum values.
- **Min value / Max value** (only for Real): numeric range.
- **Function name**: function/semantic identifier.
- **Arity**: integer.

**Relation checkboxes** (allowed operators for the universe)

- **E (=)**
- **G (>)**
- **GE (>=)**
- **NE (!=)**
- **L (<)**
- **LE (<=)**

**Buttons**

- **Add / Save**
  - **Add**: creates a new universe.
  - **Save**: updates the selected universe when you are in *Edit* mode.
- **Del**: deletes the selected universe.
- **Edit / Cancel**
  - **Edit**: loads the selected universe into the fields and switches to *Save* mode.
  - **Cancel**: exits edit mode and resets the fields.
- **<**: moves the selected universe up.
- **>**: moves the selected universe down.
- **Clear list**: removes all universes.

**List**

- Right-side list shows all universes.

---

### 4.3. Variables tab

Use this tab to define variables associated to constructs and universes.

**Fields**

- **Name**: variable full name.
- **Nickname**: short unique name used in implications.
- **Construct**: select the construct the variable belongs to.
- **Universe**: select the universe (type/range) of the variable.

**Buttons**

- **Add / Save**
  - **Add**: creates a new variable.
  - **Save**: updates the selected variable when you are in *Edit* mode.
- **Del**: deletes the selected variable.
- **Edit / Cancel**
  - **Edit**: loads the selected variable into fields and switches to *Save* mode.
  - **Cancel**: exits edit mode and clears the fields.
- **<**: moves the selected variable up.
- **>**: moves the selected variable down.
- **Clear list**: removes all variables.

**List**

- Right-side list shows all variables.

---

### 4.4. Implications tab

Use this tab to create directed implications between two literals.

The UI is split in two literal editors:

- **Literal 1** (left side)
- **Literal 2** (right side)

Each literal is defined by:

- **Variable name**: select a variable nickname.
- **Relation**: relation operators available depend on the selected variable’s universe.
- **Value**: input depends on universe type:
  - `Enum (Scalar)` and `Bool`: select a single value from a combo box.
  - `Enum (Collection)`: select multiple values using a checkable combo box.
  - `Real`: type a numeric value.
- **Negated**: checkbox to negate the literal.

**Implication lists**

- Upper list: implications as entered.
- Lower list: contrapositive/cycle-reduced representation.

These two lists are synchronized: selecting an item in one selects the corresponding item in the other.

**Buttons**

- **Add / Save**
  - **Add**: validates the two literals and adds a new implication.
  - **Save**: updates the selected implication when you are in *Edit* mode.
- **Del**: deletes the selected implication.
- **Edit / Cancel**
  - **Edit**: loads the selected implication into the literal controls and switches to *Save* mode.
  - **Cancel**: exits edit mode and resets literal controls.
- **<**: moves the selected implication up.
- **>**: moves the selected implication down.
- **Clear list**: removes all implications.

---

### 4.5. Generation tab

Use this tab to generate and visualize the model.

**Model stage** (left group)

- **Initial**
- **Reduced Cycles** *(only meaningful when the model has cycles)*
- **Transitive Closure**
- **Transitive Reduction**
- **Expanded Cycles** *(only meaningful when the model has cycles)*

SynT enables/disables options depending on whether cycles exist in the model.

**Format** (left group)

- **Table**: open a table view of the matrix.
- **Latex**: append a LaTeX representation to the text area.
- **Graph**: open a graph view.
- **Excel (csv)**: export a CSV file.

**Buttons**

- **Generate**: generates the selected stage and opens/outputs it using the chosen format.
- **Update**: rebuilds the implications list from the last generated matrix.

**Output area**

- The text area shows summary information (e.g., number of concepts, number of nodes, number of relations) and, when using LaTeX format, the LaTeX matrix.

---

### 4.6. About tab

Shows version and authors and a short description of the tool.

---

### 4.7. File menu (Load/Save)

The menu `File` provides:

- **Load**: load a previously saved session from a `.sgb` file.
- **Save**: save the current session to a `.sgb` file.

The `.sgb` file contains a JSON representation of constructs, universes, variables and implications.

---

## 5. Tips & troubleshooting

- **Order matters**: define constructs and universes first, then variables, then implications.
- **Nicknames** must be unique because implications reference variables by nickname.
- If you edit constructs/universes/variables/implications, regenerate the model again in the **Generation** tab.
- If you are using `Enum (Collection)`, remember that values are selected in a checkable combo box (multiple selections).

---

## License

This project is licensed under **Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)**.

- You may **use**, **share** and **modify** this project **for non-commercial purposes only**.
- You must **give appropriate credit** to the original authors.
- See [`LICENSE`](./LICENSE) for the full license text.

Suggested attribution:

> SynT (A software tool for automating the theory synthesis process) — Authors: Sergio Gil Borrás, Jorge Pérez Martinez, Jéssica Díaz Fernández, Ángel González Prieto.
