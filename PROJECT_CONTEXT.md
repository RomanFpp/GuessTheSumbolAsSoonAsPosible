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

The obsolete import below is currently commented out in `src/GTSASAP.java`:

```java
//import com.sun.xml.internal.ws.wsdl.writer.document.Import;
```

The program runs successfully on JDK 25.

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

On the pushed `fix-benchmark-counter` branch, the short-word path (`length <= 5`) now counts one complete generated candidate per attempt, uses a `long` counter, and converts elapsed nanoseconds directly to seconds. The longer-word path still has its separate character-by-character logic and older timing expression.

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

### Current branch snapshot (2026-10-04)

- `master` remains at `78fc1ae` (`Add project context for Codex`).
- `fix-benchmark-counter` is pushed to `origin` at `9ec80fc` (`Fix benchmark attempt counting and timing`), one commit ahead of `master`; it has not been merged.
- The commit changes only `src/GTSASAP.java`.
- The working tree also has local, uncommitted changes in `.idea/misc.xml` and the two compiled files under `out/production/GuessTheSumbolAsSoonAsPosible/`. These are not part of the feature commit and should not be included unless intentionally desired.
- This update to `PROJECT_CONTEXT.md` is also a working-tree change, separate from the feature commit.
- The short-word branch changes include the commented-out obsolete import, the `long` candidate counter, moving its increment after the inner character-generation loop, and explicit nanosecond-to-second conversion. Old `runs` lines are left commented in the source as learning notes.

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

These are observations from IntelliJ IDEA using normal Run and target `Turbo`. Random search time varies. Results before the counter correction counted generated characters, not complete candidate words; for a five-character target, one candidate produced five counter increments. Those legacy results must not be compared directly with the corrected candidate counts.

### Run 1

```text
Generated character selections (legacy counter): 47,754,879
Time:     1055.7439929 s
Speed:    ~45,233 character selections/s
```

### Run 2

```text
Generated character selections (legacy counter): 107,615,504
Time:     ~131.334658 s
Speed:    ~819,399 character selections/s
```

### Run 3

```text
Generated character selections (legacy counter): 552,755,692
Time:     216.401713 s
Speed:    ~2,554,304 character selections/s
```

### Run 4

Aborted because it was taking too long.

No numeric result should be used in benchmark statistics.

### Corrected counter and timing run

After moving the increment outside the inner character loop, an initial run took `1314.8738627 s` but printed `-179,228,020` because the counter was still an `int`; that count is invalid due to overflow.

After changing the counter to `long`, the completed `Turbo` run was:

```text
Candidate attempts: 8,929,289,669
Time:               1203 s
Speed:              ~7.42 million candidates/s
```

This is one corrected run, not a stable average. It should not be taken as proof that changing `int` to `long` made the algorithm faster; the two runs are not a controlled performance comparison, and earlier results used a different counter meaning.

### Separate observation: `Turb`

This is a different test and must not be mixed into the `Turbo` benchmark.

```text
Generated character selections (legacy counter): 36,803,888
Time:     1.0824593 s
Speed:    ~34.0 million character selections/s
```

The large variation in attempts is expected because random search has a probabilistic runtime.

The large variation in attempts/second is a separate issue that should be investigated rather than immediately attributed to randomness or the JVM.

---

## Important timing code observation

The short-word path (`userWord.length() <= 5`) now measures elapsed time as:

```java
long time = System.nanoTime() - start;
double elapsedSeconds = time / 1_000_000_000.0;
String numberOfChancesToFormat = String.format("%, d", countOfChances);
System.out.println(StringStore.averageTime + elapsedSeconds + StringStore.sec);
System.out.println(StringStore.didIt + numberOfChancesToFormat + StringStore.trying);
```

The short-word counter is a `long` and increments once after each complete candidate string has been generated. The old `runs` declaration and formula remain commented out in the source as learning notes; they are not active code.

The longer-word path still has a separate variable named `run` and uses the older expression `((double) time / run) / 1000`. In that path, `run` is not an actual repetition count; together with `/ 1000` it acts as a conversion factor from nanoseconds to seconds. Do not assume `run` and the commented short-word `runs` are the same variable.

```java
int run = 1000000;
```

The longer-word timing and its loop behavior remain separate work to review later.

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

The next task is to review the IntelliJ IDEA inspections currently reported in the code: 3 `bug` findings, 6 yellow warnings, and 1 dark-yellow warning (counts reported by Roman on 2026-10-04; individual messages have not yet been reviewed). Inspect each finding and its explanation, determine whether it indicates a real defect or a suggestion, then address them one at a time. Roman should make the code changes himself with guidance; do not suppress findings without understanding them.

The `fix-benchmark-counter` branch is pushed but not merged. Review the branch and decide whether to open a pull request or otherwise merge it into `master`; keep `master` unchanged until that decision. Keep the current local `.idea` and `out` changes out of commits unless they are intentionally needed.

For the next optimization work, use the corrected candidate counter and elapsed-seconds output as the measurement baseline. Change one meaningful thing at a time and use a separate branch for each unrelated experiment. The planned no-duplicate-candidates experiment remains future work.

The project-context file is already committed on `master`. Update it again when the code, branch status, benchmark results, or plans change significantly.
The redundant initial StringBuilder initialization was removed. The candidate comparison now uses userWord.contentEquals(stringBuilder) instead of converting the builder with toString(). Latest Turbo run: 16,088,098,890 candidate attempts in 1,828.95 seconds.