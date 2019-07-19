import models.Notifier;
import models.Player;
import twitter4j.TwitterException;
import utils.PlayersLoader;
import utils.ResultsImageGenerator;
import utils.TwitterManager;
import utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * @author Adrián Fernández Arnal - @adrianfa5
 */
public final class Main {
    private final static String PLAYERS_LIST_PATH = "files/players.txt";
    private final static String RESULTS_OUTPUT_PATH = "images/results.png";

    public static void main(String[] args) {
        List<Player> players = null;
        try {
            players = PlayersLoader.loadFromFile(PLAYERS_LIST_PATH);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        if (players != null) {
            Scanner sc = new Scanner(System.in);
            System.out.println("Enter the name of the war:");
            String warName = sc.nextLine();

            String updateStatusRateInput;
            do {
                System.out.println("Enter the rate between turns (in seconds):");
                updateStatusRateInput = sc.nextLine();
            } while (!Utils.isValidInteger(updateStatusRateInput));
            sc.close();
            int updateStatusRate = Integer.parseInt(updateStatusRateInput);

            WarSimulator simulator = new WarSimulator(warName, players);
            simulator.setUpdateStatusRate(updateStatusRate);

            final List<Player> playersReference = players;
            Notifier notifier = message -> {
                System.out.println(message);
                ResultsImageGenerator.generate(playersReference, RESULTS_OUTPUT_PATH);

                try {
                    TwitterManager.updateStatus(message, new File(RESULTS_OUTPUT_PATH));
                } catch (TwitterException e) {
                    System.err.println("Error updating Twitter status - " + e.getMessage());
                }
            };
            simulator.setNotifier(notifier);
            simulator.start();
        }
    }
}
