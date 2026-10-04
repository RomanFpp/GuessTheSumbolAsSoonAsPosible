# PROJECT_CONTEXT.md

## Project

**Name:** Guess The Symbol As Soon As Possible  
**GitHub:** `RomanFpp/GuessTheSumbolAsSoonAsPosible`  
**Repository:** https://github.com/RomanFpp/GuessTheSumbolAsSoonAsPosible  
**Default branch:** `master`

Local project path:

```text
D:\Projects\Java\GuessTheSumbolAsSoonAsPosible
```

This file exists so that Codex and future sessions can quickly understand the project without relying on the full ChatGPT conversation history.

---

## Owner / working style

The project belongs to Roman.

Preferred learning style:

- Learn by doing.
- Do not write the whole application for Roman unless explicitly requested.
- Explain the idea, architecture, algorithms, trade-offs, and next step.
- Roman implements the change himself.
- Then review his code, errors, performance results, and architecture.
- Keep explanations practical and systematic.
- Russian is the preferred conversation language.

---

## Current environment

- Windows 11
- Java: Microsoft OpenJDK 25.0.4.1
- IntelliJ IDEA
- Git for Windows
- GitHub account: `RomanFpp`
- Java project
- JavaFX is planned for the desktop version.

JDK path:

```text
C:\Users\Roman\.jdks\ms-25.0.4.1
```

Git:

```text
C:\Program Files\Git\cmd\git.exe
```

IntelliJ is the main IDE for this Java project.

---

## Current repository structure

Current important files:

```text
GuessTheSumbolAsSoonAsPosible/
├── .idea/
├── GuessTheSumbolAsSoonAsPosible.iml
├── README.md
├── out/
└── src/
    ├── GTSASAP.java
    └── StringStore.java
```

`GTSASAP.java` contains the current console implementation.

`StringStore.java` contains Russian output strings.

An obsolete import was removed from the project:

```java
import com.sun.xml.internal.ws.wsdl.writer.document.Import;
```

The program currently runs successfully on JDK 25.

---

## Current application

The current application is a console program.

Basic idea:

1. Ask the user for a word.
2. Generate random candidate strings from a large `char[] alphabetArr`.
3. Compare generated candidates with the target.
4. Count attempts.
5. Measure execution time.
6. For short words (length <= 5), the program repeatedly generates complete random strings.
7. For longer words, the current program works character-by-character.

The alphabet contains Latin characters, Cyrillic characters, digits, space, etc.

Important Unicode detail:

The alphabet contains both Latin `e` (`U+0065`) and Cyrillic `е` (`U+0435`). They are different Java `char` values and therefore different possible characters.

---

## Planned desktop application

The long-term goal is to turn the console program into a Windows desktop application using JavaFX.

Approximate UI:

```text
┌──────────────────────────────────────┐
│ Guess The Symbol                    │
├──────────────────────────────────────┤
│ Введите слово:                      │
│ [________________________]           │
│                                     │
│            [ START ]                │
│                                     │
│ Сейчас угадывается: ...             │
│ Попыток: 12345                      │
│ Время: 0.37 сек                    │
└──────────────────────────────────────┘
```

Important architectural principle:

**Keep the guessing algorithm separate from the GUI.**

The JavaFX interface should not contain the core brute-force algorithm.

The final application should also use a separate thread for the long-running guessing process so that the JavaFX UI does not freeze.

---

## Git branch strategy

`master` is the stable/control version.

Do not use `master` for experiments.

Possible branches:

```text
master
desktop-app
desktop-window
javafx-input
guessing-engine
threading
release-1.0
```

Optimization experiments should normally use their own branches.

Example:

```text
optimization-no-duplicates
```

Do not mix unrelated optimization experiments into one branch unless there is a clear reason.

---

## Optimization / benchmarking project

A major purpose of this project is to learn practical optimization.

Rules:

1. Keep `master` stable.
2. Use the same test word for comparable benchmark runs:
   **`Turbo`**
3. Keep the same alphabet, JDK, computer, and timing method where possible.
4. Change one meaningful thing at a time.
5. Measure before and after.
6. Record benchmark results.
7. Compare:
   - attempts
   - execution time
   - attempts/second
   - speedup
   - later, average/median and spread where useful.
8. Incomplete/aborted runs are not included in benchmark statistics.
9. Eventually compare the same code in:
   - IntelliJ IDEA using normal Run
   - packaged JAR / desktop application.
10. Do not use Debug mode for normal performance benchmarks.

The basic method is:

```text
measure → form hypothesis → change one thing → measure again → compare
```

---

## Current benchmark observations

These are observations from IntelliJ IDEA using the current implementation and target `Turbo`.

### Run 1

```text
Attempts: 47,754,879
Time:     1055.7439929 s
Speed:    ~45,233 attempts/s
```

### Run 2

```text
Attempts: 107,615,504
Time:     ~131.334658 s
Speed:    ~819,399 attempts/s
```

### Run 3

```text
Attempts: 552,755,692
Time:     216.401713 s
Speed:    ~2,554,304 attempts/s
```

### Run 4

Aborted because it was taking too long.

No numeric result should be used in benchmark statistics.

### Separate observation: `Turb`

This is a different test and must not be mixed into the `Turbo` benchmark.

```text
Attempts: 36,803,888
Time:     1.0824593 s
Speed:    ~34.0 million attempts/s
```

The large variation in attempts is expected because random search has a probabilistic runtime.

The large variation in attempts/second is a separate issue that should be investigated rather than immediately attributed to randomness or the JVM.

---

## Important timing code observation

The program contains timing code similar to:

```java
long time = System.nanoTime() - start;
String numberOfChancesToFormat = String.format("%, d", countOfChances);
System.out.println(StringStore.averageTime + ((double) time / runs) / 1000 + StringStore.sec);
System.out.println(StringStore.didIt + numberOfChancesToFormat + StringStore.trying);
```

There is a variable similar to:

```java
int runs = 1000 * 1000;
```

There is also another variable named `run` in another block.

Do not assume `run` and `runs` are the same variable.

The meaning and role of `runs` should be verified by inspecting the surrounding code before changing the timing logic.

---

## Optimization experiment: no duplicate candidates

A separate experiment branch is planned:

```text
optimization-no-duplicates
```

Goal:

Investigate an algorithm that avoids generating the same candidate word more than once.

Important discussion:

- Checking every generated string against a `List` would become increasingly expensive.
- A `HashSet` provides much faster average membership checks.
- However, storing hundreds of millions of generated strings requires enormous memory.
- Therefore, this experiment is not necessarily a simple CPU-speed optimization.
- It changes the search strategy and should be evaluated separately.
- It may perform more work per candidate while drastically reducing the number of duplicate candidates.

Roman wants to implement this himself with guidance rather than receiving the finished solution.

---

## Logging experiment

Writing every generated word to a `.txt` file inside the main brute-force loop would heavily distort performance.

Reasons:

- disk I/O
- string conversion
- buffering overhead
- potentially enormous output files.

This is not part of the clean benchmark.

If investigated later, it should be a separate experiment branch such as:

```text
experiment-log-every-attempt
```

---

## IntelliJ vs desktop application

Eventually benchmark the same implementation in:

1. IntelliJ IDEA → normal Run
2. Packaged Java application / JAR / desktop application

Expected principle:

- The same Java code running on the same JVM should have broadly comparable performance.
- IntelliJ itself consumes resources, but the application still runs in its own Java process.
- Debug mode can significantly affect performance.
- The comparison should use normal Run, not Debug.

This comparison is part of the future project plan.

---

## Future architecture direction

The desired architecture is approximately:

```text
JavaFX UI
   │
   ▼
Controller / application logic
   │
   ▼
Guessing engine
   │
   ├── candidate generation
   ├── comparison
   ├── attempt counter
   └── timing / benchmark support
```

The GUI should not know the details of the brute-force algorithm.

The guessing engine should be testable independently of JavaFX.

Long-running work should run outside the JavaFX Application Thread.

---

## Development philosophy

This project is not only about producing a working application.

It is also a learning project for:

- Java
- object-oriented design
- Git and GitHub
- branching
- benchmarking
- algorithmic complexity
- optimization
- Unicode
- JavaFX
- multithreading
- eventually packaging a Windows desktop application.

When suggesting changes, explain why they work and what trade-offs they introduce.

Prefer small, understandable steps over large rewrites.

---

## Current immediate direction

The immediate next step is to add this file to the repository:

```text
PROJECT_CONTEXT.md
```

Then commit it to Git.

After that, Codex can read this file when working in the repository and will have the essential project context even though it does not automatically receive the entire ChatGPT conversation history.

When the project changes significantly, update this file.
