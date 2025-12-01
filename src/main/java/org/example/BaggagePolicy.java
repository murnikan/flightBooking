package org.example;

public class BaggagePolicy {
    private int freeKg;
    private int extraKgPrice;
    private int handCarryKg;
    public BaggagePolicy(int freeKg, int extraKgPrice, int handCarryKg) {
        this.freeKg = freeKg;
        this.extraKgPrice = extraKgPrice;
        this.handCarryKg = handCarryKg;
    }

    public int getFreeKg() { return freeKg; }
    public int getExtraKgPrice() { return extraKgPrice; }
    public int getHandCarryKg() { return handCarryKg; }

    @Override
    public String toString() {
        return "Багаж: " + freeKg + " кг бесплатно, " + handCarryKg + " кг ручная кладь, доп. " + extraKgPrice + " руб/кг";
    }
}
