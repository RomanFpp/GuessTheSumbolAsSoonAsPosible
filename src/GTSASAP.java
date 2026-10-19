import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.atomic.AtomicInteger;

public class GTSASAP {
        public static void main(String[] args) throws IOException, InterruptedException {
        StringBuilder stringBuilder;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        Random random = new Random();
        System.out.println(StringStore.greeting);
        String userWord = reader.readLine();
        char[] alphabetArr = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'g', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'G', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z','а', 'б', 'в', 'г', 'д', 'е', 'ё', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п', 'р', 'с', 'т', 'у', 'ф', 'х', 'ц', 'ч',
                'ш', 'щ', 'ъ', 'ы', 'ь', 'э','ю', 'я', 'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ё', 'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М', 'Н', 'О', 'П', 'Р', 'С', 'Т', 'У', 'Ф',
                'Х', 'Ц', 'Ч', 'Ш', 'Щ', 'Ы', 'Э', 'Ю', 'Я','0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ' '};


        if (userWord.length() <= 5) {
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
                        while (!found.get()) {
                            StringBuilder candidate = new StringBuilder();

                            for (int j = 0; j < userWord.length(); j++) {
                                int letterIndex =
                                        ThreadLocalRandom.current().nextInt(alphabetArr.length);
                                candidate.append(alphabetArr[letterIndex]);
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
            finished.await();
            executor.close();
            System.out.println(
                    StringStore.maxOfThreads + maxActiveWorkers.get());

            long time = System.nanoTime() - start;
            double elapsedSeconds = time / 1_000_000_000.0;
            String numberOfChancesToFormat = String.format("%,d", attempts.sum());
            System.out.println(StringStore.averageTime + elapsedSeconds + StringStore.sec);
            System.out.println(StringStore.didIt + numberOfChancesToFormat + StringStore.trying);
        }

        if (userWord.length() > 5) {
            try {
                char[] testedWord = new char[userWord.length()]; //масив символов длиной равный длине слова пользователя
                userWord.getChars(0, userWord.length(), testedWord, 0); // копирование в него всех символов из слова пользователя
                long starts = System.nanoTime();
                int run = 1000000;
                System.out.println(StringStore.userWord + userWord + StringStore.longOf + userWord.length() + StringStore.symbols /*+ String.valueOf(testedWord[0]) + " " + String.valueOf(testedWord[userWord.length() -1])*/);
                for (int x = 1; x < userWord.length() - 1; x++) { // цикл по периодичности равный длине слова пользователя
                   /* int numberOfSymbol = 1;*/
                    System.out.println(StringStore.nowWeDo + x + StringStore.symbol);
                    do {
                        stringBuilder = new StringBuilder(); // переменная для хранения случайных символов
                        stringBuilder.append(alphabetArr[random.nextInt(alphabetArr.length)]); // выборка символа из алфавита по случайному числу
                    } while (!String.valueOf(testedWord[x]).contentEquals(stringBuilder));

                }
                long time = System.nanoTime() - starts;
                System.out.println(StringStore.averageTimeForLongWord + ((double) time / run) / 1000 + StringStore.sec);
                System.out.println();
            }catch ( ArrayIndexOutOfBoundsException e){
                System.out.println(StringStore.fuck + e );
            }
            }
        }


    }

