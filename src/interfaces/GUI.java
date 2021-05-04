package interfaces;

import classes.Bet;
import classes.Player;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class GUI {
    private JTextField directoryOfFile;
    private JButton browseButton;
    private JButton findProfitablePlayers;
    private JTextArea textAreaForTopTenPercentOfPlayers;
    private JPanel mainPanel;
    private JFileChooser fileChooser = new JFileChooser();

    public File dataSample;
    public List<Player> listOfPlayers;

    public GUI() {
        findProfitablePlayers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //reset the text area whenever the findProfitablePlayers is clicked
                textAreaForTopTenPercentOfPlayers.setText("");
                listOfPlayers = new ArrayList<>();  //reset the list of players

                try {
                    final List<Bet> bets = convertCsvToBetObjects(dataSample);
                    final Map<Long, Player> mapOfPlayers = convertBetListToMapPlayer(bets);

                    for (Map.Entry<Long, Player> entry : mapOfPlayers.entrySet()) {
                        listOfPlayers.add(entry.getValue());
                    }

                    findTopTenPercentProfitablePlayers(listOfPlayers);
                } catch (IOException e) {
                    System.out.println(e.toString());
                }
            }
        });

        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                fileChooser.showOpenDialog(mainPanel);
                dataSample = fileChooser.getSelectedFile();
                if (dataSample != null) {
                    directoryOfFile.setText(dataSample.getAbsoluteFile().getPath());
                }
            }
        });
    }

    /**
     * Method that reads all the lines within the csv file besides the first one as those are the column title and
     * create Bet objects.
     * @param dataSample
     * @return
     * @throws IOException
     */
    private List<Bet> convertCsvToBetObjects(final File dataSample) throws IOException {
        final List<String> lines = Files.readAllLines(dataSample.toPath());
        final List<Bet> betList = new ArrayList<>();

        int column = 0;
        for (int i = 1; i < lines.size(); i++) {
            final Bet bet = new Bet();
            final Scanner scanner = new Scanner(lines.get(i));
            scanner.useDelimiter(",");
            while (scanner.hasNext()) {
                final String data = scanner.next();
                if (column == 0) {
                    bet.setId(Long.parseLong(data));
                } else if (column == 1) {
                    bet.setPlayerId(Long.parseLong(data));
                } else if (column == 2) {
                    bet.setType(data);
                } else if (column == 3) {
                    bet.setAmount(new BigDecimal(data));
                } else if (column == 4) {
                    bet.setGameName(data);
                }
                column++;
            }
            column = 0;
            betList.add(bet);
        }
        return betList;
    }

    /**
     * Method to convert the list of bets into a map of unique players and their amount calculated by
     * bet amount - win amount.
     * @param listOfBet
     * @return
     */
    private Map<Long, Player> convertBetListToMapPlayer(final List<Bet> listOfBet) {
        final Map<Long, Player> playerHashMap = new HashMap<>();

        listOfBet.stream().forEach(bet -> playerHashMap.put(bet.getPlayerId(), new Player(bet.getPlayerId(), new BigDecimal(0))));

        for (Bet bet : listOfBet) {
            BigDecimal amount = bet.getAmount();
            String gameName = bet.getGameName();
            String type = bet.getType();

            if (type.equals("bet")) {
                if (gameName.equals("poker")) {
                    amount = amount.add(new BigDecimal(2)); //add £2 ante for poker bets
                } else if (gameName.equals("blackjack")) {
                    amount = amount.add(new BigDecimal(1)); //add £1 ante for blackjack bets
                }
            }

            //if type is "win" then negate the amount as that is money coming out of the casino
            if (type.equals("win")) {
                amount = amount.negate();
            }

            playerHashMap.get(bet.getPlayerId()).setAmount(playerHashMap.get(bet.getPlayerId()).getAmount().add(amount));
        }

        return playerHashMap;
    }

    /**
     * Sorts the list of players by most profitable (amount descending) and output top 10% of the players size.
     * If players size is less than 10 players then always output 1 player as the top 10%
     * @param players - list of unique players from the data set
     */
    private void findTopTenPercentProfitablePlayers(final List<Player> players) {
        final List<Player> sortedPlayersList = players.stream()
                .sorted(Comparator.comparing(Player::getAmount).reversed())
                .collect(Collectors.toList());

        final int tenPercent = sortedPlayersList.size() < 10 ? 1 : Math.round(sortedPlayersList.size() / 10);

        for (int i = 0; i < tenPercent; i++) {
            textAreaForTopTenPercentOfPlayers.append(i + 1 + ") Player id: " + String.valueOf(sortedPlayersList.get(i).getId()));
            textAreaForTopTenPercentOfPlayers.append("\n");
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("GUI");
        frame.setContentPane(new GUI().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
