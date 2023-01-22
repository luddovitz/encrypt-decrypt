package encryptdecrypt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        //Command Line Arguments
        String data = "";
        String mode = "enc";
        String in = "";
        String out = "";
        String algorithm = "shift";
        int key = 0;

        //Message to print
        String message;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-in")) {
                in = args[i+1];
            }

            if (args[i].equals("-out")) {
                out = args[i+1];
            }

            if (args[i].equals("-mode")) {
                mode = args[i+1];
            }

            if (args[i].equals("-key")) {
                key = Integer.parseInt(args[i+1]);
            }

            if (args[i].equals("-data")) {
                data = args[i+1];
            }

            if (args[i].equals("-alg")) {
                algorithm = args[i+1];
            }
        }

        //If data is empty and input path is provided
        if (data.equals("") && !in.equals("")) {
            try {
                data = readFile(in);
            } catch (FileNotFoundException e){
                System.out.print("Error, file not found!");
            }
        }

        //Algorithm
        AlgorithmStrategy strategy = new AlgorithmStrategy();

        if (algorithm.equalsIgnoreCase("unicode")) {
            strategy.setMethod(new unicodeAlgorithmMethod());
        } else {
            strategy.setMethod(new shiftAlgorithmMethod());
        }

        //Encrypt or decrypt?
        if (mode.equalsIgnoreCase("enc")) {
            message = strategy.encrypt(data, key);
        } else {
            message = strategy.decrypt(data, key);
        }

        //Print or store to file?
        if (out.equalsIgnoreCase(""))
        {
            print(message);
        } else {
            try {
                writeFile(out, message);
            } catch (IOException e) {
                System.out.println("Error - file could not be saved.");
            }
        }
    }

    private static void print(String message) {
        System.out.println(message);
    }

    private static String readFile(String path) throws FileNotFoundException {
        File file = new File(path);
        Scanner scanner = new Scanner(file);
        return scanner.nextLine();
    }

    private static void writeFile(String path, String message) throws IOException {
        FileWriter fileWriter = new FileWriter(path);
        fileWriter.write(message);
        fileWriter.close();
    }


    static class AlgorithmStrategy {
        private AlgorithmMethod method;

        public void setMethod(AlgorithmMethod method) {
            this.method = method;
        }

        public String encrypt(String message, int key) {
            return this.method.encrypt(message, key);
        }

        public String decrypt(String message, int key) {
            return this.method.decrypt(message, key);
        }

    }
    /**
     * Template interface for algorithm
     */
    interface AlgorithmMethod {
        String encrypt(String message, int key);
        String decrypt(String message, int key);
    }

    /**
     * This implements algorithm template for unicode
     */
    static class unicodeAlgorithmMethod implements AlgorithmMethod {
        @Override
        public String encrypt(String message, int key) {

            StringBuilder encryptedMessage = new StringBuilder();

            for (int i = 0; i < message.length(); i++) {
                int temp = (int) message.charAt(i) + key;
                encryptedMessage.append((char) temp);
            }
            return encryptedMessage.toString();
        }

        @Override
        public String decrypt(String message, int key) {

            StringBuilder decryptedMessage = new StringBuilder();

            for (int i = 0; i < message.length(); i++) {
                int temp = (int) message.charAt(i) - key;
                decryptedMessage.append((char) temp);
            }
            return decryptedMessage.toString();
        }
    }

    /**
     * This implements algorithm template for alphabet (shift)
     */
    static class shiftAlgorithmMethod implements AlgorithmMethod {

        final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";

        @Override
        public String encrypt(String message, int key) {

            StringBuilder encryptedMessage = new StringBuilder();

            for (int i = 0; i < message.length(); i++) {
                int temp = message.charAt(i);

                //If it's a capital letter (65-90) or lowercase (97 - 122)
                if (temp >= 65 && temp <= 90) {
                    int originalPosition = UPPERCASE.indexOf(message.charAt(i));
                    int newPosition = (originalPosition + key) % 26;
                    char newChar = UPPERCASE.charAt(newPosition);
                    encryptedMessage.append(newChar);
                } else if (temp >= 97 && temp <= 122) {
                    int originalPosition = LOWERCASE.indexOf(message.charAt(i));
                    int newPosition = (originalPosition + key) % 26;
                    char newChar = LOWERCASE.charAt(newPosition);
                    encryptedMessage.append(newChar);
                }
                else {
                    encryptedMessage.append((char) temp);
                }
            }
            return encryptedMessage.toString();
        }

        @Override
        public String decrypt(String message, int key) {

            StringBuilder decryptedMessage = new StringBuilder();

            for (int i = 0; i < message.length(); i++) {
                int temp = message.charAt(i);

                //If it's a capital letter (65-90) or lowercase (97 - 122)
                if (temp >= 65 && temp <= 90) {
                    int originalPosition = UPPERCASE.indexOf(message.charAt(i));
                    int newPosition = (originalPosition - key) % 26;
                    if (newPosition < 0) {
                        newPosition = newPosition += 26;
                    }
                    char newChar = UPPERCASE.charAt(newPosition);
                    decryptedMessage.append(newChar);
                } else if (temp >= 97 && temp <= 122) {
                    int originalPosition = LOWERCASE.indexOf(message.charAt(i));
                    int newPosition = (originalPosition - key) % 26;
                    if (newPosition < 0) {
                        newPosition = newPosition += 26;
                    }
                    char newChar = LOWERCASE.charAt(newPosition);
                    decryptedMessage.append(newChar);
                }
                else {
                    decryptedMessage.append((char) temp);
                }
            }
            return decryptedMessage.toString();
        }
    }
}
