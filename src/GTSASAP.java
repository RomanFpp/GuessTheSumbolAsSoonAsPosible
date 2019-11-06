import com.sun.xml.internal.ws.wsdl.writer.document.Import;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

public class GTSASAP {
    public static void main(String[] args) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        Random random = new Random();
        System.out.println(StringStore.greeting);
        String userWord = reader.readLine();
        char[] alphabetArr = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'g', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'G', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z','а', 'б', 'в', 'г', 'д', 'е', 'ё', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п', 'р', 'с', 'т', 'у', 'ф', 'х', 'ц', 'ч',
                'ш', 'щ', 'ъ', 'ы', 'ь', 'э','ю', 'я', 'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ё', 'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М', 'Н', 'О', 'П', 'Р', 'С', 'Т', 'У', 'Ф',
                'Х', 'Ц', 'Ч', 'Ш', 'Щ', 'Ы', 'Э', 'Ю', 'Я','0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ' '};


        if (userWord.length() <= 5) {
            int countOfChances = 0;
            long start = System.nanoTime();
            int runs = 1000 * 1000;
            do { // выполняем процесс формирования слова в цикле
                stringBuilder = new StringBuilder();
               // String myWord;
                for (int i = 0; i < userWord.length(); i++) {
                    int randomlLeter = random.nextInt(alphabetArr.length); // извлекаем случайную букву
                    stringBuilder.append(alphabetArr[randomlLeter]); // формируем строку из букв
                    countOfChances++;
                }
            } while (!userWord.equals(stringBuilder.toString()));  //всё вышеперечисленное выполняем до тех пор пока введённое и сгенерированное слова совпадут

            long time = System.nanoTime() - start;
            String numberOfChancesToFormat = String.format("%, d", countOfChances);
            System.out.println(StringStore.averageTime + ((double) time / runs) / 1000 + StringStore.sec);
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
                    } while (!String.valueOf(testedWord[x]).equals(stringBuilder.toString()));

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

