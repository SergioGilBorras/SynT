# SynT (A software tool for automating the theory synthesis process)

SynT is a desktop application that allows users to enter elements generated during the operationalisation phase of a theory, constructs and hypotheses, and automatically generate the canonical set of hypotheses that represent that theory, thereby maintaining the principle of parsimony.

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
  - [4.2. Functions tab](#42-functions-tab)
  - [4.3. Universes tab](#43-universes-tab)
  - [4.4. Variables tab](#44-variables-tab)
  - [4.5. Implications tab](#45-implications-tab)
  - [4.6. Generation tab](#46-generation-tab)
  - [4.7. About tab](#47-about-tab)
  - [4.8. File menu (Load/Save)](#48-file-menu-loadsave)
  - [5. Tips & troubleshooting](#5-tips--troubleshooting)

---

## 1. What is SynT?

SynT is a software tool developed in an academic context (Universidad Politécnica de Madrid) to support the automatic synthesis of theories.

With SynT you can:

- Define **constructs**, **universes** and **variables**.
- Define logical relationships as **implications**.
- Calculate and display the **canonical set** of resulting hypotheses as well as those that have been detected as redundant. 
- Display this data in different formats: **tables**, **graphs**, **LaTeX matrices**, or export to **CSV**. In addition, the results of applying each of the steps defined by the algorithm to obtain the canonical set (initial, reduced cycles, transitive closure, transitive reduction, expanded cycles) can be displayed.


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

```bash
java -jar dist/SynT.jar
```

> Note: the exact output folder may depend on your NetBeans configuration.

---

## 3. Data model concepts

SynT works with the following core concepts:

- **Construct**: a high-level concept in your study domain.
- **Universe**: defines a type, which includes the set of valid values and the relationships and functions for that type.
- **Function**: a named operation with an arity (number of arguments ≥ 0). Functions are defined globally (Functions tab) and can be referenced by universes and used in literals if allowed by the universe.
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
  - **Add**: creates a new construct when you are in “Mode add”.
  - **Save**: updates the selected construct when you are in “Mode edit”.
- **Del**: deletes the selected construct from the list.
- **Mode edit / Mode add**
  - **Mode edit**: loads the selected item into the fields and enables “Save”.
  - **Mode add**: exits edit mode and clears the fields, enabling “Add”.
- **<**: moves the selected construct up in the list.
- **>**: moves the selected construct down in the list.
- **Clear list**: removes all constructs.

**List**

- Right-side list shows all constructs.

---


### 4.2. Functions tab

Use this tab to define and manage functions that can be referenced by universes and when editing literals in Implications.

**Fields**
- **Function name**: unique function name.
- **Arity**: number of arguments the function accepts (integer ≥ 0).

**Buttons**
- **Add / Save**
  - **Add**: creates a new function when you are in “Mode add”.
  - **Save**: updates the selected function when you are in “Mode edit”.
- **Del**: deletes the selected function.
- **Mode edit / Mode add**
  - **Mode edit**: prepares the fields to edit the selected function and enables “Save”.
  - **Mode add**: cancels editing and enables “Add”.
- **< / >**: moves the selected function in the list.
- **Clear list**: removes all functions.

**List**
- Shows all defined functions. These functions can be selected in literals (Implications) when the universe allows it.

**Notes**
- Function names must be unique. Arity is strictly validated in literal editors.
- Arity 0 functions behave like constants.
- The order of the list determines the display order in function selectors.

---


### 4.3. Universes tab

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
- **Functions**: select functions defined in the Functions tab that are allowed in this universe.

**Relation checkboxes** (allowed operators for the universe)

- **E (=)**
- **G (>)**
- **GE (>=)**
- **NE (!=)**
- **L (<)**
- **LE (<=)**

**Buttons**

- **Add / Save**
  - **Add**: creates a new universe when you are in “Mode add”.
  - **Save**: updates the selected universe when you are in “Mode edit”.
- **Del**: deletes the selected universe.
- **Mode edit / Mode add**
  - **Mode edit**: loads the selected universe into the fields and enables “Save”.
  - **Mode add**: exits edit mode and resets the fields, enabling “Add”.
- **<**: moves the selected universe up.
- **>**: moves the selected universe down.
- **Clear list**: removes all universes.

**List**

- Right-side list shows all universes.

**Notes on functions**
- Functions are defined globally in the “Functions” tab and referenced from universes and implications.
- A function has a name and an arity (number of arguments). Its availability in a universe determines whether it can be used in literals of that universe.
- For `Enum (Collection)` universes, functions may operate on collections if their semantics require it.

---

### 4.4. Variables tab

Use this tab to define variables associated to constructs and universes.

**Fields**

- **Name**: variable full name.
- **Nickname**: short unique name used in implications.
- **Construct**: select the construct the variable belongs to.
- **Universe**: select the universe (type/range) of the variable.

**Buttons**

- **Add / Save**
  - **Add**: creates a new variable when you are in “Mode add”.
  - **Save**: updates the selected variable when you are in “Mode edit”.
- **Del**: deletes the selected variable.
- **Mode edit / Mode add**
  - **Mode edit**: loads the selected variable into the fields and enables “Save”.
  - **Mode add**: exits edit mode and clears the fields, enabling “Add”.
- **<**: moves the selected variable up.
- **>**: moves the selected variable down.
- **Clear list**: removes all variables.

**List**

- Right-side list shows all variables.

---


### 4.5. Implications tab

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
  - You may also select a **Function** defined in the Functions tab if the literal’s universe allows it; in that case, the literal’s value is built from the function and its arguments.
- **Negated**: checkbox to negate the literal.

**Implication lists**

- Upper list: implications as entered.
- Lower list: counter reciprocal representation.

These two lists are synchronized: selecting an item in one selects the corresponding item in the other.

**Buttons**

- **Add / Save**
  - **Add**: validates both literals and adds a new implication when you are in “Mode add”.
  - **Save**: updates the selected implication when you are in “Mode edit”.
- **Del**: deletes the selected implication.
- **Mode edit / Mode add**
  - **Mode edit**: loads the selected implication into the literal controls and enables “Save”.
  - **Mode add**: exits edit mode and resets the literal controls, enabling “Add”.
- **<**: moves the selected implication up.
- **>**: moves the selected implication down.
- **Clear list**: removes all implications.

**Function usage in literals**
- If the universe allows functions, you can toggle between “Value” and “Function” in the literal editor.
- When “Function” is selected, choose a function by name and provide its arguments according to its arity. Arguments must be compatible with the universe type (e.g., enum values or real numbers).
- Functions are validated by arity and availability in the selected universe; otherwise, saving the implication is blocked.
- Example: for a `Real` universe, a unary function `abs(x)` is valid; for an `Enum (Collection)` universe, a function like `containsAny(A, {a,b})` may apply if enabled in that universe.

---

### 4.6. Generation tab

Use this tab to generate and visualize the model.

**Model stage** (left group)

- **Initial**
- **Reduced Cycles** *(only meaningful when the model has cycles)*
- **Transitive Closure**
- **Transitive Reduction**
- **Expanded Cycles** *(only meaningful when the model has cycles)*

SynT enables/disables options depending on whether cycles exist in the model.

**Expanded Cycles implementation (V1/V2)**

When the model contains cycles and **Expanded Cycles** is available, you can select the cycle expansion/restoration version (e.g. **V1** or **V2**) using the combo box shown next to the **Expanded Cycles** label. Changing this selection resets the cached matrices and regenerates the model so the selected algorithm is applied.

**Format** (left group)

- **Table**: open a table view of the matrix.
- **LaTeX**: append a LaTeX representation to the text area.
- **Graph**: open a graph view.
- **Excel (CSV)**: export a CSV file.
- **Txt**: export a text file listing the implications of the selected model stage.

**Txt export details**

When exporting as **Txt**:

- The file includes an "--- Implications ---" section listing the implications present in the selected matrix.
- When the selected stage is **Expanded Cycles**, it also includes an "--- Delete implications ---" section listing implications that are considered redundant (i.e., present in the original implications list but not in the generated one).
- For each redundant implication, SynT attempts to show a shortest path that justifies why it is redundant, formatted as `A --> B --> C`.

**Buttons**

- **Generate**: generates the selected stage and opens/outputs it using the chosen format.
- **Update**: rebuilds the implications list from the last generated matrix.

**Output area**

- The text area shows summary information (e.g., number of concepts, number of nodes, number of relations) and, when using LaTeX format, the LaTeX matrix.

---

### 4.7. About tab

Shows version and authors and a short description of the tool.

---

### 4.8. File menu (Load/Save)

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
- If you use functions in Implications:
  - Verify that the function is defined and has the correct arity in the Functions tab.
  - Ensure that the literal’s universe allows the use of that function.
  - Provide arguments compatible with the universe type.
- **Validation rules**:
  - Names cannot be empty; nicknames must be unique.
  - Function arity must be an integer ≥ 0.
  - For `Real` universes, min value must be ≤ max value.
  - Enum values are trimmed; duplicates are ignored.

---

## License

This project is licensed under **Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)**.

- You may **use**, **share** and **modify** this project **for non-commercial purposes only**.
- You must **give appropriate credit** to the original authors.
- See [`LICENSE`](./LICENSE) for the full license text.

Suggested attribution:

> SynT (A software tool for automating the theory synthesis process) — Authors: Sergio Gil Borrás, Jorge Enrique Pérez Martinez, Ángel González Prieto, Jéssica Díaz Fernández.
