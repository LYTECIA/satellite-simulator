package satellite.model;

/**
 * Concrete implementation of the Power System Controller respecting CQS.
 */
public class PowerSystemController implements IPowerSystemController {

    // --- ATTRIBUTES ---
    private final IBattery battery;
    private final ISolarPanel solarPanel;
    private double currentNetFlow;
    private double grantedEnergy; 

    // --- CONSTRUCTOR ---
    public PowerSystemController(IBattery battery, ISolarPanel solarPanel) {
        if (battery == null || solarPanel == null) {
            throw new AssertionError("Precondition violated: components cannot be null.");
        }
        this.battery = battery;
        this.solarPanel = solarPanel;
        this.currentNetFlow = 0.0;
        this.grantedEnergy = 0.0;
    }

    // --- QUERIES ---

    @Override
    public IBattery getBattery() {
        return this.battery;
    }

    @Override
    public ISolarPanel getSolarPanel() {
        return this.solarPanel;
    }

    @Override
    public double getNetFlow() {
        return this.currentNetFlow;
    }

    @Override
    public double getGrantedEnergy() {
        return this.grantedEnergy;
    }

    // --- COMMANDS ---

    @Override
    public void processEnergyBalance(double totalConsumption) {
        if (totalConsumption < 0.0) {
            throw new AssertionError("Precondition violated: totalConsumption cannot be negative.");
        }

        double production = this.solarPanel.getEnergyProduction();
        
        // --- CAS 1 : SURPLUS ---
        if (production >= totalConsumption) {
            double surplus = production - totalConsumption;
            
            // Safe clamping logic using the battery interface constant
            double emptySpace = IBattery.MAX_CHARGE_LEVEL - this.battery.getChargeLevel();
            double energyToStore = Math.min(surplus, emptySpace);
            
            this.battery.charge(energyToStore);
            
            this.currentNetFlow = energyToStore; 
            this.grantedEnergy = totalConsumption; 
        } 
        
        // --- CAS 2 : DÉFICIT ---
        else {
            double deficit = totalConsumption - production;
            double availableFromBattery = this.battery.getChargeLevel();
            
            double energyDrawn = Math.min(deficit, availableFromBattery);
            
            if (energyDrawn > 0.0) {
                this.battery.discharge(energyDrawn);
            }
            
            this.currentNetFlow = -energyDrawn;
            this.grantedEnergy = production + energyDrawn; 
        }
    }
}