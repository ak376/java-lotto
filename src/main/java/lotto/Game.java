package lotto;

import camp.nextstep.edu.missionutils.Console;
import camp.nextstep.edu.missionutils.Randoms;
import java.util.stream.Collectors;
import java.text.MessageFormat;

import java.util.*;

public class Game {
    private static final int MIN_COST = 1000;

    private static final int MIN = 1;
    private static final int MAX = 45;

    private static final String NUMBER_RANGE_ERROR = "로또 숫자는 " + MIN + " 이상 " + MAX + " 이하의 숫자만 가능합니다.";




    protected String input() {
        String inputLine;
        try {
            inputLine = Console.readLine();
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "[ERROR] 값을 입력하세요"
            );
        }
        return inputLine;
    }

    public void run(){
        List<Lotto> users;
        ValidLotto Win;

        int Amount = PurchaseAmount();
        users= makeLotto(Amount);
        Win = new ValidLotto(WinNumber(), BonusNumber());

        Map<Prize, Integer> records = new HashMap<>();
        initRecords(records);

        for (Lotto user : users) {
            Prize results = Win.lottoPlace(user);
            records.put(results, records.get(results) + 1);
        }
        showresults(records, Margin(records));

    }

    public int PurchaseAmount() {
        System.out.println("구매금액을 입력해주세요");
        String input = input();
        int money = Integer.parseInt(input);
        validateNum(money);
        return (money);
    }

    public List<Integer> WinNumber(){
        System.out.println("당첨 번호를 입력해 주세요.");
        String input = input();
        List<String> splits = List.of(input.split(","));
        return Arrays.stream(input.split(",")).map(Integer::parseInt).collect(Collectors.toList());
    }

    public int BonusNumber(){
        System.out.println("보너스 번호를 입력해 주세요.");
        String input = input();
        return Integer.parseInt(input);
    }

    public List<Lotto> makeLotto(int Amount) {
        List<Lotto> lottos = new ArrayList<>();
        for (int index = 0; index < Amount; ++index) {
            List<Integer> lottoCandidate = Randoms.pickUniqueNumbersInRange(MIN, MAX, 6);
            lottos.add(new Lotto(lottoCandidate));
        }
        return lottos;
    }

    public void showresults(Map<Prize, Integer> records, double marginRate) {
        List<Prize> result = List.of(Prize.values()).stream()
                .sorted(Comparator.comparing(Prize::getPrizeMoney))
                .collect(Collectors.toList());
        System.out.println("당첨 통계\n----");

        for (Prize place : result) {
            if (place.equals(Prize.NONE)) {
                continue;
            }
            System.out.println(MessageFormat.format("{0} ({1}원) - {2}개", place.getInfo(), place.getPrizeMoney(), records.get(place)));
        }
        System.out.println("총 수익률은"+marginRate+"% 입니다");
    }

    public double Margin(Map<Prize, Integer> winningRecords) {
        int countPurchased = winningRecords.entrySet().stream()
                .mapToInt(entry -> entry.getValue() * 1000)
                .sum();
        int countWinning = winningRecords.entrySet().stream()
                .filter(entry -> !entry.getKey().equals(Prize.NONE))
                .mapToInt(entry -> entry.getValue() * entry.getKey().getPrizeMoney())
                .sum();
        System.out.println(countWinning + " : " + countPurchased);
        return 100.0 * (double) countWinning / (double) countPurchased;
    }

    private void showPurchasedLotto(Lotto lotto) {
        List<Integer> convertedNumbers = lotto.getNumbers().stream()
                .map(LottoNum::getNumber)
                .collect(Collectors.toList());
        System.out.println(convertedNumbers.toString());
    }

    private void validateNum(int money) {
        validateMinMoney(money);
        validateDivisible(money);
    }

    private void validateMinMoney(int money) {
        if (money < MIN_COST) {
            throw new IllegalArgumentException("[ERROR]구매금액은 최소 1천원입니다.");
        }
    }

    private void validateDivisible(int money) {
        if (money % MIN_COST !=0) {
            throw new IllegalArgumentException("[ERROR]구매금액은 1천원 단위입니다.");
        }
    }

    private void initRecords(Map<Prize, Integer> records) {
        for (Prize place : Prize.values()) {
            records.put(place, 0);
        }
    }

}
