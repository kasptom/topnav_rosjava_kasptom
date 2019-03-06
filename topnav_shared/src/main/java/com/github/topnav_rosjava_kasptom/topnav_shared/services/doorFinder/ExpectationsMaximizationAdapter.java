package com.github.topnav_rosjava_kasptom.topnav_shared.services.doorFinder;

//import org.apache.commons.math3.distribution.MixtureMultivariateNormalDistribution;
//import org.apache.commons.math3.distribution.fitting.MultivariateNormalMixtureExpectationMaximization;
//import org.apache.commons.math3.exception.DimensionMismatchException;
//import org.apache.commons.math3.exception.NumberIsTooSmallException;
//import org.apache.commons.math3.linear.SingularMatrixException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ExpectationsMaximizationAdapter implements IClusteringAlgorithm {

//    private MultivariateNormalMixtureExpectationMaximization expectationMaximization;
//    private MixtureMultivariateNormalDistribution fittedModel;
    private double[] firstMean;
    private double[] secondMean;


    @Override
    public List<List<DoorFinder.Point>> computeClusters(List<DoorFinder.Point> data) {
//        firstMean = null;
//        secondMean = null;
//
//        double[][] convertedData = new double[data.size()][2];
//        for (int i = 0; i < data.size(); i++) {
//            convertedData[i][0] = data.get(i).x;
//            convertedData[i][1] = data.get(i).y;
//        }
//
//        try {
//            expectationMaximization = new MultivariateNormalMixtureExpectationMaximization(convertedData);
//            MixtureMultivariateNormalDistribution distribution = MultivariateNormalMixtureExpectationMaximization.estimate(convertedData, 2);
//            expectationMaximization.fit(distribution);
//        } catch (DimensionMismatchException | NumberIsTooSmallException | SingularMatrixException npe) {
//            return Collections.emptyList();
//        }
//
//        fittedModel = expectationMaximization.getFittedModel();
//        firstMean = fittedModel.getComponents().get(0).getSecond().getMeans();
//        secondMean = fittedModel.getComponents().get(1).getSecond().getMeans();
//
//        // TODO separate data points according to the fitted model
        return Arrays.asList(Collections.singletonList(new DoorFinder.Point(firstMean[0], firstMean[1])),
                Collections.singletonList(new DoorFinder.Point(secondMean[0], secondMean[1])));
    }

    @Override
    public DoorFinder.Point getClustersMidPoint() {
        if (firstMean == null) new DoorFinder.Point(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        return new DoorFinder.Point((firstMean[0] + secondMean[0]) / 2, (firstMean[1] + secondMean[1]) / 2);
    }
}
