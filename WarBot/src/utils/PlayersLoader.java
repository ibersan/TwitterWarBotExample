package utils;

import models.Player;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PlayersLoader {

    public static List<Player> loadFromFile(String fileName) throws IOException {
        Path path = Paths.get(fileName);
        List<String> lines;
        lines = Files.readAllLines(path);

        ArrayList<Player> players = new ArrayList<>();
        String[] elements;
        try {
            for (String line : lines) {
                elements = line.split(";");
                players.add(new Player(elements[0], elements[1]));
            }
        }catch (IndexOutOfBoundsException e){
            throw new IOException("Invalid players file format");
        }

        return players;
    }
}
