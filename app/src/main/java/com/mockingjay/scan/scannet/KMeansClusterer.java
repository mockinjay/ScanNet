package com.mockingjay.scan.scannet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by casian on 23.01.2017.
 */
public class KMeansClusterer {
    // the data points for clustering
    private ArrayList<Point> data = new ArrayList<Point>();

    // the cluster centroids
    private ArrayList<Point> centroids = new ArrayList<Point>();

    // assigned clusters for each data point
    private Map<Point, ArrayList<Point>> clusters = new HashMap<Point, ArrayList<Point>>();

    // loop till no point move to another cluster
    boolean iterate = true;

    // the optime k after Gap statistics
    int optimeGapK = 0;

    /**
     * Return data coordinates
     * @return the an array of points
     */
    public ArrayList<Point> getData() { return this.data; }

    /**
     * Return the number of cluster after a Gap statistics
     * @return number of clusters
     */
    public int getOptimeGapK() { return optimeGapK; }

    /**
     * Read points from input file
     * @return dim dimension of points to be clustered
     */
    public int readData(File file) {
        int numPoints = 0, dim = 0;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(file.getInput()));
            dim = Integer.parseInt(reader.readLine().split(" ")[1]);
            numPoints = Integer.parseInt(reader.readLine().split(" ")[1]);

            for (int i = 0; i < numPoints; i++) {
                double[] coord = new double[dim];
                String line = reader.readLine();
                Scanner lineIn = new Scanner(line);
                for (int j = 0; j < dim; j++)
                    coord[j] = lineIn.nextDouble();
                data.add(new Point(dim, coord));
                lineIn.close();
            }

        } catch (IOException e) {
            System.err.println("Invalid data file format. Exiting.");
            e.printStackTrace();
            System.exit(1);
        } finally {
            if (reader != null) {
                try{
                    reader.close();
                } catch (IOException e) {
                    System.err.println("Error: reader is not closing!");
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }
        return dim;
    }

    /**
     * Function which finds the cluster index for a given point
     * @param p point
     * @param map cluster points
     * @return index of cluster which contains the given point
     */
    private static int findClusterIndexForPoint(Point p, Map<Point, ArrayList<Point>> map) {
        int i = 0;
        for (Map.Entry<Point, ArrayList<Point>> entry : map.entrySet()) {
            if (entry.getValue().contains(p))
                break;
            i++;
        }
        return i;
    }

    /**
     * Export cluster data in the given data output format.
     * @param dim dimension of point
     * @param k number of clusters
     * @param wcss within cluster sum of squares
     */
    public void writeClusterData(int dim, int k, double wcss) {
        System.out.printf("%% %d dimensions\n", dim);
        System.out.printf("%% %d points\n", data.size());
        System.out.printf("%% %d clusters/centroids\n", k);
        System.out.printf("%% %f within-cluster sum of squares\n", wcss);

        int i = 0;
        for (Point cPoint : centroids) {
            System.out.print(i + " ");
            i++;
            for (int j = 0; j < dim; j++)
                System.out.print(cPoint.getElement(j) + (j < dim - 1 ? " " : "\n"));
        }
        for (Point dPoint : data) {
            System.out.print(findClusterIndexForPoint(dPoint, clusters) + " ");
            for (int j = 0; j < dim; j++)
                System.out.print(dPoint.getElement(j) + (j < dim - 1 ? " " : "\n"));
        }
    }

    /**
     * Return the Euclidean distance between the two given point vectors.
     * @param p1 point vector 1
     * @param p2 point vector 2
     * @return the Euclidean distance between the two given point vectors
     */
    private static double getEuclideanDistance(Point p1, Point p2) {
        double sumOfSquareDiffs = getWCSS(p1, p2);
        return Math.sqrt(sumOfSquareDiffs);
    }

    /**
     * Return the minimum Within-Clusters Sum-of-Squares
     * measure for the chosen k number of clusters.
     * @param p1 point vector 1
     * @param p2 point vector 2
     * @return the minimum Within-Clusters Sum-of-Squares measure
     */
    private static double getWCSS(Point p1, Point p2) {
        double sumOfSquareDiffs = 0;
        double[] v1 = p1.getCoord();
        double[] v2 = p2.getCoord();

        for (int i = 0; i < p1.getDimension(); i++) {
            double diff = v1[i] - v2[i];
            sumOfSquareDiffs += diff * diff;
        }
        return sumOfSquareDiffs;
    }

    /**
     * Chose random obsevations from the data set and use these
     * as the initial centroids
     * @param k number of centroids
     */
    private void getRandomCentroids(int k) {
        centroids.clear();
        Random randomGenerator = new Random();
        for (int i = 0; i < k; i++) {
            int index = randomGenerator.nextInt(data.size());
            Point p = data.get(index);
            if (!centroids.contains(p))
                centroids.add(p);
            else i--;
        }
    }

    /**
     * Copy cluster points into another cluster
     * @param src source cluster points
     * @param dst destination cluster points
     */
    private static void copyCluster(Map<Point, ArrayList<Point>> src, Map<Point, ArrayList<Point>> dst) {
        dst.clear();
        for (Map.Entry<Point, ArrayList<Point>> entry : src.entrySet()) {
            dst.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Copy centroids coordinates into another centroid
     * @param src source centroids coordinates
     * @param dst destination centroids coordinates
     */
    private static void copyArray(ArrayList<Point> src, ArrayList<Point> dst) {
        dst.clear();
        for (Point dPoint : src)
            dst.add(dPoint);
    }

    /**
     * Generate a random point between a range
     * @param max maximum value
     * @param min minimum value
     * @param dimension of point
     * @return random point
     */
    private static Point generatePointRandom(double max, double min, int dimension) {
        Point p = new Point(dimension);
        Random randomGenerator = new Random();
        for (int i = 0; i < dimension; i++) {
            double x = min + (max - min) * randomGenerator.nextDouble();
            p.setElement(i, x);
        }
        return p;
    }

    /**
     * Generate a sample set with random points between a range
     * @param dimension of point
     */
    private void generateSample(int dimension) {
        data.clear();
        for (int i = 0; i < 100; i++) {
            data.add(generatePointRandom(0.234, 0.877, dimension));
        }
    }

    /**
     * Assign each data point to the nearest centroid and return whether or not any cluster assignments changed.
     * @return whether or not any cluster assignments changed
     */
    private void assignNewClusters() {
        Map<Point, ArrayList<Point>> copyClusters = new HashMap<Point, ArrayList<Point>>();
        copyCluster(clusters, copyClusters);
        clusters.clear();

        for (Point dPoint : data) {
            Double min = Double.MAX_VALUE;
            Point centroid = null;
            for (Point cPoint : centroids) {
                double euclideanDistance = getEuclideanDistance(dPoint, cPoint);
                if (min > euclideanDistance) {
                    min = euclideanDistance;
                    centroid = cPoint;
                }
            }
            ArrayList<Point> tmp = new ArrayList<Point>();
            tmp.add(dPoint);
            if (clusters.containsKey(centroid))
                clusters.get(centroid).add(dPoint);
            else
                clusters.put(centroid, tmp);

        }

        boolean flag = true;
        for (Point dPoint : data) {
            int newIndex = findClusterIndexForPoint(dPoint, clusters);
            int oldIndex = findClusterIndexForPoint(dPoint, copyClusters);
            flag = (newIndex == oldIndex);
            if(!flag) break;
        }
        if (flag) iterate = false;
    }

    /**
     * Compute new centroids at the mean point of each cluster of points.
     */
    private void computeNewCentroids() {
        centroids.clear();
        for (Map.Entry<Point, ArrayList<Point>> entry : clusters.entrySet()) {
            ArrayList<Point> points = entry.getValue();
            double[] newCentroid = new double[data.get(0).getDimension()];
            for (Point p : points) {
                double[] coord = p.getCoord();
                for (int i = 0; i < coord.length; i++) {
                    newCentroid[i] += coord[i];
                }
            }
            for (int i = 0; i < newCentroid.length; i++)
                newCentroid[i] /= points.size();
            centroids.add(new Point(newCentroid.length, newCentroid));
        }
    }

    /**
     * Compute WCSS for a single iteration of k-means clustering
     * @return the Within-Clusters Sum-of-Squares measure
     */
    private double getWCSSForSpecificIteration() {
        double wcss = 0.0;
        for (Point cPoint : centroids) {
            ArrayList<Point> points = clusters.get(cPoint);
            for (Point dPoint : points) {
                wcss += getWCSS(dPoint, cPoint);
            }
        }
        return wcss;
    }

    /**
     * Perform k-means clustering with Forgy initialization and return the 0-based cluster
     * assignments for corresponding data points.
     * @param k number of clusters
     * @return the Within-Clusters Sum-of-Squares measure
     */
    public double kMeansCluster(int k) {
        getRandomCentroids(k);
        while(true) {
            assignNewClusters();
            if (!iterate) break;
            computeNewCentroids();
        }
        return getWCSSForSpecificIteration();
    }

    /**
     * Performs 10 iterations of k-means clustering with Forgy initialization
     * and output the minimum result from iterations
     * @param k number of clusters
     * @return the minimum Within-Clusters Sum-of-Squares measure
     */
    public double kMeansClusterIterativ(int k) {
        Double minWCSS = Double.MAX_VALUE;
        ArrayList<Point> minCentroids = new ArrayList<Point>();
        HashMap<Point, ArrayList<Point>> minClusters= new HashMap<Point, ArrayList<Point>>();
        for (int i = 0; i < 10; i++) {
            iterate = true;
            double wcss = kMeansCluster(k);
            if (minWCSS > wcss) {
                minWCSS = wcss;
                copyArray(centroids, minCentroids);
                copyCluster(clusters, minClusters);
            }
//            System.out.println(wcss);
        }
        copyArray(minCentroids, centroids);
        copyCluster(minClusters, clusters);
        return minWCSS;
    }

    /**
     * Estimate the number of clusters using 'gap' method and perform
     * kmeans for the best number of cluster
     * @param kMax the maximum value of clusters
     * @param dimension of points
     * @return the minimum Within-Clusters Sum-of-Squares measure
     */
    public double kMeansClusterGap(int kMax, int dimension) {
        ArrayList<Point> originalData = new ArrayList<Point>();
        copyArray(data, originalData);

        double minWCSS = 0.0;
        double gap = 0.0;
        Double maxGap = Double.MIN_VALUE;
        double kWCSSlog = 0.0;
        double[] samplesWCSS = new double[100];
        for (int k = 2; k <= kMax; k++) {
            copyArray(originalData, data);
            minWCSS = kMeansClusterIterativ(k);
            kWCSSlog = Math.log(minWCSS);
            for (int i = 0; i < 100; i++) {
                generateSample(dimension);
                samplesWCSS[i] = Math.log(kMeansClusterIterativ(k));
            }
            double sumSamplesWCSS = 0.0;
            for (int i = 0; i < 100; i++)
                sumSamplesWCSS += samplesWCSS[i];
            sumSamplesWCSS /= 100;
            gap = sumSamplesWCSS - kWCSSlog;
            if (maxGap < gap){
                optimeGapK = k;
                maxGap = gap;
            }
//            System.out.print(gap + "\n");
        }
        copyArray(originalData, data);
        return kMeansCluster(optimeGapK);
    }
}
