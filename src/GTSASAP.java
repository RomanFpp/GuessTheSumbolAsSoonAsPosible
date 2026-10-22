import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.*;
import javax.swing.SwingWorker;

public class GTSASAP {
    private static final char[] ALPHABET = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z','а', 'б', 'в', 'г', 'д', 'е', 'ё', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п', 'р', 'с', 'т', 'у', 'ф', 'х', 'ц', 'ч',
            'ш', 'щ', 'ъ', 'ы', 'ь', 'э','ю', 'я', 'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ё', 'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М', 'Н', 'О', 'П', 'Р', 'С', 'Т', 'У', 'Ф',
            'Х', 'Ц', 'Ч', 'Ш', 'Щ', 'Ы', 'Ъ', 'Ь', 'Э', 'Ю', 'Я','0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ' '};
    private record SearchResult(
            long attempts,
            double elapsedSeconds,
            int maxActiveWorkers
    ){}
    private static SearchResult searchWord(
            String userWord, AtomicBoolean stopRequested
    ) throws InterruptedException {
        int threadCount = Runtime.getRuntime().availableProcessors();
        AtomicBoolean found = new AtomicBoolean(false);
        LongAdder attempts = new LongAdder();
        CountDownLatch finished = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        AtomicInteger activeWorkers = new AtomicInteger(0);
        AtomicInteger maxActiveWorkers = new AtomicInteger(0);
        long start = System.nanoTime();

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                int currentActive = activeWorkers.incrementAndGet();
                maxActiveWorkers.accumulateAndGet(currentActive, Math::max);

                try {
                    while (!found.get() && ! stopRequested.get()) {
                        StringBuilder candidate = new StringBuilder();

                        for (int j = 0; j < userWord.length(); j++) {
                            int letterIndex = ThreadLocalRandom.current()
                                    .nextInt(ALPHABET.length);
                            candidate.append(ALPHABET[letterIndex]);
                        }

                        attempts.increment();

                        if (userWord.contentEquals(candidate)
                                && found.compareAndSet(false, true)) {
                            finished.countDown();
                        }
                    }
                } finally {
                    activeWorkers.decrementAndGet();
                }
            });
        }

        try {
            while (!found.get() && !stopRequested.get()) {
                finished.await(100, TimeUnit.MILLISECONDS);
            }
        } catch (InterruptedException e) {
            found.set(true);
            executor.close();
            throw e;
        }

        executor.close();

        long time = System.nanoTime() - start;
        double elapsedSeconds = time / 1_000_000_000.0;
        return new SearchResult(attempts.sum(), elapsedSeconds, maxActiveWorkers.get());
    }

    private static void createWindow() {
        JFrame frame = new JFrame(StringStore.guessAsap);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(520, 300);
        frame.setLocationRelativeTo(null);
        JTextField wordField = new JTextField(20);
        JButton startButton = new JButton(StringStore.startSearching);
        JButton stopButton = new JButton(StringStore.stop);
        stopButton.setEnabled(false);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel inputPanel = new JPanel();
        inputPanel.add(wordField);
        inputPanel.add(startButton);
        inputPanel.add(stopButton);
        inputPanel.setMaximumSize(inputPanel.getPreferredSize());
        panel.add(inputPanel);
        AtomicBoolean stopRequested = new AtomicBoolean(false);
        stopButton.addActionListener(event -> stopRequested.set(true));
        JLabel attemptsLabel = new JLabel(StringStore.trues);
        JLabel timeLabel = new JLabel(StringStore.time);
        JLabel workersLabel = new JLabel(StringStore.threads);
        startButton.addActionListener(event -> {
            String userWord = wordField.getText().trim();

            if (userWord.isEmpty() || userWord.length() > 5) {
                JOptionPane.showMessageDialog(
                        frame,
                        StringStore.messageInText
                );
                return;
            }

            for (int i = 0; i < userWord.length(); i++) {
                char inputChar = userWord.charAt(i);
                boolean supported = false;

                for (char alphabetChar : ALPHABET) {
                    if (inputChar == alphabetChar) {
                        supported = true;
                        break;
                    }
                }

                if (!supported) {
                    JOptionPane.showMessageDialog(
                            frame,
                            StringStore.strangeSymbol
                    );
                    return;
                }
            }
            startButton.setEnabled(false);
            stopRequested.set(false);
            stopButton.setEnabled(true);

            SwingWorker<SearchResult, Void> worker = new SwingWorker<>() {
                @Override
                protected SearchResult doInBackground() throws Exception {
                    return searchWord(userWord, stopRequested);
                }



                @Override
                protected void done() {
                    try {
                        SearchResult result = get();

                        attemptsLabel.setText(
                                StringStore.trues + String.format("%,d", result.attempts())
                        );
                        long minutes = (long) result.elapsedSeconds() / 60;
                        double seconds = result.elapsedSeconds() % 60;
                        timeLabel.setText(
                                StringStore.time + String.format("%d мин %.2f сек", minutes, seconds)
                        );
                        workersLabel.setText(
                                StringStore.threads + result.maxActiveWorkers()
                        );
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(frame, e.getMessage());
                    } finally {
                        startButton.setEnabled(true);
                        stopButton.setEnabled(false);
                    }
                }
            };

            worker.execute();
            //JOptionPane.showMessageDialog(frame, StringStore.accepted + userWord);
        });


        panel.add(attemptsLabel);
        panel.add(timeLabel);
        panel.add(workersLabel);
        frame.add(panel);
        frame.setVisible(true);
    }

        public static void main(String[] args) throws IOException, InterruptedException {
            SwingUtilities.invokeLater(GTSASAP::createWindow);
        }


    }

