package org.sindria.xpipe.lib.nanoREST.kernel;

import java.util.Scanner;

class Console {

//    public static void main(String[] args) {
//        Scanner scanner = new Scanner(System.in);
//
//        while (true) {
//            System.out.print("Enter command (print/sum/exit): ");
//            String command = scanner.nextLine().trim();
//
//            if (command.equalsIgnoreCase("exit")) {
//                System.out.println("Exiting...");
//                break;
//            }
//
//            System.out.print("Enter arguments (e.g., --message=Hello or --a=5 --b=3): ");
//            String inputArgs = scanner.nextLine();
//            String[] parsedArgs = inputArgs.split("\\s+");
//
//            CommandKernel commandInstance;
//            if (command.equalsIgnoreCase("print")) {
//                commandInstance = new PrintCommand();
//            } else if (command.equalsIgnoreCase("sum")) {
//                commandInstance = new SumCommand();
//            } else {
//                System.out.println("Unknown command.");
//                continue;
//            }
//
//            commandInstance.run(parsedArgs);
//        }
//
//        scanner.close();
//    }
}