package org.uma.jmetal.algorithm.multiobjective.admopso.util;


import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.impl.AdaptiveGridArchiveII;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Created by Lenovo on 2015/11/5.
 */
public class AdMOPSOSelection<S extends Solution<?>> implements SelectionOperator<AdaptiveGridArchiveII<S>, S> {

    private JMetalRandom randomGenerator;

    public AdMOPSOSelection() {
        randomGenerator = JMetalRandom.getInstance();
    }

    /**
     * @param archive The data to process
     */
    @Override
    public S execute(AdaptiveGridArchiveII<S> archive) {
        int selected;
        int hypercube1 = archive.getGrid().rouletteWheel4Selection();
        int hypercube2 = archive.getGrid().rouletteWheel4Selection();

        if (hypercube1 != hypercube2) {
            if (archive.getGrid().getLocationDensity(hypercube1) <
                    archive.getGrid().getLocationDensity(hypercube2)) {

                selected = hypercube1;

            } else if (archive.getGrid().getLocationDensity(hypercube2) <
                    archive.getGrid().getLocationDensity(hypercube1)) {

                selected = hypercube2;
            } else {
                if (randomGenerator.nextDouble() < 0.5) {
                    selected = hypercube2;
                } else {
                    selected = hypercube1;
                }
            }
        } else {
            selected = hypercube1;
        }
        int base = randomGenerator.nextInt(0, archive.size() - 1);
        int cnt = 0;
        while (cnt < archive.size()) {
            S individual = archive.get((base + cnt) % archive.size());
            if (archive.getGrid().location(individual) != selected) {
                cnt++;
            } else {
                return individual;
            }
        }
        return archive.get((base + cnt) % archive.size());
    }
}
