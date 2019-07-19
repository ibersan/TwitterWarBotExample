import models.Kill;
import models.Notifier;
import models.Player;
import models.War;
import utils.Utils;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class WarSimulator {
    private War war;
    private Notifier notifier;
    private int rateSeconds;

    public WarSimulator(String warName, List<Player> players) {
        war = new War(warName, players);
        this.notifier = System.out::println;
        this.rateSeconds = 0;
    }

    public WarSimulator(String warName, List<Player> players, Notifier notifier) {
        war = new War(warName, players);
        this.notifier = notifier;
        this.rateSeconds = 0;
    }

    public void setNotifier(Notifier notifier) {
        this.notifier = notifier;
    }

    public void setUpdateStatusRate(int rateSeconds) {
        this.rateSeconds = rateSeconds;
    }

    public void start() {
        if (!thereIsMoreThanOnePlayer()) {
            System.out.println("There are not enough players. Players: " + war.getAllPlayersCount());
            return;
        }

        notifier.notify(getBeginMessage());
        while (thereIsMoreThanOnePlayer()) {
            try {
                TimeUnit.SECONDS.sleep(rateSeconds);
                performStep();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        notifier.notify(getWinMessage());
    }

    private String getBeginMessage() {
        return "The War '" + war.getName() + "' has begun" +
                "\nThere are " + war.getAllPlayersCount() + " players" +
                "\n---------------------";
    }

    private boolean thereIsMoreThanOnePlayer() {
        return war.getCurrentPlayersCount() >= 2;
    }

    private void performStep() {
        Kill kill = getRandomKillerAndVictim();
        war.performKill(kill);

        String message = "Day " + war.getCurrentDay() + "\n" +
                "\n" + getKillStatus(kill) +
                "\n" + war.getCurrentPlayersCount() + " player/s remaining\n";
        notifier.notify(message);

        war.incrementDay();
    }

    private String getWinMessage() {
        String winMessage = "";
        Player winner = war.getWinnerIfThereIsOne();
        if (winner != null) {
            winMessage = "Winner!! " + winner.toString();
        }
        return winMessage;
    }

    private Kill getRandomKillerAndVictim() {
        int random1 = Utils.randomNumber(0, war.getCurrentPlayersCount());
        int random2;
        int cont = 0;

        do {
            random2 = Utils.randomNumber(0, war.getCurrentPlayersCount());
            cont++;
        } while (random1 == random2 && cont <= (war.getCurrentPlayersCount() / 2) + 1);

        Player killer = war.getAlivePlayer(random1);
        Player victim = war.getAlivePlayer(random2);

        return new Kill(killer, victim);
    }

    private String getKillStatus(Kill kill) {
        String status;
        if (!kill.getKiller().equals(kill.getVictim())) {
            status = kill.getKiller().getName() + " (" + kill.getKiller().getNick() + ") has killed "
                    + kill.getVictim().getName() + " (" + kill.getVictim().getNick() + ")";
        } else {
            status = kill.getVictim().getName() + " (" + kill.getVictim().getNick() + ") has committed suicide";
        }

        return status;
    }
}
